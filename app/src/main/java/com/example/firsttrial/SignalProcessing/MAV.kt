package com.example.firsttrial.SignalProcessing
import kotlin.math.abs

class MAV {

    fun abs(sinyal: FloatArray): FloatArray {
        return sinyal.map { abs(it) }.toFloatArray()
    }

    fun mavSinyal(sinyal: FloatArray, orde: Int): FloatArray {
        val n = sinyal.size
        val mav = FloatArray(n)
        val mav2 = FloatArray(n)
        for (k in 0 until n) {
            var mav1 = 0f
            for (m in 0 until orde) {
                val y1 = k - m
                if (y1 < 0) {
                    mav1 += sinyal[0]
                } else {
                    // forward
                    mav1 += sinyal[k - m]
                }
            }
            mav2[k] = mav1 / orde
            // backward
            var mav3 = 0f
            for (m in 0 until orde) {
                if (k + m < n) {
                    mav3 += mav2[k + m]
                }
            }
            mav[k] = mav3 / orde
        }
        return mav
    }
}