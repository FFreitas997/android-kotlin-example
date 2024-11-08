package com.example.musicapp.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.musicapp.R

import com.example.musicapp.components.AccountDialog
import com.example.musicapp.components.BottomSheet
import com.example.musicapp.navigation.Navigation
import com.example.musicapp.screen.Screen.BottomScreen.Browse
import com.example.musicapp.screen.Screen.BottomScreen.Home
import com.example.musicapp.screen.Screen.BottomScreen.Library
import com.example.musicapp.screen.Screen.DrawerScreen.Account
import com.example.musicapp.screen.Screen.DrawerScreen.AddAccount
import com.example.musicapp.screen.Screen.DrawerScreen.Subscription
import com.example.musicapp.viewmodel.MainViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel = viewModel()) {
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    var showBottomSheet = remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    val dialogOpen = remember { mutableStateOf(false) }

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route


    val drawerScreens = listOf(Account, Subscription, AddAccount)
    val bottomScreens = listOf(Home, Browse, Library)

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text(modifier = Modifier.padding(16.dp), text = "Music App Menu")
                HorizontalDivider()
                LazyColumn {
                    items(drawerScreens) { item ->
                        DrawerItem(item = item, selected = item.route == currentRoute) {
                            scope.launch { drawerState.close() }
                            if (item.route == AddAccount.route) {
                                dialogOpen.value = true
                            } else {
                                navController.navigate(item.route)
                                viewModel.setCurrentScreen(item)
                            }
                        }
                    }
                }
            }
        },
    ) {
        val onOpenBottomSheet: () -> Unit = {
            showBottomSheet.value = true
        }

        Scaffold(
            topBar = {
                TopBar(
                    viewModel.currentScreen.value.title,
                    onOpenBottomSheet
                ) { scope.launch { drawerState.open() } }
            },
            bottomBar = { BottomBar(bottomScreens, viewModel, navController) }
        ) {
            Navigation(
                navController = navController,
                viewModel = viewModel,
                modifier = Modifier.padding(it)
            )

            AccountDialog(dialogOpen = dialogOpen)
            BottomSheet(sheetState = sheetState, showBottomSheet = showBottomSheet)
        }
    }
}

@Composable
fun DrawerItem(
    selected: Boolean = false,
    item: Screen.DrawerScreen,
    onClick: () -> Unit = {}
) {
    NavigationDrawerItem(
        label = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier.padding(end = 8.dp),
                    painter = painterResource(id = item.icon),
                    contentDescription = item.title
                )
                Text(text = item.title, style = MaterialTheme.typography.bodyLarge)
            }
        },
        selected = selected,
        onClick = onClick,
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    title: String,
    onOpenBottomSheet: () -> Unit,
    onMenuClick: () -> Unit
) {
    TopAppBar(
        title = { Text(title) },
        colors = TopAppBarDefaults
            .topAppBarColors(containerColor = colorResource(id = R.color.purple_200)),
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(imageVector = Icons.Default.AccountCircle, contentDescription = "menu")
            }
        },
        actions = {
            IconButton(onClick = onOpenBottomSheet) {
                Icon(imageVector = Icons.Default.MoreVert, contentDescription = "more")
            }
        }
    )
}

@Composable
fun BottomBar(
    bottomScreens: List<Screen.BottomScreen>,
    viewModel: MainViewModel,
    navController: NavHostController
) {
    BottomAppBar(
        modifier = Modifier.wrapContentSize(),
        containerColor = colorResource(id = R.color.purple_200),
        actions =
        {
            val currentRoute = viewModel.currentScreen.value

            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                bottomScreens
                    .forEach { screen ->
                        val tint =
                            if (currentRoute.route == screen.route)
                                Color.LightGray
                            else
                                Color.Transparent

                        Column {
                            IconButton(
                                colors = IconButtonDefaults.iconButtonColors(containerColor = tint),
                                onClick = {
                                    navController.navigate(screen.route)
                                    viewModel.setCurrentScreen(screen)
                                })
                            {
                                Icon(
                                    painter = painterResource(id = screen.icon),
                                    contentDescription = screen.title
                                )
                            }
                            Text(text = screen.title)
                        }
                    }
            }
        }
    )
}