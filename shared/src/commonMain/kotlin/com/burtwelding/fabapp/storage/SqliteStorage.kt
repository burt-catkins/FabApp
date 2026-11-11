package com.burtwelding.fabapp.storage

import androidx.sqlite.SQLiteConnection
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import androidx.sqlite.execSQL

internal class SqliteStorage(path: String) {
    private val connection: SQLiteConnection = BundledSQLiteDriver().open(path)

    init {
        try {
            connection.execSQL("PRAGMA busy_timeout = 5000")
            transaction {
                val version = connection.prepare("PRAGMA user_version").use {
                    check(it.step())
                    it.getLong(0)
                }
                check(version in 0L..1L) { "Unsupported storage schema version: $version" }
                if (version == 0L) {
                    connection.execSQL(
                        "CREATE TABLE stored_values (key TEXT PRIMARY KEY NOT NULL, type TEXT NOT NULL, payload TEXT NOT NULL)"
                    )
                    connection.execSQL("PRAGMA user_version = 1")
                }
            }
        } catch (error: Throwable) {
            connection.close()
            throw error
        }
    }

    data class Record(val type: String, val payload: String)

    fun read(key: String): Record? =
        connection.prepare("SELECT type, payload FROM stored_values WHERE key = ?").use {
            it.bindText(1, key)
            if (it.step()) Record(it.getText(0), it.getText(1)) else null
        }

    fun write(key: String, type: String, payload: String) {
        connection.prepare(
            "INSERT INTO stored_values (key, type, payload) VALUES (?, ?, ?) " +
                "ON CONFLICT(key) DO UPDATE SET type = excluded.type, payload = excluded.payload"
        ).use {
            it.bindText(1, key)
            it.bindText(2, type)
            it.bindText(3, payload)
            it.step()
        }
    }

    fun remove(key: String) {
        connection.prepare("DELETE FROM stored_values WHERE key = ?").use {
            it.bindText(1, key)
            it.step()
        }
    }

    fun <T> transaction(block: () -> T): T {
        connection.execSQL("BEGIN IMMEDIATE TRANSACTION")
        try {
            val result = block()
            connection.execSQL("COMMIT")
            return result
        } catch (error: Throwable) {
            try {
                connection.execSQL("ROLLBACK")
            } catch (rollbackError: Throwable) {
                error.addSuppressed(rollbackError)
            }
            throw error
        }
    }

    fun close() = connection.close()
}
