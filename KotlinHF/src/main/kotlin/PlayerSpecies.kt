import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import kotlin.random.Random

open class PlayerSpecies(
    xCoor: Double,
    yCoor: Double,
    radius: Double,
    energy: Double,
    speed: Double,
    map: Map?,
    color: Color
) : Species(xCoor, yCoor, radius, energy, speed, map, color) {

    var playerControl = false
    var tactics = 1
    var starvingDays = 0

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

    constructor(
        xCoor: Double,
        yCoor: Double,
        radius: Double,
        energy: Double,
        speed: Double,
        sensitivity: Double,
        map: Map?,
        color: Color,
        mutateRate: Double,
        tactics: Int
    ) : this(xCoor, yCoor, radius, energy, speed, map, color) {
        this.sensitivity = sensitivity
        this.mutationRate = mutateRate
        this.tactics = tactics
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

        val newBorn = mutateSpeedandSensitivity(newXVelocity, newYVelocity, "Player", mutationRate) as PlayerSpecies
        newBorn.tactics = tactics
        timer.toAdd.add(newBorn)
    }


    override fun render(gc: GraphicsContext) {
        if (playerControl) {
            gc.fill = Color.WHITE
            gc.fillOval(
                xCoor - radius / 2 * sensitivity,
                yCoor - radius / 2 * sensitivity,
                radius * sensitivity,
                radius * sensitivity
            )
        }
        super.render(gc)
    }

    override fun remove(timer: Timer) {
        super.remove(timer)
        if (playerControl) {
            this.playerControl = false
            val iterator = map!!.creatures.iterator()
            while (iterator.hasNext()) {
                val newControlled = iterator.next()
                if (newControlled is PlayerSpecies) {
                    if (!timer.toRemove.contains(newControlled)) {
                        newControlled.playerControl = true
                        map.controlledCreature = newControlled
                        break
                    }
                }
            }
        }
    }

    override fun collide(renderable: Renderable, timer: Timer) {
        when (renderable) {
            is Food -> {
                if (tactics != 3) {
                    consumed++
                    renderable.remove(timer)
                }
            }
            is PlayerSpecies -> {

            }
            is BigChungus -> {
                //predators cannot eat each other
            }

            is Species -> {
                if (tactics == 3) {
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

            else -> {

            }
        }
    }

    override fun searchHome() {
        if (tactics == 3) {
            searchingHome = true
            xVelocity = 0.0
            yVelocity = speed * -1
        } else
            super.searchHome()
    }

    override fun postDay(timer: Timer) {
        if (tactics != 3) super.postDay(timer)
        else {
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
        /*else {
        if (currentEnergy < 3) {
            if (!duplicateAnimation) searchHome()
        }*/
        if (atHome && searchingHome) stopSpecies()
        //}


        xCoor += xVelocity * time
        yCoor += yVelocity * time

        if (!playerControl) {
            when (tactics) {
                1 -> {//cooperative tactics
                    if (currentEnergy > 3 && consumed < 2) {
                        senseFood(sensitivity, map!!.foods)
                        sensorCounter = 0
                    } else {
                        if (!duplicateAnimation && !searchingHome) searchHome()
                        sensorCounter++
                    }
                }
                2 -> {//greedy tactics
                    if (currentEnergy > 3) {
                        senseFood(sensitivity, map!!.foods)
                        if (!atHome) sensePredator(sensitivity, map.creatures)
                        sensorCounter = 0
                    } else {
                        if (!duplicateAnimation) searchHome()
                        sensorCounter++
                    }
                }
                3 -> {//bigChungus tactics
                    if (currentEnergy > 6) {
                        sensePrey(sensitivity, map!!.creatures)
                        sensorCounter = 0
                    } else {
                        if (!duplicateAnimation) searchHome()
                        sensorCounter++
                    }
                }
            }

        }
    }
}

