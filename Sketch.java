import processing.core.PApplet;
import processing.core.PImage;

/**
 * discription
 * 
 * @author NJudd
 */
public class Sketch extends PApplet {
  // Images
  PImage imgMainBG, imgMC, imgCrouch, imgDash;
  // Background
  int intScreenW = 1000, intScreenH = 800;
  float fltXPosBG = 0;
  // Character
  int intWidthMC = 60, intHeightMC = 70, intCrouchHeightMC = 45;
  // Positions
  float fltXPos = 100, fltYPos = 400, fltPreJumpPos = fltYPos, fltDashDist = 0, fltPreDashPos = 0;
  // Speeds
  float fltXSpeed = 0, fltYSpeed = 0, fltMaxSpeed = 5, fltJumpHeight = -8, fltDashLength = 150, fltSprintSpeed = 0;
  float fltAccel = 0.3f, fltDecel = 0.2f, fltGravity = 0.3f;
  // Movement
  boolean blnJump = false, blnLeft = false, blnRight = false, blnSprint = false, blnCrouch = false, blnDash = false;
  // Dashing
  int intDashCooldown = 5000, intLastDashTime = 0, intDashCount = 0;
  String strDashDisplay = "";
  boolean blnCanDash = true;

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
    imgCrouch = loadImage("MainCharacter.png");
    imgCrouch.resize(intWidthMC, intCrouchHeightMC);
    imgDash = loadImage("DashMC.png");
    imgDash.resize(intWidthMC, intHeightMC);
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
      if (fltXPos >= width / 2 - intWidthMC) {
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
    if (fltXPos >= width / 2 - intWidthMC) {
      fltXPos = width / 2 - intWidthMC;
    }

    // Vertical movement logic (jumping and gravity)
    if (blnJump) {
      if (fltYPos >= fltPreJumpPos) {
        // Start jump
        fltYSpeed = fltJumpHeight;
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

    // Checks if character can dash
    if (!blnCanDash && millis() - intLastDashTime >= intDashCooldown) {
      blnCanDash = true;
    }

    // Calculates dash timer
    if (blnCanDash) {
      intDashCount = 0;
      strDashDisplay = "DASH";
    } else if (millis() - intLastDashTime < 1000) {
      intDashCount = 5;
      strDashDisplay = "" + intDashCount;
    } else if (millis() - intLastDashTime < 2000) {
      intDashCount = 4;
      strDashDisplay = "" + intDashCount;
    } else if (millis() - intLastDashTime < 3000) {
      intDashCount = 3;
      strDashDisplay = "" + intDashCount;
    } else if (millis() - intLastDashTime < 4000) {
      intDashCount = 2;
      strDashDisplay = "" + intDashCount;
    } else if (millis() - intLastDashTime < 5000) {
      intDashCount = 1;
      strDashDisplay = "" + intDashCount;
    }

    // Draws the character then alters its movement based on its state
    if (blnCrouch) {
      fltJumpHeight = -6;
      fltMaxSpeed = 3;
      // Draws dash timer
      textSize(20);
      textAlign(CENTER, CENTER);
      text(strDashDisplay, fltXPos + intWidthMC / 2, fltYPos + 10);
      // Draws character
      image(imgCrouch, fltXPos, fltYPos + (intHeightMC - intCrouchHeightMC));
    } else if (blnDash && fltXPos < fltDashDist) {
      fltXPos += 10;
      image(imgDash, fltXPos, fltYPos);
    } else {
      blnDash = false;
      fltDashDist = 0;
      fltPreDashPos = 0;
      fltJumpHeight = -8;
      fltMaxSpeed = 5;
      // Draws dash timer
      textSize(20);
      textAlign(CENTER, CENTER);
      text(strDashDisplay, fltXPos + intWidthMC / 2, fltYPos - 15);
      // Draws character
      image(imgMC, fltXPos, fltYPos);
    }
  }

  /**
   * Handles key press events to control the main character's movements.
   */
  public void keyPressed() {
    if (keyCode == UP && fltYPos == fltPreJumpPos) {
      blnJump = true;
    }
    if (keyCode == LEFT) {
      blnLeft = true;
    }
    if (keyCode == RIGHT) {
      blnRight = true;
    }
    if (key == 'z') {
      blnSprint = true;
    }
    if (key == 'x') {
      blnCrouch = true;
    }
    if (key == ' ' && blnCanDash) {
      blnDash = true;
      fltPreDashPos = fltXPos;
      // Stops character from dashing past the middle of the screen
      if (fltXPos >= width / 2 - intWidthMC - fltDashLength) {
        fltDashDist = fltXPos + (width / 2 - fltXPos - intWidthMC); // Adds the distance from the middle
      } else {
        fltDashDist = fltXPos + fltDashLength;
      }
      // Updates cooldown variables
      intLastDashTime = millis();
      blnCanDash = false;
    }
  }

  /**
   * Handles key release events to control the main character's movements.
   */
  public void keyReleased() {
    if (keyCode == LEFT) {
      blnLeft = false;
    }
    if (keyCode == RIGHT) {
      blnRight = false;
    }
    if (key == 'z') {
      blnSprint = false;
    }
    if (key == 'x') {
      blnCrouch = false;
    }
  }
}
