package com.example.wishlist.screen

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.wishlist.R
import com.example.wishlist.components.DismissBackgroundContentView
import com.example.wishlist.components.FloatingActionButtonView
import com.example.wishlist.components.TopBarView
import com.example.wishlist.components.WishItem
import com.example.wishlist.screen.Screen.AddScreen
import com.example.wishlist.screen.Screen.EditScreen
import com.example.wishlist.viewmodel.WishViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: WishViewModel, navController: NavController) {

    val wishList = viewModel
        .listOfWishes
        .collectAsState(initial = listOf())

    Scaffold(
        topBar = {
            TopBarView(title = stringResource(id = R.string.app_name), isHomeScreen = true)
        },
        floatingActionButton = {
            FloatingActionButtonView { navController.navigate(AddScreen.route) }
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            items(items = wishList.value, key = { wish -> wish.id }) { wish ->
                val dismissState = rememberSwipeToDismissBoxState(
                    confirmValueChange = {
                        when (it) {
                            SwipeToDismissBoxValue.StartToEnd -> viewModel.deleteWish(wish)
                            SwipeToDismissBoxValue.EndToStart -> viewModel.deleteWish(wish)
                            SwipeToDismissBoxValue.Settled -> {}
                            else -> {}
                        }
                        true
                    }
                )
                SwipeToDismissBox(
                    modifier = Modifier.padding(top = 16.dp, start = 8.dp),
                    state = dismissState,
                    backgroundContent = { DismissBackgroundContentView(dismissState) },
                    enableDismissFromStartToEnd = false,
                    content = {
                        WishItem(wish = wish) {
                            navController.navigate("${EditScreen.route}/${wish.id}")
                        }
                    }
                )
            }
        }
    }
}
