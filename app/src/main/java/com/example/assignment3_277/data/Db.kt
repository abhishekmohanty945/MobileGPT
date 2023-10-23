package com.example.assignment3_277.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Prompt:: class, Response:: class], version = 1, exportSchema = false)
abstract class Db: RoomDatabase() {
    abstract fun promptDao(): PromptDao
    abstract fun responseDao(): ResponseDao

    companion object{
        @Volatile
        private var INSTANCE: Db? = null

        fun getDatabase(context: Context): Db{
            val tempInstance = INSTANCE
            if(tempInstance != null){
                return tempInstance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    Db::class.java,
                    "db"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}