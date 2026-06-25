package com.vladusecho.lexicon.data.repository

import com.vladusecho.lexicon.domain.entity.Definition
import com.vladusecho.lexicon.domain.repository.DefinitionRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SimpleDefinitionRepositoryImpl @Inject constructor() : DefinitionRepository {

    private val _definitions = MutableStateFlow(
        listOf(
            Definition(
                id = 1,
                word = "Толерантность 1",
                description = "характер, когда человек не обращает внимания на действия остальных людей"
            ),
            Definition(
                id = 2,
                word = "Толерантность 2",
                description = "характер, когда человек не обращает внимания на действия остальных людей"
            ),
        )
    )

    override suspend fun getDefinition(id: Int): Definition {
        delay(1000)
        return _definitions.value.first { it.id == id }
    }

    override fun getDefinitions(): Flow<List<Definition>> {
        return _definitions.map { it.sortedBy { definition -> definition.word } }
    }

    override suspend fun createDefinition(definition: Definition) {
        val currentList = _definitions.value
        val updatedList = currentList + definition
        _definitions.value = updatedList
    }
}