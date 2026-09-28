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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.OrderEntity
import com.example.data.model.ProductEntity
import com.example.ui.components.HarvestTopBar
import com.example.ui.theme.*

@Composable
fun FarmerReportsScreen(
    orders: List<OrderEntity>,
    products: List<ProductEntity>,
    onNavigateBack: () -> Unit
) {
    var selectedPeriod by remember { mutableStateOf("Weekly") }
    val validOrders = orders.filter { it.status != "Cancelled" }

    val totalRevenue = validOrders.sumOf { it.totalPrice }
    val totalUnitsSold = validOrders.size * 2 // estimated from order lines
    val avgOrderValue = if (validOrders.isNotEmpty()) totalRevenue / validOrders.size else 0.0

    Scaffold(
        topBar = {
            HarvestTopBar(
                title = "Farm Sales Reports",
                subtitle = "Revenue & harvest analytics",
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Period Selector Tabs
            item {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp)
                    ) {
                        listOf("Daily", "Weekly", "Monthly").forEach { period ->
                            val isSelected = selectedPeriod == period
                            Surface(
                                modifier = Modifier.weight(1f),
                                color = if (isSelected) HarvestForestGreen else Color.Transparent,
                                shape = RoundedCornerShape(8.dp),
                                onClick = { selectedPeriod = period }
                            ) {
                                Text(
                                    text = period,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }

            // Summary Metric Cards
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("Total Revenue", style = MaterialTheme.typography.labelSmall.copy(color = TextMuted))
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "$${String.format("%.2f", totalRevenue)}",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = HarvestForestGreen
                                )
                            )
                            Text("$selectedPeriod calculation", style = MaterialTheme.typography.labelSmall.copy(color = TextMuted))
                        }
                    }

                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("Orders Fulfilled", style = MaterialTheme.typography.labelSmall.copy(color = TextMuted))
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "${validOrders.size}",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = HarvestClay
                                )
                            )
                            Text("Avg: $${String.format("%.2f", avgOrderValue)}/order", style = MaterialTheme.typography.labelSmall.copy(color = TextMuted))
                        }
                    }
                }
            }

            // Crop Performance Table
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Crop Performance & Stock Status",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        products.forEach { prod ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(prod.itemName, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                    Text(
                                        "${prod.category} • In Stock: ${prod.stockQty} ${prod.unit}",
                                        fontSize = 12.sp,
                                        color = TextMuted
                                    )
                                }
                                Text(
                                    text = "$${String.format("%.2f", prod.pricePerUnit)} / ${prod.unit}",
                                    fontWeight = FontWeight.Bold,
                                    color = HarvestForestGreen,
                                    fontSize = 13.sp
                                )
                            }
                            HorizontalDivider(color = BorderLight.copy(alpha = 0.5f))
                        }
                    }
                }
            }
        }
    }
}
