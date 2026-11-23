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
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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

enum class UnitCategory(val displayName: String) {
    LENGTH("Longitud"),
    WEIGHT("Peso"),
    TEMPERATURE("Temperatura")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnitConverterScreen(modifier: Modifier = Modifier) {
    var selectedCategory by remember { mutableStateOf(UnitCategory.LENGTH) }
    var inputValue by remember { mutableStateOf("1") }

    val units = when (selectedCategory) {
        UnitCategory.LENGTH -> listOf("Metros (m)", "Kilómetros (km)", "Centímetros (cm)", "Milímetros (mm)", "Pulgadas (in)", "Pies (ft)")
        UnitCategory.WEIGHT -> listOf("Kilogramos (kg)", "Gramos (g)", "Miligramos (mg)", "Libras (lb)", "Onzas (oz)")
        UnitCategory.TEMPERATURE -> listOf("Celsius (°C)", "Fahrenheit (°F)", "Kelvin (K)")
    }

    var fromUnit by remember(selectedCategory) { mutableStateOf(units[0]) }
    var toUnit by remember(selectedCategory) { mutableStateOf(units.getOrElse(1) { units[0] }) }

    var fromExpanded by remember { mutableStateOf(false) }
    var toExpanded by remember { mutableStateOf(false) }

    val convertedValue = calculateConversion(
        value = inputValue.toDoubleOrNull() ?: 0.0,
        category = selectedCategory,
        fromUnit = fromUnit,
        toUnit = toUnit
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Category Selection Chips
        Text(
            text = "Selecciona Categoría",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            UnitCategory.entries.forEach { category ->
                FilterChip(
                    selected = category == selectedCategory,
                    onClick = { selectedCategory = category },
                    label = { Text(category.displayName) }
                )
            }
        }

        // Input Value Field
        OutlinedTextField(
            value = inputValue,
            onValueChange = { inputValue = it },
            label = { Text("Valor de entrada") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        // Dropdowns Row
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            // From Unit Dropdown
            ExposedDropdownMenuBox(
                expanded = fromExpanded,
                onExpandedChange = { fromExpanded = !fromExpanded },
                modifier = Modifier.weight(1f)
            ) {
                OutlinedTextField(
                    value = fromUnit,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("De") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = fromExpanded) },
                    modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                    shape = RoundedCornerShape(12.dp)
                )
                ExposedDropdownMenu(
                    expanded = fromExpanded,
                    onDismissRequest = { fromExpanded = false }
                ) {
                    units.forEach { unit ->
                        DropdownMenuItem(
                            text = { Text(unit) },
                            onClick = {
                                fromUnit = unit
                                fromExpanded = false
                            }
                        )
                    }
                }
            }

            // To Unit Dropdown
            ExposedDropdownMenuBox(
                expanded = toExpanded,
                onExpandedChange = { toExpanded = !toExpanded },
                modifier = Modifier.weight(1f)
            ) {
                OutlinedTextField(
                    value = toUnit,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("A") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = toExpanded) },
                    modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                    shape = RoundedCornerShape(12.dp)
                )
                ExposedDropdownMenu(
                    expanded = toExpanded,
                    onDismissRequest = { toExpanded = false }
                ) {
                    units.forEach { unit ->
                        DropdownMenuItem(
                            text = { Text(unit) },
                            onClick = {
                                toUnit = unit
                                toExpanded = false
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Result Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Resultado Convertido",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "$inputValue $fromUnit =",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.9f)
                )
                Text(
                    text = "$convertedValue $toUnit",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

private fun calculateConversion(
    value: Double,
    category: UnitCategory,
    fromUnit: String,
    toUnit: String
): String {
    val result = when (category) {
        UnitCategory.LENGTH -> {
            val inMeters = when {
                fromUnit.startsWith("Metros") -> value
                fromUnit.startsWith("Kilómetros") -> value * 1000.0
                fromUnit.startsWith("Centímetros") -> value / 100.0
                fromUnit.startsWith("Milímetros") -> value / 1000.0
                fromUnit.startsWith("Pulgadas") -> value * 0.0254
                fromUnit.startsWith("Pies") -> value * 0.3048
                else -> value
            }
            when {
                toUnit.startsWith("Metros") -> inMeters
                toUnit.startsWith("Kilómetros") -> inMeters / 1000.0
                toUnit.startsWith("Centímetros") -> inMeters * 100.0
                toUnit.startsWith("Milímetros") -> inMeters * 1000.0
                toUnit.startsWith("Pulgadas") -> inMeters / 0.0254
                toUnit.startsWith("Pies") -> inMeters / 0.3048
                else -> inMeters
            }
        }
        UnitCategory.WEIGHT -> {
            val inGrams = when {
                fromUnit.startsWith("Kilogramos") -> value * 1000.0
                fromUnit.startsWith("Gramos") -> value
                fromUnit.startsWith("Miligramos") -> value / 1000.0
                fromUnit.startsWith("Libras") -> value * 453.59237
                fromUnit.startsWith("Onzas") -> value * 28.349523125
                else -> value
            }
            when {
                toUnit.startsWith("Kilogramos") -> inGrams / 1000.0
                toUnit.startsWith("Gramos") -> inGrams
                toUnit.startsWith("Miligramos") -> inGrams * 1000.0
                toUnit.startsWith("Libras") -> inGrams / 453.59237
                toUnit.startsWith("Onzas") -> inGrams / 28.349523125
                else -> inGrams
            }
        }
        UnitCategory.TEMPERATURE -> {
            val inCelsius = when {
                fromUnit.startsWith("Celsius") -> value
                fromUnit.startsWith("Fahrenheit") -> (value - 32.0) * (5.0 / 9.0)
                fromUnit.startsWith("Kelvin") -> value - 273.15
                else -> value
            }
            when {
                toUnit.startsWith("Celsius") -> inCelsius
                toUnit.startsWith("Fahrenheit") -> (inCelsius * 9.0 / 5.0) + 32.0
                toUnit.startsWith("Kelvin") -> inCelsius + 273.15
                else -> inCelsius
            }
        }
    }

    return if (result % 1.0 == 0.0 && !result.isInfinite()) {
        result.toLong().toString()
    } else {
        String.format(Locale.US, "%.4f", result).trimEnd('0').trimEnd('.')
    }
}
