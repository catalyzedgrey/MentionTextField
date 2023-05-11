package com.example.android.mentiontextfield.presentation.mention

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.items
import com.example.android.mentiontextfield.data.Mention
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun MentionsWindow(
    modifier: Modifier = Modifier,
    popupHeight: Dp,
    mentionsListItems: LazyPagingItems<Mention>?,
    onItemClicked: (mention: Mention) -> Unit
) {
    var mentionsLoading by remember { mutableStateOf(true) }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.padding(horizontal = 16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        LazyColumn(modifier = modifier.height(popupHeight)) {

            items(5) {
                if (mentionsListItems == null || mentionsListItems.itemCount == 0 || mentionsLoading) {
                    MentionItem(
                        isShimmerEffect = true,
                        modifier = Modifier,
                        pictureUrl = "",
                        name = "",
                        username = "",
                        isUser = true,
                    )
                }
            }

            mentionsListItems?.let {
                items(mentionsListItems) { mention ->
                    MentionItem(
                        isShimmerEffect = false,
                        modifier = Modifier.clickable(onClick = { //handle onClick
                            if (mention != null) {
                                onItemClicked(mention)
                            }
                        }),
                        pictureUrl = mention?.pictureUrl ?: "",
                        name = mention?.getFullName() ?: "",
                        isUser = true,
                    )

                }
            }

            mentionsListItems?.apply {
                when {
                    loadState.refresh is LoadState.NotLoading -> {
                        mentionsLoading = false
                    }

                    loadState.append is LoadState.NotLoading -> {
                        mentionsLoading = false
                    }
                }
            }
        }
    }

}