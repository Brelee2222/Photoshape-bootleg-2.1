import java.awt.*;

public class HeaderBar extends MenuBar {
    /*
    Menu headerOptions;
    Menu colorFilters;
    */
    HeaderBar(PhotoshapeCanvas canvas) {
        /*
        headerOptions = add(new HeaderOptions("File", canvas));
        colorFilters = add(new ColorFilterOptions("Filter", canvas));
        */
        add(new HeaderFileOptions("File", canvas));
        add(new ColorFilterOptions("Filter", canvas));
    }
}
