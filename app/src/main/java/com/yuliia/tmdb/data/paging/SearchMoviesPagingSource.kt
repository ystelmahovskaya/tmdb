package com.yuliia.tmdb.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.yuliia.tmdb.data.mapper.toDomain
import com.yuliia.tmdb.data.repository.TMDBApi
import com.yuliia.tmdb.domain.model.Movie
import kotlin.coroutines.cancellation.CancellationException

class SearchMoviesPagingSource(
    private val api: TMDBApi,
    private val query: String
) : PagingSource<Int, Movie>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        val page = params.key ?: 1
        return try {
            val response = api.searchMovies(query, page)
            LoadResult.Page(
                data = response.toDomain(),
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (page >= response.totalPages) null else page + 1
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? {
        return state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchor)?.nextKey?.minus(1)
        }
    }
}
