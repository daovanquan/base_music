package com.marusys.auto.music.widgets.di

import com.marusys.auto.music.widgets.WidgetManager
import org.koin.dsl.module

val widgetModule = module {
    single {
        WidgetManager()
    }
}