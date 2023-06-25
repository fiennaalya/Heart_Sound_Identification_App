package com.example.firsttrial.View.DetailAudio

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.firsttrial.R
import com.example.firsttrial.View.AnalisaData.AnalisaActivity
import com.example.firsttrial.databinding.ActivityDetailAudioBinding
import java.io.IOException

class DetailAudioActivity : AppCompatActivity(){

    companion object{
        const val EXTRA_TITLE = "extra_title"
        const val EXTRA_FILEPATH = "extra_filepath"
        const val EXTRA_POSITION = "extra_position"
        const val EXTRA_LIST = "extra_list"
    }

    private lateinit var runnable: Runnable
    private lateinit var animation : Animation
    private lateinit var DetailBinding : ActivityDetailAudioBinding
    private lateinit var mediaPlayer : MediaPlayer
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        DetailBinding = ActivityDetailAudioBinding.inflate(layoutInflater)
        setContentView(DetailBinding.root)

        supportActionBar?.title = "Play Audio"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val tvTitle: TextView = DetailBinding.namaAudioDetail
        val buttonplaypause : Button = DetailBinding.buttonPlayPause
        val buttonNext : Button = DetailBinding.buttonNext
        val buttonPrevious : Button = DetailBinding.buttonPrevious
        val audioseekbar : SeekBar = DetailBinding.audioSeeker
        val volumeSeekbar : SeekBar = DetailBinding.volumeSeeker
        val textProgres : TextView = DetailBinding.textViewProgress
        val textTotal : TextView = DetailBinding.textViewTotalTime
        val buttonAnalisa : Button = DetailBinding.btnAnalisaAudio

        var titleAudio = intent.getStringExtra(EXTRA_TITLE)
        var filepath = intent.getStringExtra(EXTRA_FILEPATH)
        var position = intent.getIntExtra(EXTRA_POSITION, 0)
        val listAudio = intent.getStringArrayListExtra(EXTRA_LIST)

        tvTitle.text = titleAudio
        animation = AnimationUtils.loadAnimation(this, R.anim.translate_animation)
        tvTitle.animation = animation

        mediaPlayer = MediaPlayer()

        try {
            mediaPlayer.reset()
            mediaPlayer.setDataSource(filepath)
            mediaPlayer.prepare()
            mediaPlayer.start()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        mediaPlayer.setOnCompletionListener {
            buttonplaypause.setBackgroundResource(R.drawable.play)
        }

        buttonAnalisa.setOnClickListener(){
            mediaPlayer.pause()
            buttonplaypause.setBackgroundResource(R.drawable.play)
            val moveAnalisaIntent = Intent(this@DetailAudioActivity, AnalisaActivity::class.java)
            moveAnalisaIntent.putExtra(AnalisaActivity.EXTRA_TITLEAUDIO, tvTitle.text.toString())
            moveAnalisaIntent.putExtra(AnalisaActivity.EXTRA_FILEPATHAUDIO, filepath)
            startActivity(moveAnalisaIntent)
        }

        buttonplaypause.setOnClickListener(){
            if (mediaPlayer.isPlaying == true){
                mediaPlayer.pause()
                buttonplaypause.setBackgroundResource(R.drawable.play)
            }
            else{
                mediaPlayer.start()
                buttonplaypause.setBackgroundResource(R.drawable.pause)

            }

        }



        buttonPrevious.setOnClickListener(){
            mediaPlayer.stop()
            mediaPlayer.reset()

            if (position == 0) {
                position = listAudio!!.size - 1
            } else {
                position--
            }

            filepath = listAudio!!.get(position)

            try {
                mediaPlayer.setDataSource(filepath)
                mediaPlayer.prepare()
                mediaPlayer.start()

                buttonplaypause.setBackgroundResource(R.drawable.pause)

                val newtitle = filepath!!.substring(filepath!!.lastIndexOf("/")+1)
                tvTitle.setText(newtitle)

                tvTitle.clearAnimation()
                tvTitle.startAnimation(animation)
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }

        buttonNext.setOnClickListener(){
            mediaPlayer.reset()

            if (position == listAudio!!.size -1) {
                position = 0
            } else {
                position++
            }

            filepath = listAudio!!.get(position)

            try {
                mediaPlayer.setDataSource(filepath)
                mediaPlayer.prepare()
                mediaPlayer.start()

                buttonplaypause.setBackgroundResource(R.drawable.pause)

                val newtitle = filepath!!.substring(filepath!!.lastIndexOf("/")+1)
                tvTitle.setText(newtitle)

                tvTitle.clearAnimation()
                tvTitle.startAnimation(animation)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        volumeSeekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if(fromUser){
                    volumeSeekbar.progress = progress
                    val volumeLevel : Float = progress/100f
                    val amplifiedVolumeLevel : Float = volumeLevel * 1.5f // Mengatur level volume sebesar 150%
                    mediaPlayer.setVolume(amplifiedVolumeLevel, amplifiedVolumeLevel)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {

            }
        })

        audioseekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if(fromUser){
                    mediaPlayer.seekTo(progress)
                    audioseekbar.progress = progress
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }

        })

        val handler = Handler()
        runnable = Runnable {
            val totalTime: Int = mediaPlayer.duration
            audioseekbar.max = totalTime

            val currentPosition: Int = mediaPlayer.currentPosition
            audioseekbar.progress = currentPosition

            handler.postDelayed(runnable, 1000)

            val elapesedTime : String = createTimeLabel(currentPosition)
            val lastTime : String = createTimeLabel(totalTime)

            textProgres.text = elapesedTime
            textTotal.text = lastTime

            if(elapesedTime.equals(lastTime)){
                mediaPlayer.reset()

                if (position == listAudio!!.size -1) {
                    position = 0
                } else {
                    position++
                }

                val newFilePath : String = listAudio!!.get(position)

                try {
                    mediaPlayer.setDataSource(newFilePath)
                    mediaPlayer.prepare()
                    mediaPlayer.start()

                    buttonplaypause.setBackgroundResource(R.drawable.pause)

                    val newtitle = newFilePath.substring(newFilePath.lastIndexOf("/")+1)
                    tvTitle.setText(newtitle)

                    tvTitle.clearAnimation()
                    tvTitle.startAnimation(animation)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }

        handler.post(runnable)
    }

    fun createTimeLabel(currentPosition: Int): String {
        val timeLabel: String
        val minute: Int = currentPosition/1000/60
        val second: Int = currentPosition / 1000 % 60

        if (second < 10 ){
            timeLabel = "$minute:0$second"
        } else {
            timeLabel = "$minute:$second"
        }

        return timeLabel
    }


    override fun onSupportNavigateUp(): Boolean {
        mediaPlayer.stop()
        onBackPressed()
        return true
    }
}