@file:Suppress("unused")

package kilianfriedrich.penandpaper

import kilianfriedrich.penandpaper.util.DialogueBox
import java.awt.*
import java.awt.event.*

/**
 * The `Paper` class represents a window. Everything that has been drawn will be displayed inside of a paper.
 *
 * @param width The `width` parameter describes the initial width of the paper in pixels. It defaults to 854.
 * @param height The `height` parameter describes the initial height of the paper in pixels. It defaults to 480.
 * @param title The `title` parameter stores the text that is shown in the title bar of the window. This text is also shown in [dialogue boxes][requestText]. It defaults to `Paper #(...)`.
 */
class Paper(width: Int = 854, height: Int = 480, title: String = "Paper #$paper") : Frame(title) {

    /**
     * A static field which counts the papers
     *
     * @suppress
     */
    companion object { var paper = 1 }

    /**
     * A custom canvas with Anti-Aliasing which uses [objects] to draw
     *
     * @suppress
     */
    internal class PaperCanvas(private val objects: List<(Graphics) -> Unit>): Canvas() {
        /** Stores the Anti-Aliasing configuration */
        private val rh = RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        
        /** Paints all the [objects] */
        override fun paint(g: Graphics) {
            super.paint(g)

            g as Graphics2D
            g.setRenderingHints(rh)

            g.color = background
            g.fillRect(0, 0, width, height)
            objects.forEach { it(g) }
        }
    }

    /**
     * List of pairs of keys & corresponding actions
     *
     * @suppress
     */
    private val specificListeners = mutableListOf<Pair<Int, () -> Unit>>()

    /**
     * A custom KeyListener execting these (see specificListeners) actions
     *
     * @suppress
     */
    private val keyListener: KeyListener = object: KeyListener {
        override fun keyTyped(e: KeyEvent?) { }
        override fun keyReleased(e: KeyEvent?) { }
        override fun keyPressed(e: KeyEvent) {
            specificListeners.forEach {
                if(it.first == e.keyCode) it.second()
            }
        }
    }

    /**
     * A list of methods that paint objects
     * 
     * @suppress
     */
    private val objects = mutableListOf<(Graphics) -> Unit>()

    /**
     * This function may be used to change the background color of the paper.
     *
     * @see getBackground This function may be used to receive the current background color of the paper.
     */
    override fun setBackground(bgColor: Color) {
        super.setBackground(bgColor)
        repaint()
    }

    /**
     * The constructor initializes the window by providing a default background and configuring internal fields.
     */
    init {
        paper++

        background = Color.WHITE

        val c = PaperCanvas(objects)
        c.preferredSize = Dimension(width, height)
        add(c)

        pack()
        addWindowListener(object: WindowAdapter() { override fun windowClosing(e: WindowEvent?) = dispose() })
        isVisible = true
    }

    /**
     * This function shows a dialogue box that accepts only integers. It returns the given integer after the dialogue box was closed.
     *
     * @param message The `message` parameter sets the text that is displayed inside of the dialogue box. It defaults to "(Paper name) needs an integer to continue".
     * @param title The `title` parameter sets the title of the dialogue box's window. It defaults to the paper's title.
     *
     * @see requestNumber This function shows a dialogue box that accepts only numbers. It returns the given number after the dialogue box was closed.
     * @see requestText This function shows a dialogue box that accepts any text. It returns the given text after the dialogue box was closed.
     */
    fun requestInteger(message: String = "${getTitle()} needs an integer to continue", title: String = getTitle())
            = DialogueBox(DialogueBox.Type.INT, this, message, title).input.text.toInt()

    /**
     * This function shows a dialogue box that accepts only numbers. It returns the given number after the dialogue box was closed.
     *
     * @param message The `message` parameter sets the text that is displayed inside of the dialogue box. It defaults to "(Paper name) needs a number to continue".
     * @param title The `title` parameter sets the title of the dialogue box's window. It defaults to the paper's title.
     *
     * @see requestInteger This function shows a dialogue box that accepts only integers. It returns the given integer after the dialogue box was closed.
     * @see requestText This function shows a dialogue box that accepts any text. It returns the given text after the dialogue box was closed.
     */
    private fun requestNumber(message: String = "${getTitle()} needs a number to continue", title: String = getTitle())
            = DialogueBox(DialogueBox.Type.DOUBLE, this, message, title).input.text.toDouble()

    /**
     * This function shows a dialogue box that accepts any text. It returns the given text after the dialogue box was closed.
     *
     * @param message The `message` parameter sets the text that is displayed inside of the dialogue box. It defaults to "(Paper name) needs a text to continue".
     * @param title The `title` parameter sets the title of the dialogue box's window. It defaults to the paper's title.
     *
     * @see requestInteger This function shows a dialogue box that accepts only integers. It returns the given integer after the dialogue box was closed.
     * @see requestNumber This function shows a dialogue box that accepts only numbers. It returns the given number after the dialogue box was closed.
     */
    private fun requestText(message: String = "${getTitle()} needs a text to continue", title: String = getTitle())
            = DialogueBox(DialogueBox.Type.STRING, this, message, title).input.text!!

    /** This field holds the x-position of the mouse cursor relative to the left upper edge of the window's content */
    val mouseX: Int by object { operator fun getValue(a: Any, b: Any) = mousePosition.x }
    /** This field holds the y-position of the mouse cursor relative to the left upper edge of the window's content */
    val mouseY: Int by object { operator fun getValue(a: Any, b: Any) = mousePosition.y }

    /** This function closes the paper. */
    fun close() = dispose()
    /** This function clears the paper's content. */
    fun clear() = objects.clear()
    /** This function tries to bring the paper into the foreground. */
    fun focus() = requestFocus()

    /**
     * Adds a listener for a specific key.
     *
     * @param key This parameter should be some Constant from the [KeyEvent] class, e.g. [KeyEvent.VK_SPACE].
     * @param action This parameter defines the function which is called on a key press.
     */
    fun addKeyListener(key: Int, action: () -> Unit) = specificListeners.add(Pair(key, action))

    /**
     * Adds an [object][objects].
     *
     * @suppress
     */
    internal fun addObj(obj: (Graphics) -> Unit) = objects.add(obj)

}