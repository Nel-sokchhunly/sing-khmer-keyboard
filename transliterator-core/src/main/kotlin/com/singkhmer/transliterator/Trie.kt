package com.singkhmer.transliterator

/**
 * A node in the Trie data structure with frequency tracking
 */
class TrieNode {
    val children: MutableMap<Char, TrieNode> = mutableMapOf()
    val words: MutableMap<String, Int> = mutableMapOf() // Khmer word -> frequency count
}

/**
 * Trie data structure for storing mappings from romanized input to Khmer words with frequency tracking
 */
class Trie {
    private val root = TrieNode()
    
    /**
     * Insert a romanization -> Khmer word mapping into the trie with frequency
     * @param roman The romanized input (will be stored in lowercase)
     * @param khmer The corresponding Khmer word
     * @param freq The frequency count to add (default 1)
     */
    fun insert(roman: String, khmer: String, freq: Int = 1) {
        // Ignore empty inputs
        if (roman.isBlank() || khmer.isBlank()) {
            return
        }
        
        val lowerRoman = roman.lowercase()
        var currentNode = root
        
        // Traverse/create path for each character
        for (char in lowerRoman) {
            currentNode = currentNode.children.getOrPut(char) { TrieNode() }
        }
        
        // Add or update the Khmer word with frequency
        currentNode.words[khmer] = currentNode.words.getOrDefault(khmer, 0) + freq
    }
    
    /**
     * Search for exact match of the input, returning results sorted by frequency
     * @param input The romanized input to search for
     * @return List of Khmer words for exact match sorted by frequency (highest first), or empty list if not found
     */
    fun searchExact(input: String): List<String> {
        // Ignore empty inputs
        if (input.isBlank()) {
            return emptyList()
        }
        
        val lowerInput = input.lowercase()
        var currentNode = root
        
        // Traverse the trie following the input characters
        for (char in lowerInput) {
            currentNode = currentNode.children[char] ?: return emptyList()
        }
        
        // Return words sorted by frequency (highest first)
        return currentNode.words.toList()
            .sortedByDescending { it.second }
            .map { it.first }
    }
    
    /**
     * Search for all words that start with the given prefix, returning results sorted by frequency
     * @param prefix The prefix to search for
     * @return List of all Khmer words under this prefix sorted by frequency (highest first)
     */
    fun searchPrefix(prefix: String): List<String> {
        // Ignore empty inputs
        if (prefix.isBlank()) {
            return emptyList()
        }
        
        val lowerPrefix = prefix.lowercase()
        var currentNode = root
        
        // Navigate to the prefix node
        for (char in lowerPrefix) {
            currentNode = currentNode.children[char] ?: return emptyList()
        }
        
        // Collect all words from this node and its descendants with frequencies
        val wordFrequencies = mutableMapOf<String, Int>()
        collectAllWordsWithFrequency(currentNode, wordFrequencies)
        
        // Return words sorted by frequency (highest first)
        return wordFrequencies.toList()
            .sortedByDescending { it.second }
            .map { it.first }
    }
    
    /**
     * Search for Khmer words using fuzzy matching with Levenshtein distance
     * @param input The romanized input to search for
     * @param maxDistance Maximum edit distance allowed (default 1)
     * @return List of unique Khmer words sorted by distance (ascending) then frequency (descending)
     */
    fun searchFuzzy(input: String, maxDistance: Int = 1): List<String> {
        // Ignore empty inputs
        if (input.isBlank()) {
            return emptyList()
        }
        
        val lowerInput = input.lowercase()
        
        // Get all romanization keys from the trie
        val allKeys = getAllRomanizationKeys()
        
        // Find matches within maxDistance
        data class FuzzyMatch(val distance: Int, val khmerWord: String, val frequency: Int)
        val matches = mutableListOf<FuzzyMatch>()
        
        for (key in allKeys) {
            val distance = levenshtein(lowerInput, key)
            if (distance <= maxDistance) {
                // Get all Khmer words for this romanization key
                val khmerWords = searchExact(key)
                for (khmerWord in khmerWords) {
                    // Get frequency for this specific word
                    val frequency = getWordFrequency(key, khmerWord)
                    matches.add(FuzzyMatch(distance, khmerWord, frequency))
                }
            }
        }
        
        // Sort by distance ascending, then by frequency descending
        val sortedMatches = matches.sortedWith(compareBy<FuzzyMatch> { it.distance }.thenByDescending { it.frequency })
        
        // Return unique Khmer words only (remove duplicates)
        return sortedMatches.map { it.khmerWord }.distinct()
    }
    
