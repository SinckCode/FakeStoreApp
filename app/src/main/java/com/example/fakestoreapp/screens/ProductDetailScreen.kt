package com.example.fakestoreapp.screens

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.fakestoreapp.models.Product
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.fakestoreapp.services.ProductService
import com.example.fakestoreapp.ui.theme.ProductDetailScreenRoute
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Composable
fun ProductDetailScreen(
    id : Int,
    contentPadding: PaddingValues
){


    var product by remember {
        mutableStateOf<Product?>(null)
    }

    LaunchedEffect(true) {
        try {
            val retrofit = Retrofit
                .Builder()
                .baseUrl("https://fakestoreapi.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val service = retrofit.create(ProductService::class.java)
            val result = withContext(Dispatchers.IO){
                service.getProductById(id)
            }
            product = result
            Log.i("ProductDetailScreen", product.toString())

        }catch (e: Exception){
            Log.e("ProductDetailScreen", product.toString())
        }
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding),
        contentAlignment = Alignment.Center
    ){
        Text(product?.title ?: "Error al cargar")
    }
}