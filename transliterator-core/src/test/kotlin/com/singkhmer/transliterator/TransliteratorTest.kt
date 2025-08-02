package com.singkhmer.transliterator

import kotlin.test.Test
import kotlin.test.assertEquals

class TransliteratorTest {
    @Test
    fun testHello() {
        val transliterator = Transliterator()
        assertEquals("Hello from Sing Khmer Transliteration Engine", transliterator.hello())
    }
}
