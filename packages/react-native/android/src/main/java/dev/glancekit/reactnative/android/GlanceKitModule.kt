package dev.glancekit.reactnative.android

import android.util.Log
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.bridge.ReadableType
import dev.glancekit.androidcore.ProgressCardData
import dev.glancekit.androidcore.WidgetUpdateManager
import dev.glancekit.reactnative.android.widget.ProgressCardWidget
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class GlanceKitModule(
    reactContext: ReactApplicationContext,
) : ReactContextBaseJavaModule(reactContext) {
    private val moduleScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun getName(): String = NAME

    override fun invalidate() {
        moduleScope.cancel()
        super.invalidate()
    }

    @ReactMethod
    fun updateWidget(widgetId: String, data: ReadableMap, promise: Promise) {
        Log.d(
            TAG,
            "updateWidget called widgetId=$widgetId hasTitle=${data.hasKey("title")} " +
                "hasSubtitle=${data.hasKey("subtitle")} hasProgress=${data.hasKey("progress")}",
        )
        val progressCardData = try {
            readProgressCardData(data)
        } catch (error: IllegalArgumentException) {
            Log.e(TAG, "updateWidget payload parse failure widgetId=$widgetId", error)
            promise.reject(ERROR_INVALID_WIDGET_DATA, error.message, error)
            return
        }

        moduleScope.launch {
            try {
                val parsedWidgetId = widgetId.toIntOrNull()
                if (parsedWidgetId != null && parsedWidgetId > 0) {
                    WidgetUpdateManager.updateProgressCardWidget(
                        context = reactApplicationContext,
                        widgetId = parsedWidgetId,
                        data = progressCardData,
                        widgetClass = ProgressCardWidget::class.java,
                    )
                } else {
                    WidgetUpdateManager.updateAllProgressCardWidgets(
                        context = reactApplicationContext,
                        data = progressCardData,
                        widgetClass = ProgressCardWidget::class.java,
                    )
                }
                Log.d(TAG, "updateWidget success widgetId=$widgetId")
                promise.resolve(null)
            } catch (error: IllegalArgumentException) {
                Log.e(TAG, "updateWidget validation failure widgetId=$widgetId", error)
                promise.reject(ERROR_INVALID_WIDGET_DATA, error.message, error)
            } catch (error: IllegalStateException) {
                Log.e(TAG, "updateWidget no widgets failure widgetId=$widgetId", error)
                promise.reject(ERROR_NO_WIDGETS, error.message, error)
            } catch (error: Exception) {
                Log.e(TAG, "updateWidget native failure widgetId=$widgetId", error)
                promise.reject(
                    ERROR_NATIVE_FAILURE,
                    error.message ?: "Native widget update failed.",
                    error,
                )
            }
        }
    }

    private fun readProgressCardData(data: ReadableMap): ProgressCardData {
        val title = data.requireString("title")
        val subtitle = data.requireString("subtitle")
        val progress = data.requireInt("progress")
        val deepLink = data.optionalString("deepLink")

        return ProgressCardData(
            title = title,
            subtitle = subtitle,
            progress = progress,
            deepLink = deepLink,
        )
    }

    private fun ReadableMap.requireString(key: String): String {
        if (!hasKey(key) || isNull(key)) {
            throw IllegalArgumentException("Widget data field '$key' is required.")
        }
        return getString(key)
            ?: throw IllegalArgumentException("Widget data field '$key' must be a string.")
    }

    private fun ReadableMap.optionalString(key: String): String? {
        if (!hasKey(key) || isNull(key)) {
            return null
        }
        return getString(key)
            ?: throw IllegalArgumentException("Widget data field '$key' must be a string.")
    }

    private fun ReadableMap.requireInt(key: String): Int {
        if (!hasKey(key) || isNull(key)) {
            throw IllegalArgumentException("Widget data field '$key' is required.")
        }

        return when (getType(key)) {
            ReadableType.Number -> getDouble(key).toInt()
            else -> throw IllegalArgumentException("Widget data field '$key' must be a number.")
        }
    }

    companion object {
        const val NAME = "GlanceKitModule"
        private const val TAG = "GlanceKitModule"

        private const val ERROR_INVALID_WIDGET_DATA = "E_INVALID_WIDGET_DATA"
        private const val ERROR_NO_WIDGETS = "E_NO_WIDGETS"
        private const val ERROR_NATIVE_FAILURE = "E_NATIVE_FAILURE"
    }
}
