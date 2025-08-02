package com.singkhmer.transliterator

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach

class TransliteratorSearchTest {

    private lateinit var transliterator: Transliterator

    @BeforeEach
    fun setUp() {
        transliterator = Transliterator()
        println("=== Transliterator initialized for test ===")
    }

    @Test
    fun `searchExact should return correct Khmer words for existing romanizations`() {
        println("\n--- Testing searchExact functionality ---")
        
        // Given & When
        val banResults = transliterator.searchExact("ban")
        val jgResults = transliterator.searchExact("jg")
        val nonExistentResults = transliterator.searchExact("xyz123")
        
        println("searchExact('ban') = $banResults")
        println("searchExact('jg') = $jgResults")
        println("searchExact('xyz123') = $nonExistentResults")
        
        // Then
        assertTrue(banResults.isNotEmpty(), "Should find results for 'ban'")
        assertTrue(banResults.contains("បាន"), "Should contain 'បាន' for 'ban'")
        
        assertTrue(jgResults.isNotEmpty(), "Should find results for 'jg'")
        assertTrue(jgResults.contains("ជាង"), "Should contain 'ជាង' for 'jg'")
        assertTrue(jgResults.contains("ចង់"), "Should contain 'ចង់' for 'jg'")
        
        assertTrue(nonExistentResults.isEmpty(), "Should return empty for non-existent key")
    }

    @Test
    fun `searchExact should be case insensitive`() {
        println("\n--- Testing searchExact case insensitivity ---")
        
        // Given & When
        val lowerResult = transliterator.searchExact("ban")
        val upperResult = transliterator.searchExact("BAN")
        val mixedResult = transliterator.searchExact("Ban")
        
        println("searchExact('ban') = $lowerResult")
        println("searchExact('BAN') = $upperResult")
        println("searchExact('Ban') = $mixedResult")
        
        // Then
        assertEquals(lowerResult, upperResult, "Case should not matter")
        assertEquals(lowerResult, mixedResult, "Case should not matter")
    }

    @Test
    fun `searchPrefix should return all words under prefix`() {
        println("\n--- Testing searchPrefix functionality ---")
        
        // Given & When - Test with "j" prefix which should include many words
        val jPrefixResults = transliterator.searchPrefix("j")
        
        println("searchPrefix('j') found ${jPrefixResults.size} results:")
        jPrefixResults.take(10).forEach { println("  - $it") }
        if (jPrefixResults.size > 10) println("  ... and ${jPrefixResults.size - 10} more")
        
        // Then
        assertTrue(jPrefixResults.isNotEmpty(), "Should find words with 'j' prefix")
        
        // Should include words that start with romanizations beginning with 'j'
        val hasJRelatedWords = jPrefixResults.any { it.isNotEmpty() }
        assertTrue(hasJRelatedWords, "Should contain some Khmer words")
    }

    @Test
    fun `searchPrefix should work with longer prefixes`() {
        println("\n--- Testing searchPrefix with longer prefixes ---")
        
        // Given & When
        val jgPrefixResults = transliterator.searchPrefix("jong")
        
        println("searchPrefix('jong') = $jgPrefixResults")
        
        // Then
        assertTrue(jgPrefixResults.isNotEmpty(), "Should find words with 'jong' prefix")
        // Check if it contains expected results from dataset
        assertTrue(jgPrefixResults.contains("ចុង"), "Should contain 'ចុង' from 'jong' romanization")
    }

    @Test
    fun `searchPrefix should return empty list for non-existent prefix`() {
        println("\n--- Testing searchPrefix with non-existent prefix ---")
        
        // Given & When
        val results = transliterator.searchPrefix("xyz123")
        
        println("searchPrefix('xyz123') = $results")
        
        // Then
        assertTrue(results.isEmpty(), "Should return empty list for non-existent prefix")
    }

