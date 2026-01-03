package com.example.spaceboatgame

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.view.MotionEvent
import android.view.View
import kotlin.random.Random

class GameView(context: Context) : View(context) {

    // Herní objekty
    private val ship = Ship()
    private val coins = mutableListOf<Coin>()
    private val stars = mutableListOf<Star>()

    // Skóre
    private var score = 0

    // Barvy a styly
    private val paint = Paint().apply {
        isAntiAlias = true
    }

    private val textPaint = Paint().apply {
        color = Color.WHITE
        textSize = 60f
        isAntiAlias = true
    }

    // Časovače
    private var coinSpawnTimer = 0
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

        // Aktualizace a vykreslení mincí
        updateCoins(canvas, deltaTime)

        // Vykreslení lodi
        drawShip(canvas)

        // Vykreslení skóre
        canvas.drawText("Skóre: $score", 50f, 100f, textPaint)

        // Spawn nových mincí
        coinSpawnTimer++
        if (coinSpawnTimer > 60) {
            coins.add(Coin(Random.nextFloat() * width))
            coinSpawnTimer = 0
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
        paint.color = Color.CYAN

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

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                ship.x = event.x
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    // Datové třídy pro herní objekty
    data class Ship(var x: Float = 0f, var y: Float = 0f)
    data class Coin(var x: Float, var y: Float = 0f)
    data class Star(var x: Float, var y: Float, val size: Float, val speed: Float = Random.nextFloat() + 0.5f)
}
