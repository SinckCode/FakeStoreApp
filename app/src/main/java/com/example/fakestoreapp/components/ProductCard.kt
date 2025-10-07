package com.example.fakestoreapp.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.fakestoreapp.models.Product
import com.example.fakestoreapp.models.Rating
import com.example.fakestoreapp.screens.HomeScreen
import com.example.fakestoreapp.ui.theme.FakeStoreAppTheme

@Composable
fun ProductCard(
    product : Product,
    onClick : () -> Unit,
){
    Row(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
            .height(100.dp)
            .clip(CircleShape)
            .background(Color.Cyan)
            .padding(10.dp)
            .clickable{
                onClick()
            },
        verticalAlignment = Alignment.CenterVertically
    ) {

        AsyncImage(
            model = product.image,
            contentDescription = product.title,
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(Color.White),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .padding(start = 10.dp)
                .weight(1f)
        ) {
            Text(
                text = product.title
            )
            Text(
                text = product.category
            )
        }

        Text(
            text = "$${product.price}"
        )

    }
}

@Preview
@Composable
fun ProductCardPreview(){

    val testProduct = Product(
        id = 1,
        title = "Camiseta de prueba",
        price = 19.99,
        description = "Camiseta cómoda y de alta calidad.",
        category = "Ropa",
        image = "https://ejemplo.com/camiseta.png",
        rating = Rating(rate = 4.5, count = 120)
    )

    FakeStoreAppTheme {
        ProductCard(
            product = testProduct,
            onClick = {  }
        )
    }
}