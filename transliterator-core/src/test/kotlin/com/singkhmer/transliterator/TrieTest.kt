package com.singkhmer.transliterator

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach

class TrieTest {
    
    private lateinit var trie: Trie
    
    @BeforeEach
    fun setUp() {
        trie = Trie()
    }
    
    @Test
    fun `insert and searchExact should work for basic case`() {
        // Given & When
        trie.insert("ban", "បាន")
        
        // Then
        val result = trie.searchExact("ban")
        assertEquals(listOf("បាន"), result)
    }
    
    @Test
    fun `insert should handle case insensitivity`() {
        // Given & When
        trie.insert("BAN", "បាន")
        
        // Then
        val result1 = trie.searchExact("ban")
        val result2 = trie.searchExact("BAN")
        val result3 = trie.searchExact("Ban")
        
        assertEquals(listOf("បាន"), result1)
        assertEquals(listOf("បាន"), result2)
        assertEquals(listOf("បាន"), result3)
    }
    
    @Test
    fun `insert should allow multiple Khmer words for same romanization`() {
        // Given & When
        trie.insert("jg", "ជាង")
        trie.insert("jg", "ចង់")
        
        // Then
        val result = trie.searchExact("jg")
        assertEquals(2, result.size)
        assertTrue(result.contains("ជាង"))
        assertTrue(result.contains("ចង់"))
    }
    
    @Test
    fun `insert should handle frequency accumulation for duplicate words`() {
        // Given & When
        trie.insert("ban", "បាន", 1)
        trie.insert("ban", "បាន", 2) // Should add to existing frequency
        
        // Then
        val result = trie.searchExact("ban")
        assertEquals(listOf("បាន"), result) // Still only one word but with freq = 3
    }
    
    @Test
    fun `searchExact should return empty list for non-existent key`() {
        // Given & When
        trie.insert("ban", "បាន")
        
        // Then
        val result = trie.searchExact("nonexistent")
        assertTrue(result.isEmpty())
    }
    
    @Test
    fun `searchExact should return results sorted by frequency`() {
        // Given & When
        trie.insert("test", "ទេស្ត១", 1) // Lower frequency
        trie.insert("test", "ទេស្ត២", 3) // Higher frequency
        trie.insert("test", "ទេស្ត៣", 2) // Medium frequency
        
        // Then
        val result = trie.searchExact("test")
        assertEquals(listOf("ទេស្ត២", "ទេស្ត៣", "ទេស្ត១"), result) // Sorted by frequency desc
    }
    
    @Test
    fun `searchPrefix should return all words under prefix sorted by frequency`() {
        // Given & When
        trie.insert("ba", "បា", 1)
        trie.insert("ban", "បាន", 3) // Highest frequency
        trie.insert("bang", "បាំង", 2)
        trie.insert("bat", "បាត់", 1)
        trie.insert("ca", "កា", 1) // Different prefix
        
        // Then
        val result = trie.searchPrefix("ba")
        assertEquals(4, result.size)
        
        // Should be sorted by frequency (highest first)
        assertEquals("បាន", result[0]) // freq = 3
        assertEquals("បាំង", result[1]) // freq = 2
        // Last two can be in any order since they have same frequency
        assertTrue(result.contains("បា"))
        assertTrue(result.contains("បាត់"))
        assertFalse(result.contains("កា"))
    }
    
    @Test
    fun `searchPrefix should work with single character prefix`() {
        // Given & When
        trie.insert("a", "អា", 2)
        trie.insert("ab", "អាប់", 3)
        trie.insert("abc", "អាប់ស៊ី", 1)
        
        // Then
        val result = trie.searchPrefix("a")
        assertEquals(3, result.size)
        assertEquals("អាប់", result[0]) // Highest frequency
        assertEquals("អា", result[1])   // Second highest
        assertEquals("អាប់ស៊ី", result[2]) // Lowest frequency
    }
    
    @Test
    fun `searchPrefix should return empty list for non-existent prefix`() {
        // Given & When
        trie.insert("hello", "ហេឡូ")
        
        // Then
        val result = trie.searchPrefix("xyz")
        assertTrue(result.isEmpty())
    }
    
