package com.example.android.mentiontextfield.presentation.mention

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.example.android.mentiontextfield.data.Mention
import com.example.android.mentiontextfield.domain.MentionsSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import java.util.regex.Pattern
import javax.inject.Inject

@HiltViewModel
class MentionsViewModel @Inject constructor() : ViewModel() {

    val textState = mutableStateOf(TextFieldValue())

    val mentionsMap: MutableMap<Pattern, Mention> = mutableMapOf()

    private var startSearch = true

    val searchQueryFlow = MutableStateFlow<String?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val pagedMentions = searchQueryFlow.flatMapLatest { query ->
        Pager(PagingConfig(pageSize = 10)) {
            MentionsSource(query ?: "")
        }.flow.cachedIn(viewModelScope)
    }

    fun searchMentions(query: String) {
        startSearch = true
        searchQueryFlow.value = query
    }
}

