import javafx.animation.KeyFrame
import javafx.animation.KeyValue
import javafx.animation.Timeline
import javafx.animation.TranslateTransition
import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import javafx.util.Duration
import kotlin.math.*
import kotlin.random.Random
import kotlin.random.Random.Default.nextBoolean
import kotlin.random.Random.Default.nextDouble

abstract class Species(
    xCoor: Double,
    yCoor: Double,
    radius: Double,
    var energy: Double,
    map: Map?,
    val color: Color
) : Renderable(xCoor, yCoor, radius, map), Steppable {


    var atHome: Boolean = false
    var searchingHome = false
    var stopped: Boolean = false
    var duplicateAnimation = false
    var currentEnergy = energy
    var bouncedYtimeAgo = 0
    var bouncedXtimeAgo = 0
    var consumed: Int = 0
    var xVelocity: Double = 0.0
    var yVelocity: Double = 0.0
    var speed: Double = 0.0
    var sensorCounter = 0
    var sensitivity: Double = 3.0
    var mutationRate: Double = 1.5

    constructor(
        xCoor: Double,
        yCoor: Double,
        radius: Double,
        energy: Double,
        xVelocity: Double,
        yVelocity: Double,
        map: Map,
        color: Color
    ) : this(
        xCoor, yCoor, radius, energy, 0.0, map,
        color
    ) {
        speed = sqrt(xVelocity.pow(2) + yVelocity.pow(2))
        this.xVelocity = xVelocity
        this.yVelocity = yVelocity
    }

    constructor(
        xCoor: Double,
        yCoor: Double,
        radius: Double,
        energy: Double,
        newXVelocity: Double,
        newYVelocity: Double,
        speed: Double,
        map: Map,
        color: Color
    ) : this(xCoor, yCoor, radius, energy, speed, map, color) {
        this.xVelocity = newXVelocity
        this.yVelocity = newYVelocity
    }

    constructor(
        xCoor: Double,
        yCoor: Double,
        radius: Double,
        energy: Double,
        speed: Double,
        map: Map?,
        color: Color
    ) : this(
        xCoor,
        yCoor,
        radius,
        energy,
        map,
        color
    ) {
        this.speed = speed
        xVelocity = nextDouble(-1.0, 1.0) * speed
        yVelocity = sqrt(speed.pow(2) - xVelocity.pow(2))
        val rnd = nextBoolean()
        if (rnd) yVelocity *= -1
    }


    fun calculateNewSpeedComponents() {
        xVelocity = nextDouble(-1.0, 1.0) * speed
        yVelocity = sqrt(speed.pow(2) - xVelocity.pow(2))
        val rnd = nextBoolean()
        if (rnd) yVelocity *= -1
    }


    fun checkCollision(renderable: Renderable, timer: Timer) {
        val xDiffPow = (xCoor - renderable.xCoor).pow(2)
        val yDiffPow = (yCoor - renderable.yCoor).pow(2)
        val centerDist = sqrt(xDiffPow + yDiffPow)
        if (centerDist < radius / 2 + renderable.radius / 2) {
            collide(renderable, timer)
        }
    }


    fun stopSpecies() {
        xVelocity = 0.0
        yVelocity = 0.0
        stopped = true
    }

    override fun render(gc: GraphicsContext) {
        gc.fill = color
        gc.fillOval(xCoor - radius / 2, yCoor - radius / 2, radius, radius)

        if (map!!.gameMode != 1) {
            gc.strokeOval(
                xCoor - radius / 2 * sensitivity,
                yCoor - radius / 2 * sensitivity,
                radius * sensitivity,
                radius * sensitivity
            )
        }

    }

    fun mutateSpeedandSensitivity(
        newXVelocity: Double,
        newYVelocity: Double,
        speciesName: String,
        mutationRate: Double
    ): Species {

        val mutateSpeed = Random.nextBoolean()
        var newSpeed = speed
        var newColor = color
        var newSensitivity = sensitivity

        if (mutateSpeed) {
            val higherSpeed = Random.nextBoolean()
            if (higherSpeed) {
                newSpeed *= mutationRate
                newSensitivity /= mutationRate
                newColor = color.brighter()
            } else {
                newSpeed /= mutationRate
                newSensitivity *= mutationRate
                newColor = color.darker()
            }
        }

        when (speciesName) {
            "Greedy" -> return Greedy(
                xCoor,
                yCoor,
                radius,
                energy,
                newXVelocity,
                newYVelocity,
                newSpeed,
                newSensitivity,
                map!!,
                newColor,
                mutationRate
            )
            "Cooperative" -> return Cooperative(
                xCoor,
                yCoor,
                radius,
                energy,
                newXVelocity,
                newYVelocity,
                newSpeed,
                newSensitivity,
                map!!,
                newColor,
                mutationRate
            )
            "BigChungus" -> return BigChungus(
                xCoor,
                yCoor,
                radius,
                energy,
                newXVelocity,
                newYVelocity,
                newSpeed,
                newSensitivity,
                map!!,
                newColor,
                mutationRate
            )
            "Player" -> return PlayerSpecies(
                xCoor,
                yCoor,
                radius,
                energy,
                newXVelocity,
                newYVelocity,
                newSpeed,
                newSensitivity,
                map!!,
                newColor,
                mutationRate
            )
            else -> return BigChungus(
                xCoor,
                yCoor,
                radius,
                energy,
                newXVelocity,
                newYVelocity,
                newSpeed,
                newSensitivity,
                map!!,
                newColor,
                mutationRate
            )
        }


    }


    open fun prepareForNextRound() {
        atHome = false
        stopped = false
        consumed = 0
        duplicateAnimation = false
        bouncedYtimeAgo = 0
        bouncedXtimeAgo = 0

        if (xCoor - radius / 2 < 0) xCoor = radius / 2
        if (xCoor + radius / 2 > 1024) xCoor = 1024 - radius / 2
        if (yCoor - radius / 2 < 0) yCoor = radius / 2
        if (yCoor + radius / 2 > 1024) yCoor = 1024 - radius / 2

        calculateNewSpeedComponents()
        searchingHome = false
        currentEnergy = energy
    }

    fun calculateVelocitiesToDest(x: Double, y: Double, distance: Double) {

        val xDiff = x - xCoor
        val yDiff = y - yCoor

        val xPortion = xDiff / distance
        val yPortion = yDiff / distance

        xVelocity = xPortion * speed
        yVelocity = yPortion * speed

        /*if(xDiff < 0) xVelocity*=-1
        if (yDiff < 0) yVelocity*=-1*/
    }

    open fun searchHome() {
        searchingHome = true
        val left = xCoor
        val right = 1024 - xCoor
        val top = yCoor
        val bottom = 1024 - yCoor
        val distances = mutableListOf(left, right, top, bottom)

        when (distances.min()) {
            left -> {
                xVelocity = speed * -1
                yVelocity = 0.0
            }
            right -> {
                xVelocity = speed
                yVelocity = 0.0
            }
            top -> {
                xVelocity = 0.0
                yVelocity = speed * -1
            }
            bottom -> {
                xVelocity = 0.0
                yVelocity = speed
            }
        }

    }

    open fun senseFood(sensitivity: Double, allfood: MutableList<Food>) {

        val senseRadius = radius / 2 * sensitivity

        var minDist = 1024.0
        var destX = 0.0
        var destY = 0.0
        var foundFood = false
        val iterator = allfood.iterator()
        while (iterator.hasNext()) {
            val currFood = iterator.next()
            val distance = sqrt((xCoor - currFood.xCoor).pow(2) + (yCoor - currFood.yCoor).pow(2))
            if (distance > senseRadius) continue
            if (distance < minDist) {
                minDist = distance
                destX = currFood.xCoor
                destY = currFood.yCoor
                foundFood = true
                break
            }
        }

        if (foundFood) {
            calculateVelocitiesToDest(destX, destY, minDist)
        }


    }

    fun sensePrey(sensitivity: Double, allfood: MutableList<Species>) {

        val senseRadius = radius / 2 * sensitivity

        var minDist = 1024.0
        var destX = 0.0
        var destY = 0.0
        var foundFood = false
        val iterator = allfood.iterator()
        loop@ while (iterator.hasNext()) {
            val currFood = iterator.next()
            when (currFood) {
                is BigChungus -> {
                    continue@loop
                }
                else -> {
                    if (currFood is PlayerSpecies && currFood.tactics == 3) continue@loop
                    val distance = sqrt((xCoor - currFood.xCoor).pow(2) + (yCoor - currFood.yCoor).pow(2))
                    if (distance > senseRadius || currFood.atHome) continue@loop
                    if (distance < minDist) {
                        minDist = distance
                        destX = currFood.xCoor
                        destY = currFood.yCoor
                        foundFood = true
                        break@loop
                    }
                }
            }

        }

        if (foundFood) {
            calculateVelocitiesToDest(destX, destY, minDist)
        }


    }

    open fun sensePredator(sensitivity: Double, predators: MutableList<Species>) {
        val senseRadius = radius / 2 * sensitivity

        var minDist = 1024.0
        var destX = 0.0
        var destY = 0.0
        var foundPredator = false
        val iterator = predators.iterator()
        loop@ while (iterator.hasNext()) {
            val creature = iterator.next()
            when (creature) {
                is BigChungus -> {
                    val distance = sqrt((xCoor - creature.xCoor).pow(2) + (yCoor - creature.yCoor).pow(2))
                    if (distance > senseRadius) continue@loop
                    if (distance < minDist) {
                        minDist = distance
                        destX = xCoor + (xCoor - creature.xCoor)
                        destY = yCoor + (yCoor - creature.yCoor)
                        foundPredator = true
                        break@loop
                    }
                }
                is PlayerSpecies -> {
                    if (creature.tactics != 3) return
                    val distance = sqrt((xCoor - creature.xCoor).pow(2) + (yCoor - creature.yCoor).pow(2))
                    if (distance > senseRadius) continue@loop
                    if (distance < minDist) {
                        minDist = distance
                        destX = xCoor + (xCoor - creature.xCoor)
                        destY = yCoor + (yCoor - creature.yCoor)
                        foundPredator = true
                        break@loop
                    }
                }
            }

        }

        if (foundPredator) {
            calculateVelocitiesToDest(destX, destY, minDist)
        }
    }

    abstract fun replicate(timer: Timer)

    open fun postDay(timer: Timer) {
        if (atHome)
            when (consumed) {
                0 -> remove(timer)
                1 -> return
                else -> replicate(timer)
            }
        else {
            remove(timer)
        }
    }

    abstract fun collide(renderable: Renderable, timer: Timer)
}

