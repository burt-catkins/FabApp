package com.burtwelding.fabapp.item

import kotlinx.serialization.Serializable

import com.burtwelding.fabapp.Calculator.convertDegreesToRadians
import com.burtwelding.fabapp.Calculator.convertRadiansToDegrees
import com.burtwelding.fabapp.Vector
import com.burtwelding.fabapp.storage.ItemHandle
import org.kimplify.deci.Deci
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan

@Serializable
class RightTriangle (val operand1: Operand,
                     val operand2: Operand): Item() {

    @Serializable
    sealed interface Operand {
        @Serializable
        data class Rise (val handle: ItemHandle<Length>): Operand
        @Serializable
        data class Run (val handle: ItemHandle<Length>): Operand
        @Serializable
        data class Diagonal (val handle: ItemHandle<Length>): Operand
        @Serializable
        data class TopAngle (val handle: ItemHandle<Angle>): Operand
        @Serializable
        data class BottomAngle (val handle: ItemHandle<Angle>): Operand
    }

    data class Calculation (
        val rise: Length,
        val run: Length,
        val diagonal: Length,
        val topAngle: Angle,
        val bottomAngle: Angle
    )

    init {
        require(operand1::class != operand2::class) { "Both operands are the same type! THE FUHHHHH" }
        if (operand1::class == Operand.TopAngle::class && operand2::class == Operand.BottomAngle::class)
            throw IllegalArgumentException("Cant have both Operands be angles! Pointless!")
        if (operand2::class == Operand.TopAngle::class && operand1::class == Operand.BottomAngle::class)
            throw IllegalArgumentException("bruh both operands are angles! whaaaaat?")
    }

    fun calculate () =
        Calculation(
            rise = getRise(),
            run = getRun(),
            diagonal = getDiagonal(),
            topAngle = getTopAngle(),
            bottomAngle = getBottomAngle()
        )

    fun drawFromTopCorner (rightTriangleCalculation: Calculation) =
        listOf(
            Vector(0.0, rightTriangleCalculation.rise.baseUnitDecimalValue.toDouble()),
            Vector(rightTriangleCalculation.run.baseUnitDecimalValue.toDouble(), 0.0)
        )
    fun drawFromTopCorner () = drawFromTopCorner(this.calculate())
    fun drawFromBottomCorner (rightTriangleCalculation: Calculation) =
        listOf(
            Vector(-rightTriangleCalculation.run.baseUnitDecimalValue.toDouble(), 0.0),
            Vector(0.0, -rightTriangleCalculation.rise.baseUnitDecimalValue.toDouble())
        )
    fun drawFromBottomCorner () = drawFromBottomCorner(this.calculate())

    private fun getRise (): Length {
        if (operand1 is Operand.Rise) return operand1.handle.item
        if (operand2 is Operand.Rise) return operand2.handle.item
        return when (operand1) {
            is Operand.Run -> when (operand2) {
                is Operand.BottomAngle -> getRise(operand1, operand2)
                is Operand.Diagonal -> getRise(operand1, operand2)
                is Operand.TopAngle -> getRise(operand1, operand2)
                is Operand.Run -> throw IllegalArgumentException("boff classes da run calss! da fuhhh")
            }
            is Operand.Diagonal -> when (operand2) {
                is Operand.Run -> getRise(operand2, operand1)
                is Operand.BottomAngle -> getRise(operand1, operand2)
                is Operand.TopAngle -> getRise(operand1, operand2)
                is Operand.Diagonal -> throw IllegalArgumentException("boff da classes diagaonsal! frfick!!")
            }
            is Operand.TopAngle -> when (operand2) {
                is Operand.BottomAngle -> throw IllegalArgumentException("bruh both operands are angles!")
                is Operand.Diagonal -> getRise(operand2, operand1)
                is Operand.Run -> getRise(operand2, operand1)
                is Operand.TopAngle -> throw IllegalArgumentException("both operands are top angles????")
            }
            is Operand.BottomAngle -> when (operand2) {
                is Operand.BottomAngle -> throw IllegalArgumentException("BOTH OPERANDS BOTTOM ANGS TFFFF")
                is Operand.Diagonal -> getRise(operand2, operand1)
                is Operand.Run -> getRise(operand2, operand1)
                is Operand.TopAngle -> throw IllegalArgumentException("bro stop u giving both angles as operands STAHP")
            }
        }
    }
    private fun getRun (): Length {
        if (operand1 is Operand.Run) return operand1.handle.item
        if (operand2 is Operand.Run) return operand2.handle.item
        return when (operand1) {
            is Operand.Rise -> when (operand2) {
                is Operand.BottomAngle -> getRun(operand1, operand2)
                is Operand.Diagonal -> getRun(operand1, operand2)
                is Operand.TopAngle -> getRun(operand1, operand2)
                is Operand.Rise -> throw IllegalArgumentException("boff classes da run calss! da fuhhh")
            }
            is Operand.Diagonal -> when (operand2) {
                is Operand.Rise -> getRun(operand2, operand1)
                is Operand.BottomAngle -> getRun(operand1, operand2)
                is Operand.TopAngle -> getRun(operand1, operand2)
                is Operand.Diagonal -> throw IllegalArgumentException("boff da classes diagaonsal! frfick!!")
            }
            is Operand.TopAngle -> when (operand2) {
                is Operand.BottomAngle -> throw IllegalArgumentException("bruh both operands are angles!")
                is Operand.Diagonal -> getRun(operand2, operand1)
                is Operand.Rise -> getRun(operand2, operand1)
                is Operand.TopAngle -> throw IllegalArgumentException("both operands are top angles????")
            }
            is Operand.BottomAngle -> when (operand2) {
                is Operand.BottomAngle -> throw IllegalArgumentException("BOTH OPERANDS BOTTOM ANGS TFFFF")
                is Operand.Diagonal -> getRun(operand2, operand1)
                is Operand.Rise -> getRun(operand2, operand1)
                is Operand.TopAngle -> throw IllegalArgumentException("bro stop u giving both angles as operands STAHP")
            }
        }
    }
    private fun getDiagonal(): Length {
        if (operand1 is Operand.Diagonal) return operand1.handle.item
        if (operand2 is Operand.Diagonal) return operand2.handle.item
        return when (operand1) {
            is Operand.Rise -> when (operand2) {
                is Operand.BottomAngle -> getDiagonal(operand1, operand2)
                is Operand.Run -> getDiagonal(operand1, operand2)
                is Operand.TopAngle -> getDiagonal(operand1, operand2)
                is Operand.Rise -> throw IllegalArgumentException("boff classes da run calss! da fuhhh")
            }
            is Operand.Run -> when (operand2) {
                is Operand.Rise -> getDiagonal(operand2, operand1)
                is Operand.BottomAngle -> getDiagonal(operand1, operand2)
                is Operand.TopAngle -> getDiagonal(operand1, operand2)
                is Operand.Run -> throw IllegalArgumentException("boff da classes diagaonsal! frfick!!")
            }
            is Operand.TopAngle -> when (operand2) {
                is Operand.BottomAngle -> throw IllegalArgumentException("bruh both operands are angles!")
                is Operand.Run -> getDiagonal(operand2, operand1)
                is Operand.Rise -> getDiagonal(operand2, operand1)
                is Operand.TopAngle -> throw IllegalArgumentException("both operands are top angles????")
            }
            is Operand.BottomAngle -> when (operand2) {
                is Operand.BottomAngle -> throw IllegalArgumentException("BOTH OPERANDS BOTTOM ANGS TFFFF")
                is Operand.Run -> getDiagonal(operand2, operand1)
                is Operand.Rise -> getDiagonal(operand2, operand1)
                is Operand.TopAngle -> throw IllegalArgumentException("bro stop u giving both angles as operands STAHP")
            }
        }
    }
    private fun getTopAngle (): Angle {
        if (operand1 is Operand.TopAngle) return operand1.handle.item
        if (operand2 is Operand.TopAngle) return operand2.handle.item
        return when (operand1) {
            is Operand.Rise -> when (operand2) {
                is Operand.BottomAngle -> getTopAngle(operand2)
                is Operand.Run -> getTopAngle(operand1, operand2)
                is Operand.Diagonal -> getTopAngle(operand1, operand2)
                is Operand.Rise -> throw IllegalArgumentException("boff classes da run calss! da fuhhh")
            }
            is Operand.Run -> when (operand2) {
                is Operand.Rise -> getTopAngle(operand2, operand1)
                is Operand.BottomAngle -> getTopAngle(operand2)
                is Operand.Diagonal -> getTopAngle(operand1, operand2)
                is Operand.Run -> throw IllegalArgumentException("boff da classes diagaonsal! frfick!!")
            }
            is Operand.Diagonal -> when (operand2) {
                is Operand.BottomAngle -> throw IllegalArgumentException("bruh both operands are angles!")
                is Operand.Run -> getTopAngle(operand2, operand1)
                is Operand.Rise -> getTopAngle(operand2, operand1)
                is Operand.Diagonal -> throw IllegalArgumentException("both operands are top angles????")
            }
            is Operand.BottomAngle -> getTopAngle(operand1)
        }
    }
    private fun getBottomAngle (): Angle {
        if (operand1 is Operand.BottomAngle) return operand1.handle.item
        if (operand2 is Operand.BottomAngle) return operand2.handle.item
        return when (operand1) {
            is Operand.Rise -> when (operand2) {
                is Operand.TopAngle -> getBottomAngle(operand2)
                is Operand.Run -> getBottomAngle(operand1, operand2)
                is Operand.Diagonal -> getBottomAngle(operand1, operand2)
                is Operand.Rise -> throw IllegalArgumentException("boff classes da run calss! da fuhhh")
            }
            is Operand.Run -> when (operand2) {
                is Operand.Rise -> getBottomAngle(operand2, operand1)
                is Operand.TopAngle -> getBottomAngle(operand2)
                is Operand.Diagonal -> getBottomAngle(operand1, operand2)
                is Operand.Run -> throw IllegalArgumentException("boff da classes diagaonsal! frfick!!")
            }
            is Operand.Diagonal -> when (operand2) {
                is Operand.TopAngle -> throw IllegalArgumentException("bruh both operands are angles!")
                is Operand.Run -> getBottomAngle(operand2, operand1)
                is Operand.Rise -> getBottomAngle(operand2, operand1)
                is Operand.Diagonal -> throw IllegalArgumentException("both operands are top angles????")
            }
            is Operand.TopAngle -> getBottomAngle(operand1)
        }
    }

    private fun getBottomAngle (topAngle: Operand.TopAngle) =
        Angle(Deci(90).minus(topAngle.handle.item.baseUnitDecimalValue))
    private fun getBottomAngle (rise: Operand.Rise, run: Operand.Run) =
        Angle(
            convertRadiansToDegrees(
                Deci(
                    tan(
                        rise.handle.item.baseUnitDecimalValue.div(run.handle.item.baseUnitDecimalValue)
                            .toDouble()
                    )
                )
            )
        )
    private fun getBottomAngle (rise: Operand.Rise, diagonal: Operand.Diagonal) =
        Angle(
            convertRadiansToDegrees(
                Deci(
                    sin(
                        rise.handle.item.baseUnitDecimalValue.div(diagonal.handle.item.baseUnitDecimalValue)
                            .toDouble()
                    )
                )
            )
        )
    private fun getBottomAngle (run: Operand.Run, diagonal: Operand.Diagonal) =
        Angle(
            convertRadiansToDegrees(
                Deci(
                    cos(
                        run.handle.item.baseUnitDecimalValue.div(diagonal.handle.item.baseUnitDecimalValue)
                            .toDouble()
                    )
                )
            )
        )
    private fun getTopAngle (rise: Operand.Rise, run: Operand.Run) =
        Angle(getBottomAngle(rise, run).baseUnitDecimalValue)
    private fun getTopAngle (rise: Operand.Rise, diagonal: Operand.Diagonal) =
        Angle(getBottomAngle(rise, diagonal).baseUnitDecimalValue)
    private fun getTopAngle (run: Operand.Run, diagonal: Operand.Diagonal) =
        Angle(getBottomAngle(run, diagonal).baseUnitDecimalValue)
    private fun getTopAngle (bottomAngle: Operand.BottomAngle) =
        Angle(Deci(90).minus(bottomAngle.handle.item.baseUnitDecimalValue))
    private fun getRise (run: Operand.Run, diagonal: Operand.Diagonal) =
        Length(
            Deci(
                sqrt(
                    (diagonal.handle.item.baseUnitDecimalValue.times(diagonal.handle.item.baseUnitDecimalValue)).minus(
                        run.handle.item.baseUnitDecimalValue.times(run.handle.item.baseUnitDecimalValue)
                    ).toDouble()
                )
            )
        )
    private fun getRise (run: Operand.Run, angle: Operand.BottomAngle) =
        Length(
            Deci(tan(convertDegreesToRadians(angle.handle.item.baseUnitDecimalValue).toDouble())).times(
                run.handle.item.baseUnitDecimalValue
            )
        )
    private fun getRise (run: Operand.Run, angle: Operand.TopAngle) =
        Length(
            Deci(
                tan(
                    Deci(90).minus(convertDegreesToRadians(angle.handle.item.baseUnitDecimalValue))
                        .toDouble()
                )
            ).times(run.handle.item.baseUnitDecimalValue)
        )
    private fun getRise (diagonal: Operand.Diagonal, angle: Operand.BottomAngle) =
        Length(
            Deci(sin(convertDegreesToRadians(angle.handle.item.baseUnitDecimalValue).toDouble())).times(
                diagonal.handle.item.baseUnitDecimalValue
            )
        )
    private fun getRise (diagonal: Operand.Diagonal, angle: Operand.TopAngle) =
        Length(
            Deci(sin(convertDegreesToRadians(Deci(90).minus(angle.handle.item.baseUnitDecimalValue)).toDouble())).times(
                diagonal.handle.item.baseUnitDecimalValue
            )
        )
    private fun getRun (rise: Operand.Rise, diagonal: Operand.Diagonal) =
        Length(
            Deci(
                sqrt(
                    (rise.handle.item.baseUnitDecimalValue.times(rise.handle.item.baseUnitDecimalValue)).minus(
                        diagonal.handle.item.baseUnitDecimalValue.times(diagonal.handle.item.baseUnitDecimalValue)
                    ).toDouble()
                )
            )
        )
    private fun getRun (rise: Operand.Rise, angle: Operand.BottomAngle) =
        Length(rise.handle.item.baseUnitDecimalValue.div(Deci(tan(convertDegreesToRadians(angle.handle.item.baseUnitDecimalValue).toDouble()))))
    private fun getRun (rise: Operand.Rise, angle: Operand.TopAngle) =
        Length(
            rise.handle.item.baseUnitDecimalValue.div(
                Deci(
                    tan(
                        convertDegreesToRadians(
                            Deci(90).minus(
                                angle.handle.item.baseUnitDecimalValue
                            )
                        ).toDouble()
                    )
                )
            )
        )
    private fun getRun (diagonal: Operand.Diagonal, angle: Operand.BottomAngle) =
        Length(
            Deci(cos(convertDegreesToRadians(angle.handle.item.baseUnitDecimalValue).toDouble())).times(
                diagonal.handle.item.baseUnitDecimalValue
            )
        )
    private fun getRun (diagonal: Operand.Diagonal, angle: Operand.TopAngle) =
        Length(
            Deci(
                cos(
                    Deci(90).minus(convertDegreesToRadians(angle.handle.item.baseUnitDecimalValue))
                        .toDouble()
                )
            ).times(diagonal.handle.item.baseUnitDecimalValue)
        )
    private fun getDiagonal (rise: Operand.Rise, run: Operand.Run) =
        Length(
            Deci(
                sqrt(
                    (rise.handle.item.baseUnitDecimalValue.times(rise.handle.item.baseUnitDecimalValue)).plus(
                        run.handle.item.baseUnitDecimalValue.times(run.handle.item.baseUnitDecimalValue)
                    ).toDouble()
                )
            )
        )
    private fun getDiagonal (rise: Operand.Rise, angle: Operand.BottomAngle) =
        Length(rise.handle.item.baseUnitDecimalValue.div(Deci(sin(convertDegreesToRadians(angle.handle.item.baseUnitDecimalValue).toDouble()))))
    private fun getDiagonal (rise: Operand.Rise, angle: Operand.TopAngle) =
        Length(
            rise.handle.item.baseUnitDecimalValue.div(
                Deci(
                    sin(
                        Deci(90).minus(
                            convertDegreesToRadians(angle.handle.item.baseUnitDecimalValue)
                        ).toDouble()
                    )
                )
            )
        )
    private fun getDiagonal (run: Operand.Run, angle: Operand.BottomAngle) =
        Length(run.handle.item.baseUnitDecimalValue.div(Deci(cos(convertDegreesToRadians(angle.handle.item.baseUnitDecimalValue).toDouble()))))
    private fun getDiagonal (run: Operand.Run, angle: Operand.TopAngle) =
        Length(
            run.handle.item.baseUnitDecimalValue.div(
                Deci(
                    cos(
                        Deci(90).minus(
                            convertDegreesToRadians(angle.handle.item.baseUnitDecimalValue)
                        ).toDouble()
                    )
                )
            )
        )
}