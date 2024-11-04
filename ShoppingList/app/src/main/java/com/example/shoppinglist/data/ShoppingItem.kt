package com.example.shoppinglist.data

data class ShoppingItem(
    val id: Int,
    var name: String = "no name item",
    var quantity: Int = 0,
    var isEditing: Boolean = false,
    var address: String = "no address"
)
