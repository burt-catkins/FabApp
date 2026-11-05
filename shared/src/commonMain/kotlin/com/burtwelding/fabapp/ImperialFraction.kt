package com.burtwelding.fabapp

import org.kimplify.deci.Deci

enum class ImperialFraction (val denominator: Deci) {
    HALVES(Deci(2)),
    QUARTERS(Deci(4)),
    EIGHTHS(Deci(8)),
    SIXTEENTHS(Deci(16)),
    THIRTY_SECONDS(Deci(32)),
    SIXTY_FOURTHS(Deci(64));
}