package com.vladusecho.lexicon.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.vladusecho.lexicon.data.entity.DefinitionEntity

@Database(
    entities = [
        DefinitionEntity::class,
    ],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun appDao(): AppDao
}
