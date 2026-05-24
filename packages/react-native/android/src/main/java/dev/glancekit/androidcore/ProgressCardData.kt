package dev.glancekit.androidcore

data class ProgressCardData(
    val title: String,
    val subtitle: String,
    val progress: Int,
    val deepLink: String? = null,
) {
    fun normalized(): ProgressCardData = copy(
        title = title.trim(),
        subtitle = subtitle.trim(),
        deepLink = deepLink?.trim()?.takeIf { it.isNotEmpty() },
    )

    fun validate() {
        require(title.isNotBlank()) { "Widget title cannot be empty." }
        require(subtitle.isNotBlank()) { "Widget subtitle cannot be empty." }
        require(progress in 0..100) { "Widget progress must be between 0 and 100." }
    }

    companion object {
        val DEFAULT = ProgressCardData(
            title = "Daily Progress",
            subtitle = "Tap the app to update this widget.",
            progress = 0,
            deepLink = null,
        )
    }
}
