import javafx.scene.chart.BarChart
import javafx.scene.chart.LineChart
import javafx.scene.chart.XYChart.*
import javafx.scene.paint.Color
import kotlin.random.Random

class Map(
    val chart: LineChart<Number, Number>,
    val mutationChart: BarChart<String, Number>,
    val initValues: MutableMap<Species, Int>,
    val gameMode: Int = 0,
    val initFoodNumber : Int,
    val dailyFoodNumber : Int = 30
) {

    lateinit var controlledCreature: PlayerSpecies

    private val playerSpeciesSeries = Series<Number, Number>()
    private val dummySeries = Series<Number, Number>()
    private val foodSeries = Series<Number, Number>()
    private val greedySeries = Series<Number, Number>()
    private val coopSeries = Series<Number, Number>()
    private val bigChungusSeries = Series<Number, Number>()
    private val chungusSpeedSeries = Series<String, Number>()
    private val greedySpeedSeries = Series<String, Number>()
    private val coopSpeedSeries = Series<String, Number>()
    val renderables: MutableList<Renderable> = mutableListOf()
    val foods: MutableList<Food>
        get() {
            val listOfFoods = mutableListOf<Food>()
            val iterator = renderables.iterator()
            while (iterator.hasNext()) {
                val temp = iterator.next()
                if (temp is Food) listOfFoods.add(temp)
            }
            return listOfFoods
        }
    val creatures: MutableList<Species>
        get() {
            val listOfCreatures = mutableListOf<Species>()
            val iterator = renderables.iterator()
            while (iterator.hasNext()) {
                val temp = iterator.next()
                if (temp is Species) listOfCreatures.add(temp)
            }
            return listOfCreatures
        }
    var round = 0


    init {
        dummySeries.name = "Dummy population"
        foodSeries.name = "Food number"
        greedySeries.name = "Greedy population"
        coopSeries.name = "Cooperative population"
        greedySpeedSeries.name = "Greedy speeds"
        coopSpeedSeries.name = "Coop speeds"
        bigChungusSeries.name = "BigChungus population"
        chungusSpeedSeries.name = "BigChungus speeds"
        playerSpeciesSeries.name = "Player species population"
        mutationChart.data.addAll(greedySpeedSeries, coopSpeedSeries, chungusSpeedSeries)
        mutationChart.animated = false
        chart.animated = false
        chart.data.addAll(greedySeries, coopSeries, bigChungusSeries, playerSpeciesSeries)
        initMap()
    }


    private fun initMap() {
        val iterator = initValues.iterator()
        while (iterator.hasNext()) {
            val keyValue = iterator.next()
            val creature = keyValue.key
            val population = keyValue.value
            when (creature) {
                is PlayerSpecies-> {
                    controlledCreature = PlayerSpecies(
                        512.0,
                        512.0,
                        creature.radius,
                        creature.energy,
                        creature.speed,
                        creature.sensitivity,
                        this,
                        Color.BLUE,
                        creature.mutationRate,
                        creature.tactics
                    )
                    controlledCreature.playerControl = true

                    renderables.add(controlledCreature)

                    for(i in 2..population) {
                        renderables.add(
                            PlayerSpecies(
                                Random.nextDouble(100.0, 1000.0),
                                Random.nextDouble(100.0, 1000.0),
                                creature.radius,
                                creature.energy,
                                creature.speed,
                                creature.sensitivity,
                                this,
                                Color.BLUE,
                                creature.mutationRate,
                                creature.tactics
                            )
                        )
                    }
                }
                is Greedy -> {
                    for (i in 1..population) {
                        renderables.add(
                            Greedy(
                                Random.nextDouble(100.0, 1000.0),
                                Random.nextDouble(100.0, 1000.0),
                                creature.radius,
                                creature.energy,
                                creature.speed,
                                creature.sensitivity,
                                this,
                                Color.RED,
                                creature.mutationRate
                            )
                        )
                    }
                }
                is Cooperative-> {
                    for (i in 1..population) {
                        renderables.add(
                            Cooperative(
                                Random.nextDouble(100.0, 1000.0),
                                Random.nextDouble(100.0, 1000.0),
                                creature.radius,
                                creature.energy,
                                creature.speed,
                                creature.sensitivity,
                                this,
                                Color.ORANGE,
                                creature.mutationRate
                            )
                        )
                    }
                }
                is BigChungus ->  for (i in 1..population) {
                    renderables.add(
                        BigChungus(
                            Random.nextDouble(100.0, 1000.0),
                            Random.nextDouble(100.0, 1000.0),
                            creature.radius,
                            creature.energy,
                            creature.speed,
                            creature.sensitivity,
                            this,
                            Color.GREEN,
                            creature.mutationRate
                        )
                    )
                }
                else -> {

                }
            }
        }

        for (i in 1..initFoodNumber) {
            renderables.add(
                Food(
                    Random.nextDouble(100.0, 1000.0),
                    Random.nextDouble(100.0, 1000.0),
                    8.0,
                    this
                )
            )
        }

        updateChart()
    }

    fun updateChart() {

        var dummies = 0
        var greedies = 0
        var coopies = 0
        var chungus = 0
        var playerSpecies = 0
        val greedyMutationSpeeds = mutableMapOf<Double, Int>()
        val coopMutationSpeeds = mutableMapOf<Double, Int>()
        val chungusMutationSpeeds = mutableMapOf<Double, Int>()
        val speedKeys = mutableListOf<Double>()
        val iterator = creatures.iterator()
        while (iterator.hasNext()) {
            val spec = iterator.next()
            val speedKey = spec.speed
            spec.prepareForNextRound()
            when (spec) {
                is Dummy -> {
                    dummies++
                }
                is BigChungus -> {
                    if (chungusMutationSpeeds.containsKey(speedKey)) {
                        chungusMutationSpeeds[speedKey] = chungusMutationSpeeds.getValue(speedKey) + 1
                    } else {
                        chungusMutationSpeeds[speedKey] = 1
                        speedKeys.add(speedKey)
                    }
                    chungus++
                }
                is Cooperative -> {
                    if (coopMutationSpeeds.containsKey(speedKey)) {
                        coopMutationSpeeds[speedKey] = coopMutationSpeeds.getValue(speedKey) + 1
                    } else {
                        coopMutationSpeeds[speedKey] = 1
                        speedKeys.add(speedKey)
                    }
                    coopies++
                }
                is PlayerSpecies -> {
                    spec.prepareForNextRound()
                    playerSpecies++
                }
                is Greedy -> {
                    spec.prepareForNextRound()
                    if (greedyMutationSpeeds.containsKey(speedKey)) {
                        greedyMutationSpeeds[speedKey] = greedyMutationSpeeds.getValue(speedKey) + 1
                    } else {
                        greedyMutationSpeeds[speedKey] = 1
                        speedKeys.add(speedKey)
                    }
                    greedies++
                }

            }

        }

        val sortedKeys = speedKeys.distinct().sorted()
        coopSpeedSeries.data.clear()
        greedySpeedSeries.data.clear()
        chungusSpeedSeries.data.clear()

        for (speedKey in sortedKeys) {
            val speedKeyString = "Speed: %.1f".format(speedKey)

            if (greedyMutationSpeeds.containsKey(speedKey)) {
                val speedNumb = greedyMutationSpeeds[speedKey]
                greedySpeedSeries.data.add(Data(speedKeyString, speedNumb))
            } else {
                greedySpeedSeries.data.add(Data(speedKeyString, 0))
            }

            if (coopMutationSpeeds.containsKey(speedKey)) {
                val speedNumb = coopMutationSpeeds[speedKey]
                coopSpeedSeries.data.add(Data(speedKeyString, speedNumb))
            }

            if (chungusMutationSpeeds.containsKey(speedKey)) {
                val speedNumb = chungusMutationSpeeds[speedKey]
                chungusSpeedSeries.data.add(Data(speedKeyString, speedNumb))
            }


        }


        /*dummySeries.data.add(XYChart.Data(round, dummies))*/
        foodSeries.data.add(Data(round, foods.size))
        greedySeries.data.add(Data(round, greedies))
        coopSeries.data.add(Data(round, coopies))
        bigChungusSeries.data.add(Data(round, chungus))
        playerSpeciesSeries.data.add(Data(round, playerSpecies))

        round++
    }


}