package com.example.valente_themeal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.valente_themeal.ui.theme.Valente_TheMealTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Valente_TheMealTheme {
                AppPreview()
            }
        }
    }
}

//palette colori
val ColoreButton = Color(0xFF4CAF50)     //pulsante
val Bianco = Color(0xFFF2F2F2)     //sfondo
val TestoPrincipale = Color(0xFF212121)     //testo principale
val TestoSecondario = Color(0xFF757575)     //testo secondario
val Blu = Color(0xFF03A9F4)  //linea cursore

@Composable
fun Nav(){
    val navController = rememberNavController()
    val apiService = RetrofitInstance.api

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        NavHost(navController = navController, startDestination = "home") {
            composable("home") { Home(apiService, navController) }
            composable("random") { Random(apiService, navController) }
        }
    }
}

@Composable
fun Home(apiService: MealApiService, navController: NavHostController){
    var categories by remember { mutableStateOf<ListaCategory?>(null) }

    LaunchedEffect(Unit) {
        categories = apiService.getCategories()
    }

    val scrollState = rememberScrollState()
    var textInput by remember { mutableStateOf("") }

    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 15.dp)
            .verticalScroll(scrollState),

        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Get Your Meal",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = ColoreButton,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
                .background(Bianco, shape = RoundedCornerShape(12.dp))
                .padding(16.dp)
        ){
            Column(
                modifier = Modifier.fillMaxWidth()
            ){
                Text(
                    text = "Cerca piatto",
                    fontSize = 20.sp,
                    color = TestoPrincipale
                )
                Spacer(Modifier.height(20.dp))
                Row (
                    modifier = Modifier
                        .fillMaxWidth(),

                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ){
                    TextField(
                        value = textInput,
                        onValueChange = { textInput = it },
                        label = { Text("Inserisci nome piatto") },
                        modifier = Modifier.weight(1f),
                        colors = TextFieldDefaults.colors(
                            TestoPrincipale,    //colore testo quando l'utente sta interagendo (focus)
                            TestoPrincipale,    //colore testo quando l'utente non sta interagendo (unfocused)
                            Blu,         //colore bordo inferiore quando è in focus
                            TestoSecondario,    //colore bordo inferiore quando non è in focus
                            Blu,         //colore linea del cursore
                            Color.White         //colore di sfondo
                        )
                    )

                    Button(
                        onClick = {},
                        modifier = Modifier.align(Alignment.CenterVertically),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(ColoreButton)
                    ) {
                        Text(
                            text = "🔍",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(0.dp)
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
                .background(Bianco, shape = RoundedCornerShape(12.dp))
                .padding(16.dp)
        ){
            Column(
                modifier = Modifier.fillMaxWidth()
            ){
                Text(
                    text = "Categorie",
                    fontSize = 20.sp,
                    color = TestoPrincipale
                )
                Spacer(Modifier.height(20.dp))

                if (categories == null) {
                    Text("Caricamento...", modifier = Modifier.padding(18.dp), color = TestoSecondario)
                } else {
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(categories!!.categories) { category ->
                            CategoryItem(category = category, navController)
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
                .background(Bianco, shape = RoundedCornerShape(12.dp))
                .padding(16.dp),

            contentAlignment = Alignment.Center
        ){
            Button(
                onClick = { navController.navigate("random") },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(ColoreButton)
            ) {
                Text(
                    text = "Ricetta random",
                    color = Bianco,
                    modifier = Modifier.padding(0.dp)
                )
            }
        }
    }
}

@Composable
fun CategoryItem(category: Category, navController: NavHostController){
    Button(
        onClick = { },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = ColoreButton)
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = category.strCategoryThumb,
                contentDescription = category.strCategory,
                modifier = Modifier
                    .size(50.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = category.strCategory,
                fontSize = 18.sp,
                color = Color.White
            )
        }
    }
}

@Composable
fun Random(apiService: MealApiService, navController: NavHostController){
    val scrollState = rememberScrollState()
    var meal by remember { mutableStateOf<Meal?>(null) }

    LaunchedEffect(Unit) {
        var response = apiService.getRandomMeal()
        meal = response.meals?.firstOrNull()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp, top = 30.dp)
            .verticalScroll(scrollState)
    ){
        Button(
            onClick = { navController.popBackStack() },
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(ColoreButton)
        ) {
            Text(text = "⬅ Home", fontSize = 16.sp, color = Bianco)
        }

        MostraRicetta(meal)
    }
}

@Composable
fun MostraRicetta(meal: Meal?){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Bianco, shape = RoundedCornerShape(12.dp)),

        horizontalAlignment = Alignment.CenterHorizontally
    ){
        if(meal == null){
            Text("Caricamento...", fontSize = 18.sp, color = TestoSecondario)
        }
        else{
            Text(
                text = meal.strMeal,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TestoPrincipale,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 20.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))

            AsyncImage(
                model = meal.strMealThumb,
                contentDescription = meal.strMeal,
                modifier = Modifier.size(200.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Categoria: ${meal.strCategory} | Area: ${meal.strArea}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = TestoSecondario,
                modifier = Modifier.padding(16.dp)
            )
            Spacer(modifier = Modifier.height(30.dp))

            Text(
                text = "Ingredienti:",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TestoPrincipale,
                modifier = Modifier.padding(16.dp, top = 0.dp)
            )
            ListaIngredienti(meal)
            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "Istruzioni:",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TestoPrincipale
            )
            Text(
                text = meal.strInstructions,
                fontSize = 16.sp,
                color = TestoSecondario,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Composable
fun ListaIngredienti(meal: Meal){
    val ingredienti = listOf(
        meal.strIngredient1 to meal.strMeasure1,
        meal.strIngredient2 to meal.strMeasure2,
        meal.strIngredient3 to meal.strMeasure3,
        meal.strIngredient4 to meal.strMeasure4,
        meal.strIngredient5 to meal.strMeasure5,
        meal.strIngredient6 to meal.strMeasure6,
        meal.strIngredient7 to meal.strMeasure7,
        meal.strIngredient8 to meal.strMeasure8,
        meal.strIngredient9 to meal.strMeasure9,
        meal.strIngredient10 to meal.strMeasure10,
        meal.strIngredient11 to meal.strMeasure11,
        meal.strIngredient12 to meal.strMeasure12,
        meal.strIngredient13 to meal.strMeasure13,
        meal.strIngredient14 to meal.strMeasure14,
        meal.strIngredient15 to meal.strMeasure15,
        meal.strIngredient16 to meal.strMeasure16,
        meal.strIngredient17 to meal.strMeasure17,
        meal.strIngredient18 to meal.strMeasure18,
        meal.strIngredient19 to meal.strMeasure19,
        meal.strIngredient20 to meal.strMeasure20
    ).filter { it.first?.isNotBlank() == true }     //rimuove ingredienti null/vuoti

    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        ingredienti.forEach { (ingrediente, misura) ->
            Text(
                text = "- $ingrediente: ${misura ?: "q.b."}",   //se la misura è vuota stampa q.b. (quanto basta)
                fontSize = 16.sp,
                color = TestoSecondario
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppPreview() {
    Nav()
}