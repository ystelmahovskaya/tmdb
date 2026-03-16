package com.yuliia.tmdb.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface MovieDao {

    @Query("SELECT * FROM movies WHERE isTrending = 1 ORDER BY page ASC, popularity DESC")
    fun getTrendingMoviesPaged(): PagingSource<Int, MovieEntity>

    @Query("SELECT * FROM movies WHERE id = :id")
    suspend fun getMovieById(id: Int): MovieEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovies(movies: List<MovieEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovie(movie: MovieEntity)

    @Query("DELETE FROM movies WHERE isTrending = 1")
    suspend fun clearTrending()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRemoteKey(key: RemoteKeyEntity)

    @Query("SELECT * FROM remote_keys WHERE id = :id")
    suspend fun getRemoteKey(id: String): RemoteKeyEntity?

    @Query("DELETE FROM remote_keys WHERE id = :id")
    suspend fun clearRemoteKey(id: String)

    // clear old data + insert fresh page + update remote key in one transaction
    @Transaction
    suspend fun refreshTrending(remoteKeyId: String, movies: List<MovieEntity>, remoteKey: RemoteKeyEntity) {
        clearTrending()
        clearRemoteKey(remoteKeyId)
        insertMovies(movies)
        insertRemoteKey(remoteKey)
    }
}
