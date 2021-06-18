package com.example.easymusicapp.ui.songplaying

import android.app.Activity
import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.*
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.cleveroad.audiovisualization.AudioVisualization
import com.cleveroad.audiovisualization.DbmHandler
import com.cleveroad.audiovisualization.GLAudioVisualizationView
import com.example.easymusicapp.CurrentSong
import com.example.easymusicapp.R
import com.example.easymusicapp.database.EchoDatabase
import com.example.easymusicapp.entity.Song
import com.example.easymusicapp.ui.favourites.FavouriteFragment
import java.util.*
import java.util.concurrent.TimeUnit

class SongPlayingFragment : Fragment() {


    lateinit var mediaPlayer: MediaPlayer
    lateinit var currentSong: CurrentSong
    lateinit var myActivity: Activity
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
    lateinit var audioVisualization: AudioVisualization
    lateinit var glView: GLAudioVisualizationView
    lateinit var fab: ImageButton
    lateinit var favouriteContent: EchoDatabase
    private var updateSongTime = object : Runnable {
        override fun run() {
            val getCurrent = mediaPlayer.currentPosition
            seekBar.progress = getCurrent
            startTimeText.text = String.format("%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(getCurrent.toLong()),
                    TimeUnit.MILLISECONDS.toSeconds(getCurrent.toLong()) -
                            TimeUnit.MILLISECONDS.toSeconds(TimeUnit.MILLISECONDS.toMinutes(getCurrent.toLong()))  - 60 * TimeUnit.MILLISECONDS.toMinutes(getCurrent.toLong()))
                Handler().postDelayed(this, 1000)
            }
        }
    
    var myPresShuffle = "Shuffle Feature"
    var myPresLoop = "Loop Feature"

