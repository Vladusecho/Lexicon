package com.vladusecho.lexicon.domain.usecase.definition

import com.vladusecho.lexicon.domain.repository.BackupRepository
import javax.inject.Inject

class ImportDefinitionsUseCase  @Inject constructor(
    private val backupRepository: BackupRepository
){
    suspend operator fun invoke(uriString: String): Result<Unit> {
        return backupRepository.importDefinitions(uriString)
    }
}