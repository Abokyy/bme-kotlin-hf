import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.chart.BarChart
import javafx.scene.chart.CategoryAxis
import javafx.scene.chart.LineChart
import javafx.scene.chart.NumberAxis
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.stage.Stage
import java.io.FileInputStream

class GameStage(initValues: MutableMap<Species, Int>, gameMode : Int, initFood : Int, dailyFood: Int) : Stage() {

    init {


        val root: HBox = HBox()


        val canvas = Canvas(1024.0, 1024.0)

        val xAxis = NumberAxis()
        xAxis.label = "Rounds"
        val yAxis = NumberAxis()
        yAxis.label = "Quantity"
        val yAxisMut = NumberAxis()
        yAxisMut.label = "Quantity"
        yAxis.isAutoRanging = true
        yAxis.tickUnit = 1.0
        val chart = LineChart<Number, Number>(xAxis, yAxis)
        val xCategoryAxis = CategoryAxis()

        val mutationChart = BarChart<String, Number>(xCategoryAxis, yAxisMut)

        val map = Map(chart, mutationChart, initValues, gameMode, initFood, dailyFood)

        val charts = VBox()
        val energyHBox = HBox()
        val maxEnergyLabel = Label("Slowest creature's energy: ")
        val maxEnergy = Label("")
        val playerEnergyHBox = HBox()
        val playerEnergyLabel = Label("Your remaining energy: ")
        val playerEnergy = Label("")
        energyHBox.children.addAll(maxEnergyLabel, maxEnergy)
        playerEnergyHBox.children.addAll(playerEnergyLabel, playerEnergy)
        charts.isFillWidth = true
        mutationChart.prefHeight = 472.0
        mutationChart.prefWidth = 1024.0
        chart.prefHeight = 512.0
        chart.prefWidth = 1024.0
        charts.children.addAll(energyHBox, chart, mutationChart)
        if (gameMode == 1) charts.children.add(1, playerEnergyHBox)
        root.children.add(canvas)
        root.children.add(charts)

        val theScene = Scene(root)

        val input = mutableListOf<String>()

        theScene.onKeyPressed = EventHandler {
            val code = it.code.toString()
            if (!input.contains(code))
                input.add(code)
        }

        theScene.onKeyReleased = EventHandler {
            val code = it.code.toString()
            input.remove(code)
        }

        this.scene = theScene


        val gc = canvas.graphicsContext2D


        val nanoTime = System.nanoTime()
        val timer = Timer(map, nanoTime, gc, input, maxEnergy, playerEnergy)


        timer.start()



        val img = Image(FileInputStream("src\\main\\resources\\evolution.png"))
        this.icons.add(img)
        this.title = "Natural selection simulator"
        this.show()


    }

}