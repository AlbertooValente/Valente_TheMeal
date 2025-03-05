package com.example.valente_themeal

data class Category(
    val idCategory: String,
    val strCategory: String,
    val strCategoryThumb: String,
    val strCategoryDescription: String
)

data class ListaCategory(
    val categories: List<Category>
)
