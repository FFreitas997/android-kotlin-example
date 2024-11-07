package com.example.wishlist.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.wishlist.R
import com.example.wishlist.components.TopBarView
import com.example.wishlist.data.Wish
import com.example.wishlist.viewmodel.WishViewModel
import kotlinx.coroutines.launch

@Composable
fun AddEditWishScreen(
    id: Long = 0L,
    viewModel: WishViewModel,
    navController: NavController
) {
    val isEditing = id != 0L
    val topBarTitle =
        if (isEditing)
            stringResource(id = R.string.update_wish)
        else
            stringResource(id = R.string.add_wish)

    val snackBarMessage = remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    val snackBarHostState = remember { SnackbarHostState() }

    if (isEditing) {
        val editWish = viewModel.getWishById(id).collectAsState(initial = Wish(0L, "", ""))
        viewModel.onWishTitleChange(editWish.value.title)
        viewModel.onWishDescriptionChange(editWish.value.description)
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) },
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopBarView(title = topBarTitle) { navController.navigateUp() }
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .wrapContentSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            WishTextField(
                label = "Title",
                value = viewModel.wishTitleState,
                onValueChange = { viewModel.onWishTitleChange(it) }
            )
            WishTextField(
                label = "Description",
                value = viewModel.wishDescriptionState,
                onValueChange = { viewModel.onWishDescriptionChange(it) }
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    viewModel.onWishSubmit(id) { snackBarMessage.value = it }
                    scope.launch {
                        viewModel.onWishTitleChange("")
                        viewModel.onWishDescriptionChange("")
                        navController.navigateUp()
                        snackBarHostState.showSnackbar(
                            message = snackBarMessage.value,
                            actionLabel = "Dismiss",
                            duration = SnackbarDuration.Long
                        )
                    }
                }
            ){
                Text(if (isEditing) "Update Wish" else "Add Wish")
            }
        }
    }
}

@Composable
fun WishTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(text = label) },
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = colorResource(id = R.color.black),
            unfocusedTextColor = colorResource(id = R.color.black),
            cursorColor = colorResource(id = R.color.black),
            focusedLabelColor = colorResource(id = R.color.black),
            unfocusedLabelColor = colorResource(id = R.color.black),
        )
    )
}