package com.burtwelding.fabapp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform