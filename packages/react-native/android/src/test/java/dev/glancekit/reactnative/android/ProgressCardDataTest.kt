package dev.glancekit.reactnative.android

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ProgressCardDataTest {
    @Test
    fun `validate accepts valid data`() {
        ProgressCardData(
            title = "Worker is coming",
            subtitle = "Arriving in 12 min",
            progress = 72,
            deepLink = "glancekit://progress/progress-demo",
        ).validate()
    }

    @Test(expected = IllegalArgumentException::class)
    fun `validate rejects empty title`() {
        ProgressCardData(
            title = "",
            subtitle = "Arriving in 12 min",
            progress = 72,
        ).validate()
    }

    @Test(expected = IllegalArgumentException::class)
    fun `validate rejects empty subtitle`() {
        ProgressCardData(
            title = "Worker is coming",
            subtitle = "   ",
            progress = 72,
        ).validate()
    }

    @Test(expected = IllegalArgumentException::class)
    fun `validate rejects progress below range`() {
        ProgressCardData(
            title = "Worker is coming",
            subtitle = "Arriving in 12 min",
            progress = -1,
        ).validate()
    }

    @Test(expected = IllegalArgumentException::class)
    fun `validate rejects progress above range`() {
        ProgressCardData(
            title = "Worker is coming",
            subtitle = "Arriving in 12 min",
            progress = 101,
        ).validate()
    }

    @Test
    fun `normalized trims fields and clears blank deep link`() {
        val normalized = ProgressCardData(
            title = "  Worker is coming  ",
            subtitle = "  Arriving in 12 min ",
            progress = 72,
            deepLink = "   ",
        ).normalized()

        assertEquals("Worker is coming", normalized.title)
        assertEquals("Arriving in 12 min", normalized.subtitle)
        assertNull(normalized.deepLink)
    }
}
