package com.example.recipeapp.meal_api

import com.example.recipeapp.client.RetrofitClient
import com.example.recipeapp.meal_api.service.CategoryService


private val retrofit = RetrofitClient()
    .getClient("https://www.themealdb.com/api/json/v1/1/")


fun categoryService() = retrofit.create(CategoryService::class.java)