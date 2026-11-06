package com.burtwelding.fabapp

import com.burtwelding.fabapp.storage.ItemHandle

class Circle(
    val radius: ItemHandle<Length>,
    val angle: ItemHandle<Angle>,
) : Item()