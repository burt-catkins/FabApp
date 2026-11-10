package com.burtwelding.fabapp

import com.burtwelding.fabapp.item.Angle
import com.burtwelding.fabapp.item.Length
import com.burtwelding.fabapp.item.Miter
import org.kimplify.deci.Deci
import com.burtwelding.fabapp.storage.*
import kotlin.test.Test

class SharedCommonTest {
    @Test
    fun testMiter () {
        val miter = Miter(
            leftPieceSize = ItemHandle<Length>(Length(Deci(1))),
            rightPieceSize = ItemHandle<Length>(Length(Deci(4))),
            totalJointAngle = ItemHandle<Angle>(Angle(Deci(120)))
        ).calculate()
        val bruh = 1
    }
}