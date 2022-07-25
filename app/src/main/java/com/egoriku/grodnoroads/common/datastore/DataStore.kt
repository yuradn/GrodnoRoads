package com.egoriku.grodnoroads.common.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

object PreferenceKeys {
    val APP_THEME = intPreferencesKey("app_theme")
    val APP_LANGUAGE = stringPreferencesKey("app_language")

    val IS_SHOW_STATIONARY_CAMERAS = booleanPreferencesKey("is_show_stationary_cameras")
    val IS_SHOW_MOBILE_CAMERAS = booleanPreferencesKey("is_show_mobile_cameras")
    val IS_SHOW_TRAFFIC_POLICE_EVENTS = booleanPreferencesKey("is_show_traffic_police_events")
    val IS_SHOW_INCIDENT_EVENTS = booleanPreferencesKey("is_show_incident_events")
    val IS_SHOW_CAR_CRASH_EVENTS = booleanPreferencesKey("is_show_car_crash_events")
    val IS_SHOW_TRAFFIC_JAM_EVENTS = booleanPreferencesKey("is_show_traffic_jam_events")
    val IS_SHOW_WILD_ANIMALS_EVENTS = booleanPreferencesKey("is_show_wild_animals_events")

    val IS_SHOW_TRAFFIC_JAM_APPEARANCE = booleanPreferencesKey("is_show_traffic_jam_appearance")
    val GOOGLE_MAP_STYLE = stringPreferencesKey("google_map_style")

    val ALERT_DISTANCE = intPreferencesKey("alert_distance")
}