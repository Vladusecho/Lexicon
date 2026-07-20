package com.vladusecho.lexicon.domain.entity

data class Definition(
    val id: Int,
    val word: String,
    val description: String,
    val imgUri: String? = null,
    val isFavorite: Boolean,
    val partOfSpeech: PartOfSpeech? = null
)

enum class PartOfSpeech {
    NOUN,
    VERB,
    ADJECTIVE,
    ADVERB
}
