package com.example.valente_themeal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
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
val Verde = Color(0xFF4CAF50)     //pulsante principale
val VerdePiuScuro = Color(0xFF3B8C3D)  //per accenti scuri
val Bianco = Color(0xFFF9F9F9)     //sfondo
val TestoPrincipale = Color(0xFF212121)     //testo principale
val TestoSecondario = Color(0xFF757575)     //testo secondario
val Arancione = Color(0xFFFFA726)  //accent color
val BgCard = Color(0xFFFFFFFF)    //sfondo card

@Composable
fun Nav(){
    val navController = rememberNavController()
    val apiService = RetrofitInstance.api
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            if(currentRoute == "home" || currentRoute == "cerca"){
                NavigationBar(
                    containerColor = Bianco,
                    tonalElevation = 8.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                        .shadow(
                            elevation = 16.dp,
                            spotColor = Color.Gray.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                        )
                ) {
                    //elemento navbar "Home"
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                        label = { Text("Home") },
                        selected = currentRoute == "home",
                        onClick = {
                            if (currentRoute != "home") {
                                navController.navigate("home") {
                                    popUpTo("home") { inclusive = true }
                                }
                            }
                        }
                    )

                    //elemento navbar "Cerca"
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Search, contentDescription = "Cerca") },
                        label = { Text("Cerca") },
                        selected = currentRoute == "cerca",
                        onClick = {
                            if (currentRoute != "cerca") {
                                navController.navigate("cerca") {
                                    popUpTo("cerca") { inclusive = true }
                                }
                            }
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),

            color = Bianco
        ) {
            NavHost(navController = navController, startDestination = "home") {
                composable("home") { Home(apiService, navController) }

                composable("cerca") { CercaPage(navController) }

                composable("ricercaRicette/{nomeRicetta}") { navbackStackEntry ->
                    val nomeRicetta = navbackStackEntry.arguments?.getString("nomeRicetta") ?: ""
                    RicercaRicetta(apiService, nomeRicetta, navController)
                }

                composable("ricetta/{idRicetta}") { navbackStackEntry ->
                    val idRicetta = navbackStackEntry.arguments?.getString("idRicetta") ?: ""
                    RicettaFromId(apiService, idRicetta, navController)
                }

                composable("categoria/{nomeCategoria}") { navBackStackEntry ->
                    val nomeCategoria = navBackStackEntry.arguments?.getString("nomeCategoria") ?: ""
                    RicettaCategoriaIngrediente(apiService, nomeCategoria, tipo = true, navController)
                }

                composable("ingrediente/{nomeIngrediente}") { navBackStackEntry ->
                    val nomeIngrediente = navBackStackEntry.arguments?.getString("nomeIngrediente") ?: ""
                    RicettaCategoriaIngrediente(apiService, nomeIngrediente, tipo = false, navController)
                }
            }
        }
    }
}

