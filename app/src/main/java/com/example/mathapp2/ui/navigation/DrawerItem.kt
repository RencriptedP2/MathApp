package com.example.mathapp2.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.SquareFoot
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.ui.graphics.vector.ImageVector

enum class DrawerItem(
    val title: String,
    val description: String,
    val icon: ImageVector
) {
    CALCULATOR(
        title = "Calculadora",
        description = "Básica con historial",
        icon = Icons.Default.Calculate
    ),
    UNIT_CONVERTER(
        title = "Convertidor de Unidades",
        description = "Longitud, peso y temperatura",
        icon = Icons.Default.Straighten
    ),
    GEOMETRY(
        title = "Geometría",
        description = "Área y perímetro de figuras",
        icon = Icons.Default.SquareFoot
    ),
    UTILITIES(
        title = "Utilidades Matemáticas",
        description = "Primos, factorial, MCD y MCM",
        icon = Icons.Default.Functions
    ),
    ABOUT(
        title = "Acerca de Suma",
        description = "Información y futuras extensiones",
        icon = Icons.Default.Info
    )
}
