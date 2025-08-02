package com.singkhmer.cli

import com.singkhmer.transliterator.Transliterator
import java.io.BufferedReader
import java.io.InputStreamReader

fun main(args: Array<String>) {
    val transliterator = Transliterator()
    println("🇰🇭 Sing Khmer Keyboard CLI Test Runner")
    println("Features: Exact search, Prefix search, Fuzzy search (typo correction)")
    println("")
    
    // Check if running in non-interactive mode or with arguments
    if (args.isNotEmpty()) {
        println("Testing with provided arguments:")
        args.forEach { input ->
            testInput(transliterator, input)
        }
        return
    }
    
    // Check if stdin is available
    if (System.`in`.available() == 0 && System.getProperty("java.class.path").contains("gradle")) {
        println("⚠️  Interactive mode may not work properly with Gradle. Running demo mode instead.")
        println("")
        runDemoMode(transliterator)
        return
    }
    
    println("Interactive Mode:")
    println("Type romanized Khmer text to get suggestions")
    println("Examples: 'jg', 'ban', 'slah' (typo), 'jong', etc.")
    println("Type 'exit' to quit")
    println("")
    
    // Use BufferedReader for more robust input handling
    val reader = BufferedReader(InputStreamReader(System.`in`))
    
    try {
        while (true) {
            print("🔤 Enter input: ")
            val input = reader.readLine()?.trim()
            
            if (input == null) {
                println("\n📝 Input stream ended. Exiting...")
                break
            }
            
            if (input.equals("exit", ignoreCase = true) || input.equals("quit", ignoreCase = true)) {
                println("👋 Goodbye!")
                break
            }
            
            if (input.isEmpty()) {
                println("   (empty input - try typing something like 'jg' or 'ban')")
                continue
            }
            
            testInput(transliterator, input)
            println("")
        }
    } catch (e: Exception) {
        println("❌ Error reading input: ${e.message}")
        println("Running demo mode instead:")
        println("")
        runDemoMode(transliterator)
    }
}

fun testInput(transliterator: Transliterator, input: String) {
    println("🔍 Input: '$input'")
    
    // Show all search types for debugging
    val exactResults = transliterator.searchExact(input)
    val prefixResults = transliterator.searchPrefix(input).take(5)
    val fuzzyResults = transliterator.searchFuzzy(input, 1).take(5)
    val suggestions = transliterator.suggestTop3(input)
    
    if (exactResults.isNotEmpty()) {
        println("   ✅ Exact matches: $exactResults")
    }
    if (prefixResults.isNotEmpty()) {
        println("   🔎 Prefix matches (top 5): $prefixResults")
    }
    if (fuzzyResults.isNotEmpty()) {
        println("   🔧 Fuzzy matches (top 5): $fuzzyResults")
    }
    
    if (suggestions.isEmpty()) {
        println("   ❌ No suggestions found")
    } else {
        println("   🎯 Top 3 suggestions:")
        suggestions.forEachIndexed { idx, word -> 
            println("      ${idx + 1}. $word")
        }
    }
}

fun runDemoMode(transliterator: Transliterator) {
    println("🎪 Demo Mode - Testing various inputs:")
    println("")
    
    val testCases = listOf(
        "jg" to "Exact match test",
        "ban" to "Multiple exact matches",
        "j" to "Prefix search test", 
        "jong" to "Mixed exact + prefix",
        "slah" to "Fuzzy search (missing 'n')",
        "bna" to "Fuzzy search (swapped chars)",
        "chxa" to "Fuzzy search (wrong char)",
        "xyz" to "No matches test",
        "te" to "All search types",
        "ch" to "Prefix only"
    )
    
    testCases.forEach { (input, description) ->
        println("📋 $description")
        testInput(transliterator, input)
        println("${"─".repeat(50)}")
    }
    
    println("")
    println("🎉 Demo completed!")
    println("💡 To test interactively, try: java -jar transliterator-cli.jar")
    println("💡 Or run with arguments: ./gradlew :transliterator-cli:run --args=\"jg ban slah\"")
}
