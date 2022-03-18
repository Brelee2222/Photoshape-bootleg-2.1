import java.awt.*;

public class TranslationMultiplier {
    Dimension dimension;
    double multiplierX = 1;
    double multiplierY = 1;
    public TranslationMultiplier(Dimension dimension) {
        this.dimension = (Dimension) dimension.clone();
    }

    public void setMultiplier(Dimension newDimension) {
        multiplierY = newDimension.getHeight() / dimension.getHeight();
        multiplierX = newDimension.getWidth() / dimension.getWidth();
    }

}
