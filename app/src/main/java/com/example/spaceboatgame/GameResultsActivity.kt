package com.example.spaceboatgame

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*

class GameResultsActivity : Activity() {

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
            text = "🏆 VÝSLEDKY"
            textSize = 32f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            setPadding(0, 20, 0, 40)
        }
        mainLayout.addView(titleText)

        // Načíst výsledky
        val results = settings.getGameResults()

        if (results.isEmpty()) {
            // Pokud nejsou žádné výsledky
            val noResultsText = TextView(this).apply {
                text = "Zatím žádné výsledky.\nZahrajte si hru!"
                textSize = 20f
                setTextColor(Color.LTGRAY)
                gravity = Gravity.CENTER
                setPadding(0, 100, 0, 100)
            }
            mainLayout.addView(noResultsText)
        } else {
            // Zobrazit výsledky
            results.forEachIndexed { index, result ->
                val resultLayout = createResultItem(index + 1, result)
                mainLayout.addView(resultLayout)
            }

            // Tlačítko pro vymazání výsledků
            val clearButton = Button(this).apply {
                text = "🗑️ VYMAZAT VÝSLEDKY"
                textSize = 18f
                setTextColor(Color.WHITE)
                setBackgroundColor(Color.rgb(150, 50, 50))
                setPadding(30, 20, 30, 20)
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    topMargin = 40
                }
                setOnClickListener {
                    settings.clearGameResults()
                    recreate() // Obnovit aktivitu
                }
            }
            mainLayout.addView(clearButton)
        }

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
                topMargin = 40
            }
            setOnClickListener { finish() }
        }
        mainLayout.addView(backButton)

        // ScrollView pro případ mnoha výsledků
        val scrollView = ScrollView(this).apply {
            addView(mainLayout)
        }

        setContentView(scrollView)
    }

    private fun createResultItem(position: Int, result: GameResult): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.rgb(40, 40, 70))
            setPadding(30, 20, 30, 20)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 15
            }

            // Pozice a jméno hráče
            val headerText = TextView(this@GameResultsActivity).apply {
                text = "#$position - ${result.playerName}"
                textSize = 22f
                setTextColor(Color.YELLOW)
                gravity = Gravity.START
            }
            addView(headerText)

            // Skóre a level
            val scoreLayout = LinearLayout(this@GameResultsActivity).apply {
                orientation = LinearLayout.HORIZONTAL
                setPadding(0, 10, 0, 5)
            }

            val scoreText = TextView(this@GameResultsActivity).apply {
                text = "Skóre: ${result.score}  |  Level: ${result.level}"
                textSize = 18f
                setTextColor(Color.WHITE)
            }
            scoreLayout.addView(scoreText)
            addView(scoreLayout)

            // Datum a čas
            val dateText = TextView(this@GameResultsActivity).apply {
                text = "📅 ${result.getFormattedDate()}"
                textSize = 14f
                setTextColor(Color.LTGRAY)
                setPadding(0, 5, 0, 0)
            }
            addView(dateText)
        }
    }
}
