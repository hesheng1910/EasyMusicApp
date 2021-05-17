package com.example.easymusicapp.ui.favourites

import android.app.Activity
import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.easymusicapp.R
import com.example.easymusicapp.adapters.FavouriteAdapter
import com.example.easymusicapp.entity.Song
import com.example.easymusicapp.adapters.SongAdapter
import com.example.easymusicapp.database.EchoDatabase
import java.util.*
import kotlin.collections.ArrayList


class FavouriteFragment : Fragment() {

    var myActivity: Activity? = null
    var noFavourites: TextView? = null
    var nowPlayingBottomBar: RelativeLayout? = null
    var playPauseButton: ImageButton? = null
    var songTitle: TextView? = null
    var recyclerView: RecyclerView? = null
    var trackPosition: Int = 0
    var favouriteContent: EchoDatabase? = null
    var refreshList: ArrayList<Song>? = null
    var getListFromDatabase: ArrayList<Song>? = null

    object Statified {
        var mediaPlayer: MediaPlayer? = null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_favourites, container, false)
        activity?.title = "Favourites"
        noFavourites = root.findViewById(R.id.noFavourites)
        nowPlayingBottomBar = root.findViewById(R.id.hiddenBarFavScreen)
        songTitle = root.findViewById(R.id.songTitleFavScreen)
        playPauseButton = root.findViewById(R.id.playPauseButton)
        recyclerView = root.findViewById(R.id.favouriteRecycler)
        return root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        myActivity = context as Activity
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        myActivity = activity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        favouriteContent = EchoDatabase(myActivity)
        display_favourites_by_searching()
//        bottomBarSetup()
    }

    override fun onResume() {
        super.onResume()
    }
//
//    override fun onPrepareOptionsMenu(menu: Menu) {
//        super.onPrepareOptionsMenu(menu)
//        val item = menu?.findItem(R.id.action_sort)
//        item?.isVisible = false
//    }

    fun getSongsFromPhone(): ArrayList<Song> {
        var arrayList = ArrayList<Song>()
        var contentResolver = myActivity?.contentResolver
        var songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        var songCursor = contentResolver?.query(songUri, null, null, null, null)
        if (songCursor != null && songCursor.moveToFirst()) {
            val songId = songCursor.getColumnIndex(MediaStore.Audio.Media._ID)
            val songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val songArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            val songData = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA)
            val dateIndex = songCursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED)
            while (songCursor.moveToNext()) {
                var currentId = songCursor.getLong(songId)
                var currentTitle = songCursor.getString(songTitle)
                var currentArtist = songCursor.getString(songArtist)
                var currentData = songCursor.getString(songData)
                var currentDate = songCursor.getLong(dateIndex)
                arrayList.add(Song(currentId, currentTitle, currentArtist, currentData, currentDate))
            }
        }
        return arrayList
    }
//
//    fun bottomBarSetup() {
//        try {
//            bottomBarClickHandler()
//            songTitle?.setText(SongPlayingFragment.Statified.currentSongHelper?.songTitle)
//            SongPlayingFragment.Statified.mediaPlayer?.setOnCompletionListener({
//                songTitle?.setText(SongPlayingFragment.Statified.currentSongHelper?.songTitle)
//                SongPlayingFragment.Staticated.onSongComplete()
//            })
//            if (SongPlayingFragment.Statified.mediaPlayer?.isPlaying as Boolean) {
//                nowPlayingBottomBar?.visibility = View.VISIBLE
//            } else {
//                nowPlayingBottomBar?.visibility = View.INVISIBLE
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }
//
//    fun bottomBarClickHandler() {
//        nowPlayingBottomBar?.setOnClickListener({
//            Statified.mediaPlayer = SongPlayingFragment.Statified.mediaPlayer
//            val songPlayingFragment = SongPlayingFragment()
//            val args = Bundle()
//            args.putString("songArtist", SongPlayingFragment.Statified.currentSongHelper?.songArtist)
//            args.putString("path", SongPlayingFragment.Statified.currentSongHelper?.songPath)
//            args.putString("songTitle", SongPlayingFragment.Statified.currentSongHelper?.songTitle)
//            args.putInt("songId", SongPlayingFragment.Statified.currentSongHelper?.songId?.toInt() as Int)
//            args.putInt("songPosition", SongPlayingFragment.Statified.currentSongHelper?.currentPosition?.toInt() as Int)
//            args.putParcelableArrayList("songData", SongPlayingFragment.Statified.fetchSongs)
//            args.putString("favBottomBar", "success")
//            songPlayingFragment.arguments = args
//            fragmentManager!!.beginTransaction()
//                    .replace(R.id.details_fragment, songPlayingFragment)
//                    .addToBackStack("SongPlayingFragment")
//                    .commit()
//        })
//        playPauseButton?.setOnClickListener({
//            if (SongPlayingFragment.Statified.mediaPlayer?.isPlaying as Boolean) {
//                SongPlayingFragment.Statified.mediaPlayer?.pause()
//                trackPosition = SongPlayingFragment.Statified.mediaPlayer?.getCurrentPosition() as Int
//                playPauseButton?.setBackgroundResource(R.drawable.play_icon)
//            } else {
//                SongPlayingFragment.Statified.mediaPlayer?.seekTo(trackPosition)
//                SongPlayingFragment.Statified.mediaPlayer?.start()
//                playPauseButton?.setBackgroundResource(R.drawable.pause_icon)
//            }
//        })
//    }

    fun display_favourites_by_searching() {
        if (favouriteContent?.checkSize() as Int > 0) {
            refreshList = ArrayList<Song>()
            getListFromDatabase = favouriteContent?.queryDBList()
            var fetchListFromDevice = getSongsFromPhone()
            if (fetchListFromDevice != null) {
                for (i in 0..fetchListFromDevice?.size - 1) {
                    for (j in 0..getListFromDatabase?.size as Int - 1) {
                        if ((getListFromDatabase?.get(j)?.songID) === (fetchListFromDevice?.get(i)?.songID)) {
                            refreshList?.add((getListFromDatabase as ArrayList<Song>)[j])
                        }
                    }
                }
            }
            if (refreshList == null) {
                recyclerView?.visibility = View.INVISIBLE
                noFavourites?.visibility = View.VISIBLE
            } else {
                var favouriteAdapter = FavouriteAdapter(refreshList as ArrayList<Song>, myActivity as Context)
                val mLayoutManager = LinearLayoutManager(activity)
                recyclerView?.layoutManager = mLayoutManager
                recyclerView?.itemAnimator = DefaultItemAnimator()
                recyclerView?.adapter = favouriteAdapter
                recyclerView?.setHasFixedSize(true)
            }
        } else {
            recyclerView?.visibility = View.INVISIBLE
            noFavourites?.visibility = View.VISIBLE
        }
    }

}
