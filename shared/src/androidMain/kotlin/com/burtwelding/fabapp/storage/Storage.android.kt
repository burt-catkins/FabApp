package com.burtwelding.fabapp.storage

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Volatile
private var storageDatabasePath: String? = null

internal actual fun defaultStorageDatabasePath(): String? = storageDatabasePath

@PublishedApi
@Composable
internal actual fun configureStorageForComposition() {
    val context = LocalContext.current.applicationContext
    storageDatabasePath = remember(context) {
        context.getDatabasePath("fabapp.db").absolutePath
    }
}
