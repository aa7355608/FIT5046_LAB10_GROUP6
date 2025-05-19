package com.example.healthreminderapp

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

/** All centralized entry points for dealing with the cloud */
object HealthRepository {

    private val db by lazy { FirebaseFirestore.getInstance() }
    private val auth by lazy { FirebaseAuth.getInstance() }

    /* ---------- Meals ---------- */

    /** Recommended-Meals list; Return the [name] field */
    suspend fun loadMeals(): List<String> = withContext(Dispatchers.IO) {
        return@withContext try {
            db.collection("meals")
                .orderBy("order", Query.Direction.ASCENDING)
                .limit(20)
                .get()
                .await()
                .documents
                .mapNotNull { it.getString("name") }
        } catch (e: Exception) {
            emptyList()   // If the network fails, it will return to null to prevent the App from crashing
        }
    }

    /* ---------- Food-Rankings ---------- */

    /** Food ranking: Return the list of "Name + Emoji" */
    suspend fun loadFoodRankings(): List<String> = withContext(Dispatchers.IO) {
        return@withContext try {
            db.collection("foodRankings")
                .orderBy("score", Query.Direction.DESCENDING)
                .limit(50)
                .get()
                .await()
                .documents
                .mapNotNull { it.getString("name") }
        } catch (e: Exception) {
            emptyList()
        }
    }

    /* ---------- Weight Tracker ---------- */

    /** Read the current user's most recent 30 records (kg) */
    suspend fun loadWeights(): List<Double> = withContext(Dispatchers.IO) {
        val uid = auth.currentUser?.uid ?: return@withContext emptyList()
        return@withContext try {
            db.collection("weights")
                .whereEqualTo("uid", uid)
                .orderBy("time", Query.Direction.DESCENDING)
                .limit(30)
                .get()
                .await()
                .documents
                .mapNotNull { it.getDouble("value") }
        } catch (e: Exception) {
            emptyList()
        }
    }

    /** Save a weight statement; After writing successfully, return silently */
    suspend fun saveWeight(value: Double) = withContext(Dispatchers.IO) {
        val uid = auth.currentUser?.uid ?: return@withContext
        val data = hashMapOf(
            "uid" to uid,
            "value" to value,
            "time" to Date()
        )
        db.collection("weights").add(data).await()
    }
}
