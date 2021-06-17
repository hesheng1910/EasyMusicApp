package com.example.easymusicapp.ui.songplaying

import android.app.Activity
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.*
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.cleveroad.audiovisualization.AudioVisualization
import com.cleveroad.audiovisualization.DbmHandler
import com.cleveroad.audiovisualization.GLAudioVisualizationView
import com.example.easymusicapp.CurrentSong
import com.example.easymusicapp.R
import com.example.easymusicapp.entity.Song
import java.util.*
import java.util.concurrent.TimeUnit

class SongPlayingFragment : Fragment() {

    var mAcceleration: Float = 0f
    var mAccelerationCurrent: Float = 0f
    var mAccelerationLast: Float = 0f
    lateinit var myActivity: Activity
    lateinit var mediaPlayer: MediaPlayer
    lateinit var startTimeText: TextView
    lateinit var endTimeText: TextView
    lateinit var playPauseImageButton: ImageButton
    lateinit var previousImageButton: ImageButton
    lateinit var nextImageButton: ImageButton
    lateinit var loopImageButton: ImageButton
    lateinit var shuffleImageButton: ImageButton
    lateinit var seekBar: SeekBar
    lateinit var songArtistView: TextView
    lateinit var songTitleView: TextView
    var currentPosition: Int = 0
    lateinit var fetchSongs: ArrayList<Song>
    lateinit var currentSong: CurrentSong
    lateinit var audioVisualization: AudioVisualization
    lateinit var glView: GLAudioVisualizationView
    lateinit var fab: ImageButton
//        var favouriteContent: EchoDatabase
    lateinit var mSensorManager: SensorManager
    lateinit var mSensorListener: SensorEventListener
    var myPresName = "ShakeFeature"
    var updateSongTime = object : Runnable {
            override fun run() {
                val getCurrent = mediaPlayer.currentPosition
                startTimeText.setText(String.format("%d:%d",
                        TimeUnit.MILLISECONDS.toMinutes(getCurrent.toLong() as Long),
                        TimeUnit.MILLISECONDS.toSeconds(getCurrent.toLong()) -
                                TimeUnit.MILLISECONDS.toSeconds(TimeUnit.MILLISECONDS.toMinutes(getCurrent.toLong()))))
                Handler().postDelayed(this, 1000)


            }
        }
    
    var myPresShuffle = "Shuffle Feature"
    var myPresLoop = "Loop Feature"

