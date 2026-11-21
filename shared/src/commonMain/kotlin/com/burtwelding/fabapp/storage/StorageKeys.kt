package com.burtwelding.fabapp.storage

enum class StorageKeys (val key: String) {
    CURRENT_PROGRAM ("current_program"),
    CURRENT_MITER ("current_miter"),
    MITER_SHOW_DIALOG ("miter_show_dialog")
    ;
    override fun toString(): String {
        return this.key
    }
}