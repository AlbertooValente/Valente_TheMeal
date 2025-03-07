package com.example.valente_themeal

data class Ingredient(
    val idIngredient: String,
    val strIngredient: String,
    val strDescription: String,
    val strType: String
)

data class IngredientResponse(
    val meals: List<Ingredient>     //si chiama meals perchè la risposta alla richiesta è una lista chiamata meals
)
