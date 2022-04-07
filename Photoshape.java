//I have a tendency to capitalize things

/**
This program has a linked node typed undo and revert function so that it's limited to the user's memory allocation
 The bucket function uses a pathfinder algorithm mainly because I wanted to make one in node.js, but procrastinated too much
 The Pathfinding function has been optimized to be partially made for an acutal paint bucket function, but I'm not sure if it runs as fast as a normal paint bucket function.
 It also uses a lambda for a debug mode
    -Classes : all of ToolFunction-PaintBucket & Pathfinder

 It uses binary XOR to invert an image's color
 It uses a close to accurate binary grayscaler so that it is faster
    -Classes : ColorFilteringFunctions-ColorInvert & ColorFilteringFunctions-Grayify
    -Interfaces : ColorFiltering-ColorChanger

 The eraser clears the pixels rather than cover them up
    -Classes : ToolFunctions-Erase

 The Line tool has no blink
    -Classes : ToolFunctions-LineTool

 There's a tool button to change how the tool works
    -Classes : ToolFunctionOptions

 The tool uses a JSON/Mapping like interface that sets the tool aspects, but uses a single list to make it faster because Mappings or JSONs would make it slower
    -Classes : ToolFunctionOptions-OptionContent

 has a blurr function (might be a blurr intensity)

 Image loader

 Aspect Ratio and translation multiplier

 Tools toolbar
    -Classes : SectionToolSelection

 Options Window
    -Classes : OptionsWindow

 I forgot that I could've placed ToolFunctions in Photoshape Canvas, but that's too late now.

 */

public class Photoshape {

    public static void main(String[] args) {
        new PhotoshapeWindow("Andromada ImageShaper");
    }
}
