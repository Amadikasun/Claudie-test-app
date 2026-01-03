package com.example.spaceboatgame

import android.content.Context
import android.content.SharedPreferences

class GameSettings(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("SpaceBoatGameSettings", Context.MODE_PRIVATE)

    companion object {
        // Klíče pro SharedPreferences
        private const val KEY_PLAYER_NAME = "player_name"
        private const val KEY_SHIP_COLOR = "ship_color"
        private const val KEY_SOUND_ENABLED = "sound_enabled"
        private const val KEY_VIBRATION_ENABLED = "vibration_enabled"

        // Barvy lodí
        const val SHIP_COLOR_CYAN = 0
        const val SHIP_COLOR_GREEN = 1
        const val SHIP_COLOR_RED = 2
    }

    // Jméno hráče
    var playerName: String
        get() = prefs.getString(KEY_PLAYER_NAME, "Hráč") ?: "Hráč"
        set(value) = prefs.edit().putString(KEY_PLAYER_NAME, value).apply()

    // Barva lodi
    var shipColor: Int
        get() = prefs.getInt(KEY_SHIP_COLOR, SHIP_COLOR_CYAN)
        set(value) = prefs.edit().putInt(KEY_SHIP_COLOR, value).apply()

    // Zvuky zapnuté/vypnuté
    var soundEnabled: Boolean
        get() = prefs.getBoolean(KEY_SOUND_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_SOUND_ENABLED, value).apply()

    // Vibrace zapnuté/vypnuté
    var vibrationEnabled: Boolean
        get() = prefs.getBoolean(KEY_VIBRATION_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_VIBRATION_ENABLED, value).apply()

    // Pomocná funkce pro barvu lodi
    fun getShipColorValue(): Int {
        return when (shipColor) {
            SHIP_COLOR_CYAN -> android.graphics.Color.CYAN
            SHIP_COLOR_GREEN -> android.graphics.Color.GREEN
            SHIP_COLOR_RED -> android.graphics.Color.RED
            else -> android.graphics.Color.CYAN
        }
    }

    fun getShipColorName(): String {
        return when (shipColor) {
            SHIP_COLOR_CYAN -> "Tyrkysová"
            SHIP_COLOR_GREEN -> "Zelená"
            SHIP_COLOR_RED -> "Červená"
            else -> "Tyrkysová"
        }
    }
}
