package com.example.fakestoreapp

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

fun main(){
    //hilo
    println("Inicio en hilo: ${Thread.currentThread().name}")
    cWithContext()
    println("Fin en hilo: ${Thread.currentThread().name}")
    //Una corrutina son tareas que se ejecutan de manera simultanea
    //Actividades asincronas
}

//Dispatchers
//Edificio 1. App - UI
//Edificio 2.

fun cAsync(){
    runBlocking {
        //async nos regresa un resultado
        val result = async(Dispatchers.IO) {
            println("Consultando datos de una API")
            delay(5000)
            println("Resltados traidos")
            "Datos"
        }

        println("El resultado es ${result.await()}")
    }
}

fun cWithContext(){
    runBlocking {
        val result = withContext(Dispatchers.IO){
            println("WithContext en hilo: ${Thread.currentThread().name}")
            println("Consultando info de API")
            delay(2000)
            println("Datos Obtenidos")
            "{ age: 17 }"
        }
        println("El resultado es: $result")
    }
}

fun MyGlobal(){
    GlobalScope.launch {
        SaludoAsincrono()
    }
}

// suspend es como async en lenguajes java
suspend fun SaludoAsincrono(){
    println("Hola")
    delay(2000)
    println("Hola 2: ahora es personal")
}

