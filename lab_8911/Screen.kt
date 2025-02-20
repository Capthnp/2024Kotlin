package com.example.lab_8911

sealed class Screen(val route: String,val name:String) {
    data object Home : Screen(route= "home_screen", name = "Home")
    data object Insert : Screen(route= "insert_screen", name = "Insert")
    data object Edit : Screen(route ="edit_screen", name = "Screen")
}