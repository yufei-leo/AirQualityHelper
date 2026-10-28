# Air Quality Helper

Air Quality Helper is an Android utility app developed for CP3406 Assessment 1. It provides at-a-glance air quality information for Singapore, Sydney and Melbourne.

The app displays model-based air quality estimates retrieved from the Open-Meteo Air Quality API.

## Features

- Display the selected city's US Air Quality Index (AQI).
- Show an AQI category with a coloured summary card.
- Display optional PM2.5 and PM10 concentrations in µg/m³.
- Show the data timestamp and time zone returned by the API.
- Refresh air quality data manually.
- Choose between Singapore, Sydney and Melbourne.
- Show or hide pollutant details.
- Select an alert threshold of 50, 100, 150 or 200.
- Display an on-screen alert when AQI is strictly greater than the selected threshold.
- Display loading and error states, with a refresh button for retrying failed requests.
- Support scrolling in landscape orientation.
- Retain the selected tab and current settings during screen rotation.

## How to Run

1. Clone or download this repository.
2. Open the project folder containing `settings.gradle.kts` in Android Studio.
3. Allow Gradle to synchronise and download the required dependencies.
4. Install Android SDK Platform 36 if prompted.
5. Select an emulator or Android device running Android 8.0 (API 26) or later.
6. Run the `app` configuration.

An internet connection is required to retrieve air quality data. The configured Open-Meteo public endpoint does not require an API key.

Development and manual testing were carried out using the Android Studio Medium Phone API 35 emulator.

## Using the App

The **Utility** tab displays air quality information for the selected city. Tap **Refresh** to request updated data.

The **Settings** tab controls the city, pollutant detail visibility and alert threshold. Settings affect the Utility screen without restarting the app.

The alert threshold is a personal display preference. It does not change the AQI category or its colour, and it does not trigger background notifications.

## Implementation

The app uses Kotlin, Jetpack Compose and Material Design 3.

### MainActivity.kt

Contains the activity and Compose user interface:

- `UtilityApp()` provides the scaffold and bottom navigation.
- `UtilityScreen()` displays loading, error and air quality states.
- `SettingsScreen()` provides city, detail visibility and threshold controls.

The selected navigation tab uses `rememberSaveable`. The interface observes ViewModel state using `collectAsStateWithLifecycle()`.

### AirQualityApi.kt

Defines the API response models and Retrofit interface. Gson maps JSON fields such as `us_aqi` and `pm2_5` to Kotlin properties.

`AirQualityApiClient` creates a shared Retrofit client.

### AirQualityRepository.kt

Maps supported city names to fixed coordinates and requests their air quality data through the API interface. It checks that the response contains current data.

### AirQualityViewModel.kt

Manages the selected city, display preferences, loading state, response and error message through `StateFlow`.

Network requests run in `viewModelScope`. A previous request is cancelled when a new request starts, and old results are cleared to avoid displaying another city's data during loading.

### Dependency Injection

The project uses manual constructor injection:

- `AirQualityRepository` receives an `AirQualityApi`.
- `AirQualityViewModel` receives an `AirQualityRepository`.
- A `ViewModelProvider.Factory` constructs the ViewModel with its dependency.

## Data Sources and Limitations

Air quality data is supplied by [Open-Meteo](https://open-meteo.com/en/docs/air-quality-api), using Copernicus Atmosphere Monitoring Service (CAMS) model data.

Data attribution: CAMS ENSEMBLE data provider / Open-Meteo.

- Values are model estimates, not measurements from a nearby monitoring station.
- Each supported city uses fixed coordinates; the app does not use GPS.
- The app uses the US AQI scale, rather than a country's local index such as Singapore PSI.
- The overall AQI can reflect pollutants beyond the PM2.5 and PM10 values shown.
- The displayed data time comes from the API and is not the time the Refresh button was pressed.
- Refreshing may return the same values because source data does not change with every request.
- Missing numeric values are displayed as `N/A`.
- Preferences are not saved to persistent storage and may reset after the app is closed or its process is recreated.
- The app does not provide offline data storage, historical charts or background alerts.

See the [Open-Meteo Air Quality API documentation](https://open-meteo.com/en/docs/air-quality-api) for data coverage, attribution requirements and usage terms.

## Manual Testing

The following checks were completed on the development emulator:

- Load data for each supported city.
- Refresh air quality data.
- Switch pollutant details off and on.
- Change the alert threshold and verify the corresponding on-screen alert.
- Verify that hiding pollutant details does not hide the threshold alert.
- Disconnect the network and verify that an error message appears.
- Restore the network and successfully retry using Refresh.
- Scroll through settings to reach all threshold options.
- Rotate between portrait and landscape and check content accessibility.
- Verify that the selected city, settings and navigation tab survive rotation.

These checks are manual functional tests. They do not establish compatibility with every Android device or validate the accuracy of the source air quality models.

## Acknowledgements

This project is based on the course-provided **MobileTech Utility App** starter template for CP3406 / CP5307 Assessment 1. The original template supplied the Compose scaffold, bottom navigation, counter screen and settings placeholder.

The template was extended with air quality retrieval, application state management, city selection, configurable display settings, threshold alerts and an AQI summary card.

## Generative AI Assistance

Use OpenAI's Codex to assist in planning, explanation, debugging guidance, and assisting in the writing of README. The generated suggestions have been integrated into the project and have been manually tested during the development process.
