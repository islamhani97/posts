package com.islam97.android.apps.posts.presentation.main

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import com.islam97.android.apps.posts.R
import com.islam97.android.apps.posts.core.utils.toStringRoute

@Composable
fun RowScope.NavigationItem(
    icon: ImageVector,
    label: String,
    navController: NavHostController,
    currentRoute: String?,
    route: Any
) {
    NavigationBarItem(
        icon = { Icon(icon, contentDescription = label) },
        label = { Text(label) },
        selected = currentRoute == route.toStringRoute(),
        onClick = {
            if (currentRoute != route.toStringRoute()) {
                navController.navigate(route) {
                    // Avoid multiple copies of the same destination
                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun RowScope.PreviewNavigationItem() {
    NavigationItem(
        icon = Icons.AutoMirrored.Filled.List,
        label = stringResource(R.string.posts),
        navController = NavHostController(LocalContext.current),
        currentRoute = null,
        route = Any()
    )
}