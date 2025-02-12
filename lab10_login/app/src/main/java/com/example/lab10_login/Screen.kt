package com.example.lab10_login

import okhttp3.Route

sealed class Screen (val route: String, val  name:String){
    data object Login:Screen(route = "Login_screen",name="Login")
    data object Register:Screen(route = "Register_screen",name="Register")
    data object Profile:Screen(route = "Profile_screen",name="Profile")
}