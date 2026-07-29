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
    version = 2,
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
                        val general = context.getString(R.string.category_general)
                        val transport = context.getString(R.string.category_transport)
                        val food = context.getString(R.string.category_food)
                        val health = context.getString(R.string.category_health)
                        val entertainment = context.getString(R.string.category_entertainment)
                        val clothes = context.getString(R.string.category_clothes)
                        val education = context.getString(R.string.category_education)
                        val shopping = context.getString(R.string.category_shopping)
                        val housing = context.getString(R.string.category_housing)
                        val family = context.getString(R.string.category_family)
                        val travel = context.getString(R.string.category_travel)
                        val topicGeneral = context.getString(R.string.topic_general)

                        db.execSQL(
                            "INSERT INTO Category (id, name, emoji) VALUES (0, ?, '💰')",
                            arrayOf(general)
                        )
                        db.execSQL(
                            "INSERT INTO Category (id, name, emoji) VALUES (1, ?, '🚌')",
                            arrayOf(transport)
                        )
                        db.execSQL(
                            "INSERT INTO Category (id, name, emoji) VALUES (2, ?, '🌮')",
                            arrayOf(food)
                        )
                        db.execSQL(
                            "INSERT INTO Category (id, name, emoji) VALUES (3, ?, '🏥')",
                            arrayOf(health)
                        )
                        db.execSQL(
                            "INSERT INTO Category (id, name, emoji) VALUES (4, ?, '🎮')",
                            arrayOf(entertainment)
                        )
                        db.execSQL(
                            "INSERT INTO Category (id, name, emoji) VALUES (5, ?, '👗')",
                            arrayOf(clothes)
                        )
                        db.execSQL(
                            "INSERT INTO Category (id, name, emoji) VALUES (6, ?, '📚')",
                            arrayOf(education)
                        )
                        db.execSQL(
                            "INSERT INTO Category (id, name, emoji) VALUES (7, ?, '🛍️')",
                            arrayOf(shopping)
                        )
                        db.execSQL(
                            "INSERT INTO Category (id, name, emoji) VALUES (8, ?, '🏠')",
                            arrayOf(housing)
                        )
                        db.execSQL(
                            "INSERT INTO Category (id, name, emoji) VALUES (9, ?, '👨‍👩‍👧‍👦')",
                            arrayOf(family)
                        )
                        db.execSQL(
                            "INSERT INTO Category (id, name, emoji) VALUES (10, ?, '✈️')",
                            arrayOf(travel)
                        )
                        db.execSQL(
                            "INSERT INTO Topic (id, name, categoryId) VALUES (0, ?, 0)",
                            arrayOf(topicGeneral)
                        )
                    }
                })
                    .fallbackToDestructiveMigration(true)
                    .build().also {
                    Instance = it
                }
            }
        }
    }
}
