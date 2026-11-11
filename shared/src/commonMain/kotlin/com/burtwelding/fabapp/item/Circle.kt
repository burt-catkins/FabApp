package com.burtwelding.fabapp.item

import kotlinx.serialization.Serializable

import com.burtwelding.fabapp.storage.ItemHandle
import org.kimplify.deci.Deci
import org.kimplify.deci.DeciConstants
import org.kimplify.deci.validation.safeDivide

@Serializable
class Circle (val radius: ItemHandle<Length>,
              val angle: ItemHandle<Angle>) : Item() {
    // If Angle is zero, then just get the whole circumference
    fun getCircumference () = Length(
        Deci(2)
            .times(DeciConstants.PI)
            .times(radius.item.baseUnitDecimalValue)
            .times(if (angle.item.baseUnitDecimalValue == Deci.ZERO) Deci(360) else angle.item.baseUnitDecimalValue)
            .safeDivide(Deci(360))
    )
}