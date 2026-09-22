package com.example.muhasebetakip.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class AccountingRepository(
    private val accountDao: AccountDao,
    private val transactionDao: TransactionDao
) {
    val accounts: Flow<List<AccountEntity>> = accountDao.observeAll()
    val transactions: Flow<List<TransactionEntity>> = transactionDao.observeAll()
    val totals: Flow<DashboardTotals> = combine(
        transactionDao.observeIncome(),
        transactionDao.observeExpense()
    ) { income, expense -> DashboardTotals(income, expense) }

    suspend fun addAccount(name: String, phone: String, note: String) =
        accountDao.insert(AccountEntity(name = name.trim(), phone = phone.trim(), note = note.trim()))

    suspend fun addTransaction(
        title: String,
        amountKurus: Long,
        type: TransactionType,
        accountId: Long?,
        note: String
    ) = transactionDao.insert(
        TransactionEntity(
            title = title.trim(),
            amountKurus = amountKurus,
            type = type.name,
            accountId = accountId,
            note = note.trim()
        )
    )
}
