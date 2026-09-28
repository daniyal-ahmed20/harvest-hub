package com.example.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.*
import com.example.ui.admin.*
import com.example.ui.assistant.HarvestAssistantScreen
import com.example.ui.auth.*
import com.example.ui.customer.*
import com.example.ui.farmer.*
import com.example.ui.viewmodel.HarvestViewModel
import kotlinx.coroutines.flow.collectLatest

sealed class Screen {
    object RoleSelection : Screen()
    object CustomerLogin : Screen()
    object CustomerRegister : Screen()
    object FarmerLogin : Screen()
    object AdminLogin : Screen()

    // Customer
    object CustomerHome : Screen()
    object CustomerCategories : Screen()
    object CustomerSearch : Screen()
    data class CustomerProductDetail(val product: ProductEntity) : Screen()
    object CustomerCart : Screen()
    object CustomerCheckout : Screen()
    data class CustomerOrderSuccess(val orderIds: List<String>) : Screen()
    object CustomerOrders : Screen()
    data class CustomerOrderDetail(val order: OrderEntity) : Screen()
    object CustomerWishlist : Screen()
    data class CustomerFarmerProfile(val farmerId: String) : Screen()
    object CustomerFollowedFarmers : Screen()
    object CustomerPickup : Screen()
    object CustomerNotifications : Screen()
    object CustomerAssistant : Screen()
    object CustomerProfile : Screen()
    object CustomerEditProfile : Screen()
    object CustomerSettings : Screen()
    object CustomerAboutContact : Screen()

    // Farmer
    object FarmerDashboard : Screen()
    object FarmerProducts : Screen()
    object FarmerInventory : Screen()
    object FarmerOrders : Screen()
    object FarmerReports : Screen()
    object FarmerProfile : Screen()

    // Admin
    object AdminDashboard : Screen()
    object AdminCustomers : Screen()
    object AdminFarmers : Screen()
    object AdminProducts : Screen()
    object AdminCategories : Screen()
    object AdminOrders : Screen()
    object AdminReports : Screen()
    object AdminAuditLogs : Screen()
}

