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

    @Query("SELECT * FROM definitions")
    fun getDefinitions(): Flow<List<DefinitionEntity>>

    @Upsert
    suspend fun upsertDefinition(definitionEntity: DefinitionEntity)

    @Query("DELETE FROM definitions WHERE id = :id")
    suspend fun deleteDefinition(id: Int)

    @Query("SELECT * FROM definitions WHERE word LIKE '%' || :query || '%'")
    fun searchAll(query: String): Flow<List<DefinitionEntity>>

    @Query("SELECT * FROM definitions WHERE word LIKE '%' || :query || '%' AND isFavorite = 1")
    fun searchFavourites(query: String): Flow<List<DefinitionEntity>>

    @Query("SELECT isFavorite FROM definitions WHERE id = :id")
    fun checkIsFavorite(id: Int): Flow<Boolean>

    @Query("UPDATE definitions SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun toggleFavorite(id: Int, isFavorite: Boolean)

    @Query("SELECT * FROM definitions WHERE isFavorite = 1")
    fun getFavorites(): Flow<List<DefinitionEntity>>
}
