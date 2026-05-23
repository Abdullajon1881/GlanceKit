package dev.glancekit.androidcore.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import dev.glancekit.androidcore.WidgetUpdateManager
import kotlinx.coroutines.runBlocking

open class ProgressCardWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = ProgressCardWidget()

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "onReceive action=${intent.action}")
        super.onReceive(context, intent)
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        Log.d(TAG, "onUpdate widgetIds=${appWidgetIds.joinToString()}")
        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }

    override fun onEnabled(context: Context) {
        Log.d(TAG, "onEnabled")
        super.onEnabled(context)
    }

    override fun onDisabled(context: Context) {
        Log.d(TAG, "onDisabled")
        super.onDisabled(context)
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        Log.d(TAG, "onDeleted widgetIds=${appWidgetIds.joinToString()}")
        super.onDeleted(context, appWidgetIds)
        runBlocking {
            appWidgetIds.forEach { widgetId ->
                WidgetUpdateManager.deleteProgressCardWidget(context, widgetId)
            }
        }
    }

    companion object {
        private const val TAG = "GlanceKitReceiver"
    }
}
