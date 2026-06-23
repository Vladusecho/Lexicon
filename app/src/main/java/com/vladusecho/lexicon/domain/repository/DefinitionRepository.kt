package com.vladusecho.lexicon.domain.repository

import com.vladusecho.lexicon.domain.entity.Definition

interface DefinitionRepository {

    suspend fun getDefinition(id: Int): Definition

    suspend fun getDefinitions(): List<Definition>

}