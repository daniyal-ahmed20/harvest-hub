package com.example.ui.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import com.example.data.model.CartItem
import com.example.data.model.UserEntity
import com.example.ui.components.HarvestButton
import com.example.ui.components.HarvestTopBar
import com.example.ui.theme.*

@Composable
fun CustomerCheckoutScreen(
    customer: UserEntity?,
    cartItems: List<CartItem>,
    onNavigateBack: () -> Unit,
    onConfirmOrder: (String, String, (Boolean, List<String>?, String?) -> Unit) -> Unit,
    onOrderSuccess: (List<String>) -> Unit
) {
    var selectedSlot by remember { mutableStateOf("Tomorrow, 10:00 AM - 12:00 PM") }
    var notes by remember { mutableStateOf("") }
    var isPlacingOrder by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val subtotal = cartItems.sumOf { it.subtotal }
    val simulatedMarketFee = 1.50
    val grandTotal = subtotal + simulatedMarketFee

    val pickupSlots = listOf(
        "Today, 4:00 PM - 6:00 PM (Direct Market Pickup)",
        "Tomorrow, 10:00 AM - 12:00 PM (Morning Harvest Pickup)",
        "Tomorrow, 2:00 PM - 4:00 PM (Afternoon Pickup)",
        "Saturday, 8:00 AM - 11:00 AM (Weekend Farmers Market Stall)"
    )

    Scaffold(
        topBar = {
            HarvestTopBar(
                title = "Checkout Review",
                subtitle = "Confirm farm order details",
                canNavigateBack = true,
                onNavigateBack = onNavigateBack
            )
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Grand Total",
                            style = MaterialTheme.typography.titleMedium.copy(color = TextSecondary)
                        )
                        Text(
                            text = "$${String.format("%.2f", grandTotal)}",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = HarvestForestGreen
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    HarvestButton(
                        text = "Confirm Order (Simulated)",
                        onClick = {
                            if (cartItems.isEmpty()) {
                                errorMessage = "Your basket is empty."
                                return@HarvestButton
                            }
                            isPlacingOrder = true
                            errorMessage = null
                            onConfirmOrder(selectedSlot, notes) { success, orderIds, error ->
                                isPlacingOrder = false
                                if (success && orderIds != null) {
                                    onOrderSuccess(orderIds)
                                } else {
                                    errorMessage = error ?: "Order confirmation failed."
                                }
                            }
                        },
                        isLoading = isPlacingOrder,
                        loadingText = "Placing your order...",
                        modifier = Modifier.fillMaxWidth(),
                        icon = Icons.Default.CheckCircle,
                        testTag = "btn_confirm_order"
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // SRS Simulated Payment Note Banner
            Surface(
                color = Color(0xFFFFF8E1),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFE082))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Payment,
                        contentDescription = null,
                        tint = Color(0xFFE65100)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Simulated Order Payment",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFE65100)
                            )
                        )
                        Text(
                            text = "As specified in the SRS, payments are simulated. No real credit card charge will occur.",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextPrimary)
                        )
                    }
                }
            }

            if (errorMessage != null) {
                Surface(
                    color = StatusCancelledBg,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = errorMessage ?: "",
                        color = StatusCancelledText,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            // Customer Contact & Delivery Info
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.PersonPinCircle, contentDescription = null, tint = HarvestForestGreen)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Delivery & Customer Contact",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = customer?.name ?: "Customer",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Text(
                        text = "Phone: ${customer?.phone?.ifBlank { "+1 (555) 000-0000" }}",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                    )
                    Text(
                        text = "Address: ${customer?.address?.ifBlank { "Local Farm Hub Address" }}",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                    )
                }
            }

            // Pickup / Delivery Slot Selection
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Schedule, contentDescription = null, tint = HarvestClay)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Select Pickup / Market Slot",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    pickupSlots.forEach { slot ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { selectedSlot = slot }
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedSlot == slot,
                                onClick = { selectedSlot = slot },
                                colors = RadioButtonDefaults.colors(selectedColor = HarvestForestGreen)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = slot,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = if (selectedSlot == slot) FontWeight.Bold else FontWeight.Normal
                                )
                            )
                        }
                    }
                }
            }

            // Order Items Summary
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Items in Order (${cartItems.size})",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    cartItems.forEach { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${item.quantity}x ${item.itemName}",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                                )
                                Text(
                                    text = item.farmerName,
                                    style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                                )
                            }
                            Text(
                                text = "$${String.format("%.2f", item.subtotal)}",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = BorderLight)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Subtotal", style = MaterialTheme.typography.bodySmall)
                        Text("$${String.format("%.2f", subtotal)}", style = MaterialTheme.typography.bodySmall)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Farmers Market Support Fee", style = MaterialTheme.typography.bodySmall)
                        Text("$${String.format("%.2f", simulatedMarketFee)}", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            // Special Delivery Notes
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Special Farm / Packing Instructions (Optional)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                minLines = 2
            )
        }
    }
}
