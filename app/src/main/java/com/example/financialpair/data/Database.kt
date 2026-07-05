package com.example.financialpair.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.financialpair.R
import com.example.financialpair.data.dao.CategoryDao
import com.example.financialpair.data.dao.MovementDao
import com.example.financialpair.data.dao.TopicDao
import com.example.financialpair.data.entity.Category
import com.example.financialpair.data.entity.Movement
import com.example.financialpair.data.entity.Topic

@Database(
    entities = [Movement::class, Category::class, Topic::class],
    version = 1,
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
                ).addCallback(object : Callback(){
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        val general = context.getString(R.string.category_general)
                        val transport = context.getString(R.string.category_transport)
                        val food = context.getString(R.string.category_food)
                        val health = context.getString(R.string.category_health)
                        val entertainment = context.getString(R.string.category_entertainment)
                        val clothes = context.getString(R.string.category_clothes)
                        val topicGeneral = context.getString(R.string.topic_general)

                        db.execSQL("INSERT INTO Category (id, name) VALUES (0, ?)", arrayOf(general))
                        db.execSQL("INSERT INTO Category (id, name) VALUES (1, ?)", arrayOf(transport))
                        db.execSQL("INSERT INTO Category (id, name) VALUES (2, ?)", arrayOf(food))
                        db.execSQL("INSERT INTO Category (id, name) VALUES (3, ?)", arrayOf(health))
                        db.execSQL("INSERT INTO Category (id, name) VALUES (4, ?)", arrayOf(entertainment))
                        db.execSQL("INSERT INTO Category (id, name) VALUES (5, ?)", arrayOf(clothes))
                        db.execSQL("INSERT INTO Topic (id, name, categoryId) VALUES (0, ?, 0)", arrayOf(topicGeneral))
                    }
                })
                    .build().also {
                    Instance = it
                }
            }
        }
    }
}