@Composable
fun HarvestAppNavigation(viewModel: HarvestViewModel) {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.RoleSelection) }
    val screenStack = remember { mutableStateListOf<Screen>(Screen.RoleSelection) }

    fun navigateTo(screen: Screen) {
        screenStack.add(screen)
        currentScreen = screen
    }

    fun popBack() {
        if (screenStack.size > 1) {
            screenStack.removeAt(screenStack.lastIndex)
            currentScreen = screenStack.last()
        } else {
            currentScreen = Screen.RoleSelection
        }
    }

    fun resetTo(screen: Screen) {
        screenStack.clear()
        screenStack.add(screen)
        currentScreen = screen
    }

    // State Collection
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val currentFarmer by viewModel.currentFarmer.collectAsStateWithLifecycle()
    val isDarkTheme by viewModel.isDarkTheme.collectAsStateWithLifecycle()

    val activeProducts by viewModel.allActiveProducts.collectAsStateWithLifecycle()
    val categories by viewModel.allCategories.collectAsStateWithLifecycle()
    val markets by viewModel.allMarkets.collectAsStateWithLifecycle()
    val farmers by viewModel.allFarmers.collectAsStateWithLifecycle()

    val cartItems by viewModel.cartItems.collectAsStateWithLifecycle()
    val cartCount = cartItems.sumOf { it.quantity }
    val wishlistIds by viewModel.wishlistProductIds.collectAsStateWithLifecycle()
    val notifications by viewModel.userNotifications.collectAsStateWithLifecycle()
    val unreadNotifCount = notifications.count { !it.isRead }

    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedMarketFilter by viewModel.selectedMarketFilter.collectAsStateWithLifecycle()
    val filteredProducts by viewModel.filteredProducts.collectAsStateWithLifecycle(initialValue = emptyList())

    val customerOrders by viewModel.customerOrders.collectAsStateWithLifecycle()
    val farmerProducts by viewModel.farmerProducts.collectAsStateWithLifecycle()
    val farmerOrders by viewModel.farmerOrders.collectAsStateWithLifecycle()

    val adminProducts by viewModel.allProductsAdmin.collectAsStateWithLifecycle()
    val adminCustomers by viewModel.allCustomersAdmin.collectAsStateWithLifecycle()
    val adminFarmers by viewModel.allFarmersAdmin.collectAsStateWithLifecycle()
    val adminOrders by viewModel.allOrdersAdmin.collectAsStateWithLifecycle()
    val adminAuditLogs by viewModel.allAuditLogs.collectAsStateWithLifecycle()

    val assistantMessages by viewModel.assistantMessages.collectAsStateWithLifecycle()
    val isAssistantThinking by viewModel.isAssistantThinking.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.uiAlertMessage.collectLatest { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val screen = currentScreen) {
                is Screen.RoleSelection -> {
                    RoleSelectionScreen(
                        onSelectCustomer = { navigateTo(Screen.CustomerLogin) },
                        onSelectFarmer = { navigateTo(Screen.FarmerLogin) },
                        onSelectAdmin = { navigateTo(Screen.AdminLogin) },
                        onQuickDemoLogin = { role ->
                            viewModel.quickLoginAs(role)
                            when (role) {
                                "Customer" -> resetTo(Screen.CustomerHome)
                                "Farmer" -> resetTo(Screen.FarmerDashboard)
                                "Administrator" -> resetTo(Screen.AdminDashboard)
                            }
                        }
                    )
                }

                is Screen.CustomerLogin -> {
                    BackHandler { popBack() }
                    CustomerLoginScreen(
                        onNavigateBack = { popBack() },
                        onLoginSuccess = { resetTo(Screen.CustomerHome) },
                        onNavigateToRegister = { navigateTo(Screen.CustomerRegister) },
                        onPerformLogin = { email, role, cb -> viewModel.login(email, role, cb) }
                    )
                }

                is Screen.CustomerRegister -> {
                    BackHandler { popBack() }
                    CustomerRegisterScreen(
                        onNavigateBack = { popBack() },
                        onRegisterSuccess = { resetTo(Screen.CustomerHome) },
                        onPerformRegister = { name, email, phone, addr, cb ->
                            viewModel.registerCustomer(name, email, phone, addr, cb)
                        }
                    )
                }

                is Screen.FarmerLogin -> {
                    BackHandler { popBack() }
                    FarmerLoginScreen(
                        onNavigateBack = { popBack() },
                        onLoginSuccess = { resetTo(Screen.FarmerDashboard) },
                        onPerformLogin = { email, role, cb -> viewModel.login(email, role, cb) }
                    )
                }

                is Screen.AdminLogin -> {
                    BackHandler { popBack() }
                    AdminLoginScreen(
                        onNavigateBack = { popBack() },
                        onLoginSuccess = { resetTo(Screen.AdminDashboard) },
                        onPerformLogin = { email, role, cb -> viewModel.login(email, role, cb) }
                    )
                }

                // Customer Flow
                is Screen.CustomerHome -> {
                    CustomerHomeScreen(
                        user = currentUser,
                        categories = categories,
                        products = if (selectedCategory != null) activeProducts.filter { it.category == selectedCategory } else activeProducts,
                        wishlistIds = wishlistIds,
                        cartCount = cartCount,
                        notificationCount = unreadNotifCount,
                        selectedCategory = selectedCategory,
                        onSelectCategory = { viewModel.setSelectedCategory(it) },
                        onProductClick = { prod -> navigateTo(Screen.CustomerProductDetail(prod)) },
                        onAddToCart = { prod -> viewModel.addToCart(prod, 1) },
                        onToggleWishlist = { prodId -> viewModel.toggleWishlist(prodId) },
                        onOpenSearch = { navigateTo(Screen.CustomerSearch) },
                        onOpenCart = { navigateTo(Screen.CustomerCart) },
                        onOpenNotifications = { navigateTo(Screen.CustomerNotifications) },
                        onOpenAssistant = { navigateTo(Screen.CustomerAssistant) }
                    )
                }

                is Screen.CustomerCategories -> {
                    BackHandler { popBack() }
                    CustomerCategoriesScreen(
                        categories = categories,
                        products = activeProducts,
                        wishlistIds = wishlistIds,
                        cartCount = cartCount,
                        onNavigateBack = { popBack() },
                        onProductClick = { prod -> navigateTo(Screen.CustomerProductDetail(prod)) },
                        onAddToCart = { prod -> viewModel.addToCart(prod, 1) },
                        onToggleWishlist = { prodId -> viewModel.toggleWishlist(prodId) },
                        onOpenCart = { navigateTo(Screen.CustomerCart) }
                    )
                }

                is Screen.CustomerSearch -> {
                    BackHandler { popBack() }
                    CustomerSearchScreen(
                        searchQuery = searchQuery,
                        onSearchQueryChange = { viewModel.setSearchQuery(it) },
                        categories = categories,
                        markets = markets,
                        selectedCategory = selectedCategory,
                        onSelectCategory = { viewModel.setSelectedCategory(it) },
                        selectedMarket = selectedMarketFilter,
                        onSelectMarket = { viewModel.setMarketFilter(it) },
                        products = filteredProducts,
                        wishlistIds = wishlistIds,
                        cartCount = cartCount,
                        onNavigateBack = { popBack() },
                        onProductClick = { prod -> navigateTo(Screen.CustomerProductDetail(prod)) },
                        onAddToCart = { prod -> viewModel.addToCart(prod, 1) },
                        onToggleWishlist = { prodId -> viewModel.toggleWishlist(prodId) },
                        onClearAll = { viewModel.clearFilters() }
                    )
                }

                is Screen.CustomerProductDetail -> {
                    BackHandler { popBack() }
                    CustomerProductDetailScreen(
                        product = screen.product,
                        isWishlisted = wishlistIds.contains(screen.product.productId),
                        cartCount = cartCount,
                        onNavigateBack = { popBack() },
                        onAddToCart = { qty -> viewModel.addToCart(screen.product, qty) },
                        onToggleWishlist = { viewModel.toggleWishlist(screen.product.productId) },
                        onOpenCart = { navigateTo(Screen.CustomerCart) },
                        onViewFarmerProfile = { farmerId -> navigateTo(Screen.CustomerFarmerProfile(farmerId)) }
                    )
                }

                is Screen.CustomerCart -> {
                    BackHandler { popBack() }
                    CustomerCartScreen(
                        cartItems = cartItems,
                        onNavigateBack = { popBack() },
                        onUpdateQuantity = { id, delta -> viewModel.updateCartQuantity(id, delta) },
                        onRemoveItem = { id -> viewModel.removeFromCart(id) },
                        onClearCart = { viewModel.clearCart() },
                        onProceedToCheckout = { navigateTo(Screen.CustomerCheckout) },
                        onBrowseProducts = { popBack() }
                    )
                }

                is Screen.CustomerCheckout -> {
                    BackHandler { popBack() }
                    CustomerCheckoutScreen(
                        customer = currentUser,
                        cartItems = cartItems,
                        onNavigateBack = { popBack() },
                        onConfirmOrder = { slot, notes, cb ->
                            viewModel.placeOrder(slot, notes, cb)
                        },
                        onOrderSuccess = { orderIds ->
                            resetTo(Screen.CustomerOrderSuccess(orderIds))
                        }
                    )
                }

                is Screen.CustomerOrderSuccess -> {
                    CustomerOrderSuccessScreen(
                        orderIds = screen.orderIds,
                        onViewOrderHistory = { resetTo(Screen.CustomerOrders) },
                        onContinueShopping = { resetTo(Screen.CustomerHome) }
                    )
                }

                is Screen.CustomerOrders -> {
                    BackHandler { resetTo(Screen.CustomerHome) }
                    CustomerOrdersScreen(
                        orders = customerOrders,
                        onNavigateBack = { resetTo(Screen.CustomerHome) },
                        onOrderClick = { ord -> navigateTo(Screen.CustomerOrderDetail(ord)) },
                        onStartShopping = { resetTo(Screen.CustomerHome) }
                    )
                }

                is Screen.CustomerOrderDetail -> {
                    BackHandler { popBack() }
                    CustomerOrderDetailScreen(
                        order = screen.order,
                        onNavigateBack = { popBack() }
                    )
                }

                is Screen.CustomerWishlist -> {
                    BackHandler { popBack() }
                    val wishlisted = activeProducts.filter { wishlistIds.contains(it.productId) }
                    CustomerWishlistScreen(
                        wishlistProducts = wishlisted,
                        cartCount = cartCount,
                        onNavigateBack = { popBack() },
                        onProductClick = { prod -> navigateTo(Screen.CustomerProductDetail(prod)) },
                        onAddToCart = { prod -> viewModel.addToCart(prod, 1) },
                        onToggleWishlist = { prodId -> viewModel.toggleWishlist(prodId) },
                        onOpenCart = { navigateTo(Screen.CustomerCart) },
                        onBrowseProducts = { popBack() }
                    )
                }

                is Screen.CustomerFarmerProfile -> {
                    BackHandler { popBack() }
                    val farmerObj = farmers.find { it.farmerId == screen.farmerId }
                        ?: FarmerEntity(screen.farmerId, "", "mkt_1", "Local Producer", "Family-run local organic farm", 4.9f)
                    val farmerProds = activeProducts.filter { it.farmerId == screen.farmerId }
                    CustomerFarmerProfileScreen(
                        farmer = farmerObj,
                        farmerProducts = farmerProds,
                        isFollowing = false,
                        wishlistIds = wishlistIds,
                        cartCount = cartCount,
                        onNavigateBack = { popBack() },
                        onToggleFollow = { viewModel.toggleFollowFarmer(screen.farmerId) },
                        onProductClick = { prod -> navigateTo(Screen.CustomerProductDetail(prod)) },
                        onAddToCart = { prod -> viewModel.addToCart(prod, 1) },
                        onToggleWishlist = { prodId -> viewModel.toggleWishlist(prodId) },
                        onOpenCart = { navigateTo(Screen.CustomerCart) }
                    )
                }

                is Screen.CustomerFollowedFarmers -> {
                    BackHandler { popBack() }
                    CustomerFollowedFarmersScreen(
                        followedFarmers = farmers,
                        onNavigateBack = { popBack() },
                        onFarmerClick = { f -> navigateTo(Screen.CustomerFarmerProfile(f.farmerId)) },
                        onDiscoverFarmers = { popBack() }
                    )
                }

                is Screen.CustomerPickup -> {
                    BackHandler { popBack() }
                    CustomerPickupScreen(onNavigateBack = { popBack() })
                }

                is Screen.CustomerNotifications -> {
                    BackHandler { popBack() }
                    CustomerNotificationsScreen(
                        notifications = notifications,
                        onNavigateBack = { popBack() },
                        onNotificationClick = { notif -> viewModel.markNotificationRead(notif.id) }
                    )
                }

                is Screen.CustomerAssistant -> {
                    BackHandler { popBack() }
                    HarvestAssistantScreen(
                        messages = assistantMessages,
                        isThinking = isAssistantThinking,
                        onSendMessage = { query -> viewModel.askAssistant(query) },
                        onNavigateBack = { popBack() }
                    )
                }

                is Screen.CustomerProfile -> {
                    BackHandler { popBack() }
                    CustomerProfileScreen(
                        user = currentUser,
                        onNavigateBack = { popBack() },
                        onEditProfile = { navigateTo(Screen.CustomerEditProfile) },
                        onViewOrders = { navigateTo(Screen.CustomerOrders) },
                        onViewWishlist = { navigateTo(Screen.CustomerWishlist) },
                        onViewFollowedFarmers = { navigateTo(Screen.CustomerFollowedFarmers) },
                        onViewPickupSlots = { navigateTo(Screen.CustomerPickup) },
                        onViewAboutContact = { navigateTo(Screen.CustomerAboutContact) },
                        onOpenSettings = { navigateTo(Screen.CustomerSettings) },
                        onLogout = {
                            viewModel.logout()
                            resetTo(Screen.RoleSelection)
                        }
                    )
                }

                is Screen.CustomerEditProfile -> {
                    BackHandler { popBack() }
                    CustomerEditProfileScreen(
                        user = currentUser,
                        onNavigateBack = { popBack() },
                        onSaveProfile = { name, phone, addr, cb ->
                            viewModel.updateCustomerProfile(name, phone, addr, cb)
                        }
                    )
                }

                is Screen.CustomerSettings -> {
                    BackHandler { popBack() }
                    CustomerSettingsScreen(
                        isDarkTheme = isDarkTheme,
                        onToggleDarkTheme = { viewModel.toggleDarkTheme() },
                        onNavigateBack = { popBack() }
                    )
                }

                is Screen.CustomerAboutContact -> {
                    BackHandler { popBack() }
                    CustomerAboutContactScreen(onNavigateBack = { popBack() })
                }

                // Farmer Flow
                is Screen.FarmerDashboard -> {
                    FarmerDashboardScreen(
                        farmer = currentFarmer,
                        products = farmerProducts,
                        orders = farmerOrders,
                        onAddProduct = { navigateTo(Screen.FarmerProducts) },
                        onViewAllProducts = { navigateTo(Screen.FarmerProducts) },
                        onViewInventory = { navigateTo(Screen.FarmerInventory) },
                        onViewOrders = { navigateTo(Screen.FarmerOrders) },
                        onViewReports = { navigateTo(Screen.FarmerReports) },
                        onViewProfile = { navigateTo(Screen.FarmerProfile) },
                        onLogout = {
                            viewModel.logout()
                            resetTo(Screen.RoleSelection)
                        }
                    )
                }

                is Screen.FarmerProducts -> {
                    BackHandler { popBack() }
                    FarmerProductsScreen(
                        products = farmerProducts,
                        onNavigateBack = { popBack() },
                        onSaveProduct = { id, name, cat, price, stock, unit, desc, isOrg, cb ->
                            viewModel.saveFarmerProduct(id, name, cat, price, stock, unit, desc, isOrg, cb)
                        },
                        onDeleteProduct = { id -> viewModel.deleteProduct(id) }
                    )
                }

                is Screen.FarmerInventory -> {
                    BackHandler { popBack() }
                    FarmerInventoryScreen(
                        products = farmerProducts,
                        onNavigateBack = { popBack() },
                        onUpdateStock = { id, newStock -> viewModel.updateStock(id, newStock) }
                    )
                }

                is Screen.FarmerOrders -> {
                    BackHandler { popBack() }
                    FarmerOrdersScreen(
                        orders = farmerOrders,
                        onNavigateBack = { popBack() },
                        onUpdateStatus = { id, status -> viewModel.updateOrderStatus(id, status) }
                    )
                }

                is Screen.FarmerReports -> {
                    BackHandler { popBack() }
                    FarmerReportsScreen(
                        orders = farmerOrders,
                        products = farmerProducts,
                        onNavigateBack = { popBack() }
                    )
                }

                is Screen.FarmerProfile -> {
                    BackHandler { popBack() }
                    FarmerProfileScreen(
                        farmer = currentFarmer,
                        onNavigateBack = { popBack() },
                        onUpdateProfile = { name, desc, phone, loc, cb ->
                            viewModel.updateFarmerProfile(name, desc, phone, loc, cb)
                        },
                        onLogout = {
                            viewModel.logout()
                            resetTo(Screen.RoleSelection)
                        }
                    )
                }

                // Administrator Flow
                is Screen.AdminDashboard -> {
                    AdminDashboardScreen(
                        customers = adminCustomers,
                        farmers = adminFarmers,
                        products = adminProducts,
                        orders = adminOrders,
                        auditLogs = adminAuditLogs,
                        firebaseStatusText = "Cloud Active",
                        onSyncFirebase = {
                            viewModel.syncWithFirebase { _, _ -> }
                        },
                        onViewCustomers = { navigateTo(Screen.AdminCustomers) },
                        onViewFarmers = { navigateTo(Screen.AdminFarmers) },
                        onViewProducts = { navigateTo(Screen.AdminProducts) },
                        onViewCategories = { navigateTo(Screen.AdminCategories) },
                        onViewOrders = { navigateTo(Screen.AdminOrders) },
                        onViewReports = { navigateTo(Screen.AdminReports) },
                        onViewAuditLogs = { navigateTo(Screen.AdminAuditLogs) },
                        onLogout = {
                            viewModel.logout()
                            resetTo(Screen.RoleSelection)
                        }
                    )
                }

                is Screen.AdminCustomers -> {
                    BackHandler { popBack() }
                    AdminCustomersScreen(
                        customers = adminCustomers,
                        onNavigateBack = { popBack() }
                    )
                }

                is Screen.AdminFarmers -> {
                    BackHandler { popBack() }
                    AdminFarmersScreen(
                        farmers = adminFarmers,
                        onNavigateBack = { popBack() }
                    )
                }

                is Screen.AdminProducts -> {
                    BackHandler { popBack() }
                    AdminProductsScreen(
                        products = adminProducts,
                        onToggleActive = { id, currentActive -> viewModel.toggleProductActiveAdmin(id, currentActive) },
                        onNavigateBack = { popBack() }
                    )
                }

                is Screen.AdminCategories -> {
                    BackHandler { popBack() }
                    AdminCategoriesScreen(
                        categories = categories,
                        onAddCategory = { name, desc, icon, cb -> viewModel.addCategoryAdmin(name, desc, icon, cb) },
                        onNavigateBack = { popBack() }
                    )
                }

                is Screen.AdminOrders -> {
                    BackHandler { popBack() }
                    AdminOrdersScreen(
                        orders = adminOrders,
                        onUpdateStatus = { id, st -> viewModel.updateOrderStatus(id, st) },
                        onNavigateBack = { popBack() }
                    )
                }

                is Screen.AdminReports -> {
                    BackHandler { popBack() }
                    AdminReportsScreen(
                        orders = adminOrders,
                        onNavigateBack = { popBack() }
                    )
                }

                is Screen.AdminAuditLogs -> {
                    BackHandler { popBack() }
                    AdminAuditLogsScreen(
                        auditLogs = adminAuditLogs,
                        onNavigateBack = { popBack() }
                    )
                }
            }
        }
    }
}
