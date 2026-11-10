package com.burtwelding.fabapp.storage

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer

internal expect fun defaultStorageDatabasePath(): String?

/** App-wide disk storage. The database opens on the first read or write. */
object Storage {
    private val mutex = Mutex()
    private var database: SqliteStorage? = null
    private var databasePath: String? = null

    internal val json = Json {
        encodeDefaults = true
        ignoreUnknownKeys = true
        useArrayPolymorphism = true
    }

    /** Supplies a path without opening the database. Repeating the same path is safe. */
    fun configure(databasePath: String) {
        require(databasePath.isNotBlank()) { "Database path cannot be blank" }
        runBlocking {
            mutex.withLock {
                check(this@Storage.databasePath == null || this@Storage.databasePath == databasePath) {
                    "Storage is already configured with a different database path"
                }
                this@Storage.databasePath = databasePath
            }
        }
    }

    inline fun <reified T : Any> value(key: String): StoredValue<T> = value(key, serializerFor<T>())

    inline fun <reified T : Any> value(key: String, default: T): StoredValue<T> = value(key, serializerFor<T>(), default)

    fun <T : Any> value(key: String, serializer: KSerializer<T>): StoredValue<T> = StoredValue(key, serializer, null)

    fun <T : Any> value(key: String, serializer: KSerializer<T>, default: T): StoredValue<T> = StoredValue(key, serializer, default)

    @PublishedApi
    internal inline fun <reified T : Any> serializerFor(): KSerializer<T> = storageSerializer(serializer<T>())

    internal suspend fun <T> access(block: (SqliteStorage) -> T): T = withContext(Dispatchers.IO) {
        mutex.withLock {
            val current = database ?: run {
                val location = checkNotNull(databasePath ?: defaultStorageDatabasePath()) {
                    "Configure Storage with a database path before first use"
                }
                SqliteStorage(location).also {
                    database = it
                    databasePath = location
                }
            }
            block(current)
        }
    }

    // Tests select temporary files or in-memory databases without changing app storage.
    internal fun resetForTests(databasePath: String? = null) = runBlocking {
        withContext(Dispatchers.IO) {
            mutex.withLock {
                database?.close()
                database = null
                this@Storage.databasePath = databasePath
            }
        }
    }
}

/** A key and serializer, not a cached value. Top-level values are non-null; remove represents absence. */
class StoredValue<T : Any> internal constructor(
    val key: String,
    private val serializer: KSerializer<T>,
    private val default: T?,
) {
    init {
        require(key.isNotEmpty()) { "Storage keys cannot be empty" }
    }

    suspend fun get(): T = getOrNull() ?: default ?: throw MissingStoredValueException(key)

    /** Returns the persisted value only; an absent key returns null even if a default was declared. */
    suspend fun getOrNull(): T? = Storage.access { read(it) }

    suspend fun set(value: T) {
        val payload = encode(value)
        Storage.access { it.write(key, serializer.descriptor.serialName, payload) }
    }

    /** The non-suspending transform must not call storage. A failure rolls back the entire update. */
    suspend fun update(transform: (T) -> T): T = Storage.access { database ->
        database.transaction {
            val current = read(database) ?: default ?: throw MissingStoredValueException(key)
            val replacement = transform(current)
            database.write(key, serializer.descriptor.serialName, encode(replacement))
            replacement
        }
    }

    suspend fun remove() {
        Storage.access { it.remove(key) }
    }

    /** Blocking compatibility methods perform disk I/O; prefer suspend methods in UI code. */
    fun getBlocking(): T = runBlocking { get() }
    fun getOrNullBlocking(): T? = runBlocking { getOrNull() }
    fun setBlocking(value: T) = runBlocking { set(value) }
    fun updateBlocking(transform: (T) -> T): T = runBlocking { update(transform) }
    fun removeBlocking() = runBlocking { remove() }

    internal fun insertBlocking(value: T) {
        val payload = encode(value)
        runBlocking {
            Storage.access { database ->
                database.transaction {
                    require(database.read(key) == null) { "Key already belongs to another item" }
                    database.write(key, serializer.descriptor.serialName, payload)
                }
            }
        }
    }

    private fun read(database: SqliteStorage): T? {
        val record = database.read(key) ?: return null
        require(record.type == serializer.descriptor.serialName) {
            "Stored value '$key' has type ${record.type}; requested ${serializer.descriptor.serialName}"
        }
        return try {
            Storage.json.decodeFromString(serializer, record.payload)
        } catch (error: Exception) {
            throw StoredValueSerializationException(key, "decode", error)
        }
    }

    private fun encode(value: T): String = try {
        Storage.json.encodeToString(serializer, value)
    } catch (error: Exception) {
        throw StoredValueSerializationException(key, "encode", error)
    }
}

class MissingStoredValueException(key: String) : NoSuchElementException("No saved value exists for '$key'")

class StoredValueSerializationException(key: String, operation: String, cause: Exception) :
    IllegalStateException("Could not $operation stored value '$key'", cause)
