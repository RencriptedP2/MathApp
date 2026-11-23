package com.example.mathapp2.util

import java.util.Locale

sealed class EvalResult {
    data class Success(
        val resultValue: Double,
        val formattedResult: String,
        val stepByStep: String
    ) : EvalResult()

    data class DivisionByZero(
        val divisorExpression: String,
        val zeroVarName: String?,
        val message: String
    ) : EvalResult()

    data class Error(val message: String) : EvalResult()
}

object ExpressionEvaluator {

    /**
     * Extracts all variable names (alphabetic identifiers like a, b, x, y) from an expression string.
     */
    fun extractVariables(expr: String): List<String> {
        val regex = Regex("[a-zA-Z_][a-zA-Z0-9_]*")
        return regex.findAll(expr)
            .map { it.value }
            .distinct()
            .toList()
    }

    /**
     * Evaluates a mathematical expression given variable values.
     */
    fun evaluate(
        expression: String,
        variables: Map<String, Double>
    ): EvalResult {
        val trimmed = expression.trim()
        if (trimmed.isEmpty()) {
            return EvalResult.Error("La expresión está vacía")
        }

        // Check for undefined variables
        val requiredVars = extractVariables(trimmed)
        val missing = requiredVars.filter { !variables.containsKey(it) }
        if (missing.isNotEmpty()) {
            return EvalResult.Error("Falta el valor para la variable: ${missing.joinToString(", ")}")
        }

        // Build substituted expression string for steps display
        var substitutedExpr = trimmed
        // Replace variables in descending order of length to avoid partial matches
        val sortedVars = variables.entries.sortedByDescending { it.key.length }
        for ((name, value) in sortedVars) {
            val formattedVal = formatNum(value)
            // Use word boundary regex for variable replacement
            val varRegex = Regex("\\b$name\\b")
            substitutedExpr = varRegex.replace(substitutedExpr, formattedVal)
        }

        try {
            val tokens = tokenize(substitutedExpr)
            val parser = Parser(tokens)
            val node = parser.parse()

            // Evaluate tree
            val value = node.evaluate()

            if (value.isInfinite() || value.isNaN()) {
                // Determine division by zero details
                val zeroVar = variables.entries.firstOrNull { it.value == 0.0 }?.key
                return EvalResult.DivisionByZero(
                    divisorExpression = zeroVar ?: "el divisor",
                    zeroVarName = zeroVar,
                    message = if (zeroVar != null) {
                        "En $trimmed, el divisor es $zeroVar. Cambia su valor por un número distinto de 0 y vuelve a calcular."
                    } else {
                        "No se puede dividir entre cero. Revisa los divisores en la expresión."
                    }
                )
            }

            val formattedRes = formatNum(value)
            val steps = if (substitutedExpr != trimmed) {
                "$substitutedExpr = $formattedRes"
            } else {
                "$trimmed = $formattedRes"
            }

            return EvalResult.Success(
                resultValue = value,
                formattedResult = formattedRes,
                stepByStep = steps
            )
        } catch (e: DivisionByZeroException) {
            return EvalResult.DivisionByZero(
                divisorExpression = e.expression,
                zeroVarName = e.varName,
                message = e.message
            )
        } catch (e: Exception) {
            return EvalResult.Error(e.message ?: "Expresión matemática inválida")
        }
    }

    fun formatNum(value: Double): String {
        return if (value % 1.0 == 0.0 && !value.isInfinite()) {
            value.toLong().toString()
        } else {
            String.format(Locale.US, "%.4f", value).trimEnd('0').trimEnd('.')
        }
    }

    private fun tokenize(expr: String): List<Token> {
        val tokens = mutableListOf<Token>()
        var i = 0
        val len = expr.length

        while (i < len) {
            val c = expr[i]
            when {
                c.isWhitespace() -> i++
                c.isDigit() || c == '.' -> {
                    val sb = StringBuilder()
                    while (i < len && (expr[i].isDigit() || expr[i] == '.')) {
                        sb.append(expr[i])
                        i++
                    }
                    val num = sb.toString().toDoubleOrNull()
                        ?: throw IllegalArgumentException("Número inválido: ${sb.toString()}")
                    tokens.add(Token.Number(num))
                }
                c == '+' -> { tokens.add(Token.Operator('+')); i++ }
                c == '-' -> {
                    // Check if unary minus
                    val isUnary = tokens.isEmpty() || tokens.last() is Token.Operator || tokens.last() == Token.LParen
                    if (isUnary) {
                        tokens.add(Token.UnaryMinus)
                    } else {
                        tokens.add(Token.Operator('-'))
                    }
                    i++
                }
                c == '*' || c == '×' -> { tokens.add(Token.Operator('*')); i++ }
                c == '/' || c == '÷' -> { tokens.add(Token.Operator('/')); i++ }
                c == '%' -> { tokens.add(Token.Operator('%')); i++ }
                c == '^' -> { tokens.add(Token.Operator('^')); i++ }
                c == '(' -> { tokens.add(Token.LParen); i++ }
                c == ')' -> { tokens.add(Token.RParen); i++ }
                else -> throw IllegalArgumentException("Carácter no reconocido: '$c'")
            }
        }
        return tokens
    }

