package com.vladusecho.lexicon.domain.repository

import com.vladusecho.lexicon.domain.entity.Definition
import kotlinx.coroutines.flow.Flow

interface DefinitionsRepository {

    fun getDefinition(id: Int): Flow<Definition>

    fun getDefinitions(): Flow<List<Definition>>

    suspend fun createDefinition(definition: Definition): Result<Unit>

    suspend fun updateDefinition(definition: Definition): Result<Unit>

    suspend fun deleteDefinition(id: Int): Result<Unit>

    fun search(query: String, searchFavourite: Boolean): Flow<List<Definition>>

    suspend fun getRandomDefinition(excludedId: Int): Result<Definition?>

}