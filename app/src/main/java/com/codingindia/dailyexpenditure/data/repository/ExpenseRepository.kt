package com.codingindia.dailyexpenditure.data.repository

import com.codingindia.dailyexpenditure.data.local.dao.ExpenseDao
import com.codingindia.dailyexpenditure.data.local.entity.Expense
import kotlinx.coroutines.flow.Flow

class ExpenseRepository(private val expenseDao: ExpenseDao) {

    val allExpenses: Flow<List<Expense>> = expenseDao.getAllExpenses()

    suspend fun insert(expense: Expense) {
        expenseDao.insertExpense(expense)
    }

    suspend fun delete(expense: Expense) {
        expenseDao.deleteExpense(expense)
    }

    fun getExpensesByDateRange(startDate: Long, endDate: Long): Flow<List<Expense>> {
        return expenseDao.getExpensesByDateRange(startDate, endDate)
    }
}