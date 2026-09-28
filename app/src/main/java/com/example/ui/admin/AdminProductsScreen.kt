package com.example.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ProductEntity
import com.example.ui.components.HarvestTopBar
import com.example.ui.theme.*

@Composable
fun AdminProductsScreen(
    products: List<ProductEntity>,
    onToggleActive: (String, Boolean) -> Unit,
    onNavigateBack: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val filtered = products.filter {
        searchQuery.isBlank() || it.itemName.contains(searchQuery, ignoreCase = true) ||
                it.farmerName.contains(searchQuery, ignoreCase = true) ||
                it.category.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            HarvestTopBar(
                title = "Product Moderation",
                subtitle = "${products.size} platform items",
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
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Filter crops, categories or farms...") },
                leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(filtered) { prod ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = prod.itemName,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "Farm: ${prod.farmerName} • ${prod.category}",
                                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                                )
                                Text(
                                    text = "$${String.format("%.2f", prod.pricePerUnit)} / ${prod.unit} • Stock: ${prod.stockQty}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = HarvestForestGreen,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = if (prod.isActive) "Active" else "Deactivated",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (prod.isActive) HarvestForestGreen else StatusCancelledText
                                )
                                Switch(
                                    checked = prod.isActive,
                                    onCheckedChange = { onToggleActive(prod.productId, prod.isActive) },
                                    colors = SwitchDefaults.colors(checkedThumbColor = HarvestForestGreen)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
