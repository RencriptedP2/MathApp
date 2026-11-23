package com.example.mathapp2.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mathapp2.util.EvalResult
import com.example.mathapp2.util.ExpressionEvaluator
import kotlin.math.sqrt

data class CalculationHistoryItem(
    val expression: String,
    val result: String
)

@Composable
fun CalculatorScreen(modifier: Modifier = Modifier) {
    var expression by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("0") }
    var lastEvaluated by remember { mutableStateOf(false) }

    // Memory State (MC, MR, M+, M-, MS)
    var memoryValue by remember { mutableStateOf<Double?>(null) }

    // State for local calculation history (max 50 operations)
    val history = remember { mutableStateListOf<CalculationHistoryItem>() }
    var showHistoryDialog by remember { mutableStateOf(false) }

    fun addHistoryItem(expr: String, res: String) {
        if (expr.isBlank() || res.startsWith("Error")) return
        history.add(0, CalculationHistoryItem(expression = expr, result = res))
        if (history.size > 50) {
            history.removeAt(history.size - 1)
        }
    }

    fun applyUnaryFunction(type: String) {
        val currentVal = (if (lastEvaluated) result else tryEvaluate(expression)).toDoubleOrNull()
            ?: (expression.toDoubleOrNull() ?: 0.0)

        val newResultVal = when (type) {
            "1/x" -> if (currentVal == 0.0) null else 1.0 / currentVal
            "x²" -> currentVal * currentVal
            "²√x" -> if (currentVal < 0.0) null else sqrt(currentVal)
            "+/-" -> -currentVal
            "%" -> currentVal / 100.0
            else -> currentVal
        }

        if (newResultVal == null) {
            result = if (type == "1/x") "Error (div entre 0)" else "Error (raíz negativa)"
            lastEvaluated = true
        } else {
            val formatted = ExpressionEvaluator.formatNum(newResultVal)
            expression = formatted
            result = formatted
            lastEvaluated = true
        }
    }

    fun onButtonClick(symbol: String) {
        when (symbol) {
            "C" -> {
                expression = ""
                result = "0"
                lastEvaluated = false
            }
            "CE" -> {
                expression = ""
                result = "0"
                lastEvaluated = false
            }
            "DEL" -> {
                if (expression.isNotEmpty()) {
                    expression = expression.dropLast(1)
                    result = if (expression.isEmpty()) "0" else tryEvaluate(expression)
                }
            }
            "=" -> {
                if (expression.isNotEmpty()) {
                    val finalResult = tryEvaluate(expression)
                    result = finalResult
                    addHistoryItem(expression, finalResult)
                    lastEvaluated = true
                }
            }
            "1/x", "x²", "²√x", "+/-", "%" -> {
                applyUnaryFunction(symbol)
            }
            "+", "-", "×", "÷" -> {
                if (lastEvaluated) {
                    expression = result
                    lastEvaluated = false
                }
                if (expression.isNotEmpty()) {
                    val lastChar = expression.last()
                    if (lastChar in listOf('+', '-', '×', '÷')) {
                        expression = expression.dropLast(1) + symbol
                    } else {
                        expression += symbol
                    }
                } else if (symbol == "-") {
                    expression = "-"
                }
            }
            else -> { // Numbers 0-9 and decimal point
                if (lastEvaluated) {
                    expression = symbol
                    lastEvaluated = false
                } else {
                    expression += symbol
                }
                result = tryEvaluate(expression)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Display Card containing History Button & Numbers Display
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Row: History Popup Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .clickable { showHistoryDialog = true }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = "Ver historial",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (history.isEmpty()) "📜 Historial" else "📜 Historial (${history.size})",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

                    if (memoryValue != null) {
                        Text(
                            text = "M: ${ExpressionEvaluator.formatNum(memoryValue!!)}",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Current Expression & Result Display Area
                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (expression.isNotEmpty()) {
                        Text(
                            text = expression,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.End
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                    Text(
                        text = if (expression.isEmpty()) "0" else result,
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.End
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Memory Functions Row (MC, MR, M+, M-, MS)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            MemoryButton("MC", enabled = memoryValue != null) { memoryValue = null }
            MemoryButton("MR", enabled = memoryValue != null) {
                memoryValue?.let {
                    val formatted = ExpressionEvaluator.formatNum(it)
                    expression = formatted
                    result = formatted
                    lastEvaluated = true
                }
            }
            MemoryButton("M+", enabled = true) {
                val curVal = result.toDoubleOrNull() ?: 0.0
                memoryValue = (memoryValue ?: 0.0) + curVal
            }
            MemoryButton("M-", enabled = true) {
                val curVal = result.toDoubleOrNull() ?: 0.0
                memoryValue = (memoryValue ?: 0.0) - curVal
            }
            MemoryButton("MS", enabled = true) {
                val curVal = result.toDoubleOrNull() ?: 0.0
                memoryValue = curVal
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Keypad Grid (Standard Windows Calculator Layout)
        val buttons = listOf(
            listOf("%", "CE", "C", "DEL"),
            listOf("1/x", "x²", "²√x", "÷"),
            listOf("7", "8", "9", "×"),
            listOf("4", "5", "6", "-"),
            listOf("1", "2", "3", "+"),
            listOf("+/-", "0", ".", "=")
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            buttons.forEach { row ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    row.forEach { symbol ->
                        KeypadButton(
                            symbol = symbol,
                            onClick = { onButtonClick(symbol) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }

    // Pop-up Dialog for Calculation History
    if (showHistoryDialog) {
        AlertDialog(
            onDismissRequest = { showHistoryDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Historial de operaciones",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    if (history.isEmpty()) {
                        Text(
                            text = "Sin historial aún. Las últimas 50 operaciones que calcules aparecerán aquí.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                    } else {
                        Text(
                            text = "Toca cualquier operación para cargarla (${history.size}/50):",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(8.dp))
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(260.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            items(history) { item ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                                        .clickable {
                                            expression = item.expression
                                            result = item.result
                                            lastEvaluated = true
                                            showHistoryDialog = false
                                        }
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = item.expression,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "= ${item.result}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showHistoryDialog = false }) {
                    Text("Cerrar", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                if (history.isNotEmpty()) {
                    TextButton(
                        onClick = { history.clear() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Borrar historial", color = MaterialTheme.colorScheme.error)
                    }
                }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }
}

@Composable
private fun MemoryButton(
    label: String,
    enabled: Boolean,
    onClick: () -> Unit
) {
    TextButton(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.textButtonColors(
            contentColor = if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
        )
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun KeypadButton(
    symbol: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isOperator = symbol in listOf("÷", "×", "-", "+")
    val isEquals = symbol == "="
    val isSpecialFunc = symbol in listOf("1/x", "x²", "²√x", "%", "CE", "C", "DEL", "+/-")

    when {
        isEquals -> {
            Button(
                onClick = onClick,
                modifier = modifier.height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(
                    text = symbol,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        symbol == "DEL" -> {
            OutlinedButton(
                onClick = onClick,
                modifier = modifier.height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    contentColor = MaterialTheme.colorScheme.onSurface
                )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Backspace,
                    contentDescription = "Borrar",
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        else -> {
            OutlinedButton(
                onClick = onClick,
                modifier = modifier.height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (isOperator || isSpecialFunc) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    contentColor = if (isOperator) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )
            ) {
                Text(
                    text = symbol,
                    fontSize = if (symbol.length > 2) 14.sp else 18.sp,
                    fontWeight = if (isOperator || isSpecialFunc) FontWeight.Bold else FontWeight.SemiBold
                )
            }
        }
    }
}

private fun tryEvaluate(expr: String): String {
    val trimmed = expr.trim()
    if (trimmed.isEmpty()) return "0"
    try {
        val res = ExpressionEvaluator.evaluate(trimmed, emptyMap())
        if (res is EvalResult.Success) {
            return res.formattedResult
        } else if (res is EvalResult.DivisionByZero) {
            return "Error (div entre 0)"
        }

        // For live preview while typing, strip trailing operators if incomplete expression
        var cleanExpr = trimmed
        while (cleanExpr.isNotEmpty() && cleanExpr.last() in listOf('+', '-', '×', '÷', '*', '/')) {
            cleanExpr = cleanExpr.dropLast(1).trim()
        }
        if (cleanExpr.isNotEmpty()) {
            val prevRes = ExpressionEvaluator.evaluate(cleanExpr, emptyMap())
            if (prevRes is EvalResult.Success) {
                return prevRes.formattedResult
            }
        }
    } catch (_: Exception) {
        // Fallback
    }
    return "0"
}
