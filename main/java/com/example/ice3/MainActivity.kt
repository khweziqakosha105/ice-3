package com.example.ice3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ice3.ui.theme.Ice3Theme
import java.util.UUID

// Data model for Menu Item
data class MenuItem(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String,
    val course: String,
    val price: Double
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Ice3Theme {
                MenuManagerApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuManagerApp() {
    // --- State Variables ---
    var dishName by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var course by remember { mutableStateOf("Starter") }
    var price by remember { mutableStateOf("") }
    var menuItems by remember { mutableStateOf(listOf<MenuItem>()) }

    // Dropdown state
    var expanded by remember { mutableStateOf(false) }
    val courses = listOf("Starter", "Main Course", "Dessert", "Beverage")

    // Validation snackbar state
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        "Chef's Menu Manager", 
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    ) 
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF2C3E50)
                )
            )
        },
        containerColor = Color(0xFFF5F5F5)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // --- Input Section ---
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    InputField(label = "Dish Name", value = dishName, onValueChange = { dishName = it }, placeholder = "e.g. Grilled Salmon")
                    
                    InputField(label = "Description", value = description, onValueChange = { description = it }, placeholder = "Short description of the dish")

                    Text("Course", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF34495E))
                    Box(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                        OutlinedCard(
                            onClick = { expanded = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(5.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBDC3C7))
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(course, fontSize = 16.sp)
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                            }
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.fillMaxWidth(0.85f)
                        ) {
                            courses.forEach { selection ->
                                DropdownMenuItem(
                                    text = { Text(selection) },
                                    onClick = {
                                        course = selection
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    InputField(
                        label = "Price (R)", 
                        value = price, 
                        onValueChange = { price = it }, 
                        placeholder = "e.g. 150.00",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            val numericPrice = price.toDoubleOrNull()
                            if (dishName.isBlank() || description.isBlank() || price.isBlank()) {
                                // Simple validation message
                            } else if (numericPrice == null || numericPrice <= 0) {
                                // Invalid price
                            } else {
                                val newItem = MenuItem(
                                    name = dishName,
                                    description = description,
                                    course = course,
                                    price = numericPrice
                                )
                                menuItems = menuItems + newItem
                                // Reset fields
                                dishName = ""
                                description = ""
                                price = ""
                                course = "Starter"
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE67E22)),
                        shape = RoundedCornerShape(5.dp)
                    ) {
                        Text("Add Menu Item", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // --- List Section ---
            Text(
                "Menu List (${menuItems.size} items)",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2C3E50),
                modifier = Modifier.padding(bottom = 10.dp)
            )

            if (menuItems.isEmpty()) {
                Text(
                    "No items added yet. Start by adding one above!",
                    color = Color(0xFF95A5A6),
                    modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(menuItems) { item ->
                        MenuCard(item)
                    }
                }
            }
        }
    }
}

@Composable
fun InputField(
    label: String, 
    value: String, 
    onValueChange: (String) -> Unit, 
    placeholder: String,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    Column(modifier = Modifier.padding(bottom = 12.dp)) {
        Text(label, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF34495E))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = Color(0xFFBDC3C7)) },
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
            keyboardOptions = keyboardOptions,
            shape = RoundedCornerShape(5.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFE67E22),
                unfocusedBorderColor = Color(0xFFBDC3C7)
            ),
            singleLine = true
        )
    }
}

@Composable
fun MenuCard(item: MenuItem) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 5.dp, 
                color = Color(0xFFE67E22), 
                shape = RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp)
            ) // Simulated left border
    ) {
        Column(modifier = Modifier.padding(15.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(item.name, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2C3E50))
                Text("R${"%.2f".format(item.price)}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF27AE60))
            }
            Text(item.course, fontSize = 14.sp, fontStyle = FontStyle.Italic, color = Color(0xFF7F8C8D), modifier = Modifier.padding(vertical = 4.dp))
            Text(item.description, fontSize = 14.sp, color = Color(0xFF34495E))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MenuManagerPreview() {
    Ice3Theme {
        MenuManagerApp()
    }
}
