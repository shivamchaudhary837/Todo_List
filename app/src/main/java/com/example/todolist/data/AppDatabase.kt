package com.example.todolist.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.todolist.ui.DB_NAME


@Database(entities = [TodoModel::class],version = 1)
abstract class AppDatabase:RoomDatabase() {
    abstract fun todoDao(): TodoDao

    companion object{
        @Volatile
        private var instance: AppDatabase?=null

        fun getDatabase(context: Context)= instance ?:
        synchronized(this){
            Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                DB_NAME
            ).build().also { instance =it }
        }
    }
}