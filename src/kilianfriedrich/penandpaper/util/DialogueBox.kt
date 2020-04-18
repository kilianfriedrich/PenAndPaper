package kilianfriedrich.penandpaper.util

import kilianfriedrich.penandpaper.Paper
import java.awt.*

/**
 * A dialogue window which is used to request numbers & text
 *
 * @param type One of [Type.STRING, Type.INT and Type.DOUBLE][DialogueBox.Type] - describes the expected result of the dialogue
 * @param paper The parent window (may be null) - can't be in focus until the dialogue ended
 * @param message The message in the dialogue window
 * @param title The title of the dialogue window
 *
 * @suppress
 */
internal class DialogueBox(type: Type, paper: Paper?, message: String, title: String) : Dialog(paper, title, true) {

    /** Describes the expected result of the dialogue */
    enum class Type {
        /** The dialogue window will return a String */ STRING,
        /** The dialogue window will return an Int */ INT,
        /** The dialogue window will return a Double*/ DOUBLE
    }

    /** A [TextField] with a [built-in undo action][restore]. Used to undo unsupported inputs in Number-only inputs */
    class UndoableTextField: TextField() {

        /** Stores the last content */
        private var last: String? = null

        /** Stores the current content */
        private var cur: String? = null

        /** Adds a TextListener to the Input to track changes */
        init { addTextListener { last = cur; cur = text } }

        /** Restores the Input using [last] */
        fun restore() { text = last }
    }

    /** The [UndoableTextField] inside the dialogue window */
    val input = UndoableTextField()

    /** Generates/Initializes + & - buttons of Integer & Double dialogue windows */
    private fun generateNumInput(): Pair<Button, Button> {
        val minus = Button("-")
        val plus = Button("+")

        minus.preferredSize = Dimension(30, 30)
        plus.preferredSize = Dimension(30, 30)

        add(minus, BorderLayout.WEST)
        add(plus, BorderLayout.EAST)

        return Pair(minus, plus)
    }

    /**
     * Generates/Initializes the input field of the dialogue window
     *
     * @param type The type of the dialogue box - affects mainly whether + & - buttons are added
     */
    private fun generateInput(type: Type) {

        when(type) {

            Type.INT -> {
                input.text = "0"
                input.addTextListener { if(input.text != "" && input.text != "." && input.text.toIntOrNull() == null) input.restore() }

                val buttons = generateNumInput()
                buttons.first.addActionListener { input.text = if(input.text != "") (input.text.toInt() - 1).toString() else "-1" }
                buttons.second.addActionListener { input.text = if(input.text != "") (input.text.toInt() + 1).toString() else "1" }
            }

            Type.DOUBLE -> {
                input.text = "0.0"
                input.addTextListener { if(input.text != "" && input.text != "." && input.text.toDoubleOrNull() == null) input.restore() }

                val buttons = generateNumInput()
                buttons.first.addActionListener { input.text = if(input.text != "" && input.text != ".") (input.text.toDouble() - 0.5).toString() else "-0.5" }
                buttons.second.addActionListener { input.text = if(input.text != "" && input.text != ".") (input.text.toDouble() + 0.5).toString() else "0.5" }
            }

            else -> {}
        }

        input.preferredSize = Dimension(300, 30)
        add(input, BorderLayout.CENTER)

        input.addActionListener { quit(type) }

    }

    /** Generates basic UI elements and the window */
    init {

        val lbl = Label(message, Label.CENTER)
        lbl.preferredSize = Dimension(360, 30)
        add(lbl, BorderLayout.NORTH)

        val ok = Button("Send")
        add(ok, BorderLayout.SOUTH)
        ok.addActionListener { quit(type) }

        generateInput(type)

        pack()
        isResizable = false
        isVisible = true

        input.requestFocus()
    }

    /** Closes the window */
    private fun quit(t: Type) {

        if(t != Type.STRING && (input.text == "" || input.text == "."))
            input.text = "0"

        dispose()

    }

}