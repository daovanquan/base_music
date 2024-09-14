package com.marusys.auto.music.testing.data

import com.marusys.auto.music.database.dao.BlacklistedFoldersDao
import com.marusys.auto.music.database.entities.prefs.BlacklistedFolderEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class TestBlacklistDao: BlacklistedFoldersDao, KoinComponent {

    private val databaseStore: TestDatabaseStore by inject()

    override fun getAllBlacklistedFoldersFlow(): Flow<List<BlacklistedFolderEntity>> = flow {
        emit(databaseStore.blacklistedFolders)
    }

    override suspend fun addFolder(folder: BlacklistedFolderEntity) {
        databaseStore.addFolder(folder)
    }

    override suspend fun deleteFolder(path: String) {
        databaseStore.removeFolder(path)
    }
}