package com.example.recipeapp.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipeapp.meal_api.categoryService
import kotlinx.coroutines.launch
import com.example.recipeapp.viewmodel.state.CategoryState

class CategoryViewModel : ViewModel() {

    private val _categoryService = categoryService()

    private val _categoriesState = mutableStateOf(CategoryState())
    val categoriesState: State<CategoryState> = _categoriesState

    init { fetchCategories() }

    private fun fetchCategories() {
        viewModelScope.launch {
            try {
                val response = _categoryService.getCategories()
                _categoriesState.value = _categoriesState.value
                    .copy(
                        categories = response.categories,
                        loading = false,
                        error = null
                    )
            } catch (e: Exception) {
                _categoriesState.value = _categoriesState.value
                    .copy(
                        loading = false,
                        error = e.message
                    )
            }
        }
    }
}