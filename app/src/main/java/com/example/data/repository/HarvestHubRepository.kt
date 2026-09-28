package com.example.data.repository

import com.example.data.local.HarvestHubDao
import com.example.data.model.*
import kotlinx.coroutines.flow.*
import org.json.JSONArray
import org.json.JSONObject

class HarvestHubRepository(private val dao: HarvestHubDao) {

    // --- In-Memory Reactive Cart State ---
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    fun addToCart(product: ProductEntity, quantity: Int = 1): Boolean {
        if (product.stockQty <= 0) return false
        val currentList = _cartItems.value.toMutableList()
        val index = currentList.indexOfFirst { it.productId == product.productId }
        if (index >= 0) {
            val existing = currentList[index]
            val newQty = (existing.quantity + quantity).coerceAtMost(product.stockQty)
            currentList[index] = existing.copy(quantity = newQty, maxStock = product.stockQty)
        } else {
            val addQty = quantity.coerceAtMost(product.stockQty)
            currentList.add(
                CartItem(
                    productId = product.productId,
                    itemName = product.itemName,
                    farmerId = product.farmerId,
                    farmerName = product.farmerName,
                    pricePerUnit = product.pricePerUnit,
                    unit = product.unit,
                    quantity = addQty,
                    maxStock = product.stockQty,
                    imageUrl = product.imageUrl
                )
            )
        }
        _cartItems.value = currentList
        return true
    }

    fun updateCartQuantity(productId: String, delta: Int) {
        val currentList = _cartItems.value.toMutableList()
        val index = currentList.indexOfFirst { it.productId == productId }
        if (index >= 0) {
            val item = currentList[index]
            val newQty = item.quantity + delta
            if (newQty <= 0) {
                currentList.removeAt(index)
            } else {
                currentList[index] = item.copy(quantity = newQty.coerceAtMost(item.maxStock))
            }
            _cartItems.value = currentList
        }
    }

    fun removeFromCart(productId: String) {
        _cartItems.value = _cartItems.value.filter { it.productId != productId }
    }

    fun clearCart() {
        _cartItems.value = emptyList()
    }

    // --- Products ---
    fun getAllActiveProducts(): Flow<List<ProductEntity>> = dao.getAllActiveProducts()
    fun getAllProductsAdmin(): Flow<List<ProductEntity>> = dao.getAllProductsAdmin()
    fun getProductsByFarmer(farmerId: String): Flow<List<ProductEntity>> = dao.getProductsByFarmer(farmerId)
    fun getProductsByCategory(category: String): Flow<List<ProductEntity>> = dao.getProductsByCategory(category)
    fun observeProduct(productId: String): Flow<ProductEntity?> = dao.observeProductById(productId)
    suspend fun getProductById(productId: String): ProductEntity? = dao.getProductById(productId)

    suspend fun insertProduct(product: ProductEntity) {
        dao.insertProduct(product)
        dao.insertAuditLog(
            AuditLogEntity(
                id = "log_${System.currentTimeMillis()}",
                action = "PRODUCT_CREATED",
                performedBy = product.farmerName,
                details = "Product '${product.itemName}' was added with stock ${product.stockQty} ${product.unit}."
            )
        )
    }

    suspend fun updateProduct(product: ProductEntity) {
        dao.updateProduct(product)
        dao.insertAuditLog(
            AuditLogEntity(
                id = "log_${System.currentTimeMillis()}",
                action = "PRODUCT_UPDATED",
                performedBy = product.farmerName,
                details = "Product '${product.itemName}' was updated. New price: $${product.pricePerUnit}, Stock: ${product.stockQty}."
            )
        )
    }

    suspend fun updateStock(productId: String, newStock: Int, farmerName: String) {
        dao.updateStock(productId, newStock)
        dao.insertAuditLog(
            AuditLogEntity(
                id = "log_${System.currentTimeMillis()}",
                action = "STOCK_UPDATED",
                performedBy = farmerName,
                details = "Stock adjusted for product ID $productId to $newStock."
            )
        )
    }

    suspend fun setProductActive(productId: String, isActive: Boolean, adminOrFarmer: String) {
        dao.setProductActive(productId, isActive)
        dao.insertAuditLog(
            AuditLogEntity(
                id = "log_${System.currentTimeMillis()}",
                action = if (isActive) "PRODUCT_ACTIVATED" else "PRODUCT_DEACTIVATED",
                performedBy = adminOrFarmer,
                details = "Product ID $productId active status set to $isActive."
            )
        )
    }

