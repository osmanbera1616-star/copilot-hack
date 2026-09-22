package com.example.muhasebetakip.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao {
    @Query("SELECT * FROM accounts ORDER BY name COLLATE NOCASE")
    fun observeAll(): Flow<List<AccountEntity>>

    @Insert
    suspend fun insert(account: AccountEntity): Long
}

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun observeAll(): Flow<List<TransactionEntity>>

    @Insert
    suspend fun insert(transaction: TransactionEntity): Long

    @Query("SELECT COALESCE(SUM(amountKurus), 0) FROM transactions WHERE type = 'INCOME'")
    fun observeIncome(): Flow<Long>

    @Query("SELECT COALESCE(SUM(amountKurus), 0) FROM transactions WHERE type = 'EXPENSE'")
    fun observeExpense(): Flow<Long>
}
