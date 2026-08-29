package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.BusinessCardDao
import com.example.data.local.dao.CustomQrDao
import com.example.data.local.dao.ScanDao
import com.example.data.local.dao.VaultDao
import com.example.data.local.entity.BusinessCardEntity
import com.example.data.local.entity.CustomQrEntity
import com.example.data.local.entity.ScanRecordEntity
import com.example.data.local.entity.VaultItemEntity

@Database(
    entities = [
        ScanRecordEntity::class,
        CustomQrEntity::class,
        VaultItemEntity::class,
        BusinessCardEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun scanDao(): ScanDao
    abstract fun customQrDao(): CustomQrDao
    abstract fun vaultDao(): VaultDao
    abstract fun businessCardDao(): BusinessCardDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "cass_easy_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
