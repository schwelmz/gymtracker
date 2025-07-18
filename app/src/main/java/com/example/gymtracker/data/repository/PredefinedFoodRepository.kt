package com.example.gymtracker.data.repository

import com.example.gymtracker.data.model.FoodTemplate

/**
 * This object acts as a simple data source for the foods that
 * come pre-packaged with the app. All nutritional values are approximate and per 100g.
 */
object PredefinedFoodRepository {

    fun getPredefinedFoods(): List<FoodTemplate> {
        return listOf(
            // Fruits
            FoodTemplate(
                name = "Apple",
                caloriesPer100g = 52f,
                proteinPer100g = 0f,
                carbsPer100g = 14f,
                fatPer100g = 0f
            ),
            FoodTemplate(
                name = "Avocado",
                caloriesPer100g = 160f,
                proteinPer100g = 2f,
                carbsPer100g = 9f,
                fatPer100g = 15f
            ),
            FoodTemplate(
                name = "Banana",
                caloriesPer100g = 89f,
                proteinPer100g = 1f,
                carbsPer100g = 23f,
                fatPer100g = 0f
            ),
            FoodTemplate(
                name = "Blueberries",
                caloriesPer100g = 57f,
                proteinPer100g = 1f,
                carbsPer100g = 14f,
                fatPer100g = 0f
            ),
            FoodTemplate(
                name = "Cherries",
                caloriesPer100g = 50f,
                proteinPer100g = 1f,
                carbsPer100g = 12f,
                fatPer100g = 0f
            ),
            FoodTemplate(
                name = "Grapes",
                caloriesPer100g = 69f,
                proteinPer100g = 1f,
                carbsPer100g = 18f,
                fatPer100g = 0f
            ),
            FoodTemplate(
                name = "Kiwi",
                caloriesPer100g = 61f,
                proteinPer100g = 1f,
                carbsPer100g = 15f,
                fatPer100g = 1f
            ),
            FoodTemplate(
                name = "Lemon",
                caloriesPer100g = 29f,
                proteinPer100g = 1f,
                carbsPer100g = 9f,
                fatPer100g = 0f
            ),
            FoodTemplate(
                name = "Mango",
                caloriesPer100g = 60f,
                proteinPer100g = 1f,
                carbsPer100g = 15f,
                fatPer100g = 0f
            ),
            FoodTemplate(
                name = "Orange",
                caloriesPer100g = 47f,
                proteinPer100g = 1f,
                carbsPer100g = 12f,
                fatPer100g = 0f
            ),
            FoodTemplate(
                name = "Peach",
                caloriesPer100g = 39f,
                proteinPer100g = 1f,
                carbsPer100g = 10f,
                fatPer100g = 0f
            ),
            FoodTemplate(
                name = "Pineapple",
                caloriesPer100g = 50f,
                proteinPer100g = 1f,
                carbsPer100g = 13f,
                fatPer100g = 0f
            ),
            FoodTemplate(
                name = "Raspberries",
                caloriesPer100g = 52f,
                proteinPer100g = 1f,
                carbsPer100g = 12f,
                fatPer100g = 1f
            ),
            FoodTemplate(
                name = "Strawberries",
                caloriesPer100g = 32f,
                proteinPer100g = 1f,
                carbsPer100g = 8f,
                fatPer100g = 0f
            ),
            FoodTemplate(
                name = "Watermelon",
                caloriesPer100g = 30f,
                proteinPer100g = 1f,
                carbsPer100g = 8f,
                fatPer100g = 0f
            ),

            // Vegetables
            FoodTemplate(
                name = "Asparagus, cooked",
                caloriesPer100g = 20f,
                proteinPer100g = 2f,
                carbsPer100g = 4f,
                fatPer100g = 0f
            ),
            FoodTemplate(
                name = "Bell Pepper, red, raw",
                caloriesPer100g = 31f,
                proteinPer100g = 1f,
                carbsPer100g = 6f,
                fatPer100g = 0f
            ),
            FoodTemplate(
                name = "Broccoli, raw",
                caloriesPer100g = 34f,
                proteinPer100g = 3f,
                carbsPer100g = 7f,
                fatPer100g = 0f
            ),
            FoodTemplate(
                name = "Carrot, raw",
                caloriesPer100g = 41f,
                proteinPer100g = 1f,
                carbsPer100g = 10f,
                fatPer100g = 0f
            ),
            FoodTemplate(
                name = "Cauliflower, raw",
                caloriesPer100g = 25f,
                proteinPer100g = 2f,
                carbsPer100g = 5f,
                fatPer100g = 0f
            ),
            FoodTemplate(
                name = "Celery, raw",
                caloriesPer100g = 16f,
                proteinPer100g = 1f,
                carbsPer100g = 3f,
                fatPer100g = 0f
            ),
            FoodTemplate(
                name = "Cucumber",
                caloriesPer100g = 15f,
                proteinPer100g = 1f,
                carbsPer100g = 4f,
                fatPer100g = 0f
            ),
            FoodTemplate(
                name = "Garlic, raw",
                caloriesPer100g = 149f,
                proteinPer100g = 6f,
                carbsPer100g = 33f,
                fatPer100g = 1f
            ),
            FoodTemplate(
                name = "Green Beans, cooked",
                caloriesPer100g = 35f,
                proteinPer100g = 2f,
                carbsPer100g = 8f,
                fatPer100g = 0f
            ),
            FoodTemplate(
                name = "Lettuce, Iceberg",
                caloriesPer100g = 14f,
                proteinPer100g = 1f,
                carbsPer100g = 3f,
                fatPer100g = 0f
            ),
            FoodTemplate(
                name = "Mushroom, raw",
                caloriesPer100g = 22f,
                proteinPer100g = 3f,
                carbsPer100g = 3f,
                fatPer100g = 0f
            ),
            FoodTemplate(
                name = "Onion, raw",
                caloriesPer100g = 40f,
                proteinPer100g = 1f,
                carbsPer100g = 9f,
                fatPer100g = 0f
            ),
            FoodTemplate(
                name = "Potato, raw",
                caloriesPer100g = 77f,
                proteinPer100g = 2f,
                carbsPer100g = 17f,
                fatPer100g = 0f
            ),
            FoodTemplate(
                name = "Spinach, raw",
                caloriesPer100g = 23f,
                proteinPer100g = 3f,
                carbsPer100g = 4f,
                fatPer100g = 0f
            ),
            FoodTemplate(
                name = "Sweet Potato, raw",
                caloriesPer100g = 86f,
                proteinPer100g = 2f,
                carbsPer100g = 20f,
                fatPer100g = 0f
            ),
            FoodTemplate(
                name = "Tomato",
                caloriesPer100g = 18f,
                proteinPer100g = 1f,
                carbsPer100g = 4f,
                fatPer100g = 0f
            ),
            FoodTemplate(
                name = "Zucchini, raw",
                caloriesPer100g = 17f,
                proteinPer100g = 1f,
                carbsPer100g = 3f,
                fatPer100g = 0f
            ),

            // Proteins (Meat, Poultry, Fish)
            FoodTemplate(
                name = "Bacon, cooked",
                caloriesPer100g = 541f,
                proteinPer100g = 37f,
                carbsPer100g = 1f,
                fatPer100g = 42f
            ),
            FoodTemplate(
                name = "Beef, Ground (85% lean), cooked",
                caloriesPer100g = 215f,
                proteinPer100g = 26f,
                carbsPer100g = 0f,
                fatPer100g = 15f
            ),
            FoodTemplate(
                name = "Beef, Steak (Sirloin), cooked",
                caloriesPer100g = 206f,
                proteinPer100g = 29f,
                carbsPer100g = 0f,
                fatPer100g = 9f
            ),
            FoodTemplate(
                name = "Chicken Breast, cooked",
                caloriesPer100g = 165f,
                proteinPer100g = 31f,
                carbsPer100g = 0f,
                fatPer100g = 4f
            ),
            FoodTemplate(
                name = "Chicken Thigh, cooked",
                caloriesPer100g = 209f,
                proteinPer100g = 26f,
                carbsPer100g = 0f,
                fatPer100g = 11f
            ),
            FoodTemplate(
                name = "Cod, cooked",
                caloriesPer100g = 105f,
                proteinPer100g = 23f,
                carbsPer100g = 0f,
                fatPer100g = 1f
            ),
            FoodTemplate(
                name = "Egg, whole, boiled",
                caloriesPer100g = 155f,
                proteinPer100g = 13f,
                carbsPer100g = 1f,
                fatPer100g = 11f
            ),
            FoodTemplate(
                name = "Egg White",
                caloriesPer100g = 52f,
                proteinPer100g = 11f,
                carbsPer100g = 1f,
                fatPer100g = 0f
            ),
            FoodTemplate(
                name = "Pork Chop, cooked",
                caloriesPer100g = 231f,
                proteinPer100g = 26f,
                carbsPer100g = 0f,
                fatPer100g = 13f
            ),
            FoodTemplate(
                name = "Pork Sausage, cooked",
                caloriesPer100g = 334f,
                proteinPer100g = 20f,
                carbsPer100g = 1f,
                fatPer100g = 27f
            ),
            FoodTemplate(
                name = "Salmon, cooked",
                caloriesPer100g = 206f,
                proteinPer100g = 22f,
                carbsPer100g = 0f,
                fatPer100g = 13f
            ),
            FoodTemplate(
                name = "Shrimp, cooked",
                caloriesPer100g = 99f,
                proteinPer100g = 24f,
                carbsPer100g = 0f,
                fatPer100g = 0f
            ),
            FoodTemplate(
                name = "Tofu, firm",
                caloriesPer100g = 76f,
                proteinPer100g = 8f,
                carbsPer100g = 2f,
                fatPer100g = 5f
            ),
            FoodTemplate(
                name = "Tuna, canned in water",
                caloriesPer100g = 116f,
                proteinPer100g = 26f,
                carbsPer100g = 0f,
                fatPer100g = 1f
            ),
            FoodTemplate(
                name = "Turkey Breast, cooked",
                caloriesPer100g = 135f,
                proteinPer100g = 29f,
                carbsPer100g = 0f,
                fatPer100g = 1f
            ),

            // Grains, Legumes & Pasta
            FoodTemplate(
                name = "Barley, cooked",
                caloriesPer100g = 123f,
                proteinPer100g = 2f,
                carbsPer100g = 28f,
                fatPer100g = 0f
            ),
            FoodTemplate(
                name = "Black Beans, cooked",
                caloriesPer100g = 132f,
                proteinPer100g = 9f,
                carbsPer100g = 24f,
                fatPer100g = 1f
            ),
            FoodTemplate(
                name = "Bread, white",
                caloriesPer100g = 265f,
                proteinPer100g = 9f,
                carbsPer100g = 49f,
                fatPer100g = 3f
            ),
            FoodTemplate(
                name = "Bread, whole wheat",
                caloriesPer100g = 247f,
                proteinPer100g = 13f,
                carbsPer100g = 41f,
                fatPer100g = 4f
            ),
            FoodTemplate(
                name = "Chickpeas, canned",
                caloriesPer100g = 139f,
                proteinPer100g = 8f,
                carbsPer100g = 27f,
                fatPer100g = 2f
            ),
            FoodTemplate(
                name = "Corn, sweet, cooked",
                caloriesPer100g = 96f,
                proteinPer100g = 3f,
                carbsPer100g = 21f,
                fatPer100g = 1f
            ),
            FoodTemplate(
                name = "Couscous, cooked",
                caloriesPer100g = 112f,
                proteinPer100g = 4f,
                carbsPer100g = 23f,
                fatPer100g = 0f
            ),
            FoodTemplate(
                name = "Kidney Beans, cooked",
                caloriesPer100g = 127f,
                proteinPer100g = 9f,
                carbsPer100g = 23f,
                fatPer100g = 1f
            ),
            FoodTemplate(
                name = "Lentils, cooked",
                caloriesPer100g = 116f,
                proteinPer100g = 9f,
                carbsPer100g = 20f,
                fatPer100g = 0f
            ),
            FoodTemplate(
                name = "Oats, raw",
                caloriesPer100g = 389f,
                proteinPer100g = 17f,
                carbsPer100g = 66f,
                fatPer100g = 7f
            ),
            FoodTemplate(
                name = "Pasta, dry",
                caloriesPer100g = 371f,
                proteinPer100g = 13f,
                carbsPer100g = 75f,
                fatPer100g = 2f
            ),
            FoodTemplate(
                name = "Quinoa, cooked",
                caloriesPer100g = 120f,
                proteinPer100g = 4f,
                carbsPer100g = 21f,
                fatPer100g = 2f
            ),
            FoodTemplate(
                name = "Rice, brown, cooked",
                caloriesPer100g = 111f,
                proteinPer100g = 3f,
                carbsPer100g = 23f,
                fatPer100g = 1f
            ),
            FoodTemplate(
                name = "Rice, white, cooked",
                caloriesPer100g = 130f,
                proteinPer100g = 3f,
                carbsPer100g = 28f,
                fatPer100g = 0f
            ),

            // Dairy, Nuts & Seeds
            FoodTemplate(
                name = "Almonds",
                caloriesPer100g = 579f,
                proteinPer100g = 21f,
                carbsPer100g = 22f,
                fatPer100g = 49f
            ),
            FoodTemplate(
                name = "Cashews",
                caloriesPer100g = 553f,
                proteinPer100g = 18f,
                carbsPer100g = 30f,
                fatPer100g = 44f
            ),
            FoodTemplate(
                name = "Cheese, Cheddar",
                caloriesPer100g = 404f,
                proteinPer100g = 25f,
                carbsPer100g = 1f,
                fatPer100g = 33f
            ),
            FoodTemplate(
                name = "Cheese, Cottage",
                caloriesPer100g = 98f,
                proteinPer100g = 11f,
                carbsPer100g = 3f,
                fatPer100g = 4f
            ),
            FoodTemplate(
                name = "Cheese, Cream",
                caloriesPer100g = 342f,
                proteinPer100g = 6f,
                carbsPer100g = 4f,
                fatPer100g = 34f
            ),
            FoodTemplate(
                name = "Cheese, Mozzarella",
                caloriesPer100g = 280f,
                proteinPer100g = 28f,
                carbsPer100g = 3f,
                fatPer100g = 17f
            ),
            FoodTemplate(
                name = "Cheese, Parmesan",
                caloriesPer100g = 431f,
                proteinPer100g = 38f,
                carbsPer100g = 4f,
                fatPer100g = 29f
            ),
            FoodTemplate(
                name = "Chia Seeds",
                caloriesPer100g = 486f,
                proteinPer100g = 17f,
                carbsPer100g = 42f,
                fatPer100g = 31f
            ),
            FoodTemplate(
                name = "Greek Yogurt, plain, non-fat",
                caloriesPer100g = 59f,
                proteinPer100g = 10f,
                carbsPer100g = 4f,
                fatPer100g = 0f
            ),
            FoodTemplate(
                name = "Milk, whole",
                caloriesPer100g = 61f,
                proteinPer100g = 3f,
                carbsPer100g = 5f,
                fatPer100g = 3f
            ),
            FoodTemplate(
                name = "Peanut Butter",
                caloriesPer100g = 588f,
                proteinPer100g = 25f,
                carbsPer100g = 20f,
                fatPer100g = 50f
            ),
            FoodTemplate(
                name = "Pecans",
                caloriesPer100g = 691f,
                proteinPer100g = 9f,
                carbsPer100g = 14f,
                fatPer100g = 72f
            ),
            FoodTemplate(
                name = "Walnuts",
                caloriesPer100g = 654f,
                proteinPer100g = 15f,
                carbsPer100g = 14f,
                fatPer100g = 65f
            ),
            FoodTemplate(
                name = "Whey Protein Powder",
                caloriesPer100g = 375f,
                proteinPer100g = 80f,
                carbsPer100g = 7f,
                fatPer100g = 3f
            ),


            // --- NEWLY ADDED OILS & FATS SECTION ---
            FoodTemplate(
                name = "Avocado Oil",
                caloriesPer100g = 884f,
                proteinPer100g = 0f,
                carbsPer100g = 0f,
                fatPer100g = 100f
            ),
            FoodTemplate(
                name = "Butter, salted",
                caloriesPer100g = 717f,
                proteinPer100g = 1f,
                carbsPer100g = 0f,
                fatPer100g = 81f
            ),
            FoodTemplate(
                name = "Canola Oil",
                caloriesPer100g = 884f,
                proteinPer100g = 0f,
                carbsPer100g = 0f,
                fatPer100g = 100f
            ),
            FoodTemplate(
                name = "Coconut Oil",
                caloriesPer100g = 892f,
                proteinPer100g = 0f,
                carbsPer100g = 0f,
                fatPer100g = 99f
            ),
            FoodTemplate(
                name = "Corn Oil",
                caloriesPer100g = 884f,
                proteinPer100g = 0f,
                carbsPer100g = 0f,
                fatPer100g = 100f
            ),
            FoodTemplate(
                name = "Flaxseed Oil",
                caloriesPer100g = 884f,
                proteinPer100g = 0f,
                carbsPer100g = 0f,
                fatPer100g = 100f
            ),
            FoodTemplate(
                name = "Ghee",
                caloriesPer100g = 900f,
                proteinPer100g = 0f,
                carbsPer100g = 0f,
                fatPer100g = 100f
            ),
            FoodTemplate(
                name = "Lard",
                caloriesPer100g = 902f,
                proteinPer100g = 0f,
                carbsPer100g = 0f,
                fatPer100g = 100f
            ),
            FoodTemplate(
                name = "Margarine",
                caloriesPer100g = 717f,
                proteinPer100g = 1f,
                carbsPer100g = 1f,
                fatPer100g = 81f
            ),
            FoodTemplate(
                name = "Olive Oil",
                caloriesPer100g = 884f,
                proteinPer100g = 0f,
                carbsPer100g = 0f,
                fatPer100g = 100f
            ),
            FoodTemplate(
                name = "Sesame Oil",
                caloriesPer100g = 884f,
                proteinPer100g = 0f,
                carbsPer100g = 0f,
                fatPer100g = 100f
            ),
            FoodTemplate(
                name = "Sunflower Oil",
                caloriesPer100g = 884f,
                proteinPer100g = 0f,
                carbsPer100g = 0f,
                fatPer100g = 100f
            ),


            // Beverages
            FoodTemplate(
                name = "Coffee, black",
                caloriesPer100g = 1f,
                proteinPer100g = 0f,
                carbsPer100g = 0f,
                fatPer100g = 0f
            ),
            FoodTemplate(
                name = "Juice, Apple",
                caloriesPer100g = 46f,
                proteinPer100g = 0f,
                carbsPer100g = 11f,
                fatPer100g = 0f
            ),
            FoodTemplate(
                name = "Juice, Orange",
                caloriesPer100g = 45f,
                proteinPer100g = 1f,
                carbsPer100g = 10f,
                fatPer100g = 0f
            ),

            // Snacks, Sweets & Condiments
            FoodTemplate(
                name = "Chocolate, Dark (70-85%)",
                caloriesPer100g = 598f,
                proteinPer100g = 8f,
                carbsPer100g = 46f,
                fatPer100g = 43f
            ),
            FoodTemplate(
                name = "Honey",
                caloriesPer100g = 304f,
                proteinPer100g = 0f,
                carbsPer100g = 82f,
                fatPer100g = 0f
            ),
            FoodTemplate(
                name = "Ketchup",
                caloriesPer100g = 112f,
                proteinPer100g = 2f,
                carbsPer100g = 25f,
                fatPer100g = 0f
            ),
            FoodTemplate(
                name = "Mayonnaise",
                caloriesPer100g = 724f,
                proteinPer100g = 1f,
                carbsPer100g = 1f,
                fatPer100g = 79f
            ),
            FoodTemplate(
                name = "Mustard, yellow",
                caloriesPer100g = 143f,
                proteinPer100g = 8f,
                carbsPer100g = 10f,
                fatPer100g = 8f
            ),
            FoodTemplate(
                name = "Potato Chips, salted",
                caloriesPer100g = 536f,
                proteinPer100g = 7f,
                carbsPer100g = 53f,
                fatPer100g = 35f
            ),
            FoodTemplate(
                name = "Sugar, white",
                caloriesPer100g = 387f,
                proteinPer100g = 0f,
                carbsPer100g = 100f,
                fatPer100g = 0f
            ),

                    // Additional Grains, Legumes & Cereals
            FoodTemplate(
                name = "Amaranth, cooked",
                caloriesPer100g = 102f,
                proteinPer100g = 3f,
                carbsPer100g = 19f,
                fatPer100g = 2f
            ),
            FoodTemplate(
                name = "Farro, cooked",
                caloriesPer100g = 125f,
                proteinPer100g = 4f,
                carbsPer100g = 26f,
                fatPer100g = 1f
            ),
            FoodTemplate(
                name = "Millet, cooked",
                caloriesPer100g = 119f,
                proteinPer100g = 3f,
                carbsPer100g = 23f,
                fatPer100g = 1f
            ),
            FoodTemplate(
                name = "Teff, cooked",
                caloriesPer100g = 101f,
                proteinPer100g = 4f,
                carbsPer100g = 20f,
                fatPer100g = 1f
            ),
            FoodTemplate(
                name = "Buckwheat, cooked",
                caloriesPer100g = 92f,
                proteinPer100g = 3f,
                carbsPer100g = 20f,
                fatPer100g = 1f
            ),
            FoodTemplate(
                name = "Bulgur, cooked",
                caloriesPer100g = 83f,
                proteinPer100g = 3f,
                carbsPer100g = 19f,
                fatPer100g = 0f
            ),

// More Fruits
            FoodTemplate(
                name = "Papaya",
                caloriesPer100g = 43f,
                proteinPer100g = 1f,
                carbsPer100g = 11f,
                fatPer100g = 0f
            ),
            FoodTemplate(
                name = "Passion Fruit",
                caloriesPer100g = 97f,
                proteinPer100g = 2f,
                carbsPer100g = 23f,
                fatPer100g = 1f
            ),
            FoodTemplate(
                name = "Pomegranate",
                caloriesPer100g = 83f,
                proteinPer100g = 1f,
                carbsPer100g = 19f,
                fatPer100g = 1f
            ),
            FoodTemplate(
                name = "Guava",
                caloriesPer100g = 68f,
                proteinPer100g = 2f,
                carbsPer100g = 14f,
                fatPer100g = 1f
            ),
            FoodTemplate(
                name = "Lychee",
                caloriesPer100g = 66f,
                proteinPer100g = 1f,
                carbsPer100g = 17f,
                fatPer100g = 0f
            ),
            FoodTemplate(
                name = "Cranberries",
                caloriesPer100g = 46f,
                proteinPer100g = 0f,
                carbsPer100g = 12f,
                fatPer100g = 0f
            ),

// More Dairy
            FoodTemplate(
                name = "Yogurt, plain, low-fat",
                caloriesPer100g = 63f,
                proteinPer100g = 5f,
                carbsPer100g = 7f,
                fatPer100g = 1f
            ),
            FoodTemplate(
                name = "Milk, skim",
                caloriesPer100g = 35f,
                proteinPer100g = 3f,
                carbsPer100g = 5f,
                fatPer100g = 0f
            ),
            FoodTemplate(
                name = "Milk, 2%",
                caloriesPer100g = 50f,
                proteinPer100g = 3f,
                carbsPer100g = 5f,
                fatPer100g = 2f
            ),
            FoodTemplate(
                name = "Ricotta Cheese",
                caloriesPer100g = 174f,
                proteinPer100g = 11f,
                carbsPer100g = 3f,
                fatPer100g = 13f
            ),

// More Meats
            FoodTemplate(
                name = "Duck, cooked",
                caloriesPer100g = 337f,
                proteinPer100g = 27f,
                carbsPer100g = 0f,
                fatPer100g = 25f
            ),
            FoodTemplate(
                name = "Lamb, cooked",
                caloriesPer100g = 294f,
                proteinPer100g = 25f,
                carbsPer100g = 0f,
                fatPer100g = 21f
            ),
            FoodTemplate(
                name = "Venison, cooked",
                caloriesPer100g = 158f,
                proteinPer100g = 30f,
                carbsPer100g = 0f,
                fatPer100g = 3f
            ),

// More Fish
            FoodTemplate(
                name = "Halibut, cooked",
                caloriesPer100g = 140f,
                proteinPer100g = 27f,
                carbsPer100g = 0f,
                fatPer100g = 3f
            ),
            FoodTemplate(
                name = "Sardines, canned in oil",
                caloriesPer100g = 208f,
                proteinPer100g = 25f,
                carbsPer100g = 0f,
                fatPer100g = 11f
            ),
            FoodTemplate(
                name = "Mackerel, cooked",
                caloriesPer100g = 262f,
                proteinPer100g = 23f,
                carbsPer100g = 0f,
                fatPer100g = 18f
            ),

// More Snacks / Spreads
            FoodTemplate(
                name = "Nutella",
                caloriesPer100g = 539f,
                proteinPer100g = 6f,
                carbsPer100g = 57f,
                fatPer100g = 31f
            ),
            FoodTemplate(
                name = "Granola Bar",
                caloriesPer100g = 471f,
                proteinPer100g = 8f,
                carbsPer100g = 64f,
                fatPer100g = 21f
            ),
            FoodTemplate(
                name = "Trail Mix",
                caloriesPer100g = 462f,
                proteinPer100g = 12f,
                carbsPer100g = 43f,
                fatPer100g = 27f
            ),

// More Oils & Fats
            FoodTemplate(
                name = "Palm Oil",
                caloriesPer100g = 884f,
                proteinPer100g = 0f,
                carbsPer100g = 0f,
                fatPer100g = 100f
            ),
            FoodTemplate(
                name = "Grapeseed Oil",
                caloriesPer100g = 884f,
                proteinPer100g = 0f,
                carbsPer100g = 0f,
                fatPer100g = 100f
            ),

// More Beverages
            FoodTemplate(
                name = "Tea, unsweetened",
                caloriesPer100g = 1f,
                proteinPer100g = 0f,
                carbsPer100g = 0f,
                fatPer100g = 0f
            ),
            FoodTemplate(
                name = "Cola, regular",
                caloriesPer100g = 42f,
                proteinPer100g = 0f,
                carbsPer100g = 11f,
                fatPer100g = 0f
            ),
            FoodTemplate(
                name = "Beer, regular",
                caloriesPer100g = 43f,
                proteinPer100g = 0f,
                carbsPer100g = 4f,
                fatPer100g = 0f
            ),
            FoodTemplate(
                name = "Red Wine",
                caloriesPer100g = 85f,
                proteinPer100g = 0f,
                carbsPer100g = 3f,
                fatPer100g = 0f
            ),

        //GERMAN FOODS
            FoodTemplate(
                name = "Apfel",
                caloriesPer100g = 52f,
                proteinPer100g = 0f,
                carbsPer100g = 14f,
                fatPer100g = 0f
            ),
            FoodTemplate(
                name = "Avocado",
                caloriesPer100g = 160f,
                proteinPer100g = 2f,
                carbsPer100g = 9f,
                fatPer100g = 15f
            ),
            FoodTemplate(
                name = "Banane",
                caloriesPer100g = 89f,
                proteinPer100g = 1f,
                carbsPer100g = 23f,
                fatPer100g = 0f
            ),
            FoodTemplate(
                name = "Blaubeeren",
                caloriesPer100g = 57f,
                proteinPer100g = 1f,
                carbsPer100g = 14f,
                fatPer100g = 0f
            ),
            FoodTemplate(
                name = "Kirschen",
                caloriesPer100g = 50f,
                proteinPer100g = 1f,
                carbsPer100g = 12f,
                fatPer100g = 0f
            ),
            FoodTemplate(
                name = "Trauben",
                caloriesPer100g = 69f,
                proteinPer100g = 1f,
                carbsPer100g = 18f,
                fatPer100g = 0f
            ),
            FoodTemplate(
                name = "Kiwi",
                caloriesPer100g = 61f,
                proteinPer100g = 1f,
                carbsPer100g = 15f,
                fatPer100g = 1f
            ),
            FoodTemplate(
                name = "Zitrone",
                caloriesPer100g = 29f,
                proteinPer100g = 1f,
                carbsPer100g = 9f,
                fatPer100g = 0f
            ),
            FoodTemplate(
                name = "Mango",
                caloriesPer100g = 60f,
                proteinPer100g = 1f,
                carbsPer100g = 15f,
                fatPer100g = 0f
            ),
            FoodTemplate(
                name = "Orange",
                caloriesPer100g = 47f,
                proteinPer100g = 1f,
                carbsPer100g = 12f,
                fatPer100g = 0f
            ),
            FoodTemplate(
                name = "Pfirsich",
                caloriesPer100g = 39f,
                proteinPer100g = 1f,
                carbsPer100g = 10f,
                fatPer100g = 0f
            ),
            FoodTemplate(
                name = "Ananas",
                caloriesPer100g = 50f,
                proteinPer100g = 1f,
                carbsPer100g = 13f,
                fatPer100g = 0f
            ),
            FoodTemplate(
                name = "Himbeeren",
                caloriesPer100g = 52f,
                proteinPer100g = 1f,
                carbsPer100g = 12f,
                fatPer100g = 1f
            ),
            FoodTemplate(
                name = "Erdbeeren",
                caloriesPer100g = 32f,
                proteinPer100g = 1f,
                carbsPer100g = 8f,
                fatPer100g = 0f
            ),
            FoodTemplate(
                name = "Wassermelone",
                caloriesPer100g = 30f,
                proteinPer100g = 1f,
                carbsPer100g = 8f,
                fatPer100g = 0f
            ),
            FoodTemplate(
                name = "Papaya",
                caloriesPer100g = 43f,
                proteinPer100g = 1f,
                carbsPer100g = 11f,
                fatPer100g = 0f
            ),
            FoodTemplate(
                name = "Passionsfrucht",
                caloriesPer100g = 97f,
                proteinPer100g = 2f,
                carbsPer100g = 23f,
                fatPer100g = 1f
            ),
            FoodTemplate(
                name = "Granatapfel",
                caloriesPer100g = 83f,
                proteinPer100g = 1f,
                carbsPer100g = 19f,
                fatPer100g = 1f
            ),
            FoodTemplate(
                name = "Guave",
                caloriesPer100g = 68f,
                proteinPer100g = 2f,
                carbsPer100g = 14f,
                fatPer100g = 1f
            ),
            FoodTemplate(
                name = "Litschi",
                caloriesPer100g = 66f,
                proteinPer100g = 1f,
                carbsPer100g = 17f,
                fatPer100g = 0f
            ),
            FoodTemplate(
                name = "Preiselbeeren",
                caloriesPer100g = 46f,
                proteinPer100g = 0f,
                carbsPer100g = 12f,
                fatPer100g = 0f
            ),
            // Gemüse
            FoodTemplate(
                name = "Spargel, gekocht",
                caloriesPer100g = 20f,
                proteinPer100g = 2f,
                carbsPer100g = 4f,
                fatPer100g = 0f
            ),
            FoodTemplate(
                name = "Rote Paprika, roh",
                caloriesPer100g = 31f,
                proteinPer100g = 1f,
                carbsPer100g = 6f,
                fatPer100g = 0f
            ),
            FoodTemplate(
                name = "Brokkoli, roh",
                caloriesPer100g = 34f,
                proteinPer100g = 3f,
                carbsPer100g = 7f,
                fatPer100g = 0f
            ),
            FoodTemplate(
                name = "Karotte, roh",
                caloriesPer100g = 41f,
                proteinPer100g = 1f,
                carbsPer100g = 10f,
                fatPer100g = 0f
            ),
            FoodTemplate(
                name = "Blumenkohl, roh",
                caloriesPer100g = 25f,
                proteinPer100g = 2f,
                carbsPer100g = 5f,
                fatPer100g = 0f
            ),
            FoodTemplate(
                name = "Sellerie, roh",
                caloriesPer100g = 16f,
                proteinPer100g = 1f,
                carbsPer100g = 3f,
                fatPer100g = 0f
            ),
            FoodTemplate(
                name = "Gurke",
                caloriesPer100g = 15f,
                proteinPer100g = 1f,
                carbsPer100g = 4f,
                fatPer100g = 0f
            ),
            FoodTemplate(
                name = "Knoblauch, roh",
                caloriesPer100g = 149f,
                proteinPer100g = 6f,
                carbsPer100g = 33f,
                fatPer100g = 1f
            ),
            FoodTemplate(
                name = "Grüne Bohnen, gekocht",
                caloriesPer100g = 35f,
                proteinPer100g = 2f,
                carbsPer100g = 8f,
                fatPer100g = 0f
            ),
            FoodTemplate(
                name = "Eisbergsalat",
                caloriesPer100g = 14f,
                proteinPer100g = 1f,
                carbsPer100g = 3f,
                fatPer100g = 0f
            ),
            FoodTemplate(
                name = "Champignons, roh",
                caloriesPer100g = 22f,
                proteinPer100g = 3f,
                carbsPer100g = 3f,
                fatPer100g = 0f
            ),
            FoodTemplate(
                name = "Zwiebel, roh",
                caloriesPer100g = 40f,
                proteinPer100g = 1f,
                carbsPer100g = 9f,
                fatPer100g = 0f
            ),
            FoodTemplate(
                name = "Kartoffel, roh",
                caloriesPer100g = 77f,
                proteinPer100g = 2f,
                carbsPer100g = 17f,
                fatPer100g = 0f
            ),
            FoodTemplate(
                name = "Spinat, roh",
                caloriesPer100g = 23f,
                proteinPer100g = 3f,
                carbsPer100g = 4f,
                fatPer100g = 0f
            ),
            FoodTemplate(
                name = "Süßkartoffel, roh",
                caloriesPer100g = 86f,
                proteinPer100g = 2f,
                carbsPer100g = 20f,
                fatPer100g = 0f
            ),
            FoodTemplate(
                name = "Tomate",
                caloriesPer100g = 18f,
                proteinPer100g = 1f,
                carbsPer100g = 4f,
                fatPer100g = 0f
            ),
            FoodTemplate(
                name = "Zucchini, roh",
                caloriesPer100g = 17f,
                proteinPer100g = 1f,
                carbsPer100g = 3f,
                fatPer100g = 0f
            ),

            // Fleisch & Fisch
            FoodTemplate(
                name = "Hähnchenbrust, gekocht",
                caloriesPer100g = 165f,
                proteinPer100g = 31f,
                carbsPer100g = 0f,
                fatPer100g = 4f
            ),
            FoodTemplate(
                name = "Hähnchenschenkel, gekocht",
                caloriesPer100g = 209f,
                proteinPer100g = 26f,
                carbsPer100g = 0f,
                fatPer100g = 11f
            ),
            FoodTemplate(
                name = "Rindfleisch, Steak, gekocht",
                caloriesPer100g = 206f,
                proteinPer100g = 29f,
                carbsPer100g = 0f,
                fatPer100g = 9f
            ),
            FoodTemplate(
                name = "Hackfleisch, Rind (85%), gekocht",
                caloriesPer100g = 215f,
                proteinPer100g = 26f,
                carbsPer100g = 0f,
                fatPer100g = 15f
            ),
            FoodTemplate(
                name = "Bacon, gekocht",
                caloriesPer100g = 541f,
                proteinPer100g = 37f,
                carbsPer100g = 1f,
                fatPer100g = 42f
            ),
            FoodTemplate(
                name = "Lachs, gekocht",
                caloriesPer100g = 206f,
                proteinPer100g = 22f,
                carbsPer100g = 0f,
                fatPer100g = 13f
            ),
            FoodTemplate(
                name = "Garnelen, gekocht",
                caloriesPer100g = 99f,
                proteinPer100g = 24f,
                carbsPer100g = 0f,
                fatPer100g = 0f
            ),
            FoodTemplate(
                name = "Thunfisch, Dose",
                caloriesPer100g = 116f,
                proteinPer100g = 26f,
                carbsPer100g = 0f,
                fatPer100g = 1f
            ),
            FoodTemplate(
                name = "Tofu, fest",
                caloriesPer100g = 76f,
                proteinPer100g = 8f,
                carbsPer100g = 2f,
                fatPer100g = 5f
            ),

            // Milchprodukte
            FoodTemplate(
                name = "Milch, Vollmilch",
                caloriesPer100g = 61f,
                proteinPer100g = 3f,
                carbsPer100g = 5f,
                fatPer100g = 3f
            ),
            FoodTemplate(
                name = "Milch, fettarm",
                caloriesPer100g = 50f,
                proteinPer100g = 3f,
                carbsPer100g = 5f,
                fatPer100g = 2f
            ),
            FoodTemplate(
                name = "Joghurt, griechisch, fettfrei",
                caloriesPer100g = 59f,
                proteinPer100g = 10f,
                carbsPer100g = 4f,
                fatPer100g = 0f
            ),
            FoodTemplate(
                name = "Quark",
                caloriesPer100g = 98f,
                proteinPer100g = 11f,
                carbsPer100g = 3f,
                fatPer100g = 4f
            ),
            FoodTemplate(
                name = "Käse, Mozzarella",
                caloriesPer100g = 280f,
                proteinPer100g = 28f,
                carbsPer100g = 3f,
                fatPer100g = 17f
            ),
            FoodTemplate(
                name = "Käse, Parmesan",
                caloriesPer100g = 431f,
                proteinPer100g = 38f,
                carbsPer100g = 4f,
                fatPer100g = 29f
            ),
            FoodTemplate(
                name = "Käse, Cheddar",
                caloriesPer100g = 404f,
                proteinPer100g = 25f,
                carbsPer100g = 1f,
                fatPer100g = 33f
            ),

            // Getränke
            FoodTemplate(
                name = "Kaffee, schwarz",
                caloriesPer100g = 1f,
                proteinPer100g = 0f,
                carbsPer100g = 0f,
                fatPer100g = 0f
            ),
            FoodTemplate(
                name = "Tee, ungesüßt",
                caloriesPer100g = 1f,
                proteinPer100g = 0f,
                carbsPer100g = 0f,
                fatPer100g = 0f
            ),
            FoodTemplate(
                name = "Orangensaft",
                caloriesPer100g = 45f,
                proteinPer100g = 1f,
                carbsPer100g = 10f,
                fatPer100g = 0f
            ),
            FoodTemplate(
                name = "Cola",
                caloriesPer100g = 42f,
                proteinPer100g = 0f,
                carbsPer100g = 11f,
                fatPer100g = 0f
            ),
            FoodTemplate(
                name = "Bier",
                caloriesPer100g = 43f,
                proteinPer100g = 0f,
                carbsPer100g = 4f,
                fatPer100g = 0f
            ),
            FoodTemplate(
                name = "Rotwein",
                caloriesPer100g = 85f,
                proteinPer100g = 0f,
                carbsPer100g = 3f,
                fatPer100g = 0f
            ),
// Gemüse
            FoodTemplate(
                name = "Rote Bete",
                caloriesPer100g = 43f,
                proteinPer100g = 2f,
                carbsPer100g = 10f,
                fatPer100g = 0f
            ),
            FoodTemplate(
                name = "Rosenkohl",
                caloriesPer100g = 43f,
                proteinPer100g = 3f,
                carbsPer100g = 9f,
                fatPer100g = 0f
            ),
            FoodTemplate(
                name = "Grünkohl",
                caloriesPer100g = 49f,
                proteinPer100g = 4f,
                carbsPer100g = 9f,
                fatPer100g = 1f
            ),
            FoodTemplate(
                name = "Aubergine",
                caloriesPer100g = 25f,
                proteinPer100g = 1f,
                carbsPer100g = 6f,
                fatPer100g = 0f
            ),
            FoodTemplate(
                name = "Zwiebel",
                caloriesPer100g = 40f,
                proteinPer100g = 1f,
                carbsPer100g = 9f,
                fatPer100g = 0f
            ),
            FoodTemplate(
                name = "Rettich",
                caloriesPer100g = 16f,
                proteinPer100g = 1f,
                carbsPer100g = 3f,
                fatPer100g = 0f
            ),

// Fleisch & Fisch
            FoodTemplate(
                name = "Hackfleisch (Rind)",
                caloriesPer100g = 250f,
                proteinPer100g = 26f,
                carbsPer100g = 0f,
                fatPer100g = 18f
            ),
            FoodTemplate(
                name = "Leberkäse",
                caloriesPer100g = 304f,
                proteinPer100g = 14f,
                carbsPer100g = 3f,
                fatPer100g = 27f
            ),
            FoodTemplate(
                name = "Bratwurst",
                caloriesPer100g = 297f,
                proteinPer100g = 12f,
                carbsPer100g = 2f,
                fatPer100g = 27f
            ),
            FoodTemplate(
                name = "Forelle, gekocht",
                caloriesPer100g = 148f,
                proteinPer100g = 20f,
                carbsPer100g = 0f,
                fatPer100g = 7f
            ),
            FoodTemplate(
                name = "Kabeljau, gekocht",
                caloriesPer100g = 105f,
                proteinPer100g = 23f,
                carbsPer100g = 0f,
                fatPer100g = 1f
            ),

// Milchprodukte
            FoodTemplate(
                name = "Buttermilch",
                caloriesPer100g = 35f,
                proteinPer100g = 3f,
                carbsPer100g = 4f,
                fatPer100g = 1f
            ),
            FoodTemplate(
                name = "Schmand",
                caloriesPer100g = 261f,
                proteinPer100g = 3f,
                carbsPer100g = 3f,
                fatPer100g = 26f
            ),
            FoodTemplate(
                name = "Quark, mager",
                caloriesPer100g = 67f,
                proteinPer100g = 12f,
                carbsPer100g = 4f,
                fatPer100g = 0f
            ),
            FoodTemplate(
                name = "Schlagsahne",
                caloriesPer100g = 292f,
                proteinPer100g = 2f,
                carbsPer100g = 3f,
                fatPer100g = 30f
            ),

// Brot & Getreide
            FoodTemplate(
                name = "Vollkornbrot",
                caloriesPer100g = 247f,
                proteinPer100g = 9f,
                carbsPer100g = 41f,
                fatPer100g = 3f
            ),
            FoodTemplate(
                name = "Roggenbrot",
                caloriesPer100g = 259f,
                proteinPer100g = 8f,
                carbsPer100g = 48f,
                fatPer100g = 3f
            ),
            FoodTemplate(
                name = "Knäckebrot",
                caloriesPer100g = 330f,
                proteinPer100g = 10f,
                carbsPer100g = 70f,
                fatPer100g = 1f
            ),
            FoodTemplate(
                name = "Haferflocken",
                caloriesPer100g = 389f,
                proteinPer100g = 13f,
                carbsPer100g = 66f,
                fatPer100g = 7f
            ),

// Getränke
            FoodTemplate(
                name = "Apfelsaft",
                caloriesPer100g = 46f,
                proteinPer100g = 0f,
                carbsPer100g = 11f,
                fatPer100g = 0f
            ),
            FoodTemplate(
                name = "Orangensaft",
                caloriesPer100g = 45f,
                proteinPer100g = 1f,
                carbsPer100g = 10f,
                fatPer100g = 0f
            ),
            FoodTemplate(
                name = "Schwarztee",
                caloriesPer100g = 1f,
                proteinPer100g = 0f,
                carbsPer100g = 0f,
                fatPer100g = 0f
            ),
            FoodTemplate(
                name = "Rotwein",
                caloriesPer100g = 85f,
                proteinPer100g = 0f,
                carbsPer100g = 3f,
                fatPer100g = 0f
            ),
            FoodTemplate(
                name = "Weißwein",
                caloriesPer100g = 82f,
                proteinPer100g = 0f,
                carbsPer100g = 2f,
                fatPer100g = 0f
            ),
            FoodTemplate(
                name = "Bier",
                caloriesPer100g = 43f,
                proteinPer100g = 0f,
                carbsPer100g = 4f,
                fatPer100g = 0f
            ),

// Süßes & Snacks
            FoodTemplate(
                name = "Milchschokolade",
                caloriesPer100g = 535f,
                proteinPer100g = 7f,
                carbsPer100g = 59f,
                fatPer100g = 30f
            ),
            FoodTemplate(
                name = "Gummibärchen",
                caloriesPer100g = 343f,
                proteinPer100g = 7f,
                carbsPer100g = 77f,
                fatPer100g = 0f
            ),
            FoodTemplate(
                name = "Kartoffelchips",
                caloriesPer100g = 536f,
                proteinPer100g = 7f,
                carbsPer100g = 53f,
                fatPer100g = 35f
            ),
            FoodTemplate(
                name = "Studentenfutter",
                caloriesPer100g = 462f,
                proteinPer100g = 12f,
                carbsPer100g = 43f,
                fatPer100g = 27f
            )


            )
    }
}