        private fun onSongComplete() {
            if (currentSong.isShuffle as Boolean) {
                playNext("PlayNextLikeNormalShuffle")
                currentSong.isPlaying = true
            } else {
                if (currentSong.isLoop as Boolean) {
                    currentSong.isPlaying = true
                    val nextSong = fetchSongs.get(currentPosition)
                    currentSong.songTitle = nextSong.songTitle
                    currentSong.songPath = nextSong.songData
                    currentSong.currentPosition = currentPosition
                    currentSong.songId = nextSong.songID as Long
                    updateTextViews(currentSong.songTitle as String, currentSong.songArtist as String)
                    mediaPlayer.reset()
                    try {
                        myActivity.let { mediaPlayer.setDataSource(it, Uri.parse(currentSong.songPath)) }
                        mediaPlayer.prepare()
                        mediaPlayer.start()
                        processInformation(mediaPlayer as MediaPlayer)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    playNext("PlayNextNormal")
                    currentSong.isPlaying = true
                }
            }
//            if (Statified.favouriteContent.checkifIDExists(Statified.currentSong.songId.toInt() as Int) as Boolean) {
//                Statified.fab.setImageDrawable(ContextCompat.getDrawable(Statified.myActivity!!, R.drawable.favorite_on))
//            } else {
//                Statified.fab.setImageDrawable(ContextCompat.getDrawable(Statified.myActivity!!, R.drawable.favorite_off))
//            }
        }

        fun updateTextViews(songTitle: String, songArtist: String) {
            var songTitleUpdated = songTitle
            var songArtistUpdated = songTitle
            if (songTitle.equals("<unknown>", true)) {
                songTitleUpdated = "unknown"
            }
            if (songArtist.equals("<unknown>", true)) {
                songArtistUpdated = "unknown"
            }
            songTitleView.setText(songTitleUpdated)
            songArtistView.setText(songArtistUpdated)
        }

        fun processInformation(mediaPlayer: MediaPlayer) {
            val finalTime = mediaPlayer.duration
            val startTime = mediaPlayer.currentPosition
            seekBar.max = finalTime
            startTimeText.setText(String.format("%d:%d",
                TimeUnit.MILLISECONDS.toMinutes(startTime.toLong()),
                TimeUnit.MILLISECONDS.toSeconds(startTime.toLong()) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(startTime.toLong()))))
            endTimeText.setText(String.format("%d:%d",
                TimeUnit.MILLISECONDS.toMinutes(finalTime.toLong()),
                TimeUnit.MILLISECONDS.toSeconds(finalTime.toLong()) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(finalTime.toLong()))))
            seekBar.setProgress(startTime)
            Handler().postDelayed(updateSongTime, 1000)
        }

        fun playNext(check: String) {

            if (check.equals("PlayNextNormal", true)) {
                currentPosition += 1
            } else if (check.equals("PlayNextLikeNormalShuffle", true)) {
                var randomObject = Random()
                var randomPosition = randomObject.nextInt(fetchSongs.size.plus(1) as Int)
                currentPosition = randomPosition
            }
            if (currentPosition == fetchSongs.size) {
                currentPosition = 0
            }
            currentSong.isLoop = false
            var nextSong = fetchSongs.get(currentPosition)
            currentSong.songPath = nextSong.songData
            currentSong.songTitle = nextSong.songTitle
            currentSong.songArtist = nextSong.artist
            currentSong.songId = nextSong.songID as Long
            updateTextViews(currentSong.songTitle as String, currentSong.songArtist as String)
            mediaPlayer.reset()
            try {
                myActivity.let { mediaPlayer.setDataSource(it, Uri.parse(currentSong.songPath)) }
                mediaPlayer.prepare()
                mediaPlayer.start()
                processInformation(mediaPlayer as MediaPlayer)
            } catch (e: Exception) {
                e.printStackTrace()
            }
//            if (Statified.favouriteContent.checkifIDExists(Statified.currentSong.songId.toInt() as Int) as Boolean) {
//                Statified.fab.setImageDrawable(ContextCompat.getDrawable(Statified.myActivity!!, R.drawable.favorite_on))
//            } else {
//                Statified.fab.setImageDrawable(ContextCompat.getDrawable(Statified.myActivity!!, R.drawable.favorite_off))
//            }
        }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_song_playing, container, false)
        setHasOptionsMenu(true)
        activity?.title = "Now Playing"
        seekBar = view.findViewById(R.id.seekBar)!!
        startTimeText = view.findViewById(R.id.startTime)
        endTimeText = view.findViewById(R.id.endTime)
        playPauseImageButton = view.findViewById(R.id.playPauseButton)
        nextImageButton = view.findViewById(R.id.nextButton)
        previousImageButton = view.findViewById(R.id.previousButton)
        loopImageButton = view.findViewById(R.id.loopButton)
        shuffleImageButton = view.findViewById(R.id.shuffleButton)
        songArtistView = view.findViewById(R.id.songArtist)
        songTitleView = view.findViewById(R.id.songTitle)
        glView = view.findViewById(R.id.visualizer_view)
        fab = view.findViewById(R.id.favouriteIcon)
        fab.alpha = 0.8f
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        audioVisualization = glView as AudioVisualization
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        myActivity = context as Activity
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        myActivity = activity
    }

    override fun onResume() {
        super.onResume()
        audioVisualization.onResume()
        mSensorManager.registerListener(mSensorListener,
            mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        audioVisualization.onPause()
        mSensorManager.unregisterListener(mSensorListener)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        audioVisualization.release()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mSensorManager = myActivity.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mAcceleration = 0.0f
        mAccelerationCurrent = SensorManager.GRAVITY_EARTH
        mAccelerationLast = SensorManager.GRAVITY_EARTH
        bindShakeListener()
    }
    

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

//        Statified.favouriteContent = EchoDatabase(Statified.myActivity)
        currentSong = CurrentSong()
        currentSong.isPlaying = true
        currentSong.isLoop = false
        currentSong.isShuffle = false

        var path: String? = null
        var _songTitle: String
        val _songArtist: String
        var songId: Long = 0
        try {

            path = arguments?.getString("path").toString()
            _songTitle = arguments?.getString("songTitle").toString()
            _songArtist = arguments?.getString("songArtist").toString()
            songId = arguments?.getInt("songId")!!.toLong()

            currentPosition = requireArguments().getInt("position")
            fetchSongs = requireArguments().getParcelableArrayList("songData")!!

            currentSong.songPath = path
            currentSong.songTitle = _songTitle
            currentSong.songArtist = _songArtist
            currentSong.songId = songId
            currentSong.currentPosition = currentPosition

            updateTextViews(currentSong.songTitle as String,
                currentSong.songArtist as String)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        var fromFavBottomBar = arguments?.get("favBottomBar") as? String
        if (fromFavBottomBar != null) {
//            Statified.mediaPlayer = FavouriteFragment.Statified.mediaPlayer
        } else {
            mediaPlayer = MediaPlayer()
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)

            try {
                myActivity.let { mediaPlayer.setDataSource(it, Uri.parse(path)) }
                mediaPlayer.prepare()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            mediaPlayer.start()
        }
        processInformation(mediaPlayer as MediaPlayer)
        if (currentSong.isPlaying as Boolean) {
            playPauseImageButton.setBackgroundResource(R.drawable.pause_icon)
        } else {
            playPauseImageButton.setBackgroundResource(R.drawable.play_icon)
        }
        mediaPlayer.setOnCompletionListener {
            onSongComplete()
        }
        clickHandler()
        var visualizationHandler = DbmHandler.Factory.newVisualizerHandler(myActivity as Context, 0)
        audioVisualization.linkTo(visualizationHandler)
        var prefsForShuffle = myActivity.getSharedPreferences(myPresShuffle, Context.MODE_PRIVATE)
        var isShuffleAllowed = prefsForShuffle.getBoolean("Feature", false)
        if (isShuffleAllowed as Boolean) {
            currentSong.isShuffle = true
            currentSong.isLoop = false
            shuffleImageButton.setBackgroundResource(R.drawable.shuffle_icon)
            loopImageButton.setBackgroundResource(R.drawable.loop_white_icon)
        } else {
            currentSong.isShuffle = false
            shuffleImageButton.setBackgroundResource(R.drawable.shuffle_white_icon)
        }
        var prefsForLoop = myActivity.getSharedPreferences(myPresLoop, Context.MODE_PRIVATE)
        var isLoopAllowed = prefsForLoop.getBoolean("Feature", false)
        if (isLoopAllowed as Boolean) {
            currentSong.isShuffle = false
            currentSong.isLoop = true
            shuffleImageButton.setBackgroundResource(R.drawable.shuffle_white_icon)
            loopImageButton.setBackgroundResource(R.drawable.loop_icon)
        } else {
            currentSong.isLoop = false
            loopImageButton.setBackgroundResource(R.drawable.loop_white_icon)
        }
//        if (Statified.favouriteContent.checkifIDExists(Statified.currentSong.songId.toInt() as Int) as Boolean) {
//            Statified.fab.setImageDrawable(ContextCompat.getDrawable(Statified.myActivity!!, R.drawable.favorite_on))
//        } else {
//            Statified.fab.setImageDrawable(ContextCompat.getDrawable(Statified.myActivity!!, R.drawable.favorite_off))
//        }
    }

    fun clickHandler() {

//        Statified.fab.setOnClickListener {
//            if (Statified.favouriteContent.checkifIDExists(Statified.currentSong.songId.toInt() as Int) as Boolean) {
//                Statified.fab.setImageDrawable(ContextCompat.getDrawable(Statified.myActivity!!, R.drawable.favorite_off))
//                Statified.favouriteContent.deleteFavourite(Statified.currentSong.songId.toInt() as Int)
//                Toast.makeText(Statified.myActivity, "Removed from Favourites", Toast.LENGTH_SHORT).show()
//            } else {
//                Statified.fab.setImageDrawable(ContextCompat.getDrawable(Statified.myActivity!!, R.drawable.favorite_on))
//                Statified.favouriteContent.storeAsFavourite(Statified.currentSong.songId.toInt(),
//                    Statified.currentSong.songArtist, Statified.currentSong.songTitle,
//                    Statified.currentSong.songPath)
//                Toast.makeText(Statified.myActivity, "Added to Favourites", Toast.LENGTH_SHORT).show()
//            }
//        }
        shuffleImageButton.setOnClickListener {
            var editorShuffle = myActivity.getSharedPreferences(
                myPresShuffle,
                Context.MODE_PRIVATE
            ).edit()
            var editorLoop = myActivity.getSharedPreferences(
                myPresLoop,
                Context.MODE_PRIVATE
            ).edit()
            if (currentSong.isShuffle as Boolean) {
                shuffleImageButton.setBackgroundResource(R.drawable.shuffle_white_icon)
                currentSong.isShuffle = false
                editorShuffle.putBoolean("Feature", false)
                editorShuffle.apply()
            } else {
                currentSong.isShuffle = true
                currentSong.isLoop = false
                shuffleImageButton.setBackgroundResource(R.drawable.shuffle_icon)
                loopImageButton.setBackgroundResource(R.drawable.loop_white_icon)
                editorShuffle.putBoolean("Feature", true)
                editorShuffle.apply()
                editorLoop.putBoolean("Feature", false)
                editorLoop.apply()
            }
        }
        nextImageButton.setOnClickListener {
            currentSong.isPlaying = true
            playPauseImageButton.setBackgroundResource(R.drawable.pause_icon)
            if (currentSong.isShuffle as Boolean) {
                playNext("PlayNextLikeNormalShuffle")
            } else {
                playNext("PlayNextNormal")
            }
        }
        previousImageButton.setOnClickListener {
            currentSong.isPlaying = true
            if (currentSong.isLoop as Boolean) {
                loopImageButton.setBackgroundResource(R.drawable.loop_white_icon)
            }
            playPrevious()
        }
        loopImageButton.setOnClickListener {
            val editorShuffle = myActivity.getSharedPreferences(
                myPresShuffle,
                Context.MODE_PRIVATE
            ).edit()
            val editorLoop = myActivity.getSharedPreferences(
                myPresLoop,
                Context.MODE_PRIVATE
            ).edit()
            if (currentSong.isLoop as Boolean) {
                currentSong.isLoop = false
                loopImageButton.setBackgroundResource(R.drawable.loop_white_icon)
                editorLoop.putBoolean("Feature", false)
                editorLoop.apply()
            } else {
                currentSong.isLoop = true
                currentSong.isShuffle = false
                loopImageButton.setBackgroundResource(R.drawable.loop_icon)
                shuffleImageButton.setBackgroundResource(R.drawable.shuffle_white_icon)
                editorShuffle.putBoolean("Feature", false)
                editorShuffle.apply()
                editorLoop.putBoolean("Feature", true)
                editorLoop.apply()
            }
        }
        playPauseImageButton.setOnClickListener {
            if (mediaPlayer.isPlaying as Boolean) {
                mediaPlayer.pause()
                currentSong.isPlaying = false
                playPauseImageButton.setBackgroundResource(R.drawable.play_icon)
            } else {
                mediaPlayer.start()
                currentSong.isPlaying = true
                playPauseImageButton.setBackgroundResource(R.drawable.pause_icon)
            }
        }
    }

    private fun playPrevious() {

        currentPosition -= 1
        if (currentPosition == -1) {
            currentPosition = 0
        }
        if (currentSong.isPlaying as Boolean) {
            playPauseImageButton.setBackgroundResource(R.drawable.pause_icon)
        } else {
            playPauseImageButton.setBackgroundResource(R.drawable.play_icon)
        }
        currentSong.isLoop = false

        var nextSong = fetchSongs.get(currentPosition)
        currentSong.songPath = nextSong.songData
        currentSong.songTitle = nextSong.songTitle
        currentSong.songArtist = nextSong.artist
        currentSong.songId = nextSong.songID as Long
        updateTextViews(currentSong.songTitle as String, currentSong.songArtist as String)
        mediaPlayer.reset()
        try {
            myActivity.let { mediaPlayer.setDataSource(it, Uri.parse(currentSong.songPath)) }
            mediaPlayer.prepare()
            mediaPlayer.start()
            processInformation(mediaPlayer as MediaPlayer)
        } catch (e: Exception) {
            e.printStackTrace()
        }
//        if (Statified.favouriteContent.checkifIDExists(Statified.currentSong.songId.toInt() as Int) as Boolean) {
//            Statified.fab.setImageDrawable(ContextCompat.getDrawable(Statified.myActivity!!, R.drawable.favorite_on))
//        } else {
//            Statified.fab.setImageDrawable(ContextCompat.getDrawable(Statified.myActivity!!, R.drawable.favorite_off))
//        }
    }

    fun bindShakeListener() {
        mSensorListener = object : SensorEventListener {
            override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
            }

            override fun onSensorChanged(p0: SensorEvent?) {
                val x = p0!!.values[0]
                val y = p0.values[1]
                val z = p0.values[2]
                mAccelerationLast = mAccelerationCurrent
                mAccelerationCurrent = Math.sqrt(((x * x + y * y + z * z).toDouble())).toFloat()
                val delta = mAccelerationCurrent - mAccelerationLast
                mAcceleration = mAcceleration * 0.9f + delta
                if (mAcceleration > 12) {
                    val prefs = myActivity.getSharedPreferences(myPresName, Context.MODE_PRIVATE)
                    val isAllowed = prefs.getBoolean("feature", false)
                    if (isAllowed as Boolean) {
                        playNext("PlayNextNormal")
                    }
                }
            }
        }
    }
}