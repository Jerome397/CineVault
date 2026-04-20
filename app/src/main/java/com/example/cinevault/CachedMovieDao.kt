package com.example.cinevault

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CachedMovieDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(movies: List<CachedMovieEntity>)

    @Query("DELETE FROM cached_movies WHERE category = :category")
    suspend fun clearCategory(category: String)

    @Query("SELECT * FROM cached_movies WHERE category = :category ORDER BY title ASC")
    fun getMoviesByCategory(category: String): Flow<List<CachedMovieEntity>>
}