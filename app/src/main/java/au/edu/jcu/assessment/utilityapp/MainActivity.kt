package au.edu.jcu.assessment.utilityapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import au.edu.jcu.assessment.utilityapp.ui.theme.MobileTech_UtilityAppTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.RadioButton

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MobileTech_UtilityAppTheme {
                UtilityApp()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UtilityAppPreview() {
    MobileTech_UtilityAppTheme {
        UtilityApp()
    }
}

@Composable
fun UtilityApp() {
    var selectedTab by remember { mutableStateOf("Utility") }
    var showDetails by remember { mutableStateOf(true) }
    var selectedCity by remember { mutableStateOf("Singapore") }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Utility") },
                    label = { Text("Utility") },
                    selected = selectedTab == "Utility",
                    onClick = { selectedTab = "Utility" }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                    label = { Text("Settings") },
                    selected = selectedTab == "Settings",
                    onClick = { selectedTab = "Settings" }
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedTab) {
                "Utility" -> UtilityScreen(
                    showDetails = showDetails,
                    city = selectedCity
                )

                "Settings" -> SettingsScreen(
                    showDetails = showDetails,
                    onShowDetailsChange = { showDetails = it },
                    selectedCity = selectedCity,
                    onCityChange = { selectedCity = it }
                )
            }
        }
    }
}

@Composable
fun UtilityScreen(
    showDetails: Boolean,
    city: String
) {
    // Sample data for the first UI prototype.
    val aqi = 42
    val pm25 = 8.5
    val pm10 = 16.0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Air Quality Helper",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = city,
            style = MaterialTheme.typography.titleLarge
        )

        Text(
            text = "US AQI: $aqi",
            style = MaterialTheme.typography.displaySmall
        )

        Text(
            text = "Good",
            style = MaterialTheme.typography.titleMedium
        )

        if (showDetails) {
            Text(
                text = "PM2.5: $pm25 µg/m³",
                style = MaterialTheme.typography.bodyLarge
            )

            Text(
                text = "PM10: $pm10 µg/m³",
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Text(
            text = "Sample data only — not live readings",
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun SettingsScreen(
    showDetails: Boolean,
    onShowDetailsChange: (Boolean) -> Unit,
    selectedCity: String,
    onCityChange: (String) -> Unit
) {
    val cities = listOf("Singapore", "Sydney", "Melbourne")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "City",
            style = MaterialTheme.typography.titleMedium
        )

        cities.forEach { city ->
            androidx.compose.material3.TextButton(
                onClick = { onCityChange(city) }
            ) {
                RadioButton(
                    selected = selectedCity == city,
                    onClick = null
                )

                Text(
                    text = city,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }

        Text(
            text = "Show pollutant details",
            style = MaterialTheme.typography.titleMedium
        )

        Text(
            text = "Display PM2.5 and PM10 on the home screen.",
            style = MaterialTheme.typography.bodyMedium
        )

        Switch(
            checked = showDetails,
            onCheckedChange = onShowDetailsChange
        )
    }
}