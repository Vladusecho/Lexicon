package com.vladusecho.lexicon.domain.repository

import com.vladusecho.lexicon.domain.entity.Definition
import com.vladusecho.lexicon.domain.entity.Settings
import kotlinx.coroutines.flow.Flow

interface DefinitionRepository {

    fun getDefinition(id: Int): Flow<Definition>

    fun getDefinitions(): Flow<List<Definition>>

    suspend fun createDefinition(definition: Definition)

    suspend fun updateDefinition(definition: Definition)

    suspend fun deleteDefinition(id: Int)

    fun getFavorites(): Flow<List<Definition>>

    fun checkIsFavorite(id: Int): Flow<Boolean>

    suspend fun toggleFavorite(id: Int)

    fun getSettings(): Flow<Settings>

    suspend fun toggleDarkMode(isDarkMode: Boolean)

    fun search(query: String): Flow<List<Definition>>
}