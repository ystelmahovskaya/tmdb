package com.yuliia.tmdb.data.paging

import androidx.paging.PagingSource
import com.yuliia.tmdb.TestData
import com.yuliia.tmdb.data.repository.TMDBApi
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SearchMoviesPagingSourceTest {

    private val api: TMDBApi = mockk()

    @Test
    fun `load returns search results`() = runTest {
        val response = TestData.movieListResponse(
            listOf(TestData.movieDto(id = 1, title = "Batman"))
        ).copy(page = 1, totalPages = 2)
        coEvery { api.searchMovies("batman", 1) } returns response

        val pagingSource = SearchMoviesPagingSource(api, "batman")
        val result = pagingSource.load(PagingSource.LoadParams.Refresh(null, 20, false))

        val page = result as PagingSource.LoadResult.Page
        assertEquals(1, page.data.size)
        assertEquals("Batman", page.data[0].title)
        assertNull(page.prevKey)
        assertEquals(2, page.nextKey)
    }

    @Test
    fun `load returns null nextKey on last page`() = runTest {
        val response = TestData.movieListResponse().copy(page = 5, totalPages = 5)
        coEvery { api.searchMovies("query", 5) } returns response

        val pagingSource = SearchMoviesPagingSource(api, "query")
        val result = pagingSource.load(PagingSource.LoadParams.Refresh(5, 20, false))

        val page = result as PagingSource.LoadResult.Page
        assertNull(page.nextKey)
    }

    @Test
    fun `load returns Error on exception`() = runTest {
        coEvery { api.searchMovies("test", 1) } throws RuntimeException("Timeout")

        val pagingSource = SearchMoviesPagingSource(api, "test")
        val result = pagingSource.load(PagingSource.LoadParams.Refresh(null, 20, false))

        assertTrue(result is PagingSource.LoadResult.Error)
    }
}
