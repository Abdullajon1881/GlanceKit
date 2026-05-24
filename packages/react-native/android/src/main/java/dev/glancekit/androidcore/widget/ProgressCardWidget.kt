package dev.glancekit.androidcore.widget

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.Action
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.Text
import dev.glancekit.androidcore.ProgressCardData
import dev.glancekit.androidcore.WidgetUpdateManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

open class ProgressCardWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        Log.d(TAG, "provideGlance start glanceId=$id")
        try {
            val renderState = loadRenderState(context, id)
            Log.d(TAG, "provideGlance before provideContent glanceId=$id")
            provideContent {
                Log.d(
                    TAG,
                    "provideContent entered glanceId=$id mode=${renderState.mode} widgetId=${renderState.widgetId}",
                )

                when (renderState.mode) {
                    RenderMode.Fallback -> {
                        Log.d(TAG, "render static content fallback glanceId=$id")
                        MinimalFallbackContent()
                    }

                    RenderMode.Data -> {
                        Log.d(
                            TAG,
                            "render data content widgetId=${renderState.widgetId} title=${renderState.data.title}",
                        )
                        ProgressCardTextContent(
                            data = renderState.data,
                            onClick = renderState.onClick,
                        )
                    }
                }
            }
        } catch (error: Throwable) {
            Log.e(TAG, "provideGlance failure catch glanceId=$id", error)
            throw error
        }
    }

    private suspend fun loadRenderState(context: Context, id: GlanceId): RenderState {
        return runCatching {
            val manager = GlanceAppWidgetManager(context)
            val widgetId = manager.getAppWidgetId(id)
            Log.d(TAG, "state read start widgetId=$widgetId glanceId=$id")

            val data = withContext(Dispatchers.IO) {
                WidgetUpdateManager.loadProgressCardWidget(context, widgetId)
            }.also { loadedData ->
                Log.d(
                    TAG,
                    "state read success widgetId=$widgetId title=${loadedData.title} progress=${loadedData.progress}",
                )
            }

            RenderState(
                widgetId = widgetId,
                data = data,
                onClick = createClickAction(context, widgetId, data),
                mode = RenderMode.Data,
            )
        }.getOrElse { error ->
            Log.e(TAG, "state read failure glanceId=$id", error)
            RenderState(
                widgetId = -1,
                data = ProgressCardData.DEFAULT,
                onClick = null,
                mode = RenderMode.Fallback,
            )
        }
    }

    private fun createClickAction(
        context: Context,
        widgetId: Int,
        data: ProgressCardData,
    ): Action? {
        Log.d(TAG, "click action creation start widgetId=$widgetId")
        return runCatching {
            val intent = createDeepLinkIntent(context, widgetId, data)
            actionStartActivity(intent).also {
                Log.d(TAG, "click action creation success widgetId=$widgetId uri=${intent.data}")
            }
        }.getOrElse { error ->
            Log.e(TAG, "click action creation failure widgetId=$widgetId", error)
            null
        }
    }

    private fun createDeepLinkIntent(
        context: Context,
        widgetId: Int,
        data: ProgressCardData,
    ): Intent {
        val deepLinkUri = data.deepLink?.let(Uri::parse) ?: Uri.Builder()
            .scheme("glancekit")
            .authority("progress")
            .appendPath(widgetId.toString())
            .build()

        return Intent(Intent.ACTION_VIEW, deepLinkUri).apply {
            `package` = context.packageName
            addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP
            )
        }
    }

    companion object {
        private const val TAG = "GlanceKitWidget"
    }
}

private data class RenderState(
    val widgetId: Int,
    val data: ProgressCardData,
    val onClick: Action?,
    val mode: RenderMode,
)

private enum class RenderMode {
    Fallback,
    Data,
}

@Composable
private fun MinimalFallbackContent() {
    Column(modifier = GlanceModifier.padding(CONTENT_PADDING)) {
        Text(text = "GlanceKit")
        Spacer(modifier = GlanceModifier.height(TEXT_SPACING_SMALL))
        Text(text = "Waiting for data")
    }
}

@Composable
private fun ProgressCardTextContent(
    data: ProgressCardData,
    onClick: Action?,
) {
    val contentModifier = GlanceModifier
        .padding(CONTENT_PADDING)
        .let { modifier ->
            if (onClick == null) {
                modifier
            } else {
                modifier.clickable(onClick)
            }
        }

    Column(modifier = contentModifier) {
        Text(text = data.title.ifBlank { ProgressCardData.DEFAULT.title })
        Spacer(modifier = GlanceModifier.height(TEXT_SPACING_SMALL))
        Text(text = data.subtitle.ifBlank { ProgressCardData.DEFAULT.subtitle })
        Spacer(modifier = GlanceModifier.height(TEXT_SPACING_LARGE))
        Text(text = "${data.progress.coerceIn(0, 100)}% complete")
    }
}

private val CONTENT_PADDING = 16.dp
private val TEXT_SPACING_SMALL = 4.dp
private val TEXT_SPACING_LARGE = 10.dp
