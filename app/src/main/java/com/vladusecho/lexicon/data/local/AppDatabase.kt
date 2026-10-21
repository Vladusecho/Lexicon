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
    version = 5,
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

        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE definitions ADD COLUMN repetitions INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE definitions ADD COLUMN intervalDays INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE definitions ADD COLUMN easeFactor REAL NOT NULL DEFAULT 2.5")
                db.execSQL("ALTER TABLE definitions ADD COLUMN nextReviewDate INTEGER NOT NULL DEFAULT 0")
            }
        }
    }
}
