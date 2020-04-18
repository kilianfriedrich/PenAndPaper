package kilianfriedrich.penandpaper.util

import kotlin.math.PI

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
