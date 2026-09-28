package com.example.data.firebase

import android.content.Context
import com.example.data.model.*
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

object HarvestFirebaseManager {

    private val _connectionStatus = MutableStateFlow(FirebaseStatus.CHECKING)
    val connectionStatus: StateFlow<FirebaseStatus> = _connectionStatus.asStateFlow()

    private val _lastSyncTime = MutableStateFlow<Long?>(null)
    val lastSyncTime: StateFlow<Long?> = _lastSyncTime.asStateFlow()

    private var firestore: FirebaseFirestore? = null

    fun initialize(context: Context) {
        try {
            val apps = FirebaseApp.getApps(context)
            if (apps.isNotEmpty()) {
                firestore = FirebaseFirestore.getInstance()
                _connectionStatus.value = FirebaseStatus.CONNECTED
            } else {
                _connectionStatus.value = FirebaseStatus.OFFLINE_MODE
            }
        } catch (e: Exception) {
            _connectionStatus.value = FirebaseStatus.OFFLINE_MODE
        }
    }

    suspend fun syncProductToFirestore(product: ProductEntity): Boolean = withContext(Dispatchers.IO) {
        val db = firestore ?: return@withContext false
        try {
            val productMap = hashMapOf(
                "productId" to product.productId,
                "farmerId" to product.farmerId,
                "farmerName" to product.farmerName,
                "itemName" to product.itemName,
                "category" to product.category,
                "pricePerUnit" to product.pricePerUnit,
                "stockQty" to product.stockQty,
                "unit" to product.unit,
                "description" to product.description,
                "isActive" to product.isActive,
                "isOrganic" to product.isOrganic,
                "marketName" to product.marketName,
                "updatedAt" to System.currentTimeMillis()
            )
            db.collection("products").document(product.productId)
                .set(productMap, SetOptions.merge())
                .await()
            _lastSyncTime.value = System.currentTimeMillis()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun syncOrderToFirestore(order: OrderEntity): Boolean = withContext(Dispatchers.IO) {
        val db = firestore ?: return@withContext false
        try {
            val orderMap = hashMapOf(
                "orderId" to order.orderId,
                "customerId" to order.customerId,
                "customerName" to order.customerName,
                "customerPhone" to order.customerPhone,
                "customerAddress" to order.customerAddress,
                "farmerId" to order.farmerId,
                "farmerName" to order.farmerName,
                "itemsJson" to order.itemsJson,
                "pickupSlotTime" to order.pickupSlotTime,
                "status" to order.status,
                "totalPrice" to order.totalPrice,
                "createdAt" to order.createdAt,
                "notes" to order.notes
            )
            db.collection("orders").document(order.orderId)
                .set(orderMap, SetOptions.merge())
                .await()
            _lastSyncTime.value = System.currentTimeMillis()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun syncAllLocalData(
        products: List<ProductEntity>,
        orders: List<OrderEntity>,
        farmers: List<FarmerEntity>
    ): Pair<Int, String> = withContext(Dispatchers.IO) {
        val db = firestore ?: return@withContext Pair(0, "Firebase offline. Using local Room database.")
        var syncedCount = 0
        try {
            val batch = db.batch()

            for (p in products) {
                val ref = db.collection("products").document(p.productId)
                batch.set(ref, p)
                syncedCount++
            }

            for (o in orders) {
                val ref = db.collection("orders").document(o.orderId)
                batch.set(ref, o)
                syncedCount++
            }

            for (f in farmers) {
                val ref = db.collection("farmers").document(f.farmerId)
                batch.set(ref, f)
                syncedCount++
            }

            batch.commit().await()
            _lastSyncTime.value = System.currentTimeMillis()
            _connectionStatus.value = FirebaseStatus.CONNECTED
            Pair(syncedCount, "Successfully synchronized $syncedCount records with Cloud Firestore.")
        } catch (e: Exception) {
            Pair(syncedCount, "Sync partial or offline: ${e.message}")
        }
    }
}

enum class FirebaseStatus {
    CHECKING,
    CONNECTED,
    OFFLINE_MODE
}
