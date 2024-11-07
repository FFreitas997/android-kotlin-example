package com.example.wishlist.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wishlist.Graph
import com.example.wishlist.data.Wish
import com.example.wishlist.repository.WishRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class WishViewModel(private val repository: WishRepository = Graph.repository) : ViewModel() {

    var wishTitleState by mutableStateOf("")
    var wishDescriptionState by mutableStateOf("")

    lateinit var listOfWishes: Flow<List<Wish>>

    init {
        viewModelScope
            .launch { listOfWishes = repository.getAllWishes() }
    }

    fun onWishTitleChange(wishTitle: String) {
        wishTitleState = wishTitle
    }

    fun onWishDescriptionChange(wishDescription: String) {
        wishDescriptionState = wishDescription
    }

    fun onWishSubmit(wishID: Long, onNotify: (String) -> Unit) {
        if (wishTitleState.isEmpty() || wishDescriptionState.isEmpty()) {
            onNotify("Please fill all the fields to create a wish")
            return
        }
        if (wishID != 0L) {
            val updatedWish = Wish(
                id = wishID,
                title = wishTitleState.trim(),
                description = wishDescriptionState.trim()
            )
            updateWish(updatedWish)
            onNotify("Wish updated successfully")
        } else {
            val newWish = Wish(
                title = wishTitleState.trim(),
                description = wishDescriptionState.trim()
            )
            createWish(newWish)
            onNotify("Wish created successfully")
        }
    }


    fun createWish(wish: Wish) {
        viewModelScope
            .launch(Dispatchers.IO) { repository.createWish(entity = wish) }
    }

    fun updateWish(wish: Wish) {
        viewModelScope
            .launch(Dispatchers.IO) { repository.updateWish(entity = wish) }
    }

    fun deleteWish(wish: Wish) {
        viewModelScope
            .launch(Dispatchers.IO) { repository.deleteWish(entity = wish) }
    }

    fun getWishById(id: Long): Flow<Wish> = repository.getWishById(id = id)

}