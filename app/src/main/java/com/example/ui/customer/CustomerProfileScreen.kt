package com.example.ui.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.UserEntity
import com.example.ui.components.HarvestTopBar
import com.example.ui.theme.*

@Composable
fun CustomerProfileScreen(
    user: UserEntity?,
    onNavigateBack: () -> Unit,
    onEditProfile: () -> Unit,
    onViewOrders: () -> Unit,
    onViewWishlist: () -> Unit,
    onViewFollowedFarmers: () -> Unit,
    onViewPickupSlots: () -> Unit,
    onViewAboutContact: () -> Unit,
    onOpenSettings: () -> Unit,
    onLogout: () -> Unit
) {
    Scaffold(
        topBar = {
            HarvestTopBar(
                title = "My Account",
                subtitle = user?.role ?: "Customer",
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Profile Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(HarvestMint),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = HarvestForestGreen,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = user?.name ?: "Customer",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = user?.email ?: "",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                        )
                        Text(
                            text = user?.phone?.ifBlank { "No phone registered" } ?: "",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                        )
                    }
                    IconButton(
                        onClick = onEditProfile,
                        modifier = Modifier.testTag("btn_edit_profile")
                    ) {
                        Icon(Icons.Outlined.Edit, contentDescription = "Edit Profile", tint = HarvestForestGreen)
                    }
                }
            }

            // Quick Actions Group
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column {
                    ProfileMenuRow("Order History", "Track past and current simulated orders", Icons.Outlined.ShoppingBag, onViewOrders)
                    HorizontalDivider(color = BorderLight)
                    ProfileMenuRow("Saved Wishlist", "View saved crops and farm goods", Icons.Outlined.FavoriteBorder, onViewWishlist)
                    HorizontalDivider(color = BorderLight)
                    ProfileMenuRow("Followed Producers", "Farmers you follow and support", Icons.Outlined.Agriculture, onViewFollowedFarmers)
                    HorizontalDivider(color = BorderLight)
                    ProfileMenuRow("Pickup Slots", "Manage default collection preferences", Icons.Outlined.Schedule, onViewPickupSlots)
                }
            }

            // Information & Settings
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column {
                    ProfileMenuRow("Settings & Theme", "Appearance, display and preferences", Icons.Outlined.Settings, onOpenSettings)
                    HorizontalDivider(color = BorderLight)
                    ProfileMenuRow("About HarvestHub & Contact", "Mission, admin contacts, feedback form", Icons.Outlined.Info, onViewAboutContact)
                }
            }

            // Sign Out
            OutlinedButton(
                onClick = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("btn_profile_logout"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusCancelledText)
            ) {
                Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Log out of HarvestHub", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ProfileMenuRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
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
