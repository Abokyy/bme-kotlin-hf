import javafx.scene.paint.Color
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.random.Random

class BigChungus(
    xCoor: Double,
    yCoor: Double,
    radius: Double,
    energy: Double,
    speed: Double,
    map: Map?,
    color: Color
) : Species(xCoor, yCoor, radius, energy, speed, map, color) {

    var starvingDays: Int = 0


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

    constructor(
        xCoor: Double,
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

        val newBorn = mutateSpeedandSensitivity(newXVelocity, newYVelocity, "BigChungus", mutationRate)
        timer.toAdd.add(newBorn)
    }

    override fun postDay(timer: Timer) {
        if (atHome) {
            when (consumed) {
                0 -> starvingDays++
                else -> {
                    var x = 0
                    if (starvingDays == 0)
                        while (x < 1) {
                            replicate(timer)
                            x++
                        }
                    starvingDays = 0
                }
            }
            if (starvingDays > 1) remove(timer)
        } else {
            remove(timer)
        }
    }

    override fun collide(renderable: Renderable, timer: Timer) {
        when (renderable) {
            is Food -> {
                //consumed++
                //renderable.remove(timer)
            }
            is BigChungus -> {

            }


            is Species -> {
                if (renderable is PlayerSpecies && renderable.tactics == 3) return //predators cannot eat each other
                if (!renderable.atHome) {
                    consumed++
                    renderable.remove(timer)
                } else {
                    if (atHome && stopped) {
                        val left = xCoor
                        val right = 1024 - xCoor
                        val top = yCoor
                        val bottom = 1024 - yCoor
                        val distances = mutableListOf(left, right, top, bottom)

                        when (distances.min()) {
                            left -> {
                                yCoor += 25
                            }
                            right -> {
                                yCoor -= 25
                            }
                            top -> {
                                xCoor += 25
                            }
                            bottom -> {
                                xCoor -= 25
                            }
                        }
                    }
                }
            }
        }
    }


    override fun searchHome() {
        searchingHome = true
        xVelocity = 0.0
        yVelocity = speed * -1
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
            if (currentEnergy > 6) {
                sensePrey(sensitivity, map!!.creatures)
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