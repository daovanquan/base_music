package com.marusys.auto.music.store.common.extention

import android.database.Cursor

fun Cursor.getInt(columnName: String): Int {
    try {
        return getInt(getColumnIndexOrThrow(columnName))
    } catch (ex: Throwable) {
        throw IllegalStateException("invalid column $columnName", ex)
    }
}

fun Cursor.getLong(columnName: String): Long {
    try {
        return getLong(getColumnIndexOrThrow(columnName))
    } catch (ex: Throwable) {
        throw IllegalStateException("invalid column $columnName", ex)
    }
}

fun Cursor.getString(columnName: String): String {
    try {
        return getString(getColumnIndexOrThrow(columnName))
    } catch (ex: Throwable) {
        throw IllegalStateException("invalid column $columnName", ex)
    }
}

fun Cursor.getStringOrNull(columnName: String): String? {
    try {
        return getString(getColumnIndexOrThrow(columnName))
    } catch (ex: Throwable) {
        throw IllegalStateException("invalid column $columnName", ex)
    }
}
