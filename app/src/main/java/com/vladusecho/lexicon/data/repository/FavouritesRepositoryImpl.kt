package com.vladusecho.lexicon.data.repository

import com.vladusecho.lexicon.data.local.AppDao
import com.vladusecho.lexicon.data.mapper.toDefinitions
import com.vladusecho.lexicon.domain.entity.Definition
import com.vladusecho.lexicon.domain.repository.FavouritesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FavouritesRepositoryImpl @Inject constructor(
    private val appDao: AppDao
): FavouritesRepository {
    override fun getFavorites(): Flow<List<Definition>> {
        return appDao.getFavorites().map { it.toDefinitions() }
    }

    override fun checkIsFavorite(id: Int): Flow<Boolean> {
        return appDao.checkIsFavorite(id)
    }

    override suspend fun toggleFavorite(id: Int, isFavorite: Boolean) {
        appDao.toggleFavorite(id, isFavorite)
    }
}