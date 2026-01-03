package com.example.spaceboatgame

import android.content.Context
import android.content.SharedPreferences

class GameSettings(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("SpaceBoatGameSettings", Context.MODE_PRIVATE)

    companion object {
        // Klíče pro SharedPreferences
        private const val KEY_DIFFICULTY = "difficulty"
        private const val KEY_PLAYER_NAME = "player_name"
        private const val KEY_SHIP_COLOR = "ship_color"
        private const val KEY_SOUND_ENABLED = "sound_enabled"
        private const val KEY_VIBRATION_ENABLED = "vibration_enabled"

        // Úrovně obtížnosti
        const val DIFFICULTY_EASY = 0
        const val DIFFICULTY_NORMAL = 1
        const val DIFFICULTY_HARD = 2

        // Barvy lodí
        const val SHIP_COLOR_CYAN = 0
        const val SHIP_COLOR_GREEN = 1
        const val SHIP_COLOR_RED = 2
        const val SHIP_COLOR_PURPLE = 3
        const val SHIP_COLOR_GOLD = 4
    }

    // Obtížnost hry
    var difficulty: Int
        get() = prefs.getInt(KEY_DIFFICULTY, DIFFICULTY_NORMAL)
        set(value) = prefs.edit().putInt(KEY_DIFFICULTY, value).apply()

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

    // Pomocné funkce pro obtížnost
    fun getDifficultyName(): String {
        return when (difficulty) {
            DIFFICULTY_EASY -> "Snadná"
            DIFFICULTY_NORMAL -> "Normální"
            DIFFICULTY_HARD -> "Těžká"
            else -> "Normální"
        }
    }

    fun getSpeedMultiplier(): Float {
        return when (difficulty) {
            DIFFICULTY_EASY -> 0.7f
            DIFFICULTY_NORMAL -> 1.0f
            DIFFICULTY_HARD -> 1.5f
            else -> 1.0f
        }
    }

    fun getSpawnRateMultiplier(): Float {
        return when (difficulty) {
            DIFFICULTY_EASY -> 1.5f
            DIFFICULTY_NORMAL -> 1.0f
            DIFFICULTY_HARD -> 0.6f
            else -> 1.0f
        }
    }

    // Pomocná funkce pro barvu lodi
    fun getShipColorValue(): Int {
        return when (shipColor) {
            SHIP_COLOR_CYAN -> android.graphics.Color.CYAN
            SHIP_COLOR_GREEN -> android.graphics.Color.GREEN
            SHIP_COLOR_RED -> android.graphics.Color.RED
            SHIP_COLOR_PURPLE -> android.graphics.Color.rgb(147, 112, 219)
            SHIP_COLOR_GOLD -> android.graphics.Color.rgb(255, 215, 0)
            else -> android.graphics.Color.CYAN
        }
    }

    fun getShipColorName(): String {
        return when (shipColor) {
            SHIP_COLOR_CYAN -> "Tyrkysová"
            SHIP_COLOR_GREEN -> "Zelená"
            SHIP_COLOR_RED -> "Červená"
            SHIP_COLOR_PURPLE -> "Fialová"
            SHIP_COLOR_GOLD -> "Zlatá"
            else -> "Tyrkysová"
        }
    }
}
