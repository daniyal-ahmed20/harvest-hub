package com.example.ui.farmer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ProductEntity
import com.example.ui.components.EmptyStateView
import com.example.ui.components.HarvestButton
import com.example.ui.components.HarvestTopBar
import com.example.ui.theme.*

@Composable
fun FarmerProductsScreen(
    products: List<ProductEntity>,
    onNavigateBack: () -> Unit,
    onSaveProduct: (String?, String, String, Double, Int, String, String, Boolean, () -> Unit) -> Unit,
    onDeleteProduct: (String) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var editingProduct by remember { mutableStateOf<ProductEntity?>(null) }
    var deleteCandidate by remember { mutableStateOf<ProductEntity?>(null) }

    Scaffold(
        topBar = {
            HarvestTopBar(
                title = "My Farm Products",
                subtitle = "${products.size} listed crops",
                canNavigateBack = true,
                onNavigateBack = onNavigateBack,
                actions = {
                    IconButton(
                        onClick = {
                            editingProduct = null
                            showDialog = true
                        },
                        modifier = Modifier.testTag("btn_farmer_add_product")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add Product", tint = HarvestForestGreen)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    editingProduct = null
                    showDialog = true
                },
                containerColor = HarvestForestGreen,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.testTag("fab_farmer_add_product")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Product")
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        if (products.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                EmptyStateView(
                    title = "You haven't added any products yet",
                    subtitle = "List your fresh produce so customers can order directly from your farm.",
                    actionLabel = "Add Product",
                    onAction = {
                        editingProduct = null
                        showDialog = true
                    },
                    icon = Icons.Outlined.Eco
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(products) { prod ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(HarvestMint),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Eco,
                                    contentDescription = null,
                                    tint = HarvestForestGreen,
                                    modifier = Modifier.size(28.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = prod.itemName,
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        modifier = Modifier.weight(1f, fill = false)
                                    )
                                    if (prod.isOrganic) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            color = HarvestForestLight,
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = "ORG",
                                                color = Color.White,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }

                                Text(
                                    text = "${prod.category} • $${String.format("%.2f", prod.pricePerUnit)} / ${prod.unit}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = HarvestForestGreen
                                    )
                                )

                                Text(
                                    text = if (prod.stockQty > 0) "Stock: ${prod.stockQty} ${prod.unit}" else "Out of stock",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (prod.stockQty > 0) TextMuted else StatusCancelledText,
                                        fontWeight = if (prod.stockQty > 0) FontWeight.Normal else FontWeight.Bold
                                    )
                                )
                            }

                            IconButton(
                                onClick = {
                                    editingProduct = prod
                                    showDialog = true
                                },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(Icons.Outlined.Edit, contentDescription = "Edit", tint = TextSecondary, modifier = Modifier.size(20.dp))
                            }

                            IconButton(
                                onClick = { deleteCandidate = prod },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(Icons.Outlined.Delete, contentDescription = "Delete", tint = StatusCancelledText, modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }
            }
        }
    }

    // Add / Edit Product Dialog (F06 / F07)
    if (showDialog) {
        ProductFormDialog(
            initialProduct = editingProduct,
            onDismiss = { showDialog = false },
            onSave = { name, category, price, stock, unit, desc, isOrg ->
                onSaveProduct(editingProduct?.productId, name, category, price, stock, unit, desc, isOrg) {
                    showDialog = false
                }
            }
        )
    }

    // Delete Confirmation Dialog (F08)
    if (deleteCandidate != null) {
        AlertDialog(
            onDismissRequest = { deleteCandidate = null },
            title = { Text("Remove Product?", fontWeight = FontWeight.Bold) },
            text = {
                Text("Are you sure you want to remove '${deleteCandidate?.itemName}' from the catalog? It will no longer be available to customers.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        deleteCandidate?.let { onDeleteProduct(it.productId) }
                        deleteCandidate = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StatusCancelledText)
                ) {
                    Text("Remove Product")
                }
            },
            dismissButton = {
                TextButton(onClick = { deleteCandidate = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun ProductFormDialog(
    initialProduct: ProductEntity?,
    onDismiss: () -> Unit,
    onSave: (String, String, Double, Int, String, String, Boolean) -> Unit
) {
    var name by remember { mutableStateOf(initialProduct?.itemName ?: "") }
    val categories = listOf("Fruits", "Vegetables", "Grains/Pulses", "Dairy", "Herbs/Spices", "Organic Products")
    var category by remember { mutableStateOf(initialProduct?.category ?: "Vegetables") }
    var priceText by remember { mutableStateOf(initialProduct?.pricePerUnit?.toString() ?: "") }
    var stockText by remember { mutableStateOf(initialProduct?.stockQty?.toString() ?: "") }
    val units = listOf("kg", "dozen", "bunch", "pack", "liter")
    var unit by remember { mutableStateOf(initialProduct?.unit ?: "kg") }
    var description by remember { mutableStateOf(initialProduct?.description ?: "") }
    var isOrganic by remember { mutableStateOf(initialProduct?.isOrganic ?: true) }
    var validationError by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initialProduct == null) "Add New Crop" else "Edit Product", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (validationError != null) {
                    Text(validationError ?: "", color = StatusCancelledText, fontSize = 12.sp)
                }

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Product Name *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = priceText,
                        onValueChange = { priceText = it },
                        label = { Text("Price ($) *") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = stockText,
                        onValueChange = { stockText = it },
                        label = { Text("Stock Qty *") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Category dropdown / selector
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Category", fontSize = 11.sp, color = TextMuted)
                        var expanded by remember { mutableStateOf(false) }
                        OutlinedButton(
                            onClick = { expanded = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(category, fontSize = 12.sp, maxLines = 1)
                        }
                        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            categories.forEach { cat ->
                                DropdownMenuItem(
                                    text = { Text(cat) },
                                    onClick = {
                                        category = cat
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Unit dropdown
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Unit", fontSize = 11.sp, color = TextMuted)
                        var unitExpanded by remember { mutableStateOf(false) }
                        OutlinedButton(
                            onClick = { unitExpanded = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(unit, fontSize = 12.sp)
                        }
                        DropdownMenu(expanded = unitExpanded, onDismissRequest = { unitExpanded = false }) {
                            units.forEach { u ->
                                DropdownMenuItem(
                                    text = { Text(u) },
                                    onClick = {
                                        unit = u
                                        unitExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    maxLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = isOrganic,
                        onCheckedChange = { isOrganic = it },
                        colors = CheckboxDefaults.colors(checkedColor = HarvestForestGreen)
                    )
                    Text("Certified Organic Produce", fontSize = 13.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val price = priceText.toDoubleOrNull()
                    val stock = stockText.toIntOrNull()
                    if (name.isBlank()) {
                        validationError = "Product name is required."
                        return@Button
                    }
                    if (price == null || price <= 0.0) {
                        validationError = "Enter a valid positive price."
                        return@Button
                    }
                    if (stock == null || stock < 0) {
                        validationError = "Enter a valid stock quantity."
                        return@Button
                    }
                    onSave(name, category, price, stock, unit, description, isOrganic)
                },
                colors = ButtonDefaults.buttonColors(containerColor = HarvestForestGreen)
            ) {
                Text("Save Crop")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
