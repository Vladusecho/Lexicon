package com.vladusecho.lexicon.di

import com.vladusecho.lexicon.data.repository.SimpleDefinitionRepositoryImpl
import com.vladusecho.lexicon.domain.repository.DefinitionRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
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
}