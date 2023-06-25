package com.example.firsttrial.View.AnalisaData

import android.graphics.Color
import android.os.Bundle
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.firsttrial.SignalProcessing.*
import com.example.firsttrial.databinding.ActivityAnalisaBinding
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder

class AnalisaActivity : AppCompatActivity() {

    private lateinit var AnalisaBinding : ActivityAnalisaBinding
    private lateinit var chart1 : LineChart
    private lateinit var chart2 : LineChart
    private lateinit var chart3 : BarChart
    private lateinit var tvTitleReceived : TextView
    private lateinit var scaleGestureDetector: ScaleGestureDetector
    private lateinit var chartGestureListener: ChartGestureListener
    private lateinit var chartGestureListener2: ChartGestureListener2

    companion object {
        const val EXTRA_TITLEAUDIO = "extra_titleAudio"
        const val EXTRA_FILEPATHAUDIO = "extra_filepathAudio"
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        AnalisaBinding = ActivityAnalisaBinding.inflate(layoutInflater)
        setContentView(AnalisaBinding.root)

        supportActionBar?.title = "Analisa Audio"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        bacaData()

        val btnProses = AnalisaBinding.btnProses
        val btnFrek = AnalisaBinding.btnFrekuensi

        btnProses.setOnClickListener(){
            linearEnvelope()
        }

        btnFrek.setOnClickListener(){
            fft()
        }


    }

    private fun bacaData(): FloatArray{
        val chart1 = AnalisaBinding.chart

        val titleAudioAnalisa = intent.getStringExtra(EXTRA_TITLEAUDIO)
        val filepathAnalisa = intent.getStringExtra(EXTRA_FILEPATHAUDIO)

        tvTitleReceived = AnalisaBinding.namaAudioAnalisa
        val text = "$titleAudioAnalisa"
        tvTitleReceived.text = text

        val file =  File(filepathAnalisa)
        val bytes = file.readBytes()

        val buffer = ByteBuffer.wrap(bytes)
        buffer.order(ByteOrder.LITTLE_ENDIAN)

        val samples = FloatArray(bytes.size/4)

        for (i in 0 until bytes.size step 4) {
            samples[i / 4] = buffer.short.toFloat() / 32768f
            buffer.short // melewati data left channel
        }

        plotChart(chart1, samples, "Sinyal Awal")

        return samples

    }

    private fun dekomposisiFix(samples : FloatArray) : Pair<List<Float>, List<Float>> {
        val dekomposisiClass = Dekomposisi()
        val (a1, d1) = dekomposisiClass.dekomposisi(2, samples.toList(), samples.toList())
        val (a2, d2) = dekomposisiClass.dekomposisi(4, samples.toList(), a1)
        val (a3, d3) = dekomposisiClass.dekomposisi(8, samples.toList(),a2)
        val (a4, d4) = dekomposisiClass.dekomposisi(16, samples.toList(),a3)
        val (a5, d5) = dekomposisiClass.dekomposisi(32, samples.toList(),a4)
        return Pair(a5, d5)
    }

    private fun rekonstruksiFix(a : List<Float>, d : List<Float>) : FloatArray {
        val rekonstruksiClass = Rekonstruksi()
        val aRekon = rekonstruksiClass.rekonstruksi(a,d)

        val floatArray = FloatArray(aRekon.size)

        for (i in aRekon.indices) {
            floatArray[i] = aRekon[i].toFloat()
        }

        return floatArray

    }

    private fun linearEnvelope(): FloatArray{
        val chart1 = AnalisaBinding.chart
        val chart2 = AnalisaBinding.chartProcess
        val sample = bacaData()
        val dekomposisi5 = dekomposisiFix(sample)
        val rekonstruksi5 = rekonstruksiFix(dekomposisi5.first, dekomposisi5.second)

        val mav = MAV()
        val absRekon = mav.abs(rekonstruksi5)
        val prosesMavSinyal = mav.mavSinyal(absRekon, 60)

        val norm = Normalisasi()
        val prosesNorm = norm.normalize_mav(prosesMavSinyal)

        val threshold = Thresholding()
        val textS1 = AnalisaBinding.textViewS1
        val textS2 = AnalisaBinding.textViewS2
        val prosesThreshold = threshold.threshold(prosesNorm, 0.4F)
        val middlePoints = prosesThreshold.second
        val middlePointsFinal = threshold.SuaraSkip(middlePoints)
        val (S1, S2)= threshold.detectS1andS2(middlePointsFinal, prosesNorm, textS1, textS2)

        plotChart3Signals(prosesNorm,  S1, S2, "sinyal MAV normalisasi","Sinyal S1", "Sinyal S2")
        return prosesNorm
    }

    private fun fft()/*: Pair<FloatArray, FloatArray> */{
        chart3 = AnalisaBinding.chartFFT
        val textMPF = AnalisaBinding.MPF
        val DFT = DFT()
        val FFT = FFTdanMPF()
        val prosesNorm = linearEnvelope()
        val (frekDFT3, powerDFT3) = DFT.dft3(prosesNorm, 8000f, textMPF)
        plotFFTResult(frekDFT3, powerDFT3, chart3)
    }

