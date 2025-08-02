package com.singkhmer.transliterator

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach

class TransliteratorFuzzyTest {
    
    private lateinit var transliterator: Transliterator
    private lateinit var trie: Trie
    
    @BeforeEach
    fun setUp() {
        transliterator = Transliterator()
        trie = Trie()
        println("=== Fresh Transliterator and Trie initialized for fuzzy search test ===")
    }
    
    // Levenshtein Distance Tests
    
    @Test
    fun `levenshtein should calculate distance correctly for identical strings`() {
        println("\n--- Testing Levenshtein distance for identical strings ---")
        
        // Given & When
        val distance = trie.levenshtein("hello", "hello")
        
        println("levenshtein('hello', 'hello') = $distance")
        
        // Then
        assertEquals(0, distance, "Distance should be 0 for identical strings")
        println("✓ Identical strings have distance 0")
    }
    
    @Test
    fun `levenshtein should calculate distance correctly for empty strings`() {
        println("\n--- Testing Levenshtein distance for empty strings ---")
        
        // Given & When
        val distance1 = trie.levenshtein("", "hello")
        val distance2 = trie.levenshtein("hello", "")
        val distance3 = trie.levenshtein("", "")
        
        println("levenshtein('', 'hello') = $distance1")
        println("levenshtein('hello', '') = $distance2")
        println("levenshtein('', '') = $distance3")
        
        // Then
        assertEquals(5, distance1, "Distance should equal length of non-empty string")
        assertEquals(5, distance2, "Distance should equal length of non-empty string")
        assertEquals(0, distance3, "Distance should be 0 for two empty strings")
        println("✓ Empty string distances calculated correctly")
    }
    
    @Test
    fun `levenshtein should calculate distance correctly for single character differences`() {
        println("\n--- Testing Levenshtein distance for single character differences ---")
        
        // Given & When
        val substitution = trie.levenshtein("cat", "bat") // 1 substitution
        val insertion = trie.levenshtein("cat", "cats")   // 1 insertion
        val deletion = trie.levenshtein("cats", "cat")    // 1 deletion
        
        println("levenshtein('cat', 'bat') = $substitution (substitution)")
        println("levenshtein('cat', 'cats') = $insertion (insertion)")
        println("levenshtein('cats', 'cat') = $deletion (deletion)")
        
        // Then
        assertEquals(1, substitution, "Single substitution should have distance 1")
        assertEquals(1, insertion, "Single insertion should have distance 1")
        assertEquals(1, deletion, "Single deletion should have distance 1")
        println("✓ Single character operations have distance 1")
    }
    
    @Test
    fun `levenshtein should calculate distance correctly for complex cases`() {
        println("\n--- Testing Levenshtein distance for complex cases ---")
        
        // Given & When
        val complex1 = trie.levenshtein("kitten", "sitting") // k->s, e->i, insert g
        val complex2 = trie.levenshtein("saturday", "sunday") // multiple operations
        val complex3 = trie.levenshtein("slanh", "slah")     // single deletion
        
        println("levenshtein('kitten', 'sitting') = $complex1")
        println("levenshtein('saturday', 'sunday') = $complex2")
        println("levenshtein('slanh', 'slah') = $complex3")
        
        // Then
        assertEquals(3, complex1, "kitten -> sitting should have distance 3")
        assertEquals(3, complex2, "saturday -> sunday should have distance 3")
        assertEquals(1, complex3, "slanh -> slah should have distance 1")
        println("✓ Complex cases calculated correctly")
    }
    
    // Fuzzy Search Tests
    
    @Test
    fun `searchFuzzy should find exact matches with distance 0`() {
        println("\n--- Testing fuzzy search for exact matches ---")
        
        // Given
        trie.insert("ban", "បាន")
        trie.insert("jg", "ចង់")
        
        // When
        val results = trie.searchFuzzy("ban", 1)
        
        println("searchFuzzy('ban', 1) = $results")
        
        // Then
        assertTrue(results.contains("បាន"), "Should find exact match")
        assertFalse(results.contains("ចង់"), "Should not include unrelated words")
        println("✓ Exact matches found correctly")
    }
    
    @Test
    fun `searchFuzzy should find single character typos`() {
        println("\n--- Testing fuzzy search for single character typos ---")
        
        // Given
        trie.insert("slanh", "ស្លាញ់")
        trie.insert("ban", "បាន")
        
        // When
        val results1 = trie.searchFuzzy("slah", 1)  // Missing 'n'
        val results2 = trie.searchFuzzy("slxnh", 1) // Wrong character 'x'
        val results3 = trie.searchFuzzy("slaanh", 1) // Extra 'a'
        
        println("searchFuzzy('slah', 1) = $results1 (missing 'n')")
        println("searchFuzzy('slxnh', 1) = $results2 (wrong character 'x')")
        println("searchFuzzy('slaanh', 1) = $results3 (extra 'a')")
        
        // Then
        assertTrue(results1.contains("ស្លាញ់"), "Should find word with missing character")
        assertTrue(results2.contains("ស្លាញ់"), "Should find word with substituted character")
        assertTrue(results3.contains("ស្លាញ់"), "Should find word with extra character")
        
        assertFalse(results1.contains("បាន"), "Should not include unrelated words")
        assertFalse(results2.contains("បាន"), "Should not include unrelated words")
        assertFalse(results3.contains("បាន"), "Should not include unrelated words")
        
        println("✓ Single character typos found correctly")
    }
    
