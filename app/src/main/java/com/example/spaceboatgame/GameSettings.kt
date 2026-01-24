package com.example.spaceboatgame

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class GameResult(
    val playerName: String,
    val score: Int,
    val level: Int,
    val timestamp: Long
) {
    fun getFormattedDate(): String {
        val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}

class GameSettings(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("SpaceBoatGameSettings", Context.MODE_PRIVATE)

    companion object {
        // Klíče pro SharedPreferences
        private const val KEY_PLAYER_NAME = "player_name"
        private const val KEY_SHIP_COLOR = "ship_color"
        private const val KEY_SOUND_ENABLED = "sound_enabled"
        private const val KEY_VIBRATION_ENABLED = "vibration_enabled"
        private const val KEY_GAME_RESULTS = "game_results"

        // Barvy lodí
        const val SHIP_COLOR_CYAN = 0
        const val SHIP_COLOR_GREEN = 1
        const val SHIP_COLOR_RED = 2

        // Maximální počet uložených výsledků
        private const val MAX_RESULTS = 10
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

    // Uložit výsledek hry
    fun saveGameResult(score: Int, level: Int) {
        val results = getGameResults().toMutableList()
        val newResult = GameResult(
            playerName = playerName,
            score = score,
            level = level,
            timestamp = System.currentTimeMillis()
        )
        results.add(0, newResult) // Přidat na začátek seznamu

        // Omezit počet uložených výsledků
        val limitedResults = results.take(MAX_RESULTS)

        // Uložit do JSON
        val jsonArray = JSONArray()
        limitedResults.forEach { result ->
            val jsonObject = JSONObject()
            jsonObject.put("playerName", result.playerName)
            jsonObject.put("score", result.score)
            jsonObject.put("level", result.level)
            jsonObject.put("timestamp", result.timestamp)
            jsonArray.put(jsonObject)
        }

        prefs.edit().putString(KEY_GAME_RESULTS, jsonArray.toString()).apply()
    }

    // Načíst výsledky hry
    fun getGameResults(): List<GameResult> {
        val resultsJson = prefs.getString(KEY_GAME_RESULTS, "[]") ?: "[]"
        val results = mutableListOf<GameResult>()

        try {
            val jsonArray = JSONArray(resultsJson)
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                results.add(
                    GameResult(
                        playerName = jsonObject.getString("playerName"),
                        score = jsonObject.getInt("score"),
                        level = jsonObject.getInt("level"),
                        timestamp = jsonObject.getLong("timestamp")
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return results
    }

    // Vymazat všechny výsledky
    fun clearGameResults() {
        prefs.edit().putString(KEY_GAME_RESULTS, "[]").apply()
    }
}
