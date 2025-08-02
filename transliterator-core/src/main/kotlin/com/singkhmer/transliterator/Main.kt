package com.singkhmer.transliterator

fun main() {
    val transliterator = Transliterator()

    // search for "jg"
    val results = transliterator.suggestTop3("jg")
    println("jg -> ${results}")

    // search for "j"
    val results2 = transliterator.suggestTop3("jos")
    println("jos -> ${results2}")

    // search for "nh"
    val results3 = transliterator.suggestTop3("nh")
    println("nh -> ${results3}")

    // search for "ng"
    val results4 = transliterator.suggestTop3("ng")
    println("ng -> ${results4}")
    
    
} 