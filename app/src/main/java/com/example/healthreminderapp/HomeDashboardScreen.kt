package com.example.healthreminderapp

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

/** Quick–entry card model */
private data class Feature(val title: String, val icon: ImageVector, val route: String)
private data class QA(val question: String, val answer: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeDashboardScreen(navController: NavHostController) {

    /* ---------- UI state ---------- */
    var search by remember { mutableStateOf("") }

    val features = listOf(
        Feature("Recommended Meals", Icons.Default.RestaurantMenu, "meals"),
        Feature("Food Rankings",     Icons.Default.EmojiEvents,    "foodRankings"),
        Feature("Weight Test",       Icons.Default.Scale,          "weightTest")
    )

    val qaList = listOf(
        QA(
            "Does drinking coffee affect fat loss?",
            "Black coffee is extremely low in calories and can aid alertness and fat oxidation in moderation."
        ),
        QA(
            "Do you have to walk 10,000 steps every day to lose weight?",
            "The key is total energy expenditure; step count is only a reference indicator."
        ),
        QA(
            "Is it healthy to eat only fruit for dinner?",
            "Fruit is high in sugar and low in protein; eating only fruit long-term can lead to nutrient imbalance."
        ),
        QA(
            "Which is better in milk tea—oat milk or coconut milk?",
            "Coconut milk is slightly lower in calories, but it’s still best to reduce sugar and avoid heavy cream tops."
        )
    )

    /* NOTE: The top bar is provided by BottomNavigationScreen’s Scaffold.
       This Scaffold only supplies content. */
    Scaffold { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            /* ---- Welcome & search box ---- */
            item {
                Text(
                    "Welcome to Health Dashboard",
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "Search foods, check nutrition, or explore your fitness journey…",
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                )
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = search,
                    onValueChange = { search = it },
                    placeholder   = { Text("Search foods…") },
                    leadingIcon   = { Icon(Icons.Default.Search, null) },
                    modifier      = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape      = RoundedCornerShape(24.dp),
                    singleLine = true,
                    colors     = TextFieldDefaults.outlinedTextFieldColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                )
            }

            /* ---- Feature cards ---- */
            item {
                Row(Modifier.fillMaxWidth()) {
                    features.forEach { feat ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .weight(1f)
                                .padding(8.dp)
                                .clickable { navController.navigate(feat.route) }
                        ) {
                            Card(
                                shape     = RoundedCornerShape(12.dp),
                                elevation = CardDefaults.cardElevation(4.dp),
                                modifier  = Modifier.size(64.dp),
                                colors    = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer
                                )
                            ) {
                                Box(Modifier.fillMaxSize(), Alignment.Center) {
                                    Icon(
                                        feat.icon,
                                        contentDescription = feat.title,
                                        modifier = Modifier.size(32.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                            Spacer(Modifier.height(6.dp))
                            Text(
                                feat.title,
                                style = MaterialTheme.typography.bodySmall,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            /* ---- Daily Q&A header ---- */
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.secondaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.ChatBubble,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Text("Daily Q&A", style = MaterialTheme.typography.titleMedium)
                }
            }

            /* ---- Q&A list ---- */
            items(qaList) { qa ->
                Column(Modifier.fillMaxWidth()) {
                    Text("Q: ${qa.question}", style = MaterialTheme.typography.bodyLarge)
                    Spacer(Modifier.height(4.dp))
                    Text("A: ${qa.answer}", color = Color.Gray)
                    Spacer(Modifier.height(12.dp))
                    Divider()
                }
            }
        }
    }
}
