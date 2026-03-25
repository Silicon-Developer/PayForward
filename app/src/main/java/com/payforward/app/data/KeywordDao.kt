package com.payforward.app.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface KeywordDao {

    @Query("SELECT * FROM keywords ORDER BY isDefault DESC, word ASC")
    fun getAll(): Flow<List<Keyword>>

    @Query("SELECT * FROM keywords WHERE isEnabled = 1")
    fun getEnabled(): Flow<List<Keyword>>

    @Query("SELECT * FROM keywords WHERE isEnabled = 1")
    suspend fun getEnabledList(): List<Keyword>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(keyword: Keyword): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(keywords: List<Keyword>)

    @Update
    suspend fun update(keyword: Keyword)

    @Delete
    suspend fun delete(keyword: Keyword)

    @Query("SELECT COUNT(*) FROM keywords")
    suspend fun getCount(): Int
}
