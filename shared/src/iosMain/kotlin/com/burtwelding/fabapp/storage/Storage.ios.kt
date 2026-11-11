package com.burtwelding.fabapp.storage

import androidx.compose.runtime.Composable
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSApplicationSupportDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSUserDomainMask

@PublishedApi
@Composable
internal actual fun configureStorageForComposition() = Unit

@OptIn(ExperimentalForeignApi::class)
internal actual fun defaultStorageDatabasePath(): String? {
    val root = NSSearchPathForDirectoriesInDomains(NSApplicationSupportDirectory, NSUserDomainMask, true).first() as String
    val directory = "$root/FabApp"
    check(NSFileManager.defaultManager.createDirectoryAtPath(directory, true, null, null)) {
        "Could not create storage directory: $directory"
    }
    return "$directory/fabapp.db"
}
