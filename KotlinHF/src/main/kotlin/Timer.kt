import javafx.animation.AnimationTimer
import javafx.scene.canvas.GraphicsContext
import javafx.scene.control.Label
import javafx.scene.paint.Color
import java.util.concurrent.TimeUnit
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.random.Random

class Timer(
    val map: Map,
    var lastNanoTime: Long,
    val gc: GraphicsContext,
    val input: MutableList<String>,
    val maxEnergy: Label,
    val playerEnergy: Label
) :
    AnimationTimer() {

    val toRemove = mutableListOf<Renderable>()
    val toAdd = mutableListOf<Renderable>()
    private val renderables = map.renderables
    var afterRound = true
    var added = false

    override fun handle(p0: Long) {


        if (checkIfAllStopped(renderables)) {
            if (afterRound) {
                afterRound = false
                afterRound()
            } else {
                restartMap()
            }
        } else {
            animationLoop(renderables, p0)
        }

        val removeIterator = toRemove.iterator()
        while (removeIterator.hasNext()) {
            renderables.remove(removeIterator.next())
        }
        toRemove.removeAll(toRemove)
    }

    private fun animationLoop(renderables: MutableList<Renderable>, p0: Long) {
        gc.clearRect(0.0, 0.0, 1024.0, 1024.0)
        gc.fill = Color.BLACK
        gc.fillRect(0.0,0.0, 16.0, 1024.0)
        gc.fillRect(0.0,0.0, 1024.0, 16.0)
        gc.fillRect(1008.0,0.0, 16.0, 1024.0)
        gc.fillRect(0.0,1008.0, 1024.0, 16.0)



        val elapsedTime = (p0 - lastNanoTime) / 1_000_000_000_000_000.0
        lastNanoTime = elapsedTime.toLong()


        if (map.gameMode == 1) {

            gc.fill = Color.GRAY
            gc.fillRect(0.0, 0.0, 1024.0, 1024.0)

            if (input.contains("UP")) {
                map.controlledCreature.xVelocity = 0.0
                map.controlledCreature.yVelocity = map.controlledCreature.speed * -1
            }
            if (input.contains("RIGHT")) {
                map.controlledCreature.xVelocity = map.controlledCreature.speed
                map.controlledCreature.yVelocity = 0.0
            }
            if (input.contains("DOWN")) {
                map.controlledCreature.xVelocity = 0.0
                map.controlledCreature.yVelocity = map.controlledCreature.speed
            }
            if (input.contains("LEFT")) {
                map.controlledCreature.xVelocity = map.controlledCreature.speed * -1
                map.controlledCreature.yVelocity = 0.0
            }
            if (input.contains("UP") && input.contains("LEFT")) {
                map.controlledCreature.xVelocity = map.controlledCreature.speed / sqrt(2.0) * -1
                map.controlledCreature.yVelocity = map.controlledCreature.speed / sqrt(2.0) * -1
            }
            if (input.contains("LEFT") && input.contains("DOWN")) {
                map.controlledCreature.xVelocity = map.controlledCreature.speed / sqrt(2.0) * -1
                map.controlledCreature.yVelocity = map.controlledCreature.speed / sqrt(2.0)
            }
            if (input.contains("DOWN") && input.contains("RIGHT")) {
                map.controlledCreature.xVelocity = map.controlledCreature.speed / sqrt(2.0)
                map.controlledCreature.yVelocity = map.controlledCreature.speed / sqrt(2.0)
            }
            if (input.contains("RIGHT") && input.contains("UP")) {
                map.controlledCreature.xVelocity = map.controlledCreature.speed / sqrt(2.0)
                map.controlledCreature.yVelocity = map.controlledCreature.speed / sqrt(2.0) * -1
            }
            if (input.contains("SPACE")) {
                map.controlledCreature.searchHome()
            }


        }


        var maxEnergyDouble = 0.0
        if (map.gameMode == 1) map.controlledCreature.render(gc)
        val iterator1 = renderables.iterator()
        while (iterator1.hasNext()) {
            val renderable = iterator1.next()
            if (map.gameMode == 1) {
                val distance = sqrt(
                    (map.controlledCreature.xCoor - renderable.xCoor).pow(2) + (map.controlledCreature.yCoor - renderable.yCoor).pow(
                        2
                    )
                )
                if (distance <= map.controlledCreature.sensitivity * map.controlledCreature.radius / 2) {
                    if (renderable != map.controlledCreature)
                        renderable.render(gc)
                }
            } else {
                renderable.render(gc)
            }
            if (renderable is Steppable) {
                renderable.step(elapsedTime)
                if (renderable is Species) {
                    val energy = renderable.currentEnergy
                    if (energy > maxEnergyDouble && !renderable.stopped)
                        maxEnergyDouble = energy
                    //if (map.foods.size == 0) renderable.searchHome()
                    val iterator2 = renderables.iterator()
                    while (iterator2.hasNext()) {
                        val possibleCollide = iterator2.next()
                        if (possibleCollide == renderable)
                            continue
                        else {
                            renderable.checkCollision(possibleCollide, this)
                        }
                    }
                }
            }
        }
        maxEnergy.text = maxEnergyDouble.toString()
        if (map.gameMode == 1) playerEnergy.text = map.controlledCreature.currentEnergy.toString()
    }

    private fun checkIfAllStopped(renderables: MutableList<Renderable>): Boolean {

        var allStopped = true
        val iterator = renderables.iterator()
        while (iterator.hasNext()) {
            val temp = iterator.next()
            if (temp is Species)
                if (!temp.stopped) allStopped = false
        }

        return allStopped

    }

    private fun afterRound() {


        val postiterator = renderables.iterator()
        while (postiterator.hasNext()) {
            val temp = postiterator.next()
            if (temp is Species)
                temp.postDay(this)
        }

        val addingiterator = toAdd.iterator()
        while (addingiterator.hasNext()) {
            renderables.add(addingiterator.next())
        }
        toAdd.removeAll(toAdd)
        TimeUnit.SECONDS.sleep(1)
    }


    private fun restartMap() {


        for (i in 1..map.dailyFoodNumber) {
            renderables.add(
                Food(
                    Random.nextDouble(100.0, 1000.0),
                    Random.nextDouble(100.0, 1000.0),
                    8.0,
                    map
                )
            )
        }

        map.updateChart()


        added = false
        afterRound = true

        //Thread.sleep(3000)
        TimeUnit.SECONDS.sleep(3)

    }
}