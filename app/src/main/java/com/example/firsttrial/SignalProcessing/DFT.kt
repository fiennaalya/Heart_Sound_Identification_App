package com.example.firsttrial.SignalProcessing

import android.widget.TextView
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

class DFT {
    fun dft(s1: FloatArray, fs1 : Float){
        val ndata = s1.size
        val sreal = FloatArray(ndata)
        val simaj = FloatArray(ndata)

        for (k in 0 until ndata) {
            for (n in 0 until ndata) {
                sreal.set(k, sreal[k] + (s1[n] * cos(2 * Math.PI * k * n / ndata)).toFloat())
                simaj.set(k, simaj[k] - (s1[n] * sin(2 * Math.PI * k * n / ndata)).toFloat())
            }
        }

        val mag = FloatArray(ndata)
        for (k in 0 until ndata) {
            mag[k] = sqrt(sreal[k].pow(2) + simaj[k].pow(2))
        }

        println("DFT")
        val x = FloatArray(ndata / 2) { k -> (k * fs1) / ndata }
        for (k in 0 until ndata / 2) {
            println("frekuensi : ${x[k]}, magnitude: ${mag[k]}")
        }
    }

    fun dft3(s1: FloatArray, fs1: Float, textMPF:TextView): Pair<FloatArray, FloatArray> {
        val stringBuilderMPF = StringBuilder()
        val ndata = s1.size
        val sreal = FloatArray(ndata)
        val simaj = FloatArray(ndata)

        for (k in 0 until ndata) {
            for (n in 0 until ndata) {
                sreal.set(k, sreal[k] + (s1[n] * cos(2 * Math.PI * k * n / ndata)).toFloat())
                simaj.set(k, simaj[k] - (s1[n] * sin(2 * Math.PI * k * n / ndata)).toFloat())
            }
        }

        val mag = mutableListOf<Float>()
        val x = mutableListOf<Float>()
        for (k in 1 until ndata / 2) {
            x.add((k * fs1) / ndata)
            mag.add(sqrt(sreal[k] * sreal[k] + simaj[k] * simaj[k]))
        }

        // Plotting the magnitude spectrum using bar chart
        // Kode plotting tidak dapat dikonversi langsung ke Kotlin karena tidak ada library yang setara dengan matplotlib di Kotlin
        // Anda perlu menggunakan library plotting yang kompatibel dengan Kotlin untuk melakukan plotting

        // Compute the Mean Power Frequency (MPF)
        val totalPower = mag.sum()
        val sumProduct = x.zip(mag).sumByDouble { (xVal, magVal) -> xVal * magVal.toDouble() }
        val mpf = sumProduct / totalPower

        val maxMagnitude = mag.maxOrNull()!!
        val maxMagnitudeIndex = mag.indexOf(maxMagnitude)
        val mpf2 = x[maxMagnitudeIndex]

        println("DFT")
        println("MPF 1 baru: $mpf")
        println("MPF 2 baru: $mpf2")

        stringBuilderMPF.append("Mean Power Frequency (MPF) = ${mpf2} Hz")
        textMPF.text = stringBuilderMPF.toString()

        return Pair(x.toFloatArray(), mag.toFloatArray())
    }


}