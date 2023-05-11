package com.example.android.mentiontextfield.domain

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.android.mentiontextfield.data.Mention

class MentionsSource(query: String?) : PagingSource<Int, Mention>() {
    override fun getRefreshKey(state: PagingState<Int, Mention>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Mention> {
        //This is dummy data, you should replace this with your own paginated data
        //And you can pass the query here to the real request

        val mentionsList = mutableListOf<Mention>()
        for (i in 0..10) {
            mentionsList.add(
                Mention("$i",
                    name = "name $i",
                    pictureUrl = "https://upload.wikimedia.org/wikipedia/commons/7/7c/Profile_avatar_placeholder_large.png?20150327203541",
                    type = "user")
            )
        }
        return LoadResult.Page(
            data = mentionsList,
            prevKey = null,
            nextKey = null
        )
    }


}