package com.example.ui.admin

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.OrderEntity
import com.example.ui.components.HarvestTopBar
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AdminOrdersScreen(
    orders: List<OrderEntity>,
    onUpdateStatus: (String, String) -> Unit,
    onNavigateBack: () -> Unit
) {
    var selectedFilter by remember { mutableStateOf("All") }
    val statuses = listOf("Pending", "Confirmed", "Ready for Pickup", "Completed", "Cancelled")

    val filtered = if (selectedFilter == "All") orders else orders.filter { it.status == selectedFilter }

    Scaffold(
        topBar = {
            HarvestTopBar(
                title = "Platform Orders",
                subtitle = "${orders.size} total orders",
                canNavigateBack = true,
                onNavigateBack = onNavigateBack
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Filter chips
            ScrollableTabRow(
                selectedTabIndex = if (selectedFilter == "All") 0 else statuses.indexOf(selectedFilter) + 1,
                edgePadding = 0.dp
            ) {
                Tab(
                    selected = selectedFilter == "All",
                    onClick = { selectedFilter = "All" },
                    text = { Text("All (${orders.size})") }
                )
                statuses.forEach { s ->
                    Tab(
                        selected = selectedFilter == s,
                        onClick = { selectedFilter = s },
                        text = { Text(s) }
                    )
                }
            }

            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(filtered) { order ->
                    val dateFormat = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault())
                    val formatted = dateFormat.format(Date(order.createdAt))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Order #${order.orderId}",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                StatusBadge(status = order.status)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "Customer: ${order.customerName} (${order.customerPhone})", style = MaterialTheme.typography.bodySmall)
                            Text(text = "Farm: ${order.farmerName}", style = MaterialTheme.typography.bodySmall.copy(color = HarvestForestGreen, fontWeight = FontWeight.SemiBold))
                            Text(text = "Time: $formatted", style = MaterialTheme.typography.labelSmall.copy(color = TextMuted))
                            Text(
                                text = "Amount: $${String.format("%.2f", order.totalPrice)}",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = HarvestForestGreen)
                            )
                        }
                    }
                }
            }
        }
    }
}
