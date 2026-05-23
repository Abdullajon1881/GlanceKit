package dev.glancekit.reactnative.android.widget

import androidx.glance.appwidget.GlanceAppWidget

class ProgressCardWidgetReceiver : dev.glancekit.androidcore.widget.ProgressCardWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = ProgressCardWidget()
}
