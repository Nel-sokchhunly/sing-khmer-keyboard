package com.singkhmer.transliterator

class Transliterator {
    private val trie = Trie()
    
    init {
        // Load dataset and populate trie on initialization
        val dataset = loadDataset()
        dataset.forEach { (romanization, khmerWords) ->
            khmerWords.forEach { khmerWord ->
                trie.insert(romanization, khmerWord) // Uses default freq = 1
            }
        }
    }
    
    fun hello(): String {
        return "Hello from Sing Khmer Transliteration Engine"
    }
    
    /**
     * Search for exact matches of the input romanization
     * @param input The romanized input to search for
     * @return List of Khmer words for exact match, or empty list if not found
     */
    fun searchExact(input: String) = trie.searchExact(input)
    
    /**
     * Search for all Khmer words that start with the given romanization prefix
     * @param input The romanization prefix to search for
     * @return List of all Khmer words under this prefix
     */
    fun searchPrefix(input: String) = trie.searchPrefix(input)
    
    /**
     * Search for Khmer words using fuzzy matching with Levenshtein distance
     * Useful for handling typos and approximate matches
     * @param input The romanized input to search for
     * @param maxDistance Maximum edit distance allowed (default 1)
     * @return List of unique Khmer words sorted by distance (ascending) then frequency (descending)
     */
    fun searchFuzzy(input: String, maxDistance: Int = 1) = trie.searchFuzzy(input, maxDistance)
    
    /**
     * Get top 3 Khmer word suggestions for the input
     * Prioritizes exact matches first, then prefix matches, then fuzzy matches as fallback
     * @param input The romanized input to get suggestions for
     * @return List of up to 3 Khmer words, empty list for empty/whitespace input
     */
    fun suggestTop3(input: String): List<String> {
        // Handle empty or whitespace input
        if (input.isBlank()) {
            return emptyList()
        }
        
        // Get exact matches first (highest priority)
        val exactResults = trie.searchExact(input)
        
        // If we have 3 or more exact matches, return first 3
        if (exactResults.size >= 3) {
            return exactResults.take(3)
        }
        
        // Combine results: exact matches first, then prefix matches, then fuzzy matches
        val result = mutableListOf<String>()
        
        // Add exact matches first
        result.addAll(exactResults)
        
        // If we have fewer than 3 exact matches, fill remaining slots with prefix matches
        if (result.size < 3) {
            val prefixResults = trie.searchPrefix(input)
            
            // Add prefix matches that are not already in exact matches
            for (prefixMatch in prefixResults) {
                if (!result.contains(prefixMatch) && result.size < 3) {
                    result.add(prefixMatch)
                }
            }
        }
        
        // If we still have fewer than 3 results, use fuzzy search as fallback
        if (result.size < 3) {
            val fuzzyResults = trie.searchFuzzy(input, 1) // Default distance of 1
            
            // Add fuzzy matches that are not already in the result list
            for (fuzzyMatch in fuzzyResults) {
                if (!result.contains(fuzzyMatch) && result.size < 3) {
                    result.add(fuzzyMatch)
                }
            }
        }
        
        // Return up to 3 results (deduplicated by the contains() checks above)
        return result.take(3)
    }
    
    /**
     * Increment the frequency of a specific Khmer word for a given romanization
     * This simulates a user selecting a suggestion and helps the system learn user preferences
     * @param roman The romanized input that was typed
     * @param khmer The Khmer word that was selected by the user
     */
    fun incrementFrequency(roman: String, khmer: String) {
        trie.incrementFrequency(roman, khmer)
    }
    
    fun loadDataset(): Map<String, MutableList<String>> {
        val datasetMap = mutableMapOf<String, MutableList<String>>()
        
        try {
            // Read the dataset file from resources
            val inputStream = this::class.java.classLoader.getResourceAsStream("dataset.txt")
                ?: throw IllegalStateException("dataset.txt not found in resources")
            
            inputStream.bufferedReader().use { reader ->
                reader.lineSequence().forEach { line ->
                    // Skip empty lines
                    if (line.isBlank()) return@forEach
                    
                    // Parse line format: "Khmer word: romanization1, romanization2"
                    val parts = line.split(":", limit = 2)
                    if (parts.size == 2) {
                        val khmerWord = parts[0].trim()
                        val romanizationsStr = parts[1].trim()
                        
                        // Skip if either part is empty
                        if (khmerWord.isEmpty() || romanizationsStr.isEmpty()) return@forEach
                        
                        // Split romanizations by comma and process each one
                        val romanizations = romanizationsStr.split(",")
                        for (romanization in romanizations) {
                            val cleanRomanization = romanization.trim().lowercase()
                            
                            // Skip empty romanizations
                            if (cleanRomanization.isEmpty()) continue
                            
                            // Add to map: romanization -> list of Khmer words
                            datasetMap.getOrPut(cleanRomanization) { mutableListOf() }.add(khmerWord)
                        }
                    }
                    // Silently skip malformed lines (lines without ":" or with wrong format)
                }
            }
        } catch (e: Exception) {
            // Handle any IO or other exceptions gracefully
            println("Error loading dataset: ${e.message}")
            return emptyMap()
        }
        
        return datasetMap
    }
}
