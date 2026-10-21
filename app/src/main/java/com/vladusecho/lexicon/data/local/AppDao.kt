package com.vladusecho.lexicon.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.vladusecho.lexicon.data.entity.DefinitionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {

    @Query("SELECT * FROM definitions WHERE id = :id")
    fun getDefinition(id: Int): Flow<DefinitionEntity>

    @Query("SELECT * FROM definitions WHERE id = :id")
    suspend fun getDefinitionOnce(id: Int): DefinitionEntity?

    @Query("SELECT * FROM definitions ORDER BY word COLLATE NOCASE ASC")
    fun getDefinitions(): Flow<List<DefinitionEntity>>

    @Upsert
    suspend fun upsertDefinition(definitionEntity: DefinitionEntity)

    @Query("DELETE FROM definitions WHERE id = :id")
    suspend fun deleteDefinition(id: Int)

    @Query("SELECT * FROM definitions WHERE word LIKE '%' || :query || '%' ORDER BY word COLLATE NOCASE ASC")
    fun searchAll(query: String): Flow<List<DefinitionEntity>>

    @Query("SELECT * FROM definitions WHERE word LIKE '%' || :query || '%' AND isFavorite = 1 ORDER BY word COLLATE NOCASE ASC")
    fun searchFavourites(query: String): Flow<List<DefinitionEntity>>

    @Query("SELECT isFavorite FROM definitions WHERE id = :id")
    fun checkIsFavorite(id: Int): Flow<Boolean>

    @Query("UPDATE definitions SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun toggleFavorite(id: Int, isFavorite: Boolean)

    @Query("SELECT * FROM definitions WHERE isFavorite = 1 ORDER BY word COLLATE NOCASE ASC")
    fun getFavorites(): Flow<List<DefinitionEntity>>

    @Query("SELECT * FROM definitions WHERE id != :excludedId ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomDefinition(excludedId: Int): DefinitionEntity?

    @Query("SELECT * FROM definitions ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomDefinitionAny(): DefinitionEntity?

    @Query("SELECT * FROM definitions WHERE nextReviewDate <= :currentTime ORDER BY nextReviewDate ASC")
    fun getDueDefinitions(currentTime: Long): Flow<List<DefinitionEntity>>

    @Query("SELECT COUNT(*) FROM definitions WHERE nextReviewDate <= :currentTime")
    fun getDueDefinitionsCount(currentTime: Long): Flow<Int>

    @Query("UPDATE definitions SET repetitions = :repetitions, intervalDays = :intervalDays, easeFactor = :easeFactor, nextReviewDate = :nextReviewDate WHERE id = :id")
    suspend fun updateReviewProgress(
        id: Int,
        repetitions: Int,
        intervalDays: Int,
        easeFactor: Float,
        nextReviewDate: Long
    )
}
