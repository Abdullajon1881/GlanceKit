package dev.glancekit.androidcore

import android.content.Context
import android.util.Log
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.updateAll
import dev.glancekit.androidcore.widget.ProgressCardWidget

object WidgetUpdateManager {
    suspend fun updateProgressCardWidget(
        context: Context,
        widgetId: Int,
        data: ProgressCardData,
        widgetClass: Class<out ProgressCardWidget> = ProgressCardWidget::class.java,
    ): ProgressCardData {
        require(widgetId > 0) { "Widget ID must be a positive integer." }

        Log.d(TAG, "updateProgressCardWidget start widgetId=$widgetId widgetClass=${widgetClass.name} rawData=$data")
        val normalized = data.normalized()
        normalized.validate()
        WidgetStateRepository.saveWidget(context, widgetId, normalized)
        refreshProgressCardWidget(context, widgetId, widgetClass)
        Log.d(TAG, "updateProgressCardWidget success widgetId=$widgetId normalized=$normalized")
        return normalized
    }

    suspend fun loadProgressCardWidget(
        context: Context,
        widgetId: Int,
    ): ProgressCardData {
        Log.d(TAG, "loadProgressCardWidget widgetId=$widgetId")
        return WidgetStateRepository.loadWidget(context, widgetId)
    }

    suspend fun refreshProgressCardWidget(
        context: Context,
        widgetId: Int,
        widgetClass: Class<out ProgressCardWidget> = ProgressCardWidget::class.java,
    ) {
        Log.d(TAG, "refreshProgressCardWidget widgetId=$widgetId widgetClass=${widgetClass.name}")
        val manager = GlanceAppWidgetManager(context)
        val glanceIds = manager.getGlanceIds(widgetClass)
        Log.d(TAG, "refreshProgressCardWidget glanceIds count=${glanceIds.size} values=$glanceIds")
        val widget = widgetClass.getDeclaredConstructor().newInstance()
        glanceIds.forEach { glanceId ->
            val appWidgetId = manager.getAppWidgetId(glanceId)
            if (appWidgetId == widgetId) {
                Log.d(TAG, "refreshProgressCardWidget matched glanceId=$glanceId appWidgetId=$appWidgetId")
                widget.update(context, glanceId)
            }
        }
    }

    suspend fun refreshAllProgressCardWidgets(
        context: Context,
        widgetClass: Class<out ProgressCardWidget> = ProgressCardWidget::class.java,
    ) {
        Log.d(TAG, "refreshAllProgressCardWidgets widgetClass=${widgetClass.name}")
        widgetClass.getDeclaredConstructor().newInstance().updateAll(context)
    }

    suspend fun updateAllProgressCardWidgets(
        context: Context,
        data: ProgressCardData,
        widgetClass: Class<out ProgressCardWidget> = ProgressCardWidget::class.java,
    ): Int {
        val normalized = data.normalized()
        normalized.validate()

        val manager = GlanceAppWidgetManager(context)
        val glanceIds = manager.getGlanceIds(widgetClass)
        Log.d(
            TAG,
            "updateAllProgressCardWidgets widgetClass=${widgetClass.name} glanceIds count=${glanceIds.size} values=$glanceIds",
        )
        check(glanceIds.isNotEmpty()) {
            "No ProgressCardWidget instances are currently added to the home screen. " +
                "Searched for widgetClass=${widgetClass.name}."
        }

        Log.d(
            TAG,
            "updateAllProgressCardWidgets count=${glanceIds.size} normalized=$normalized",
        )
        val widget = widgetClass.getDeclaredConstructor().newInstance()
        glanceIds.forEach { glanceId ->
            val appWidgetId = manager.getAppWidgetId(glanceId)
            WidgetStateRepository.saveWidget(context, appWidgetId, normalized)
            Log.d(TAG, "updateAllProgressCardWidgets updating glanceId=$glanceId appWidgetId=$appWidgetId")
            widget.update(context, glanceId)
        }

        return glanceIds.size
    }

    suspend fun deleteProgressCardWidget(context: Context, widgetId: Int) {
        Log.d(TAG, "deleteProgressCardWidget widgetId=$widgetId")
        WidgetStateRepository.deleteWidget(context, widgetId)
    }

    private const val TAG = "GlanceKitUpdate"
}
