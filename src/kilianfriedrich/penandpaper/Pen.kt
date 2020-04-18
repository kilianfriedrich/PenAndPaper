@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package kilianfriedrich.penandpaper

import kilianfriedrich.penandpaper.util.toDeg
import kilianfriedrich.penandpaper.util.toRad
import java.awt.*
import java.awt.geom.AffineTransform
import java.awt.image.BufferedImage
import kotlin.math.*

/**
 * A `Pen` is used to draw on a paper. Each pen can only draw on one paper, but multiple pens can paint on a single paper.
 *
 * @param paper This field specifies which paper is affected by the pen's actions.
 */
class Pen(private val paper: Paper) {

    /**
     * The horizontal position of the pen (relative to upper left corner). Use [moveTo] or [moveBy] to change the value.
     *
     * @see moveTo
     * @see moveBy
     * @see yPos
     */
    var xPos: Double = 0.0
        private set(value) { field = round(value * 1_000) / 1_000.0 }

    /**
     * The vertical position of the pen (relative upper left corner). Use [moveTo] or [moveBy] to change the value.
     *
     * @see moveTo
     * @see moveBy
     * @see xPos
     */
    var yPos: Double = 0.0
        private set(value) { field = round(value * 1_000) / 1_000.0 }

    /**
     * The direction of the pen. This field may be changed directly or via [turnTo].
     */
    var direction: Double = 0.0  // 0 = right; 90 = down; 180 = left; 270 = up
        set(value) {
            var deg = value
            while(deg < 0) deg += 360
            deg %= 360
            field = round(deg * 1_000) / 1_000.0
        }

    /** The color of the pen. */
    var color = Color.BLACK!!
    
    /** The stroke's width - used by geometric forms, not by texts. */
    var strokeWidth = 2

    /**
     * The pen will only draw when this is true.
     * 
     * @suppress
     */
    private var down = false

    /** Return whether the pen draws (true) or not (false). */
    fun isDown() = down
    /** Sets the pen to drawing mode */
    fun down() { down = true }
    /** Disables drawing mode. The pen will move but won't draw anything on it's way. */
    fun up() { down = false }

    /**
     * This function moves the pen to a specific position and rotates it the correct way.
     * 
     * @param toX This parameter specifies the horizontal target position (relative to the upper left corner).
     * @param toY This parameter specifies the vertical target position (relative to the upper left corner).
     */
    fun moveTo(toX: Double, toY: Double) {

        turnTo(toX, toY)

        val fromX = xPos
        val fromY = yPos
        val col = color
        val thickness = BasicStroke(strokeWidth.toFloat())
        if(down) paper.addObj {
            it as Graphics2D
            it.stroke = thickness
            it.color = col
            it.drawLine(fromX.roundToInt(), fromY.roundToInt(), toX.roundToInt(), toY.roundToInt())
        }

        xPos = toX; yPos = toY

        paper.repaint()

    }

    /**
     * This function moves the pen along the current rotation.
     *
     * @param d This parameter specifies the distance to move.
     */
    fun moveBy(d: Int) = moveTo(
        xPos + d * cos(direction.toRad()),
        yPos + d * sin(direction.toRad())
    )

    /**
     * This function turns the pen so that it faces to a specific position.
     * 
     * @param x This parameter specifies the horizontal target position (relative to the upper left corner).
     * @param y This parameter specifies the vertical target position (relative to the upper left corner).
     * 
     * @see direction
     */
    fun turnTo(x: Double, y: Double) {

        if(y == yPos) {
            direction = if (x < xPos) 180.0 else 0.0
            return
        } else if(x == xPos) {
            direction = if (y < yPos) 270.0 else 90.0
            return
        }

        direction = atan(abs(y - yPos) / abs(x - xPos)).toDeg()

        if(x < xPos && y > yPos) direction = 180 - direction
        if(x < xPos && y < yPos) direction += 180
        if(x > xPos && y < yPos) direction = 360 - direction
    }

