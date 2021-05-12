package com.example.easymusicapp.ui.home

import android.app.Activity
import android.content.Context
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
import com.example.easymusicapp.entity.Song
import com.example.easymusicapp.adapters.SongAdapter
import java.util.*
import kotlin.collections.ArrayList

@Suppress("DEPRECATION")
class HomeFragment : Fragment() {

    private lateinit var nowPlayingButtonBar: RelativeLayout
    private lateinit var playPauseButton: ImageButton
    private lateinit var songTitle: TextView
    private lateinit var noSongs: RelativeLayout
    private lateinit var visibleLayout: RelativeLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var myActivity: Activity
    private lateinit var songAdapter: SongAdapter
    private lateinit var getSongsList: ArrayList<Song>
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_home, container, false)
        setHasOptionsMenu(true)
        visibleLayout = root.findViewById(R.id.visibleLayout)
        noSongs = root.findViewById(R.id.noSongs)
        nowPlayingButtonBar = root.findViewById(R.id.hiddenBarMainScreen)
        playPauseButton = root.findViewById(R.id.playPauseButton)
        recyclerView = root.findViewById(R.id.mainRecycleView)

        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        getSongsList = getLocalSongs()

        if (getSongsList.size == 0) {
            visibleLayout.visibility = View.INVISIBLE
            noSongs.visibility = View.VISIBLE
        } else {
            songAdapter = SongAdapter(getSongsList, myActivity as Context)
            val layoutManager = LinearLayoutManager(myActivity)
            recyclerView.layoutManager = layoutManager
            recyclerView.itemAnimator = DefaultItemAnimator()
            recyclerView.adapter = songAdapter

        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        myActivity = context as Activity
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        myActivity = activity
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.main, menu)
        return
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val switcher = item.itemId
        if (switcher == R.id.action_sort_by_name_a_z) {
            if (getSongsList.size != 0) {
                Collections.sort(getSongsList, Song.nameComparator)
            }
            songAdapter.notifyDataSetChanged()
            return false
        } else if (switcher == R.id.action_sort_by_name_z_a) {
            if (getSongsList.size != 0) {
                Collections.sort(getSongsList, Song.dateComparator)
            }
            songAdapter.notifyDataSetChanged()
            return false
        } else if (switcher == R.id.action_sort_by_date_newest) {
            if (getSongsList.size != 0) {
                Collections.sort(getSongsList, Song.dateComparator)
            }
            songAdapter.notifyDataSetChanged()
            return false
        } else if (switcher == R.id.action_sort_by_date_oldest) {
            if (getSongsList.size != 0) {
                Collections.sort(getSongsList, Song.dateComparator)
            }
            songAdapter.notifyDataSetChanged()
            return false
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getLocalSongs(): ArrayList<Song> {
        val listSong = ArrayList<Song>()
        val contentResolver = myActivity.contentResolver
        val collection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DATE_ADDED,
                MediaStore.Audio.Media.DATA
        )
        val query = contentResolver?.query(collection,projection,null,null,null)

        query?.use {
            cursor ->
            if (cursor.moveToFirst()) {
                val songId = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val songTitle = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                val songArtist = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                val songData = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
                val dateIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)
                while (cursor.moveToNext()) {
                    val curId = cursor.getLong(songId)
                    val curTitle = cursor.getString(songTitle)
                    val curArtist = cursor.getString(songArtist)
                    val curData = cursor.getString(songData)
                    val curDate = cursor.getLong(dateIndex)
                    listSong.add(Song(curId, curTitle, curArtist, curData, curDate))
                }
            }
        }
            return listSong
    }
}
