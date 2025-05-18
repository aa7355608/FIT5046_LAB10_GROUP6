package com.example.healthreminderapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/* 数据模型 */
private data class FoodRank(
    val rank:  Int,
    val name:  String,
    val score: Int        // Health score 0–100
)

/* 20 条静态排行榜 */
private val rankings = listOf(
    FoodRank(1, "Steamed Broccoli",          99),
    FoodRank(2, "Baked Salmon",              97),
    FoodRank(3, "Greek Yogurt (non-fat)",    95),
    FoodRank(4, "Quinoa",                    93),
    FoodRank(5, "Avocado",                   91),
    FoodRank(6, "Lentil Soup",               89),
    FoodRank(7, "Grilled Chicken Breast",    88),
    FoodRank(8, "Oatmeal Porridge",          86),
    FoodRank(9, "Blueberries",               85),
    FoodRank(10,"Almonds (30 g)",            84),
    FoodRank(11,"Roasted Sweet Potato",      83),
    FoodRank(12,"Chickpea Salad",            82),
    FoodRank(13,"Tuna Steak (seared)",       81),
    FoodRank(14,"Edamame",                   80),
    FoodRank(15,"Brown Rice (cooked)",       78),
    FoodRank(16,"Apple",                     77),
    FoodRank(17,"Cottage Cheese (low-fat)",  75),
    FoodRank(18,"Dark Chocolate 85 %",       72),
    FoodRank(19,"Whole-Wheat Pasta",         70),
    FoodRank(20,"Protein Mug Cake",          68)
)

/* -------- UI -------- */
@Composable
fun FoodRankingsScreen() {
    LazyColumn(
        contentPadding      = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(rankings) { item ->
            RankCard(item)
        }
    }
}

@Composable
private fun RankCard(item: FoodRank) {
    Card(
        modifier  = Modifier
            .fillMaxWidth()
            .heightIn(min = 80.dp),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            /* 星型图标 + 排名数字 */
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(Icons.Default.Star, null, modifier = Modifier.matchParentSize())
                Text(
                    item.rank.toString(),
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            /* 食品名称与得分 */
            Column {
                Text(
                    item.name,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(Modifier.height(4.dp))
                Text("Health score: ${item.score}/100")
            }
        }
    }
}

