import processing.core.PApplet;
import processing.core.PImage;

public class Sketch extends PApplet {
  // Variables and images used for the background
  PImage imgMainBG;
  int intScreenW = 800;
  int intScreenH = 700;
  int intSpeedBG = 3;
  float fltXPosBG = 0;

  // Variables and images used for the main character
  PImage imgMC;
  int intWidthMC = 60;
  int intHeightMC = 70;
  int intSpeed;
  int intWalk = 3;
  int intSprint = 5;
  int intJumpSpeed = 5;
  int intJumpDuration = 30;
  float fltXPos = 100;
  float fltYPos = 400;
  float fltJumpHeight = 125;
  float fltFallSpeed = 5;
  float fltPreJumpYPos = fltYPos;
  boolean blnJump = false;
  boolean blnLeft = false;
  boolean blnRight = false;
  boolean blnPeakReached = false;
  boolean blnSprint = false;

  /**
   * Initializes background size
   */
  public void settings() {
    // Background size
    size(intScreenW, intScreenH);
  }

  /**
   * Sets up the initial environment
   */
  public void setup() {
    // Loads the main background image
    imgMainBG = loadImage("MainBG.jpeg");
    imgMainBG.resize(intScreenW, intScreenH);

    // Loads the main character image
    imgMC = loadImage("MainCharacter.png");
    imgMC.resize(intWidthMC, intHeightMC);

  }

  /**
   * Top level method to execute the program
   */
  public void draw() {
    // Clears the background
    background(255);

    // Print the background image
    image(imgMainBG, fltXPosBG, 0);
    image(imgMainBG, fltXPosBG + intScreenW, 0);

    // Changes background speed when sprinting
    if (blnSprint) {
      intSpeedBG = intSprint;
    } else {
      intSpeedBG = intWalk;
    }

    // Updates backgorund to achieve scrolling effect
    if (blnRight && fltXPos > width / 2 - 75) { // substracting 75 gives the player more vision in front of them
      fltXPosBG -= intSpeedBG;
    } else if (blnRight) {
      fltXPos += intSpeed;
    }
    if (fltXPosBG <= -width) {
      fltXPosBG = 0;
    }

    // Main character's horizontal movement
    if (blnLeft) {
      fltXPos -= intSpeed;
    }

    // Main character's sprinting
    if (blnSprint) {
      intSpeed = intSprint;
    } else {
      intSpeed = intWalk;
    }

    // Checks if the main character has jumped
    if (blnJump) {
      // Alters main characters y position until they reach the peak of their jump
      if (fltYPos > fltPreJumpYPos - fltJumpHeight && blnPeakReached == false) {
        fltYPos -= intJumpSpeed;
      } else if (fltYPos < fltPreJumpYPos) { // Going down after jumping
        fltFallSpeed += 0.1;
        fltYPos += fltFallSpeed;
        blnPeakReached = true;
      } else if (fltYPos >= fltPreJumpYPos) { // Resets so  can jump again
        fltYPos = fltPreJumpYPos;
        blnPeakReached = false;
        blnJump = false;
        fltFallSpeed = 4;
      }
    }

    // Prints the main character to the screen
    image(imgMC, fltXPos, fltYPos);
  }

  /**
   * Checks if keys have been pressed
   */
  public void keyPressed() {
    // main character movement
    if (keyCode == UP) {
      blnJump = true;
    } else if (keyCode == LEFT) {
      blnLeft = true;
    } else if (keyCode == RIGHT) {
      blnRight = true;
    } else if (key == 'z') {
      blnSprint = true;
    }
  }

  /**
   * Checks if keys have been pressed
   */
  public void keyReleased() {
    // Main character movement
    if (keyCode == LEFT) {
      blnLeft = false;
    } else if (keyCode == RIGHT) {
      blnRight = false;
    } else if (key == 'z') {
      blnSprint = false;
    }
  }
}