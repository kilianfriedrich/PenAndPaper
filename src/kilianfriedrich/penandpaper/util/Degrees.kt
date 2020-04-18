package kilianfriedrich.penandpaper.util

import kilianfriedrich.penandpaper.Paper
import kilianfriedrich.penandpaper.Pen
import java.awt.Color
import kotlin.math.PI

fun main() {

    val p = Paper()
    val pen = Pen(p)
    pen.moveTo(200.0, 200.0)
    pen.turnTo(400.0, 400.0)
    pen.down()
    pen.write("HELLO WORLD")

}

/**
 * Converts DEG to RAD
 *
 * @return a degree between 0 and 2PI
 * @suppress
 */
internal fun Double.toRad() = (this * PI / 180) % (2 * PI)

/**
 *  Converts RAD to DEG
 *
 *  @return a degree between 0 and 360
 *  @suppress
 */
internal fun Double.toDeg() = (this * 180 / PI) % 360
