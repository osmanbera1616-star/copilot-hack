package com.example.muhasebetakip.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.muhasebetakip.data.AccountEntity
import com.example.muhasebetakip.data.TransactionEntity
import com.example.muhasebetakip.data.TransactionType
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private enum class Screen { HOME, TRANSACTIONS, ACCOUNTS }

@Composable
fun AccountingApp(viewModel: AccountingViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var screen by remember { mutableStateOf(Screen.HOME) }
    var showTransactionForm by remember { mutableStateOf(false) }
    var showAccountForm by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Muhasebe Takip", fontWeight = FontWeight.Bold) }) },
        bottomBar = {
            NavigationBar(modifier = Modifier.navigationBarsPadding()) {
                NavigationBarItem(screen == Screen.HOME, { screen = Screen.HOME }, icon = { Icon(Icons.Default.Home, null) }, label = { Text("Özet") })
                NavigationBarItem(screen == Screen.TRANSACTIONS, { screen = Screen.TRANSACTIONS }, icon = { Icon(Icons.Default.ReceiptLong, null) }, label = { Text("İşlemler") })
                NavigationBarItem(screen == Screen.ACCOUNTS, { screen = Screen.ACCOUNTS }, icon = { Icon(Icons.Default.People, null) }, label = { Text("Cariler") })
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                if (screen == Screen.ACCOUNTS) showAccountForm = true else showTransactionForm = true
            }) { Icon(Icons.Default.Add, "Yeni kayıt") }
        }
    ) { padding ->
        when (screen) {
            Screen.HOME -> DashboardScreen(state, { screen = Screen.TRANSACTIONS }, Modifier.padding(padding))
            Screen.TRANSACTIONS -> TransactionScreen(state.transactions, Modifier.padding(padding))
            Screen.ACCOUNTS -> AccountScreen(state.accounts, Modifier.padding(padding))
        }
    }

    if (showTransactionForm) {
        TransactionForm(state.accounts, viewModel) { showTransactionForm = false }
    }
    if (showAccountForm) {
        AccountForm(viewModel) { showAccountForm = false }
    }
}

@Composable
private fun DashboardScreen(
    state: AccountingUiState,
    onTransactions: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier.fillMaxSize().padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Spacer(Modifier.height(8.dp))
            Text("Genel Bakış", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text("İşletmenizin finansal durumunu takip edin.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        item {
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer), shape = RoundedCornerShape(20.dp)) {
                Column(Modifier.fillMaxWidth().padding(20.dp)) {
                    Text("Güncel bakiye", color = MaterialTheme.colorScheme.onPrimaryContainer)
                    Text(formatMoney(state.totals.balanceKurus), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                }
            }
        }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SummaryCard("Gelir", state.totals.incomeKurus, Color(0xFF1B7F5A), Icons.Default.ArrowUpward, Modifier.weight(1f))
                SummaryCard("Gider", state.totals.expenseKurus, Color(0xFFC2413A), Icons.Default.ArrowDownward, Modifier.weight(1f))
            }
        }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Son işlemler", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                TextButton(onClick = onTransactions) { Text("Tümünü gör") }
            }
        }
        if (state.transactions.isEmpty()) {
            item { EmptyState("Henüz işlem eklenmedi", "Yeni gelir veya gider eklemek için + düğmesine dokunun.") }
        } else {
            items(state.transactions.take(5), key = { it.id }) { TransactionRow(it) }
        }
        item { Spacer(Modifier.height(80.dp)) }
    }
}

