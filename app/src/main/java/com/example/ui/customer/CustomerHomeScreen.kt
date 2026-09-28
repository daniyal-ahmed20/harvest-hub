package com.example.ui.customer

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.CategoryEntity
import com.example.data.model.ProductEntity
import com.example.data.model.UserEntity
import com.example.ui.components.EmptyStateView
import com.example.ui.components.HarvestTopBar
import com.example.ui.components.ProductCard
import com.example.ui.theme.*

@Composable
fun CustomerHomeScreen(
    user: UserEntity?,
    categories: List<CategoryEntity>,
    products: List<ProductEntity>,
    wishlistIds: List<String>,
    cartCount: Int,
    notificationCount: Int,
    selectedCategory: String?,
    onSelectCategory: (String?) -> Unit,
    onProductClick: (ProductEntity) -> Unit,
    onAddToCart: (ProductEntity) -> Unit,
    onToggleWishlist: (String) -> Unit,
    onOpenSearch: () -> Unit,
    onOpenCart: () -> Unit,
    onOpenNotifications: () -> Unit,
    onOpenAssistant: () -> Unit
) {
    Scaffold(
        topBar = {
            HarvestTopBar(
                title = "HarvestHub",
                subtitle = "Welcome, ${user?.name ?: "Guest"}",
                canNavigateBack = false,
                cartBadgeCount = cartCount,
                onCartClick = onOpenCart,
                notificationCount = notificationCount,
                onNotificationClick = onOpenNotifications,
                actions = {
                    IconButton(
                        onClick = onOpenAssistant,
                        modifier = Modifier.testTag("btn_assistant_shortcut")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = "Farm Assistant",
                            tint = HarvestForestGreen
                        )
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // Search Input Trigger
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .border(1.dp, BorderLight, RoundedCornerShape(16.dp))
                        .clickable { onOpenSearch() }
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                        .testTag("home_search_bar_trigger")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.Search,
                            contentDescription = "Search",
                            tint = TextMuted
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Search heirloom tomatoes, farm milk, berries...",
                            style = MaterialTheme.typography.bodyMedium.copy(color = TextMuted)
                        )
                    }
                }
            }

            // Hero Farm Landscape Banner
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Box(modifier = Modifier.height(150.dp).fillMaxWidth()) {
                        Image(
                            painter = painterResource(id = R.drawable.farm_hero_banner_1790620818217),
                            contentDescription = "Local Organic Farm Scenery",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Color.Black.copy(alpha = 0.35f)
                                )
                        )
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(16.dp)
                        ) {
                            Surface(
                                color = HarvestForestGreen,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "100% FARM-TO-TABLE",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Fresh Morning Harvest",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                            Text(
                                text = "Zero intermediaries. Direct support for local farm families.",
                                style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.9f))
                            )
                        }
                    }
                }
            }

            // Agricultural Assistant Promo Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onOpenAssistant() },
                    colors = CardDefaults.cardColors(containerColor = HarvestMint)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(HarvestForestGreen),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Spa,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "AI Farm Products Assistant",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = HarvestForestDark
                                )
                            )
                            Text(
                                text = "Get seasonal recipes, storage tips & farm nutrition guidance",
                                style = MaterialTheme.typography.bodySmall.copy(color = HarvestForestLight)
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.ArrowForwardIos,
                            contentDescription = null,
                            tint = HarvestForestGreen,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            // Categories Section
            item {
                Column(modifier = Modifier.padding(top = 16.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Browse Categories",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        if (selectedCategory != null) {
                            TextButton(onClick = { onSelectCategory(null) }) {
                                Text("Clear Filter", color = HarvestForestGreen, fontSize = 12.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // "All" chip
                        item {
                            FilterChip(
                                selected = selectedCategory == null,
                                onClick = { onSelectCategory(null) },
                                label = { Text("All Products") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = HarvestForestGreen,
                                    selectedLabelColor = Color.White
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                        items(categories) { cat ->
                            FilterChip(
                                selected = selectedCategory == cat.name,
                                onClick = {
                                    if (selectedCategory == cat.name) {
                                        onSelectCategory(null)
                                    } else {
                                        onSelectCategory(cat.name)
                                    }
                                },
                                label = { Text(cat.name) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = HarvestForestGreen,
                                    selectedLabelColor = Color.White
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }
                }
            }

            // Products Section
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (selectedCategory == null) "Fresh Farm Harvest" else "$selectedCategory from Local Farms",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "${products.size} items",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (products.isEmpty()) {
                item {
                    EmptyStateView(
                        title = "No fresh products available",
                        subtitle = "We couldn't find any products in this selection.",
                        actionLabel = "Clear Filter",
                        onAction = { onSelectCategory(null) }
                    )
                }
            } else {
                // Products in paired rows for optimal scrolling
                val chunkedProducts = products.chunked(2)
                items(chunkedProducts) { pair ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        for (prod in pair) {
                            Box(modifier = Modifier.weight(1f)) {
                                ProductCard(
                                    product = prod,
                                    isWishlisted = wishlistIds.contains(prod.productId),
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
