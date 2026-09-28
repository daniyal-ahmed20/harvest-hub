package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.*
import com.example.data.seed.SeedData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        FarmerEntity::class,
        ProductEntity::class,
        OrderEntity::class,
        MarketEntity::class,
        CategoryEntity::class,
        WishlistEntity::class,
        FollowedFarmerEntity::class,
        NotificationEntity::class,
        AuditLogEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class HarvestHubDatabase : RoomDatabase() {
    abstract fun dao(): HarvestHubDao

    companion object {
        @Volatile
        private var INSTANCE: HarvestHubDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): HarvestHubDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HarvestHubDatabase::class.java,
                    "harvesthub_database"
                )
                    .addCallback(HarvestHubDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class HarvestHubDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateDatabase(database.dao())
                }
            }
        }

        suspend fun populateDatabase(dao: HarvestHubDao) {
            // Pre-seed users
            SeedData.defaultUsers.forEach { dao.insertUser(it) }
            // Pre-seed markets
            SeedData.defaultMarkets.forEach { dao.insertMarket(it) }
            // Pre-seed farmers
            SeedData.defaultFarmers.forEach { dao.insertFarmer(it) }
            // Pre-seed categories
            SeedData.defaultCategories.forEach { dao.insertCategory(it) }
            // Pre-seed products
            SeedData.defaultProducts.forEach { dao.insertProduct(it) }
            // Pre-seed orders
            SeedData.defaultOrders.forEach { dao.insertOrder(it) }
            // Pre-seed notifications
            SeedData.defaultNotifications.forEach { dao.insertNotification(it) }
            // Pre-seed audit logs
            SeedData.defaultAuditLogs.forEach { dao.insertAuditLog(it) }
        }
    }
}
