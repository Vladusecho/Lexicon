package com.vladusecho.lexicon.di

import android.content.Context
import androidx.room.Room
import com.vladusecho.lexicon.data.local.AppDatabase
import com.vladusecho.lexicon.data.local.DataStoreHelper
import com.vladusecho.lexicon.data.local.FileManagerHelper
import com.vladusecho.lexicon.data.repository.DefinitionsRepositoryImpl
import com.vladusecho.lexicon.data.repository.FavouritesRepositoryImpl
import com.vladusecho.lexicon.data.repository.SettingsRepositoryImpl
import com.vladusecho.lexicon.data.repository.SimpleRepositoryImpl
import com.vladusecho.lexicon.domain.repository.DefinitionsRepository
import com.vladusecho.lexicon.domain.repository.FavouritesRepository
import com.vladusecho.lexicon.domain.repository.SettingsRepository
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
        definitionRepositoryImpl: SimpleRepositoryImpl
    ): DefinitionsRepository

    @Binds
    @Singleton
    fun bindSettingsRepository(
        settingsRepositoryImpl: SettingsRepositoryImpl
    ): SettingsRepository

    @Binds
    @Singleton
    fun bindFavouritesRepository(
        favouritesRepositoryImpl: FavouritesRepositoryImpl
    ): FavouritesRepository

    companion object {

        @Provides
        @Singleton
        fun provideAppDatabase(
            @ApplicationContext context: Context
        ) = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "app_database"
        ).build()

        @Provides
        @Singleton
        fun provideAppDao(
            appDatabase: AppDatabase
        ) = appDatabase.appDao()


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