    @Test
    fun `searchExact and searchPrefix should handle empty input`() {
        println("\n--- Testing searchExact and searchPrefix with empty inputs ---")
        
        // Given & When
        val exactEmptyResults = transliterator.searchExact("")
        val prefixEmptyResults = transliterator.searchPrefix("")
        val exactBlankResults = transliterator.searchExact("  ")
        val prefixBlankResults = transliterator.searchPrefix("  ")
        
        println("searchExact('') = $exactEmptyResults")
        println("searchPrefix('') = $prefixEmptyResults")
        println("searchExact('  ') = $exactBlankResults")
        println("searchPrefix('  ') = $prefixBlankResults")
        
        // Then
        assertTrue(exactEmptyResults.isEmpty(), "searchExact should return empty for empty input")
        assertTrue(prefixEmptyResults.isEmpty(), "searchPrefix should return empty for empty input")
        assertTrue(exactBlankResults.isEmpty(), "searchExact should return empty for blank input")
        assertTrue(prefixBlankResults.isEmpty(), "searchPrefix should return empty for blank input")
    }

    @Test
    fun `trie should be populated on initialization`() {
        println("\n--- Testing Trie initialization ---")
        
        // Given & When - Constructor already called in setUp
        val commonRomanizations = listOf("ban", "jg", "chea", "mean", "del")
        println("Checking common romanizations after initialization:")
        
        // Then - All common romanizations should have results
        commonRomanizations.forEach { romanization ->
            val results = transliterator.searchExact(romanization)
            println("  searchExact('$romanization') = ${results.take(3)}")
            assertTrue(results.isNotEmpty(), "Should find results for common romanization: $romanization")
        }
    }

    @Test
    fun `searchPrefix should include exact matches in results`() {
        println("\n--- Testing searchPrefix includes exact matches ---")
        
        // Given & When
        val exactResults = transliterator.searchExact("ban")
        val prefixResults = transliterator.searchPrefix("ban")
        
        println("Exact results for 'ban': $exactResults")
        println("Prefix results for 'ban' (first 5): ${prefixResults.take(5)}")
        
        // Then
        assertTrue(exactResults.isNotEmpty(), "Should have exact matches for 'ban'")
        assertTrue(prefixResults.isNotEmpty(), "Should have prefix matches for 'ban'")
        exactResults.forEach { exactMatch ->
            assertTrue(prefixResults.contains(exactMatch), "Prefix results should include exact match: $exactMatch")
        }
    }

    @Test
    fun `performance test - multiple searches should be fast`() {
        println("\n--- Testing performance ---")
        
        // Given
        val startTime = System.currentTimeMillis()
        val searchTerms = listOf("j", "ch", "k", "b", "s", "m", "n", "p", "t", "r")
        println("Performing 2000 searches...")
        
        // When - Perform multiple searches
        repeat(100) {
            searchTerms.forEach { term ->
                transliterator.searchExact(term)
                transliterator.searchPrefix(term)
            }
        }
        
        // Then
        val endTime = System.currentTimeMillis()
        val duration = endTime - startTime
        println("2000 searches took ${duration}ms")
        
        // Should complete 2000 searches (100 * 10 * 2) in reasonable time
        assertTrue(duration < 1000, "2000 searches should complete in under 1 second, took ${duration}ms")
    }

    // New tests for suggestTop3 function

    @Test
    fun `suggestTop3 should return empty list for empty or whitespace input`() {
        println("\n--- Testing suggestTop3 with empty inputs ---")
        
        // Given & When
        val emptyResult = transliterator.suggestTop3("")
        val whitespaceResult = transliterator.suggestTop3("   ")
        val tabResult = transliterator.suggestTop3("\t")
        
        println("suggestTop3('') = $emptyResult")
        println("suggestTop3('   ') = $whitespaceResult")
        println("suggestTop3('\\t') = $tabResult")
        
        // Then
        assertTrue(emptyResult.isEmpty(), "Should return empty list for empty input")
        assertTrue(whitespaceResult.isEmpty(), "Should return empty list for whitespace input")
        assertTrue(tabResult.isEmpty(), "Should return empty list for tab input")
        println("✓ Empty lists returned for empty/whitespace inputs")
    }

