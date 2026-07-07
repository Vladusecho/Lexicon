package com.vladusecho.lexicon.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.vladusecho.lexicon.data.local.DataStoreHelper
import com.vladusecho.lexicon.data.local.dataStore
import com.vladusecho.lexicon.domain.entity.Definition
import com.vladusecho.lexicon.domain.entity.Settings
import com.vladusecho.lexicon.domain.repository.DefinitionRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject


class SimpleDefinitionRepositoryImpl @Inject constructor(
    private val dataStoreHelper: DataStoreHelper
) : DefinitionRepository {

    private val _definitions = MutableStateFlow(
        listOf(
            Definition(
                id = 1,
                word = "Толерантность",
                description = "характер, когда человек не обращает внимания на действия остальных людей",
                isFavorite = false
            ),
            Definition(
                id = 2,
                word = "Аффирмации",
                description = "что то там крутое и мотивирующее",
                isFavorite = false
            ),
        )
    )

    override fun getDefinition(id: Int): Flow<Definition> {
        return _definitions.map { it.first { definition -> definition.id == id } }
    }

    override fun getDefinitions(): Flow<List<Definition>> {
        return _definitions.map { it.sortedBy { definition -> definition.word.lowercase() } }
    }

    override suspend fun createDefinition(definition: Definition) {
        val currentList = _definitions.value
        val updatedList = currentList + definition
        _definitions.value = updatedList
    }

    override suspend fun updateDefinition(definition: Definition) {
        val currentList = _definitions.value
        val updatedList = currentList.map {
            if (it.id == definition.id) {
                definition
            } else {
                it
            }
        }
        _definitions.value = updatedList
    }

    override suspend fun deleteDefinition(id: Int) {
        val currentList = _definitions.value
        val updatedList = currentList.filter { it.id != id }
        _definitions.value = updatedList
    }

    override fun getFavorites(): Flow<List<Definition>> {
        return _definitions.map { it.filter { definition -> definition.isFavorite }.sortedBy { definition -> definition.word.lowercase() } }
    }

    override fun checkIsFavorite(id: Int): Flow<Boolean> {
        return _definitions.map { it.any { definition -> definition.id == id && definition.isFavorite } }
    }

    override suspend fun toggleFavorite(id: Int) {
        val currentList = _definitions.value
        val updatedList = currentList.map {
            if (it.id == id) {
                it.copy(isFavorite = !it.isFavorite)
            } else {
                it
            }
        }
        _definitions.value = updatedList
    }

    override fun getSettings(): Flow<Settings> {
        return dataStoreHelper.getSettings()
    }

    override suspend fun toggleDarkMode(isDarkMode: Boolean) {
        dataStoreHelper.context.dataStore.edit {
            it[dataStoreHelper.isDarkMode] = isDarkMode
        }
    }
}