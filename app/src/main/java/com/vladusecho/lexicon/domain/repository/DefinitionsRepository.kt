package com.vladusecho.lexicon.domain.repository

import com.vladusecho.lexicon.domain.entity.Definition
import kotlinx.coroutines.flow.Flow

interface DefinitionsRepository {

    fun getDefinition(id: Int): Flow<Definition>

    fun getDefinitions(): Flow<List<Definition>>

    suspend fun createDefinition(
        definition: Definition,
        imageUri: String? = null
    ): Result<Unit>

    suspend fun updateDefinition(
        definition: Definition,
        newImageUri: String? = null,
        isImageRemoved: Boolean = false
    ): Result<Unit>

    suspend fun deleteDefinition(id: Int): Result<Unit>

    fun search(query: String, searchFavourite: Boolean): Flow<List<Definition>>

    suspend fun getRandomDefinition(excludedId: Int): Result<Definition?>

    fun checkIsFavorite(id: Int): Flow<Boolean>

    suspend fun toggleFavorite(id: Int, isFavorite: Boolean): Result<Unit>

    fun getFavorites(): Flow<List<Definition>>
}
