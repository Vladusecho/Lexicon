package com.vladusecho.lexicon.domain.usecase.definition

import com.vladusecho.lexicon.domain.entity.Definition
import com.vladusecho.lexicon.domain.entity.PartOfSpeech
import com.vladusecho.lexicon.domain.repository.DefinitionsRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test

class CreateDefinitionUseCaseTest {

    private val definitionsRepository: DefinitionsRepository = mockk()
    private val useCase = CreateDefinitionUseCase(definitionsRepository)

    @Test
    fun `invoke delegates to definitionsRepository createDefinition`() = runTest {
        val definition = Definition(
            id = 0,
            word = "Эрудиция",
            description = "Глубокие познания в разных областях",
            isFavorite = false,
            partOfSpeech = PartOfSpeech.NOUN
        )

        coEvery { definitionsRepository.createDefinition(definition, null) } returns Result.success(Unit)

        val result = useCase(definition, null)

        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { definitionsRepository.createDefinition(definition, null) }
    }
}
