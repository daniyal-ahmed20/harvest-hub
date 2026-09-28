package com.example.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.OrderEntity
import com.example.ui.components.HarvestTopBar
import com.example.ui.theme.*

@Composable
fun AdminReportsScreen(
    orders: List<OrderEntity>,
    onNavigateBack: () -> Unit
) {
    val totalRevenue = orders.filter { it.status != "Cancelled" }.sumOf { it.totalPrice }
    val completedOrders = orders.count { it.status == "Completed" }
    val pendingOrders = orders.count { it.status == "Pending" }
    val confirmedOrders = orders.count { it.status == "Confirmed" || it.status == "Ready for Pickup" }

    Scaffold(
        topBar = {
            HarvestTopBar(
                title = "Platform Analytics",
                subtitle = "EGreen Basket marketplace totals",
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
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Platform Financial Summary",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "$${String.format("%.2f", totalRevenue)}",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = HarvestForestGreen
                            )
                        )
                        Text(
                            text = "Total Gross Farm Produce Sales (Simulated Orders)",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                        )
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Fulfillment Pipeline",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Pending Fulfillments")
                            Text("$pendingOrders", fontWeight = FontWeight.Bold, color = HarvestClay)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Confirmed / Ready for Pickup")
                            Text("$confirmedOrders", fontWeight = FontWeight.Bold, color = HarvestForestGreen)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Completed Orders")
                            Text("$completedOrders", fontWeight = FontWeight.Bold, color = HarvestForestGreen)
                        }
                    }
                }
            }
        }
    }
}
