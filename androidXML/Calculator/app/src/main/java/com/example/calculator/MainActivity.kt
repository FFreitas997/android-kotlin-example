package com.example.calculator

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.text.NumberFormat
import java.util.Locale
import java.util.Stack

class MainActivity : AppCompatActivity() {

    private var textView: TextView? = null
    private var finalReached = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        textView = findViewById<TextView>(R.id.result)

        findViewById<Button>(R.id.button15)
            .setOnClickListener { onNumberClick(it) }
        findViewById<Button>(R.id.button16)
            .setOnClickListener { onNumberClick(it) }
        findViewById<Button>(R.id.button17)
            .setOnClickListener { onNumberClick(it) }
        findViewById<Button>(R.id.button18)
            .setOnClickListener { onOperatorClick(it) }
        findViewById<Button>(R.id.button21)
            .setOnClickListener { onNumberClick(it) }
        findViewById<Button>(R.id.button20)
            .setOnClickListener { onNumberClick(it) }
        findViewById<Button>(R.id.button19)
            .setOnClickListener { onNumberClick(it) }
        findViewById<Button>(R.id.button23)
            .setOnClickListener { onOperatorClick(it) }
        findViewById<Button>(R.id.button28)
            .setOnClickListener { onNumberClick(it) }
        findViewById<Button>(R.id.button27)
            .setOnClickListener { onNumberClick(it) }
        findViewById<Button>(R.id.button25)
            .setOnClickListener { onNumberClick(it) }
        findViewById<Button>(R.id.button24)
            .setOnClickListener { onOperatorClick(it) }
        findViewById<Button>(R.id.button32)
            .setOnClickListener { onNumberClick(it) }
        findViewById<Button>(R.id.button29)
            .setOnClickListener { onClearClick(it) }
        findViewById<Button>(R.id.button31)
            .setOnClickListener { onOperatorClick(it) }
        findViewById<Button>(R.id.button33)
            .setOnClickListener { onEqualClick(it) }
    }

    fun onNumberClick(view: View) {
        if (finalReached) {
            textView?.text = ""
            finalReached = false
        }
        textView?.append((view as Button).text)
    }

    fun onOperatorClick(view: View) {
        if (textView?.text?.isEmpty() == true) {
            return
        }
        if (textView?.text?.last() == ' ') {
            return
        }
        textView?.append(" " + (view as Button).text + " ")
    }

    fun onClearClick(view: View) {
        textView?.text = ""
    }

    fun onEqualClick(view: View) {
        val expression = textView?.text.toString()
        val result = evaluateExpression(expression).toInt()
        textView?.append("\n${(view as Button).text} $result")
        finalReached = true
    }


    fun evaluateExpression(expression: String): Double {
        val tokens = expression.replace(" ", "").toCharArray()
        val values = Stack<Double>()
        val ops = Stack<Char>()

        var i = 0
        while (i < tokens.size) {
            if (tokens[i] in '0'..'9' || tokens[i] == '.') {
                val sb = StringBuilder()
                while (i < tokens.size && (tokens[i] in '0'..'9' || tokens[i] == '.')) {
                    sb.append(tokens[i++])
                }
                values.push(sb.toString().toDouble())
                i--
            } else if (tokens[i] == '(') {
                ops.push(tokens[i])
            } else if (tokens[i] == ')') {
                while (ops.peek() != '(') {
                    values.push(applyOp(ops.pop(), values.pop(), values.pop()))
                }
                ops.pop()
            } else if (tokens[i] in arrayOf('+', '-', 'X', '/')) {
                while (!ops.isEmpty() && hasPrecedence(tokens[i], ops.peek())) {
                    values.push(applyOp(ops.pop(), values.pop(), values.pop()))
                }
                ops.push(tokens[i])
            }
            i++
        }

        while (!ops.isEmpty()) {
            values.push(applyOp(ops.pop(), values.pop(), values.pop()))
        }

        return values.pop()
    }

    fun applyOp(op: Char, b: Double, a: Double): Double {
        return when (op) {
            '+' -> a + b
            '-' -> a - b
            'X' -> a * b
            '/' -> {
                if (b == 0.0) throw UnsupportedOperationException("Cannot divide by zero")
                a / b
            }

            else -> 0.0
        }
    }

    fun hasPrecedence(op1: Char, op2: Char): Boolean {
        if (op2 == '(' || op2 == ')') return false
        if ((op1 == 'X' || op1 == '/') && (op2 == '+' || op2 == '-')) return false
        return true
    }
}