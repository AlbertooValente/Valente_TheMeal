package com.example.valente_themeal

import retrofit2.http.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

interface MealApiService {
    //categorie di pasti
    @GET("categories.php")
    suspend fun getCategories(): ListaCategory

    //lista dei pasti di una categoria specifica
    @GET("filter.php")
    suspend fun getMealsByCategory(@Query("c") category: String): MealsResponse

    //dettagli di un pasto tramite ID
    @GET("lookup.php")
    suspend fun getMeal(@Query("i") mealId: String): MealsResponse

    //pasti per nome
    @GET("search.php")
    suspend fun searchMeals(@Query("s") mealName: String): MealsResponse

    //ricetta casuale
    @GET("random.php")
    suspend fun getRandomMeal(): MealsResponse

    //lista ingredienti
    @GET("list.php?i")
    suspend fun getIngredients(): IngredientResponse

    //lista dei pasti con un ingrediente
    @GET("filter.php")
    suspend fun getMealsByIngredient(@Query("i") ingredient: String): MealsResponse
}


object RetrofitInstance {
    private const val BASE_URL = "https://www.themealdb.com/api/json/v1/1/"

    val api: MealApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MealApiService::class.java)
    }
}