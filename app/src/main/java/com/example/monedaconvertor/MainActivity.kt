package com.example.monedaconvertor

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
//import androidx.compose.ui.semantics.text
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.forEach
import androidx.lifecycle.lifecycleScope
import com.example.monedaconvertor.data.RetrofitService
import com.example.monedaconvertor.data.model.RemoteResult // Asegúrate que esta es la importación correcta
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // La configuración de EdgeToEdge se puede hacer con el tema
        // o con WindowCompat.setDecorFitsSystemWindows(window, false) antes de setContentView.
        // Por simplicidad, me enfocaré en la lógica principal.
        setContentView(R.layout.activity_main) // ¡Importante! setContentView primero

        // Aplicar padding para System Bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val containerMonedasLayout = findViewById<LinearLayout>(R.id.containerMonedasDinamico)
        val inflater = LayoutInflater.from(this)
        val service = RetrofitService.RetrofitServiceFactory.makeRetrofitService()

        // --- INICIO DE LA LÓGICA DE CARGA DE MONEDAS ---
        lifecycleScope.launch {
            try {
                Log.d("MainActivity", "Iniciando llamada a TodasMonedas...")
                val apiKey = "c7783f7e2d6a7ad55ea925db" // <<< --- !!! REEMPLAZA ESTO POR TU API KEY REAL !!!
                val baseCurrency = "BOB" // O la moneda base que necesites

                // Asegúrate que tu método TodasMonedas en RetrofitService espera estos parámetros
                // y devuelve un objeto RemoteResult (o como hayas llamado a tu modelo principal)
                val remoteResult: RemoteResult = service.TodasMonedas(apiKey, baseCurrency)
                Log.d("MainActivity", "Respuesta de API (RemoteResult): $remoteResult")

                if (remoteResult.result == "success" && remoteResult.conversion_rates != null) {
                    val ratesMap = remoteResult.conversion_rates // Esto debería ser Map<String, Double>

                    // Limpia vistas anteriores antes de añadir las nuevas
                    containerMonedasLayout.removeAllViews()

                    if (ratesMap.isEmpty()) {
                        Log.w("MainActivity", "El mapa de tasas está vacío.")
                        val emptyTextView = TextView(this@MainActivity).apply {
                            text = "No hay tasas de cambio disponibles para mostrar."
                            textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                        }
                        containerMonedasLayout.addView(emptyTextView)
                    } else {
                        // Itera sobre el mapa de tasas (monedas)
                        ratesMap.forEach { (codigoMoneda, tasa) ->
                            // Infla el layout del item de moneda
                            val itemView = inflater.inflate(R.layout.item_moneda_layout, containerMonedasLayout, false)

                            // Accede a las vistas dentro del item inflado
                            val textViewInfo1 = itemView.findViewById<TextView>(R.id.textViewMonedaInfo1)
                            val textViewInfo2 = itemView.findViewById<TextView>(R.id.textViewMonedaInfo2)

                            // Establece los datos de la moneda actual
                            textViewInfo1.text = "Moneda: $codigoMoneda"
                            textViewInfo2.text = "Tasa: $tasa"

                            // Añade la vista del item al contenedor
                            containerMonedasLayout.addView(itemView)
                            Log.d("MainActivity", "Añadido item para: $codigoMoneda")
                        }
                    }
                } else {
                    Log.w("MainActivity", "La respuesta de la API no fue exitosa o no hay tasas: ${remoteResult.result}")
                    containerMonedasLayout.removeAllViews() // Limpia por si acaso
                    val errorTextView = TextView(this@MainActivity).apply {
                        text = "No se pudieron cargar las tasas de cambio (API: ${remoteResult.result})."
                        textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                    }
                    containerMonedasLayout.addView(errorTextView)
                }

            } catch (e: Exception) {
                Log.e("MainActivity", "Error al obtener y mostrar las monedas", e)
                containerMonedasLayout.removeAllViews() // Limpia por si acaso
                val errorTextView = TextView(this@MainActivity).apply {
                    text = "Error de red o procesamiento: ${e.localizedMessage}"
                    textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                }
                containerMonedasLayout.addView(errorTextView)
            }
        }
        // --- FIN DE LA LÓGICA DE CARGA DE MONEDAS ---

        // Configuración de botones para navegar a otras Activities
        val buttonPantalla1 = findViewById<Button>(R.id.buttonPantalla1)
        val buttonPantalla2 = findViewById<Button>(R.id.buttonPantalla2)
        val buttonPantalla3 = findViewById<Button>(R.id.buttonPantalla3)

        buttonPantalla1.setOnClickListener {
            // Considera si realmente quieres iniciar una nueva instancia de MainActivity
            // Podría ser más útil navegar a una pantalla principal diferente o no hacer nada
            // val intent = Intent(this, MainActivity::class.java)
            // startActivity(intent)
            Log.d("MainActivity", "Botón Pantalla 1 presionado")
            // Si este botón es para refrescar, deberías llamar a la lógica de carga de monedas de nuevo,
            // no iniciar una nueva MainActivity.
        }

        buttonPantalla2.setOnClickListener {
            // Asegúrate de que MainActivity2 exista y esté declarada en tu AndroidManifest.xml
            val intent = Intent(this, MainActivity2::class.java)
            startActivity(intent)
        }

        buttonPantalla3.setOnClickListener {
            // Asegúrate de que MainActivity3 exista y esté declarada en tu AndroidManifest.xml
            val intent = Intent(this, MainActivity3::class.java)
            startActivity(intent)
        }
    }
}
