package com.example.firsttrial.SignalProcessing

class Dekomposisi {
    val h: DoubleArray = DoubleArray(4)
    val g: DoubleArray = DoubleArray(4)

    init {
        h[0] = (1 + Math.sqrt(3.0)) / (4 * Math.sqrt(2.0))
        h[1] = (3 + Math.sqrt(3.0)) / (4 * Math.sqrt(2.0))
        h[2] = (3 - Math.sqrt(3.0)) / (4 * Math.sqrt(2.0))
        h[3] = (1 - Math.sqrt(3.0)) / (4 * Math.sqrt(2.0))

        for (i in 0 until 4) {
            g[i] = Math.pow(-1.0, i.toDouble()) * h[3 - i]
        }
    }


    fun dekomposisi(div: Int, sinyalAwal : List<Float>, sinyal: List<Float>): Pair<List<Float>, List<Float>> {
        val ndat = sinyalAwal.size
        val a = DoubleArray(ndat / div)
        val d = DoubleArray(ndat / div)

        for (i in 1 until ndat / div) {
            a[i] = 0.0
            d[i] = 0.0
            for (j in 0..3) {
                if (2 * i + j < sinyal.size) {
                    a[i] += h[j] * sinyal[2 * i + j]
                    d[i] += g[j] * sinyal[2 * i + j]
                }
            }
        }

        return Pair(a.map { it.toFloat() }, d.map { it.toFloat() })
    }


}