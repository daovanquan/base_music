package com.marusys.auto.music.network.di

import android.content.Context
import com.marusys.auto.music.network.data.NetworkMonitor
import com.marusys.auto.music.network.service.LyricsService
import org.koin.core.annotation.Qualifier
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

val retrofitModule = module {
    single(named("LyricsRetrofitService")) { Retrofit.Builder()
            .baseUrl(LyricsService.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build() }

    single {
        get<Retrofit>(named("LyricsRetrofitService")).create(LyricsService::class.java)
    }

    single { NetworkMonitor(get()) }
}