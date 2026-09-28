package com.example.ui.customer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.data.model.ProductEntity
import com.example.ui.components.EmptyStateView
import com.example.ui.components.HarvestTopBar
import com.example.ui.components.ProductCard
import com.example.ui.theme.*

@Composable
fun CustomerWishlistScreen(
    wishlistProducts: List<ProductEntity>,
    cartCount: Int,
    onNavigateBack: () -> Unit,
    onProductClick: (ProductEntity) -> Unit,
    onAddToCart: (ProductEntity) -> Unit,
    onToggleWishlist: (String) -> Unit,
    onOpenCart: () -> Unit,
    onBrowseProducts: () -> Unit
) {
    Scaffold(
        topBar = {
            HarvestTopBar(
                title = "My Wishlist",
                subtitle = "${wishlistProducts.size} saved favorites",
                canNavigateBack = true,
                onNavigateBack = onNavigateBack,
                cartBadgeCount = cartCount,
                onCartClick = onOpenCart
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        if (wishlistProducts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                EmptyStateView(
                    title = "Your wishlist is empty",
                    subtitle = "Save fresh seasonal favorites to reorder anytime.",
                    actionLabel = "Browse Products",
                    onAction = onBrowseProducts,
                    icon = Icons.Outlined.FavoriteBorder
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val paired = wishlistProducts.chunked(2)
                items(paired) { pair ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        for (prod in pair) {
                            Box(modifier = Modifier.weight(1f)) {
                                ProductCard(
                                    product = prod,
                                    isWishlisted = true,
                                    onProductClick = { onProductClick(prod) },
                                    onAddToCart = { onAddToCart(prod) },
                                    onToggleWishlist = { onToggleWishlist(prod.productId) }
                                )
                            }
                        }
                        if (pair.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}
