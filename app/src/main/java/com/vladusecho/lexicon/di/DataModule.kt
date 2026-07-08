package com.vladusecho.lexicon.di

import android.content.Context
import com.vladusecho.lexicon.data.local.DataStoreHelper
import com.vladusecho.lexicon.data.local.FileManagerHelper
import com.vladusecho.lexicon.data.repository.SimpleDefinitionRepositoryImpl
import com.vladusecho.lexicon.domain.repository.DefinitionRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {

    @Binds
    @Singleton
    fun bindDefinitionRepository(
        definitionRepositoryImpl: SimpleDefinitionRepositoryImpl
    ): DefinitionRepository

    companion object {

        @Provides
        @Singleton
        fun provideDataStoreHelper(
            @ApplicationContext context: Context
        ): DataStoreHelper {
            return DataStoreHelper(context)
        }

        @Provides
        @Singleton
        fun provideFileManagerHelper(
            @ApplicationContext context: Context
        ): FileManagerHelper {
            return FileManagerHelper(context)
        }
    }
}
