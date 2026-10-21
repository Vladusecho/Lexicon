package com.vladusecho.lexicon.data.repository

import androidx.core.net.toUri
import com.vladusecho.lexicon.data.local.AppDao
import com.vladusecho.lexicon.data.local.FileManagerHelper
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
    private val appDao: AppDao,
    private val fileManagerHelper: FileManagerHelper
) : DefinitionsRepository {

    override fun getDefinition(id: Int): Flow<Definition> {
        return appDao.getDefinition(id).map { it.toDefinition() }
    }

    override fun getDefinitions(): Flow<List<Definition>> {
        return appDao.getDefinitions().map { it.toDefinitions() }
    }

    override suspend fun createDefinition(
        definition: Definition,
        imageUri: String?
    ): Result<Unit> = runCatching {
        val finalImagePath = if (!imageUri.isNullOrBlank()) {
            if (imageUri.startsWith("content://") || imageUri.startsWith("file://")) {
                fileManagerHelper.saveImageToInternalStorage(imageUri.toUri())
            } else {
                imageUri
            }
        } else {
            definition.imgUri
        }
        val entity = definition.copy(imgUri = finalImagePath).toDefinitionEntity()
        appDao.upsertDefinition(entity)
    }.onFailure { if (it is CancellationException) throw it }

    override suspend fun updateDefinition(
        definition: Definition,
        newImageUri: String?,
        isImageRemoved: Boolean
    ): Result<Unit> = runCatching {
        val existingEntity = appDao.getDefinitionOnce(definition.id)
        val finalImagePath = when {
            isImageRemoved -> {
                fileManagerHelper.deleteImage(existingEntity?.imgUri)
                null
            }
            !newImageUri.isNullOrBlank() && (newImageUri.startsWith("content://") || newImageUri.startsWith("file://")) -> {
                val savedPath = fileManagerHelper.saveImageToInternalStorage(newImageUri.toUri())
                if (savedPath != null) {
                    fileManagerHelper.deleteImage(existingEntity?.imgUri)
                }
                savedPath ?: existingEntity?.imgUri
            }
            else -> {
                definition.imgUri ?: existingEntity?.imgUri
            }
        }
        val entity = definition.copy(imgUri = finalImagePath).toDefinitionEntity()
        appDao.upsertDefinition(entity)
    }.onFailure { if (it is CancellationException) throw it }

    override suspend fun deleteDefinition(id: Int): Result<Unit> = runCatching {
        val existing = appDao.getDefinitionOnce(id)
        if (existing?.imgUri != null) {
            fileManagerHelper.deleteImage(existing.imgUri)
        }
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
        val entity = appDao.getRandomDefinition(excludedId) ?: appDao.getRandomDefinitionAny()
        entity?.toDefinition()
    }.onFailure { if (it is CancellationException) throw it }

    override fun checkIsFavorite(id: Int): Flow<Boolean> {
        return appDao.checkIsFavorite(id)
    }

    override suspend fun toggleFavorite(id: Int, isFavorite: Boolean): Result<Unit> = runCatching {
        appDao.toggleFavorite(id, isFavorite)
    }.onFailure { if (it is CancellationException) throw it }

    override fun getFavorites(): Flow<List<Definition>> {
        return appDao.getFavorites().map { it.toDefinitions() }
    }

    override fun getDueDefinitions(currentTime: Long): Flow<List<Definition>> {
        return appDao.getDueDefinitions(currentTime).map { it.toDefinitions() }
    }

    override fun getDueWordsCount(currentTime: Long): Flow<Int> {
        return appDao.getDueDefinitionsCount(currentTime)
    }

    override suspend fun updateReviewProgress(
        id: Int,
        repetitions: Int,
        intervalDays: Int,
        easeFactor: Float,
        nextReviewDate: Long
    ): Result<Unit> = runCatching {
        appDao.updateReviewProgress(id, repetitions, intervalDays, easeFactor, nextReviewDate)
    }.onFailure { if (it is CancellationException) throw it }
}
