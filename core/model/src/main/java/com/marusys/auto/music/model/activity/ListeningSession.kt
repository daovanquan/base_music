package com.marusys.auto.music.model.activity

import java.util.Date


data class ListeningSession(
    val startTime: Date,
    val durationSeconds: Int
)