package com.vladusecho.lexicon.domain.entity

import kotlinx.serialization.Serializable

@Serializable
data class Definition(
    val id: Int = 0,
    val word: String,
    val description: String,
    val imgUri: String? = null,
    val isFavorite: Boolean = false,
    val partOfSpeech: PartOfSpeech = PartOfSpeech.NOUN,
    val createdAt: Long = System.currentTimeMillis()
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
