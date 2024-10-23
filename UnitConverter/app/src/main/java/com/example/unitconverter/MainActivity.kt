package com.example.unitconverter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.unitconverter.ui.theme.UnitConverterTheme
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UnitConverterTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    UnitConverter(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun UnitConverter(modifier: Modifier = Modifier) {

    var inputValue by remember { mutableStateOf("") }
    var outputValue by remember { mutableStateOf("") }
    var firstSelectExpanded by remember { mutableStateOf(false) }
    var secondSelectExpanded by remember { mutableStateOf(false) }
    var firstSelectValue by remember { mutableStateOf(ConverterOption.Meters) }
    var secondSelectValue by remember { mutableStateOf(ConverterOption.Meters) }
    var conversionFactor by remember { mutableDoubleStateOf(1.00) }
    var secondConversionFactor by remember { mutableDoubleStateOf(1.00) }

    val customTextStyle = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontSize = 32.sp,
        fontWeight = FontWeight.Bold,
        color = Color.hsl(0f, 0.75f, 0.75f)
    )

    fun processConversion() {
        val inputDouble = inputValue.toDoubleOrNull() ?: 0.0
        val result =
            (inputDouble * conversionFactor * 100 / secondConversionFactor).roundToInt() / 100.0
        outputValue = result.toString()
    }

    fun onFirstSelect(firstOption: ConverterOption) {
        firstSelectValue = firstOption
        firstSelectExpanded = false
        conversionFactor = when (firstOption) {
            ConverterOption.Centimeters -> 0.01
            ConverterOption.Meters -> 1.0
            ConverterOption.Feet -> 0.3048
            ConverterOption.Millimeters -> 0.001
        }
        processConversion()
    }

    fun onSecondSelect(secondOption: ConverterOption) {
        secondSelectValue = secondOption
        secondSelectExpanded = false
        secondConversionFactor = when (secondOption) {
            ConverterOption.Centimeters -> 0.01
            ConverterOption.Meters -> 1.0
            ConverterOption.Feet -> 0.3048
            ConverterOption.Millimeters -> 0.001
        }
        processConversion()
    }



    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Unit Converter",
            modifier = Modifier.padding(bottom = 32.dp),
            style = MaterialTheme.typography.headlineLarge
        )

        OutlinedTextField(
            value = inputValue,
            label = { Text("Input Value") },
            onValueChange = { inputValue = it; processConversion() })

        Spacer(modifier = Modifier.height(16.dp))

        Row {

            Box {

                Button(onClick = { firstSelectExpanded = true }) {
                    Text(firstSelectValue.name)
                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Select Arrow Down")
                }

                DropdownMenu(
                    expanded = firstSelectExpanded,
                    onDismissRequest = { firstSelectExpanded = false }) {
                    DropdownMenuItem(
                        text = { Text(ConverterOption.Centimeters.name) },
                        onClick = { onFirstSelect(ConverterOption.Centimeters) })
                    DropdownMenuItem(
                        text = { Text(ConverterOption.Meters.name) },
                        onClick = { onFirstSelect(ConverterOption.Meters) })
                    DropdownMenuItem(
                        text = { Text(ConverterOption.Feet.name) },
                        onClick = { onFirstSelect(ConverterOption.Feet) })
                    DropdownMenuItem(
                        text = { Text(ConverterOption.Millimeters.name) },
                        onClick = { onFirstSelect(ConverterOption.Millimeters) })
                }

            }

            Spacer(modifier = Modifier.width(16.dp))

            Box {

                Button(onClick = { secondSelectExpanded = true }) {
                    Text(secondSelectValue.name)
                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Select Arrow Down")
                }

                DropdownMenu(
                    expanded = secondSelectExpanded,
                    onDismissRequest = { secondSelectExpanded = false }) {
                    DropdownMenuItem(
                        text = { Text(ConverterOption.Centimeters.name) },
                        onClick = { onSecondSelect(ConverterOption.Centimeters) })
                    DropdownMenuItem(
                        text = { Text(ConverterOption.Meters.name) },
                        onClick = { onSecondSelect(ConverterOption.Meters) })
                    DropdownMenuItem(
                        text = { Text(ConverterOption.Feet.name) },
                        onClick = { onSecondSelect(ConverterOption.Feet) })
                    DropdownMenuItem(
                        text = { Text(ConverterOption.Millimeters.name) },
                        onClick = { onSecondSelect(ConverterOption.Millimeters) })
                }

            }

        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Result: $outputValue ${secondSelectValue.name}",
            style = MaterialTheme.typography.headlineMedium
        )
    }

}


@Preview(showBackground = true)
@Composable
fun PreviewUnitConverter() {
    UnitConverterTheme { UnitConverter() }
}