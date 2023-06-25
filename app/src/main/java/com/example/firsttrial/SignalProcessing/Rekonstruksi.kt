package com.example.firsttrial.SignalProcessing

class Rekonstruksi {
    val ih: DoubleArray = DoubleArray(4)
    val ig: DoubleArray = DoubleArray(4)

    init{
        ih[0] = (1 - Math.sqrt(3.0)) / (4 * Math.sqrt(2.0))
        ih[1] = (3 - Math.sqrt(3.0)) / (4 * Math.sqrt(2.0))
        ih[2] = (3 + Math.sqrt(3.0)) / (4 * Math.sqrt(2.0))
        ih[3] = (1 + Math.sqrt(3.0)) / (4 * Math.sqrt(2.0))

        for (i in 0..3) {
            ig[i] = Math.pow(-1.0, i.toDouble()) * ih[3 - i]
        }
    }

    fun rekonstruksi(a: List<Float>, d: List<Float>): FloatArray {
        val sinyal = DoubleArray(a.size * 2)

        for (i in a.indices) {
            for (j in 0..3) {
                if (2 * i + j < sinyal.size) {
                    sinyal[2 * i + j] += ih[j] * a[i] + ig[j] * d[i]
                }
            }
        }

        return sinyal.map { it.toFloat() }.toFloatArray()
    }

}