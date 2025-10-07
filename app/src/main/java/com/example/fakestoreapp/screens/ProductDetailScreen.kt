package com.example.fakestoreapp.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.example.fakestoreapp.models.Product
import com.example.fakestoreapp.services.ProductService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.floor

@Composable
fun ProductDetailScreen(
    id: Int,
    contentPadding: PaddingValues,
    navController: NavController? = null
) {
    var product by remember { mutableStateOf<Product?>(null) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(id) {
        try {
            val retrofit = Retrofit.Builder()
                .baseUrl("https://fakestoreapi.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val service = retrofit.create(ProductService::class.java)
            product = withContext(Dispatchers.IO) { service.getProductById(id) }
        } catch (e: Exception) {
            Log.e("ProductDetailScreen", "Error: ${e.message}")
        } finally {
            loading = false
        }
    }

    Scaffold(
        bottomBar = {
            // Buy now + precio
            val p = product
            Surface(color = Color.White) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (p != null) {
                        Text(
                            text = NumberFormat.getCurrencyInstance(Locale.US).format(p.price),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.weight(1f))
                    }
                    Button(
                        onClick = { /* TODO: agregar al carrito / comprar */ },
                        modifier = Modifier.height(56.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) { Text("Buy now") }
                }
            }
        }
    ) { insets ->
        if (loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(insets)
                    .padding(contentPadding),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }
            return@Scaffold
        }

        val p = product
        if (p == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(insets)
                    .padding(contentPadding),
                contentAlignment = Alignment.Center
            ) { Text("No se pudo cargar el producto") }
            return@Scaffold
        }

        // ======= UI =======
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(insets)
                .padding(contentPadding)
        ) {
            // Cabecera con fondo y top bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp)
                    .background(Color(0xFFE9EEF6), RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
            )

            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController?.popBackStack() }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                }
                Spacer(Modifier.weight(1f))
                Text("Details", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.weight(1f))
                IconButton(onClick = { /* wishlist */ }) {
                    Icon(Icons.Outlined.FavoriteBorder, contentDescription = "Fav")
                }
            }

            // Imagen grande
            AsyncImage(
                model = p.image,
                contentDescription = p.title,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 64.dp)
                    .size(230.dp)
            )

            // Hoja inferior con info
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {

                    // Título + categoría como pill + disponibilidad
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                p.title,
                                modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Pill(text = p.category.replaceFirstChar { it.uppercase() })
                            val inStock = p.rating.count > 0
                            Pill(
                                text = if (inStock) "In stock" else "Out of stock",
                                container = if (inStock) Color(0xFFE7F8ED) else Color(0xFFFCE8E8),
                                content = if (inStock) Color(0xFF0E7A3E) else Color(0xFFB42318)
                            )
                        }
                    }

                    // Rating (estrellas) + total reviews
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RatingStars(rate = p.rating.rate)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "(${p.rating.count})",
                            color = Color(0xFF6B7280),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    // Descripción (truncada como el mockup)
                    Text(
                        p.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF6B7280),
                        maxLines = 4,
                        overflow = TextOverflow.Ellipsis
                    )

                    // Mini ficha de datos
                    InfoRow(label = "Category", value = p.category)
                    InfoRow(label = "Rating", value = "${"%.1f".format(p.rating.rate)} / 5")
                }
            }
        }
    }
}

/* ---------- Helpers ---------- */

@Composable
private fun Pill(
    text: String,
    container: Color = Color(0xFFF3F4F6),
    content: Color = Color(0xFF111827)
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(container)
            .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(10.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) { Text(text, color = content, style = MaterialTheme.typography.labelLarge) }
}

@Composable
private fun RatingStars(rate: Double, max: Int = 5) {
    // genera enteras, media y vacías
    val full = floor(rate).toInt().coerceIn(0, max)
    val hasHalf = (rate - full) >= 0.25 && (rate - full) < 0.75
    val half = if (hasHalf && full < max) 1 else 0
    val empty = (max - full - half).coerceAtLeast(0)

    Row(verticalAlignment = Alignment.CenterVertically) {
        repeat(full) {
            Icon(
                imageVector = Icons.Rounded.Star,
                contentDescription = null,
                tint = Color(0xFFFFC107),
                modifier = Modifier.size(18.dp)
            )
        }
        repeat(half) {
            Icon(
                imageVector = Icons.Rounded.Star,
                contentDescription = null,
                tint = Color(0xFFFFC107),
                modifier = Modifier.size(18.dp)
            )
        }
        repeat(empty) {
            Icon(
                imageVector = Icons.Rounded.PlayArrow,
                contentDescription = null,
                tint = Color(0xFFFFC107),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF9FAFB))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, modifier = Modifier.weight(1f), color = Color(0xFF6B7280))
        Text(value, fontWeight = FontWeight.Medium)
    }
}
