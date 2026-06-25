package com.vladusecho.lexicon.domain.repository

import com.vladusecho.lexicon.domain.entity.Definition
import kotlinx.coroutines.flow.Flow

interface DefinitionRepository {

    suspend fun getDefinition(id: Int): Definition

    fun getDefinitions(): Flow<List<Definition>>

    suspend fun createDefinition(definition: Definition)

}