package com.vladusecho.lexicon.domain.repository

import com.vladusecho.lexicon.domain.entity.Definition
import kotlinx.coroutines.flow.Flow

interface DefinitionRepository {

    fun getDefinition(id: Int): Flow<Definition>

    fun getDefinitions(): Flow<List<Definition>>

    suspend fun createDefinition(definition: Definition)

    suspend fun updateDefinition(definition: Definition)

    suspend fun deleteDefinition(id: Int)

    fun getFavorites(): Flow<List<Definition>>
}