    suspend fun deleteProduct(productId: String, performer: String) {
        dao.deleteProduct(productId)
        dao.insertAuditLog(
            AuditLogEntity(
                id = "log_${System.currentTimeMillis()}",
                action = "PRODUCT_DELETED",
                performedBy = performer,
                details = "Product ID $productId was deleted."
            )
        )
    }

    // --- Orders ---
    fun getOrdersByCustomer(customerId: String): Flow<List<OrderEntity>> = dao.getOrdersByCustomer(customerId)
    fun getOrdersByFarmer(farmerId: String): Flow<List<OrderEntity>> = dao.getOrdersByFarmer(farmerId)
    fun getAllOrdersAdmin(): Flow<List<OrderEntity>> = dao.getAllOrdersAdmin()
    fun getOrderById(orderId: String): Flow<OrderEntity?> = dao.getOrderById(orderId)

    suspend fun placeOrder(
        customer: UserEntity,
        items: List<CartItem>,
        pickupSlot: String,
        notes: String = ""
    ): Result<List<String>> {
        if (items.isEmpty()) {
            return Result.failure(IllegalStateException("Cannot place an order with empty basket."))
        }

        // Validate stock
        for (item in items) {
            val prod = dao.getProductById(item.productId)
            if (prod == null || !prod.isActive) {
                return Result.failure(IllegalStateException("Product '${item.itemName}' is no longer available."))
            }
            if (prod.stockQty < item.quantity) {
                return Result.failure(IllegalStateException("Only ${prod.stockQty} ${prod.unit} of '${prod.itemName}' are available in stock."))
            }
        }

        // Group items by farmer (per SRS: each farmer gets their own sub-order if items come from multiple farmers)
        val groupedByFarmer = items.groupBy { it.farmerId }
        val generatedOrderIds = mutableListOf<String>()

        for ((farmerId, farmerItems) in groupedByFarmer) {
            val farmerName = farmerItems.firstOrNull()?.farmerName ?: "Local Farmer"
            val orderTotal = farmerItems.sumOf { it.subtotal }
            val orderId = "HH-" + (10000 + (Math.random() * 89999).toInt())

            // Create JSON items
            val jsonArray = JSONArray()
            for (item in farmerItems) {
                val obj = JSONObject()
                obj.put("productId", item.productId)
                obj.put("itemName", item.itemName)
                obj.put("farmerId", item.farmerId)
                obj.put("farmerName", item.farmerName)
                obj.put("pricePerUnit", item.pricePerUnit)
                obj.put("unit", item.unit)
                obj.put("quantity", item.quantity)
                obj.put("maxStock", item.maxStock)
                jsonArray.put(obj)
            }

            val orderEntity = OrderEntity(
                orderId = orderId,
                customerId = customer.userId,
                customerName = customer.name,
                customerPhone = customer.phone,
                customerAddress = customer.address,
                farmerId = farmerId,
                farmerName = farmerName,
                itemsJson = jsonArray.toString(),
                pickupSlotTime = pickupSlot,
                status = "Pending",
                totalPrice = orderTotal,
                createdAt = System.currentTimeMillis(),
                notes = notes
            )

            dao.insertOrder(orderEntity)
            generatedOrderIds.add(orderId)

            // Reduce stock
            for (item in farmerItems) {
                val currentProd = dao.getProductById(item.productId)
                if (currentProd != null) {
                    val remaining = (currentProd.stockQty - item.quantity).coerceAtLeast(0)
                    dao.updateStock(item.productId, remaining)
                }
            }

            // Notification for farmer
            val farmerObj = dao.getFarmerById(farmerId).firstOrNull()
            if (farmerObj != null) {
                dao.insertNotification(
                    NotificationEntity(
                        id = "notif_${System.currentTimeMillis()}_${(100..999).random()}",
                        recipientUserId = farmerObj.userId,
                        title = "New Order #$orderId",
                        message = "New order received from ${customer.name} for $${String.format("%.2f", orderTotal)}.",
                        type = "order"
                    )
                )
            }

            // Audit Log
            dao.insertAuditLog(
                AuditLogEntity(
                    id = "log_${System.currentTimeMillis()}_${(100..999).random()}",
                    action = "ORDER_PLACED",
                    performedBy = customer.email,
                    details = "Order #$orderId placed for $farmerName ($${String.format("%.2f", orderTotal)})."
                )
            )
        }

        // Clear cart after successful order creation
        clearCart()

        // Notification for customer
        dao.insertNotification(
            NotificationEntity(
                id = "notif_cust_${System.currentTimeMillis()}",
                recipientUserId = customer.userId,
                title = "Order Confirmation",
                message = "Your order(s) [${generatedOrderIds.joinToString(", ")}] have been placed successfully!",
                type = "order"
            )
        )

        return Result.success(generatedOrderIds)
    }

