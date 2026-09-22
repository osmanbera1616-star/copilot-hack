package com.example.muhasebetakip.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "accounts")
data class AccountEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val phone: String = "",
    val note: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

enum class TransactionType { INCOME, EXPENSE }

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val accountId: Long? = null,
    val title: String,
    val amountKurus: Long,
    val type: String,
    val date: Long = System.currentTimeMillis(),
    val note: String = ""
)

data class DashboardTotals(
    val incomeKurus: Long = 0,
    val expenseKurus: Long = 0
) {
    val balanceKurus: Long get() = incomeKurus - expenseKurus
}
