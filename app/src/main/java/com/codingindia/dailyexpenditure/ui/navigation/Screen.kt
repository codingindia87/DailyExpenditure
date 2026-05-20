package com.codingindia.dailyexpenditure.ui.navigation

import kotlinx.serialization.Serializable

sealed interface Screen {
    @Serializable
    object Intro : Screen
    @Serializable
    object NameInput : Screen
    @Serializable
    object Home : Screen
    @Serializable
    object AddExpense : Screen
    @Serializable
    object Analysis : Screen
    @Serializable
    object Download: Screen

}