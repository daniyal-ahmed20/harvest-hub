package com.example.data.seed

import com.example.data.model.*

object SeedData {
    val defaultUsers = listOf(
        UserEntity(
            userId = "cust_1",
            name = "Sarah Jenkins",
            email = "sarah.customer@example.com",
            role = "Customer",
            phone = "+1 (555) 234-5678",
            address = "742 Evergreen Terrace, Springfield",
            createdAt = System.currentTimeMillis() - 86400000L * 15
        ),
        UserEntity(
            userId = "cust_2",
            name = "David Miller",
            email = "david.m@example.com",
            role = "Customer",
            phone = "+1 (555) 345-6789",
            address = "120 Oak Lane, Riverdale",
            createdAt = System.currentTimeMillis() - 86400000L * 25
        ),
        UserEntity(
            userId = "farmer_u1",
            name = "John Dawson",
            email = "john.farmer@greenvalley.com",
            role = "Farmer",
            phone = "+1 (555) 876-5432",
            address = "Green Valley Acre 4, Hillsboro",
            createdAt = System.currentTimeMillis() - 86400000L * 60
        ),
        UserEntity(
            userId = "farmer_u2",
            name = "Maria Elena Vance",
            email = "maria.vance@sunmeadow.org",
            role = "Farmer",
            phone = "+1 (555) 987-6543",
            address = "Sun Meadow Pastoral Way, Sunridge",
            createdAt = System.currentTimeMillis() - 86400000L * 50
        ),
        UserEntity(
            userId = "admin_u1",
            name = "HarvestHub Master Admin",
            email = "admin@harvesthub.com",
            role = "Administrator",
            phone = "+1 (800) 555-FARM",
            address = "HarvestHub HQ, Suite 100",
            createdAt = System.currentTimeMillis() - 86400000L * 90
        )
    )

    val defaultMarkets = listOf(
        MarketEntity(
            marketId = "mkt_1",
            marketName = "Central Organic Farmers Market",
            address = "100 Market Square, Downtown",
            gpsCoordinates = "40.7128° N, 74.0060° W",
            operatingHours = "Wed & Sat: 7:00 AM - 2:00 PM",
            activeStatus = true
        ),
        MarketEntity(
            marketId = "mkt_2",
            marketName = "Metro Green Bazaar",
            address = "455 Greenway Blvd, Midtown",
            gpsCoordinates = "40.7306° N, 73.9352° W",
            operatingHours = "Daily: 8:00 AM - 6:00 PM",
            activeStatus = true
        ),
        MarketEntity(
            marketId = "mkt_3",
            marketName = "Valley Agricultural Fair",
            address = "88 Farm-to-Table Way, Valley Creek",
            gpsCoordinates = "40.7589° N, 73.9851° W",
            operatingHours = "Thurs - Sun: 9:00 AM - 4:00 PM",
            activeStatus = true
        )
    )

    val defaultFarmers = listOf(
        FarmerEntity(
            farmerId = "farmer_1",
            userId = "farmer_u1",
            marketId = "mkt_1",
            businessName = "Green Valley Organic Farms",
            description = "Family-owned for 3 generations. 100% certified organic seasonal heirloom produce and pesticide-free cultivation.",
            rating = 4.9f,
            contactPhone = "+1 (555) 876-5432",
            location = "Hillsboro Valley"
        ),
        FarmerEntity(
            farmerId = "farmer_2",
            userId = "farmer_u2",
            marketId = "mkt_2",
            businessName = "Sun Meadow Dairy & Grains",
            description = "Grass-fed dairy, artisanal farmstead cheeses, heirloom grains and non-GMO pulses straight from pasture to table.",
            rating = 4.8f,
            contactPhone = "+1 (555) 987-6543",
            location = "Sunridge Pastures"
        )
    )

    val defaultCategories = listOf(
        CategoryEntity("cat_fruits", "Fruits", "Sun-ripened orchard and berry fruits", "fruit"),
        CategoryEntity("cat_vegetables", "Vegetables", "Crisp leafy greens, roots and vine vegetables", "eco"),
        CategoryEntity("cat_grains", "Grains/Pulses", "Whole grains, milled pulses and heirloom rice", "grain"),
        CategoryEntity("cat_dairy", "Dairy", "Fresh pasture milk, country butter and farm cheese", "local_drink"),
        CategoryEntity("cat_herbs", "Herbs/Spices", "Aromatic culinary herbs, ground spices and seeds", "spa"),
        CategoryEntity("cat_organic", "Organic Products", "Cold-pressed honey, pure syrups and farm preserves", "inventory_2")
    )

