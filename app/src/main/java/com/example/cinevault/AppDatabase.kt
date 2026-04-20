package com.example.cinevault

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        FavoriteMovieEntity::class,
        WatchlistMovieEntity::class,
        CachedMovieEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteMovieDao(): FavoriteMovieDao
    abstract fun watchlistMovieDao(): WatchlistMovieDao
    abstract fun cachedMovieDao(): CachedMovieDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "cinevault_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}