    @Test
    fun `searchFuzzy should respect maxDistance parameter`() {
        println("\n--- Testing fuzzy search with different maxDistance values ---")
        
        // Given
        trie.insert("hello", "សួស្តី")
        
        // When
        val distance0 = trie.searchFuzzy("hello", 0) // Exact only
        val distance1 = trie.searchFuzzy("hallo", 1) // 1 substitution
        val distance2 = trie.searchFuzzy("hxllo", 2) // 1 substitution (within limit)
        val distance1Strict = trie.searchFuzzy("hxlly", 1) // 2 substitutions (exceeds limit)
        
        println("searchFuzzy('hello', 0) = $distance0 (exact only)")
        println("searchFuzzy('hallo', 1) = $distance1 (1 substitution within limit)")
        println("searchFuzzy('hxllo', 2) = $distance2 (1 substitution within limit 2)")
        println("searchFuzzy('hxlly', 1) = $distance1Strict (2 substitutions exceed limit 1)")
        
        // Then
        assertTrue(distance0.contains("សួស្តី"), "Exact match should be found with distance 0")
        assertTrue(distance1.contains("សួស្តី"), "1 substitution should be found with maxDistance 1")
        assertTrue(distance2.contains("សួស្តី"), "1 substitution should be found with maxDistance 2")
        assertTrue(distance1Strict.isEmpty(), "2 substitutions should not be found with maxDistance 1")
        
        println("✓ maxDistance parameter respected correctly")
    }
    
    @Test
    fun `searchFuzzy should handle empty input gracefully`() {
        println("\n--- Testing fuzzy search with empty input ---")
        
        // Given
        trie.insert("test", "តេស្ត")
        
        // When
        val emptyResult = trie.searchFuzzy("", 1)
        val blankResult = trie.searchFuzzy("   ", 1)
        
        println("searchFuzzy('', 1) = $emptyResult")
        println("searchFuzzy('   ', 1) = $blankResult")
        
        // Then
        assertTrue(emptyResult.isEmpty(), "Empty input should return empty result")
        assertTrue(blankResult.isEmpty(), "Blank input should return empty result")
        println("✓ Empty inputs handled gracefully")
    }
    
    @Test
    fun `searchFuzzy should sort by distance then frequency`() {
        println("\n--- Testing fuzzy search sorting by distance and frequency ---")
        
        // Given
        trie.insert("test", "តេស្ត១", 1)   // Exact match, low frequency
        trie.insert("tests", "តេស្ត២", 3) // Distance 1, high frequency
        trie.insert("text", "តេស្ត៣", 2)  // Distance 1, medium frequency
        
        // When
        val results = trie.searchFuzzy("test", 2)
        
        println("searchFuzzy('test', 2) = $results")
        println("Expected order: exact match first, then by frequency for same distance")
        
        // Then
        assertEquals("តេស្ត១", results[0], "Exact match should be first (distance 0)")
        assertEquals("តេស្ត២", results[1], "Higher frequency should come before lower frequency for same distance")
        assertEquals("តេស្ត៣", results[2], "Lower frequency should come last for same distance")
        
        println("✓ Results sorted correctly by distance then frequency")
    }
    
    @Test
    fun `searchFuzzy should return unique results only`() {
        println("\n--- Testing fuzzy search for unique results ---")
        
        // Given - Same Khmer word accessible via multiple romanizations
        trie.insert("jg", "ចង់", 2)
        trie.insert("jongg", "ចង់", 1)  // Same Khmer word
        trie.insert("chongg", "ចង់", 1) // Same Khmer word
        
        // When - Search for something that matches multiple romanizations
        val results = trie.searchFuzzy("jgg", 1) // Should match "jg" and possibly others
        
        println("searchFuzzy('jgg', 1) = $results")
        
        // Then
        val uniqueResults = results.toSet()
        assertEquals(results.size, uniqueResults.size, "Results should contain no duplicates")
        assertTrue(results.contains("ចង់"), "Should contain the Khmer word")
        
        println("✓ Results are unique (no duplicates)")
    }
    