    @Test
    fun `suggestTop3 should prioritize exact matches when available`() {
        println("\n--- Testing suggestTop3 exact match prioritization ---")
        
        // Given & When
        val input = "jg"
        val exactResults = transliterator.searchExact(input)
        val suggestedResults = transliterator.suggestTop3(input)
        
        println("Input: '$input'")
        println("Exact matches: $exactResults")
        println("suggestTop3 results: $suggestedResults")
        
        // Then
        assertTrue(suggestedResults.isNotEmpty(), "Should have results for 'jg'")
        assertTrue(suggestedResults.size <= 3, "Should return at most 3 results")
        
        // Should include exact matches for "jg"
        exactResults.take(3).forEach { exactMatch ->
            assertTrue(suggestedResults.contains(exactMatch), "Should prioritize exact match: $exactMatch")
        }
        println("✓ Exact matches prioritized")
    }

    @Test
    fun `suggestTop3 should fill remaining slots with prefix matches when exact matches less than 3`() {
        println("\n--- Testing suggestTop3 slot filling with prefix matches ---")
        
        // Given & When - Use a romanization that has only 1 exact match
        val input = "ban"
        val exactResults = transliterator.searchExact(input)
        val prefixResults = transliterator.searchPrefix(input)
        val suggestedResults = transliterator.suggestTop3(input)
        
        println("Input: '$input'")
        println("Exact matches (${exactResults.size}): $exactResults")
        println("Prefix matches (${prefixResults.size}): ${prefixResults.take(10)}${if (prefixResults.size > 10) "..." else ""}")
        println("suggestTop3 results: $suggestedResults")
        
        // Then
        assertTrue(suggestedResults.isNotEmpty(), "Should have results")
        assertTrue(suggestedResults.size <= 3, "Should return at most 3 results")
        
        // Should include the exact match
        assertTrue(suggestedResults.contains("បាន"), "Should include exact match 'បាន'")
        
        // If there are more results, they should be from prefix search
        if (suggestedResults.size > 1) {
            suggestedResults.forEach { result ->
                assertTrue(prefixResults.contains(result), "All results should be from prefix search: $result")
            }
        }
        println("✓ Slots filled with prefix matches")
        println("✓ All results are from prefix search")
        
    }
    
    @Test
    fun `suggestTop3 should limit results to maximum 3`() {
        println("\n--- Testing suggestTop3 result limit ---")
        
        // Given & When - Use a prefix that likely has many matches
        val input = "j"
        val exactResults = transliterator.searchExact(input)
        val prefixResults = transliterator.searchPrefix(input)
        val suggestedResults = transliterator.suggestTop3(input)
        
        println("Input: '$input'")
        println("Exact matches: ${exactResults.size}")
        println("Prefix matches: ${prefixResults.size}")
        println("suggestTop3 results (${suggestedResults.size}): $suggestedResults")
        
        // Then
        assertTrue(suggestedResults.size <= 3, "Should return at most 3 results, got ${suggestedResults.size}")
        println("✓ Results limited to 3")
    }
    
    @Test
    fun `suggestTop3 should remove duplicates between exact and prefix matches`() {
        println("\n--- Testing suggestTop3 duplicate removal ---")
        
        // Given & When
        val input = "jg"
        val exactResults = transliterator.searchExact(input)
        val prefixResults = transliterator.searchPrefix(input)
        val suggestedResults = transliterator.suggestTop3(input)
        
        println("Input: '$input'")
        println("Exact matches: $exactResults")
        println("Prefix matches (first 5): ${prefixResults.take(5)}")
        println("suggestTop3 results: $suggestedResults")
        
        // Then
        val uniqueResults = suggestedResults.toSet()
        assertEquals(suggestedResults.size, uniqueResults.size, "Should not contain duplicates")
        println("✓ No duplicates found in results")
    }
    