    private sealed class Token {
        data class Number(val value: Double) : Token()
        data class Operator(val op: Char) : Token()
        object UnaryMinus : Token()
        object LParen : Token()
        object RParen : Token()
    }

    private class DivisionByZeroException(
        val expression: String,
        val varName: String?,
        override val message: String
    ) : Exception(message)

    private interface ASTNode {
        fun evaluate(): Double
    }

    private class NumNode(val value: Double) : ASTNode {
        override fun evaluate(): Double = value
    }

    private class UnaryMinusNode(val operand: ASTNode) : ASTNode {
        override fun evaluate(): Double = -operand.evaluate()
    }

    private class BinaryOpNode(val left: ASTNode, val op: Char, val right: ASTNode) : ASTNode {
        override fun evaluate(): Double {
            val l = left.evaluate()
            val r = right.evaluate()
            return when (op) {
                '+' -> l + r
                '-' -> l - r
                '*' -> l * r
                '/' -> {
                    if (r == 0.0) {
                        throw DivisionByZeroException(
                            expression = "división",
                            varName = null,
                            message = "No se puede dividir entre cero."
                        )
                    }
                    l / r
                }
                '%' -> {
                    if (r == 0.0) throw DivisionByZeroException("módulo", null, "No se puede dividir entre cero.")
                    l % r
                }
                '^' -> Math.pow(l, r)
                else -> throw IllegalArgumentException("Operador desconocido: $op")
            }
        }
    }

    private class Parser(private val tokens: List<Token>) {
        private var pos = 0

        fun parse(): ASTNode {
            val node = parseExpression()
            if (pos < tokens.size) {
                throw IllegalArgumentException("Sintaxis inesperada cerca de token: ${tokens[pos]}")
            }
            return node
        }

        private fun parseExpression(): ASTNode {
            var node = parseTerm()
            while (pos < tokens.size) {
                val tok = tokens[pos]
                if (tok is Token.Operator && (tok.op == '+' || tok.op == '-')) {
                    pos++
                    val right = parseTerm()
                    node = BinaryOpNode(node, tok.op, right)
                } else {
                    break
                }
            }
            return node
        }

        private fun parseTerm(): ASTNode {
            var node = parseFactor()
            while (pos < tokens.size) {
                val tok = tokens[pos]
                if (tok is Token.Operator && (tok.op == '*' || tok.op == '/' || tok.op == '%')) {
                    pos++
                    val right = parseFactor()
                    node = BinaryOpNode(node, tok.op, right)
                } else {
                    break
                }
            }
            return node
        }

        private fun parseFactor(): ASTNode {
            var node = parsePrimary()
            while (pos < tokens.size) {
                val tok = tokens[pos]
                if (tok is Token.Operator && tok.op == '^') {
                    pos++
                    val right = parsePrimary()
                    node = BinaryOpNode(node, '^', right)
                } else {
                    break
                }
            }
            return node
        }

        private fun parsePrimary(): ASTNode {
            if (pos >= tokens.size) {
                throw IllegalArgumentException("Expresión incompleta")
            }
            val tok = tokens[pos++]
            return when (tok) {
                is Token.Number -> NumNode(tok.value)
                is Token.UnaryMinus -> UnaryMinusNode(parsePrimary())
                is Token.LParen -> {
                    val node = parseExpression()
                    if (pos >= tokens.size || tokens[pos] != Token.RParen) {
                        throw IllegalArgumentException("Falta paréntesis de cierre ')'")
                    }
                    pos++ // consume ')'
                    node
                }
                else -> throw IllegalArgumentException("Token inesperado: $tok")
            }
        }
    }
}
