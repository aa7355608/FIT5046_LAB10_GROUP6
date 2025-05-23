package com.example.healthreminderapp

import android.content.Context
import java.io.DataInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.exp

class SimpleNeuralNetwork(context: Context) {
    private var weights: FloatArray? = null
    private var inputLayer = 1
    private var hiddenLayer = 8
    private var outputLayer = 3
    
    init {
        try {
            context.assets.open("workout_model.tflite").use { inputStream ->
                val dis = DataInputStream(inputStream)
                val numWeights = dis.readInt()
                weights = FloatArray(numWeights)
                for (i in 0 until numWeights) {
                    weights!![i] = dis.readFloat()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun relu(x: Float): Float {
        return if (x > 0) x else 0f
    }

    private fun softmax(x: FloatArray): FloatArray {
        val max = x.maxOrNull() ?: 0f
        val exp = x.map { exp((it - max).toDouble()).toFloat() }
        val sum = exp.sum()
        return exp.map { it / sum }.toFloatArray()
    }

    fun predict(input: Float): FloatArray {
        if (weights == null) return floatArrayOf(0.33f, 0.33f, 0.34f)

        var offset = 0
        
        // 第一层权重和偏置
        val hidden = FloatArray(hiddenLayer)
        for (i in 0 until hiddenLayer) {
            hidden[i] = weights!![offset + i] * input + weights!![offset + hiddenLayer * inputLayer + i]
            hidden[i] = relu(hidden[i])
        }
        offset += hiddenLayer * inputLayer + hiddenLayer

        // 输出层
        val output = FloatArray(outputLayer)
        for (i in 0 until outputLayer) {
            for (j in 0 until hiddenLayer) {
                output[i] += weights!![offset + i * hiddenLayer + j] * hidden[j]
            }
            output[i] += weights!![offset + outputLayer * hiddenLayer + i]
        }

        return softmax(output)
    }
} 