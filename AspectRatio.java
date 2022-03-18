import java.awt.*;

public class AspectRatio {
    public double multiplierWidth = 1;
    public double multiplierHeight = 1;

    public void setNewMultipliers(Dimension newDimension) {
        multiplierWidth = newDimension.getWidth() / newDimension.getHeight();
        multiplierHeight = newDimension.getHeight() / newDimension.getWidth();
    }
}