    val defaultProducts = listOf(
        ProductEntity(
            productId = "prod_1",
            farmerId = "farmer_1",
            farmerName = "Green Valley Organic Farms",
            itemName = "Heirloom Vine-Ripened Tomatoes",
            category = "Vegetables",
            pricePerUnit = 4.50,
            stockQty = 35,
            unit = "kg",
            description = "Intensely sweet and juicy heritage tomatoes harvested this morning. Unsprayed and vine-ripened under natural sunlight.",
            isOrganic = true,
            marketName = "Central Organic Farmers Market"
        ),
        ProductEntity(
            productId = "prod_2",
            farmerId = "farmer_1",
            farmerName = "Green Valley Organic Farms",
            itemName = "Crisp Baby Spinach & Arugula Mix",
            category = "Vegetables",
            pricePerUnit = 3.20,
            stockQty = 24,
            unit = "pack",
            description = "Tender young leaves hand-picked, washed in spring water, and bagged fresh for maximum crispness and vitamin retention.",
            isOrganic = true,
            marketName = "Central Organic Farmers Market"
        ),
        ProductEntity(
            productId = "prod_3",
            farmerId = "farmer_1",
            farmerName = "Green Valley Organic Farms",
            itemName = "Honeycrisp Mountain Apples",
            category = "Fruits",
            pricePerUnit = 5.00,
            stockQty = 40,
            unit = "kg",
            description = "Extra crunchy, juicy apples with the perfect balance of tart and honey sweetness. Great for snacking or baking.",
            isOrganic = true,
            marketName = "Central Organic Farmers Market"
        ),
        ProductEntity(
            productId = "prod_4",
            farmerId = "farmer_1",
            farmerName = "Green Valley Organic Farms",
            itemName = "Fresh Garden Strawberries",
            category = "Fruits",
            pricePerUnit = 4.80,
            stockQty = 18,
            unit = "bunch",
            description = "Naturally sweet ruby strawberries. No artificial fertilizers, ripened slowly for deep farm flavor.",
            isOrganic = true,
            marketName = "Central Organic Farmers Market"
        ),
        ProductEntity(
            productId = "prod_5",
            farmerId = "farmer_2",
            farmerName = "Sun Meadow Dairy & Grains",
            itemName = "Artisanal Raw Pasture Milk",
            category = "Dairy",
            pricePerUnit = 6.00,
            stockQty = 15,
            unit = "liter",
            description = "Rich, creamy whole milk from pasture-raised Jersey cows grazing exclusively on fresh wild clover and timothy grass.",
            isOrganic = true,
            marketName = "Metro Green Bazaar"
        ),
        ProductEntity(
            productId = "prod_6",
            farmerId = "farmer_2",
            farmerName = "Sun Meadow Dairy & Grains",
            itemName = "Farmstead Cultured Salted Butter",
            category = "Dairy",
            pricePerUnit = 5.50,
            stockQty = 20,
            unit = "pack",
            description = "Small-batch slow-churned butter with a golden yellow hue and sea-salt flake finish. Traditional farm recipe.",
            isOrganic = true,
            marketName = "Metro Green Bazaar"
        ),
        ProductEntity(
            productId = "prod_7",
            farmerId = "farmer_2",
            farmerName = "Sun Meadow Dairy & Grains",
            itemName = "Organic Whole Grain Basmati",
            category = "Grains/Pulses",
            pricePerUnit = 7.50,
            stockQty = 30,
            unit = "kg",
            description = "Long grain aromatic aged brown basmati rice. High fiber, naturally stone-milled without chemical bleaches.",
            isOrganic = true,
            marketName = "Metro Green Bazaar"
        ),
        ProductEntity(
            productId = "prod_8",
            farmerId = "farmer_2",
            farmerName = "Sun Meadow Dairy & Grains",
            itemName = "Heirloom Red Split Lentils",
            category = "Grains/Pulses",
            pricePerUnit = 3.80,
            stockQty = 50,
            unit = "kg",
            description = "Nutritious quick-cooking red pulses packed with natural plant protein, iron, and rich nutty aroma.",
            isOrganic = true,
            marketName = "Metro Green Bazaar"
        ),
        ProductEntity(
            productId = "prod_9",
            farmerId = "farmer_1",
            farmerName = "Green Valley Organic Farms",
            itemName = "Wild Fresh Rosemary & Thyme",
            category = "Herbs/Spices",
            pricePerUnit = 2.50,
            stockQty = 12,
            unit = "bunch",
            description = "Fragrant Mediterranean herb bouquet cut fresh this morning. Essential for farm roasts, sauces, and herbal teas.",
            isOrganic = true,
            marketName = "Central Organic Farmers Market"
        ),
        ProductEntity(
            productId = "prod_10",
            farmerId = "farmer_1",
            farmerName = "Green Valley Organic Farms",
            itemName = "Pure Raw Wildflower Honey",
            category = "Organic Products",
            pricePerUnit = 9.50,
            stockQty = 8,
            unit = "pack",
            description = "Unfiltered, unpasteurized honey harvested from bee boxes stationed next to our blooming wildflower meadows.",
            isOrganic = true,
            marketName = "Central Organic Farmers Market"
        )
    )

