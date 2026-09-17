package com.vladusecho.lexicon.domain.entity

import kotlinx.serialization.Serializable

@Serializable
data class Definition(
    val id: Int,
    val word: String,
    val description: String,
    val imgUri: String? = null,
    val isFavorite: Boolean,
    val partOfSpeech: PartOfSpeech
)

@Serializable
enum class PartOfSpeech(
    val label: String
) {
    NOUN(
        label = "Существительное"
    ),
    VERB(
        label = "Глагол"
    ),
    ADJECTIVE(
        label = "Прилагательное"
    ),
    ADVERB(
        label = "Наречие"
    ),
    PARTICIPLE(
        label = "Причастие"
    ),
    ADVERBIAL_PARTICIPLE(
        label = "Деепричастие"
    )
}
