package com.vladusecho.lexicon.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.vladusecho.lexicon.data.entity.DefinitionEntity

@Database(
    entities = [
        DefinitionEntity::class,
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun appDao(): AppDao
}
