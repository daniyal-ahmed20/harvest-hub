package com.example.ui.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CategoryEntity
import com.example.data.model.ProductEntity
import com.example.ui.components.HarvestTopBar
import com.example.ui.components.ProductCard
import com.example.ui.theme.*

@Composable
fun CustomerCategoriesScreen(
    categories: List<CategoryEntity>,
    products: List<ProductEntity>,
    wishlistIds: List<String>,
    cartCount: Int,
    onNavigateBack: () -> Unit,
    onProductClick: (ProductEntity) -> Unit,
    onAddToCart: (ProductEntity) -> Unit,
    onToggleWishlist: (String) -> Unit,
    onOpenCart: () -> Unit
) {
    var selectedCategoryName by remember { mutableStateOf<String?>(categories.firstOrNull()?.name) }

    val filteredProducts = remember(selectedCategoryName, products) {
        if (selectedCategoryName == null) products
        else products.filter { it.category.equals(selectedCategoryName, ignoreCase = true) }
    }

    Scaffold(
        topBar = {
            HarvestTopBar(
                title = "Farm Categories",
                subtitle = "Browse verified farm catalog",
                canNavigateBack = true,
                onNavigateBack = onNavigateBack,
                cartBadgeCount = cartCount,
                onCartClick = onOpenCart
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Category Selector Horizontal Carousel or Grid
            item {
                Text(
                    text = "Select Category",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    val chunked = categories.chunked(2)
                    for (row in chunked) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            for (cat in row) {
                                val isSelected = selectedCategoryName == cat.name
                                Card(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(14.dp))
                                        .clickable { selectedCategoryName = cat.name }
                                        .testTag("cat_card_${cat.name}"),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isSelected) HarvestForestGreen else MaterialTheme.colorScheme.surface
                                    ),
                                    elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 1.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(CircleShape)
                                                .background(if (isSelected) Color.White.copy(alpha = 0.2f) else HarvestMint),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = getCategoryIcon(cat.name),
                                                contentDescription = null,
                                                tint = if (isSelected) Color.White else HarvestForestGreen,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            text = cat.name,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.SemiBold,
                                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onBackground
                                            ),
                                            fontSize = 13.sp
                                        )
                                    }
                                }
                            }
                            if (row.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${selectedCategoryName ?: "All"} Products",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "${filteredProducts.size} available",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                    )
                }
            }

            // Products in this category
            val paired = filteredProducts.chunked(2)
            items(paired) { pair ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
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

fun getCategoryIcon(name: String): ImageVector {
    return when (name) {
        "Fruits" -> Icons.Default.Favorite
        "Vegetables" -> Icons.Default.Eco
        "Grains/Pulses" -> Icons.Default.Grain
        "Dairy" -> Icons.Default.LocalDrink
        "Herbs/Spices" -> Icons.Default.Spa
        "Organic Products" -> Icons.Default.Inventory2
        else -> Icons.Default.Grass
    }
}
