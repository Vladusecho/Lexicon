package com.vladusecho.lexicon.domain.repository

import com.vladusecho.lexicon.domain.entity.Definition
import kotlinx.coroutines.flow.Flow

interface FavouritesRepository {

    fun getFavorites(): Flow<List<Definition>>

    fun checkIsFavorite(id: Int): Flow<Boolean>

    suspend fun toggleFavorite(id: Int, isFavorite: Boolean)
}