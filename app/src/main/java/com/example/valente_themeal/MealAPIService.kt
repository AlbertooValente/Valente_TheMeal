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
    suspend fun getMealsByCategory(@Query("c") category: String): List<Meal>

    //dettagli di un pasto tramite ID
    @GET("lookup.php")
    suspend fun getMeal(@Query("i") mealId: String): Meal

    //pasti per nome
    @GET("search.php")
    suspend fun searchMeals(@Query("s") mealName: String): List<Meal>

    //ricetta casuale
    @GET("random.php")
    suspend fun getRandomMeal(): MealsResponse
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