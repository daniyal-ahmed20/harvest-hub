package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val userId: String,
    val name: String,
    val email: String,
    val role: String, // "Customer", "Farmer", "Administrator"
    val phone: String = "",
    val address: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val isActive: Boolean = true
)

@Entity(tableName = "farmers")
data class FarmerEntity(
    @PrimaryKey val farmerId: String,
    val userId: String,
    val marketId: String,
    val businessName: String,
    val description: String,
    val rating: Float = 4.8f,
    val contactPhone: String = "",
    val location: String = "Local Valley",
    val isActive: Boolean = true
)

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val productId: String,
    val farmerId: String,
    val farmerName: String,
    val itemName: String,
    val category: String, // "Fruits", "Vegetables", "Grains/Pulses", "Dairy", "Herbs/Spices", "Organic Products"
    val pricePerUnit: Double,
    val stockQty: Int,
    val unit: String = "kg", // "kg", "dozen", "bunch", "pack", "liter"
    val imageUrl: String = "",
    val description: String = "",
    val isActive: Boolean = true,
    val isOrganic: Boolean = true,
    val marketName: String = "Central Farmers Market"
)

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey val orderId: String,
    val customerId: String,
    val customerName: String,
    val customerPhone: String,
    val customerAddress: String,
    val farmerId: String,
    val farmerName: String,
    val itemsJson: String, // JSON list of CartItem
    val pickupSlotTime: String = "Tomorrow 10:00 AM - 12:00 PM",
    val status: String = "Pending", // "Pending", "Confirmed", "Ready for Pickup", "Completed", "Cancelled"
    val totalPrice: Double,
    val createdAt: Long = System.currentTimeMillis(),
    val notes: String = ""
)

@Entity(tableName = "markets")
data class MarketEntity(
    @PrimaryKey val marketId: String,
    val marketName: String,
    val address: String,
    val gpsCoordinates: String,
    val operatingHours: String,
    val activeStatus: Boolean = true
)

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey val categoryId: String,
    val name: String,
    val description: String,
    val iconName: String,
    val isActive: Boolean = true
)

@Entity(tableName = "wishlist")
data class WishlistEntity(
    @PrimaryKey val id: String,
    val customerId: String,
    val productId: String,
    val addedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "followed_farmers")
data class FollowedFarmerEntity(
    @PrimaryKey val id: String,
    val customerId: String,
    val farmerId: String,
    val followedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey val id: String,
    val recipientUserId: String,
    val title: String,
    val message: String,
    val type: String, // "order", "restock", "pickup", "system"
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)

@Entity(tableName = "audit_logs")
data class AuditLogEntity(
    @PrimaryKey val id: String,
    val action: String,
    val performedBy: String,
    val details: String,
    val timestamp: Long = System.currentTimeMillis()
)

// UI and Business Models
data class CartItem(
    val productId: String,
    val itemName: String,
    val farmerId: String,
    val farmerName: String,
    val pricePerUnit: Double,
    val unit: String,
    val quantity: Int,
    val maxStock: Int,
    val imageUrl: String = ""
) {
    val subtotal: Double get() = pricePerUnit * quantity
}

data class AssistantMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val isFromUser: Boolean,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val suggestedAction: String? = null
)
