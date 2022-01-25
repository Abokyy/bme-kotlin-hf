import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color

class Food(xCoor : Double, yCoor : Double, radius : Double = 8.0, map: Map) : Renderable(xCoor, yCoor, radius, map) {
    override fun render(gc: GraphicsContext) {
        gc.fill = Color.ORANGE
        gc.fillOval(xCoor - radius/2, yCoor - radius/2, radius, radius)
    }
}