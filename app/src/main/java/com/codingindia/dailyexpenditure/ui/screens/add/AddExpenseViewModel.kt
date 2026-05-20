package com.codingindia.dailyexpenditure.ui.screens.add

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.codingindia.dailyexpenditure.data.local.entity.Expense
import com.codingindia.dailyexpenditure.data.repository.ExpenseRepository
import kotlinx.coroutines.launch

class AddExpenseViewModel(private val repository: ExpenseRepository) : ViewModel() {

    var amount by mutableStateOf("")
        private set
    var description by mutableStateOf("")
        private set
    var category by mutableStateOf("Food")
        private set

    fun onAmountChange(newAmount: String) {
        amount = newAmount
    }

    fun onDescriptionChange(newDesc: String) {
        description = newDesc
    }

    fun onCategoryChange(newCategory: String) {
        category = newCategory
    }

    fun saveExpense(onSuccess: () -> Unit) {
        val parsedAmount = amount.toDoubleOrNull() ?: 0.0
        if (parsedAmount <= 0.0 || description.isBlank()) return

        viewModelScope.launch {
            val newExpense = Expense(
                amount = parsedAmount,
                description = description,
                category = category,
                timestamp = System.currentTimeMillis()
            )

            repository.insert(newExpense)
            onSuccess()
        }
    }
}

class AddExpenseViewModelFactory(private val repository: ExpenseRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddExpenseViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddExpenseViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}