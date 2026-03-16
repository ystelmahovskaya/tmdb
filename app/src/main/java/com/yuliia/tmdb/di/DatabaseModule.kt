package com.yuliia.tmdb.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.yuliia.tmdb.data.local.MovieDao
import com.yuliia.tmdb.data.local.TMDBDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// v2: added trailer URL support
private val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE movies ADD COLUMN trailerUrl TEXT DEFAULT NULL")
    }
}

// v3: added pagination support (page ordering + remote keys for RemoteMediator)
private val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE movies ADD COLUMN page INTEGER NOT NULL DEFAULT 0")
        db.execSQL(
            """CREATE TABLE IF NOT EXISTS remote_keys (
                id TEXT NOT NULL PRIMARY KEY,
                nextPage INTEGER,
                lastUpdated INTEGER NOT NULL
            )"""
        )
    }
}

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): TMDBDatabase {
        return Room.databaseBuilder(
            context,
            TMDBDatabase::class.java,
            "tmdb_database"
        ).addMigrations(MIGRATION_1_2, MIGRATION_2_3).build()
    }

    @Provides
    @Singleton
    fun provideMovieDao(database: TMDBDatabase): MovieDao {
        return database.movieDao()
    }
}