@Composable
fun Home(apiService: MealApiService, navController: NavHostController){
    var categories by remember { mutableStateOf<ListaCategory?>(null) }
    var ingredienti by remember { mutableStateOf<IngredientResponse?>(null) }
    var randomMeals by remember { mutableStateOf<List<Meal>?>(null) }

    LaunchedEffect(Unit) {
        categories = apiService.getCategories()
        ingredienti = apiService.getIngredients()

        val randomMealsList = mutableListOf<Meal>()

        repeat(10) {
            val meal = apiService.getRandomMeal().meals?.firstOrNull()

            if (meal != null && !randomMealsList.any { it.idMeal == meal.idMeal }) {
                randomMealsList.add(meal)
            }
        }

        randomMeals = randomMealsList
    }

    val scrollState = rememberScrollState()

    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 20.dp)
            .verticalScroll(scrollState),

        horizontalAlignment = Alignment.CenterHorizontally
    ){
        //titolo
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Get Your Meal",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = Verde,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        //ricerca per categorie
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),

            colors = CardDefaults.cardColors(containerColor = BgCard),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Categorie",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TestoPrincipale
                )
                Spacer(Modifier.height(16.dp))

                if (categories == null) {
                    Text("Caricamento...", modifier = Modifier.padding(18.dp), color = TestoSecondario)
                } else {
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(categories!!.categories) { category ->
                            CategoryItem(category, navController)
                        }
                    }
                }
            }
        }


        //ricerca per ingrediente
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),

            colors = CardDefaults.cardColors(containerColor = BgCard),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Ingredienti",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TestoPrincipale
                )
                Spacer(Modifier.height(16.dp))

                if (ingredienti == null) {
                    Text("Caricamento...", modifier = Modifier.padding(18.dp), color = TestoSecondario)
                } else {
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(ingredienti!!.meals) { ingrediente ->
                            IngredientItem(ingrediente, navController)
                        }
                    }
                }
            }
        }

        //ricette random
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),

            colors = CardDefaults.cardColors(containerColor = BgCard),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Ricette Suggerite",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TestoPrincipale
                )
                Spacer(Modifier.height(16.dp))

                if (randomMeals == null) {
                    Text("Caricamento...", modifier = Modifier.padding(18.dp), color = TestoSecondario)
                } else {
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(randomMeals!!) { meal ->
                            RandomMealItem(meal, navController)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CercaPage(navController: NavHostController){
    val scrollState = rememberScrollState()
    var textInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),

        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Text(
                text = "Cerca Ricette",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = Verde,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),

            colors = CardDefaults.cardColors(containerColor = BgCard),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Digita il nome del piatto da cercare",
                    fontSize = 18.sp,
                    color = TestoPrincipale,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                TextField(
                    value = textInput,
                    onValueChange = { textInput = it },
                    label = { Text("Nome piatto") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),

                    colors = TextFieldDefaults.colors(
                        focusedTextColor = TestoPrincipale,
                        unfocusedTextColor = TestoPrincipale,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        cursorColor = Verde,
                        focusedLabelColor = Verde,
                        focusedIndicatorColor = Verde
                    )
                )

                Button(
                    onClick = {
                        if (textInput.isNotBlank()) {
                            navController.navigate("ricercaRicette/${textInput}")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Verde)
                ) {
                    Text(
                        text = "Cerca 🔍",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun RicercaRicetta(apiService: MealApiService, nomeRicetta: String, navController: NavHostController) {
    val scrollState = rememberScrollState()
    var meals by remember { mutableStateOf<List<Meal>?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var isError by remember { mutableStateOf(false) }

    LaunchedEffect(nomeRicetta) {
        isLoading = true
        isError = false

        try {
            meals = apiService.searchMeals(nomeRicetta).meals
            isError = meals.isNullOrEmpty()
        } catch (e: Exception) {
            isError = true
        }

        isLoading = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 30.dp)
            .padding(horizontal = 16.dp)
            .verticalScroll(scrollState)
    ){
        Button(
            onClick = { navController.popBackStack() },
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(Bianco)
        ) {
            Text(text = "← Back", fontSize = 16.sp, color = TestoSecondario)
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),

            colors = CardDefaults.cardColors(containerColor = BgCard),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),

                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Risultati per \"$nomeRicetta\"",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TestoPrincipale,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                if (isLoading) {
                    Text("Caricamento...", fontSize = 18.sp, color = TestoSecondario)
                } else if (isError) {
                    Text("Nessuna ricetta trovata per '$nomeRicetta'", fontSize = 18.sp, color = Color.Red)
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),

                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        meals?.forEach { meal ->
                            MealItem(meal, navController)
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RicettaCategoriaIngrediente(apiService: MealApiService, parametro: String, tipo: Boolean, navController: NavHostController){
    val scrollState = rememberScrollState()
    var meals by remember { mutableStateOf<List<Meal>?>(null) }

    LaunchedEffect(parametro) {
        if(tipo){
            meals = apiService.getMealsByCategory(parametro).meals
        }
        else{
            meals = apiService.getMealsByIngredient(parametro).meals
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp)
            .padding(horizontal = 16.dp)
            .verticalScroll(scrollState)
    ) {
        Button(
            onClick = { navController.popBackStack() },
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(Bianco)
        ) {
            Text(text = "← Back", fontSize = 16.sp, color = TestoSecondario)
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),

            colors = CardDefaults.cardColors(containerColor = BgCard),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),

                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (tipo) "Categoria: $parametro" else "Ingrediente: $parametro",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TestoPrincipale,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                if (meals == null) {
                    Text("Caricamento...", fontSize = 18.sp, color = TestoSecondario)
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),

                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        meals?.forEach { meal ->
                            MealItem(meal, navController)
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RicettaFromId(apiService: MealApiService, idRicetta: String, navController: NavHostController){
    val scrollState = rememberScrollState()
    var meal by remember { mutableStateOf<Meal?>(null) }

    LaunchedEffect(Unit) {
        meal = apiService.getMeal(idRicetta).meals?.firstOrNull()
    }

    if(meal == null){
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),

            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Caricamento...", fontSize = 18.sp, color = TestoSecondario)
        }
    }
    else{
        MostraRicetta(meal!!, navController, scrollState)
    }
}


@Composable
fun CategoryItem(categoria: Category, navController: NavHostController){
    Card(
        modifier = Modifier
            .width(160.dp)
            .padding(4.dp),

        colors = CardDefaults.cardColors(containerColor = Verde),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Verde)
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = categoria.strCategoryThumb,
                contentDescription = categoria.strCategory,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .padding(4.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = categoria.strCategory,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(4.dp))

            Button(
                onClick = { navController.navigate("categoria/${categoria.strCategory}") },
                colors = ButtonDefaults.buttonColors(containerColor = VerdePiuScuro),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Scopri",
                    fontSize = 14.sp,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun IngredientItem(ingrediente: Ingredient, navController: NavHostController){
    Card(
        modifier = Modifier
            .width(140.dp)
            .padding(4.dp),

        colors = CardDefaults.cardColors(containerColor = Arancione),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Arancione)
                .padding(8.dp),

            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = ingrediente.strIngredient,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { navController.navigate("ingrediente/${ingrediente.strIngredient}") },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE69500)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Ricette",
                    fontSize = 14.sp,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun RandomMealItem(meal: Meal, navController: NavHostController) {
    Card(
        modifier = Modifier
            .width(200.dp)
            .padding(4.dp),
        colors = CardDefaults.cardColors(containerColor = BgCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = meal.strMealThumb,
                contentDescription = meal.strMeal,
                modifier = Modifier
                    .size(180.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = meal.strMeal,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TestoPrincipale,
                textAlign = TextAlign.Center,
                maxLines = 2,
                modifier = Modifier.height(48.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Button(
                onClick = { navController.navigate("ricetta/${meal.idMeal}") },
                colors = ButtonDefaults.buttonColors(containerColor = Verde),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Vedi Ricetta",
                    fontSize = 14.sp,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun MealItem(meal: Meal, navController: NavHostController){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = BgCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = meal.strMealThumb,
                contentDescription = meal.strMeal,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = meal.strMeal,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TestoPrincipale
                )
            }
            Button(
                onClick = { navController.navigate("ricetta/${meal.idMeal}") },
                colors = ButtonDefaults.buttonColors(containerColor = Verde),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Vedi",
                    fontSize = 14.sp,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun MostraRicetta(meal: Meal, navController: NavHostController, scrollState: ScrollState){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 30.dp, bottom = 30.dp)
            .padding(horizontal = 16.dp)
            .verticalScroll(scrollState)
    ){
        Button(
            onClick = { navController.popBackStack() },
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(Bianco)
        ) {
            Text(text = "← Back", fontSize = 16.sp, color = TestoSecondario)
        }

        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp),

            colors = CardDefaults.cardColors(containerColor = Bianco),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),

                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Text(
                    text = meal.strMeal,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = TestoPrincipale,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 10.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))

                AsyncImage(
                    model = meal.strMealThumb,
                    contentDescription = meal.strMeal,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(240.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .border(2.dp, Verde.copy(alpha = 0.3f), RoundedCornerShape(24.dp))
                )
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),

                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Verde.copy(alpha = 0.2f)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.padding(4.dp)
                    ) {
                        Text(
                            text = "Categoria: ${meal.strCategory}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TestoPrincipale,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Card(
                        colors = CardDefaults.cardColors(containerColor = Arancione.copy(alpha = 0.2f)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.padding(4.dp)
                    ) {
                        Text(
                            text = "Area: ${meal.strArea}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TestoPrincipale,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                HorizontalDivider(
                    color = Color.LightGray,
                    thickness = 1.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp)
                )

                //ingredienti
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),

                    colors = CardDefaults.cardColors(containerColor = BgCard),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Ingredienti",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TestoPrincipale
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        ListaIngredienti(meal)
                    }
                }

                //istruzioni
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = BgCard),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Istruzioni",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TestoPrincipale
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = meal.strInstructions,
                            fontSize = 16.sp,
                            color = TestoSecondario,
                            lineHeight = 24.sp
                        )
                    }
                }
            }
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

    LazyColumn(
        modifier = Modifier.height((ingredienti.size * 36).dp.coerceAtMost(250.dp))
    ) {
        items(ingredienti) { (ingrediente, misura) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),

                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(Verde, CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = ingrediente ?: "",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = TestoPrincipale,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = misura ?: "",
                    fontSize = 16.sp,
                    color = TestoSecondario,
                    fontStyle = FontStyle.Italic
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun AppPreview() {
    Nav()
}