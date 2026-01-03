package com.example.spaceboatgame

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*

class SettingsActivity : Activity() {

    private lateinit var settings: GameSettings

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        settings = GameSettings(this)

        // Vytvoření UI programově
        val mainLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.rgb(20, 20, 40))
            setPadding(40, 40, 40, 40)
        }

        // Nadpis
        val titleText = TextView(this).apply {
            text = "⚙️ NASTAVENÍ"
            textSize = 32f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            setPadding(0, 20, 0, 60)
        }
        mainLayout.addView(titleText)

        // Jméno hráče
        addPlayerNameSetting(mainLayout)

        // Obtížnost
        addDifficultySetting(mainLayout)

        // Barva lodi
        addShipColorSetting(mainLayout)

        // Zvuky
        addSoundSetting(mainLayout)

        // Vibrace
        addVibrationSetting(mainLayout)

        // Tlačítko zpět
        val backButton = Button(this).apply {
            text = "← ZPĚT DO HRY"
            textSize = 20f
            setTextColor(Color.WHITE)
            setBackgroundColor(Color.rgb(50, 100, 200))
            setPadding(30, 30, 30, 30)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = 80
            }
            setOnClickListener { finish() }
        }
        mainLayout.addView(backButton)

        // ScrollView pro případ malé obrazovky
        val scrollView = ScrollView(this).apply {
            addView(mainLayout)
        }

        setContentView(scrollView)
    }

    private fun addPlayerNameSetting(parent: LinearLayout) {
        val label = createLabel("Jméno hráče:")
        parent.addView(label)

        val nameInput = EditText(this).apply {
            setText(settings.playerName)
            textSize = 18f
            setTextColor(Color.WHITE)
            setHintTextColor(Color.LTGRAY)
            setBackgroundColor(Color.rgb(40, 40, 60))
            setPadding(30, 20, 30, 20)
            inputType = InputType.TYPE_CLASS_TEXT
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 40
            }
        }

        nameInput.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val newName = nameInput.text.toString().trim()
                if (newName.isNotEmpty()) {
                    settings.playerName = newName
                }
            }
        }

        parent.addView(nameInput)
    }

    private fun addDifficultySetting(parent: LinearLayout) {
        val label = createLabel("Obtížnost:")
        parent.addView(label)

        val radioGroup = RadioGroup(this).apply {
            orientation = RadioGroup.VERTICAL
            setPadding(20, 10, 20, 10)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 40
            }
        }

        val difficulties = listOf(
            "🟢 Snadná" to GameSettings.DIFFICULTY_EASY,
            "🟡 Normální" to GameSettings.DIFFICULTY_NORMAL,
            "🔴 Těžká" to GameSettings.DIFFICULTY_HARD
        )

        difficulties.forEach { (text, value) ->
            val radioButton = RadioButton(this).apply {
                this.text = text
                textSize = 18f
                setTextColor(Color.WHITE)
                id = View.generateViewId()
                isChecked = settings.difficulty == value
                setPadding(20, 20, 20, 20)
            }

            radioButton.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    settings.difficulty = value
                }
            }

            radioGroup.addView(radioButton)
        }

        parent.addView(radioGroup)
    }

    private fun addShipColorSetting(parent: LinearLayout) {
        val label = createLabel("Barva lodi:")
        parent.addView(label)

        val colorContainer = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            setPadding(0, 10, 0, 10)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 40
            }
        }

        val colors = listOf(
            GameSettings.SHIP_COLOR_CYAN to Color.CYAN,
            GameSettings.SHIP_COLOR_GREEN to Color.GREEN,
            GameSettings.SHIP_COLOR_RED to Color.RED,
            GameSettings.SHIP_COLOR_PURPLE to Color.rgb(147, 112, 219),
            GameSettings.SHIP_COLOR_GOLD to Color.rgb(255, 215, 0)
        )

        colors.forEach { (colorId, colorValue) ->
            val colorButton = Button(this).apply {
                text = if (settings.shipColor == colorId) "✓" else ""
                setBackgroundColor(colorValue)
                setTextColor(Color.WHITE)
                textSize = 24f
                layoutParams = LinearLayout.LayoutParams(100, 100).apply {
                    marginStart = 10
                    marginEnd = 10
                }

                setOnClickListener {
                    settings.shipColor = colorId
                    // Aktualizovat všechna tlačítka
                    recreate()
                }
            }
            colorContainer.addView(colorButton)
        }

        parent.addView(colorContainer)
    }

    private fun addSoundSetting(parent: LinearLayout) {
        val switchLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, 10, 0, 10)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 30
            }
        }

        val label = createLabel("🔊 Zvuky:")
        label.layoutParams = LinearLayout.LayoutParams(
            0,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            1f
        )
        switchLayout.addView(label)

        val soundSwitch = Switch(this).apply {
            isChecked = settings.soundEnabled
            setOnCheckedChangeListener { _, isChecked ->
                settings.soundEnabled = isChecked
            }
        }
        switchLayout.addView(soundSwitch)

        parent.addView(switchLayout)
    }

    private fun addVibrationSetting(parent: LinearLayout) {
        val switchLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, 10, 0, 10)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 30
            }
        }

        val label = createLabel("📳 Vibrace:")
        label.layoutParams = LinearLayout.LayoutParams(
            0,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            1f
        )
        switchLayout.addView(label)

        val vibrationSwitch = Switch(this).apply {
            isChecked = settings.vibrationEnabled
            setOnCheckedChangeListener { _, isChecked ->
                settings.vibrationEnabled = isChecked
            }
        }
        switchLayout.addView(vibrationSwitch)

        parent.addView(switchLayout)
    }

    private fun createLabel(text: String): TextView {
        return TextView(this).apply {
            this.text = text
            textSize = 20f
            setTextColor(Color.WHITE)
            setPadding(0, 20, 0, 10)
        }
    }
}