    private fun onSongComplete() {
            if (currentSong.isShuffle) {
                playNext("PlayNextLikeNormalShuffle")
                currentSong.isPlaying = true
            } else {
                if (currentSong.isLoop) {
                    currentSong.isPlaying = true
                    val nextSong = fetchSongs[currentPosition]
                    currentSong.songTitle = nextSong.songTitle
                    currentSong.songPath = nextSong.songData
                    currentSong.currentPosition = currentPosition
                    currentSong.songId = nextSong.songID
                    updateTextViews(currentSong.songTitle as String, currentSong.songArtist as String)
                    mediaPlayer.reset()
                    try {
                        myActivity.let { mediaPlayer.setDataSource(it, Uri.parse(currentSong.songPath)) }
                        mediaPlayer.prepare()
                        mediaPlayer.start()
                        processInformation(mediaPlayer)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    playNext("PlayNextNormal")
                    currentSong.isPlaying = true
                }
            }
            if (favouriteContent.checkifIDExists(currentSong.songId.toInt() as Int) as Boolean) {
                fab.setImageDrawable(ContextCompat.getDrawable(myActivity, R.drawable.favorite_on))
            } else {
                fab.setImageDrawable(ContextCompat.getDrawable(myActivity, R.drawable.favorite_off))
            }
        }

    private fun updateTextViews(songTitle: String, songArtist: String) {
            var songTitleUpdated = songTitle
            var songArtistUpdated = songArtist
            if (songTitle.equals("<unknown>", true)) {
                songTitleUpdated = "unknown"
            }
            if (songArtist.equals("<unknown>", true)) {
                songArtistUpdated = "unknown"
            }
        songTitleView.text = songTitleUpdated
        songArtistView.text = songArtistUpdated
        }

    private fun processInformation(mediaPlayer: MediaPlayer) {
            val finalTime = mediaPlayer.duration
            val startTime = mediaPlayer.currentPosition
        seekBar.max = finalTime
        seekBar.progress = startTime
        startTimeText.text = String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(startTime.toLong()),
                TimeUnit.MILLISECONDS.toSeconds(startTime.toLong()) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(startTime.toLong())) - 60 * TimeUnit.MILLISECONDS.toMinutes(startTime.toLong()))
        endTimeText.text = String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(finalTime.toLong()),
                TimeUnit.MILLISECONDS.toSeconds(finalTime.toLong()) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(finalTime.toLong())))

            Handler().postDelayed(updateSongTime, 1000)
        }

    private fun playNext(check: String) {

            if (check.equals("PlayNextNormal", true)) {
                currentPosition += 1
            } else if (check.equals("PlayNextLikeNormalShuffle", true)) {
                val randomObject = Random()
                val randomPosition = randomObject.nextInt(fetchSongs.size.plus(1))
                currentPosition = randomPosition
            }
            if (currentPosition == fetchSongs.size) {
                currentPosition = 0
            }
            currentSong.isLoop = false
            val nextSong = fetchSongs[currentPosition]
            currentSong.songPath = nextSong.songData
            currentSong.songTitle = nextSong.songTitle
            currentSong.songArtist = nextSong.artist
            currentSong.songId = nextSong.songID
            updateTextViews(currentSong.songTitle as String, currentSong.songArtist as String)
            mediaPlayer.reset()
            try {
                myActivity.let { mediaPlayer.setDataSource(it, Uri.parse(currentSong.songPath)) }
                mediaPlayer.prepare()
                mediaPlayer.start()
                processInformation(mediaPlayer)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            if (favouriteContent.checkifIDExists(currentSong.songId.toInt())) {
                fab.setImageDrawable(ContextCompat.getDrawable(myActivity, R.drawable.favorite_on))
            } else {
                fab.setImageDrawable(ContextCompat.getDrawable(myActivity, R.drawable.favorite_off))
            }
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_song_playing, container, false)
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
        audioVisualization = glView
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
    }

    override fun onPause() {
        super.onPause()
        audioVisualization.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        audioVisualization.release()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        favouriteContent = EchoDatabase(myActivity)
        currentSong = CurrentSong()
        currentSong.isPlaying = true
        currentSong.isLoop = false
        currentSong.isShuffle = false

        var path: String? = null
        val songTitle2: String
        val songArtist2: String
        var songId: Long = 0
        try {

            path = arguments?.getString("path").toString()
            songTitle2 = arguments?.getString("songTitle").toString()
            songArtist2 = arguments?.getString("songArtist").toString()
            songId = arguments?.getInt("songId")!!.toLong()

            currentPosition = requireArguments().getInt("position")
            fetchSongs = requireArguments().getParcelableArrayList("songData")!!

            currentSong.songPath = path
            currentSong.songTitle = songTitle2
            currentSong.songArtist = songArtist2
            currentSong.songId = songId
            currentSong.currentPosition = currentPosition

            updateTextViews(currentSong.songTitle as String,
                currentSong.songArtist as String)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val fromFavBottomBar = arguments?.get("favBottomBar") as? String
        if (fromFavBottomBar != null) {
            mediaPlayer = FavouriteFragment.mediaPlayer
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
        processInformation(mediaPlayer)
        if (currentSong.isPlaying) {
            playPauseImageButton.setBackgroundResource(R.drawable.pause_icon)
        } else {
            playPauseImageButton.setBackgroundResource(R.drawable.play_icon)
        }
        mediaPlayer.setOnCompletionListener {
            onSongComplete()
        }
        clickHandler()
        val visualizationHandler = DbmHandler.Factory.newVisualizerHandler(myActivity as Context, 0)
        audioVisualization.linkTo(visualizationHandler)
        val prefsForShuffle = myActivity.getSharedPreferences(myPresShuffle, Context.MODE_PRIVATE)
        val isShuffleAllowed = prefsForShuffle.getBoolean("Feature", false)
        if (isShuffleAllowed) {
            currentSong.isShuffle = true
            currentSong.isLoop = false
            shuffleImageButton.setBackgroundResource(R.drawable.shuffle_icon)
            loopImageButton.setBackgroundResource(R.drawable.loop_white_icon)
        } else {
            currentSong.isShuffle = false
            shuffleImageButton.setBackgroundResource(R.drawable.shuffle_white_icon)
        }
        val prefsForLoop = myActivity.getSharedPreferences(myPresLoop, Context.MODE_PRIVATE)
        val isLoopAllowed = prefsForLoop.getBoolean("Feature", false)
        if (isLoopAllowed) {
            currentSong.isShuffle = false
            currentSong.isLoop = true
            shuffleImageButton.setBackgroundResource(R.drawable.shuffle_white_icon)
            loopImageButton.setBackgroundResource(R.drawable.loop_icon)
        } else {
            currentSong.isLoop = false
            loopImageButton.setBackgroundResource(R.drawable.loop_white_icon)
        }
        if (favouriteContent.checkifIDExists(currentSong.songId.toInt())) {
            fab.setImageDrawable(ContextCompat.getDrawable(myActivity, R.drawable.favorite_on))
        } else {
            fab.setImageDrawable(ContextCompat.getDrawable(myActivity, R.drawable.favorite_off))
        }
    }

    private fun clickHandler() {

        fab.setOnClickListener {
            if (favouriteContent.checkifIDExists(currentSong.songId.toInt())) {
                fab.setImageDrawable(ContextCompat.getDrawable(myActivity, R.drawable.favorite_off))
                favouriteContent.deleteFavourite(currentSong.songId.toInt())
                Toast.makeText(myActivity, "Xóa bài hát từ danh mục yêu thích", Toast.LENGTH_SHORT).show()
            } else {
                fab.setImageDrawable(ContextCompat.getDrawable(myActivity, R.drawable.favorite_on))
                favouriteContent.storeAsFavourite(currentSong.songId.toInt(),
                    currentSong.songArtist, currentSong.songTitle,
                    currentSong.songPath)
                Toast.makeText(myActivity, "Thêm bài hát vào danh mục yêu thích", Toast.LENGTH_SHORT).show()
            }
        }
        shuffleImageButton.setOnClickListener {
            val editorShuffle = myActivity.getSharedPreferences(
                myPresShuffle,
                Context.MODE_PRIVATE
            ).edit()
            val editorLoop = myActivity.getSharedPreferences(
                myPresLoop,
                Context.MODE_PRIVATE
            ).edit()
            if (currentSong.isShuffle) {
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
            if (currentSong.isShuffle) {
                playNext("PlayNextLikeNormalShuffle")
            } else {
                playNext("PlayNextNormal")
            }
        }
        previousImageButton.setOnClickListener {
            currentSong.isPlaying = true
            if (currentSong.isLoop) {
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
            if (currentSong.isLoop) {
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
        if (currentSong.isPlaying) {
            playPauseImageButton.setBackgroundResource(R.drawable.pause_icon)
        } else {
            playPauseImageButton.setBackgroundResource(R.drawable.play_icon)
        }
        currentSong.isLoop = false

        val nextSong = fetchSongs[currentPosition]
        currentSong.songPath = nextSong.songData
        currentSong.songTitle = nextSong.songTitle
        currentSong.songArtist = nextSong.artist
        currentSong.songId = nextSong.songID
        updateTextViews(currentSong.songTitle as String, currentSong.songArtist as String)
        mediaPlayer.reset()
        try {
            myActivity.let { mediaPlayer.setDataSource(it, Uri.parse(currentSong.songPath)) }
            mediaPlayer.prepare()
            mediaPlayer.start()
            processInformation(mediaPlayer)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (favouriteContent.checkifIDExists(currentSong.songId.toInt())) {
            fab.setImageDrawable(ContextCompat.getDrawable(myActivity, R.drawable.favorite_on))
        } else {
            fab.setImageDrawable(ContextCompat.getDrawable(myActivity, R.drawable.favorite_off))
        }
    }

}