    val defaultOrders = listOf(
        OrderEntity(
            orderId = "HH-92041",
            customerId = "cust_1",
            customerName = "Sarah Jenkins",
            customerPhone = "+1 (555) 234-5678",
            customerAddress = "742 Evergreen Terrace, Springfield",
            farmerId = "farmer_1",
            farmerName = "Green Valley Organic Farms",
            itemsJson = """[{"productId":"prod_1","itemName":"Heirloom Vine-Ripened Tomatoes","farmerId":"farmer_1","farmerName":"Green Valley Organic Farms","pricePerUnit":4.5,"unit":"kg","quantity":2,"maxStock":35},{"productId":"prod_3","itemName":"Honeycrisp Mountain Apples","farmerId":"farmer_1","farmerName":"Green Valley Organic Farms","pricePerUnit":5.0,"unit":"kg","quantity":1,"maxStock":40}]""",
            pickupSlotTime = "Today, 4:00 PM - 6:00 PM",
            status = "Confirmed",
            totalPrice = 14.00,
            createdAt = System.currentTimeMillis() - 86400000L * 2
        ),
        OrderEntity(
            orderId = "HH-92088",
            customerId = "cust_1",
            customerName = "Sarah Jenkins",
            customerPhone = "+1 (555) 234-5678",
            customerAddress = "742 Evergreen Terrace, Springfield",
            farmerId = "farmer_2",
            farmerName = "Sun Meadow Dairy & Grains",
            itemsJson = """[{"productId":"prod_5","itemName":"Artisanal Raw Pasture Milk","farmerId":"farmer_2","farmerName":"Sun Meadow Dairy & Grains","pricePerUnit":6.0,"unit":"liter","quantity":2,"maxStock":15}]""",
            pickupSlotTime = "Tomorrow, 10:00 AM - 12:00 PM",
            status = "Pending",
            totalPrice = 12.00,
            createdAt = System.currentTimeMillis() - 3600000L * 4
        )
    )

    val defaultNotifications = listOf(
        NotificationEntity(
            id = "notif_1",
            recipientUserId = "cust_1",
            title = "Order Confirmed!",
            message = "Farmer John from Green Valley Organic Farms has confirmed your order #HH-92041.",
            type = "order",
            timestamp = System.currentTimeMillis() - 7200000L,
            isRead = false
        ),
        NotificationEntity(
            id = "notif_2",
            recipientUserId = "cust_1",
            title = "Fresh Harvest Restock",
            message = "Fresh Garden Strawberries are back in stock at Central Farmers Market.",
            type = "restock",
            timestamp = System.currentTimeMillis() - 86400000L,
            isRead = true
        ),
        NotificationEntity(
            id = "notif_3",
            recipientUserId = "farmer_u1",
            title = "New Customer Order",
            message = "You received order #HH-92041 from Sarah Jenkins ($14.00).",
            type = "order",
            timestamp = System.currentTimeMillis() - 86400000L * 2,
            isRead = true
        )
    )

    val defaultAuditLogs = listOf(
        AuditLogEntity(
            id = "log_1",
            action = "USER_REGISTERED",
            performedBy = "sarah.customer@example.com",
            details = "Customer registered successfully with verified address.",
            timestamp = System.currentTimeMillis() - 86400000L * 15
        ),
        AuditLogEntity(
            id = "log_2",
            action = "PRODUCT_CREATED",
            performedBy = "john.farmer@greenvalley.com",
            details = "Added new product 'Heirloom Vine-Ripened Tomatoes' (35 kg in stock).",
            timestamp = System.currentTimeMillis() - 86400000L * 10
        ),
        AuditLogEntity(
            id = "log_3",
            action = "ORDER_PLACED",
            performedBy = "sarah.customer@example.com",
            details = "Order #HH-92041 created for Green Valley Organic Farms. Amount: $14.00.",
            timestamp = System.currentTimeMillis() - 86400000L * 2
        )
    )
}
