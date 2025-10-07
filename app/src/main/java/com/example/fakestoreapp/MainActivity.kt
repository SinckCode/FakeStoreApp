package com.example.fakestoreapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.fakestoreapp.screens.ProductDetailScreen
import com.example.fakestoreapp.screens.ProductsScreen
import com.example.fakestoreapp.ui.theme.FakeStoreAppTheme
import com.example.fakestoreapp.ui.theme.HomeScreenRoute
import com.example.fakestoreapp.ui.theme.ProductDetailScreenRoute


/**
 *
 *
 * 1. Crear modelos
 * 2. Crear servicios
 * 3. Consumir servicios
 * 4. Refrescar Ui
 *
 *
 */


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FakeStoreAppTheme {
                val navController = rememberNavController()
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = HomeScreenRoute
                    ){
                        composable<HomeScreenRoute>{
                            ProductsScreen(
                                navController = navController,
                                contentPadding = innerPadding
                            )
                        }

                        composable<ProductDetailScreenRoute>{ backStack ->
                            val args = backStack.toRoute<ProductDetailScreenRoute>()
                            ProductDetailScreen(
                                id = args.id,
                                contentPadding = innerPadding,
                                navController = navController
                            )
                        }
                    }
                }
            }
        }
    }
}

