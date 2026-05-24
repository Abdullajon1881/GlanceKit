package dev.glancekit.androidcore

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

private val Context.widgetDataStore by preferencesDataStore(name = "glancekit_widgets")

internal object WidgetStateRepository {
    suspend fun loadWidget(context: Context, widgetId: Int): ProgressCardData {
        if (widgetId <= 0) {
            Log.w(TAG, "loadWidget called with invalid widgetId=$widgetId, returning default state")
            return ProgressCardData.DEFAULT
        }

        val preferences = context.widgetDataStore.data.first()
        val data = ProgressCardData(
            title = preferences[stringPreferencesKey(titleKey(widgetId))]
                ?: ProgressCardData.DEFAULT.title,
            subtitle = preferences[stringPreferencesKey(subtitleKey(widgetId))]
                ?: ProgressCardData.DEFAULT.subtitle,
            progress = preferences[intPreferencesKey(progressKey(widgetId))]
                ?: ProgressCardData.DEFAULT.progress,
            deepLink = preferences[stringPreferencesKey(deepLinkKey(widgetId))],
        )
        Log.d(
            TAG,
            "loadWidget widgetId=$widgetId title=${data.title} progress=${data.progress} deepLink=${data.deepLink}",
        )
        return data
    }

    suspend fun saveWidget(context: Context, widgetId: Int, data: ProgressCardData) {
        Log.d(
            TAG,
            "saveWidget widgetId=$widgetId title=${data.title} progress=${data.progress} deepLink=${data.deepLink}",
        )
        context.widgetDataStore.edit { preferences ->
            preferences[stringPreferencesKey(titleKey(widgetId))] = data.title
            preferences[stringPreferencesKey(subtitleKey(widgetId))] = data.subtitle
            preferences[intPreferencesKey(progressKey(widgetId))] = data.progress
            val deepLinkKey = stringPreferencesKey(deepLinkKey(widgetId))
            if (data.deepLink == null) {
                preferences.remove(deepLinkKey)
            } else {
                preferences[deepLinkKey] = data.deepLink
            }
        }
    }

    suspend fun deleteWidget(context: Context, widgetId: Int) {
        if (widgetId <= 0) {
            Log.w(TAG, "deleteWidget skipped for invalid widgetId=$widgetId")
            return
        }

        Log.d(TAG, "deleteWidget widgetId=$widgetId")
        context.widgetDataStore.edit { preferences ->
            preferences.remove(stringPreferencesKey(titleKey(widgetId)))
            preferences.remove(stringPreferencesKey(subtitleKey(widgetId)))
            preferences.remove(intPreferencesKey(progressKey(widgetId)))
            preferences.remove(stringPreferencesKey(deepLinkKey(widgetId)))
        }
    }

    private fun titleKey(widgetId: Int) = "widget_${widgetId}_title"
    private fun subtitleKey(widgetId: Int) = "widget_${widgetId}_subtitle"
    private fun progressKey(widgetId: Int) = "widget_${widgetId}_progress"
    private fun deepLinkKey(widgetId: Int) = "widget_${widgetId}_deep_link"

    private const val TAG = "GlanceKitState"
}
