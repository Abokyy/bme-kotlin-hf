import javafx.collections.FXCollections
import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.Spinner
import javafx.scene.control.SpinnerValueFactory
import javafx.scene.image.Image
import javafx.scene.layout.FlowPane
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.stage.Stage
import java.awt.Toolkit
import java.io.FileInputStream
import java.util.concurrent.Flow

class WelcomeStage : Stage() {


    val vBox = VBox()

    private val titles = FlowPane()
    private val species = Label("Species")
    private val population = Label("Population")
    private val size = Label("Size (radius)")
    private val speed = Label("Speed")
    private val energy = Label("Energy")
    private val sensitivity = Label("Sensitivity")
    private val mutateRate = Label("Mutation rate")

    private val foodNumbersFLowPane = FlowPane()
    private val initFoodNumberLabel = Label("Starting food number:")
    private val dailyFoodNumberLabel = Label("Daily additional food number:")
    private val initFoodSpinner = Spinner<Int>()
    private val dailyFoodSpinner = Spinner<Int>()
    private val initFoodFactory = SpinnerValueFactory.IntegerSpinnerValueFactory(5, 150, 20, 5)
    private val dailyFoodFactory = SpinnerValueFactory.IntegerSpinnerValueFactory(1, 150, 20, 1)

    private val greedypopulationFactory = SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10, 2)
    private val greedysizeFactory = SpinnerValueFactory.DoubleSpinnerValueFactory(15.0, 150.0, 15.0, 1.0)
    private val greedyspeedFactory = SpinnerValueFactory.DoubleSpinnerValueFactory(1.0, 50.0, 12.0, 0.5)
    private val greedyenergyFactory = SpinnerValueFactory.DoubleSpinnerValueFactory(20.0, 120.0, 30.0, 0.5)
    private val greedysensitivityFactory = SpinnerValueFactory.DoubleSpinnerValueFactory(1.0, 50.0, 10.0, 0.2)
    private val greedyMutationRateFactory = SpinnerValueFactory.DoubleSpinnerValueFactory(1.0, 3.0, 1.5, 0.1)

    private val cooperativepopulationFactory = SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10, 2)
    private val cooperativesizeFactory = SpinnerValueFactory.DoubleSpinnerValueFactory(15.0, 150.0, 15.0, 1.0)
    private val cooperativespeedFactory = SpinnerValueFactory.DoubleSpinnerValueFactory(1.0, 50.0, 10.0, 0.5)
    private val cooperativeenergyFactory = SpinnerValueFactory.DoubleSpinnerValueFactory(20.0, 120.0, 30.0, 0.5)
    private val cooperativesensitivityFactory = SpinnerValueFactory.DoubleSpinnerValueFactory(1.0, 50.0, 12.0, 0.2)
    private val cooperativeMutationRateFactory = SpinnerValueFactory.DoubleSpinnerValueFactory(1.0, 3.0, 1.5, 0.1)

    private val bigChunguspopulationFactory = SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10, 2)
    private val bigChungussizeFactory = SpinnerValueFactory.DoubleSpinnerValueFactory(15.0, 150.0, 25.0, 1.0)
    private val bigChungusspeedFactory = SpinnerValueFactory.DoubleSpinnerValueFactory(1.0, 50.0, 10.0, 0.5)
    private val bigChungusenergyFactory = SpinnerValueFactory.DoubleSpinnerValueFactory(20.0, 120.0, 30.0, 0.5)
    private val bigChungussensitivityFactory = SpinnerValueFactory.DoubleSpinnerValueFactory(1.0, 50.0, 8.0, 0.2)
    private val bigChungusMutationRateFactory = SpinnerValueFactory.DoubleSpinnerValueFactory(1.0, 3.0, 1.5, 0.1)

    private val playerpopulationFactory = SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 1)
    private val playersizeFactory = SpinnerValueFactory.DoubleSpinnerValueFactory(15.0, 150.0, 15.0, 1.0)
    private val playerspeedFactory = SpinnerValueFactory.DoubleSpinnerValueFactory(1.0, 50.0, 10.0, 0.5)
    private val playerenergyFactory = SpinnerValueFactory.DoubleSpinnerValueFactory(20.0, 120.0, 30.0, 0.5)
    private val playersensitivityFactory = SpinnerValueFactory.DoubleSpinnerValueFactory(1.0, 50.0, 20.0, 0.2)
    private val playerSpeciesMutationRateFactory = SpinnerValueFactory.DoubleSpinnerValueFactory(1.0, 3.0, 1.5, 0.1)


    private val modeHBox = FlowPane()
    private val modeLabel = Label("Mode")
    private val modes =
        FXCollections.observableArrayList<String>("Simulation", "Game mode")
    private val modeSpinner = Spinner<String>()
    private val modeValueFactory = SpinnerValueFactory.ListSpinnerValueFactory<String>(modes)

    private val greedyHBox = FlowPane()
    private val greedyLabel = Label("Greedy starting population")
    private val greedyPopulation = Spinner<Int>()
    private val greedySize = Spinner<Double>()
    private val greedySpeed = Spinner<Double>()
    private val greedyEnergy = Spinner<Double>()
    private val greedySensitivity = Spinner<Double>()
    private val greedyMutateSpinner = Spinner<Double>()

    private val cooperativeHBox = FlowPane()
    private val cooperativeLabel = Label("Cooperative starting population")
    private val cooperativePopulation = Spinner<Int>()
    private val cooperativeSize = Spinner<Double>()
    private val cooperativeSpeed = Spinner<Double>()
    private val cooperativeEnergy = Spinner<Double>()
    private val cooperativeSensitivity = Spinner<Double>()
    private val cooperativeMutateSpinner = Spinner<Double>()

    private val bigChungusHBox = FlowPane()
    private val bigChungusLabel = Label("BigChungus starting population")
    private val bigChungusPopulation = Spinner<Int>()
    private val bigChungusSize = Spinner<Double>()
    private val bigChungusSpeed = Spinner<Double>()
    private val bigChungusEnergy = Spinner<Double>()
    private val bigChungusSensitivity = Spinner<Double>()
    private val bigChungusMutateSpinner = Spinner<Double>()

    private val playerHBox = FlowPane()
    private val playerLabel = Label("Your species's starting population")
    private val playerPopulation = Spinner<Int>()
    private val playerSize = Spinner<Double>()
    private val playerSpeed = Spinner<Double>()
    private val playerEnergy = Spinner<Double>()
    private val playerSensitivity = Spinner<Double>()
    private val playerSpeciesMutateSpinner = Spinner<Double>()
    private val playerTactics = Label("Your species's tactic")
    private val tactics = FXCollections.observableArrayList<String>("Cooperative", "Greedy", "BigChungus")
    private val tacticValueFactory = SpinnerValueFactory.ListSpinnerValueFactory<String>(tactics)
    private val tacticsSpinner = Spinner<String>()

    //private val playerTactics = Label("Your species tactics")

    private val startButton = Button("Start")

    init {

        species.prefWidth = 200.0
        population.prefWidth = 150.0
        size.prefWidth = 150.0
        speed.prefWidth = 150.0
        energy.prefWidth = 150.0
        sensitivity.prefWidth = 150.0
        mutateRate.prefWidth = 150.0

        greedyLabel.prefWidth = 200.0
        cooperativeLabel.prefWidth = 200.0
        bigChungusLabel.prefWidth = 200.0
        playerLabel.prefWidth = 200.0

        foodNumbersFLowPane.hgap = 30.0
        initFoodSpinner.valueFactory = initFoodFactory
        dailyFoodSpinner.valueFactory = dailyFoodFactory
        foodNumbersFLowPane.children.addAll(
            initFoodNumberLabel,
            initFoodSpinner,
            dailyFoodNumberLabel,
            dailyFoodSpinner
        )

        modeSpinner.valueFactory = modeValueFactory

        greedyPopulation.valueFactory = greedypopulationFactory
        greedySize.valueFactory = greedysizeFactory
        greedySpeed.valueFactory = greedyspeedFactory
        greedyEnergy.valueFactory = greedyenergyFactory
        greedySensitivity.valueFactory = greedysensitivityFactory
        greedyMutateSpinner.valueFactory = greedyMutationRateFactory

        cooperativePopulation.valueFactory = cooperativepopulationFactory
        cooperativeSize.valueFactory = cooperativesizeFactory
        cooperativeSpeed.valueFactory = cooperativespeedFactory
        cooperativeEnergy.valueFactory = cooperativeenergyFactory
        cooperativeSensitivity.valueFactory = cooperativesensitivityFactory
        cooperativeMutateSpinner.valueFactory = cooperativeMutationRateFactory

        bigChungusPopulation.valueFactory = bigChunguspopulationFactory
        bigChungusSize.valueFactory = bigChungussizeFactory
        bigChungusSpeed.valueFactory = bigChungusspeedFactory
        bigChungusEnergy.valueFactory = bigChungusenergyFactory
        bigChungusSensitivity.valueFactory = bigChungussensitivityFactory
        bigChungusMutateSpinner.valueFactory = bigChungusMutationRateFactory

        playerPopulation.valueFactory = playerpopulationFactory
        playerSize.valueFactory = playersizeFactory
        playerSpeed.valueFactory = playerspeedFactory
        playerEnergy.valueFactory = playerenergyFactory
        playerSensitivity.valueFactory = playersensitivityFactory
        playerSpeciesMutateSpinner.valueFactory = playerSpeciesMutationRateFactory
        tacticsSpinner.valueFactory = tacticValueFactory



        modeHBox.children.addAll(modeLabel, modeSpinner)
        titles.children.addAll(species, population, size, speed, energy, sensitivity, mutateRate)
        greedyHBox.children.addAll(
            greedyLabel,
            greedyPopulation,
            greedySize,
            greedySpeed,
            greedyEnergy,
            greedySensitivity,
            greedyMutateSpinner
        )
        cooperativeHBox.children.addAll(
            cooperativeLabel,
            cooperativePopulation,
            cooperativeSize,
            cooperativeSpeed,
            cooperativeEnergy,
            cooperativeSensitivity,
            cooperativeMutateSpinner
        )
        bigChungusHBox.children.addAll(
            bigChungusLabel,
            bigChungusPopulation,
            bigChungusSize,
            bigChungusSpeed,
            bigChungusEnergy,
            bigChungusSensitivity,
            bigChungusMutateSpinner
        )
        playerHBox.children.addAll(
            playerLabel,
            playerPopulation,
            playerSize,
            playerSpeed,
            playerEnergy,
            playerSensitivity,
            playerSpeciesMutateSpinner,
            playerTactics,
            tacticsSpinner
        )


        vBox.children.addAll(
            modeHBox,
            titles,
            greedyHBox,
            cooperativeHBox,
            bigChungusHBox,
            foodNumbersFLowPane,
            startButton
        )

        var gameMode = 0

        modeValueFactory.valueProperty().addListener { _ ->
            gameMode = when (modeValueFactory.value) {
                "Simulation" -> {
                    vBox.children.remove(playerHBox)
                    0
                }
                "Game mode" -> {
                    vBox.children.add(5, playerHBox)
                    1
                }

                else -> 0
            }
        }

        var playerTactic = 1

        tacticValueFactory.valueProperty().addListener { _ ->
            playerTactic = when (tacticValueFactory.value) {
                "Cooperative" -> 1
                "Greedy" -> 2
                "BigChungus" -> 3
                else -> 1
            }
        }


        startButton.onAction = EventHandler {
            val initValues = mutableMapOf<Species, Int>()

            val greedy = Greedy(
                0.0,
                0.0,
                greedySize.value,
                greedyEnergy.value,
                greedySpeed.value,
                greedySensitivity.value,
                null,
                Color.RED,
                greedyMutateSpinner.value
            )
            val cooperative = Cooperative(
                0.0,
                0.0,
                cooperativeSize.value,
                cooperativeEnergy.value,
                cooperativeSpeed.value,
                cooperativeSensitivity.value,
                null,
                Color.ORANGE,
                cooperativeMutateSpinner.value
            )
            val bigChungus = BigChungus(
                0.0,
                0.0,
                bigChungusSize.value,
                bigChungusEnergy.value,
                bigChungusSpeed.value,
                bigChungusSensitivity.value,
                null,
                Color.GREEN,
                bigChungusMutateSpinner.value
            )
            val playerSpecies = PlayerSpecies(
                0.0,
                0.0,
                playerSize.value,
                playerEnergy.value,
                playerSpeed.value,
                playerSensitivity.value,
                null,
                Color.BLUE,
                playerSpeciesMutateSpinner.value,
                playerTactic
            )

            initValues[greedy] = greedyPopulation.value
            initValues[cooperative] = cooperativePopulation.value
            initValues[bigChungus] = bigChungusPopulation.value
            if (gameMode == 1)
                initValues[playerSpecies] = playerPopulation.value


            GameStage(initValues, gameMode, initFoodSpinner.value, dailyFoodSpinner.value)
        }

        val img = Image(FileInputStream("src\\main\\resources\\evolution.png"))
        this.icons.add(img)

        this.scene = Scene(vBox, 1100.0, 250.0)
        this.title = "Simulation settings"
        this.show()

    }


}