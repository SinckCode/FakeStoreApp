package com.example.fakestoreapp.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil3.compose.AsyncImage
import com.example.fakestoreapp.models.Product
import com.example.fakestoreapp.services.ProductService
import com.example.fakestoreapp.ui.theme.FakeStoreAppTheme
import com.example.fakestoreapp.ui.theme.ProductDetailScreenRoute
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/* ---------- Paleta rápida para buen contraste ---------- */
private val SurfaceSoft      = Color(0xFFF7F8FC)
private val TextPrimary      = Color(0xFF111827) // casi negro
private val TextSecondary    = Color(0xFF6B7280) // gris 500
private val TextMuted        = Color(0xFF9CA3AF) // gris 400
private val ChipBg           = Color(0xFFF2F3F5)
private val BannerBg         = Color(0xFFF3EEE8)
private val PriceBg          = Color(0xFFF4F5F7)
private val AccentIndicator  = Color(0xFFE3A37A) // naranja suave

@Composable
fun ProductsScreen(
    navController: NavController,
    contentPadding: PaddingValues
) {
    var products by remember { mutableStateOf(listOf<Product>()) }
    var loading by remember { mutableStateOf(true) }
    var selectedCategory by remember { mutableStateOf("All Product") }

    LaunchedEffect(true) {
        try {
            val retrofit = Retrofit.Builder()
                .baseUrl("https://fakestoreapi.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val service = retrofit.create(ProductService::class.java)
            val result = async(Dispatchers.IO) { service.getAllProducts() }
            products = result.await()
        } catch (e: Exception) {
            Log.e("HomeScreen", e.toString())
        } finally {
            loading = false
        }
    }

    Scaffold(
        containerColor = SurfaceSoft,
        bottomBar = { HomeBottomBar() }
    ) { insets ->
        if (loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(insets),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(insets)
                    .padding(contentPadding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item { HeaderSection() }
                item { SearchBar() }
                item { PromoBanner() }
                item {
                    CategoryChips(
                        categories = listOf("All Product", "Smartphone", "Wearable", "Camera"),
                        selected = selectedCategory,
                        onSelected = { selectedCategory = it }
                    )
                }
                item {
                    SectionTitle(
                        title = "New Arrival",
                        onSeeAll = { /* TODO: navigate to list */ }
                    )
                }
                item {
                    NewArrivalRow(
                        items = products.take(10),
                        onClick = { p -> navController.navigate(ProductDetailScreenRoute(p.id)) }
                    )
                }
            }
        }
    }
}

@Composable
private fun HeaderSection() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(0xFFEFEFEF)),
            contentAlignment = Alignment.Center
        ) {
            Text("R", fontWeight = FontWeight.Bold, color = TextPrimary)
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text("Hello,", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
            Text("Ryan AN", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = TextPrimary)
        }
        IconButton(onClick = { /* notifications */ }) {
            Icon(Icons.Default.Notifications, contentDescription = null, tint = TextPrimary)
        }
        IconButton(onClick = { /* menu */ }) {
            Icon(Icons.Default.Menu, contentDescription = null, tint = TextPrimary)
        }
    }
}

@Composable
private fun SearchBar() {
    OutlinedTextField(
        value = "",
        onValueChange = {},
        readOnly = true,
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted) },
        placeholder = { Text("Search for brand", color = TextMuted) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            disabledContainerColor = Color.White,
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
            disabledBorderColor = Color.Transparent,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            cursorColor = TextPrimary
        )
    )
}

@Composable
private fun PromoBanner() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = BannerBg),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    "Get Pixel 7 and\nPixel 7 Pro",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text("Full speed ahead.", color = TextSecondary, modifier = Modifier.padding(top = 4.dp))
                Button(
                    onClick = { /* CTA */ },
                    modifier = Modifier.padding(top = 12.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TextPrimary,
                        contentColor = Color.White
                    )
                ) { Text("Shop Now") }
            }
            // Imagen de producto (placeholder)
            Box(
                Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFEDEDED))
            )
        }
    }
}

@Composable
private fun CategoryChips(
    categories: List<String>,
    selected: String,
    onSelected: (String) -> Unit
) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(categories) { cat ->
            FilterChip(
                selected = selected == cat,
                onClick = { onSelected(cat) },
                label = { Text(cat) },
                shape = RoundedCornerShape(10.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = TextPrimary,
                    selectedLabelColor = Color.White,
                    containerColor = ChipBg,
                    labelColor = TextPrimary
                )
            )
        }
    }
}

@Composable
private fun SectionTitle(title: String, onSeeAll: () -> Unit) {
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = TextPrimary)
        Spacer(Modifier.weight(1f))
        TextButton(
            onClick = onSeeAll,
            colors = ButtonDefaults.textButtonColors(contentColor = TextSecondary)
        ) { Text("See All") }
    }
}

@Composable
private fun NewArrivalRow(
    items: List<Product>,
    onClick: (Product) -> Unit
) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        items(items) { p ->
            ProductMiniCard(product = p, onClick = { onClick(p) })
        }
    }
}

@Composable
private fun ProductMiniCard(
    product: Product,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.width(200.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(Modifier.padding(12.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(PriceBg),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = product.image,
                    contentDescription = product.title,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Spacer(Modifier.height(10.dp))
            Text(
                product.title,
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "$ ${"%.2f".format(product.price)}",
                    style = MaterialTheme.typography.titleSmall,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.weight(1f))
                IconButton(onClick = { /* wishlist */ }, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.FavoriteBorder, contentDescription = null, tint = TextMuted)
                }
            }
        }
    }
}

@Composable
private fun HomeBottomBar() {
    val itemColors = NavigationBarItemDefaults.colors(
        selectedIconColor = Color.White,
        selectedTextColor = Color(0xFF4B5563),
        unselectedIconColor = TextMuted,
        unselectedTextColor = TextMuted,
        indicatorColor = AccentIndicator
    )
    NavigationBar(containerColor = Color.White) {
        NavigationBarItem(
            selected = true, onClick = { /* Home */ },
            icon = { Icon(Icons.Default.Search, null) }, label = { Text("Home") },
            colors = itemColors
        )
        NavigationBarItem(
            selected = false, onClick = { /* Wishlist */ },
            icon = { Icon(Icons.Default.FavoriteBorder, null) }, label = { Text("Wishlist") },
            colors = itemColors
        )
        NavigationBarItem(
            selected = false, onClick = { /* Cart */ },
            icon = { Icon(Icons.Default.ShoppingCart, null) }, label = { Text("Cart") },
            colors = itemColors
        )
        NavigationBarItem(
            selected = false, onClick = { /* Profile */ },
            icon = { Icon(Icons.Default.AccountCircle, null) }, label = { Text("Profile") },
            colors = itemColors
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProductsScreenPreview() {
    FakeStoreAppTheme(darkTheme = false) { // fuerza tema claro en preview
        ProductsScreen(
            navController = rememberNavController(),
            contentPadding = PaddingValues(0.dp)
        )
    }
}
