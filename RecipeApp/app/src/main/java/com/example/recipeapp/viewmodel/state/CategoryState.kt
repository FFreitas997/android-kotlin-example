package com.example.recipeapp.viewmodel.state

import com.example.recipeapp.data.Category

data class CategoryState(
    val categories: List<Category> = emptyList(),
    val loading: Boolean = true,
    val error: String? = null
)