    @Test
    fun `insert should ignore empty inputs`() {
        // Given & When
        trie.insert("", "បាន")
        trie.insert("ban", "")
        trie.insert("  ", "បាន")
        trie.insert("ban", "  ")
        
        // Then
        val result1 = trie.searchExact("")
        val result2 = trie.searchExact("ban")
        val result3 = trie.searchExact("  ")
        
        assertTrue(result1.isEmpty())
        assertTrue(result2.isEmpty())
        assertTrue(result3.isEmpty())
    }
    
    @Test
    fun `searchExact should ignore empty inputs`() {
        // Given & When
        trie.insert("ban", "បាន")
        
        // Then
        val result1 = trie.searchExact("")
        val result2 = trie.searchExact("  ")
        
        assertTrue(result1.isEmpty())
        assertTrue(result2.isEmpty())
    }
    
    @Test
    fun `searchPrefix should ignore empty inputs`() {
        // Given & When
        trie.insert("ban", "បាន")
        
        // Then
        val result1 = trie.searchPrefix("")
        val result2 = trie.searchPrefix("  ")
        
        assertTrue(result1.isEmpty())
        assertTrue(result2.isEmpty())
    }
    
    @Test
    fun `incrementFrequency should increase frequency and affect search order`() {
        // Given & When
        trie.insert("jg", "ជាង", 1)
        trie.insert("jg", "ចង់", 1)
        
        val initialResult = trie.searchExact("jg")
        
        // Increment frequency of one word
        trie.incrementFrequency("jg", "ចង់")
        
        val afterIncrementResult = trie.searchExact("jg")
        
        // Then
        assertEquals(2, initialResult.size)
        assertEquals(2, afterIncrementResult.size)
        
        // "ចង់" should now be first due to higher frequency
        assertEquals("ចង់", afterIncrementResult[0])
        assertTrue(afterIncrementResult.contains("ជាង"))
    }
    
    @Test
    fun `incrementFrequency should ignore non-existent romanization`() {
        // Given & When
        trie.insert("ban", "បាន")
        
        // Try to increment frequency for non-existent romanization
        trie.incrementFrequency("nonexistent", "សូម")
        
        // Then
        val result = trie.searchExact("nonexistent")
        assertTrue(result.isEmpty()) // Should still be empty
    }
    
    @Test
    fun `incrementFrequency should ignore non-existent Khmer word`() {
        // Given & When
        trie.insert("ban", "បាន")
        
        val beforeIncrement = trie.searchExact("ban")
        
        // Try to increment frequency for non-existent Khmer word
        trie.incrementFrequency("ban", "សូម") // This word doesn't exist for "ban"
        
        val afterIncrement = trie.searchExact("ban")
        
        // Then
        assertEquals(beforeIncrement, afterIncrement) // Should be unchanged
    }
    
    @Test
    fun `incrementFrequency should handle empty inputs gracefully`() {
        // Given & When
        trie.insert("ban", "បាន")
        
        // Try to increment with empty inputs
        trie.incrementFrequency("", "បាន")
        trie.incrementFrequency("ban", "")
        trie.incrementFrequency("", "")
        
        // Then - Should not crash and original data should be intact
        val result = trie.searchExact("ban")
        assertEquals(listOf("បាន"), result)
    }
    
    @Test
    fun `complex scenario with frequency-based sorting`() {
        // Given & When - Using actual data from dataset with custom frequencies
        trie.insert("jg", "ជាង", 1)  // Line 100
        trie.insert("jg", "ចង់", 1)   // Line 107
        trie.insert("jongg", "ចង់", 1) // Line 107
        trie.insert("chongg", "ចង់", 1) // Line 107
        trie.insert("jeang", "ជាង", 1) // Line 100
        trie.insert("cheang", "ជាង", 1) // Line 100
        
        // Simulate user preferring "ចង់" for "jg"
        trie.incrementFrequency("jg", "ចង់")
        trie.incrementFrequency("jg", "ចង់")
        
        // Then
        val jgResults = trie.searchExact("jg")
        assertEquals(2, jgResults.size)
        assertEquals("ចង់", jgResults[0]) // Should be first due to higher frequency
        assertEquals("ជាង", jgResults[1]) // Should be second
        
        val jonggResults = trie.searchExact("jongg")
        assertEquals(listOf("ចង់"), jonggResults)
        
        val prefixResults = trie.searchPrefix("j")
        assertTrue(prefixResults.contains("ជាង"))
        assertTrue(prefixResults.contains("ចង់"))
    }
    
