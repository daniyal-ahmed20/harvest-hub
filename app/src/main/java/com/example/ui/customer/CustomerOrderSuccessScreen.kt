package com.example.ui.customer

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.HarvestButton
import com.example.ui.components.HarvestCrate3DVisual
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

@Composable
fun CustomerOrderSuccessScreen(
    orderIds: List<String>,
    onViewOrderHistory: () -> Unit,
    onContinueShopping: () -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // 3D Isometric Wooden Crate with Floating Harvest Produce
            HarvestCrate3DVisual(
                modifier = Modifier
                    .size(140.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Order Placed Successfully!",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center,
                color = HarvestForestGreen
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Your simulated order has been recorded and transmitted to the farmers.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Order ID Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "ORDER REFERENCE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextMuted
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = orderIds.joinToString(", "),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = HarvestForestDark
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    StatusBadge(status = "Pending")
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Local farmers have received notification and are preparing your fresh basket.",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            HarvestButton(
                text = "View in Order History",
                onClick = onViewOrderHistory,
                modifier = Modifier.fillMaxWidth(),
                icon = Icons.Default.ShoppingBag,
                testTag = "btn_success_view_history"
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onContinueShopping,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("btn_continue_shopping"),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Continue Shopping", fontWeight = FontWeight.SemiBold, color = HarvestForestGreen)
            }
        }
    }
}
