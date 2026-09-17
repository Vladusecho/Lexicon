package com.vladusecho.lexicon.data.repository

import android.content.Context
import androidx.core.net.toUri
import com.vladusecho.lexicon.domain.entity.Definition
import com.vladusecho.lexicon.domain.repository.BackupRepository
import com.vladusecho.lexicon.domain.repository.DefinitionsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json
import java.io.BufferedWriter
import java.io.OutputStreamWriter
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

class BackupRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val definitionsRepository: DefinitionsRepository
) : BackupRepository {

    private val jsonConfig = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    override suspend fun exportDefinitions(uriString: String): Result<Unit> =
        runCatching {
            val definitions = definitionsRepository.getDefinitions().first()
            val jsonString = jsonConfig.encodeToString(definitions)
            val uri = uriString.toUri()
            context.contentResolver.openOutputStream(uri)?.use {
                BufferedWriter(OutputStreamWriter(it)).use { writer ->
                    writer.write(jsonString)
                }
            } ?: IllegalStateException("Failed to open output stream")
        }

    override suspend fun importDefinitions(uriString: String): Result<Unit> =
        runCatching {
            val uri = uriString.toUri()
            val jsonString = context.contentResolver.openInputStream(uri)?.use {
                it.bufferedReader().use { reader ->
                    reader.readText()
                }
            } ?: throw IllegalStateException("Failed to open input stream")
            val definitions = jsonConfig.decodeFromString<List<Definition>>(jsonString)
            definitions.forEach {
                definitionsRepository.createDefinition(it)
            }
        }
}
