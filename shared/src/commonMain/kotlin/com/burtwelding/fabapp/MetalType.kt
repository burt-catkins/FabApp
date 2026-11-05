package com.burtwelding.fabapp

import org.kimplify.deci.Deci

enum class MetalType (val cubicFeetPounds: Deci) {
    ALUMINUM(Deci(168.48)),
    BRASS(Deci(535.68)),
    BRONZE(Deci(541)),
    CHROMIUM(Deci(428)),
    COPPER(Deci(559.87)),
    IRON(Deci(491.09)),
    IRON_CAST_GREY(Deci(442)),
    IRON_WROUGHT(Deci(480)),
    STAINLESS_STEEL(Deci(494.21)),
    STEEL_ROLLED(Deci(490))
}