package com.vkedu.rickandmortyapp

data class Character(
    val id: Int,
    val name: String,
    val species: String,
    val image: String,
    val episode: List<String>
)

data class CharacterResponse(
    val info: Info,
    val results: List<Character>
)

data class Info(
    val next: String?
)