    /**
     * This function draws a circle. The current position of the pen will be the top of the circle.
     * The pen will end up in the same position as before.
     * 
     * @param rad The parameter specifies the radius of the circle.
     */
    fun drawCircle(rad: Int) {

        val topLeftX: Int
        val topLeftY: Int
        when {
            direction == 0.0 -> {
                topLeftX = (xPos - rad).roundToInt()
                topLeftY = (yPos).roundToInt()
            }
            0.0 < direction && direction < 90.0 -> {
                topLeftX = (xPos - sin(direction.toRad()) * rad - rad).roundToInt()
                topLeftY = (yPos + cos(direction.toRad()) * rad - rad).roundToInt()
            }
            direction == 90.0 -> {
                topLeftX = (xPos - 2 * rad).roundToInt()
                topLeftY = (yPos - rad).roundToInt()
            }
            90.0 < direction && direction < 180.0 -> {
                topLeftX = (xPos - cos((direction - 90).toRad()) * rad - rad).roundToInt()
                topLeftY = (yPos - sin((direction - 90).toRad()) * rad - rad).roundToInt()
            }
            direction == 180.0 -> {
                topLeftX = (xPos - rad).roundToInt()
                topLeftY = (yPos - 2 * rad).roundToInt()
            }
            180.0 < direction && direction < 270.0 -> {
                topLeftX = (xPos + sin((direction - 180).toRad()) * rad - rad).roundToInt()
                topLeftY = (yPos - cos((direction - 180).toRad()) * rad - rad).roundToInt()
            }
            direction == 270.0 -> {
                topLeftX = (xPos).roundToInt()
                topLeftY = (yPos - rad).roundToInt()
            }
            else -> {
                topLeftX = (xPos + cos((direction - 270).toRad()) * rad - rad).roundToInt()
                topLeftY = (yPos + sin((direction - 270).toRad()) * rad - rad).roundToInt()
            }
        }

        val col = color
        val thickness = BasicStroke(strokeWidth.toFloat())

        if(down) paper.addObj {

            it as Graphics2D
            it.stroke = thickness
            it.color = col

            it.drawOval(topLeftX,topLeftY,rad * 2,rad * 2)
        }

    }

    /**
     * This function draws a rectangle. The current position of the pen will be the upper left corner of the rectangle.
     * The pen will end up in the same position as before.
     * 
     * @param width This parameter specifies the rectangle's width.
     * @param height This parameter specifies the rectangle's height.
     */
    fun drawRect(width: Int, height: Int) {

        if(down) {

            moveBy(width); direction += 90
            moveBy(height); direction += 90
            moveBy(width); direction += 90
            moveBy(height); direction += 90

        }

    }

    /**
     * This function writes data on the paper.
     * 
     * @param obj This parameter specifies the object to write. This could be a [String] or anything else. [toString()][Object.toString] will be called.
     * @param style This parameter specifies the font's style. Defaults to [Font.PLAIN] when [strokeWidth] < 4 or [Font.BOLD] else.
     * @param size This parameter specifies the font's size. Defaults to 15.
     */
    fun write(obj: Any, style: Int = if(strokeWidth > 3) Font.BOLD else Font.PLAIN, size: Int = 15) {

        if(!down) return

        val text = obj.toString()
        val font = Font("Dialog", style, size)

        val rotationTransform = AffineTransform.getRotateInstance(direction.toRad())
        val rotatedFont = font.deriveFont(rotationTransform)

        val metrics = paper.getFontMetrics(font)
        val maxSize = sqrt(metrics.height.toDouble().pow(2) + metrics.stringWidth(text).toDouble().pow(2))

        val img = BufferedImage(ceil(maxSize * 2).toInt(), ceil(maxSize * 2).toInt(), BufferedImage.TYPE_INT_ARGB)
        val g = img.createGraphics()
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        g.color = color
        g.font = rotatedFont
        g.drawString(text, maxSize.roundToInt(), maxSize.roundToInt())

        val x = (xPos - maxSize).roundToInt()
        val y = (yPos - maxSize).roundToInt()

        paper.addObj {

            it.drawImage(img, x, y, null)

        }

        up()
        moveBy(metrics.stringWidth(text))
        down()

        paper.repaint()

    }

}