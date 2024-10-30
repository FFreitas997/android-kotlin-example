package com.example.recipeapp.client

interface HttpClient<S> {
    fun getClient(baseURL: String): S
}