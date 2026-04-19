package com.example.cinevault

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WatchlistMovieDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(movie: WatchlistMovieEntity)

    @Delete
    suspend fun delete(movie: WatchlistMovieEntity)

    @Query("SELECT * FROM watchlist_movies ORDER BY title ASC")
    fun getAllWatchlist(): Flow<List<WatchlistMovieEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM watchlist_movies WHERE imdbID = :id)")
    suspend fun isInWatchlist(id: String): Boolean
}