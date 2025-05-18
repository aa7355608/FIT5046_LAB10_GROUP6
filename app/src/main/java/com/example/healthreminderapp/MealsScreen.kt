package com.example.healthreminderapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/* ---------- data model ---------- */
private data class MealInfo(
    val category: String,
    val name:     String,
    val kcal:     Int,   // kcal / kg
    val protein:  Int,   // g / kg
    val fat:      Int,   // g / kg
    val carbs:    Int,   // g / kg
    val method:   String // 1–3-step cooking guide
)

/* ---------- 20 recipes with methods ---------- */
private val meals = listOf(
    /* 1. Lean Proteins */
    MealInfo(
        "Lean Proteins","Grilled Chicken Breast",1650,310,36,0,
        "Pound evenly → Brush with olive oil, salt, paprika → Grill 3-4 min per side to 74 °C / 165 °F."
    ),
    MealInfo(
        "Lean Proteins","Turkey Tenderloin",1600,300,35,0,
        "Rub garlic-herb mix → Roast 25 min at 200 °C → Rest 5 min, slice."
    ),
    MealInfo(
        "Lean Proteins","Tofu Stir-fry",760,82,48,90,
        "Press & cube tofu → Sear until golden → Toss with mixed veggies & soy-ginger sauce 3 min."
    ),

    /* 2. Seafood */
    MealInfo(
        "Seafood","Baked Salmon Fillet",2080,206,130,0,
        "Season with salt, pepper, lemon → Bake skin-down 10-12 min at 220 °C → Serve with dill."
    ),
    MealInfo(
        "Seafood","Seared Tuna Steak",1840,230,60,0,
        "Coat with cracked pepper → Sear very hot pan 90 s per side → Rest, slice rare."
    ),
    MealInfo(
        "Seafood","Garlic Shrimp",990,240,14,0,
        "Sauté garlic in olive oil → Add shrimp, chili flakes → Cook until pink ~3 min."
    ),

    /* 3. Whole Grains */
    MealInfo(
        "Whole Grains","Quinoa Pilaf",1200,40,60,210,
        "Rinse quinoa → Simmer 2 parts water 15 min → Fluff with fork, mix herbs & raisins."
    ),
    MealInfo(
        "Whole Grains","Oatmeal Porridge",1570,130,70,270,
        "Boil 3 parts milk/water → Add rolled oats → Simmer 5 min, top with fruit & honey."
    ),

    /* 4. Vegetables */
    MealInfo(
        "Vegetables","Steamed Broccoli",350,29,4,70,
        "Cut florets → Steam 4-5 min until bright green → Drizzle lemon & olive oil."
    ),
    MealInfo(
        "Vegetables","Roasted Sweet Potato",860,15,1,200,
        "Cube with skin → Toss oil, cinnamon → Roast 25 min at 220 °C, turning once."
    ),
    MealInfo(
        "Vegetables","Sautéed Kale",430,35,5,90,
        "Remove stems → Sauté with garlic 2 min → Splash soy sauce, cook 1 min more."
    ),

    /* 5. Fruits */
    MealInfo(
        "Fruits","Fresh Banana",890,9,3,228,
        "Peel and enjoy as a quick carb-rich snack."
    ),
    MealInfo(
        "Fruits","Apple Slices with Peanut Butter",520,3,2,138,
        "Core & slice apple → Spread thin peanut-butter layer on each slice."
    ),

    /* 6. Dairy & Alternatives */
    MealInfo(
        "Dairy","Non-fat Greek Yogurt Parfait",590,100,0,80,
        "Layer yogurt, berries, and granola in a glass → Chill 5 min before serving."
    ),
    MealInfo(
        "Dairy","Cottage Cheese Bowl",850,110,20,50,
        "Top cottage cheese with pineapple chunks & chia seeds → Serve cold."
    ),

    /* 7. Legumes */
    MealInfo(
        "Legumes","Lentil Soup",960,90,6,170,
        "Sauté onion, carrot → Add lentils & broth → Simmer 25 min, finish with cumin."
    ),
    MealInfo(
        "Legumes","Chickpea Salad",1220,90,40,200,
        "Rinse chickpeas → Toss with tomato, cucumber, lemon, parsley & olive oil."
    ),

    /* 8. Nuts & Seeds */
    MealInfo(
        "Nuts & Seeds","Almond Snack Pack",2400,210,190,80,
        "Portion 30 g dry-roasted almonds into a reusable container for on-the-go fuel."
    ),

    /* 9. Healthy Fats */
    MealInfo(
        "Healthy Fats","Avocado Toast",1600,20,150,90,
        "Mash avocado with lime & salt → Spread on toasted whole-grain bread → Add chili flakes."
    ),

    /* 10. Healthy Desserts */
    MealInfo(
        "Healthy Desserts","Dark Chocolate 85 %",2350,70,360,190,
        "Break 20 g squares → Enjoy slowly for a rich antioxidant dessert."
    )
)

/* ---------- UI ---------- */
@Composable
fun MealsScreen() {
    LazyColumn(
        contentPadding      = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        meals.groupBy { it.category }.forEach { (cat, list) ->
            item {
                Text(
                    cat,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
            items(list) { meal -> MealCard(meal) }
        }
    }
}

@Composable
private fun MealCard(meal: MealInfo) {
    Card(
        modifier  = Modifier
            .fillMaxWidth()
            .heightIn(min = 140.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(Icons.Default.Restaurant, null, modifier = Modifier.size(48.dp))
            Column {
                Text(meal.name, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold))
                Spacer(Modifier.height(4.dp))
                Text("Energy  : ${meal.kcal} kcal / kg")
                Text("Protein : ${meal.protein} g / kg")
                Text("Fat     : ${meal.fat} g / kg")
                Text("Carbs   : ${meal.carbs} g / kg")
                Spacer(Modifier.height(6.dp))
                Text("Method: ${meal.method}")
            }
        }
    }
}