    @Test
    fun `searchPrefix should handle prefix that is also a complete word with frequencies`() {
        // Given & When
        trie.insert("j", "ជ", 3)
        trie.insert("jg", "ជាង", 2)
        trie.insert("jol", "ចូល", 1)
        
        // Then
        val result = trie.searchPrefix("j")
        assertEquals(3, result.size)
        assertEquals("ជ", result[0])     // Highest frequency
        assertEquals("ជាង", result[1])   // Second highest
        assertEquals("ចូល", result[2])   // Lowest frequency
    }
    
    // Levenshtein Distance Tests
    
    @Test
    fun `levenshtein should handle basic cases correctly`() {
        // Given & When & Then
        assertEquals(0, trie.levenshtein("test", "test"), "Identical strings should have distance 0")
        assertEquals(1, trie.levenshtein("test", "text"), "Single substitution should have distance 1")
        assertEquals(1, trie.levenshtein("test", "tests"), "Single insertion should have distance 1")
        assertEquals(1, trie.levenshtein("tests", "test"), "Single deletion should have distance 1")
        assertEquals(4, trie.levenshtein("", "test"), "Empty to non-empty should equal length")
        assertEquals(4, trie.levenshtein("test", ""), "Non-empty to empty should equal length")
    }
    
    @Test
    fun `levenshtein should handle romanization typos correctly`() {
        // Given & When & Then - Test cases relevant to Khmer romanization
        assertEquals(1, trie.levenshtein("slanh", "slah"), "Missing character")
        assertEquals(1, trie.levenshtein("ban", "bam"), "Wrong character")
        assertEquals(1, trie.levenshtein("jg", "jgg"), "Extra character")
        assertEquals(1, trie.levenshtein("chea", "chxa"), "Single substitution e->x")
    }
    
    // Basic Fuzzy Search Tests
    
    @Test
    fun `searchFuzzy should find exact matches`() {
        // Given
        trie.insert("ban", "បាន")
        trie.insert("jg", "ចង់")
        
        // When
        val result = trie.searchFuzzy("ban", 1)
        
        // Then
        assertTrue(result.contains("បាន"), "Should find exact match")
        assertFalse(result.contains("ចង់"), "Should not include unrelated words")
    }
    
    @Test
    fun `searchFuzzy should find single character typos`() {
        // Given
        trie.insert("slanh", "ស្លាញ់")
        
        // When
        val result = trie.searchFuzzy("slah", 1) // Missing 'n'
        
        // Then
        assertTrue(result.contains("ស្លាញ់"), "Should find word despite missing character")
    }
    
    @Test
    fun `searchFuzzy should respect maxDistance limit`() {
        // Given
        trie.insert("test", "តេស្ត")
        
        // When
        val withinLimit = trie.searchFuzzy("text", 1) // 1 substitution
        val exceedsLimit = trie.searchFuzzy("txxt", 1) // 2 substitutions
        
        // Then
        assertTrue(withinLimit.contains("តេស្ត"), "Should find match within distance limit")
        assertFalse(exceedsLimit.contains("តេស្ត"), "Should not find match exceeding distance limit")
    }
    
    @Test
    fun `searchFuzzy should return empty for empty input`() {
        // Given
        trie.insert("test", "តេស្ត")
        
        // When
        val result = trie.searchFuzzy("", 1)
        
        // Then
        assertTrue(result.isEmpty(), "Should return empty for empty input")
    }
    
    @Test
    fun `searchFuzzy should sort by distance and frequency`() {
        // Given
        trie.insert("test", "តេស្ត១", 1)   // Exact match, low frequency
        trie.insert("tests", "តេស្ត២", 3) // Distance 1, high frequency
        trie.insert("text", "តេស្ត៣", 2)  // Distance 1, medium frequency
        
        // When
        val result = trie.searchFuzzy("test", 2)
        
        // Then
        assertEquals("តេស្ត១", result[0], "Exact match should be first")
        assertEquals("តេស្ត២", result[1], "Higher frequency should come next for same distance")
        assertEquals("តេស្ត៣", result[2], "Lower frequency should come last for same distance")
    }
} 