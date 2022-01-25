import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import kotlin.math.pow
import kotlin.math.sqrt

class Dummy(
    xCoor: Double,
    yCoor: Double,
    radius: Double = 25.0,
    energy: Double,
    speed: Double,
    map: Map,
    color: Color
) : Species(
    xCoor, yCoor, radius, energy, speed, map,
    color
) {


    constructor(
        xCoor: Double,
        yCoor: Double,
        radius: Double,
        energy: Double,
        xVelocity: Double,
        yVelocity: Double,
        map: Map,
        color: Color
    ) : this(xCoor, yCoor, radius, energy, 0.0, map, color) {
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
    ) : this(xCoor, yCoor, radius, energy, speed, map,color) {
        this.xVelocity = newXVelocity
        this.yVelocity = newYVelocity
    }


    override fun replicate(timer: Timer) {
        var newXVelocity: Double = 0.0
        var newYVelocity: Double = 0.0
        if (xCoor < yCoor) {

            if (xCoor < 16) {
                newYVelocity = 5.0
            } else {
                newXVelocity = 5.0
            }
        } else {

            if (yCoor < 16) {
                newXVelocity = -5.0
            } else newYVelocity = -5.0

        }

        val newBorn = Dummy(xCoor, yCoor, radius, 0.5, newXVelocity, newYVelocity, speed, map!!,color)
        timer.toAdd.add(newBorn)
    }


    override fun collide(renderable: Renderable, timer: Timer) {
        when (renderable) {
            is Food -> {
                consumed++
                searchHome()
                renderable.remove(timer)
            }
            is Dummy -> {

            }
        }
    }




    override fun step(time: Double) {

        if (stopped) return

        energy -= speed * time / 100.0

        atHome = xCoor < 16 || yCoor < 16 || xCoor > 1008 || yCoor > 1008

        if (energy < 0) stopSpecies()
        else {
            if (consumed > 0) {
                if (atHome) stopSpecies()
            }

        }

        if (xCoor + radius / 2 > 1024 || xCoor - radius / 2 < 0) xVelocity *= -1
        if (yCoor + radius / 2 > 1024 || yCoor - radius / 2 < 0) yVelocity *= -1

        xCoor += xVelocity * time
        yCoor += yVelocity * time

    }
}