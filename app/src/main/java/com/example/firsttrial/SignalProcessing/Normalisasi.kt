package com.example.firsttrial.SignalProcessing

class Normalisasi {
    fun max(sinyal: FloatArray): Float {
        var S = 0f
        for (i in sinyal.indices) {
            if (S < sinyal[i]) {
                S = sinyal[i]
            }
        }
        return S
    }

    fun normalize_mav(mav: FloatArray): FloatArray {
        val max_mav = max(mav)
        val normmav = FloatArray(mav.size)
        for (i in mav.indices) {
            normmav[i] = mav[i] / max_mav
        }
        return normmav
    }
}