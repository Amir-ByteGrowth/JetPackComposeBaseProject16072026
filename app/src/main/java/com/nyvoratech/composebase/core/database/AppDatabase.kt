package com.nyvoratech.composebase.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.nyvoratech.composebase.ui.users.data.local.UserDao
import com.nyvoratech.composebase.ui.users.data.local.UserEntity

@Database(
    entities = [UserEntity::class],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {
        const val DATABASE_NAME = "composebase.db"
    }
}
