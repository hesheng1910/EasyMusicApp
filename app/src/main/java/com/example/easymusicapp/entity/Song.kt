package com.example.easymusicapp.entity

import android.os.Parcel
import android.os.Parcelable
import java.util.*
import kotlin.Comparator

data class Song(var songID: Long, var songTitle: String?, var artist: String?,
                var songData: String?, var dateAdded: Long) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readLong(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readLong()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(songID)
        parcel.writeString(songTitle)
        parcel.writeString(artist)
        parcel.writeString(songData)
        parcel.writeLong(dateAdded)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Song> {
        override fun createFromParcel(parcel: Parcel): Song {
            return Song(parcel)
        }

        override fun newArray(size: Int): Array<Song?> {
            return arrayOfNulls(size)
        }
        var nameComparator: Comparator<Song> = Comparator<Song> { song1, song2 ->
            val songOne = song1.songTitle!!.toUpperCase(Locale.ROOT)
            val songTwo = song2.songTitle!!.toUpperCase(Locale.ROOT)
            songOne.compareTo(songTwo)
        }
        var dateComparator: Comparator<Song> = Comparator<Song> { song1, song2 ->
            val songOne = song1.dateAdded.toDouble()
            val songTwo = song2.dateAdded.toDouble()
            songTwo.compareTo(songOne)
        }
    }


}