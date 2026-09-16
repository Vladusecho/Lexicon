package com.vladusecho.lexicon.domain.repository

interface BackupRepository {

    suspend fun exportDefinitions(uriString: String): Result<Unit>
}