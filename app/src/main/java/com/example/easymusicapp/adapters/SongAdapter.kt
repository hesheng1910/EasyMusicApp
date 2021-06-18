package com.example.easymusicapp.adapters

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.easymusicapp.R
import com.example.easymusicapp.entity.Song
import com.example.easymusicapp.ui.songplaying.SongPlayingFragment

class SongAdapter(_listSong: ArrayList<Song>, _context: Context) : RecyclerView.Adapter<SongAdapter.ViewHolder>() {

    var listSong : ArrayList<Song> = _listSong
    var context: Context = _context

    // Init content for a item
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var songTitle: TextView = view.findViewById(R.id.songTitle)
        var artist: TextView = view.findViewById(R.id.songArtist)
        var contentItem: RelativeLayout = view.findViewById(R.id.contentRow)
    }
    // Init layout of a item using layout item_song
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_song, parent, false)

        return ViewHolder(itemView)
    }
    // Binding data from list song to view item
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val songObject = listSong[position]
        holder.songTitle.text = songObject.songTitle
        holder.artist.text = songObject.artist
        holder.contentItem.setOnClickListener {

                val songPlayingFragment = SongPlayingFragment()
                val args = Bundle()
                args.putString("songArtist", songObject.artist)
                args.putString("path", songObject.songData)
                args.putString("songTitle", songObject.songTitle)
                args.putInt("songId", songObject.songID.toInt() as Int)
                args.putInt("songPosition",position)
                args.putParcelableArrayList("songData", listSong)
                songPlayingFragment.arguments = args
                (context as FragmentActivity).supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.nav_host_fragment, songPlayingFragment)
                        .addToBackStack("SongPlayingFragment")
                        .commit()
        }
    }
    // Get  quantity item
    override fun getItemCount(): Int = listSong.size
}