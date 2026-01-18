package com.fourshil.musicya.data.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.fourshil.musicya.data.model.Song

class SongsPagingSource(
    private val repository: MusicRepository
) : PagingSource<Int, Song>() {

    override fun getRefreshKey(state: PagingState<Int, Song>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Song> {
        val page = params.key ?: 0
        val pageSize = params.loadSize

        return try {
            val songs = repository.getSongsPaged(offset = page * pageSize, limit = pageSize)
            
            LoadResult.Page(
                data = songs,
                prevKey = if (page == 0) null else page - 1,
                nextKey = if (songs.isEmpty() || songs.size < pageSize) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
