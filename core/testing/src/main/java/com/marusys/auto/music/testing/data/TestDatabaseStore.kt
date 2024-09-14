package com.marusys.auto.music.testing.data

import com.marusys.auto.music.database.entities.prefs.BlacklistedFolderEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TestDatabaseStore {
    val blacklistedFolders: List<BlacklistedFolderEntity> = emptyList()

    fun clear() {

    }

    suspend fun addFolder(folder: BlacklistedFolderEntity) = withContext(Dispatchers.IO) {
        blacklistedFolders.toMutableList().add(folder)
    }

    suspend fun removeFolder(path: String) = withContext(Dispatchers.IO) {
        blacklistedFolders.toMutableList().removeIf { it.folderPath == path }
    }

}