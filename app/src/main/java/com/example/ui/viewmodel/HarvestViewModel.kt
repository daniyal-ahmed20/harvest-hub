package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.firebase.FirebaseStatus
import com.example.data.firebase.HarvestFirebaseManager
import com.example.data.local.HarvestHubDatabase
import com.example.data.model.*
import com.example.data.repository.HarvestHubRepository
import com.example.data.seed.SeedData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HarvestViewModel(application: Application) : AndroidViewModel(application) {

    private val db = HarvestHubDatabase.getDatabase(application, viewModelScope)
    val repository = HarvestHubRepository(db.dao())

    // --- Firebase Synchronization & Status ---
    val firebaseStatus: StateFlow<FirebaseStatus> = HarvestFirebaseManager.connectionStatus
    val lastSyncTime: StateFlow<Long?> = HarvestFirebaseManager.lastSyncTime

    // --- Authentication & Active User ---
    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    private val _currentFarmer = MutableStateFlow<FarmerEntity?>(null)
    val currentFarmer: StateFlow<FarmerEntity?> = _currentFarmer.asStateFlow()

    private val _isDarkTheme = MutableStateFlow(false)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    fun toggleDarkTheme() {
        _isDarkTheme.value = !_isDarkTheme.value
    }

    // --- Customer Navigation & Screen State ---
    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedMarketFilter = MutableStateFlow<String?>(null)
    val selectedMarketFilter: StateFlow<String?> = _selectedMarketFilter.asStateFlow()

    private val _maxPriceFilter = MutableStateFlow<Float?>(null)
    val maxPriceFilter: StateFlow<Float?> = _maxPriceFilter.asStateFlow()

    // --- UI Notifications & Status Alerts ---
    private val _uiAlertMessage = MutableSharedFlow<String>()
    val uiAlertMessage = _uiAlertMessage.asSharedFlow()

    // --- Assistant Conversation ---
    private val _assistantMessages = MutableStateFlow<List<AssistantMessage>>(
        listOf(
            AssistantMessage(
                isFromUser = false,
                text = "Hello! I am your HarvestHub Farm Assistant. Ask me anything about seasonal crops, storage tips, organic nutrition, or fresh farm recipes!"
            )
        )
    )
    val assistantMessages: StateFlow<List<AssistantMessage>> = _assistantMessages.asStateFlow()

    private val _isAssistantThinking = MutableStateFlow(false)
    val isAssistantThinking: StateFlow<Boolean> = _isAssistantThinking.asStateFlow()

    // --- Reactive DB Flows ---
    val allActiveProducts: StateFlow<List<ProductEntity>> = repository.getAllActiveProducts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allProductsAdmin: StateFlow<List<ProductEntity>> = repository.getAllProductsAdmin()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allCategories: StateFlow<List<CategoryEntity>> = repository.getAllCategories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allMarkets: StateFlow<List<MarketEntity>> = repository.getAllMarkets()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allFarmers: StateFlow<List<FarmerEntity>> = repository.getAllActiveFarmers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allFarmersAdmin: StateFlow<List<FarmerEntity>> = repository.getAllFarmers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allCustomersAdmin: StateFlow<List<UserEntity>> = repository.getAllCustomers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allOrdersAdmin: StateFlow<List<OrderEntity>> = repository.getAllOrdersAdmin()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allAuditLogs: StateFlow<List<AuditLogEntity>> = repository.getAllAuditLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val cartItems: StateFlow<List<CartItem>> = repository.cartItems

    // Customer specific flows
    val wishlistProductIds: StateFlow<List<String>> = _currentUser.flatMapLatest { user ->
        if (user != null) repository.getWishlistProductIds(user.userId) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val followedFarmerIds: StateFlow<List<String>> = _currentUser.flatMapLatest { user ->
        if (user != null) repository.getFollowedFarmerIds(user.userId) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val customerOrders: StateFlow<List<OrderEntity>> = _currentUser.flatMapLatest { user ->
        if (user != null) repository.getOrdersByCustomer(user.userId) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userNotifications: StateFlow<List<NotificationEntity>> = _currentUser.flatMapLatest { user ->
        if (user != null) repository.getNotifications(user.userId) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Farmer specific flows
    val farmerProducts: StateFlow<List<ProductEntity>> = _currentFarmer.flatMapLatest { farmer ->
        if (farmer != null) repository.getProductsByFarmer(farmer.farmerId) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val farmerOrders: StateFlow<List<OrderEntity>> = _currentFarmer.flatMapLatest { farmer ->
        if (farmer != null) repository.getOrdersByFarmer(farmer.farmerId) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        HarvestFirebaseManager.initialize(application)

        // Ensure default initial users are available in DB
        viewModelScope.launch(Dispatchers.IO) {
            val admin = repository.getUserByEmail("admin@harvesthub.com")
            if (admin == null) {
                SeedData.defaultUsers.forEach { repository.insertUser(it) }
                SeedData.defaultMarkets.forEach { db.dao().insertMarket(it) }
                SeedData.defaultFarmers.forEach { db.dao().insertFarmer(it) }
                SeedData.defaultCategories.forEach { repository.insertCategory(it) }
                SeedData.defaultProducts.forEach { repository.insertProduct(it) }
                SeedData.defaultOrders.forEach { db.dao().insertOrder(it) }
                SeedData.defaultNotifications.forEach { db.dao().insertNotification(it) }
                SeedData.defaultAuditLogs.forEach { db.dao().insertAuditLog(it) }
            }
        }
    }

    // --- Authentication Actions ---
    fun login(email: String, role: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = repository.getUserByEmail(email.trim())
            if (user != null) {
                if (user.role.equals(role, ignoreCase = true) || role == "Any") {
                    _currentUser.value = user
                    if (user.role == "Farmer") {
                        val farmer = repository.getFarmerByUserId(user.userId)
                        _currentFarmer.value = farmer
                    }
                    onResult(true, null)
                } else {
                    onResult(false, "This account is registered as a ${user.role}, not $role.")
                }
            } else {
                onResult(false, "No account found with this email.")
            }
        }
    }

    fun quickLoginAs(role: String) {
        viewModelScope.launch(Dispatchers.IO) {
            when (role) {
                "Customer" -> {
                    val user = repository.getUserByEmail("sarah.customer@example.com")
                    _currentUser.value = user
                }
                "Farmer" -> {
                    val user = repository.getUserByEmail("john.farmer@greenvalley.com")
                    _currentUser.value = user
                    val farmer = repository.getFarmerByUserId(user?.userId ?: "")
                    _currentFarmer.value = farmer
                }
                "Administrator" -> {
                    val user = repository.getUserByEmail("admin@harvesthub.com")
                    _currentUser.value = user
                }
            }
        }
    }

    fun registerCustomer(
        name: String,
        email: String,
        phone: String,
        address: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val existing = repository.getUserByEmail(email.trim())
            if (existing != null) {
                onResult(false, "An account with this email already exists.")
                return@launch
            }
            val newUser = UserEntity(
                userId = "cust_${System.currentTimeMillis()}",
                name = name.trim(),
                email = email.trim(),
                role = "Customer",
                phone = phone.trim(),
                address = address.trim(),
                createdAt = System.currentTimeMillis()
            )
            repository.insertUser(newUser)
            _currentUser.value = newUser
            repository.logAudit("USER_REGISTERED", newUser.email, "New customer account created.")
            onResult(true, null)
        }
    }

    fun logout() {
        _currentUser.value = null
        _currentFarmer.value = null
        repository.clearCart()
    }

    // --- Search & Filters ---
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedCategory(category: String?) {
        _selectedCategory.value = category
    }

    fun setMarketFilter(market: String?) {
        _selectedMarketFilter.value = market
    }

    fun setPriceFilter(maxPrice: Float?) {
        _maxPriceFilter.value = maxPrice
    }

    fun clearFilters() {
        _searchQuery.value = ""
        _selectedCategory.value = null
        _selectedMarketFilter.value = null
        _maxPriceFilter.value = null
    }

    // Filtered Products for Customer
    val filteredProducts: Flow<List<ProductEntity>> = combine(
        allActiveProducts,
        _searchQuery,
        _selectedCategory,
        _selectedMarketFilter,
        _maxPriceFilter
    ) { products, query, category, market, maxPrice ->
        products.filter { prod ->
            val matchesQuery = query.isBlank() || prod.itemName.contains(query, ignoreCase = true) ||
                    prod.description.contains(query, ignoreCase = true) ||
                    prod.farmerName.contains(query, ignoreCase = true)
            val matchesCategory = category.isNullOrBlank() || prod.category.equals(category, ignoreCase = true)
            val matchesMarket = market.isNullOrBlank() || prod.marketName.equals(market, ignoreCase = true)
            val matchesPrice = maxPrice == null || prod.pricePerUnit <= maxPrice
            matchesQuery && matchesCategory && matchesMarket && matchesPrice
        }
    }

    // --- Cart Actions ---
    fun addToCart(product: ProductEntity, qty: Int = 1) {
        val success = repository.addToCart(product, qty)
        viewModelScope.launch {
            if (success) {
                _uiAlertMessage.emit("Added ${product.itemName} to your basket!")
            } else {
                _uiAlertMessage.emit("Sorry, ${product.itemName} is out of stock.")
            }
        }
    }

    fun updateCartQuantity(productId: String, delta: Int) {
        repository.updateCartQuantity(productId, delta)
    }

    fun removeFromCart(productId: String) {
        repository.removeFromCart(productId)
    }

    fun clearCart() {
        repository.clearCart()
    }

    // --- Order Checkout ---
    fun placeOrder(
        pickupSlot: String,
        notes: String,
        onComplete: (Boolean, List<String>?, String?) -> Unit
    ) {
        val user = _currentUser.value ?: return onComplete(false, null, "Please sign in to place an order.")
        val items = cartItems.value
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.placeOrder(user, items, pickupSlot, notes)
            if (result.isSuccess) {
                val orderIds = result.getOrNull()
                onComplete(true, orderIds, null)
            } else {
                onComplete(false, null, result.exceptionOrNull()?.message ?: "Order placement failed.")
            }
        }
    }

    // --- Wishlist & Follow ---
    fun toggleWishlist(productId: String) {
        val user = _currentUser.value ?: return
        val currentIds = wishlistProductIds.value
        val isWishlisted = currentIds.contains(productId)
        viewModelScope.launch(Dispatchers.IO) {
            repository.toggleWishlist(user.userId, productId, isWishlisted)
            _uiAlertMessage.emit(if (isWishlisted) "Removed from Wishlist" else "Saved to Wishlist!")
        }
    }

    fun toggleFollowFarmer(farmerId: String) {
        val user = _currentUser.value ?: return
        val currentIds = followedFarmerIds.value
        val isFollowing = currentIds.contains(farmerId)
        viewModelScope.launch(Dispatchers.IO) {
            repository.toggleFollowFarmer(user.userId, farmerId, isFollowing)
            _uiAlertMessage.emit(if (isFollowing) "Unfollowed farmer" else "Now following farmer!")
        }
    }

    // --- Profile Updates ---
    fun updateCustomerProfile(name: String, phone: String, address: String, onDone: () -> Unit) {
        val current = _currentUser.value ?: return
        val updated = current.copy(name = name.trim(), phone = phone.trim(), address = address.trim())
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateUser(updated)
            _currentUser.value = updated
            _uiAlertMessage.emit("Profile updated successfully.")
            onDone()
        }
    }

    // --- Farmer Operations ---
    fun saveFarmerProduct(
        productId: String?,
        name: String,
        category: String,
        price: Double,
        stock: Int,
        unit: String,
        description: String,
        isOrganic: Boolean,
        onDone: () -> Unit
    ) {
        val farmer = _currentFarmer.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            if (productId == null) {
                val newProd = ProductEntity(
                    productId = "prod_${System.currentTimeMillis()}",
                    farmerId = farmer.farmerId,
                    farmerName = farmer.businessName,
                    itemName = name.trim(),
                    category = category,
                    pricePerUnit = price,
                    stockQty = stock,
                    unit = unit,
                    description = description.trim(),
                    isOrganic = isOrganic,
                    marketName = "Central Farmers Market"
                )
                repository.insertProduct(newProd)
            } else {
                val existing = repository.getProductById(productId)
                if (existing != null) {
                    val updated = existing.copy(
                        itemName = name.trim(),
                        category = category,
                        pricePerUnit = price,
                        stockQty = stock,
                        unit = unit,
                        description = description.trim(),
                        isOrganic = isOrganic
                    )
                    repository.updateProduct(updated)
                }
            }
            onDone()
        }
    }

    fun updateStock(productId: String, newStock: Int) {
        val farmer = _currentFarmer.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateStock(productId, newStock, farmer.businessName)
        }
    }

    fun deleteProduct(productId: String) {
        val farmer = _currentFarmer.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteProduct(productId, farmer.businessName)
        }
    }

    fun updateOrderStatus(orderId: String, newStatus: String) {
        val user = _currentUser.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateOrderStatus(orderId, newStatus, user.name)
        }
    }

    fun updateFarmerProfile(businessName: String, description: String, phone: String, location: String, onDone: () -> Unit) {
        val farmer = _currentFarmer.value ?: return
        val updated = farmer.copy(
            businessName = businessName.trim(),
            description = description.trim(),
            contactPhone = phone.trim(),
            location = location.trim()
        )
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateFarmer(updated)
            _currentFarmer.value = updated
            _uiAlertMessage.emit("Farmer profile updated.")
            onDone()
        }
    }

    // --- Admin Operations ---
    fun toggleProductActiveAdmin(productId: String, currentActive: Boolean) {
        val user = _currentUser.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            repository.setProductActive(productId, !currentActive, user.name)
        }
    }

    fun addCategoryAdmin(name: String, description: String, icon: String, onDone: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val cat = CategoryEntity(
                categoryId = "cat_${System.currentTimeMillis()}",
                name = name.trim(),
                description = description.trim(),
                iconName = icon
            )
            repository.insertCategory(cat)
            repository.logAudit("CATEGORY_ADDED", "Admin", "Added new category '$name'.")
            onDone()
        }
    }

    // --- Assistant Queries ---
    fun askAssistant(query: String) {
        if (query.isBlank()) return
        val userMsg = AssistantMessage(isFromUser = true, text = query.trim())
        _assistantMessages.value = _assistantMessages.value + userMsg
        _isAssistantThinking.value = true

        viewModelScope.launch {
            delay(600) // Realistic thoughtful agricultural processing delay
            val answer = com.example.data.knowledge_base.HarvestAssistantData.getAnswerForQuery(query)
            val botMsg = AssistantMessage(isFromUser = false, text = answer)
            _assistantMessages.value = _assistantMessages.value + botMsg
            _isAssistantThinking.value = false
        }
    }

    fun markNotificationRead(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.markNotificationAsRead(id)
        }
    }

    fun syncWithFirebase(onComplete: (Int, String) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val prods = allProductsAdmin.value
            val ords = allOrdersAdmin.value
            val farms = allFarmersAdmin.value
            val result = HarvestFirebaseManager.syncAllLocalData(prods, ords, farms)
            _uiAlertMessage.emit(result.second)
            onComplete(result.first, result.second)
        }
    }
}
