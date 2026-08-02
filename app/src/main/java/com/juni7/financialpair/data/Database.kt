package com.juni7.financialpair.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.juni7.financialpair.R
import com.juni7.financialpair.data.dao.CategoryDao
import com.juni7.financialpair.data.dao.MovementDao
import com.juni7.financialpair.data.dao.TopicDao
import com.juni7.financialpair.data.entity.Category
import com.juni7.financialpair.data.entity.Movement
import com.juni7.financialpair.data.entity.Topic

@Database(
    entities = [Movement::class, Category::class, Topic::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun movementDao(): MovementDao
    abstract fun topicDao(): TopicDao
    abstract fun categoryDao(): CategoryDao

    companion object{
        @Volatile
        private var Instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    AppDatabase::class.java,
                    "financial_pair_db"
                ).addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)

                        // Insert fixed categories in English for standardization
                        db.execSQL("INSERT INTO Category (id, name) VALUES (0, 'general')")
                        db.execSQL("INSERT INTO Category (id, name) VALUES (1, 'transport')")
                        db.execSQL("INSERT INTO Category (id, name) VALUES (2, 'food')")
                        db.execSQL("INSERT INTO Category (id, name) VALUES (3, 'health')")
                        db.execSQL("INSERT INTO Category (id, name) VALUES (4, 'entertainment')")
                        db.execSQL("INSERT INTO Category (id, name) VALUES (5, 'clothes')")
                        db.execSQL("INSERT INTO Category (id, name) VALUES (6, 'education')")
                        db.execSQL("INSERT INTO Category (id, name) VALUES (7, 'shopping')")
                        db.execSQL("INSERT INTO Category (id, name) VALUES (8, 'housing')")
                        db.execSQL("INSERT INTO Category (id, name) VALUES (9, 'family')")
                        db.execSQL("INSERT INTO Category (id, name) VALUES (10, 'travel')")

                        // Default Topic
                        val topicGeneral = context.getString(R.string.topic_general)
                        db.execSQL(
                            "INSERT INTO Topic (id, name, categoryId) VALUES (0, ?, 0)",
                            arrayOf(topicGeneral)
                        )
                    }
                })
                    .build().also {
                    Instance = it
                }
            }
        }
    }
}
