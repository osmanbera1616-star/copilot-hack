package com.example.muhasebetakip

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.muhasebetakip.data.AppDatabase
import com.example.muhasebetakip.data.AccountingRepository
import com.example.muhasebetakip.ui.AccountingApp
import com.example.muhasebetakip.ui.AccountingViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val viewModel = ViewModelProvider(
            this,
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                    val database = AppDatabase.getInstance(applicationContext)
                    return AccountingViewModel(
                        application as Application,
                        AccountingRepository(database.accountDao(), database.transactionDao())
                    ) as T
                }
            }
        )[AccountingViewModel::class.java]

        setContent {
            MaterialTheme {
                Surface { AccountingApp(viewModel) }
            }
        }
    }
}
