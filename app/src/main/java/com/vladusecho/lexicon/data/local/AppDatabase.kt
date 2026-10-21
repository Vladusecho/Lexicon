package com.vladusecho.lexicon.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.vladusecho.lexicon.data.entity.DefinitionEntity

@Database(
    entities = [
        DefinitionEntity::class,
    ],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun appDao(): AppDao

    companion object {
        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE definitions ADD COLUMN createdAt INTEGER NOT NULL DEFAULT 0")
            }
        }
    }
}
