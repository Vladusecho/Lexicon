package com.vladusecho.lexicon.data.mapper

import com.vladusecho.lexicon.data.entity.DefinitionEntity
import com.vladusecho.lexicon.domain.entity.Definition

fun DefinitionEntity.toDefinition() = Definition(
    id = this.id,
    word = this.word,
    description = this.description,
    imgUri = this.imgUri,
    isFavorite = this.isFavorite
)

fun List<DefinitionEntity>.toDefinitions() = this.map { it.toDefinition() }

fun Definition.toDefinitionEntity() = DefinitionEntity(
    id = this.id,
    word = this.word,
    description = this.description,
    imgUri = this.imgUri,
    isFavorite = this.isFavorite
)