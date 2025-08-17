package com.example.winemap.android.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.winemap.android.ui.rememberAuthViewModel
import com.example.winemap.android.ui.rememberPostViewModel
import com.example.winemap.android.ui.rememberSearchViewModel
import com.example.winemap.android.ui.screens.SplashScreen
import com.example.winemap.android.ui.screens.auth.SignInScreen
import com.example.winemap.android.ui.screens.auth.RegisterScreen
import com.example.winemap.android.ui.screens.map.MapScreen
import com.example.winemap.android.ui.screens.profile.ProfileScreen
import com.example.winemap.android.ui.screens.profile.EditProfileScreen
import com.example.winemap.android.ui.screens.profile.FavoritesScreen
import com.example.winemap.android.ui.screens.search.SearchScreen
import com.example.winemap.android.ui.screens.posts.CreatePostScreen
import com.example.winemap.android.ui.screens.posts.EditPostScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        // Auth Flow
        composable("splash") {
            SplashScreen(
                onNavigateToAuth = {
                    navController.navigate("signin") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }

        composable("signin") {
            SignInScreen(
                onNavigateToRegister = {
                    navController.navigate("register")
                },
                onNavigateToMain = {
                    navController.navigate("main") {
                        popUpTo("signin") { inclusive = true }
                    }
                }
            )
        }

        composable("register") {
            RegisterScreen(
                onNavigateToSignIn = {
                    navController.popBackStack()
                },
                onNavigateToMain = {
                    navController.navigate("main") {
                        popUpTo("register") { inclusive = true }
                    }
                }
            )
        }

        // Main App with Bottom Navigation
        composable("main") {
            MainScreenWithNavigation()
        }
    }
}

@Composable
fun MainScreenWithNavigation() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { paddingValues ->
        MainNavHost(
            navController = navController,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItem.Feed,
        BottomNavItem.Favorites,
        BottomNavItem.Add,
        BottomNavItem.Search,
        BottomNavItem.Profile
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = Color(0xFFE8DCC6),
        modifier = Modifier.height(80.dp)
    ) {
        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    if (item == BottomNavItem.Add) {
                        FloatingActionButton(
                            onClick = { navController.navigate(item.route) },
                            containerColor = Color(0xFF8B6F47),
                            modifier = Modifier.size(56.dp)
                        ) {
                            Text(
                                text = getIconForNavItem(item),
                                fontSize = 24.sp,
                                color = Color.White
                            )
                        }
                    } else {
                        Text(
                            text = getIconForNavItem(item),
                            fontSize = 24.sp,
                            color = if (currentRoute == item.route) Color(0xFF6B5B73) else Color.Gray
                        )
                    }
                },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF6B5B73),
                    unselectedIconColor = Color.Gray,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}

@Composable
fun MainNavHost(navController: NavHostController, modifier: Modifier = Modifier) {
    val authViewModel = rememberAuthViewModel()
    val postViewModel = rememberPostViewModel()
    val searchViewModel = rememberSearchViewModel()

    // Listen for authentication changes
    val uiState by authViewModel.uiState.collectAsState()

    // Navigate to signin when user is signed out
    LaunchedEffect(uiState.isAuthenticated) {
        if (!uiState.isAuthenticated) {
            // Find the root nav controller and navigate to signin
            val context = navController.context
            if (context is androidx.activity.ComponentActivity) {
                context.finish()
                val intent = android.content.Intent(context, context::class.java)
                context.startActivity(intent)
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = BottomNavItem.Feed.route,
        modifier = modifier
    ) {
        // Feed/Map Screen
        composable(BottomNavItem.Feed.route) {
            MapScreen(
                postViewModel = postViewModel,
                onWinerySelected = { winery ->
                    // Handle winery selection
                },
                onRelatedPostsClick = { wineryId ->
                    // Navigate to winery posts
                }
            )
        }

        // Favorites Screen
        composable(BottomNavItem.Favorites.route) {
            FavoritesScreen(
                postViewModel = postViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onWineryClick = { wineryId ->
                    // Navigate to winery details
                }
            )
        }

        // Create Post Screen
        composable(BottomNavItem.Add.route) {
            CreatePostScreen(
                postViewModel = postViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onPostCreated = {
                    navController.popBackStack()
                }
            )
        }

        // Search Screen
        composable(BottomNavItem.Search.route) {
            SearchScreen(
                searchViewModel = searchViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Profile Screen - Simple logout
        composable(BottomNavItem.Profile.route) {
            ProfileScreen(
                onNavigateToEditProfile = {
                    navController.navigate("edit_profile")
                },
                onNavigateToEditPost = { postId ->
                    navController.navigate("edit_post/$postId")
                }
            )
        }

        // Edit Profile Screen
        composable("edit_profile") {
            EditProfileScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onSaveProfile = { username, bio ->
                    // Save profile logic
                    navController.popBackStack()
                }
            )
        }

        // Edit Post Screen
        composable("edit_post/{postId}") { backStackEntry ->
            val postId = backStackEntry.arguments?.getString("postId") ?: ""
            EditPostScreen(
                postViewModel = postViewModel,
                postId = postId,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onPostUpdated = {
                    navController.popBackStack()
                },
                onPostDeleted = {
                    navController.popBackStack()
                }
            )
        }
    }
}

sealed class BottomNavItem(val route: String, val title: String) {
    object Feed : BottomNavItem("feed", "Feed")
    object Favorites : BottomNavItem("favorites", "Favorites")
    object Add : BottomNavItem("create_post", "Add")
    object Search : BottomNavItem("search", "Search")
    object Profile : BottomNavItem("profile", "Profile")
}

fun getIconForNavItem(item: BottomNavItem): String {
    return when (item) {
        BottomNavItem.Feed -> "🏠"         // Home icon
        BottomNavItem.Favorites -> "❤️"    // Heart icon
        BottomNavItem.Add -> "➕"          // Plus icon (in FAB)
        BottomNavItem.Search -> "🔍"       // Search icon
        BottomNavItem.Profile -> "👤"      // Profile icon
    }
}
