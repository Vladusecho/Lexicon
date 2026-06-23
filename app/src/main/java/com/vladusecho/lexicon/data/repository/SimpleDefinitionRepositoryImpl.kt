package com.vladusecho.lexicon.data.repository

import com.vladusecho.lexicon.domain.entity.Definition
import com.vladusecho.lexicon.domain.repository.DefinitionRepository
import kotlinx.coroutines.delay
import javax.inject.Inject

class SimpleDefinitionRepositoryImpl @Inject constructor() : DefinitionRepository {

    val definitions = mutableListOf<Definition>().apply {
        repeat(20) {
            add(Definition(
                id = it,
                word = "Толерантность #$it",
                description = "характер, когда человек не обращает внимания на действия остальных людей"
            ))
        }
    }

    override suspend fun getDefinition(id: Int): Definition {
        delay(1000)
        return definitions.first { it.id == id }
    }

    override suspend fun getDefinitions(): List<Definition> {
        delay(2000)
        return definitions.toList()
    }
}