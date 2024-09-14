package com.marusys.auto.music.widgets

import android.content.Context
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import com.marusys.auto.music.widgets.ui.CardWidgetUi
import com.marusys.auto.music.widgets.ui.WidgetState



class CardWidget : AbstractAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val widgetManager = getWidgetManager(context)

        provideContent {
            val state by widgetManager.state.collectAsState(WidgetState.NoQueue)
            CardWidgetUi(state)
        }
    }

}

class CardWidgetReceiver: GlanceAppWidgetReceiver() {

    override val glanceAppWidget: GlanceAppWidget = CardWidget()

}