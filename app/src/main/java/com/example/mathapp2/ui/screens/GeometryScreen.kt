package com.example.mathapp2.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import java.util.Locale
import kotlin.math.PI

enum class ShapeType(val displayName: String) {
    RECTANGLE("Rectángulo"),
    CIRCLE("Círculo"),
    TRIANGLE("Triángulo")
}

@Composable
fun GeometryScreen(modifier: Modifier = Modifier) {
    var selectedShape by remember { mutableStateOf(ShapeType.RECTANGLE) }

    var length by remember { mutableStateOf("5") }
    var width by remember { mutableStateOf("3") }

    var radius by remember { mutableStateOf("4") }

    var base by remember { mutableStateOf("6") }
    var height by remember { mutableStateOf("4") }
    var sideA by remember { mutableStateOf("5") }
    var sideC by remember { mutableStateOf("5") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Selecciona Figura",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            ShapeType.entries.forEach { shape ->
                FilterChip(
                    selected = shape == selectedShape,
                    onClick = { selectedShape = shape },
                    label = { Text(shape.displayName) }
                )
            }
        }

        when (selectedShape) {
            ShapeType.RECTANGLE -> {
                OutlinedTextField(
                    value = length,
                    onValueChange = { length = it },
                    label = { Text("Longitud (l)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = width,
                    onValueChange = { width = it },
                    label = { Text("Ancho (w)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                val lVal = length.toDoubleOrNull() ?: 0.0
                val wVal = width.toDoubleOrNull() ?: 0.0
                val area = lVal * wVal
                val perimeter = 2 * (lVal + wVal)

                GeometryResultCard(
                    formulaText = "Área = l × w  |  Perímetro = 2(l + w)",
                    areaText = formatNumber(area),
                    perimeterLabel = "Perímetro",
                    perimeterText = formatNumber(perimeter)
                )
            }
            ShapeType.CIRCLE -> {
                OutlinedTextField(
                    value = radius,
                    onValueChange = { radius = it },
                    label = { Text("Radio (r)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                val rVal = radius.toDoubleOrNull() ?: 0.0
                val area = PI * rVal * rVal
                val circumference = 2 * PI * rVal

                GeometryResultCard(
                    formulaText = "Área = πr²  |  Circunferencia = 2πr",
                    areaText = formatNumber(area),
                    perimeterLabel = "Circunferencia",
                    perimeterText = formatNumber(circumference)
                )
            }
            ShapeType.TRIANGLE -> {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = base,
                        onValueChange = { base = it },
                        label = { Text("Base (b)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = height,
                        onValueChange = { height = it },
                        label = { Text("Altura (h)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Text(
                    text = "Longitud de lados (para calcular el perímetro)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = sideA,
                        onValueChange = { sideA = it },
                        label = { Text("Lado A") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = base,
                        onValueChange = { base = it },
                        label = { Text("Base (B)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = sideC,
                        onValueChange = { sideC = it },
                        label = { Text("Lado C") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                val bVal = base.toDoubleOrNull() ?: 0.0
                val hVal = height.toDoubleOrNull() ?: 0.0
                val aVal = sideA.toDoubleOrNull() ?: 0.0
                val cVal = sideC.toDoubleOrNull() ?: 0.0

                val area = 0.5 * bVal * hVal
                val perimeter = aVal + bVal + cVal

                GeometryResultCard(
                    formulaText = "Área = ½ × b × h  |  Perímetro = a + b + c",
                    areaText = formatNumber(area),
                    perimeterLabel = "Perímetro",
                    perimeterText = formatNumber(perimeter)
                )
            }
        }
    }
}

@Composable
private fun GeometryResultCard(
    formulaText: String,
    areaText: String,
    perimeterLabel: String,
    perimeterText: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Fórmulas: $formulaText",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Área calculada",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                    Text(
                        text = areaText,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Column {
                    Text(
                        text = perimeterLabel,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                    Text(
                        text = perimeterText,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}

private fun formatNumber(value: Double): String {
    return if (value % 1.0 == 0.0 && !value.isInfinite()) {
        value.toLong().toString()
    } else {
        String.format(Locale.US, "%.4f", value).trimEnd('0').trimEnd('.')
    }
}
