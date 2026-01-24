package com.example.spaceboatgame

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.view.MotionEvent
import android.view.View
import kotlin.random.Random

class GameView(context: Context) : View(context) {

    // Nastavení hry
    private val settings = GameSettings(context)

    // Herní objekty
    private val ship = Ship()
    private val coins = mutableListOf<Coin>()
    private val obstacles = mutableListOf<Obstacle>()
    private val stars = mutableListOf<Star>()

    // Skóre a stav hry
    private var score = 0
    private var level = 1
    private var gameOver = false
    private var isPaused = false
    private var resultSaved = false

    // Barvy a styly
    private val paint = Paint().apply {
        isAntiAlias = true
    }

    private val textPaint = Paint().apply {
        color = Color.WHITE
        textSize = 60f
        isAntiAlias = true
    }

    private val gameOverPaint = Paint().apply {
        color = Color.RED
        textSize = 100f
        isAntiAlias = true
        textAlign = Paint.Align.CENTER
    }

    private val restartPaint = Paint().apply {
        color = Color.WHITE
        textSize = 50f
        isAntiAlias = true
        textAlign = Paint.Align.CENTER
    }

    private val pausePaint = Paint().apply {
        color = Color.WHITE
        textSize = 45f
        isAntiAlias = true
        style = Paint.Style.FILL
    }

    private val pauseTextPaint = Paint().apply {
        color = Color.YELLOW
        textSize = 90f
        isAntiAlias = true
        textAlign = Paint.Align.CENTER
    }

    // Časovače
    private var coinSpawnTimer = 0
    private var obstacleSpawnTimer = 0
    private var lastUpdateTime = System.currentTimeMillis()