    suspend fun updateOrderStatus(orderId: String, newStatus: String, performedBy: String) {
        dao.updateOrderStatus(orderId, newStatus)
        val order = dao.getOrderById(orderId).firstOrNull()
        if (order != null) {
            dao.insertNotification(
                NotificationEntity(
                    id = "notif_stat_${System.currentTimeMillis()}",
                    recipientUserId = order.customerId,
                    title = "Order #$orderId Status Updated",
                    message = "Your order status is now: $newStatus",
                    type = "order"
                )
            )
            dao.insertAuditLog(
                AuditLogEntity(
                    id = "log_stat_${System.currentTimeMillis()}",
                    action = "ORDER_STATUS_CHANGED",
                    performedBy = performedBy,
                    details = "Order #$orderId status updated to '$newStatus'."
                )
            )
        }
    }

    // --- Users & Farmers ---
    suspend fun getUserByEmail(email: String): UserEntity? = dao.getUserByEmail(email)
    fun getUserById(userId: String): Flow<UserEntity?> = dao.getUserById(userId)
    fun getAllCustomers(): Flow<List<UserEntity>> = dao.getAllCustomers()
    suspend fun insertUser(user: UserEntity) = dao.insertUser(user)
    suspend fun updateUser(user: UserEntity) = dao.updateUser(user)

    fun getAllActiveFarmers(): Flow<List<FarmerEntity>> = dao.getAllActiveFarmers()
    fun getAllFarmers(): Flow<List<FarmerEntity>> = dao.getAllFarmers()
    fun getFarmerById(farmerId: String): Flow<FarmerEntity?> = dao.getFarmerById(farmerId)
    suspend fun getFarmerByUserId(userId: String): FarmerEntity? = dao.getFarmerByUserId(userId)
    suspend fun updateFarmer(farmer: FarmerEntity) = dao.updateFarmer(farmer)

    // --- Categories & Markets ---
    fun getAllCategories(): Flow<List<CategoryEntity>> = dao.getAllCategories()
    suspend fun insertCategory(category: CategoryEntity) = dao.insertCategory(category)
    fun getAllMarkets(): Flow<List<MarketEntity>> = dao.getAllActiveMarkets()

    // --- Wishlist ---
    fun getWishlistProductIds(customerId: String): Flow<List<String>> = dao.getWishlistProductIds(customerId)
    suspend fun toggleWishlist(customerId: String, productId: String, isCurrentlyInWishlist: Boolean) {
        if (isCurrentlyInWishlist) {
            dao.removeFromWishlist(customerId, productId)
        } else {
            dao.addToWishlist(
                WishlistEntity(
                    id = "wish_${customerId}_$productId",
                    customerId = customerId,
                    productId = productId
                )
            )
        }
    }

    // --- Followed Farmers ---
    fun getFollowedFarmerIds(customerId: String): Flow<List<String>> = dao.getFollowedFarmerIds(customerId)
    suspend fun toggleFollowFarmer(customerId: String, farmerId: String, isFollowing: Boolean) {
        if (isFollowing) {
            dao.unfollowFarmer(customerId, farmerId)
        } else {
            dao.followFarmer(
                FollowedFarmerEntity(
                    id = "follow_${customerId}_$farmerId",
                    customerId = customerId,
                    farmerId = farmerId
                )
            )
        }
    }

    // --- Notifications ---
    fun getNotifications(userId: String): Flow<List<NotificationEntity>> = dao.getNotificationsForUser(userId)
    suspend fun markNotificationAsRead(id: String) = dao.markNotificationAsRead(id)

    // --- Audit Logs ---
    fun getAllAuditLogs(): Flow<List<AuditLogEntity>> = dao.getAllAuditLogs()
    suspend fun logAudit(action: String, performedBy: String, details: String) {
        dao.insertAuditLog(
            AuditLogEntity(
                id = "log_${System.currentTimeMillis()}",
                action = action,
                performedBy = performedBy,
                details = details
            )
        )
    }
}
