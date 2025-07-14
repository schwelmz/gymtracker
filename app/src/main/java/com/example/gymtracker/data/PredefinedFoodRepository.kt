package com.example.gymtracker.data

/**
 * This object acts as a simple data source for the foods that
 * come pre-packaged with the app. All nutritional values are approximate and per 100g.
 */
object PredefinedFoodRepository {

    fun getPredefinedFoods(): List<FoodTemplate> {
        return listOf(
            // Fruits
            FoodTemplate(name = "Apple", caloriesPer100g = 52, proteinPer100g = 0, carbsPer100g = 14, fatPer100g = 0),
            FoodTemplate(name = "Avocado", caloriesPer100g = 160, proteinPer100g = 2, carbsPer100g = 9, fatPer100g = 15),
            FoodTemplate(name = "Banana", caloriesPer100g = 89, proteinPer100g = 1, carbsPer100g = 23, fatPer100g = 0),
            FoodTemplate(name = "Blueberries", caloriesPer100g = 57, proteinPer100g = 1, carbsPer100g = 14, fatPer100g = 0),
            FoodTemplate(name = "Cherries", caloriesPer100g = 50, proteinPer100g = 1, carbsPer100g = 12, fatPer100g = 0),
            FoodTemplate(name = "Grapes", caloriesPer100g = 69, proteinPer100g = 1, carbsPer100g = 18, fatPer100g = 0),
            FoodTemplate(name = "Kiwi", caloriesPer100g = 61, proteinPer100g = 1, carbsPer100g = 15, fatPer100g = 1),
            FoodTemplate(name = "Lemon", caloriesPer100g = 29, proteinPer100g = 1, carbsPer100g = 9, fatPer100g = 0),
            FoodTemplate(name = "Mango", caloriesPer100g = 60, proteinPer100g = 1, carbsPer100g = 15, fatPer100g = 0),
            FoodTemplate(name = "Orange", caloriesPer100g = 47, proteinPer100g = 1, carbsPer100g = 12, fatPer100g = 0),
            FoodTemplate(name = "Peach", caloriesPer100g = 39, proteinPer100g = 1, carbsPer100g = 10, fatPer100g = 0),
            FoodTemplate(name = "Pineapple", caloriesPer100g = 50, proteinPer100g = 1, carbsPer100g = 13, fatPer100g = 0),
            FoodTemplate(name = "Raspberries", caloriesPer100g = 52, proteinPer100g = 1, carbsPer100g = 12, fatPer100g = 1),
            FoodTemplate(name = "Strawberries", caloriesPer100g = 32, proteinPer100g = 1, carbsPer100g = 8, fatPer100g = 0),
            FoodTemplate(name = "Watermelon", caloriesPer100g = 30, proteinPer100g = 1, carbsPer100g = 8, fatPer100g = 0),

            // Vegetables
            FoodTemplate(name = "Asparagus, cooked", caloriesPer100g = 20, proteinPer100g = 2, carbsPer100g = 4, fatPer100g = 0),
            FoodTemplate(name = "Bell Pepper, red, raw", caloriesPer100g = 31, proteinPer100g = 1, carbsPer100g = 6, fatPer100g = 0),
            FoodTemplate(name = "Broccoli, raw", caloriesPer100g = 34, proteinPer100g = 3, carbsPer100g = 7, fatPer100g = 0),
            FoodTemplate(name = "Carrot, raw", caloriesPer100g = 41, proteinPer100g = 1, carbsPer100g = 10, fatPer100g = 0),
            FoodTemplate(name = "Cauliflower, raw", caloriesPer100g = 25, proteinPer100g = 2, carbsPer100g = 5, fatPer100g = 0),
            FoodTemplate(name = "Celery, raw", caloriesPer100g = 16, proteinPer100g = 1, carbsPer100g = 3, fatPer100g = 0),
            FoodTemplate(name = "Cucumber", caloriesPer100g = 15, proteinPer100g = 1, carbsPer100g = 4, fatPer100g = 0),
            FoodTemplate(name = "Garlic, raw", caloriesPer100g = 149, proteinPer100g = 6, carbsPer100g = 33, fatPer100g = 1),
            FoodTemplate(name = "Green Beans, cooked", caloriesPer100g = 35, proteinPer100g = 2, carbsPer100g = 8, fatPer100g = 0),
            FoodTemplate(name = "Lettuce, Iceberg", caloriesPer100g = 14, proteinPer100g = 1, carbsPer100g = 3, fatPer100g = 0),
            FoodTemplate(name = "Mushroom, raw", caloriesPer100g = 22, proteinPer100g = 3, carbsPer100g = 3, fatPer100g = 0),
            FoodTemplate(name = "Onion, raw", caloriesPer100g = 40, proteinPer100g = 1, carbsPer100g = 9, fatPer100g = 0),
            FoodTemplate(name = "Potato, raw", caloriesPer100g = 77, proteinPer100g = 2, carbsPer100g = 17, fatPer100g = 0),
            FoodTemplate(name = "Spinach, raw", caloriesPer100g = 23, proteinPer100g = 3, carbsPer100g = 4, fatPer100g = 0),
            FoodTemplate(name = "Sweet Potato, raw", caloriesPer100g = 86, proteinPer100g = 2, carbsPer100g = 20, fatPer100g = 0),
            FoodTemplate(name = "Tomato", caloriesPer100g = 18, proteinPer100g = 1, carbsPer100g = 4, fatPer100g = 0),
            FoodTemplate(name = "Zucchini, raw", caloriesPer100g = 17, proteinPer100g = 1, carbsPer100g = 3, fatPer100g = 0),

            // Proteins (Meat, Poultry, Fish)
            FoodTemplate(name = "Bacon, cooked", caloriesPer100g = 541, proteinPer100g = 37, carbsPer100g = 1, fatPer100g = 42),
            FoodTemplate(name = "Beef, Ground (85% lean), cooked", caloriesPer100g = 215, proteinPer100g = 26, carbsPer100g = 0, fatPer100g = 15),
            FoodTemplate(name = "Beef, Steak (Sirloin), cooked", caloriesPer100g = 206, proteinPer100g = 29, carbsPer100g = 0, fatPer100g = 9),
            FoodTemplate(name = "Chicken Breast, cooked", caloriesPer100g = 165, proteinPer100g = 31, carbsPer100g = 0, fatPer100g = 4),
            FoodTemplate(name = "Chicken Thigh, cooked", caloriesPer100g = 209, proteinPer100g = 26, carbsPer100g = 0, fatPer100g = 11),
            FoodTemplate(name = "Cod, cooked", caloriesPer100g = 105, proteinPer100g = 23, carbsPer100g = 0, fatPer100g = 1),
            FoodTemplate(name = "Egg, whole, boiled", caloriesPer100g = 155, proteinPer100g = 13, carbsPer100g = 1, fatPer100g = 11),
            FoodTemplate(name = "Egg White", caloriesPer100g = 52, proteinPer100g = 11, carbsPer100g = 1, fatPer100g = 0),
            FoodTemplate(name = "Pork Chop, cooked", caloriesPer100g = 231, proteinPer100g = 26, carbsPer100g = 0, fatPer100g = 13),
            FoodTemplate(name = "Pork Sausage, cooked", caloriesPer100g = 334, proteinPer100g = 20, carbsPer100g = 1, fatPer100g = 27),
            FoodTemplate(name = "Salmon, cooked", caloriesPer100g = 206, proteinPer100g = 22, carbsPer100g = 0, fatPer100g = 13),
            FoodTemplate(name = "Shrimp, cooked", caloriesPer100g = 99, proteinPer100g = 24, carbsPer100g = 0, fatPer100g = 0),
            FoodTemplate(name = "Tofu, firm", caloriesPer100g = 76, proteinPer100g = 8, carbsPer100g = 2, fatPer100g = 5),
            FoodTemplate(name = "Tuna, canned in water", caloriesPer100g = 116, proteinPer100g = 26, carbsPer100g = 0, fatPer100g = 1),
            FoodTemplate(name = "Turkey Breast, cooked", caloriesPer100g = 135, proteinPer100g = 29, carbsPer100g = 0, fatPer100g = 1),

            // Grains, Legumes & Pasta
            FoodTemplate(name = "Barley, cooked", caloriesPer100g = 123, proteinPer100g = 2, carbsPer100g = 28, fatPer100g = 0),
            FoodTemplate(name = "Black Beans, cooked", caloriesPer100g = 132, proteinPer100g = 9, carbsPer100g = 24, fatPer100g = 1),
            FoodTemplate(name = "Bread, white", caloriesPer100g = 265, proteinPer100g = 9, carbsPer100g = 49, fatPer100g = 3),
            FoodTemplate(name = "Bread, whole wheat", caloriesPer100g = 247, proteinPer100g = 13, carbsPer100g = 41, fatPer100g = 4),
            FoodTemplate(name = "Chickpeas, canned", caloriesPer100g = 139, proteinPer100g = 8, carbsPer100g = 27, fatPer100g = 2),
            FoodTemplate(name = "Corn, sweet, cooked", caloriesPer100g = 96, proteinPer100g = 3, carbsPer100g = 21, fatPer100g = 1),
            FoodTemplate(name = "Couscous, cooked", caloriesPer100g = 112, proteinPer100g = 4, carbsPer100g = 23, fatPer100g = 0),
            FoodTemplate(name = "Kidney Beans, cooked", caloriesPer100g = 127, proteinPer100g = 9, carbsPer100g = 23, fatPer100g = 1),
            FoodTemplate(name = "Lentils, cooked", caloriesPer100g = 116, proteinPer100g = 9, carbsPer100g = 20, fatPer100g = 0),
            FoodTemplate(name = "Oats, raw", caloriesPer100g = 389, proteinPer100g = 17, carbsPer100g = 66, fatPer100g = 7),
            FoodTemplate(name = "Pasta, dry", caloriesPer100g = 371, proteinPer100g = 13, carbsPer100g = 75, fatPer100g = 2),
            FoodTemplate(name = "Quinoa, cooked", caloriesPer100g = 120, proteinPer100g = 4, carbsPer100g = 21, fatPer100g = 2),
            FoodTemplate(name = "Rice, brown, cooked", caloriesPer100g = 111, proteinPer100g = 3, carbsPer100g = 23, fatPer100g = 1),
            FoodTemplate(name = "Rice, white, cooked", caloriesPer100g = 130, proteinPer100g = 3, carbsPer100g = 28, fatPer100g = 0),

            // Dairy, Nuts & Seeds
            FoodTemplate(name = "Almonds", caloriesPer100g = 579, proteinPer100g = 21, carbsPer100g = 22, fatPer100g = 49),
            FoodTemplate(name = "Cashews", caloriesPer100g = 553, proteinPer100g = 18, carbsPer100g = 30, fatPer100g = 44),
            FoodTemplate(name = "Cheese, Cheddar", caloriesPer100g = 404, proteinPer100g = 25, carbsPer100g = 1, fatPer100g = 33),
            FoodTemplate(name = "Cheese, Cottage", caloriesPer100g = 98, proteinPer100g = 11, carbsPer100g = 3, fatPer100g = 4),
            FoodTemplate(name = "Cheese, Cream", caloriesPer100g = 342, proteinPer100g = 6, carbsPer100g = 4, fatPer100g = 34),
            FoodTemplate(name = "Cheese, Mozzarella", caloriesPer100g = 280, proteinPer100g = 28, carbsPer100g = 3, fatPer100g = 17),
            FoodTemplate(name = "Cheese, Parmesan", caloriesPer100g = 431, proteinPer100g = 38, carbsPer100g = 4, fatPer100g = 29),
            FoodTemplate(name = "Chia Seeds", caloriesPer100g = 486, proteinPer100g = 17, carbsPer100g = 42, fatPer100g = 31),
            FoodTemplate(name = "Greek Yogurt, plain, non-fat", caloriesPer100g = 59, proteinPer100g = 10, carbsPer100g = 4, fatPer100g = 0),
            FoodTemplate(name = "Milk, whole", caloriesPer100g = 61, proteinPer100g = 3, carbsPer100g = 5, fatPer100g = 3),
            FoodTemplate(name = "Peanut Butter", caloriesPer100g = 588, proteinPer100g = 25, carbsPer100g = 20, fatPer100g = 50),
            FoodTemplate(name = "Pecans", caloriesPer100g = 691, proteinPer100g = 9, carbsPer100g = 14, fatPer100g = 72),
            FoodTemplate(name = "Walnuts", caloriesPer100g = 654, proteinPer100g = 15, carbsPer100g = 14, fatPer100g = 65),
            FoodTemplate(name = "Whey Protein Powder", caloriesPer100g = 375, proteinPer100g = 80, carbsPer100g = 7, fatPer100g = 3),


            // --- NEWLY ADDED OILS & FATS SECTION ---
            FoodTemplate(name = "Avocado Oil", caloriesPer100g = 884, proteinPer100g = 0, carbsPer100g = 0, fatPer100g = 100),
            FoodTemplate(name = "Butter, salted", caloriesPer100g = 717, proteinPer100g = 1, carbsPer100g = 0, fatPer100g = 81),
            FoodTemplate(name = "Canola Oil", caloriesPer100g = 884, proteinPer100g = 0, carbsPer100g = 0, fatPer100g = 100),
            FoodTemplate(name = "Coconut Oil", caloriesPer100g = 892, proteinPer100g = 0, carbsPer100g = 0, fatPer100g = 99),
            FoodTemplate(name = "Corn Oil", caloriesPer100g = 884, proteinPer100g = 0, carbsPer100g = 0, fatPer100g = 100),
            FoodTemplate(name = "Flaxseed Oil", caloriesPer100g = 884, proteinPer100g = 0, carbsPer100g = 0, fatPer100g = 100),
            FoodTemplate(name = "Ghee", caloriesPer100g = 900, proteinPer100g = 0, carbsPer100g = 0, fatPer100g = 100),
            FoodTemplate(name = "Lard", caloriesPer100g = 902, proteinPer100g = 0, carbsPer100g = 0, fatPer100g = 100),
            FoodTemplate(name = "Margarine", caloriesPer100g = 717, proteinPer100g = 1, carbsPer100g = 1, fatPer100g = 81),
            FoodTemplate(name = "Olive Oil", caloriesPer100g = 884, proteinPer100g = 0, carbsPer100g = 0, fatPer100g = 100),
            FoodTemplate(name = "Sesame Oil", caloriesPer100g = 884, proteinPer100g = 0, carbsPer100g = 0, fatPer100g = 100),
            FoodTemplate(name = "Sunflower Oil", caloriesPer100g = 884, proteinPer100g = 0, carbsPer100g = 0, fatPer100g = 100),


            // Beverages
            FoodTemplate(name = "Coffee, black", caloriesPer100g = 1, proteinPer100g = 0, carbsPer100g = 0, fatPer100g = 0),
            FoodTemplate(name = "Juice, Apple", caloriesPer100g = 46, proteinPer100g = 0, carbsPer100g = 11, fatPer100g = 0),
            FoodTemplate(name = "Juice, Orange", caloriesPer100g = 45, proteinPer100g = 1, carbsPer100g = 10, fatPer100g = 0),

            // Snacks, Sweets & Condiments
            FoodTemplate(name = "Chocolate, Dark (70-85%)", caloriesPer100g = 598, proteinPer100g = 8, carbsPer100g = 46, fatPer100g = 43),
            FoodTemplate(name = "Honey", caloriesPer100g = 304, proteinPer100g = 0, carbsPer100g = 82, fatPer100g = 0),
            FoodTemplate(name = "Ketchup", caloriesPer100g = 112, proteinPer100g = 2, carbsPer100g = 25, fatPer100g = 0),
            FoodTemplate(name = "Mayonnaise", caloriesPer100g = 724, proteinPer100g = 1, carbsPer100g = 1, fatPer100g = 79),
            FoodTemplate(name = "Mustard, yellow", caloriesPer100g = 143, proteinPer100g = 8, carbsPer100g = 10, fatPer100g = 8),
            FoodTemplate(name = "Potato Chips, salted", caloriesPer100g = 536, proteinPer100g = 7, carbsPer100g = 53, fatPer100g = 35),
            FoodTemplate(name = "Sugar, white", caloriesPer100g = 387, proteinPer100g = 0, carbsPer100g = 100, fatPer100g = 0),

                    // Additional Grains, Legumes & Cereals
        FoodTemplate(name = "Amaranth, cooked", caloriesPer100g = 102, proteinPer100g = 3, carbsPer100g = 19, fatPer100g = 2),
        FoodTemplate(name = "Farro, cooked", caloriesPer100g = 125, proteinPer100g = 4, carbsPer100g = 26, fatPer100g = 1),
        FoodTemplate(name = "Millet, cooked", caloriesPer100g = 119, proteinPer100g = 3, carbsPer100g = 23, fatPer100g = 1),
        FoodTemplate(name = "Teff, cooked", caloriesPer100g = 101, proteinPer100g = 4, carbsPer100g = 20, fatPer100g = 1),
        FoodTemplate(name = "Buckwheat, cooked", caloriesPer100g = 92, proteinPer100g = 3, carbsPer100g = 20, fatPer100g = 1),
        FoodTemplate(name = "Bulgur, cooked", caloriesPer100g = 83, proteinPer100g = 3, carbsPer100g = 19, fatPer100g = 0),

// More Fruits
        FoodTemplate(name = "Papaya", caloriesPer100g = 43, proteinPer100g = 1, carbsPer100g = 11, fatPer100g = 0),
        FoodTemplate(name = "Passion Fruit", caloriesPer100g = 97, proteinPer100g = 2, carbsPer100g = 23, fatPer100g = 1),
        FoodTemplate(name = "Pomegranate", caloriesPer100g = 83, proteinPer100g = 1, carbsPer100g = 19, fatPer100g = 1),
        FoodTemplate(name = "Guava", caloriesPer100g = 68, proteinPer100g = 2, carbsPer100g = 14, fatPer100g = 1),
        FoodTemplate(name = "Lychee", caloriesPer100g = 66, proteinPer100g = 1, carbsPer100g = 17, fatPer100g = 0),
        FoodTemplate(name = "Cranberries", caloriesPer100g = 46, proteinPer100g = 0, carbsPer100g = 12, fatPer100g = 0),

// More Dairy
        FoodTemplate(name = "Yogurt, plain, low-fat", caloriesPer100g = 63, proteinPer100g = 5, carbsPer100g = 7, fatPer100g = 1),
        FoodTemplate(name = "Milk, skim", caloriesPer100g = 35, proteinPer100g = 3, carbsPer100g = 5, fatPer100g = 0),
        FoodTemplate(name = "Milk, 2%", caloriesPer100g = 50, proteinPer100g = 3, carbsPer100g = 5, fatPer100g = 2),
        FoodTemplate(name = "Ricotta Cheese", caloriesPer100g = 174, proteinPer100g = 11, carbsPer100g = 3, fatPer100g = 13),

// More Meats
        FoodTemplate(name = "Duck, cooked", caloriesPer100g = 337, proteinPer100g = 27, carbsPer100g = 0, fatPer100g = 25),
        FoodTemplate(name = "Lamb, cooked", caloriesPer100g = 294, proteinPer100g = 25, carbsPer100g = 0, fatPer100g = 21),
        FoodTemplate(name = "Venison, cooked", caloriesPer100g = 158, proteinPer100g = 30, carbsPer100g = 0, fatPer100g = 3),

// More Fish
        FoodTemplate(name = "Halibut, cooked", caloriesPer100g = 140, proteinPer100g = 27, carbsPer100g = 0, fatPer100g = 3),
        FoodTemplate(name = "Sardines, canned in oil", caloriesPer100g = 208, proteinPer100g = 25, carbsPer100g = 0, fatPer100g = 11),
        FoodTemplate(name = "Mackerel, cooked", caloriesPer100g = 262, proteinPer100g = 23, carbsPer100g = 0, fatPer100g = 18),

// More Snacks / Spreads
        FoodTemplate(name = "Nutella", caloriesPer100g = 539, proteinPer100g = 6, carbsPer100g = 57, fatPer100g = 31),
        FoodTemplate(name = "Granola Bar", caloriesPer100g = 471, proteinPer100g = 8, carbsPer100g = 64, fatPer100g = 21),
        FoodTemplate(name = "Trail Mix", caloriesPer100g = 462, proteinPer100g = 12, carbsPer100g = 43, fatPer100g = 27),

// More Oils & Fats
        FoodTemplate(name = "Palm Oil", caloriesPer100g = 884, proteinPer100g = 0, carbsPer100g = 0, fatPer100g = 100),
        FoodTemplate(name = "Grapeseed Oil", caloriesPer100g = 884, proteinPer100g = 0, carbsPer100g = 0, fatPer100g = 100),

// More Beverages
        FoodTemplate(name = "Tea, unsweetened", caloriesPer100g = 1, proteinPer100g = 0, carbsPer100g = 0, fatPer100g = 0),
        FoodTemplate(name = "Cola, regular", caloriesPer100g = 42, proteinPer100g = 0, carbsPer100g = 11, fatPer100g = 0),
        FoodTemplate(name = "Beer, regular", caloriesPer100g = 43, proteinPer100g = 0, carbsPer100g = 4, fatPer100g = 0),
        FoodTemplate(name = "Red Wine", caloriesPer100g = 85, proteinPer100g = 0, carbsPer100g = 3, fatPer100g = 0),

        //GERMAN FOODS
        FoodTemplate(name = "Apfel", caloriesPer100g = 52, proteinPer100g = 0, carbsPer100g = 14, fatPer100g = 0),
        FoodTemplate(name = "Avocado", caloriesPer100g = 160, proteinPer100g = 2, carbsPer100g = 9, fatPer100g = 15),
        FoodTemplate(name = "Banane", caloriesPer100g = 89, proteinPer100g = 1, carbsPer100g = 23, fatPer100g = 0),
        FoodTemplate(name = "Blaubeeren", caloriesPer100g = 57, proteinPer100g = 1, carbsPer100g = 14, fatPer100g = 0),
        FoodTemplate(name = "Kirschen", caloriesPer100g = 50, proteinPer100g = 1, carbsPer100g = 12, fatPer100g = 0),
        FoodTemplate(name = "Trauben", caloriesPer100g = 69, proteinPer100g = 1, carbsPer100g = 18, fatPer100g = 0),
        FoodTemplate(name = "Kiwi", caloriesPer100g = 61, proteinPer100g = 1, carbsPer100g = 15, fatPer100g = 1),
        FoodTemplate(name = "Zitrone", caloriesPer100g = 29, proteinPer100g = 1, carbsPer100g = 9, fatPer100g = 0),
        FoodTemplate(name = "Mango", caloriesPer100g = 60, proteinPer100g = 1, carbsPer100g = 15, fatPer100g = 0),
        FoodTemplate(name = "Orange", caloriesPer100g = 47, proteinPer100g = 1, carbsPer100g = 12, fatPer100g = 0),
        FoodTemplate(name = "Pfirsich", caloriesPer100g = 39, proteinPer100g = 1, carbsPer100g = 10, fatPer100g = 0),
        FoodTemplate(name = "Ananas", caloriesPer100g = 50, proteinPer100g = 1, carbsPer100g = 13, fatPer100g = 0),
        FoodTemplate(name = "Himbeeren", caloriesPer100g = 52, proteinPer100g = 1, carbsPer100g = 12, fatPer100g = 1),
        FoodTemplate(name = "Erdbeeren", caloriesPer100g = 32, proteinPer100g = 1, carbsPer100g = 8, fatPer100g = 0),
        FoodTemplate(name = "Wassermelone", caloriesPer100g = 30, proteinPer100g = 1, carbsPer100g = 8, fatPer100g = 0),
        FoodTemplate(name = "Papaya", caloriesPer100g = 43, proteinPer100g = 1, carbsPer100g = 11, fatPer100g = 0),
        FoodTemplate(name = "Passionsfrucht", caloriesPer100g = 97, proteinPer100g = 2, carbsPer100g = 23, fatPer100g = 1),
        FoodTemplate(name = "Granatapfel", caloriesPer100g = 83, proteinPer100g = 1, carbsPer100g = 19, fatPer100g = 1),
        FoodTemplate(name = "Guave", caloriesPer100g = 68, proteinPer100g = 2, carbsPer100g = 14, fatPer100g = 1),
        FoodTemplate(name = "Litschi", caloriesPer100g = 66, proteinPer100g = 1, carbsPer100g = 17, fatPer100g = 0),
        FoodTemplate(name = "Preiselbeeren", caloriesPer100g = 46, proteinPer100g = 0, carbsPer100g = 12, fatPer100g = 0),
            // Gemüse
            FoodTemplate(name = "Spargel, gekocht", caloriesPer100g = 20, proteinPer100g = 2, carbsPer100g = 4, fatPer100g = 0),
            FoodTemplate(name = "Rote Paprika, roh", caloriesPer100g = 31, proteinPer100g = 1, carbsPer100g = 6, fatPer100g = 0),
            FoodTemplate(name = "Brokkoli, roh", caloriesPer100g = 34, proteinPer100g = 3, carbsPer100g = 7, fatPer100g = 0),
            FoodTemplate(name = "Karotte, roh", caloriesPer100g = 41, proteinPer100g = 1, carbsPer100g = 10, fatPer100g = 0),
            FoodTemplate(name = "Blumenkohl, roh", caloriesPer100g = 25, proteinPer100g = 2, carbsPer100g = 5, fatPer100g = 0),
            FoodTemplate(name = "Sellerie, roh", caloriesPer100g = 16, proteinPer100g = 1, carbsPer100g = 3, fatPer100g = 0),
            FoodTemplate(name = "Gurke", caloriesPer100g = 15, proteinPer100g = 1, carbsPer100g = 4, fatPer100g = 0),
            FoodTemplate(name = "Knoblauch, roh", caloriesPer100g = 149, proteinPer100g = 6, carbsPer100g = 33, fatPer100g = 1),
            FoodTemplate(name = "Grüne Bohnen, gekocht", caloriesPer100g = 35, proteinPer100g = 2, carbsPer100g = 8, fatPer100g = 0),
            FoodTemplate(name = "Eisbergsalat", caloriesPer100g = 14, proteinPer100g = 1, carbsPer100g = 3, fatPer100g = 0),
            FoodTemplate(name = "Champignons, roh", caloriesPer100g = 22, proteinPer100g = 3, carbsPer100g = 3, fatPer100g = 0),
            FoodTemplate(name = "Zwiebel, roh", caloriesPer100g = 40, proteinPer100g = 1, carbsPer100g = 9, fatPer100g = 0),
            FoodTemplate(name = "Kartoffel, roh", caloriesPer100g = 77, proteinPer100g = 2, carbsPer100g = 17, fatPer100g = 0),
            FoodTemplate(name = "Spinat, roh", caloriesPer100g = 23, proteinPer100g = 3, carbsPer100g = 4, fatPer100g = 0),
            FoodTemplate(name = "Süßkartoffel, roh", caloriesPer100g = 86, proteinPer100g = 2, carbsPer100g = 20, fatPer100g = 0),
            FoodTemplate(name = "Tomate", caloriesPer100g = 18, proteinPer100g = 1, carbsPer100g = 4, fatPer100g = 0),
            FoodTemplate(name = "Zucchini, roh", caloriesPer100g = 17, proteinPer100g = 1, carbsPer100g = 3, fatPer100g = 0),

            // Fleisch & Fisch
            FoodTemplate(name = "Hähnchenbrust, gekocht", caloriesPer100g = 165, proteinPer100g = 31, carbsPer100g = 0, fatPer100g = 4),
            FoodTemplate(name = "Hähnchenschenkel, gekocht", caloriesPer100g = 209, proteinPer100g = 26, carbsPer100g = 0, fatPer100g = 11),
            FoodTemplate(name = "Rindfleisch, Steak, gekocht", caloriesPer100g = 206, proteinPer100g = 29, carbsPer100g = 0, fatPer100g = 9),
            FoodTemplate(name = "Hackfleisch, Rind (85%), gekocht", caloriesPer100g = 215, proteinPer100g = 26, carbsPer100g = 0, fatPer100g = 15),
            FoodTemplate(name = "Bacon, gekocht", caloriesPer100g = 541, proteinPer100g = 37, carbsPer100g = 1, fatPer100g = 42),
            FoodTemplate(name = "Lachs, gekocht", caloriesPer100g = 206, proteinPer100g = 22, carbsPer100g = 0, fatPer100g = 13),
            FoodTemplate(name = "Garnelen, gekocht", caloriesPer100g = 99, proteinPer100g = 24, carbsPer100g = 0, fatPer100g = 0),
            FoodTemplate(name = "Thunfisch, Dose", caloriesPer100g = 116, proteinPer100g = 26, carbsPer100g = 0, fatPer100g = 1),
            FoodTemplate(name = "Tofu, fest", caloriesPer100g = 76, proteinPer100g = 8, carbsPer100g = 2, fatPer100g = 5),

            // Milchprodukte
            FoodTemplate(name = "Milch, Vollmilch", caloriesPer100g = 61, proteinPer100g = 3, carbsPer100g = 5, fatPer100g = 3),
            FoodTemplate(name = "Milch, fettarm", caloriesPer100g = 50, proteinPer100g = 3, carbsPer100g = 5, fatPer100g = 2),
            FoodTemplate(name = "Joghurt, griechisch, fettfrei", caloriesPer100g = 59, proteinPer100g = 10, carbsPer100g = 4, fatPer100g = 0),
            FoodTemplate(name = "Quark", caloriesPer100g = 98, proteinPer100g = 11, carbsPer100g = 3, fatPer100g = 4),
            FoodTemplate(name = "Käse, Mozzarella", caloriesPer100g = 280, proteinPer100g = 28, carbsPer100g = 3, fatPer100g = 17),
            FoodTemplate(name = "Käse, Parmesan", caloriesPer100g = 431, proteinPer100g = 38, carbsPer100g = 4, fatPer100g = 29),
            FoodTemplate(name = "Käse, Cheddar", caloriesPer100g = 404, proteinPer100g = 25, carbsPer100g = 1, fatPer100g = 33),

            // Getränke
            FoodTemplate(name = "Kaffee, schwarz", caloriesPer100g = 1, proteinPer100g = 0, carbsPer100g = 0, fatPer100g = 0),
            FoodTemplate(name = "Tee, ungesüßt", caloriesPer100g = 1, proteinPer100g = 0, carbsPer100g = 0, fatPer100g = 0),
            FoodTemplate(name = "Orangensaft", caloriesPer100g = 45, proteinPer100g = 1, carbsPer100g = 10, fatPer100g = 0),
            FoodTemplate(name = "Cola", caloriesPer100g = 42, proteinPer100g = 0, carbsPer100g = 11, fatPer100g = 0),
            FoodTemplate(name = "Bier", caloriesPer100g = 43, proteinPer100g = 0, carbsPer100g = 4, fatPer100g = 0),
            FoodTemplate(name = "Rotwein", caloriesPer100g = 85, proteinPer100g = 0, carbsPer100g = 3, fatPer100g = 0),
// Gemüse
            FoodTemplate(name = "Rote Bete", caloriesPer100g = 43, proteinPer100g = 2, carbsPer100g = 10, fatPer100g = 0),
            FoodTemplate(name = "Rosenkohl", caloriesPer100g = 43, proteinPer100g = 3, carbsPer100g = 9, fatPer100g = 0),
            FoodTemplate(name = "Grünkohl", caloriesPer100g = 49, proteinPer100g = 4, carbsPer100g = 9, fatPer100g = 1),
            FoodTemplate(name = "Aubergine", caloriesPer100g = 25, proteinPer100g = 1, carbsPer100g = 6, fatPer100g = 0),
            FoodTemplate(name = "Zwiebel", caloriesPer100g = 40, proteinPer100g = 1, carbsPer100g = 9, fatPer100g = 0),
            FoodTemplate(name = "Rettich", caloriesPer100g = 16, proteinPer100g = 1, carbsPer100g = 3, fatPer100g = 0),

// Fleisch & Fisch
            FoodTemplate(name = "Hackfleisch (Rind)", caloriesPer100g = 250, proteinPer100g = 26, carbsPer100g = 0, fatPer100g = 18),
            FoodTemplate(name = "Leberkäse", caloriesPer100g = 304, proteinPer100g = 14, carbsPer100g = 3, fatPer100g = 27),
            FoodTemplate(name = "Bratwurst", caloriesPer100g = 297, proteinPer100g = 12, carbsPer100g = 2, fatPer100g = 27),
            FoodTemplate(name = "Forelle, gekocht", caloriesPer100g = 148, proteinPer100g = 20, carbsPer100g = 0, fatPer100g = 7),
            FoodTemplate(name = "Kabeljau, gekocht", caloriesPer100g = 105, proteinPer100g = 23, carbsPer100g = 0, fatPer100g = 1),

// Milchprodukte
            FoodTemplate(name = "Buttermilch", caloriesPer100g = 35, proteinPer100g = 3, carbsPer100g = 4, fatPer100g = 1),
            FoodTemplate(name = "Schmand", caloriesPer100g = 261, proteinPer100g = 3, carbsPer100g = 3, fatPer100g = 26),
            FoodTemplate(name = "Quark, mager", caloriesPer100g = 67, proteinPer100g = 12, carbsPer100g = 4, fatPer100g = 0),
            FoodTemplate(name = "Schlagsahne", caloriesPer100g = 292, proteinPer100g = 2, carbsPer100g = 3, fatPer100g = 30),

// Brot & Getreide
            FoodTemplate(name = "Vollkornbrot", caloriesPer100g = 247, proteinPer100g = 9, carbsPer100g = 41, fatPer100g = 3),
            FoodTemplate(name = "Roggenbrot", caloriesPer100g = 259, proteinPer100g = 8, carbsPer100g = 48, fatPer100g = 3),
            FoodTemplate(name = "Knäckebrot", caloriesPer100g = 330, proteinPer100g = 10, carbsPer100g = 70, fatPer100g = 1),
            FoodTemplate(name = "Haferflocken", caloriesPer100g = 389, proteinPer100g = 13, carbsPer100g = 66, fatPer100g = 7),

// Getränke
            FoodTemplate(name = "Apfelsaft", caloriesPer100g = 46, proteinPer100g = 0, carbsPer100g = 11, fatPer100g = 0),
            FoodTemplate(name = "Orangensaft", caloriesPer100g = 45, proteinPer100g = 1, carbsPer100g = 10, fatPer100g = 0),
            FoodTemplate(name = "Schwarztee", caloriesPer100g = 1, proteinPer100g = 0, carbsPer100g = 0, fatPer100g = 0),
            FoodTemplate(name = "Rotwein", caloriesPer100g = 85, proteinPer100g = 0, carbsPer100g = 3, fatPer100g = 0),
            FoodTemplate(name = "Weißwein", caloriesPer100g = 82, proteinPer100g = 0, carbsPer100g = 2, fatPer100g = 0),
            FoodTemplate(name = "Bier", caloriesPer100g = 43, proteinPer100g = 0, carbsPer100g = 4, fatPer100g = 0),

// Süßes & Snacks
            FoodTemplate(name = "Milchschokolade", caloriesPer100g = 535, proteinPer100g = 7, carbsPer100g = 59, fatPer100g = 30),
            FoodTemplate(name = "Gummibärchen", caloriesPer100g = 343, proteinPer100g = 7, carbsPer100g = 77, fatPer100g = 0),
            FoodTemplate(name = "Kartoffelchips", caloriesPer100g = 536, proteinPer100g = 7, carbsPer100g = 53, fatPer100g = 35),
            FoodTemplate(name = "Studentenfutter", caloriesPer100g = 462, proteinPer100g = 12, carbsPer100g = 43, fatPer100g = 27)


            )
    }
}