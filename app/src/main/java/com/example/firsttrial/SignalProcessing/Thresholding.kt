package com.example.firsttrial.SignalProcessing

import android.widget.TextView
import kotlin.math.roundToInt

class Thresholding {
    fun threshold(mav: FloatArray, thres: Float): Pair<FloatArray, FloatArray>  {
        val max_value = mav.max() ?: 0f
        val res = FloatArray(mav.size)
        val x_start = mutableListOf<Float>()
        val x_end = mutableListOf<Float>()
        val middle_fix = mutableListOf<Float>()
        val resMiddle = FloatArray(mav.size)
        val dwtM = 16f

        for (i in mav.indices) {
            if ((Math.round(mav[i] * 100) / 100f) > thres * max_value) {
                res[i] = 1f
            } else {
                res[i] = 0f
            }
        }

        for (i in res.indices) {
            if (i == 0 && res[i] == 1f ) {
                x_start.add(i.toFloat())
            } else if (i == res.indices.last && res[i] == 1f) {
                x_end.add(i.toFloat())
            } else if (i > 0 && res[i] == 1f && res[i-1] == 0f && res[i+1] == 1f) {
                x_start.add(i.toFloat())
            } else if (i < res.indices.last && res[i] == 1f && res[i+1] == 0f && res[i-1] == 1f) {
                x_end.add(i.toFloat())
            }
        }

        //println("x_start: ${x_start.map { it / 8000 * 16 }}")
        //println("x_end: ${x_end.map { it / 8000 * 16 }}")
        for(i in x_start.indices){
            val start = x_start[i]
            val end = if (i < x_end.size) x_end[i] else x_start.last()
            val middle = ((start + end) / 2)/8000*dwtM
            val selisih_values = (end-start)/8000*dwtM
            var skip_iteration = false
            //println("x start: ${start / 8000 * 16}, x end: ${end / 8000 * 16}")

            if (i > 0 && i < x_start.indices.last){
                val prev_start = x_start[i-1]
                val prev_end = x_end[i -  1]
                val prev_middle = ((prev_start+prev_end)/2)/8000*dwtM
                val diff = middle - prev_middle

                if (diff < 0.150 && selisih_values < 0.020){
                    skip_iteration = true
                } else if (diff > 0.150 && selisih_values < 0.020){
                    skip_iteration = true
                }
            }

            if (!skip_iteration){
                val middlepoint = middle
                middle_fix.add(middlepoint)
            }

            //println("$middle_fix")

        }

        for (i in mav.indices) {
            val middleFixValue = i.toFloat() / 8000f * dwtM
            if (middle_fix.any { Math.abs(it - middleFixValue) < 0.01f }) {
                resMiddle[i] = 1f
            }
        }

        return Pair(resMiddle, middle_fix.toFloatArray() )

    }

    fun SuaraSkip(middlePoints: FloatArray): FloatArray {
        val middlePointsFinal = mutableListOf<Float>()
        for (i in middlePoints.indices) {
            if (i < middlePoints.indices.last) {
                val difference = middlePoints[i + 1] - middlePoints[i]
                val skipIteration = difference < 0.120
                if (!skipIteration) {
                    middlePointsFinal.add(middlePoints[i])
                    //println("Middle point: ${middlePoints[i]}")
                    //println("")
                } else {
                    //println("Middle point skip: ${middlePoints[i]}")
                    //println(difference)
                }
            } else {
                middlePointsFinal.add(middlePoints[i])
                //println("Middle point: ${middlePoints[i]}")
                //println("")
            }
        }
        return middlePointsFinal.toFloatArray()
    }

    fun detectS1andS2(middlePoints: FloatArray, mav: FloatArray, textS1: TextView, textS2: TextView): Pair<FloatArray, FloatArray> {
        val labels = mutableListOf<String>()
        val middleS1 = mutableListOf<Float>()
        val middleS2 = mutableListOf<Float>()
        val resS1 = FloatArray(mav.size)
        val resS2 = FloatArray(mav.size)
        val dwtM = 16f

        val stringBuilderS1 = StringBuilder()
        val stringBuilderS2 = StringBuilder()

        for (i in middlePoints.indices) {
            if (i < middlePoints.indices.last){
                val difference = middlePoints[i + 1] - middlePoints[i]
                val roundedDifference = (difference * 100).roundToInt() / 100.0 // Memutar nilai difference ke 2 angka belakang koma
                if (0.150 <= roundedDifference && roundedDifference <= 0.350) {
                    val label = "s1"
                    labels.add(label)
                    //println("point = ${middlePoints[i]}, label = $label")
                    middleS1.add(middlePoints[i])
                    stringBuilderS1.append("Titik S1 = ${middlePoints[i]}\n")
                } else if (0.350 <= roundedDifference && roundedDifference <= 0.800) {
                    val label = "s2"
                    labels.add(label)
                    //println("point = ${middlePoints[i]}, label = $label")
                    middleS2.add(middlePoints[i])
                    stringBuilderS2.append("Titik S2 = ${middlePoints[i]}\n")
                } else {
                    val label = "skip"
                    labels.add(label)
                    //println("point = ${middlePoints[i]}, label = $label")
                }
            }
        }

        val labelLast = if (labels.last() == "s1") {
            middleS2.add(middlePoints.last())
            stringBuilderS2.append("Titik S2 = ${middlePoints.last()}\n")
            "s2"
        } else {
            middleS1.add(middlePoints.last())
            stringBuilderS1.append("Titik S1 = ${middlePoints.last()}\n")
            "s1"
        }
        labels.add(labelLast)
        //println("point = ${middlePoints.last()}, label = $labelLast")

        //println(middleS1)
        //println(middleS2)

        for (i in mav.indices) {
            val middleFixValue = i.toFloat() / 8000f * dwtM
            if (middleS1.any { Math.abs(it - middleFixValue) < 0.01f }) {
                resS1[i] = 1f
            } else if (middleS2.any() { Math.abs(it - middleFixValue) < 0.01f}){
                resS2[i] = 1f
            }
        }

        textS1.text = stringBuilderS1.toString()
        textS2.text = stringBuilderS2.toString()

        return Pair(resS1, resS2)

    }


}