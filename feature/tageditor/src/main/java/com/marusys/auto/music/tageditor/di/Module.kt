package com.marusys.auto.music.tageditor.di

import com.marusys.auto.music.tageditor.viewmodel.TagEditorViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val tagEditorModule = module {
    viewModel { parameters -> TagEditorViewModel(get(), parameters.get()) }
}