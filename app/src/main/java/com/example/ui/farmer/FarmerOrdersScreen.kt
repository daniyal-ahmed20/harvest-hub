package com.example.ui.farmer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.OrderEntity
import com.example.ui.components.EmptyStateView
import com.example.ui.components.HarvestTopBar
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun FarmerOrdersScreen(
    orders: List<OrderEntity>,
    onNavigateBack: () -> Unit,
    onUpdateStatus: (String, String) -> Unit
) {
    val statuses = listOf("Pending", "Confirmed", "Ready for Pickup", "Completed", "Cancelled")

    Scaffold(
        topBar = {
            HarvestTopBar(
                title = "Customer Orders",
                subtitle = "${orders.size} orders received",
                canNavigateBack = true,
                onNavigateBack = onNavigateBack
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        if (orders.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                EmptyStateView(
                    title = "No customer orders yet",
                    subtitle = "When customers purchase your listed produce, their orders will show up here.",
                    icon = Icons.Outlined.ShoppingBag
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(orders) { order ->
                    val dateFormat = SimpleDateFormat("MMM dd • hh:mm a", Locale.getDefault())
                    val formattedDate = dateFormat.format(Date(order.createdAt))

                    val itemStrings = remember(order.itemsJson) {
                        val list = mutableListOf<String>()
                        try {
                            val array = JSONArray(order.itemsJson)
                            for (i in 0 until array.length()) {
                                val obj = array.getJSONObject(i)
                                val name = obj.optString("itemName")
                                val qty = obj.optInt("quantity")
                                val unit = obj.optString("unit")
                                list.add("• $qty $unit × $name")
                            }
                        } catch (_: Exception) {}
                        list
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("farmer_order_${order.orderId}"),
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
                                Column {
                                    Text(
                                        text = "Order #${order.orderId}",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Text(
                                        text = "Placed $formattedDate",
                                        style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                                    )
                                }
                                StatusBadge(status = order.status)
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "Customer: ${order.customerName} (${order.customerPhone})",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                            )
                            Text(
                                text = "Delivery Address: ${order.customerAddress}",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                            )
                            Text(
                                text = "Pickup Slot: ${order.pickupSlotTime}",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Items breakdown
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    itemStrings.forEach { itemText ->
                                        Text(
                                            text = itemText,
                                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Order Total: $${String.format("%.2f", order.totalPrice)}",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = HarvestForestGreen
                                    )
                                )

                                // Status Action Dropdown
                                var showMenu by remember { mutableStateOf(false) }
                                Box {
                                    OutlinedButton(
                                        onClick = { showMenu = true },
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Text("Update Status", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                    }

                                    DropdownMenu(
                                        expanded = showMenu,
                                        onDismissRequest = { showMenu = false }
                                    ) {
                                        statuses.forEach { s ->
                                            DropdownMenuItem(
                                                text = { Text(s) },
                                                onClick = {
                                                    onUpdateStatus(order.orderId, s)
                                                    showMenu = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
