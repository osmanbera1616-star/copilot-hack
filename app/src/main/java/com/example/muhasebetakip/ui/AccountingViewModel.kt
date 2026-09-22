package com.example.muhasebetakip.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.muhasebetakip.data.AccountEntity
import com.example.muhasebetakip.data.AccountingRepository
import com.example.muhasebetakip.data.DashboardTotals
import com.example.muhasebetakip.data.TransactionEntity
import com.example.muhasebetakip.data.TransactionType
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class AccountingUiState(
    val accounts: List<AccountEntity> = emptyList(),
    val transactions: List<TransactionEntity> = emptyList(),
    val totals: DashboardTotals = DashboardTotals()
)

class AccountingViewModel(
    application: Application,
    private val repository: AccountingRepository
) : AndroidViewModel(application) {
    val state: StateFlow<AccountingUiState> = combine(
        repository.accounts,
        repository.transactions,
        repository.totals
    ) { accounts, transactions, totals ->
        AccountingUiState(accounts, transactions, totals)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AccountingUiState())

    fun addAccount(name: String, phone: String, note: String, onResult: (Boolean) -> Unit) {
        if (name.isBlank()) {
            onResult(false)
            return
        }
        viewModelScope.launch {
            repository.addAccount(name, phone, note)
            onResult(true)
        }
    }

    fun addTransaction(
        title: String,
        amount: String,
        type: TransactionType,
        accountId: Long?,
        note: String,
        onResult: (Boolean) -> Unit
    ) {
        val normalized = amount.replace(",", ".").toDoubleOrNull()
        if (title.isBlank() || normalized == null || normalized <= 0) {
            onResult(false)
            return
        }
        viewModelScope.launch {
            repository.addTransaction(title, (normalized * 100).toLong(), type, accountId, note)
            onResult(true)
        }
    }
}
