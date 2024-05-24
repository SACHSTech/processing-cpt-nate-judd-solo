import processing.core.PApplet;
import processing.core.PImage;

public class Sketch extends PApplet {
  // variables and images used for the background
  PImage imgMainBG;
  int intScreenW = 800;
  int intScreenH = 600;
  int intSpeedBG = 2;
	float fltXPosBG = 0;
	
  /**
   * Called once at the beginning of execution, put your size all in this method
   */
  public void settings() {
    // Background Size
    size(intScreenW, intScreenH);
  }

  /** 
   * Called once at the beginning of execution.  Add initial set up
   * values here i.e background, stroke, fill etc.
   */
  public void setup() {
    // Loads the main background image
    imgMainBG = loadImage("MainBG.jpeg");
    imgMainBG.resize(intScreenW, intScreenH);
  }

  /**
   * Called repeatedly, anything drawn to the screen goes here
   */
  public void draw() {
    // Clears the background
    background(255);

    // Print the background image
    image(imgMainBG, fltXPosBG, 0);
    image(imgMainBG, fltXPosBG + intScreenW, 0);

    // Updates backgorund to achieve scrolling effect
    fltXPosBG -= intSpeedBG;
    if (fltXPosBG <= -width) {
      fltXPosBG = 0;
    }
  }
  
  // define other methods down here.
}