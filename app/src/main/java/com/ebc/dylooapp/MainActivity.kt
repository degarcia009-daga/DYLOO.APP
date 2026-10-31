package com.ebc.dylooapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// --- SISTEMA DE DISEÑO ---
val MoradoPrimario = Color(0xFF584FE8) //[cite: 1]
val MoradoOscuro = Color(0xFF1B1650) //[cite: 1]
val FondoApp = Color(0xFFF7F7FB) //[cite: 1]
val ColorAmbar = Color(0xFFF5A623) //[cite: 1]

// --- MODELOS DE DATOS ---
data class PaqueteCabina(
    val id: Int,
    val nombre: String,
    val precio: Double,
    val caracteristicas: List<String>
)

val catalogoPaquetes = listOf(
    PaqueteCabina(1, "Paquete Básico", 1500.0, listOf("2 horas de servicio", "Alfombra roja", "Operador", "Luces LED")), //[cite: 1]
    PaqueteCabina(2, "Paquete Estándar", 2000.0, listOf("3 horas de servicio", "Props temáticos", "Luces adicionales", "Descarga rápida")), //[cite: 1]
    PaqueteCabina(3, "Paquete Deluxe", 2500.0, listOf("4 horas de servicio", "Fondo personalizado", "VIP Area", "Red Social Directa")) //[cite: 1]
)

enum class Pantalla {
    CATALOGO, DETALLE, PAGO
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                DylooApp()
            }
        }
    }
}

