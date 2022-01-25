import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import kotlin.random.Random.Default.nextBoolean

class Greedy(
    xCoor: Double,
    yCoor: Double,
    radius: Double = 25.0,
    energy: Double,
    speed: Double,
    map: Map?,
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
        newXVelocity: Double,
        newYVelocity: Double,
        speed: Double,
        sensitivity: Double,
        map: Map,
        color: Color,
        mutateRate: Double
    ) : this(
        xCoor, yCoor, radius, energy, speed, map,
        color
    ) {
        this.xVelocity = newXVelocity
        this.yVelocity = newYVelocity
        this.sensitivity = sensitivity
        duplicateAnimation = true
        currentEnergy = 2.0
        this.mutationRate = mutateRate
    }

    constructor( xCoor: Double,
                 yCoor: Double,
                 radius: Double,
                 energy: Double,
                 speed: Double,
                 sensitivity: Double,
                 map: Map?,
                 color: Color,
                 mutateRate: Double
    ) : this(xCoor, yCoor, radius, energy, speed, map, color) {
        this.sensitivity = sensitivity
        this.mutationRate = mutateRate
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

        val dir = nextBoolean()
        if (dir) {
            newXVelocity *= -1
            newYVelocity *= -1
        }

        val newBorn = mutateSpeedandSensitivity(newXVelocity, newYVelocity, "Greedy", mutationRate)
        timer.toAdd.add(newBorn)
    }

    override fun collide(renderable: Renderable, timer: Timer) {
        when (renderable) {
            is Food -> {
                consumed++
                renderable.remove(timer)
            }
            else -> {

            }
        }
    }






    override fun render(gc: GraphicsContext) {
        super.render(gc)

    }





    override fun prepareForNextRound() {
        super.prepareForNextRound()
        sensorCounter = 0
    }




    override fun step(time: Double) {
        if (stopped) return
        currentEnergy -= speed * time / 100.0
        atHome = xCoor < 16 || yCoor < 16 || xCoor > 1008 || yCoor > 1008
        if ((xCoor + radius / 2 > 1024 || xCoor - radius / 2 < 0) && bouncedXtimeAgo >= 10) {
            xVelocity *= -1
            bouncedXtimeAgo = 0
        } else {
            bouncedXtimeAgo++
        }
        if ((yCoor + radius / 2 > 1024 || yCoor - radius / 2 < 0) && bouncedYtimeAgo >= 10) {
            yVelocity *= -1
            bouncedYtimeAgo = 0
        } else {
            bouncedYtimeAgo++
        }


        if (currentEnergy < 0) stopSpecies()
        else {
            if (currentEnergy > 3) {
                senseFood(sensitivity, map!!.foods)
                if (!atHome) sensePredator(sensitivity, map.creatures)
                sensorCounter = 0
            } else {
                if (!duplicateAnimation) searchHome()
                sensorCounter++
            }
            if (atHome && searchingHome) stopSpecies()
        }



        xCoor += xVelocity * time
        yCoor += yVelocity * time
    }
}