package com.payforward.app.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageLogDao {

    @Query("SELECT * FROM message_logs ORDER BY timestamp DESC")
    fun getAll(): Flow<List<MessageLog>>

    @Query("SELECT * FROM message_logs WHERE timestamp >= :startTime ORDER BY timestamp DESC")
    fun getFromDate(startTime: Long): Flow<List<MessageLog>>

    @Query("""
        SELECT * FROM message_logs 
        WHERE body LIKE '%' || :query || '%' 
        OR sender LIKE '%' || :query || '%' 
        OR matchedKeyword LIKE '%' || :query || '%'
        ORDER BY timestamp DESC
    """)
    fun search(query: String): Flow<List<MessageLog>>

    @Query("""
        SELECT * FROM message_logs 
        WHERE (:statusFilter IS NULL OR status = :statusFilter)
        AND (:query IS NULL OR body LIKE '%' || :query || '%' 
             OR sender LIKE '%' || :query || '%')
        ORDER BY timestamp DESC
    """)
    fun searchWithFilter(query: String?, statusFilter: ForwardStatus?): Flow<List<MessageLog>>

    @Query("SELECT COUNT(*) FROM message_logs WHERE timestamp >= :startOfDay")
    fun getScannedTodayCount(startOfDay: Long): Flow<Int>

    @Query("SELECT COUNT(*) FROM message_logs WHERE timestamp >= :startOfDay AND status = 'SUCCESS'")
    fun getForwardedTodayCount(startOfDay: Long): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(log: MessageLog): Long

    @Update
    suspend fun update(log: MessageLog)

    @Delete
    suspend fun delete(log: MessageLog)

    @Query("DELETE FROM message_logs")
    suspend fun deleteAll()

    @Query("SELECT * FROM message_logs ORDER BY timestamp DESC")
    fun getAllLogsSync(): List<MessageLog>

    @Query("SELECT COUNT(*) FROM message_logs WHERE timestamp >= :since")
    suspend fun getCountSince(since: Long): Int

    @Query("SELECT COUNT(*) FROM message_logs WHERE timestamp >= :since AND status = 'SUCCESS'")
    suspend fun getSuccessCountSince(since: Long): Int
}
