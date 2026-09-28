package com.example.data.local

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface HarvestHubDao {

    // --- Users ---
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE userId = :userId LIMIT 1")
    fun getUserById(userId: String): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE role = 'Customer'")
    fun getAllCustomers(): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    // --- Farmers ---
    @Query("SELECT * FROM farmers WHERE farmerId = :farmerId LIMIT 1")
    fun getFarmerById(farmerId: String): Flow<FarmerEntity?>

    @Query("SELECT * FROM farmers WHERE userId = :userId LIMIT 1")
    suspend fun getFarmerByUserId(userId: String): FarmerEntity?

    @Query("SELECT * FROM farmers WHERE isActive = 1")
    fun getAllActiveFarmers(): Flow<List<FarmerEntity>>

    @Query("SELECT * FROM farmers")
    fun getAllFarmers(): Flow<List<FarmerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFarmer(farmer: FarmerEntity)

    @Update
    suspend fun updateFarmer(farmer: FarmerEntity)

    // --- Products ---
    @Query("SELECT * FROM products WHERE isActive = 1 ORDER BY itemName ASC")
    fun getAllActiveProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products ORDER BY itemName ASC")
    fun getAllProductsAdmin(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE farmerId = :farmerId")
    fun getProductsByFarmer(farmerId: String): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE productId = :productId LIMIT 1")
    suspend fun getProductById(productId: String): ProductEntity?

    @Query("SELECT * FROM products WHERE productId = :productId LIMIT 1")
    fun observeProductById(productId: String): Flow<ProductEntity?>

    @Query("SELECT * FROM products WHERE isActive = 1 AND category = :category")
    fun getProductsByCategory(category: String): Flow<List<ProductEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity)

    @Update
    suspend fun updateProduct(product: ProductEntity)

    @Query("UPDATE products SET stockQty = :newStock WHERE productId = :productId")
    suspend fun updateStock(productId: String, newStock: Int)

    @Query("UPDATE products SET isActive = :isActive WHERE productId = :productId")
    suspend fun setProductActive(productId: String, isActive: Boolean)

    @Query("DELETE FROM products WHERE productId = :productId")
    suspend fun deleteProduct(productId: String)

    // --- Orders ---
    @Query("SELECT * FROM orders WHERE customerId = :customerId ORDER BY createdAt DESC")
    fun getOrdersByCustomer(customerId: String): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE farmerId = :farmerId ORDER BY createdAt DESC")
    fun getOrdersByFarmer(farmerId: String): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders ORDER BY createdAt DESC")
    fun getAllOrdersAdmin(): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE orderId = :orderId LIMIT 1")
    fun getOrderById(orderId: String): Flow<OrderEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity)

    @Query("UPDATE orders SET status = :newStatus WHERE orderId = :orderId")
    suspend fun updateOrderStatus(orderId: String, newStatus: String)

    // --- Markets ---
    @Query("SELECT * FROM markets WHERE activeStatus = 1")
    fun getAllActiveMarkets(): Flow<List<MarketEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMarket(market: MarketEntity)

    // --- Categories ---
    @Query("SELECT * FROM categories WHERE isActive = 1")
    fun getAllCategories(): Flow<List<CategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CategoryEntity)

    // --- Wishlist ---
    @Query("SELECT * FROM wishlist WHERE customerId = :customerId")
    fun getWishlistForCustomer(customerId: String): Flow<List<WishlistEntity>>

    @Query("SELECT productId FROM wishlist WHERE customerId = :customerId")
    fun getWishlistProductIds(customerId: String): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToWishlist(wishlist: WishlistEntity)

    @Query("DELETE FROM wishlist WHERE customerId = :customerId AND productId = :productId")
    suspend fun removeFromWishlist(customerId: String, productId: String)

    // --- Followed Farmers ---
    @Query("SELECT farmerId FROM followed_farmers WHERE customerId = :customerId")
    fun getFollowedFarmerIds(customerId: String): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun followFarmer(followed: FollowedFarmerEntity)

    @Query("DELETE FROM followed_farmers WHERE customerId = :customerId AND farmerId = :farmerId")
    suspend fun unfollowFarmer(customerId: String, farmerId: String)

    // --- Notifications ---
    @Query("SELECT * FROM notifications WHERE recipientUserId = :userId ORDER BY timestamp DESC")
    fun getNotificationsForUser(userId: String): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markNotificationAsRead(id: String)

    // --- Audit Logs ---
    @Query("SELECT * FROM audit_logs ORDER BY timestamp DESC")
    fun getAllAuditLogs(): Flow<List<AuditLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuditLog(log: AuditLogEntity)
}
