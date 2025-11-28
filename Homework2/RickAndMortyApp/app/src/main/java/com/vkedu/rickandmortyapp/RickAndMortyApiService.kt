package com.vkedu.rickandmortyapp

import retrofit2.http.GET
import retrofit2.http.Query

interface RickAndMortyApiService {
    @GET("character")
    suspend fun getCharacters(
        @Query("page") page: Int
    ): CharacterResponse
}