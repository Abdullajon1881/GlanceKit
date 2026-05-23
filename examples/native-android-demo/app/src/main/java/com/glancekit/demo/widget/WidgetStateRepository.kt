package com.glancekit.demo.widget

import android.content.Context

object WidgetStateRepository {
    suspend fun loadWidget(
        context: Context,
        widgetId: Int,
    ): dev.glancekit.androidcore.ProgressCardData {
        return dev.glancekit.androidcore.WidgetUpdateManager.loadProgressCardWidget(
            context = context,
            widgetId = widgetId,
        )
    }

    suspend fun updateWidget(
        context: Context,
        widgetId: Int,
        data: dev.glancekit.androidcore.ProgressCardData,
    ) {
        dev.glancekit.androidcore.WidgetUpdateManager.updateProgressCardWidget(
            context = context,
            widgetId = widgetId,
            data = data,
        )
    }
}
