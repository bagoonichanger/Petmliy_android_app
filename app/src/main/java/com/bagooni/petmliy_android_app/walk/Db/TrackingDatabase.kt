package com.bagooni.petmliy_android_app.walk.Db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [Tracking::class],
    version = 1
)

@TypeConverters(Converters::class)
abstract class TrackingDatabase : RoomDatabase(){
    abstract fun getTrackingDao():TrackingDAO

    companion object{
        @Volatile
        private var instance  :TrackingDatabase? = null

        fun getDatabase(context: Context) : TrackingDatabase? {
            if(instance == null){
                synchronized(TrackingDatabase::class){
                    instance = Room.databaseBuilder(context.applicationContext, TrackingDatabase::class.java, "tracking_database").build()
                }
            }
            return instance
        }
    }
}