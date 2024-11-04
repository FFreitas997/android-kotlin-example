package com.example.shoppinglist

import android.Manifest
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import com.example.shoppinglist.data.ShoppingItem
import com.example.shoppinglist.utils.LocationUtils
import com.example.shoppinglist.viewmodels.LocationViewModel


@Composable
fun ShoppingListApp(
    modifier: Modifier,
    locationUtils: LocationUtils,
    viewModel: LocationViewModel,
    navController: NavController,
    context: Context,
    address: String,
    dialogOpen: MutableState<Boolean>
) {

    var listOfItems by remember { mutableStateOf(listOf<ShoppingItem>()) }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            onResultFromRequestPermission(context, permissions)
        }
    )

    val onEditItemList = { item: ShoppingItem ->
        listOfItems = listOfItems.map { it.copy(isEditing = it.id == item.id) }
    }

    val onDeleteItemList = { item: ShoppingItem ->
        listOfItems = listOfItems - item
    }

    val onSelectLocation = {
        val coarseLocationPermission = Manifest.permission.ACCESS_COARSE_LOCATION
        val fineLocationPermission = Manifest.permission.ACCESS_FINE_LOCATION

        if (locationUtils.hasLocationPermission()) {
            locationUtils.requestLocationUpdates(viewModel)
            navController
                .navigate("locationscreen"){ launchSingleTop = true }
        } else
            requestPermissionLauncher
                .launch(arrayOf(coarseLocationPermission, fineLocationPermission))
    }


    Column(modifier = modifier.fillMaxSize()) {

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            items(listOfItems) { item ->
                if (item.isEditing)
                    ShoppingItemEditor(item){ name, quantity ->
                        listOfItems = listOfItems.map { it.copy(isEditing = false) }
                        val editedItem = listOfItems.find { it.id == item.id }
                        editedItem?.let {
                            it.name = name
                            it.quantity = quantity
                            it.address = address
                        }
                    }
                else
                    ShoppingListItem(item, onEditItemList, onDeleteItemList)
            }
        }

    }

    if (dialogOpen.value) {

        val onDismissRequest = { dialogOpen.value = false }

        AlertDialogDisplay(onSelectLocation, onDismissRequest) { name, quantity ->
            val newItem = ShoppingItem(
                id = listOfItems.size + 1,
                name = if (name.isNotBlank()) name else "Unknown",
                quantity = if (quantity.isNotBlank()) quantity.toIntOrNull() ?: 0 else 0,
                address = address
            )
            listOfItems = listOfItems + newItem
            dialogOpen.value = false
        }
    }
}

fun onResultFromRequestPermission(context: Context, permissions: Map<String, Boolean>) {
    val coarseLocationPermission = Manifest.permission.ACCESS_COARSE_LOCATION
    val fineLocationPermission = Manifest.permission.ACCESS_FINE_LOCATION

    if (permissions[coarseLocationPermission] == false || permissions[fineLocationPermission] == false){

        val shouldRequestPermissionForFineLocation = ActivityCompat
            .shouldShowRequestPermissionRationale(context as MainActivity, fineLocationPermission)

        val shouldRequestPermissionForCoarseLocation = ActivityCompat
            .shouldShowRequestPermissionRationale(context, coarseLocationPermission)

        if (shouldRequestPermissionForFineLocation || shouldRequestPermissionForCoarseLocation)
            Toast
                .makeText(context, "Location permission required", Toast.LENGTH_SHORT)
                .show()
        else
            Toast
                .makeText(context, "Location permission denied, please enable it in the settings", Toast.LENGTH_SHORT)
                .show()
        return
    }
    Toast
        .makeText(context, "Location permission granted", Toast.LENGTH_SHORT)
        .show()
}







