package com.vladusecho.lexicon.data.repository

import com.vladusecho.lexicon.domain.entity.Definition
import com.vladusecho.lexicon.domain.repository.DefinitionsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject


class SimpleRepositoryImpl @Inject constructor() : DefinitionsRepository {

    private val _definitions = MutableStateFlow(
        listOf(
            Definition(
                id = 1,
                word = "Толерантность",
                description = "терпимость к иному мировоззрению, образу жизни, поведению и обычаям. Она предполагает уважение чужого мнения и активное принятие многообразия культур, но не означает безразличие или отказ от собственных принципов.",
                isFavorite = false
            ),
            Definition(
                id = 2,
                word = "Аффирмации",
                description = "короткие, позитивные утверждения или фразы самовнушения, которые при регулярном повторении помогают создать правильный психологический настрой, справиться со стрессом и улучшить отношение к себе.",
                isFavorite = false
            ),
            Definition(
                id = 3,
                word = "Бойкот",
                description = "прекращение отношений, деловых, политических или личных контактов с отдельным лицом, организацией, компанией или государством в знак протеста против их действий или политики",
                isFavorite = false
            ),
            Definition(
                id = 4,
                word = "Вассалитет",
                description = "средневековая система иерархической зависимости между феодалами. В обмен на земельный надел (феод) и защиту, вассал приносил сеньору клятву верности, обязуясь платить налоги и нести военную службу.",
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

//    override fun getFavorites(): Flow<List<Definition>> {
//        return _definitions.map {
//            it.filter { definition -> definition.isFavorite }
//                .sortedBy { definition -> definition.word.lowercase() }
//        }
//    }
//
//    override fun checkIsFavorite(id: Int): Flow<Boolean> {
//        return _definitions.map { it.any { definition -> definition.id == id && definition.isFavorite } }
//    }
//
//    override suspend fun toggleFavorite(id: Int) {
//        val currentList = _definitions.value
//        val updatedList = currentList.map {
//            if (it.id == id) {
//                it.copy(isFavorite = !it.isFavorite)
//            } else {
//                it
//            }
//        }
//        _definitions.value = updatedList
//    }

    override fun search(query: String, searchFavourite: Boolean): Flow<List<Definition>> {
        return _definitions.map {
            if (searchFavourite) {
                it.filter { definition ->
                    definition.isFavorite && definition.word.startsWith(
                        query.trim(),
                        ignoreCase = true
                    )
                }
                    .sortedBy { definition -> definition.word.lowercase() }
            } else {
                it.filter { definition ->
                    definition.word.startsWith(
                        query.trim(),
                        ignoreCase = true
                    )
                }
                    .sortedBy { definition -> definition.word.lowercase() }
            }
        }
    }
}