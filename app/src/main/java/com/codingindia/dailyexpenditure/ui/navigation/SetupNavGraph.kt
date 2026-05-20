package com.codingindia.dailyexpenditure.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.codingindia.dailyexpenditure.data.local.PreferenceManager
import com.codingindia.dailyexpenditure.ui.screens.add.AddExpenseScreen
import com.codingindia.dailyexpenditure.ui.screens.add.AddExpenseViewModel
import com.codingindia.dailyexpenditure.ui.screens.analysis.AnalysisScreen
import com.codingindia.dailyexpenditure.ui.screens.download.DownloadScreen
import com.codingindia.dailyexpenditure.ui.screens.download.DownloadViewModel
import com.codingindia.dailyexpenditure.ui.screens.home.HomeScreen
import com.codingindia.dailyexpenditure.ui.screens.home.HomeViewModel
import com.codingindia.dailyexpenditure.ui.screens.intro.IntroScreen
import com.codingindia.dailyexpenditure.ui.screens.profile.NameInputScreen

@Composable
fun SetupNavGraph(
    navController: NavHostController,
    homeViewModel: HomeViewModel,
    addExpenseViewModel: AddExpenseViewModel,
    downloadViewModel: DownloadViewModel
) {

    val context = LocalContext.current
    val preferenceManager = PreferenceManager(context)

    val startDestination = when {
        preferenceManager.isFirstTimeLaunch() -> Screen.Intro
        preferenceManager.getUserName().isEmpty() -> Screen.NameInput
        else -> Screen.Home
    }

    NavHost(
        navController = navController, startDestination = startDestination
    ) {
        // 1. Intro Screen
        composable<Screen.Intro> {
            IntroScreen(
                onFinished = {
                    preferenceManager.setFirstTimeLaunchFalse()
                    navController.navigate(Screen.NameInput) {
                        popUpTo(Screen.Intro) { inclusive = true }
                    }
                })
        }

        //2. Name Screen
        composable<Screen.NameInput> {
            NameInputScreen(
                onNameSaved = {
                    navController.navigate(Screen.Home) {
                        popUpTo(Screen.NameInput) { inclusive = true }
                    }
                })
        }

        //3. Home Screen
        composable<Screen.Home> {
            HomeScreen(
                onNavigateToAddExpense = { navController.navigate(Screen.AddExpense) },
                onNavigateToAnalysis = {
                    navController.navigate(Screen.Analysis)
                },
                onNavigateToDownload = { navController.navigate(Screen.Download) },
                viewModel = homeViewModel
            )
        }

        //4. Add Expense Screen
        composable<Screen.AddExpense> {
            AddExpenseScreen(
                onBack = { navController.popBackStack() }, viewModel = addExpenseViewModel
            )
        }

        //5. Analysis Screen
        composable<Screen.Analysis> {
            AnalysisScreen(
                viewModel = homeViewModel, onNavigateBack = { navController.popBackStack() })
        }

        //6. Download Screen
        composable<Screen.Download> {
            DownloadScreen(
                viewModel = downloadViewModel, onBack = { navController.popBackStack() })
        }
    }

}