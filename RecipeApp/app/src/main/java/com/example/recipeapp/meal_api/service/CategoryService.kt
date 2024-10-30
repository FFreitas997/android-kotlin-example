package com.example.recipeapp.meal_api.service

import com.example.recipeapp.data.CategoryResponse
import retrofit2.http.GET

/**
 * Interface for CategoryRepository
 * @see <a href="https://www.themealdb.com" >Meal API</a>
 */

interface CategoryService {

    @GET("categories.php")
    suspend fun getCategories(): CategoryResponse
}