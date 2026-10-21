package com.vladusecho.lexicon.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.vladusecho.lexicon.domain.entity.PartOfSpeech

@Entity(tableName = "definitions")
data class DefinitionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val word: String,
    val description: String,
    val imgUri: String?,
    val isFavorite: Boolean,
    val partOfSpeech: PartOfSpeech,
    val createdAt: Long = System.currentTimeMillis(),
    val repetitions: Int = 0,
    val intervalDays: Int = 0,
    val easeFactor: Float = 2.5f,
    val nextReviewDate: Long = 0L
)
