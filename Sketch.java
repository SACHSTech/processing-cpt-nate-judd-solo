import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PImage;

/**
 * The main code of the Rooftop Runner game.
 * 
 * @author NJudd
 */
public class Sketch extends PApplet {
  // Character and background images
  PImage imgMainBG, imgMC, imgCrouch, imgCrouchR, imgCrouchL, imgDash, imgDashR, imgDashL, imgRight, imgLeft;
  // Background
  int intScreenW = 1000, intScreenH = 800;
  float fltXPosBG = 0;
  // Character
  int intWidthMC = 60, intHeightMC = 70, intCrouchHeightMC = 45;
  // Positions
  float fltXPos = 55, fltYPos = 600, fltPreJumpPos = 0, fltDashDist = 0, fltPreDashPos = 0;
  // Speeds
  float fltXSpeed = 0, fltYSpeed = 0, fltMaxSpeed = 5, fltJumpHeight = -8, fltDashLength = 150, fltSprintSpeed = 0;
  float fltAccel = 0.3f, fltDecel = 0.2f, fltGravity = 0.3f;
  // Movement
  boolean blnJump = false, blnLeft = false, blnRight = false, blnSprint = false, blnCrouch = false, blnDash = false;
  // Dashing
  int intDashCooldown = 5000, intLastDashTime = 0, intDashCount = 0;
  String strDashDisplay = "";
  boolean blnCanDash = true;
  // Platform
  PImage imgBlock;
  ArrayList<Platform> platforms = new ArrayList<Platform>();
  int intPlatformCount = 6;
  int intBlockSize = 30;
  // Starting platform
  int intStartPlatLength = 5;
  float fltStartPlatX = 10;
  // Initializes variables for lives
  PImage imgLives;
  int intLifeSize = 30;
  int intLifeCount = 5;

  /**
   * Initializes the size of the canvas.
   */
  public void settings() {
    size(intScreenW, intScreenH);
  }

  /**
   * Sets up the initial environment.
   */
  public void setup() {
    // Background
    imgMainBG = loadImage("MainBG.jpeg");
    imgMainBG.resize(intScreenW, intScreenH);

    // Character
    imgRight = loadImage("MainCharacter.png");
    imgRight.resize(intWidthMC, intHeightMC);
    imgLeft = loadImage("LeftMC.png");
    imgLeft.resize(intWidthMC, intHeightMC);
    imgCrouchR = loadImage("MainCharacter.png");
    imgCrouchR.resize(intWidthMC, intCrouchHeightMC);
    imgCrouchL = loadImage("LeftMC.png");
    imgCrouchL.resize(intWidthMC, intCrouchHeightMC);
    imgDashR = loadImage("DashMC.png");
    imgDashR.resize(intWidthMC, intHeightMC);
    imgDashL = loadImage("DashLeft.png");
    imgDashL.resize(intWidthMC, intHeightMC);
    imgCrouch = imgCrouchR;
    imgDash = imgDashR;
    imgMC = imgRight;

    // Platform
    imgBlock = loadImage("platformBlock.png");
    imgBlock.resize(intBlockSize, intBlockSize);

    // Creates platforms
    for (int i = 0; i < intPlatformCount; i++) {
      platforms.add(new Platform(this, intBlockSize, platforms));
    }

    // Lives image
    imgLives = loadImage("heart.png");
    imgLives.resize(intLifeSize, intLifeSize);
  }

