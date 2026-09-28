package com.example.ui.customer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.components.HarvestButton
import com.example.ui.components.HarvestTopBar
import com.example.ui.theme.*

@Composable
fun CustomerPickupScreen(
    onNavigateBack: () -> Unit
) {
    var selectedSlotIndex by remember { mutableStateOf(0) }
    var isSaved by remember { mutableStateOf(false) }

    val slots = listOf(
        "Central Market Stall • Today 4:00 PM - 6:00 PM",
        "Green Valley Farm Gate • Tomorrow 10:00 AM - 12:00 PM",
        "Metro Green Hub • Tomorrow 2:00 PM - 4:00 PM",
        "Weekend Agri Bazaar • Saturday 9:00 AM - 1:00 PM"
    )

    Scaffold(
        topBar = {
            HarvestTopBar(
                title = "Pickup Slots",
                subtitle = "Select market collection time",
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Available Farm-Gate & Market Slots",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

            slots.forEachIndexed { index, slot ->
                val isSelected = selectedSlotIndex == index
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { selectedSlotIndex = index; isSaved = false },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) HarvestMint else MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = { selectedSlotIndex = index; isSaved = false },
                            colors = RadioButtonDefaults.colors(selectedColor = HarvestForestGreen)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = slot,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            )
                        }
                    }
                }
            }

            if (isSaved) {
                Surface(
                    color = HarvestMint,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = HarvestForestGreen)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Pickup slot preference updated for your upcoming orders.",
                            style = MaterialTheme.typography.bodySmall.copy(color = HarvestForestDark)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            HarvestButton(
                text = "Save Pickup Slot",
                onClick = { isSaved = true },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
