import javafx.scene.canvas.GraphicsContext
import javafx.scene.layout.Pane

abstract class Renderable(var xCoor: Double, var yCoor: Double, val radius: Double, val map: Map?) {
    abstract fun render(gc: GraphicsContext)

    open fun remove(timer: Timer) {
            timer.toRemove.add(this)
    }

}