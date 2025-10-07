package com.example.fakestoreapp.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarBorder
import androidx.compose.material.icons.rounded.StarHalf
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

/* ------- Paleta para contraste ------- */
private val SurfaceSoft   = Color(0xFFF7F8FC)
private val HeaderSoft    = Color(0xFFE9EEF6)
private val TextPrimary   = Color(0xFF111827)
private val TextSecondary = Color(0xFF6B7280)
private val PillBg        = Color(0xFFF3F4F6)
private val PillBorder    = Color(0xFFE5E7EB)
private val SheetBg       = Color.White
private val StarColor     = Color(0xFFFFC107)
private val Accent        = Color(0xFFE3A37A)

@Composable
fun ProductDetailScreen(
    id: Int,
    contentPadding: PaddingValues,
    navController: NavController //
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
        containerColor = SurfaceSoft,
        bottomBar = { BottomPurchaseBar(product) }
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

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(insets)
                .statusBarsPadding()
                .padding(contentPadding)
        ) {
            // Cabecera con fondo
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp)
                    .background(
                        HeaderSoft,
                        RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
                    )
            )

            // Top bar
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                }
                Spacer(Modifier.weight(1f))
                Text(
                    "Details",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Spacer(Modifier.weight(1f))
                IconButton(onClick = { /* wishlist */ }) {
                    Icon(Icons.Outlined.FavoriteBorder, contentDescription = "Fav", tint = TextPrimary)
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
                    .size(260.dp)
            )

            // Sheet inferior
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                colors = CardDefaults.cardColors(containerColor = SheetBg),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Título + pills
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            p.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
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

                    // Rating + reviews
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RatingStars(rate = p.rating.rate)
                        Spacer(Modifier.width(8.dp))
                        Text("(${p.rating.count})", color = TextSecondary)
                    }

                    // Descripción
                    Text(
                        p.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        maxLines = 4,
                        overflow = TextOverflow.Ellipsis
                    )

                    // Info
                    InfoRow(label = "Category", value = p.category)
                    InfoRow(label = "Rating", value = "${"%.1f".format(p.rating.rate)} / 5")
                    Spacer(Modifier.height(4.dp))
                }
            }
        }
    }
}

@Composable
private fun BottomPurchaseBar(p: Product?) {
    Surface(color = Color.White, shadowElevation = 2.dp) {
        Row(
            Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (p != null) {
                Text(
                    text = NumberFormat.getCurrencyInstance(Locale.US).format(p.price),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(Modifier.weight(1f))
            }
            Button(
                onClick = { /* TODO: Comprar */ },
                modifier = Modifier.height(56.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Accent, contentColor = Color.White)
            ) { Text("Buy now") }
        }
    }
}

@Composable
private fun Pill(
    text: String,
    container: Color = PillBg,
    content: Color = TextPrimary
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(container)
            .border(1.dp, PillBorder, RoundedCornerShape(10.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) { Text(text, color = content, style = MaterialTheme.typography.labelLarge) }
}

@Composable
private fun RatingStars(rate: Double, max: Int = 5) {
    val full = floor(rate).toInt().coerceIn(0, max)
    val fractional = rate - full
    val half = if (fractional >= 0.25 && fractional < 0.75 && full < max) 1 else 0
    val empty = (max - full - half).coerceAtLeast(0)

    Row(verticalAlignment = Alignment.CenterVertically) {
        repeat(full) {
            Icon(Icons.Rounded.Star, contentDescription = null, tint = StarColor, modifier = Modifier.size(18.dp))
        }
        repeat(half) {
            Icon(Icons.Rounded.StarHalf, contentDescription = null, tint = StarColor, modifier = Modifier.size(18.dp))
        }
        repeat(empty) {
            Icon(Icons.Rounded.StarBorder, contentDescription = null, tint = StarColor, modifier = Modifier.size(18.dp))
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
        Text(label, modifier = Modifier.weight(1f), color = TextSecondary)
        Text(value, fontWeight = FontWeight.Medium, color = TextPrimary)
    }
}
