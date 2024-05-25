import processing.core.PApplet;
import processing.core.PImage;

/**
 * discription
 * 
 * @author NJudd
 */
public class Sketch extends PApplet {
  PImage imgMainBG, imgMC;
  int intScreenW = 800, intScreenH = 700;
  float fltXPosBG = 0;
  int intWidthMC = 60, intHeightMC = 70;
  float fltXPos = 100, fltYPos = 400, fltPreJumpPos = fltYPos;
  float fltXSpeed = 0, fltYSpeed = 0, fltMaxSpeed = 5, fltJumpSpeed = -8, fltSprintSpeed;
  float fltAccel = 0.2f, fltDecel = 0.1f, fltGravity = 0.3f;
  boolean blnJump = false, blnLeft = false, blnRight = false, blnSprint = false;

  /**
   * Initializes the size of the canvas.
   */
  public void settings() {
    size(intScreenW, intScreenH);
  }

  /**
   * Sets up the initial environment by loading and resizing images.
   */
  public void setup() {
    imgMainBG = loadImage("MainBG.jpeg");
    imgMainBG.resize(intScreenW, intScreenH);
    imgMC = loadImage("MainCharacter.png");
    imgMC.resize(intWidthMC, intHeightMC);
  }

  /**
   * Draws the frame and updates the positions of the background and main
   * character.
   */
  public void draw() {
    background(255);
    image(imgMainBG, fltXPosBG, 0);
    image(imgMainBG, fltXPosBG + intScreenW, 0);

    // Calculate maximum speed in the sprinting state
    if (blnSprint) {
      fltSprintSpeed = fltMaxSpeed;
    } else {
      // Speed is reduced to 60% of the maximum sprint speed
      fltSprintSpeed = fltMaxSpeed * 0.6f;
    }

    // Horizontal movement logic
    if (blnRight) {
      // Accelerate right
      fltXSpeed += fltAccel;
      if (fltXSpeed > fltSprintSpeed) {
        fltXSpeed = fltSprintSpeed;
      }
      // Move character and background
      if (fltXPos > width / 2 - intWidthMC) {
        fltXPosBG -= fltXSpeed;
      } else {
        fltXPos += fltXSpeed;
      }
    } else if (blnLeft) {
      // Accelerate left
      fltXSpeed -= fltAccel;
      if (fltXSpeed < -fltSprintSpeed) {
        fltXSpeed = -fltSprintSpeed;
      }
      // Move character
      fltXPos += fltXSpeed;
    } else {
      // Decelerate if no movement
      if (fltXSpeed > 0) {
        fltXSpeed -= fltDecel;
        if (fltXSpeed < 0)
          fltXSpeed = 0;
        fltXPos += fltXSpeed;
      } else if (fltXSpeed < 0) {
        fltXSpeed += fltDecel;
        if (fltXSpeed > 0)
          fltXSpeed = 0;
        fltXPos += fltXSpeed;
      }
    }

    // Reset background position if it goes off screen
    if (fltXPosBG <= -width) {
      fltXPosBG = 0;
    }

    // Clamp character position to stay on screen
    if (fltXPos > width / 2 - intWidthMC / 2) {
      fltXPos = width / 2 - intWidthMC / 2;
    }

    // Vertical movement logic (jumping and gravity)
    if (blnJump) {
      if (fltYPos >= fltPreJumpPos) {
        // Start jump
        fltYSpeed = fltJumpSpeed;
        blnJump = false;
      }
    }

    // Apply gravity
    fltYSpeed += fltGravity;
    fltYPos += fltYSpeed;

    // Reset vertical position if on ground
    if (fltYPos >= fltPreJumpPos) {
      fltYPos = fltPreJumpPos;
      fltYSpeed = 0;
    }

    // Draw character
    image(imgMC, fltXPos, fltYPos);
  }

  /**
   * Handles key press events to control the main character's movements.
   */
  public void keyPressed() {
    if (keyCode == UP && fltYPos == fltPreJumpPos)
      blnJump = true;
    if (keyCode == LEFT)
      blnLeft = true;
    if (keyCode == RIGHT)
      blnRight = true;
    if (key == 'z')
      blnSprint = true;
  }

  /**
   * Handles key release events to control the main character's movements.
   */
  public void keyReleased() {
    if (keyCode == LEFT)
      blnLeft = false;
    if (keyCode == RIGHT)
      blnRight = false;
    if (key == 'z')
      blnSprint = false;
  }
}
