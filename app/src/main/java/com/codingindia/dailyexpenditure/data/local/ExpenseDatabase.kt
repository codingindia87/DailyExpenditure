package com.codingindia.dailyexpenditure.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.codingindia.dailyexpenditure.data.local.dao.ExpenseDao
import com.codingindia.dailyexpenditure.data.local.entity.Expense

@Database(entities = [Expense::class], version = 1, exportSchema = false)
abstract class ExpenseDatabase : RoomDatabase() {

    abstract fun expenseDao(): ExpenseDao
}