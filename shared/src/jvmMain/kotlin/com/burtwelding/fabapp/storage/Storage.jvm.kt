package com.burtwelding.fabapp.storage

import androidx.compose.runtime.Composable
import java.io.File

@PublishedApi
@Composable
internal actual fun configureStorageForComposition() = Unit

internal actual fun defaultStorageDatabasePath(): String? {
    val directory = desktopStorageDirectory(
        osName = System.getProperty("os.name"),
        userHome = System.getProperty("user.home"),
        localAppData = System.getenv("LOCALAPPDATA"),
        appData = System.getenv("APPDATA"),
        xdgDataHome = System.getenv("XDG_DATA_HOME"),
    )
    check(directory.isDirectory || directory.mkdirs()) { "Could not create storage directory: $directory" }
    return File(directory, "fabapp.db").absolutePath
}

internal fun desktopStorageDirectory(
    osName: String,
    userHome: String,
    localAppData: String? = null,
    appData: String? = null,
    xdgDataHome: String? = null,
): File {
    fun String?.absoluteDirectory(): File? =
        this?.takeIf { it.isNotBlank() }?.let(::File)?.takeIf { it.isAbsolute }

    val root = when {
        osName.startsWith("Windows", ignoreCase = true) ->
            localAppData.absoluteDirectory() ?: appData.absoluteDirectory() ?: File(userHome, "AppData/Local")
        osName.startsWith("Mac", ignoreCase = true) -> File(userHome, "Library/Application Support")
        else -> xdgDataHome.absoluteDirectory() ?: File(userHome, ".local/share")
    }
    return File(root, "FabApp")
}
