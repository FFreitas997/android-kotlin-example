package com.example.shoppinglist

data class ShoppingItem(
    val id: Int,
    var name: String = "no name item",
    var quantity: Int = 0,
    var isEditing: Boolean = false
)
