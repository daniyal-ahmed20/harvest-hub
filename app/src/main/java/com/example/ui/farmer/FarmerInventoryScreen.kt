package com.example.ui.farmer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ProductEntity
import com.example.ui.components.HarvestTopBar
import com.example.ui.theme.*

@Composable
fun FarmerInventoryScreen(
    products: List<ProductEntity>,
    onNavigateBack: () -> Unit,
    onUpdateStock: (String, Int) -> Unit
) {
    Scaffold(
        topBar = {
            HarvestTopBar(
                title = "Inventory Manager",
                subtitle = "${products.size} tracked items",
                canNavigateBack = true,
                onNavigateBack = onNavigateBack
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Surface(
                    color = HarvestMint,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Inventory, contentDescription = null, tint = HarvestForestGreen)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Instant Inventory Sync: Stock adjustments immediately update the customer store catalog and prevent overselling.",
                            style = MaterialTheme.typography.bodySmall.copy(color = HarvestForestDark)
                        )
                    }
                }
            }

            items(products) { prod ->
                val isLowStock = prod.stockQty in 1..10
                val isOutOfStock = prod.stockQty <= 0

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = prod.itemName,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "${prod.category} • $${String.format("%.2f", prod.pricePerUnit)} / ${prod.unit}",
                                    style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                                )
                            }

                            Surface(
                                color = when {
                                    isOutOfStock -> Color(0xFFFFEBEE)
                                    isLowStock -> Color(0xFFFFF3E0)
                                    else -> HarvestMint
                                },
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = when {
                                        isOutOfStock -> "OUT OF STOCK"
                                        isLowStock -> "LOW STOCK"
                                        else -> "IN STOCK"
                                    },
                                    color = when {
                                        isOutOfStock -> StatusCancelledText
                                        isLowStock -> Color(0xFFE65100)
                                        else -> HarvestForestGreen
                                    },
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Stock Controls
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Current Stock: ${prod.stockQty} ${prod.unit}",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (isOutOfStock) StatusCancelledText else MaterialTheme.colorScheme.onBackground
                                )
                            )

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedButton(
                                    onClick = { onUpdateStock(prod.productId, (prod.stockQty - 5).coerceAtLeast(0)) },
                                    enabled = prod.stockQty > 0,
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text("-5", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                                OutlinedButton(
                                    onClick = { onUpdateStock(prod.productId, (prod.stockQty - 1).coerceAtLeast(0)) },
                                    enabled = prod.stockQty > 0,
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text("-1", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                                Button(
                                    onClick = { onUpdateStock(prod.productId, prod.stockQty + 1) },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = HarvestForestGreen),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text("+1", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                                Button(
                                    onClick = { onUpdateStock(prod.productId, prod.stockQty + 5) },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = HarvestForestGreen),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text("+5", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
