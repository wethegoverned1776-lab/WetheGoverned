package net.wetheGoverned.ui.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object VerifyPerson : Screen("verify_person")
    object MyProfile : Screen("my_profile")
    object UserProfile : Screen("user_profile/{uid}") {
        fun createRoute(uid: String) = "user_profile/$uid"
    }
    object NetworkDirectory : Screen("network_directory")
}
