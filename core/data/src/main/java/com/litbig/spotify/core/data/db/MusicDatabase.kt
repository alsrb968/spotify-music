package com.litbig.spotify.core.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.litbig.spotify.core.data.model.local.MusicMetadataEntity

@Database(
    entities = [MusicMetadataEntity::class],
    version = 1,
    exportSchema = false
)
abstract class MusicDatabase : RoomDatabase() {
    abstract fun musicMetadataDao(): MusicMetadataDao

    companion object {
        private const val DB_NAME = "metadata.db"

        fun getInstance(context: Context): MusicDatabase =
            Room.databaseBuilder(context, MusicDatabase::class.java, DB_NAME)
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build()
    }
}