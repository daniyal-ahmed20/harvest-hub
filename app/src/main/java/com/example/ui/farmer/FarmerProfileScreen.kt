package com.example.ui.farmer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.FarmerEntity
import com.example.ui.components.HarvestButton
import com.example.ui.components.HarvestTopBar
import com.example.ui.theme.*

@Composable
fun FarmerProfileScreen(
    farmer: FarmerEntity?,
    onNavigateBack: () -> Unit,
    onUpdateProfile: (String, String, String, String, () -> Unit) -> Unit,
    onLogout: () -> Unit
) {
    var businessName by remember { mutableStateOf(farmer?.businessName ?: "") }
    var description by remember { mutableStateOf(farmer?.description ?: "") }
    var phone by remember { mutableStateOf(farmer?.contactPhone ?: "") }
    var location by remember { mutableStateOf(farmer?.location ?: "") }
    var isSaving by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            HarvestTopBar(
                title = "Farmer Profile",
                subtitle = "Manage farm business details",
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
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Farm Business Information",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )

                    OutlinedTextField(
                        value = businessName,
                        onValueChange = { businessName = it },
                        label = { Text("Farm / Business Name") },
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Outlined.Store, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        label = { Text("Farm Location / Region") },
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Outlined.LocationOn, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Farmer Contact Phone") },
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Outlined.Phone, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Farm Biography & Organic Practices") },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    HarvestButton(
                        text = "Save Farmer Details",
                        onClick = {
                            isSaving = true
                            onUpdateProfile(businessName, description, phone, location) {
                                isSaving = false
                                onNavigateBack()
                            }
                        },
                        isLoading = isSaving,
                        loadingText = "Saving farmer profile...",
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "btn_save_farmer_profile"
                    )
                }
            }

            // Sign out
            OutlinedButton(
                onClick = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("btn_farmer_logout"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusCancelledText)
            ) {
                Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Log out of Farmer Portal", fontWeight = FontWeight.Bold)
            }
        }
    }
}
