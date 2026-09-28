package com.example.ui.admin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
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
import com.example.data.model.*
import com.example.ui.components.HarvestTopBar
import com.example.ui.farmer.KpiCard
import com.example.ui.theme.*

@Composable
fun AdminDashboardScreen(
    customers: List<UserEntity>,
    farmers: List<FarmerEntity>,
    products: List<ProductEntity>,
    orders: List<OrderEntity>,
    auditLogs: List<AuditLogEntity>,
    firebaseStatusText: String = "Cloud Ready",
    onSyncFirebase: () -> Unit = {},
    onViewCustomers: () -> Unit,
    onViewFarmers: () -> Unit,
    onViewProducts: () -> Unit,
    onViewCategories: () -> Unit,
    onViewOrders: () -> Unit,
    onViewReports: () -> Unit,
    onViewAuditLogs: () -> Unit,
    onLogout: () -> Unit
) {
    val totalRevenue = orders.filter { it.status != "Cancelled" }.sumOf { it.totalPrice }
    val activeProducts = products.count { it.isActive }
    var isSyncing by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            HarvestTopBar(
                title = "HarvestHub Master Console",
                subtitle = "Administrator Platform Overview",
                canNavigateBack = false,
                actions = {
                    IconButton(
                        onClick = onLogout,
                        modifier = Modifier.testTag("btn_admin_logout_top")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Log out", tint = StatusCancelledText)
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
            // Platform KPI Grid
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        KpiCard(
                            title = "Platform Sales",
                            value = "$${String.format("%.2f", totalRevenue)}",
                            subtitle = "Simulated gross orders",
                            icon = Icons.Outlined.MonetizationOn,
                            color = HarvestForestGreen,
                            modifier = Modifier.weight(1f),
                            onClick = onViewReports
                        )
                        KpiCard(
                            title = "Total Orders",
                            value = "${orders.size}",
                            subtitle = "${orders.count { it.status == "Completed" }} completed",
                            icon = Icons.Outlined.ReceiptLong,
                            color = HarvestClay,
                            modifier = Modifier.weight(1f),
                            onClick = onViewOrders
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        KpiCard(
                            title = "Active Farmers",
                            value = "${farmers.size}",
                            subtitle = "Verified producers",
                            icon = Icons.Outlined.Agriculture,
                            color = Color(0xFFE65100),
                            modifier = Modifier.weight(1f),
                            onClick = onViewFarmers
                        )
                        KpiCard(
                            title = "Customers",
                            value = "${customers.size}",
                            subtitle = "Registered accounts",
                            icon = Icons.Outlined.People,
                            color = Color(0xFF0277BD),
                            modifier = Modifier.weight(1f),
                            onClick = onViewCustomers
                        )
                    }
                }
            }

            // Firebase Cloud Firestore Engine Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = HarvestMint)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CloudQueue,
                                    contentDescription = null,
                                    tint = HarvestForestGreen,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Firebase Cloud Firestore",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = HarvestForestDark
                                    )
                                )
                            }

                            Surface(
                                color = HarvestForestGreen,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = firebaseStatusText.uppercase(),
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Dual-layer persistence: Room SQLite Local Cache + Cloud Firestore real-time sync for collections [products, orders, farmers, users].",
                            style = MaterialTheme.typography.bodySmall.copy(color = HarvestForestDark)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                onSyncFirebase()
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = HarvestForestGreen),
                            modifier = Modifier.fillMaxWidth().height(42.dp)
                        ) {
                            Icon(Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Trigger Cloud Sync Batch", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Platform Management Hub Actions
            item {
                Text(
                    text = "Platform Management Modules",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column {
                        AdminNavRow("Customer Accounts", "${customers.size} buyers registered", Icons.Outlined.People, onViewCustomers)
                        HorizontalDivider(color = BorderLight)
                        AdminNavRow("Farmers & Producers", "${farmers.size} local farms verified", Icons.Outlined.Agriculture, onViewFarmers)
                        HorizontalDivider(color = BorderLight)
                        AdminNavRow("Product Moderation", "$activeProducts of ${products.size} active crops", Icons.Outlined.Inventory2, onViewProducts)
                        HorizontalDivider(color = BorderLight)
                        AdminNavRow("Category Directory", "6 SRS categories configured", Icons.Outlined.Category, onViewCategories)
                        HorizontalDivider(color = BorderLight)
                        AdminNavRow("Platform Orders", "${orders.size} customer orders logged", Icons.Outlined.ShoppingBag, onViewOrders)
                        HorizontalDivider(color = BorderLight)
                        AdminNavRow("Analytics & Reports", "Daily, weekly and platform totals", Icons.Outlined.BarChart, onViewReports)
                        HorizontalDivider(color = BorderLight)
                        AdminNavRow("Security & Audit Logs", "${auditLogs.size} operational events tracked", Icons.Outlined.Security, onViewAuditLogs)
                    }
                }
            }

            // Recent System Audit Log
            item {
                Text(
                    text = "Recent Platform Activity",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }

            items(auditLogs.take(4)) { log ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = log.action,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = HarvestForestGreen
                                )
                            )
                            Text(
                                text = log.performedBy,
                                style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = log.details,
                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                        )
                    }
                }
            }

            item {
                OutlinedButton(
                    onClick = onLogout,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("btn_admin_logout_bottom"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusCancelledText)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Exit Admin Console", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun AdminNavRow(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = HarvestForestGreen, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
            Text(subtitle, style = MaterialTheme.typography.bodySmall.copy(color = TextMuted))
        }
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextMuted)
    }
}
