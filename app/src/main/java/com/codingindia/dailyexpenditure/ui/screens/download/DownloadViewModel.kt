package com.codingindia.dailyexpenditure.ui.screens.download

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.codingindia.dailyexpenditure.core.util.generateExpensePdf
import com.codingindia.dailyexpenditure.data.local.dao.ExpenseDao
import com.codingindia.dailyexpenditure.data.repository.ExpenseRepository
import com.codingindia.dailyexpenditure.ui.screens.home.HomeViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DownloadViewModel(private val repository: ExpenseRepository) : ViewModel() {

    var isRangeMode by mutableStateOf(false)
        private set
    var singleDate by mutableLongStateOf(System.currentTimeMillis())
        private set
    var startDate by mutableLongStateOf(System.currentTimeMillis())
        private set
    var endDate by mutableLongStateOf(System.currentTimeMillis())
        private set

    private val timeRangeFlow = MutableStateFlow(
        Pair(getStartOfDay(System.currentTimeMillis()), getEndOfDay(System.currentTimeMillis()))
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val filteredExpenses = timeRangeFlow.flatMapLatest { queryTimes ->
        repository.getExpensesByDateRange(queryTimes.first, queryTimes.second)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setMode(rangeMode: Boolean) {
        isRangeMode = rangeMode
        updateQueryTimestamps()
    }

    fun updateSingleDate(time: Long) { singleDate = time; updateQueryTimestamps() }
    fun updateStartDate(time: Long) { startDate = time; updateQueryTimestamps() }
    fun updateEndDate(time: Long) { endDate = time; updateQueryTimestamps() }

    private fun updateQueryTimestamps() {
        if (!isRangeMode) {
            timeRangeFlow.value = Pair(getStartOfDay(singleDate), getEndOfDay(singleDate))
        } else {
            timeRangeFlow.value = Pair(getStartOfDay(startDate), getEndOfDay(endDate))
        }
    }

    fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }


    private fun getStartOfDay(time: Long): Long {
        val cal = java.util.Calendar.getInstance().apply {
            timeInMillis = time
            set(java.util.Calendar.HOUR_OF_DAY, 0)
            set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
        }
        return cal.timeInMillis
    }

    private fun getEndOfDay(time: Long): Long {
        val cal = java.util.Calendar.getInstance().apply {
            timeInMillis = time
            set(java.util.Calendar.HOUR_OF_DAY, 23)
            set(java.util.Calendar.MINUTE, 59)
            set(java.util.Calendar.SECOND, 59)
            set(java.util.Calendar.MILLISECOND, 999)
        }
        return cal.timeInMillis
    }

    fun generatePdfReport(context: Context, onComplete: (String) -> Unit) {
        val currentList = filteredExpenses.value

        if (currentList.isNotEmpty()) {

            val path = generateExpensePdf(context, currentList, "Expense_Report")

            if (path.isNotEmpty()) {
                onComplete("PDF डाउनलोड हो गई है!\nपाथ: $path")
            } else {
                onComplete("PDF बनाने में कोई खराबी आई!")
            }
        } else {
            onComplete("चुनी गई तारीखों में कोई खर्च नहीं मिला!")
        }
    }
}

class DownloadViewModelFactory(private val repository: ExpenseRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DownloadViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DownloadViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}