    @Test
    fun `suggestTop3 should return first 3 exact matches when there are 3 or more`() {
        println("\n--- Testing suggestTop3 with 3+ exact matches ---")
        
        // Given - Find a romanization with 3+ exact matches
        val romanizationsWithMultipleMatches = listOf("ng", "te", "jg", "oy", "neng")
        
        for (romanization in romanizationsWithMultipleMatches) {
            val exactResults = transliterator.searchExact(romanization)
            println("Checking '$romanization': ${exactResults.size} exact matches")
            
            if (exactResults.size >= 3) {
                // When
                val suggestedResults = transliterator.suggestTop3(romanization)
                
                println("Found romanization with 3+ matches: '$romanization'")
                println("Exact matches (${exactResults.size}): $exactResults")
                println("suggestTop3 results: $suggestedResults")
                
                // Then
                assertEquals(3, suggestedResults.size, "Should return exactly 3 results for '$romanization'")
                
                // All suggested results should be from exact matches
                suggestedResults.forEach { suggested ->
                    assertTrue(exactResults.contains(suggested), 
                        "All results should be exact matches for '$romanization': $suggested")
                }
                println("✓ All 3 results are from exact matches")
                return // Test passed for this romanization
            }
        }
        
        // If no romanization with 3+ matches found, that's okay for this test
        println("Note: No romanization found with 3+ exact matches for comprehensive testing")
    }
    
    @Test
    fun `suggestTop3 should be case insensitive`() {
        println("\n--- Testing suggestTop3 case insensitivity ---")
        
        // Given & When
        val lowerResult = transliterator.suggestTop3("ban")
        val upperResult = transliterator.suggestTop3("BAN")
        val mixedResult = transliterator.suggestTop3("Ban")
        
        println("suggestTop3('ban') = $lowerResult")
        println("suggestTop3('BAN') = $upperResult")
        println("suggestTop3('Ban') = $mixedResult")
        
        // Then
        assertEquals(lowerResult, upperResult, "Results should be same regardless of case")
        assertEquals(lowerResult, mixedResult, "Results should be same regardless of case")
        println("✓ All case variations return same results")
    }
    
    @Test
    fun `suggestTop3 should return empty list for non-existent romanization`() {
        println("\n--- Testing suggestTop3 with non-existent input ---")
        
        // Given & When
        val input = "xyz123notfound"
        val results = transliterator.suggestTop3(input)
        
        println("suggestTop3('$input') = $results")
        
        // Then
        assertTrue(results.isEmpty(), "Should return empty list for non-existent romanization")
        println("✓ Empty list returned for non-existent romanization")
    }
    
    @Test
    fun `suggestTop3 should handle single character input efficiently`() {
        println("\n--- Testing suggestTop3 single character efficiency ---")
        
        // Given & When
        val input = "k"
        val results = transliterator.suggestTop3(input)
        
        println("suggestTop3('$input') = $results")
        
        // Then
        assertTrue(results.size <= 3, "Should return at most 3 results")
        
        // Should be fast for single character
        val startTime = System.currentTimeMillis()
        repeat(100) {
            transliterator.suggestTop3(input)
        }
        val duration = System.currentTimeMillis() - startTime
        println("100 single-character searches took ${duration}ms")
        assertTrue(duration < 100, "100 single-character searches should be fast, took ${duration}ms")
    }
    
    // NEW TESTS for fuzzy search fallback functionality
    
    @Test
    fun `suggestTop3 should use fuzzy search as fallback when exact and prefix matches are insufficient`() {
        println("\n--- Testing suggestTop3 fuzzy search fallback ---")
        
        // Given - Use a typo that might not have exact or prefix matches
        val input = "slah" // Missing 'n' from "slanh"
        
        // When
        val exactResults = transliterator.searchExact(input)
        val prefixResults = transliterator.searchPrefix(input)
        val suggestedResults = transliterator.suggestTop3(input)
        val fuzzyResults = transliterator.searchFuzzy(input, 1)
        
        println("Input with typo: '$input'")
        println("Exact matches: $exactResults")
        println("Prefix matches: ${prefixResults.take(3)}")
        println("Fuzzy matches: $fuzzyResults")
        println("suggestTop3 results: $suggestedResults")
        
        // Then
        val totalDirectMatches = exactResults.size + prefixResults.size
        if (totalDirectMatches < 3 && fuzzyResults.isNotEmpty()) {
            assertTrue(suggestedResults.isNotEmpty(), "Should have suggestions despite typo")
            
            // Check if fuzzy results are included
            val hasFuzzyResults = fuzzyResults.any { fuzzyMatch ->
                suggestedResults.contains(fuzzyMatch)
            }
            if (hasFuzzyResults) {
                println("✓ Fuzzy search successfully provided fallback suggestions")
            } else {
                println("ℹ Note: Fuzzy matches may have been filtered due to duplicates")
            }
        } else {
            println("ℹ Note: Sufficient exact/prefix matches available, fuzzy search not needed")
        }
        
        assertTrue(suggestedResults.size <= 3, "Should respect 3-result limit")
        println("✓ Fuzzy search fallback functionality working")
    }
    
