package com.payforward.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.TypeConverter

@Database(
    entities = [MessageLog::class, Keyword::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun messageLogDao(): MessageLogDao
    abstract fun keywordDao(): KeywordDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "payforward_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

class Converters {
    @TypeConverter
    fun fromForwardStatus(value: ForwardStatus): String = value.name

    @TypeConverter
    fun toForwardStatus(value: String): ForwardStatus = ForwardStatus.valueOf(value)

    @TypeConverter
    fun fromMessageSource(value: MessageSource): String = value.name

    @TypeConverter
    fun toMessageSource(value: String): MessageSource = MessageSource.valueOf(value)
}
