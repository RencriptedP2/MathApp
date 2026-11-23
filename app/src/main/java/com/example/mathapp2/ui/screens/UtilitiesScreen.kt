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
import java.math.BigInteger
import java.util.Locale

@Composable
fun UtilitiesScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        PrimeCheckerCard()
        FactorialCard()
        GcdLcmCard()
        ListStatisticsCard()
    }
}

@Composable
private fun PrimeCheckerCard() {
    var inputStr by remember { mutableStateOf("17") }

    val num = inputStr.toLongOrNull()
    val resultText = when {
        num == null -> "Ingresa un número entero válido"
        num <= 1 -> "$num NO es primo"
        else -> {
            val isPrime = checkIsPrime(num)
            if (isPrime) {
                "✅ ¡$num es un Número Primo!"
            } else {
                val factors = getFactors(num)
                "❌ $num NO es primo (Factores: ${factors.joinToString(", ")})"
            }
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "1. Comprobador de Números Primos",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = inputStr,
                onValueChange = { inputStr = it },
                label = { Text("Ingresa número entero") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = resultText,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun FactorialCard() {
    var inputStr by remember { mutableStateOf("10") }

    val n = inputStr.toIntOrNull()
    val resultText = when {
        n == null || n < 0 -> "Ingresa entero no negativo"
        n > 100 -> "Valor muy grande (máx 100)"
        else -> {
            val fact = calculateFactorial(n)
            "$n! = $fact"
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "2. Factorial de un Número (n!)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = inputStr,
                onValueChange = { inputStr = it },
                label = { Text("Ingresa número (0-100)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = resultText,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun GcdLcmCard() {
    var inputA by remember { mutableStateOf("24") }
    var inputB by remember { mutableStateOf("36") }

    val numA = inputA.toLongOrNull()
    val numB = inputB.toLongOrNull()

    val resultText = if (numA != null && numB != null && numA > 0 && numB > 0) {
        val gcd = findGCD(numA, numB)
        val lcm = (numA / gcd) * numB
        "MCD = $gcd  |  MCM = $lcm"
    } else {
        "Ingresa enteros positivos A y B"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "3. Máximo Común Divisor (MCD) y Mínimo Común Múltiplo (MCM)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = inputA,
                    onValueChange = { inputA = it },
                    label = { Text("Número A") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = inputB,
                    onValueChange = { inputB = it },
                    label = { Text("Número B") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = resultText,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun ListStatisticsCard() {
    var inputListStr by remember { mutableStateOf("12, 45, 23, 67, 89, 34") }

    val numbers = inputListStr
        .split(",")
        .mapNotNull { it.trim().toDoubleOrNull() }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "4. Estadística Rápida de Lista",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = inputListStr,
                onValueChange = { inputListStr = it },
                label = { Text("Números separados por comas") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))

            if (numbers.isNotEmpty()) {
                val count = numbers.size
                val sum = numbers.sum()
                val avg = sum / count
                val min = numbers.minOrNull() ?: 0.0
                val max = numbers.maxOrNull() ?: 0.0

                Text(text = "Cantidad: $count | Suma: ${formatDouble(sum)}", style = MaterialTheme.typography.bodyMedium)
                Text(text = "Promedio: ${formatDouble(avg)}", style = MaterialTheme.typography.bodyMedium)
                Text(text = "Mínimo: ${formatDouble(min)} | Máximo: ${formatDouble(max)}", style = MaterialTheme.typography.bodyMedium)
            } else {
                Text(text = "Ingresa números válidos separados por comas", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

private fun checkIsPrime(n: Long): Boolean {
    if (n <= 1) return false
    if (n <= 3) return true
    if (n % 2 == 0L || n % 3 == 0L) return false
    var i = 5L
    while (i * i <= n) {
        if (n % i == 0L || n % (i + 2) == 0L) return false
        i += 6
    }
    return true
}

private fun getFactors(n: Long): List<Long> {
    val factors = mutableListOf<Long>()
    for (i in 1..minOf(n, 1000L)) {
        if (n % i == 0L) {
            factors.add(i)
            if (factors.size >= 8) break
        }
    }
    return factors
}

private fun calculateFactorial(n: Int): String {
    var result = BigInteger.ONE
    for (i in 2..n) {
        result = result.multiply(BigInteger.valueOf(i.toLong()))
    }
    return result.toString()
}

private fun findGCD(a: Long, b: Long): Long {
    var x = a
    var y = b
    while (y != 0L) {
        val temp = y
        y = x % y
        x = temp
    }
    return x
}

private fun formatDouble(value: Double): String {
    return if (value % 1.0 == 0.0 && !value.isInfinite()) {
        value.toLong().toString()
    } else {
        String.format(Locale.US, "%.4f", value).trimEnd('0').trimEnd('.')
    }
}
