package com.example.ui.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ProductEntity
import com.example.ui.components.HarvestButton
import com.example.ui.components.HarvestTopBar
import com.example.ui.theme.*

@Composable
fun CustomerProductDetailScreen(
    product: ProductEntity,
    isWishlisted: Boolean,
    cartCount: Int,
    onNavigateBack: () -> Unit,
    onAddToCart: (Int) -> Unit,
    onToggleWishlist: () -> Unit,
    onOpenCart: () -> Unit,
    onViewFarmerProfile: (String) -> Unit
) {
    var quantity by remember { mutableStateOf(1) }
    val maxStock = product.stockQty
    val isOutOfStock = maxStock <= 0

    Scaffold(
        topBar = {
            HarvestTopBar(
                title = product.itemName,
                subtitle = product.category,
                canNavigateBack = true,
                onNavigateBack = onNavigateBack,
                cartBadgeCount = cartCount,
                onCartClick = onOpenCart,
                actions = {
                    IconButton(
                        onClick = onToggleWishlist,
                        modifier = Modifier.testTag("detail_wishlist_btn")
                    ) {
                        Icon(
                            imageVector = if (isWishlisted) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = if (isWishlisted) "Remove from wishlist" else "Add to wishlist",
                            tint = if (isWishlisted) StatusCancelledText else MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            )
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Total Price",
                            style = MaterialTheme.typography.labelMedium.copy(color = TextMuted)
                        )
                        Text(
                            text = "$${String.format("%.2f", product.pricePerUnit * quantity)}",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = HarvestForestGreen
                            )
                        )
                    }

                    HarvestButton(
                        text = if (isOutOfStock) "Out of Stock" else "Add to Basket",
                        onClick = {
                            if (!isOutOfStock) {
                                onAddToCart(quantity)
                            }
                        },
                        enabled = !isOutOfStock,
                        icon = Icons.Default.AddShoppingCart,
                        modifier = Modifier.width(180.dp),
                        testTag = "btn_add_to_cart_detail"
                    )
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Hero Product Visual Showcase Box
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = when (product.category) {
                        "Vegetables" -> Color(0xFFE8F5E9)
                        "Fruits" -> Color(0xFFFFF3E0)
                        "Dairy" -> Color(0xFFE1F5FE)
                        "Grains/Pulses" -> Color(0xFFFFF8E1)
                        "Herbs/Spices" -> Color(0xFFF1F8E9)
                        else -> Color(0xFFEFEBE9)
                    }
                )
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = getCategoryIcon(product.category),
                            contentDescription = null,
                            tint = HarvestForestGreen,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = product.category.uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = HarvestForestDark
                            )
                        )
                    }

                    if (product.isOrganic) {
                        Surface(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(14.dp),
                            color = HarvestForestGreen,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "CERTIFIED ORGANIC",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Title and Pricing
            Text(
                text = product.itemName,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = "$${String.format("%.2f", product.pricePerUnit)}",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = HarvestForestGreen
                    )
                )
                Text(
                    text = " / ${product.unit}",
                    style = MaterialTheme.typography.titleMedium.copy(color = TextSecondary),
                    modifier = Modifier.padding(bottom = 2.dp, start = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Farmer & Market card (clickable to Farmer Profile C14)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onViewFarmerProfile(product.farmerId) }
                    .border(1.dp, BorderLight, RoundedCornerShape(16.dp))
                    .testTag("link_farmer_profile"),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFFECB3)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Agriculture,
                            contentDescription = null,
                            tint = Color(0xFFE65100),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = product.farmerName,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Market: ${product.marketName}",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                        )
                    }
                    OutlinedButton(
                        onClick = { onViewFarmerProfile(product.farmerId) },
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text("View Farm", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Stock Availability and Quantity Counter
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Availability",
                        style = MaterialTheme.typography.labelMedium.copy(color = TextMuted)
                    )
                    Text(
                        text = if (isOutOfStock) "Out of Stock" else "$maxStock ${product.unit} in stock",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isOutOfStock) StatusCancelledText else HarvestForestGreen
                        )
                    )
                }

                if (!isOutOfStock) {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { if (quantity > 1) quantity-- },
                                enabled = quantity > 1
                            ) {
                                Icon(Icons.Default.Remove, contentDescription = "Decrease")
                            }
                            Text(
                                text = "$quantity ${product.unit}",
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                            IconButton(
                                onClick = { if (quantity < maxStock) quantity++ },
                                enabled = quantity < maxStock
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Increase")
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Description
            Text(
                text = "Product Details",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = product.description.ifEmpty { "Locally cultivated crop harvested directly from farm pastures without chemical additives." },
                style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Safe Farm Promise
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = HarvestMint)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.VerifiedUser,
                        contentDescription = null,
                        tint = HarvestForestGreen
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "HarvestHub Verified Fresh: Picked within 24 hours of market dispatch.",
                        style = MaterialTheme.typography.bodySmall.copy(color = HarvestForestDark)
                    )
                }
            }
        }
    }
}