@Composable
private fun SummaryCard(title: String, amount: Long, color: Color, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier) {
    Card(modifier, colors = CardDefaults.cardColors(containerColor = color.copy(alpha = .12f))) {
        Column(Modifier.padding(14.dp)) {
            Icon(icon, null, tint = color)
            Spacer(Modifier.height(8.dp))
            Text(title, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(formatMoney(amount), fontWeight = FontWeight.Bold, color = color)
        }
    }
}

@Composable
private fun TransactionScreen(transactions: List<TransactionEntity>, modifier: Modifier) {
    LazyColumn(modifier.fillMaxSize().padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item { Spacer(Modifier.height(8.dp)); Text("İşlemler", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold) }
        if (transactions.isEmpty()) item { EmptyState("Kayıt bulunamadı", "İlk işleminizi eklemek için + düğmesine dokunun.") }
        items(transactions, key = { it.id }) { TransactionRow(it) }
        item { Spacer(Modifier.height(80.dp)) }
    }
}

@Composable
private fun AccountScreen(accounts: List<AccountEntity>, modifier: Modifier) {
    LazyColumn(modifier.fillMaxSize().padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item { Spacer(Modifier.height(8.dp)); Text("Cari Hesaplar", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold) }
        item { Text("Müşteri ve tedarikçilerinizi tek yerde yönetin.", color = MaterialTheme.colorScheme.onSurfaceVariant) }
        if (accounts.isEmpty()) item { EmptyState("Henüz cari eklenmedi", "Yeni cari eklemek için + düğmesine dokunun.") }
        items(accounts, key = { it.id }) { account ->
            Card(Modifier.fillMaxWidth()) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.People, null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(account.name, fontWeight = FontWeight.SemiBold)
                        if (account.phone.isNotBlank()) Text(account.phone, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
        item { Spacer(Modifier.height(80.dp)) }
    }
}

@Composable
private fun TransactionRow(transaction: TransactionEntity) {
    val income = transaction.type == TransactionType.INCOME.name
    Card(Modifier.fillMaxWidth()) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(if (income) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward, null, tint = if (income) Color(0xFF1B7F5A) else Color(0xFFC2413A))
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(transaction.title, fontWeight = FontWeight.SemiBold)
                Text(SimpleDateFormat("dd MMM yyyy", Locale("tr", "TR")).format(Date(transaction.date)), color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text((if (income) "+" else "-") + formatMoney(transaction.amountKurus), fontWeight = FontWeight.Bold, color = if (income) Color(0xFF1B7F5A) else Color(0xFFC2413A))
        }
    }
}

@Composable
private fun EmptyState(title: String, message: String) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(title, fontWeight = FontWeight.SemiBold)
            Text(message, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TransactionForm(accounts: List<AccountEntity>, viewModel: AccountingViewModel, onDismiss: () -> Unit) {
    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var type by remember { mutableStateOf(TransactionType.EXPENSE) }
    var error by remember { mutableStateOf(false) }
    AlertDialog(onDismissRequest = onDismiss, title = { Text("Yeni işlem") }, text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { type = TransactionType.EXPENSE }) { Text(if (type == TransactionType.EXPENSE) "✓ Gider" else "Gider") }
                OutlinedButton(onClick = { type = TransactionType.INCOME }) { Text(if (type == TransactionType.INCOME) "✓ Gelir" else "Gelir") }
            }
            OutlinedTextField(title, { title = it }, label = { Text("Açıklama") }, singleLine = true)
            OutlinedTextField(amount, { amount = it }, label = { Text("Tutar (₺)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), singleLine = true)
            OutlinedTextField(note, { note = it }, label = { Text("Not (isteğe bağlı)") }, singleLine = true)
            if (error) Text("Açıklama ve geçerli bir tutar girin.", color = MaterialTheme.colorScheme.error)
        }
    }, confirmButton = {
        Button(onClick = { viewModel.addTransaction(title, amount, type, accounts.firstOrNull()?.id, note) { ok -> if (ok) onDismiss() else error = true } }) { Text("Kaydet") }
    }, dismissButton = { TextButton(onClick = onDismiss) { Text("Vazgeç") } })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AccountForm(viewModel: AccountingViewModel, onDismiss: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }
    AlertDialog(onDismissRequest = onDismiss, title = { Text("Yeni cari") }, text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedTextField(name, { name = it }, label = { Text("Ad / firma") }, singleLine = true)
            OutlinedTextField(phone, { phone = it }, label = { Text("Telefon") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone), singleLine = true)
            OutlinedTextField(note, { note = it }, label = { Text("Not") }, singleLine = true)
            if (error) Text("Cari adı zorunludur.", color = MaterialTheme.colorScheme.error)
        }
    }, confirmButton = {
        Button(onClick = { viewModel.addAccount(name, phone, note) { ok -> if (ok) onDismiss() else error = true } }) { Text("Kaydet") }
    }, dismissButton = { TextButton(onClick = onDismiss) { Text("Vazgeç") } })
}

private fun formatMoney(kurus: Long): String =
    NumberFormat.getCurrencyInstance(Locale("tr", "TR")).format(kurus / 100.0)
