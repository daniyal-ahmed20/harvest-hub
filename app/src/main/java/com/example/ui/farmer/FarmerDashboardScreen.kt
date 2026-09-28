package com.example.ui.farmer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FarmerEntity
import com.example.data.model.OrderEntity
import com.example.data.model.ProductEntity
import com.example.ui.components.HarvestButton
import com.example.ui.components.HarvestTopBar
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

@Composable
fun FarmerDashboardScreen(
    farmer: FarmerEntity?,
    products: List<ProductEntity>,
    orders: List<OrderEntity>,
    onAddProduct: () -> Unit,
    onViewAllProducts: () -> Unit,
    onViewInventory: () -> Unit,
    onViewOrders: () -> Unit,
    onViewReports: () -> Unit,
    onViewProfile: () -> Unit,
    onLogout: () -> Unit
) {
    val activeProducts = products.count { it.isActive }
    val lowStockCount = products.count { it.stockQty in 1..10 }
    val outOfStockCount = products.count { it.stockQty <= 0 }
    val pendingOrders = orders.count { it.status == "Pending" }
    val totalRevenue = orders.filter { it.status != "Cancelled" }.sumOf { it.totalPrice }

    Scaffold(
        topBar = {
            HarvestTopBar(
                title = farmer?.businessName ?: "Farmer Portal",
                subtitle = "Agricultural Producer Dashboard",
                canNavigateBack = false,
                actions = {
                    IconButton(
                        onClick = onViewProfile,
                        modifier = Modifier.testTag("btn_farmer_profile")
                    ) {
                        Icon(Icons.Outlined.AccountCircle, contentDescription = "Profile")
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
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Welcome Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Harvest Overview",
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Direct farm sales & inventory management",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                        )
                    }

                    Button(
                        onClick = onAddProduct,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = HarvestForestGreen),
                        modifier = Modifier.testTag("btn_quick_add_product")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Crop", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // KPI Cards Grid (Active Products, Pending Orders, Low Stock, Revenue)
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        KpiCard(
                            title = "Active Crops",
                            value = "$activeProducts",
                            subtitle = "in market catalog",
                            icon = Icons.Outlined.Eco,
                            color = HarvestForestGreen,
                            modifier = Modifier.weight(1f),
                            onClick = onViewAllProducts
                        )
                        KpiCard(
                            title = "Pending Orders",
                            value = "$pendingOrders",
                            subtitle = "needs fulfillment",
                            icon = Icons.Outlined.PendingActions,
                            color = if (pendingOrders > 0) HarvestClay else HarvestForestLight,
                            modifier = Modifier.weight(1f),
                            onClick = onViewOrders
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        KpiCard(
                            title = "Low / Out of Stock",
                            value = "${lowStockCount + outOfStockCount}",
                            subtitle = "$outOfStockCount zero stock",
                            icon = Icons.Outlined.WarningAmber,
                            color = if (outOfStockCount > 0) StatusCancelledText else Color(0xFFE65100),
                            modifier = Modifier.weight(1f),
                            onClick = onViewInventory
                        )
                        KpiCard(
                            title = "Total Sales",
                            value = "$${String.format("%.2f", totalRevenue)}",
                            subtitle = "${orders.size} orders received",
                            icon = Icons.Outlined.MonetizationOn,
                            color = HarvestForestGreen,
                            modifier = Modifier.weight(1f),
                            onClick = onViewReports
                        )
                    }
                }
            }

            // Quick Nav Shortcuts
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onViewInventory,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Inventory", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    OutlinedButton(
                        onClick = onViewOrders,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Orders (${orders.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    OutlinedButton(
                        onClick = onViewReports,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Reports", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Recent Orders Section
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent Incoming Orders",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    TextButton(onClick = onViewOrders) {
                        Text("View All", color = HarvestForestGreen, fontSize = 12.sp)
                    }
                }
            }

            if (orders.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Text(
                            text = "No customer orders received yet.",
                            style = MaterialTheme.typography.bodyMedium.copy(color = TextMuted),
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            } else {
                items(orders.take(3)) { order ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Order #${order.orderId}",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "Customer: ${order.customerName}",
                                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                                )
                                Text(
                                    text = "$${String.format("%.2f", order.totalPrice)} • ${order.pickupSlotTime}",
                                    style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
                                )
                            }
                            StatusBadge(status = order.status)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun KpiCard(
    title: String,
    value: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = TextSecondary
                    )
                )
                Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = color
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
            )
        }
    }
}