    init {
        // Inicializace hvězd na pozadí
        repeat(50) {
            stars.add(Star(
                Random.nextFloat() * 1080f,
                Random.nextFloat() * 1920f,
                Random.nextFloat() * 3f + 1f
            ))
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val currentTime = System.currentTimeMillis()
        val deltaTime = (currentTime - lastUpdateTime) / 1000f
        lastUpdateTime = currentTime

        // Nastavení velikosti obrazovky
        if (ship.x == 0f) {
            ship.x = width / 2f
            ship.y = height - 200f
        }

        // Černé pozadí vesmíru
        canvas.drawColor(Color.BLACK)

        // Vykreslení hvězd
        drawStars(canvas, deltaTime)

        if (gameOver) {
            // Game Over obrazovka
            canvas.drawText("GAME OVER", width / 2f, height / 2f - 200f, gameOverPaint)
            canvas.drawText("Skóre: $score", width / 2f, height / 2f - 50f, restartPaint)
            canvas.drawText("Klikni pro restart", width / 2f, height / 2f + 50f, restartPaint)

            // Tlačítko výsledků
            drawResultsButton(canvas)

            // Tlačítko nastavení
            drawSettingsButton(canvas)

            // Pokračovat v animaci i během game over
            invalidate()
            return
        }

        // Vykreslení tlačítka pauzy
        drawPauseButton(canvas)

        // Vykreslení tlačítka nastavení (viditelné i během hry)
        drawSettingsButton(canvas)

        if (isPaused) {
            // Pauza obrazovka
            // Vykreslení lodi a objektů (zmrazené)
            updateCoins(canvas, 0f)
            updateObstacles(canvas, 0f)
            drawShip(canvas)

            // Vykreslení skóre a levelu
            canvas.drawText("Skóre: $score", 50f, 100f, textPaint)
            level = (score / 20) + 1
            canvas.drawText("Lvl: $level", 50f, 180f, textPaint)

            // Pauza text
            canvas.drawText("PAUZA", width / 2f, height / 2f, pauseTextPaint)
            canvas.drawText("Klikni kamkoliv pro pokračování", width / 2f, height / 2f + 100f, restartPaint)

            // Tlačítko nastavení
            drawSettingsButton(canvas)

            // Pokračovat v animaci i během pauzy
            invalidate()
            return
        }

        // Aktualizace a vykreslení mincí
        updateCoins(canvas, deltaTime)

        // Aktualizace a vykreslení překážek
        updateObstacles(canvas, deltaTime)

        // Vykreslení lodi
        drawShip(canvas)

        // Aktualizace levelu podle skóre
        level = (score / 20) + 1

        // Vykreslení skóre a levelu
        canvas.drawText("Skóre: $score", 50f, 100f, textPaint)
        canvas.drawText("Lvl: $level", 50f, 180f, textPaint)

        // Spawn nových mincí
        coinSpawnTimer++
        if (coinSpawnTimer > 60) {
            coins.add(Coin(Random.nextFloat() * width))
            coinSpawnTimer = 0
        }

        // Spawn nových překážek (asteroidů) - progresivní obtížnost
        obstacleSpawnTimer++
        val spawnInterval = (150 - ((score / 20) * 5)).coerceAtLeast(30)
        if (obstacleSpawnTimer > spawnInterval) {
            obstacles.add(Obstacle(Random.nextFloat() * (width - 60) + 30))
            obstacleSpawnTimer = 0
        }

        // Pokračovat v animaci
        invalidate()
    }

    private fun drawStars(canvas: Canvas, deltaTime: Float) {
        paint.color = Color.WHITE
        stars.forEach { star ->
            star.y += star.speed * 100 * deltaTime
            if (star.y > height) {
                star.y = 0f
                star.x = Random.nextFloat() * width
            }
            canvas.drawCircle(star.x, star.y, star.size, paint)
        }
    }

    private fun drawShip(canvas: Canvas) {
        // Použití barvy z nastavení
        paint.color = settings.getShipColorValue()

        // Kreslení vesmírné lodi (trojúhelník)
        val path = Path().apply {
            moveTo(ship.x, ship.y - 40f)  // Špička
            lineTo(ship.x - 30f, ship.y + 40f)  // Levý roh
            lineTo(ship.x + 30f, ship.y + 40f)  // Pravý roh
            close()
        }
        canvas.drawPath(path, paint)

        // Okno lodi
        paint.color = Color.BLUE
        canvas.drawCircle(ship.x, ship.y, 15f, paint)

        // Motory (oheň)
        paint.color = Color.RED
        canvas.drawCircle(ship.x - 15f, ship.y + 45f, 8f, paint)
        canvas.drawCircle(ship.x + 15f, ship.y + 45f, 8f, paint)

        paint.color = Color.YELLOW
        canvas.drawCircle(ship.x - 15f, ship.y + 48f, 5f, paint)
        canvas.drawCircle(ship.x + 15f, ship.y + 48f, 5f, paint)
    }

    private fun updateCoins(canvas: Canvas, deltaTime: Float) {
        val iterator = coins.iterator()
        while (iterator.hasNext()) {
            val coin = iterator.next()
            coin.y += 300 * deltaTime

            // Kontrola kolize s lodí
            val distance = Math.sqrt(
                ((ship.x - coin.x) * (ship.x - coin.x) +
                (ship.y - coin.y) * (ship.y - coin.y)).toDouble()
            )

            if (distance < 50) {
                score++
                iterator.remove()
                continue
            }

            // Odstranění mincí mimo obrazovku
            if (coin.y > height) {
                iterator.remove()
                continue
            }

            // Vykreslení mince
            drawCoin(canvas, coin)
        }
    }

    private fun updateObstacles(canvas: Canvas, deltaTime: Float) {
        val iterator = obstacles.iterator()
        while (iterator.hasNext()) {
            val obstacle = iterator.next()

            // Progresivní zvyšování rychlosti podle skóre
            val speedMultiplier = 200 + ((score / 20) * 10)
            obstacle.y += speedMultiplier * deltaTime

            // Kontrola kolize s lodí
            val distance = Math.sqrt(
                ((ship.x - obstacle.x) * (ship.x - obstacle.x) +
                (ship.y - obstacle.y) * (ship.y - obstacle.y)).toDouble()
            )

            if (distance < 60) {
                if (!gameOver) {
                    gameOver = true
                    // Uložit výsledek hry pouze jednou
                    if (!resultSaved) {
                        settings.saveGameResult(score, level)
                        resultSaved = true
                    }
                }
                iterator.remove()
                continue
            }

            // Odstranění překážek mimo obrazovku
            if (obstacle.y > height) {
                iterator.remove()
                continue
            }

            // Vykreslení překážky
            drawObstacle(canvas, obstacle)
        }
    }

    private fun drawCoin(canvas: Canvas, coin: Coin) {
        // Zlatá mince
        paint.color = Color.YELLOW
        canvas.drawCircle(coin.x, coin.y, 25f, paint)

        paint.color = Color.rgb(255, 215, 0) // Tmavší zlatá
        canvas.drawCircle(coin.x, coin.y, 20f, paint)

        // Symbol $
        paint.color = Color.rgb(184, 134, 11)
        paint.textSize = 30f
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText("$", coin.x, coin.y + 10f, paint)
        paint.textAlign = Paint.Align.LEFT
    }

    private fun drawObstacle(canvas: Canvas, obstacle: Obstacle) {
        // Asteroid - šedý kámen s drsným povrchem
        paint.color = Color.GRAY
        canvas.drawCircle(obstacle.x, obstacle.y, 35f, paint)

        paint.color = Color.DKGRAY
        canvas.drawCircle(obstacle.x, obstacle.y, 30f, paint)

        // Krátery na asteroidu
        paint.color = Color.rgb(60, 60, 60)
        canvas.drawCircle(obstacle.x - 10f, obstacle.y - 8f, 8f, paint)
        canvas.drawCircle(obstacle.x + 12f, obstacle.y + 5f, 6f, paint)
        canvas.drawCircle(obstacle.x - 5f, obstacle.y + 12f, 5f, paint)
    }

    private fun drawPauseButton(canvas: Canvas) {
        // Tlačítko pauzy v pravém horním rohu (vlevo od nastavení)
        val buttonX = width - 320f
        val buttonY = 90f
        val buttonWidth = 55f
        val buttonHeight = 70f

        if (isPaused) {
            // Zobrazit trojúhelník (play symbol)
            paint.color = Color.WHITE
            val path = Path().apply {
                moveTo(buttonX, buttonY - buttonHeight / 2)
                lineTo(buttonX, buttonY + buttonHeight / 2)
                lineTo(buttonX + buttonWidth, buttonY)
                close()
            }
            canvas.drawPath(path, paint)
        } else {
            // Zobrazit dvě čáry (pause symbol ||)
            paint.color = Color.WHITE
            canvas.drawRect(buttonX, buttonY - buttonHeight / 2,
                           buttonX + 18f, buttonY + buttonHeight / 2, paint)
            canvas.drawRect(buttonX + 37f, buttonY - buttonHeight / 2,
                           buttonX + 55f, buttonY + buttonHeight / 2, paint)
        }
    }

    private fun drawSettingsButton(canvas: Canvas) {
        // Tlačítko nastavení v pravém horním rohu
        val buttonX = width - 70f
        val buttonY = 90f
        val buttonSize = 100f

        // Kruh na pozadí
        paint.color = Color.rgb(40, 40, 60)
        canvas.drawCircle(buttonX, buttonY, buttonSize / 2, paint)

        // Okraj
        paint.color = Color.WHITE
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 3f
        canvas.drawCircle(buttonX, buttonY, buttonSize / 2, paint)
        paint.style = Paint.Style.FILL

        // Ikona ozubeného kolečka ⚙
        paint.color = Color.WHITE
        paint.textSize = 80f
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText("⚙", buttonX, buttonY + 25f, paint)
        paint.textAlign = Paint.Align.LEFT
    }

    private fun drawResultsButton(canvas: Canvas) {
        // Tlačítko výsledků uprostřed obrazovky
        val buttonX = width / 2f
        val buttonY = height / 2f + 150f
        val buttonWidth = 400f
        val buttonHeight = 100f

        // Tlačítko na pozadí
        paint.color = Color.rgb(50, 150, 50)
        canvas.drawRect(
            buttonX - buttonWidth / 2,
            buttonY - buttonHeight / 2,
            buttonX + buttonWidth / 2,
            buttonY + buttonHeight / 2,
            paint
        )

        // Okraj tlačítka
        paint.color = Color.WHITE
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 5f
        canvas.drawRect(
            buttonX - buttonWidth / 2,
            buttonY - buttonHeight / 2,
            buttonX + buttonWidth / 2,
            buttonY + buttonHeight / 2,
            paint
        )
        paint.style = Paint.Style.FILL

        // Text tlačítka
        paint.color = Color.WHITE
        paint.textSize = 50f
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText("🏆 VÝSLEDKY", buttonX, buttonY + 18f, paint)
        paint.textAlign = Paint.Align.LEFT
    }

    private fun restartGame() {
        gameOver = false
        isPaused = false
        score = 0
        level = 1
        resultSaved = false
        coins.clear()
        obstacles.clear()
        coinSpawnTimer = 0
        obstacleSpawnTimer = 0
        ship.x = width / 2f
        ship.y = height - 200f
        invalidate()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                // Kontrola kliknutí na tlačítko nastavení (kdykoliv)
                val settingsButtonX = width - 70f
                val settingsButtonY = 90f
                val settingsButtonRadius = 50f

                val distanceSettings = Math.sqrt(
                    ((event.x - settingsButtonX) * (event.x - settingsButtonX) +
                    (event.y - settingsButtonY) * (event.y - settingsButtonY)).toDouble()
                )

                if (distanceSettings < settingsButtonRadius + 20) {
                    // Otevřít obrazovku nastavení
                    val intent = Intent(context, SettingsActivity::class.java)
                    context.startActivity(intent)
                    return true
                }

                // Kontrola kliknutí na tlačítko výsledků (pouze během game over)
                if (gameOver) {
                    val resultsButtonX = width / 2f
                    val resultsButtonY = height / 2f + 150f
                    val resultsButtonWidth = 400f
                    val resultsButtonHeight = 100f

                    if (event.x >= resultsButtonX - resultsButtonWidth / 2 &&
                        event.x <= resultsButtonX + resultsButtonWidth / 2 &&
                        event.y >= resultsButtonY - resultsButtonHeight / 2 &&
                        event.y <= resultsButtonY + resultsButtonHeight / 2) {
                        // Otevřít obrazovku výsledků
                        val intent = Intent(context, GameResultsActivity::class.java)
                        context.startActivity(intent)
                        return true
                    }

                    // Kliknutí mimo tlačítko výsledků = restart hry
                    restartGame()
                    return true
                }

                // Během pauzy - jakékoliv kliknutí pokračuje ve hře
                if (isPaused) {
                    isPaused = false
                    return true
                }

                // Kontrola kliknutí na tlačítko pauzy (pouze během hry)
                val pauseButtonX = width - 320f
                val pauseButtonY = 90f

                // Velká klikací oblast pro snadné ovládání
                if (event.x >= pauseButtonX - 40f && event.x <= pauseButtonX + 95f &&
                    event.y >= pauseButtonY - 60f && event.y <= pauseButtonY + 60f) {
                    isPaused = true
                    return true
                }

                // Normální ovládání lodi
                ship.x = event.x
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                if (!gameOver && !isPaused) {
                    ship.x = event.x
                }
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    // Datové třídy pro herní objekty
    data class Ship(var x: Float = 0f, var y: Float = 0f)
    data class Coin(var x: Float, var y: Float = 0f)
    data class Obstacle(var x: Float, var y: Float = 0f)
    data class Star(var x: Float, var y: Float, val size: Float, val speed: Float = Random.nextFloat() + 0.5f)
}
