package com.example.healthreminderapp

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

/** 所有与云端打交道的集中入口 */
object HealthRepository {

    private val db by lazy { FirebaseFirestore.getInstance() }
    private val auth by lazy { FirebaseAuth.getInstance() }

    /* ---------- Meals ---------- */

    /** Recommended-Meals 列表；返回 [name] 字段 */
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
            emptyList()   // 网络失败就返回空，不让 App 崩溃
        }
    }

    /* ---------- Food-Rankings ---------- */

    /** 食物排行：返回「名称 + Emoji」列表 */
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

    /** 读取当前用户最近 30 次记录（kg） */
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

    /** 保存一条体重；写成功后静默返回 */
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
