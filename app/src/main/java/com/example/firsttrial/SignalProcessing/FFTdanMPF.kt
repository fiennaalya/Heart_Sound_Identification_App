package com.example.firsttrial.SignalProcessing

import android.widget.TextView
import org.jtransforms.fft.FloatFFT_1D
import kotlin.math.abs

class FFTdanMPF {
   fun fft(sinyal: FloatArray, fs: Float, textMPF: TextView): Pair<FloatArray, FloatArray> {
        val stringBuilderMPF = StringBuilder()
        val fft = FloatFFT_1D(sinyal.size.toLong())
        val spectrum = FloatArray(sinyal.size * 2)

        val maxValue = sinyal.maxOrNull() ?: 1f
        val minValue = sinyal.minOrNull() ?: 0f
        val range = maxValue - minValue

        for (i in sinyal.indices) {
            spectrum[2 * i] = (sinyal[i] - minValue) / range
            spectrum[2 * i + 1] = 0f
        }

        fft.realForward(spectrum)

        val freq = FloatArray(sinyal.size)
        val nyquist = fs / 2f
        val deltaFreq = fs / sinyal.size

        for (i in freq.indices) {
            freq[i] = i * deltaFreq
            if (freq[i] > nyquist) {
                freq[i] -= fs
            }
        }

        val powerSpectrum = FloatArray(sinyal.size)
        for (i in powerSpectrum.indices) {
            val re = spectrum[2 * i]
            val im = spectrum[2 * i + 1]
            powerSpectrum[i] = kotlin.math.sqrt((re * re + im * im).toDouble()).toFloat()
        }

        /*for (i in powerSpectrum.indices) {
            println("Frequency: ${freq[i]} Hz, Power Spectrum: ${powerSpectrum[i]}")
        }*/

        for (i in freq.indices) {
            val frequency = freq[i]
            val magnitude = powerSpectrum[i]
            println("Frequency: $frequency Hz, Magnitude: $magnitude")
        }

        // Compute the Mean Power Frequency (MPF)
        val totalPower = powerSpectrum.sum()
        var sum = 0f
        var mpf = 0f

        for (i in powerSpectrum.indices) {
            sum += freq[i] * powerSpectrum[i]
            mpf = sum / totalPower
        }

        println("Mean Power Frequency (MPF): $mpf Hz")
        stringBuilderMPF.append("Mean Power Frequency (MPF) = ${mpf} Hz")
        textMPF.text = stringBuilderMPF.toString()

        return Pair(freq, powerSpectrum)
    }

    fun fft2(sinyal: FloatArray, fs: Float, textMPF: TextView){
        val sinyalLen = sinyal.size
        val sinyalHalfLen = sinyalLen/2

        //fft of signal
        val fft = FloatFFT_1D(sinyalLen.toLong())
        val transformed = FloatArray(sinyalLen*2)
        System.arraycopy(sinyal, 0, transformed, 0, sinyalLen)
        fft.realForwardFull(transformed)

        //frequency axis
        val freq = FloatArray(sinyalLen){i -> i/(sinyalLen*fs)}

        // Compute the power spectrum
        val powerSpectrum = FloatArray(sinyalLen){ i ->
            val real = transformed[i*2]
            val imag = transformed[i*2 +1]
            abs(real+imag)

        }

        // Compute the Mean Power Frequency (MPF)
        val maxMagnitude = powerSpectrum.take(sinyalHalfLen).maxOrNull() ?: 0.0
        val maxMagnitudeIndex = powerSpectrum.take(sinyalHalfLen).indexOf(maxMagnitude)
        val mpf2 = freq[maxMagnitudeIndex]

        println("Mean Power Frequency (MPF) 2: $mpf2 Hz")
        for (i in 0 until sinyalHalfLen) {
            println("Frequency: ${freq[i]} Hz, Power Spectrum: ${powerSpectrum[i]}")
        }

    }

    fun fft3(sinyal: FloatArray, fs: Float, textMPF: TextView): Pair<FloatArray, FloatArray> {
        val sinyalLen = sinyal.size
        val sinyalHalfLen = sinyalLen / 2

        //fft of signal
        val fft = FloatFFT_1D(sinyalLen.toLong())
        val transformed = FloatArray(sinyalLen * 2)
        System.arraycopy(sinyal, 0, transformed, 0, sinyalLen)
        fft.realForwardFull(transformed)

        //frequency axis
        val freq = FloatArray(sinyalLen) { i -> i / (sinyalLen * fs) }

        // Compute the power spectrum
        val powerSpectrum = FloatArray(sinyalLen) { i ->
            val real = transformed[i * 2]
            val imag = transformed[i * 2 + 1]
            abs(real + imag)
        }

        val x = mutableListOf<Float>()
        val mag = mutableListOf<Float>()
        for (k in 1 until sinyalHalfLen) {
            x.add(freq[k])
            mag.add(powerSpectrum[k])
        }

        // Compute the Mean Power Frequency (MPF)
        val totalPower = mag.sum()
        val sumProduct = x.zip(mag).sumByDouble { (xVal, magVal) -> xVal * magVal.toDouble() }
        val mpf = sumProduct / totalPower

        val maxMagnitude = mag.maxOrNull() ?: 0.0
        val maxMagnitudeIndex = mag.indexOf(maxMagnitude)
        val mpf2 = x[maxMagnitudeIndex]

        println("Mean Power Frequency (MPF) 2: $mpf2 Hz")
        for (i in 0 until sinyalHalfLen) {
            println("Frequency: ${freq[i]} Hz, Power Spectrum: ${powerSpectrum[i]}")
        }

        // Update the UI with MPF value
        val stringBuilderMPF = StringBuilder()
        stringBuilderMPF.append("Mean Power Frequency (MPF) = ${mpf2} Hz")
        textMPF.text = stringBuilderMPF.toString()

        return Pair(x.toFloatArray(), mag.toFloatArray())
    }



}