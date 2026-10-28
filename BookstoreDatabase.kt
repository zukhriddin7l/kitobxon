package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        BookEntity::class,
        ChapterEntity::class,
        BookmarkEntity::class,
        CartItemEntity::class,
        SavedAddressEntity::class,
        OrderEntity::class,
        OrderItemEntity::class,
        ReviewEntity::class,
        ReaderSettingsEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class BookstoreDatabase : RoomDatabase() {

    abstract fun bookstoreDao(): BookstoreDao

    companion object {
        @Volatile
        private var INSTANCE: BookstoreDatabase? = null

        fun getInstance(context: Context): BookstoreDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BookstoreDatabase::class.java,
                    "kitobxon_bookstore.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