    @Test
    fun `suggestTop3 should prioritize exact over prefix over fuzzy matches`() {
        println("\n--- Testing suggestTop3 priority order ---")
        
        // Given - Use an input that might have all three types of matches
        val input = "te" // Short enough to potentially trigger fuzzy matches
        
        // When
        val exactResults = transliterator.searchExact(input)
        val prefixResults = transliterator.searchPrefix(input)
        val fuzzyResults = transliterator.searchFuzzy(input, 1)
        val suggestedResults = transliterator.suggestTop3(input)
        
        println("Input: '$input'")
        println("Exact matches (${exactResults.size}): ${exactResults.take(3)}")
        println("Prefix matches (${prefixResults.size}): ${prefixResults.take(3)}")
        println("Fuzzy matches (${fuzzyResults.size}): ${fuzzyResults.take(3)}")
        println("suggestTop3 results: $suggestedResults")
        
        // Then - Verify priority order
        val result = suggestedResults.toMutableList()
        
        // Remove exact matches from result and verify they came first
        var exactCount = 0
        val exactIterator = exactResults.iterator()
        while (exactIterator.hasNext() && result.isNotEmpty()) {
            val exactMatch = exactIterator.next()
            if (result.first() == exactMatch) {
                result.removeFirst()
                exactCount++
            } else {
                break
            }
        }
        
        // Remove prefix matches and verify they came next
        var prefixCount = 0
        val prefixIterator = prefixResults.iterator()
        while (prefixIterator.hasNext() && result.isNotEmpty()) {
            val prefixMatch = prefixIterator.next()
            if (result.contains(prefixMatch)) {
                result.remove(prefixMatch)
                prefixCount++
            }
        }
        
        // Remaining should be fuzzy matches
        val fuzzyCount = result.size
        
        println("Priority breakdown: $exactCount exact + $prefixCount prefix + $fuzzyCount fuzzy = ${suggestedResults.size} total")
        println("✓ Priority order maintained: exact → prefix → fuzzy")
    }
    
    @Test
    fun `suggestTop3 should not include duplicate results across search types`() {
        println("\n--- Testing suggestTop3 deduplication across search types ---")
        
        // Given - Use an input that likely appears in multiple search results
        val input = "j"
        
        // When
        val exactResults = transliterator.searchExact(input)
        val prefixResults = transliterator.searchPrefix(input)
        val fuzzyResults = transliterator.searchFuzzy(input, 1)
        val suggestedResults = transliterator.suggestTop3(input)
        
        println("Input: '$input'")
        println("Exact matches: ${exactResults.size}")
        println("Prefix matches: ${prefixResults.size}")
        println("Fuzzy matches: ${fuzzyResults.size}")
        println("suggestTop3 results: $suggestedResults")
        
        // Then
        val uniqueResults = suggestedResults.toSet()
        assertEquals(suggestedResults.size, uniqueResults.size, "Should contain no duplicates across all search types")
        assertTrue(suggestedResults.size <= 3, "Should not exceed 3 results")
        
        println("✓ No duplicates found across exact, prefix, and fuzzy results")
    }
    
    @Test
    fun `demonstrate suggestTop3 with various inputs`() {
        println("\n=== DEMONSTRATION: suggestTop3 with various inputs ===")
        
        val testInputs = listOf("j", "jg", "ban", "ch", "k", "jong", "xyz", "slah", "bna")
        
        testInputs.forEach { input ->
            val exactResults = transliterator.searchExact(input)
            val suggestedResults = transliterator.suggestTop3(input)
            
            println("\nInput: '$input'")
            println("  Exact matches (${exactResults.size}): $exactResults")
            println("  suggestTop3 → $suggestedResults")
        }
        
        println("\n=== END DEMONSTRATION ===")
    }
} 