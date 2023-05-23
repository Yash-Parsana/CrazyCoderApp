package com.parsanatech.crazycoder

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = arrayOf(FriendCodeforces::class,FriendLeetcode::class,FriendAtcoder::class,FriendCodechef::class,MyPlatforms::class), version = 2, exportSchema = false)
abstract class MyDatabase: RoomDatabase() {



    abstract fun getDao():Dao

    companion object{

        val MIGRATION_1_2 = object : Migration(1, 2){
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE IF NOT EXISTS FriendCodechef (handle TEXT NOT NULL, itsMe INTEGER NOT NULL, PRIMARY KEY(handle))")
                database.execSQL("CREATE UNIQUE INDEX index_FriendCodechef_handle ON FriendCodechef (handle)");

            }
        }
        private var instance:MyDatabase?=null

        fun getDatabase(context:Context):MyDatabase
        {
            return instance?: synchronized(this){
                val generateinstance= Room.databaseBuilder(
                    context.applicationContext,
                    MyDatabase::class.java,
                    "databasesaver"
                ).addMigrations(MIGRATION_1_2).build()
                instance=generateinstance
                generateinstance
            }

        }



    }




}