@Composable
fun DylooApp() {
    var pantallaActual by remember { mutableStateOf(Pantalla.CATALOGO) }
    var paqueteSeleccionado by remember { mutableStateOf<PaqueteCabina?>(null) }
    var fechaSeleccionada by remember { mutableStateOf<Int?>(null) }

    Surface(modifier = Modifier.fillMaxSize(), color = FondoApp) {
        when (pantallaActual) {
            Pantalla.CATALOGO -> PantallaCatalogo(
                alSeleccionarPaquete = { paquete ->
                    paqueteSeleccionado = paquete
                    pantallaActual = Pantalla.DETALLE
                }
            )
            Pantalla.DETALLE -> PantallaDetalle(
                paquete = paqueteSeleccionado!!,
                fechaSeleccionada = fechaSeleccionada,
                alSeleccionarFecha = { fechaSeleccionada = it },
                alContinuar = { pantallaActual = Pantalla.PAGO },
                alVolver = { pantallaActual = Pantalla.CATALOGO }
            )
            Pantalla.PAGO -> PantallaPago(
                paquete = paqueteSeleccionado!!,
                fecha = fechaSeleccionada!!,
                alPagar = {
                    pantallaActual = Pantalla.CATALOGO // Reinicia el flujo tras el éxito
                    fechaSeleccionada = null
                },
                alVolver = { pantallaActual = Pantalla.DETALLE }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaCatalogo(alSeleccionarPaquete: (PaqueteCabina) -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Catálogo Cabinas 360", color = MoradoOscuro, fontWeight = FontWeight.Bold) }, //[cite: 1]
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).padding(16.dp).fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(catalogoPaquetes) { paquete ->
                Card(
                    shape = RoundedCornerShape(16.dp), //[cite: 1]
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = paquete.nombre, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MoradoOscuro)
                        Spacer(modifier = Modifier.height(8.dp))
                        paquete.caracteristicas.forEach { caracteristica ->
                            Text(text = "• $caracteristica", color = Color.DarkGray)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "PRECIO FINAL: $${paquete.precio}", //[cite: 1]
                            fontWeight = FontWeight.Bold,
                            color = MoradoPrimario,
                            fontSize = 18.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { alSeleccionarPaquete(paquete) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = MoradoPrimario),
                            shape = RoundedCornerShape(50) // Botones Pill[cite: 1]
                        ) {
                            Text("Ver disponibilidad", color = Color.White) //[cite: 1]
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaDetalle(
    paquete: PaqueteCabina,
    fechaSeleccionada: Int?,
    alSeleccionarFecha: (Int) -> Unit,
    alContinuar: () -> Unit,
    alVolver: () -> Unit
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del Producto", color = MoradoOscuro) }, //[cite: 1]
                navigationIcon = {
                    Button(onClick = alVolver, colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)) {
                        Text("<", color = MoradoOscuro, fontSize = 24.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp).fillMaxSize()) {
            Text(text = paquete.nombre, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MoradoOscuro)
            Text(text = "$${paquete.precio} MXN / día", fontSize = 20.sp, color = MoradoPrimario, fontWeight = FontWeight.Bold) //[cite: 1]
            Spacer(modifier = Modifier.height(16.dp))

            Text("Selecciona una fecha (Octubre)", fontWeight = FontWeight.Bold, color = MoradoOscuro)
            Spacer(modifier = Modifier.height(8.dp))

            // Calendario Interactivo[cite: 1]
            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier.fillMaxWidth().height(250.dp)
            ) {
                items(30) { dia ->
                    val diaReal = dia + 1
                    val ocupado = diaReal % 5 == 0 // Simulamos días ocupados
                    val esSeleccionado = fechaSeleccionada == diaReal

                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .aspectRatio(1f)
                            .background(
                                color = when {
                                    esSeleccionado -> MoradoPrimario // Día seleccionado pintado de morado sólido[cite: 1]
                                    ocupado -> Color.LightGray // Días ocupados en gris[cite: 1]
                                    else -> Color.White
                                },
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable(enabled = !ocupado) {
                                alSeleccionarFecha(diaReal)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (ocupado) "X" else diaReal.toString(), // Días ocupados marcados con cruz[cite: 1]
                            color = if (esSeleccionado || ocupado) Color.White else MoradoOscuro,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = {
                    if (fechaSeleccionada != null) {
                        alContinuar()
                    } else {
                        Toast.makeText(context, "Por favor, selecciona una fecha disponible", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MoradoPrimario),
                shape = RoundedCornerShape(50)
            ) {
                Text("SELECCIONAR FECHA Y CONTINUAR", color = Color.White) //[cite: 1]
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaPago(
    paquete: PaqueteCabina,
    fecha: Int,
    alPagar: () -> Unit,
    alVolver: () -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Resumen del Evento", color = MoradoOscuro) }, //[cite: 1]
                navigationIcon = {
                    Button(onClick = alVolver, colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)) {
                        Text("<", color = MoradoOscuro, fontSize = 24.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp).fillMaxSize()) {

            // Resumen
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Paquete: ${paquete.nombre}", fontWeight = FontWeight.Bold)
                    Text("Fecha: $fecha de Octubre")
                    Text("Total: $${paquete.precio}", color = MoradoPrimario, fontWeight = FontWeight.Bold)
                    Text("Anticipo requerido: $${paquete.precio / 2}") //[cite: 1]
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Datos del Cliente", fontWeight = FontWeight.Bold, color = MoradoOscuro) //[cite: 1]
            Spacer(modifier = Modifier.height(8.dp))

            // Formulario y validaciones de campos vacíos[cite: 1]
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre Completo") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = telefono,
                onValueChange = { telefono = it },
                label = { Text("Teléfono") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = direccion,
                onValueChange = { direccion = it },
                label = { Text("Dirección exacta de la fiesta") }, //[cite: 1]
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = {
                    if (nombre.isNotBlank() && telefono.isNotBlank() && direccion.isNotBlank()) {
                        Toast.makeText(context, "¡Anticipo pagado y reserva confirmada!", Toast.LENGTH_LONG).show() // Simulación de pago seguro dentro de la aplicación[cite: 1]
                        alPagar()
                    } else {
                        Toast.makeText(context, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show() //[cite: 1]
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MoradoPrimario),
                shape = RoundedCornerShape(50)
            ) {
                Text("Abonar Anticipo Seguro", color = Color.White) //[cite: 1]
            }
        }
    }
}