    private fun plotChart(chart: LineChart, sinyal : FloatArray, nama : String){
        val entriesSinyal = ArrayList<Entry>()
        for (i in sinyal.indices) {
            entriesSinyal.add(Entry(i.toFloat() / 8000f, sinyal[i]))
        }

        val dataSetSinyal = LineDataSet(entriesSinyal, "$nama")
        dataSetSinyal.color = Color.RED
        dataSetSinyal.setDrawCircles(false)

        val lineDataSinyal = LineData(dataSetSinyal)
        chart.data = lineDataSinyal

        val durationInSecondsSinyal = sinyal.size.toFloat() / 8000f
        //println("Duration in seconds rekon: $durationInSecondsSinyal $nama")

        val xAxis = chart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.labelCount = 7 // Menampilkan 7 label pada sumbu x
        xAxis.setDrawGridLines(false)
        xAxis.axisMinimum = 0f
        xAxis.axisMaximum = durationInSecondsSinyal
        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return String.format("%.1f", value) // Mengatur format angka pada sumbu x
            }
        }

        chart.notifyDataSetChanged()
        chart.invalidate()

        chartGestureListener = ChartGestureListener(chart)
        scaleGestureDetector = ScaleGestureDetector(this, chartGestureListener)

    }

    private fun plotChart3Signals(sinyal1: FloatArray, sinyal2: FloatArray, sinyal3: FloatArray, nama1: String, nama2: String, nama3: String) {
        chart2 = AnalisaBinding.chartProcess
        val toggleButtonS1 = AnalisaBinding.toggleButtonS1
        val toggleButtonS2 = AnalisaBinding.toggleButtonS2

        val entriesSinyal1 = ArrayList<Entry>()
        for (i in sinyal1.indices) {
            entriesSinyal1.add(Entry(i.toFloat() / 8000f * 16, sinyal1[i]))
        }

        val entriesSinyal2 = ArrayList<Entry>()
        for (i in sinyal2.indices) {
            entriesSinyal2.add(Entry(i.toFloat() / 8000f * 16, sinyal2[i]))
        }

        val entriesSinyal3 = ArrayList<Entry>()
        for (i in sinyal3.indices) {
            entriesSinyal3.add(Entry(i.toFloat() / 8000f * 16, sinyal3[i]))
        }

        val dataSetSinyal1 = LineDataSet(entriesSinyal1, "$nama1")
        dataSetSinyal1.color = Color.RED
        dataSetSinyal1.setDrawCircles(false)

        val dataSetSinyal2 = LineDataSet(entriesSinyal2, "$nama2")
        dataSetSinyal2.color = Color.BLACK
        dataSetSinyal2.setDrawCircles(false)

        val dataSetSinyal3 = LineDataSet(entriesSinyal3, "$nama3")
        dataSetSinyal3.color = Color.BLUE
        dataSetSinyal3.setDrawCircles(false)

        val lineDataSinyal = LineData(dataSetSinyal1, dataSetSinyal2, dataSetSinyal3)
        chart2.data = lineDataSinyal

        val durationInSecondsSinyal = sinyal1.size.toFloat() / 8000f * 16
        //println("Duration in seconds rekon: $durationInSecondsSinyal $nama1")

        val xAxis = chart2.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.labelCount = 7 // Menampilkan 7 label pada sumbu x
        xAxis.setDrawGridLines(false)
        xAxis.axisMinimum = 0f
        xAxis.axisMaximum = durationInSecondsSinyal
        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return String.format("%.1f", value) // Mengatur format angka pada sumbu x
            }
        }

        chart2.notifyDataSetChanged()
        chart2.invalidate()

        chartGestureListener = ChartGestureListener(chart2)
        scaleGestureDetector = ScaleGestureDetector(this, chartGestureListener)

        toggleButtonS1.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                chart2.data.getDataSetByIndex(2).isVisible = false
            } else {
                chart2.data.getDataSetByIndex(2).isVisible = true
            }
            chart2.notifyDataSetChanged()
            chart2.invalidate()
        }

        toggleButtonS2.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                chart2.data.getDataSetByIndex(1).isVisible = false
            } else {
                chart2.data.getDataSetByIndex(1).isVisible = true
            }
            chart2.notifyDataSetChanged()
            chart2.invalidate()
        }
    }

    fun plotFFTResult(freq: FloatArray, powerSpectrum: FloatArray, chart: BarChart) {
        val entries = mutableListOf<BarEntry>()

        for (i in freq.indices) {
            entries.add(BarEntry(freq[i], powerSpectrum[i]))
        }

        val dataSet = BarDataSet(entries, "FFT Result")
        dataSet.color = Color.RED

        val barData = BarData(dataSet)
        chart.data = barData

        val xAxis = chart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return "$value Hz"
            }
        }

        val yAxisLeft = chart.axisLeft
        val yAxisRight = chart.axisRight
        yAxisLeft.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
        yAxisRight.isEnabled = false

        chart.notifyDataSetChanged()
        chart.invalidate()

        chartGestureListener2 = ChartGestureListener2(chart)
        scaleGestureDetector = ScaleGestureDetector(this, chartGestureListener2)
    }


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        scaleGestureDetector.onTouchEvent(event!!)
        return super.onTouchEvent(event)
    }

    inner class ChartGestureListener(private val chart: LineChart) :
        ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            if (detector != null) {
                val scale = detector.scaleFactor
                chart.zoom(scale, scale, detector.focusX, detector.focusY, YAxis.AxisDependency.LEFT)
                chart.notifyDataSetChanged()
                chart.invalidate()
            }
            return true
        }
    }

    inner class ChartGestureListener2(private val chart: BarChart) :
        ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            if (detector != null) {
                val scale = detector.scaleFactor
                chart.zoom(scale, scale, detector.focusX, detector.focusY, YAxis.AxisDependency.LEFT)
                chart.notifyDataSetChanged()
                chart.invalidate()
            }
            return true
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}