  /**
   * Draws the frame and updates the positions of the background and main
   * character.
   */
  public void draw() {
    background(255);
    image(imgMainBG, fltXPosBG, 0);
    image(imgMainBG, fltXPosBG + intScreenW, 0);

    // Draws lives of the screen

    for (int i = intLifeCount; i >= 0; i--) {
      image(imgLives, 965 - i * 5 - i * intLifeSize, 5);
    }

    // Calculate maximum speed in the sprinting state
    if (blnSprint) {
      fltSprintSpeed = fltMaxSpeed;
    } else {
      // Speed is reduced to 60% of the maximum sprint speed
      fltSprintSpeed = fltMaxSpeed * 0.6f;
    }

    // Horizontal movement logic
    if (blnRight) {
      // Change the direction of the character
      imgMC = imgRight;
      imgDash = imgDashR;
      imgCrouch = imgCrouchR;
      // Accelerate right
      fltXSpeed += fltAccel;
      if (fltXSpeed > fltSprintSpeed) {
        fltXSpeed = fltSprintSpeed;
      }
      // Move character and background
      if (fltXPos >= width - 200 - intWidthMC) {
        fltXPosBG -= fltXSpeed;
      } else {
        fltXPos += fltXSpeed;
      }
    } else if (blnLeft) {
      // Change the direction of the character
      imgMC = imgLeft;
      imgDash = imgDashL;
      imgCrouch = imgCrouchL;
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

    // Apply gravity
    fltYSpeed += fltGravity;

    // Initializes the character's y position to a falling state
    fltYPos += fltYSpeed;

    // Vertical movement logic (jumping and gravity)
    if (blnJump) {
      if (fltYPos >= fltPreJumpPos) {
        // Start jump
        fltYSpeed = fltJumpHeight;
        blnJump = false;
      }
    }

    // Draws initial platform
    for (int i = 0; i < intStartPlatLength; i++) {
      image(imgBlock, fltStartPlatX + i * intBlockSize, height - intBlockSize - 10);
    }

    // Checks if character is standing on the initial platform
    boolean blnOnStartPlatform = fltYPos + intHeightMC > height - intBlockSize - 10
        && fltXPos + intWidthMC > fltStartPlatX
        && fltXPos < intBlockSize * intStartPlatLength + fltStartPlatX;

    if (blnOnStartPlatform) {
      // Reset y speed only when the character is landing on the platform
      if (fltYSpeed > 0) {
        fltYSpeed = 0;
      }
      // Sets y position to the top of the start platform
      fltYPos = height - intBlockSize - 10 - intHeightMC;
      fltPreJumpPos = height - intBlockSize - 10 - intHeightMC;
    }

    // Stops character from going inside the initial platform
    if (fltYPos > height - intBlockSize - 10 && fltXPos > fltStartPlatX
        && fltXPos < intBlockSize * intStartPlatLength + fltStartPlatX) {
      fltXPos = intBlockSize * intStartPlatLength + fltStartPlatX;
    }

    // Prints moving platforms
    for (int i = 0; i < platforms.size(); i++) {
      // Prints platforms
      platforms.get(i).platformShift(platforms);
      platforms.get(i).draw();
    }

    // Iterates through each moving platform
    for (int i = 0; i < intPlatformCount; i++) {
      // Checks if the character is on top of a moving platform
      if (fltYPos + intHeightMC > platforms.get(i).getPlatformPositionY()
          && fltYPos < platforms.get(i).getPlatformPositionY()
          && fltXPos + intWidthMC / 2 > platforms.get(i).getPlatformPositionX()
          && fltXPos < platforms.get(i).getPlatformPositionX() + intBlockSize * platforms.get(i).getPlatformLength()
              + 30) {

        // Reset y speed only when the character is landing on the platform
        if (fltYSpeed > 0) {
          fltYSpeed = 0;
        }

        // Sets position to the top of the platform
        fltYPos = platforms.get(i).getPlatformPositionY() - intHeightMC;
        fltPreJumpPos = platforms.get(i).getPlatformPositionY() - intHeightMC;
        fltXPos -= platforms.get(i).getPlatformSpeed();
      }

      // Stops character from phasing though platforms
      if (fltYPos > platforms.get(i).getPlatformPositionY()
          && fltYPos < platforms.get(i).getPlatformPositionY() + intBlockSize
          && fltXPos + intWidthMC > platforms.get(i).getPlatformPositionX()
          && fltXPos < platforms.get(i).getPlatformPositionX() + intBlockSize * platforms.get(i).getPlatformLength()
              + 30 && fltYSpeed < 0) {
        // Places character below the platform
        fltYPos = platforms.get(i).getPlatformPositionY() + intBlockSize + 1;
        fltYSpeed = 0;
      }

      // Checks if character can be hit by a platform
      if (fltYPos + intHeightMC > platforms.get(i).getPlatformPositionY()
          && fltYPos < platforms.get(i).getPlatformPositionY() + intBlockSize 
          && fltXPos + intWidthMC > platforms.get(i).getPlatformPositionX()
          && fltXPos < platforms.get(i).getPlatformPositionX() + intBlockSize * platforms.get(i).getPlatformLength()
              + 30) {
        // Character gets pushed by the platform
        fltXPos -= platforms.get(i).getPlatformSpeed();
      }
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
      // Alters movement in crouch position
      fltJumpHeight = -6;
      fltMaxSpeed = 3;

      // Draws dash timer
      textSize(20);
      textAlign(CENTER, CENTER);
      text(strDashDisplay, fltXPos + intWidthMC / 2, fltYPos + 10);

      // Draws character
      image(imgCrouch, fltXPos, fltYPos + (intHeightMC - intCrouchHeightMC));

      // Dash to the right
    } else if (blnDash && fltXPos < fltDashDist && imgDash == imgDashR) {
      fltXPos += 10;
      image(imgDash, fltXPos, fltYPos);

      // Dash to the left
    } else if (blnDash && imgMC == imgLeft && fltXPos > fltDashDist) {
      fltXPos -= 10;
      image(imgDash, fltXPos, fltYPos);

      // Resets charcter to normal state
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
    if (key == 'z' && blnCrouch == false) {
      blnSprint = true;
    }
    if (key == 'x') {
      blnCrouch = true;
    }
    if (key == ' ' && blnCanDash) {
      blnDash = true;
      fltPreDashPos = fltXPos;

      // When facing right
      if (imgDash == imgDashR) {
        // Stops character from dashing past the middle of the screen
        if (fltXPos >= width - 200 - intWidthMC - fltDashLength) {
          fltDashDist = fltXPos + (width - 210 - fltXPos - intWidthMC); // Adds the distance from the scrolling point
        } else {
          fltDashDist = fltXPos + fltDashLength;
        }
        // When facing left
      } else if (imgDash == imgDashL) {
        // Stops character from dashing off the left side of the screen
        if (fltXPos <= fltDashLength) {
          fltDashDist = fltXPos - fltXPos;
        } else {
          fltDashDist = fltXPos - fltDashLength - intWidthMC;
        }
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
