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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

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
    val factory = remember {
        AirQualityViewModel.Factory(
            AirQualityRepository(AirQualityApiClient.api)
        )
    }

    val airQualityViewModel: AirQualityViewModel = viewModel(
        factory = factory
    )

    val uiState by airQualityViewModel.uiState.collectAsStateWithLifecycle()

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
                    uiState = uiState,
                    onRefresh = { airQualityViewModel.refresh() }
                )

                "Settings" -> SettingsScreen(
                    showDetails = uiState.showDetails,
                    onShowDetailsChange = {
                        airQualityViewModel.setShowDetails(it)
                    },
                    selectedCity = uiState.selectedCity,
                    onCityChange = {
                        airQualityViewModel.selectCity(it)
                    }
                )
            }
        }
    }
}

@Composable
fun UtilityScreen(
    uiState: AirQualityUiState,
    onRefresh: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Air Quality Helper",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = uiState.selectedCity,
            style = MaterialTheme.typography.titleLarge
        )

        when {
            uiState.isLoading -> {
                CircularProgressIndicator()
                Text("Loading air quality...")
            }

            uiState.errorMessage != null -> {
                Text(
                    text = uiState.errorMessage,
                    color = MaterialTheme.colorScheme.error
                )
            }

            else -> {
                val current = uiState.response?.current
                val aqi = current?.usAqi

                val category = when {
                    aqi == null || !aqi.isFinite() || aqi < 0 ->
                        "Unavailable"
                    aqi <= 50 -> "Good"
                    aqi <= 100 -> "Moderate"
                    aqi <= 150 -> "Unhealthy for sensitive groups"
                    aqi <= 200 -> "Unhealthy"
                    aqi <= 300 -> "Very unhealthy"
                    else -> "Hazardous"
                }

                Text(
                    text = "US AQI: ${aqi ?: "N/A"}",
                    style = MaterialTheme.typography.displaySmall
                )

                Text(
                    text = category,
                    style = MaterialTheme.typography.titleMedium
                )

                if (uiState.showDetails) {
                    Text(
                        text = "PM2.5: ${
                            current?.pm25?.let { "$it µg/m³" } ?: "N/A"
                        }",
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Text(
                        text = "PM10: ${
                            current?.pm10?.let { "$it µg/m³" } ?: "N/A"
                        }",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                Text(
                    text = "Data time: ${
                        current?.time?.replace("T", " ") ?: "N/A"
                    }",
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    text = "Time zone: ${uiState.response?.timezone ?: "N/A"}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Button(
            onClick = onRefresh,
            enabled = !uiState.isLoading
        ) {
            Text("Refresh")
        }

        Text(
            text = "Source: Open-Meteo / CAMS\n" +
                    "Model estimates, not station measurements.",
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