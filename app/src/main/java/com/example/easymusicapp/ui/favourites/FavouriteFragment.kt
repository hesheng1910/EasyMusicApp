package com.example.easymusicapp.ui.favourites

import android.annotation.SuppressLint
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

    private lateinit var myActivity: Activity
    private lateinit var noFavourites: TextView

    private lateinit var recyclerView: RecyclerView
    private lateinit var favouriteContent: EchoDatabase
    private lateinit var refreshList: ArrayList<Song>
    private lateinit var getListFromDatabase: ArrayList<Song>

    companion object {
       lateinit var mediaPlayer: MediaPlayer
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_favourites, container, false)
        activity?.title = "Favourites"
        noFavourites = root.findViewById(R.id.noFavourites)
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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        favouriteContent = EchoDatabase(myActivity)
        display_favourites_by_searching()
    }
    //
//    override fun onPrepareOptionsMenu(menu: Menu) {
//        super.onPrepareOptionsMenu(menu)
//        val item = menu?.findItem(R.id.action_sort)
//        item?.isVisible = false
//    }

    @SuppressLint("Recycle")
    fun getSongsFromPhone(): ArrayList<Song> {
        val arrayList = ArrayList<Song>()
        val contentResolver = myActivity.contentResolver
        val songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val songCursor = contentResolver?.query(songUri, null, null, null, null)
        if (songCursor != null && songCursor.moveToFirst()) {
            val songId = songCursor.getColumnIndex(MediaStore.Audio.Media._ID)
            val songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val songArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            val songData = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA)
            val dateIndex = songCursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED)
            while (songCursor.moveToNext()) {
                val currentId = songCursor.getLong(songId)
                val currentTitle = songCursor.getString(songTitle)
                val currentArtist = songCursor.getString(songArtist)
                val currentData = songCursor.getString(songData)
                val currentDate = songCursor.getLong(dateIndex)
                arrayList.add(Song(currentId, currentTitle, currentArtist, currentData, currentDate))
            }
        }
        return arrayList
    }

    private fun display_favourites_by_searching() {
        if (favouriteContent.checkSize() > 0) {
            refreshList = ArrayList<Song>()
            getListFromDatabase = favouriteContent.queryDBList()!!
            val fetchListFromDevice = getSongsFromPhone()
            for (i in 0 until fetchListFromDevice.size) {
                for (j in 0 until getListFromDatabase.size) {
                    if ((getListFromDatabase?.get(j)?.songID) === (fetchListFromDevice?.get(i)?.songID)) {
                        refreshList.add(getListFromDatabase[j])
                    }
                }
            }
            val favouriteAdapter = FavouriteAdapter(refreshList, myActivity as Context)
            val mLayoutManager = LinearLayoutManager(activity)
            recyclerView.layoutManager = mLayoutManager
            recyclerView.itemAnimator = DefaultItemAnimator()
            recyclerView.adapter = favouriteAdapter
            recyclerView.setHasFixedSize(true)
        } else {
            recyclerView.visibility = View.INVISIBLE
            noFavourites.visibility = View.VISIBLE
        }
    }

}
