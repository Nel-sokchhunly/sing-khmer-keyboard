package com.singkhmer.transliterator

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class TransliteratorDatasetTest {

    @Test
    fun `loadDataset should load dataset from resources and create correct mappings`() {
        // Given
        val transliterator = Transliterator()
        
        // When
        val dataset = transliterator.loadDataset()
        
        // Then
        assertNotNull(dataset, "Dataset should not be null")
        assertFalse(dataset.isEmpty(), "Dataset should not be empty")
        
        // Assert that "ban" exists and maps to Khmer word "បាន" (line 1 in dataset)
        assertTrue(dataset.containsKey("ban"), "Dataset should contain key 'ban'")
        val banWords = dataset["ban"]
        assertNotNull(banWords, "List for 'ban' should not be null")
        assertTrue(banWords!!.contains("បាន"), "The key 'ban' should map to 'បាន'")
        
        // Assert that "jg" exists and maps to both "ជាង" and "ចង់" (lines 100, 107 in dataset)
        assertTrue(dataset.containsKey("jg"), "Dataset should contain key 'jg'")
        val jgWords = dataset["jg"]
        assertNotNull(jgWords, "List for 'jg' should not be null")
        assertTrue(jgWords!!.contains("ជាង"), "The key 'jg' should map to 'ជាង'")
        assertTrue(jgWords.contains("ចង់"), "The key 'jg' should map to 'ចង់'")
    }
    
    @Test
    fun `loadDataset should handle multiple romanizations for same Khmer word`() {
        // Given
        val transliterator = Transliterator()
        
        // When
        val dataset = transliterator.loadDataset()
        
        // Then
        // From dataset line 100: "ជាង: cheang, jeang, jg" - all should map to the same Khmer word
        assertTrue(dataset.containsKey("cheang"), "Dataset should contain key 'cheang'")
        assertTrue(dataset.containsKey("jeang"), "Dataset should contain key 'jeang'")
        assertTrue(dataset.containsKey("jg"), "Dataset should contain key 'jg'")
        
        val cheangWords = dataset["cheang"]
        val jeangWords = dataset["jeang"]
        val jgWords = dataset["jg"]
        
        assertNotNull(cheangWords, "List for 'cheang' should not be null")
        assertNotNull(jeangWords, "List for 'jeang' should not be null")
        assertNotNull(jgWords, "List for 'jg' should not be null")
        
        assertTrue(cheangWords!!.contains("ជាង"), "The key 'cheang' should map to 'ជាង'")
        assertTrue(jeangWords!!.contains("ជាង"), "The key 'jeang' should map to 'ជាង'")
        assertTrue(jgWords!!.contains("ជាង"), "The key 'jg' should map to 'ជាង'")
    }
    
    @Test
    fun `loadDataset should handle case insensitive romanizations`() {
        // Given
        val transliterator = Transliterator()
        
        // When
        val dataset = transliterator.loadDataset()
        
        // Then
        // All romanizations should be stored in lowercase
        val uppercaseKeys = dataset.keys.filter { it != it.lowercase() }
        assertTrue(uppercaseKeys.isEmpty(), "All keys should be lowercase, found: $uppercaseKeys")
    }
    
    @Test
    fun `loadDataset should return non-empty dataset`() {
        // Given
        val transliterator = Transliterator()
        
        // When
        val dataset = transliterator.loadDataset()
        
        // Then
        assertTrue(dataset.isNotEmpty(), "Dataset should contain entries")
        
        // Verify some basic properties
        dataset.forEach { (romanization, khmerWords) ->
            assertFalse(romanization.isBlank(), "Romanization key should not be blank")
            assertTrue(khmerWords.isNotEmpty(), "Each romanization should map to at least one Khmer word")
            khmerWords.forEach { khmerWord ->
                assertFalse(khmerWord.isBlank(), "Khmer word should not be blank")
            }
        }
    }
} 