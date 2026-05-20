package com.codingindia.dailyexpenditure

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.codingindia.dailyexpenditure.data.local.ExpenseDatabase
import com.codingindia.dailyexpenditure.data.repository.ExpenseRepository
import com.codingindia.dailyexpenditure.ui.navigation.SetupNavGraph
import com.codingindia.dailyexpenditure.ui.screens.add.AddExpenseViewModel
import com.codingindia.dailyexpenditure.ui.screens.add.AddExpenseViewModelFactory
import com.codingindia.dailyexpenditure.ui.screens.download.DownloadViewModel
import com.codingindia.dailyexpenditure.ui.screens.download.DownloadViewModelFactory
import com.codingindia.dailyexpenditure.ui.screens.home.HomeViewModel
import com.codingindia.dailyexpenditure.ui.screens.home.HomeViewModelFactory
import com.codingindia.dailyexpenditure.ui.theme.DailyExpenditureTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        val database = Room.databaseBuilder(
            applicationContext, ExpenseDatabase::class.java, "expense_database"
        ).build()


        val repository = ExpenseRepository(database.expenseDao())

        val addExpenseViewModel = ViewModelProvider(
            this, AddExpenseViewModelFactory(repository)
        )[AddExpenseViewModel::class.java]

        val homeViewModel = ViewModelProvider(
            this, HomeViewModelFactory(repository)
        )[HomeViewModel::class.java]

        val downloadViewModel = ViewModelProvider(
            this, DownloadViewModelFactory(repository)
        )[DownloadViewModel::class.java]

        setContent {
            DailyExpenditureTheme {
                val navController = rememberNavController()

                SetupNavGraph(
                    navController = navController,
                    homeViewModel = homeViewModel,
                    addExpenseViewModel = addExpenseViewModel,
                    downloadViewModel = downloadViewModel
                )
            }
        }
    }
}