package com.vladusecho.lexicon.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.vladusecho.lexicon.domain.entity.PartOfSpeech

@Entity(tableName = "definitions")
data class DefinitionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val word: String,
    val description: String,
    val imgUri: String?,
    val isFavorite: Boolean,
    val partOfSpeech: PartOfSpeech?
)