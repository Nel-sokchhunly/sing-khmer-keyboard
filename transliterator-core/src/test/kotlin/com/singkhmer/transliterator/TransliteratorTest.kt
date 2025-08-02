package com.singkhmer.transliterator

import kotlin.test.Test
import kotlin.test.assertEquals

class TransliteratorTest {
    @Test
    fun testHello() {
        val transliterator = Transliterator()

        // test search for "jg"
        val results = transliterator.searchExact("jg")
        println(results)
    }
}
