package com.vkedu.rickandmortyapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.Flow

class CharacterViewModel : ViewModel() {

    val characters: Flow<PagingData<Character>> = Pager(PagingConfig(pageSize = 20)) {
        CharacterPagingSource(NetworkModule.apiService)
    }.flow.cachedIn(viewModelScope)

}