package com.marusys.auto.music.widgets

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidget
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

abstract class AbstractAppWidget : GlanceAppWidget(), KoinComponent {

    fun getWidgetManager(context: Context): WidgetManager {
        val widgetManager: WidgetManager = get()
        return widgetManager
    }
}