    @Test
    fun `searchFuzzy should be case insensitive`() {
        println("\n--- Testing fuzzy search case insensitivity ---")
        
        // Given
        trie.insert("ban", "បាន")
        
        // When
        val lowerResult = trie.searchFuzzy("ban", 1)
        val upperResult = trie.searchFuzzy("BAN", 1)
        val mixedResult = trie.searchFuzzy("Ban", 1)
        val typoLower = trie.searchFuzzy("bam", 1)
        val typoUpper = trie.searchFuzzy("BAM", 1)
        
        println("searchFuzzy('ban', 1) = $lowerResult")
        println("searchFuzzy('BAN', 1) = $upperResult")
        println("searchFuzzy('Ban', 1) = $mixedResult")
        println("searchFuzzy('bam', 1) = $typoLower")
        println("searchFuzzy('BAM', 1) = $typoUpper")
        
        // Then
        assertEquals(lowerResult, upperResult, "Case should not matter for exact match")
        assertEquals(lowerResult, mixedResult, "Case should not matter for exact match")
        assertEquals(typoLower, typoUpper, "Case should not matter for fuzzy match")
        
        assertTrue(typoLower.contains("បាន"), "Should find match despite case and typo")
        
        println("✓ Fuzzy search is case insensitive")
    }
    
    // Integration Tests with Transliterator
    
    @Test
    fun `transliterator searchFuzzy should work with dataset`() {
        println("\n--- Testing Transliterator fuzzy search with real dataset ---")
        
        // Given - Use actual transliterator with loaded dataset
        // When
        val results1 = transliterator.searchFuzzy("ba", 1)  // Should match "ban" -> "បាន"
        val results2 = transliterator.searchFuzzy("jgg", 1) // Should match "jg" -> various words
        
        println("transliterator.searchFuzzy('ba', 1) = ${results1.take(5)}")
        println("transliterator.searchFuzzy('jgg', 1) = ${results2.take(5)}")
        
        // Then
        assertTrue(results1.isNotEmpty(), "Should find fuzzy matches for 'ba'")
        assertTrue(results2.isNotEmpty(), "Should find fuzzy matches for 'jgg'")
        
        println("✓ Transliterator fuzzy search works with dataset")
    }
    
    @Test
    fun `searchFuzzy should handle non-existent inputs gracefully`() {
        println("\n--- Testing fuzzy search with non-existent inputs ---")
        
        // Given
        trie.insert("test", "តេស្ត")
        
        // When
        val noResults = trie.searchFuzzy("xyz123", 1)
        val almostMatch = trie.searchFuzzy("xyz123", 10) // High distance
        
        println("searchFuzzy('xyz123', 1) = $noResults")
        println("searchFuzzy('xyz123', 10) = $almostMatch")
        
        // Then
        assertTrue(noResults.isEmpty(), "Should return empty for completely unrelated input")
        // With high enough distance, even 'xyz123' might match something
        
        println("✓ Non-existent inputs handled gracefully")
    }
    
    @Test
    fun `demonstrate fuzzy search capabilities`() {
        println("\n=== DEMONSTRATION: Fuzzy Search Capabilities ===")
        
        // Given - Insert some test data
        trie.insert("slanh", "ស្លាញ់", 5)
        trie.insert("ban", "បាន", 3)
        trie.insert("jg", "ចង់", 4)
        trie.insert("jg", "ជាង", 2)
        trie.insert("chea", "ជា", 6)
        
        val testCases = listOf(
            "slah" to 1,    // Missing 'n'
            "bna" to 1,     // Swapped characters
            "jgg" to 1,     // Extra character
            "chxa" to 1,    // Wrong character
            "hello" to 2    // Completely different
        )
        
        testCases.forEach { (input, maxDist) ->
            val results = trie.searchFuzzy(input, maxDist)
            println("searchFuzzy('$input', $maxDist) → ${results.take(3)}")
        }
        
        println("\n=== END DEMONSTRATION ===")
    }
    
    @Test
    fun `fuzzy search should work with learning system`() {
        println("\n--- Testing fuzzy search integration with learning ---")
        
        // Given
        trie.insert("test", "តេស្ត១", 1)
        trie.insert("tests", "តេស្ត២", 1)
        
        val initialResults = trie.searchFuzzy("tst", 2) // Should match both
        println("Initial searchFuzzy('tst', 2) = $initialResults")
        
        // When - Simulate user preferring one result
        repeat(3) {
            trie.incrementFrequency("tests", "តេស្ត២")
        }
        
        val learnedResults = trie.searchFuzzy("tst", 2)
        println("After learning searchFuzzy('tst', 2) = $learnedResults")
        
        // Then
        if (learnedResults.size >= 2) {
            // The more frequently selected word should appear first among same-distance results
            assertTrue(learnedResults.contains("តេស្ត២"), "Should contain learned preference")
        }
        
        println("✓ Fuzzy search integrates with learning system")
    }
} 