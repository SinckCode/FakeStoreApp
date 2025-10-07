package com.example.fakestoreapp

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


fun main(){
    val a = 1
    val b = 2


    operacionDeNumeros(a, b, operacion = {a,b -> a+b})

}

//FUNCION QUE RECIBA DOS NUMEROS Y QUE REGRESE DOS NUMEROS
fun operacionDeNumeros(a:Int,b:Int, operacion : (Int , Int) -> Int ){
    println("El numero A vale: $a")
    println("El numero B vale: $b")

    val result = operacion(a,b)

    println(result)
}

//Callback
//Hell Callbacks

fun suma(a : Int, b : Int) : Int {
    return a + b
}

fun resta(a : Int, b : Int) : Int {
    return a - b
}

@Composable
fun BookCard(onClik : () -> Unit) {

    Column(
        modifier = Modifier
            .clickable(){
                
            }
    ) {

    }


}

@Composable
fun HomeScreen(){
    Column {
        BookCard(
            onClik = {
                println("Navegando a otro lugar")
            }
        )
    }
}