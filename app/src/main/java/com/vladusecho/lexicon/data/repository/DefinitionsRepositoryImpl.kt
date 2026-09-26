package com.vladusecho.lexicon.data.repository

import com.vladusecho.lexicon.data.local.AppDao
import com.vladusecho.lexicon.data.mapper.toDefinition
import com.vladusecho.lexicon.data.mapper.toDefinitionEntity
import com.vladusecho.lexicon.data.mapper.toDefinitions
import com.vladusecho.lexicon.domain.entity.Definition
import com.vladusecho.lexicon.domain.repository.DefinitionsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

class DefinitionsRepositoryImpl @Inject constructor(
    private val appDao: AppDao
) : DefinitionsRepository {
    override fun getDefinition(id: Int): Flow<Definition> {
        return appDao.getDefinition(id).map { it.toDefinition() }
    }

    override fun getDefinitions(): Flow<List<Definition>> {
        return appDao.getDefinitions().map { it.toDefinitions() }
    }

    override suspend fun createDefinition(definition: Definition): Result<Unit> = runCatching {
        appDao.upsertDefinition(definition.toDefinitionEntity())
    }.onFailure { if (it is CancellationException) throw it }

    override suspend fun updateDefinition(definition: Definition): Result<Unit> = runCatching {
        appDao.upsertDefinition(definition.toDefinitionEntity())
    }.onFailure { if (it is CancellationException) throw it }

    override suspend fun deleteDefinition(id: Int): Result<Unit> = runCatching {
        appDao.deleteDefinition(id)
    }.onFailure { if (it is CancellationException) throw it }

    override fun search(
        query: String,
        searchFavourite: Boolean
    ): Flow<List<Definition>> {
        return if (searchFavourite) {
            appDao.searchFavourites(query)
        } else {
            appDao.searchAll(query)
        }.map { it.toDefinitions() }
    }

    override suspend fun getRandomDefinition(excludedId: Int): Result<Definition?> = runCatching {
        appDao.getRandomDefinition(excludedId)?.toDefinition()
    }.onFailure { if (it is CancellationException) throw it }
}