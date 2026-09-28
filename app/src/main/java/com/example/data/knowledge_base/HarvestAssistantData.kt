package com.example.data.knowledge_base

object HarvestAssistantData {
    val suggestedQuestions = listOf(
        "Which farm fruits are highest in Vitamin C?",
        "How do I store heirloom tomatoes properly?",
        "What seasonal vegetables are best this month?",
        "What are the nutritional benefits of fresh spinach?",
        "Give me a farm-fresh crisp salad recipe.",
        "How does pasture milk differ from commercial milk?"
    )

    fun getAnswerForQuery(query: String): String {
        val q = query.lowercase().trim()
        return when {
            q.contains("vitamin c") || q.contains("citrus") -> {
                "🍓 **Vitamin C Rich Farm Fruits:**\n" +
                "• **Fresh Garden Strawberries:** One cup provides ~89 mg of Vitamin C (more than an orange!).\n" +
                "• **Valencia Oranges:** Freshly picked from orchards, packed with 70 mg of bioavailable Vitamin C.\n" +
                "• **Blackberries & Raspberries:** Great seasonal additions that retain nutrients when eaten fresh.\n\n" +
                "💡 *Farm Tip:* Consume berries within 3–4 days of purchase without pre-washing until right before eating."
            }
            q.contains("tomato") && (q.contains("store") || q.contains("storage") || q.contains("keep")) -> {
                "🍅 **Heirloom Tomato Storage Guide:**\n" +
                "• **Never refrigerate ripe tomatoes!** Cold temperatures below 55°F (13°C) break down cell walls and destroy aromatic flavor compounds.\n" +
                "• Store them **stem-side down** on a flat plate or wicker tray at comfortable room temperature.\n" +
                "• Keep away from direct harsh sun. If a tomato is slightly soft, use it within 48 hours for sauces or salads."
            }
            q.contains("seasonal") || q.contains("vegetables") || q.contains("season") -> {
                "🥬 **Current Peak Harvest Vegetables:**\n" +
                "• **Heirloom Tomatoes & Crisp Bell Peppers:** Sweet, firm, and harvested daily.\n" +
                "• **Baby Spinach & Wild Arugula:** Peak nutrient density from cool morning harvests.\n" +
                "• **Crunchy Carrots & Radishes:** Rich in beta-carotene and minerals from healthy organic soils.\n\n" +
                "🌿 Check the **Vegetables** category in HarvestHub for same-day harvest availability from local farms!"
            }
            q.contains("spinach") || q.contains("benefits") || q.contains("iron") -> {
                "🌿 **Health Benefits of Fresh Farm Spinach:**\n" +
                "• **Nutrient Powerhouse:** Loaded with Vitamin A, Vitamin K, Vitamin C, Iron, and Folate.\n" +
                "• **Eye Health:** High concentrations of lutein and zeaxanthin protect vision.\n" +
                "• **Absorption Tip:** Pair raw spinach with lemon juice or tomatoes—Vitamin C enhances non-heme iron absorption by up to 300%!"
            }
            q.contains("salad") || q.contains("recipe") -> {
                "🥗 **HarvestHub 5-Minute Farmer Salad:**\n" +
                "• **Ingredients:**\n" +
                "  - 2 cups fresh Baby Spinach & Arugula mix\n" +
                "  - 1 sliced Heirloom Tomato\n" +
                "  - 1/4 cup crumbled Farmstead Cheese\n" +
                "  - 1 tbsp extra virgin olive oil + fresh lemon juice\n" +
                "  - Pinch of coarse sea salt and cracked pepper\n" +
                "• **Method:** Toss leaves lightly with oil and lemon, top with sliced heirloom tomato and cheese. Serve immediately!"
            }
            q.contains("milk") || q.contains("dairy") || q.contains("pasture") -> {
                "🥛 **Pasture-Raised Farm Milk Benefits:**\n" +
                "• Milk from pasture-fed cows has significantly higher Omega-3 fatty acids and CLA (conjugated linoleic acid).\n" +
                "• Naturally rich in fat-soluble vitamins A, D, and K2 without synthetic hormone treatments.\n" +
                "• Keep chilled at 38°F (3°C) and consume within 7 days of harvest for optimal freshness."
            }
            q.contains("organic") -> {
                "🌱 **About HarvestHub Organic Standards:**\n" +
                "All registered HarvestHub farmers practice zero-synthetic-pesticide farming, soil rotation, and natural composting. Browse the 'Organic Products' category to view verified certifications!"
            }
            else -> {
                "🌾 **HarvestHub Assistant Answer:**\n" +
                "Thank you for asking about farm produce! For '$query', our local farmers recommend prioritizing freshness, seasonal availability, and supporting chemical-free local crops. Check our marketplace categories or consult your local farmer directly via their profile!"
            }
        }
    }
}