    /**
     * Increment the frequency of a specific Khmer word for a given romanization
     * This simulates a user selecting a suggestion
     * @param roman The romanized input
     * @param khmer The Khmer word that was selected
     */
    fun incrementFrequency(roman: String, khmer: String) {
        // Ignore empty inputs
        if (roman.isBlank() || khmer.isBlank()) {
            return
        }
        
        val lowerRoman = roman.lowercase()
        var currentNode = root
        
        // Navigate to the node for this romanization
        for (char in lowerRoman) {
            currentNode = currentNode.children[char] ?: return // Node doesn't exist
        }
        
        // If the Khmer word exists at this node, increment its frequency
        if (currentNode.words.containsKey(khmer)) {
            currentNode.words[khmer] = currentNode.words[khmer]!! + 1
        }
        // If word doesn't exist, do nothing (as per requirements)
    }
    
    /**
     * Compute Levenshtein distance between two strings
     * @param a First string
     * @param b Second string
     * @return Edit distance between the strings
     */
    fun levenshtein(a: String, b: String): Int {
        if (a.isEmpty()) return b.length
        if (b.isEmpty()) return a.length
        
        val dp = Array(a.length + 1) { IntArray(b.length + 1) }
        
        // Initialize first row and column
        for (i in 0..a.length) dp[i][0] = i
        for (j in 0..b.length) dp[0][j] = j
        
        // Fill the DP table
        for (i in 1..a.length) {
            for (j in 1..b.length) {
                val cost = if (a[i - 1] == b[j - 1]) 0 else 1
                dp[i][j] = minOf(
                    dp[i - 1][j] + 1,      // Deletion
                    dp[i][j - 1] + 1,      // Insertion
                    dp[i - 1][j - 1] + cost // Substitution
                )
            }
        }
        
        return dp[a.length][b.length]
    }
    
    /**
     * Get all romanization keys stored in the trie
     * @return List of all romanization strings
     */
    private fun getAllRomanizationKeys(): List<String> {
        val keys = mutableListOf<String>()
        collectAllKeys(root, "", keys)
        return keys
    }
    
    /**
     * Helper function to recursively collect all romanization keys
     * @param node Current trie node
     * @param currentKey Current key being built
     * @param keys List to collect keys into
     */
    private fun collectAllKeys(node: TrieNode, currentKey: String, keys: MutableList<String>) {
        // If this node has words, it's a complete romanization key
        if (node.words.isNotEmpty()) {
            keys.add(currentKey)
        }
        
        // Recursively collect from children
        for ((char, childNode) in node.children) {
            collectAllKeys(childNode, currentKey + char, keys)
        }
    }
    
    /**
     * Get the frequency of a specific Khmer word for a given romanization
     * @param roman The romanization key
     * @param khmer The Khmer word
     * @return Frequency count, or 0 if not found
     */
    private fun getWordFrequency(roman: String, khmer: String): Int {
        val lowerRoman = roman.lowercase()
        var currentNode = root
        
        // Navigate to the romanization node
        for (char in lowerRoman) {
            currentNode = currentNode.children[char] ?: return 0
        }
        
        // Return frequency for the Khmer word
        return currentNode.words[khmer] ?: 0
    }
    
    /**
     * Helper function to recursively collect all words with frequencies from a node and its descendants
     * @param node The starting node
     * @param wordFrequencies The map to collect word frequencies into
     */
    private fun collectAllWordsWithFrequency(node: TrieNode, wordFrequencies: MutableMap<String, Int>) {
        // Add words from current node, combining frequencies if word appears multiple times
        for ((word, frequency) in node.words) {
            wordFrequencies[word] = wordFrequencies.getOrDefault(word, 0) + frequency
        }
        
        // Recursively collect from children
        for (childNode in node.children.values) {
            collectAllWordsWithFrequency(childNode, wordFrequencies)
        }
    }
}
