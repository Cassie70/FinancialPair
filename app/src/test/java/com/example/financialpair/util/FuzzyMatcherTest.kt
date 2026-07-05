package com.example.financialpair.util

import com.example.financialpair.data.entity.Topic
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class FuzzyMatcherTest {

    @Test
    fun `test exact match`() {
        val topics = listOf(Topic(id = 1, name = "Uber", categoryId = 1))
        val result = FuzzyMatcher.findBestMatchedTopic("Uber", topics)
        assertNotNull(result)
        assertEquals(1, result?.id)
    }

    @Test
    fun `test fuzzy match - Buber matches Uber`() {
        val topics = listOf(Topic(id = 1, name = "Uber", categoryId = 1))
        val result = FuzzyMatcher.findBestMatchedTopic("Buber", topics)
        assertNotNull(result)
        assertEquals(1, result?.id)
    }

    @Test
    fun `test fuzzy match - Casa de toñe matches Cada de Toño`() {
        val topics = listOf(Topic(id = 1, name = "Cada de Toño", categoryId = 1))
        val result = FuzzyMatcher.findBestMatchedTopic("Casa de toñe", topics)
        assertNotNull(result)
        assertEquals(1, result?.id)
    }

    @Test
    fun `test fuzzy match - description with extra words`() {
        val topics = listOf(Topic(id = 1, name = "Starbucks", categoryId = 1))
        val result = FuzzyMatcher.findBestMatchedTopic("Starbucks Coffee Shop", topics)
        assertNotNull(result)
        assertEquals(1, result?.id)
    }

    @Test
    fun `test fuzzy match - no match when distance too high`() {
        val topics = listOf(Topic(id = 1, name = "Netflix", categoryId = 1))
        val result = FuzzyMatcher.findBestMatchedTopic("Amazon Prime", topics)
        assertNull(result)
    }

    @Test
    fun `test multiple topics - picks best match`() {
        val topics = listOf(
            Topic(id = 1, name = "Café", categoryId = 1),
            Topic(id = 2, name = "Cafetería Central", categoryId = 1)
        )
        // "Cafeteria" matches "Cafetería Central" window "Cafetería" (dist 1) 
        // and matches "Café" (dist > 1?)
        val result = FuzzyMatcher.findBestMatchedTopic("Cafetería Central", topics)
        assertEquals(2, result?.id)
    }
}
