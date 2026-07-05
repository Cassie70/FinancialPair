package com.example.financialpair.util

import com.example.financialpair.data.entity.Topic
import java.text.Normalizer

object FuzzyMatcher {

    fun String.normalizeSearch(): String =
        Normalizer.normalize(this, Normalizer.Form.NFD)
            .replace("\\p{M}+".toRegex(), "")
            .lowercase()

    fun levenshtein(a: String, b: String): Int {
        val dp = Array(a.length + 1) { IntArray(b.length + 1) }

        for (i in 0..a.length) dp[i][0] = i
        for (j in 0..b.length) dp[0][j] = j

        for (i in 1..a.length) {
            for (j in 1..b.length) {
                val cost = if (a[i - 1] == b[j - 1]) 0 else 1

                dp[i][j] = minOf(
                    dp[i - 1][j] + 1,
                    dp[i][j - 1] + 1,
                    dp[i - 1][j - 1] + cost
                )
            }
        }

        return dp[a.length][b.length]
    }

    fun getMinFuzzyDistance(
        description: String,
        topic: String
    ): Int {
        val normalizedDescription = description.normalizeSearch()
        val normalizedTopic = topic.normalizeSearch()

        val descriptionWords = normalizedDescription.split(Regex("[^\\p{L}\\p{N}]+"))
            .filter { it.isNotBlank() }
        val topicWords = normalizedTopic.split(" ")

        if (descriptionWords.size < topicWords.size) return Int.MAX_VALUE

        var minDistance = Int.MAX_VALUE

        for (i in 0..descriptionWords.size - topicWords.size) {
            val window = descriptionWords
                .subList(i, i + topicWords.size)
                .joinToString(" ")

            val distance = levenshtein(window, normalizedTopic)
            if (distance < minDistance) {
                minDistance = distance
            }
        }

        return minDistance
    }

    fun findBestMatchedTopic(description: String, topics: List<Topic>): Topic? {
        return topics
            .map { it to getMinFuzzyDistance(description, it.name) }
            .filter { (topic, distance) ->
                val normalizedTopic = topic.name.normalizeSearch()
                // Aumentamos el umbral para nombres más largos
                val threshold = if (normalizedTopic.length > 8) 2 else 1
                distance <= threshold
            }
            .minWithOrNull(compareBy<Pair<Topic, Int>> { it.second }.thenByDescending { it.first.name.length })
            ?.first
    }
}
