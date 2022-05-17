//package com.c22ho01.hotelranking.data.local
//
//import android.content.Context
//import androidx.room.Database
//import androidx.room.Room
//import androidx.room.RoomDatabase
//
//@Database(
//    entities = [],
//    version = 1,
//    exportSchema = false,
//)
//abstract class AppDatabase : RoomDatabase() {
//
//    companion object {
//        private const val DATABASE_NAME = "hotel_ranking_app_db"
//
//        @Volatile
//        private var INSTANCE: AppDatabase? = null
//
//        @JvmStatic
//        fun getDatabase(context: Context): AppDatabase {
//            return INSTANCE
//                ?: synchronized(this) {
//                    INSTANCE
//                        ?: Room.databaseBuilder(
//                            context.applicationContext, AppDatabase::class.java, DATABASE_NAME
//                        )
//                            .fallbackToDestructiveMigration()
//                            .build()
//                            .also { INSTANCE = it }
//                }
//        }
//    }
//}