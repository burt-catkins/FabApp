# Storage

## Items with `ItemHandle`

### Save and read

```kotlin
val handle = ItemHandle(Length(Deci(10))) // Holds a new length in memory

val id = handle.save()                 // Suspend until saved, then return the UUID
val reference = ItemHandle<Length>(id) // Point to the same saved item by UUID
val length = reference.resolve()       // Suspend while reading from disk
val sameLength = reference.item        // Read synchronously
```

Pass a UUID to `save(id)` to choose the key. Saving the same handle again returns its saved UUID.

### Edit and delete

Save the item before calling `update` or `remove`.

```kotlin
// Read the latest length, change its value, and save it in one transaction.
reference.update { it.copy(baseUnitDecimalValue = Deci(20)) }
val updated = handle.resolve() // Reads the updated length at the same UUID
reference.remove()             // Removes the item from the database
```

| Operation | In a coroutine | Synchronous | What happens |
| --- | --- | --- | --- |
| Save | `save()` | `saveBlocking()` | Saves a new item and returns its UUID. A saved handle returns its existing UUID. |
| Read | `resolve()` | `.item` | Returns the direct item or reads the latest saved value for the UUID. A missing saved item throws. |
| Edit | `update { … }` | `updateBlocking { … }` | Reads, transforms, and saves the item atomically. Returns the replacement. |
| Delete | `remove()` | `removeBlocking()` | Deletes the saved item. Handles for that UUID throw on their next read. |

### In Compose

```kotlin
@Composable
fun SavedLength(id: Uuid) {
    // Start with null and load the saved length when id changes.
    val length by produceState<Length?>(null, id) {
        value = ItemHandle<Length>(id).resolve() // Suspend while loading the item
    }
    // Show a placeholder until the first read finishes.
    Text(length?.baseUnitDecimalValue?.toString() ?: "Loading…")
}
```

`SavedLength` loads when `id` changes. To save an item from a UI event, call `handle.save()` in a UI coroutine.

## Preferences and general values with Storage

```kotlin
// Declare the key and use false when there is no saved value.
val darkMode = Storage.value("preferences/darkMode", default = false)

// Run these calls from a coroutine.
darkMode.set(true)           // Save true and wait for the write to finish
val enabled = darkMode.get() // Read the saved value
darkMode.update { !it }      // Toggle and save in one transaction
darkMode.remove()            // Clear the saved value so reads use the default again
```

| In a coroutine | Synchronous | What happens | When the key is missing |
| --- | --- | --- | --- |
| `get()` | `getBlocking()` | Reads the saved value. | Returns the default or throws if no default exists. |
| `getOrNull()` | `getOrNullBlocking()` | Reads the saved value. | Returns `null`, regardless of the default. |
| `set(value)` | `setBlocking(value)` | Saves the supplied value. | Creates the entry. |
| `update { … }` | `updateBlocking { … }` | Transforms and saves the value atomically. Returns the replacement. | Transforms the default or throws if no default exists. |
| `remove()` | `removeBlocking()` | Deletes the saved value. | Completes successfully. |

Custom types need `@Serializable`.

### In Compose

```kotlin
@Composable
fun DarkModeSwitch() {
    // Observe the saved preference, starting with false while it loads.
    var darkMode by rememberStorage("preferences/darkMode", default = false)

    Switch(
        checked = darkMode,                  // Reflect the current preference
        onCheckedChange = { darkMode = it }, // Queue a save when the switch changes
    )
}
```

| When | What `rememberStorage` does |
| --- | --- |
| The composable first appears | Shows the default while loading the saved value. |
| You assign a new value | Saves asynchronously and reflects the committed value. |
| Another caller saves or removes the same key | Reads the value again. Removing it restores the default. |
| You pass `blocking = true` | Waits for each save before the assignment returns. |
