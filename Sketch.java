import java.util.ArrayList;
import processing.core.PApplet;
import processing.core.PImage;

/**
 * The main code of the Rooftop Runner game.
 * 
 * @author NJudd
 */
public class Sketch extends PApplet {
  // Background
  PImage imgMainBG;
  int intScreenW = 1000, intScreenH = 800;
  float fltXPosBG = 0, fltScrollXPos = intScreenW - 260;

  // Character
  PImage imgMC, imgCrouch, imgCrouchR, imgCrouchL, imgDash, imgDashR, imgDashL, imgRight, imgLeft;
  // Sizes
  int intWidthMC = 60, intHeightMC = 70, intCrouchHeightMC = 35;
  // Positions
  float fltXPos = 55, fltYPos = 10, fltPreJumpPos = 0, fltDashDist = 0, fltPreDashPos = 0;
  // Speeds
  float fltXSpeed = 0, fltYSpeed = 0, fltMaxSpeed = 5, fltJumpHeight = -10, fltDashLength = 150, fltSprintSpeed = 0;
  float fltAccel = 0.3f, fltDecel = 0.2f, fltGravity = 0.3f;
  // Movement
  boolean blnJump = false, blnLeft = false, blnRight = false, blnSprint = false, blnCrouch = false, blnDash = false;
  // Dashing
  String strDashDisplay = "";
  int intDashCooldown = 5000, intLastDashTime = 0, intDashCount = 0;
  boolean blnCanDash = true;

  // Moving platforms
  PImage imgBlock;
  ArrayList<Platform> platforms = new ArrayList<Platform>();
  int intPlatformCount = 6, intBlockSize = 30;

  // Non-moving platform 1
  PImage imgBlock2;
  int intPlat1Length = 5;
  float fltPlat1X1 = 10, fltPlat1X2 = intBlockSize * intPlat1Length + fltPlat1X1;
  float fltPlat1Y1 = intScreenH - intBlockSize - 10, fltPlat1Y2 = fltPlat1Y1 + intBlockSize;

  // Non-moving platform 2
  int intPlat2Length = 4;
  float fltPlat2X1 = intScreenW / 2 - 2 * intBlockSize, fltPlat2X2 = intBlockSize * intPlat2Length + fltPlat2X1;
  float fltPlat2Y1 = intScreenH / 2 - intBlockSize + 20, fltPlat2Y2 = fltPlat2Y1 + intBlockSize;

  // Non-moving platform 3
  int intPlat3Length = 4;
  float fltPlat3X2 = fltScrollXPos + intWidthMC, fltPlat3X1 = fltPlat3X2 - intBlockSize * intPlat3Length;
  float fltPlat3Y1 = 200, fltPlat3Y2 = fltPlat3Y1 + intBlockSize;

  // Lives
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

    // Character states
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

    // Stationary platforms
    imgBlock2 = loadImage("platformBlock2.png");
    imgBlock2.resize(intBlockSize, intBlockSize);

    // Moving platforms
    imgBlock = loadImage("platformBlock.png");
    imgBlock.resize(intBlockSize, intBlockSize);

    // Creates platforms
    for (int i = 0; i < intPlatformCount; i++) {
      platforms.add(new Platform(this, intBlockSize, platforms));

      // Staggers platforms x positions
      platforms.get(i).setPlatformPositionX(width + 100 * i);
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

    // Initializes character positions
    float fltXPos2 = fltXPos + intWidthMC;
    float fltYPos2 = fltYPos + intHeightMC;

    // Stops character from falling after a certian point
    // if (fltYPos > fltPlat3Y2 + 50) {
    // fltYPos = fltPlat3Y2 + 50;
    // fltPreJumpPos = fltYPos;
    // }

    // Draws platform 1
    for (int i = 0; i < intPlat1Length; i++) {
      image(imgBlock2, fltPlat1X1 + i * intBlockSize, fltPlat1Y1);
    }

    // Checks if character is on the right of platform 1
    boolean blnRightOfPlat1 = fltXPos + intWidthMC / 2 > fltPlat1X1 + ((intBlockSize * intPlat1Length) / 2);

    // Checks if character is standing on platform 1
    if (fltYPos2 > fltPlat1Y1 && fltYPos < fltPlat1Y1 && fltXPos2 - 10 > fltPlat1X1 && fltXPos + 10 < fltPlat1X2) {
      // Reset y speed only when the character is landing on platform 1
      if (fltYSpeed > 0) {
        fltYSpeed = 0;
      }

      // Sets position to the top of the platform 1
      fltYPos = fltPlat1Y1 - intHeightMC;
      fltPreJumpPos = fltPlat1Y1 - intHeightMC;

      // Allows character to phase though platform 1
    } else if (fltYPos > fltPlat1Y1 && fltYPos < fltPlat1Y2 && fltXPos2 > fltPlat1X1 && fltXPos < fltPlat1X2
        && fltYSpeed < 0) {
      // Places character above the platform and sets its speed to zero
      fltYSpeed = 0;
      fltYPos = fltPlat1Y1 - intHeightMC + 1;

      // Stops character from going through platform 1 from the right
    } else if (blnRightOfPlat1 && fltYPos2 > fltPlat1Y1 && fltYPos < fltPlat1Y2 && fltXPos2 > fltPlat1X1
        && fltXPos < fltPlat1X2) {
      blnLeft = false;
      fltXPos = fltPlat1X2;

      // Stops character from going through platform 1 from the left
    } else if (!blnRightOfPlat1 && fltYPos2 > fltPlat1Y1 && fltYPos < fltPlat1Y2 && fltXPos2 > fltPlat1X1
        && fltXPos < fltPlat1X2) {
      blnRight = false;
      fltXPos = fltPlat1X1 - intWidthMC;
    }

    // Draws platform 2
    for (int i = 0; i < intPlat2Length; i++) {
      image(imgBlock2, fltPlat2X1 + i * intBlockSize, fltPlat2Y1);
    }

    // Checks if character is on the right of platform 2
    boolean blnRightOfPlat2 = fltXPos + intWidthMC / 2 > fltPlat2X1 + ((intBlockSize * intPlat2Length) / 2);

    // Checks if character is standing on platform 2
    if (fltYPos2 > fltPlat2Y1 && fltYPos < fltPlat2Y1 && fltXPos2 - 10 > fltPlat2X1 && fltXPos + 10 < fltPlat2X2) {
      // Reset y speed only when the character is landing on platform 2
      if (fltYSpeed > 0) {
        fltYSpeed = 0;
      }

      // Sets position to the top of the platform 2
      fltYPos = fltPlat2Y1 - intHeightMC;
      fltPreJumpPos = fltPlat2Y1 - intHeightMC;

      // Allows character to phase though platform 2
    } else if (fltYPos > fltPlat2Y1 && fltYPos < fltPlat2Y2 && fltXPos2 > fltPlat2X1 && fltXPos < fltPlat2X2
        && fltYSpeed < 0) {
      // Places character above the platform and sets its speed to zero
      fltYSpeed = 0;
      fltYPos = fltPlat2Y1 - intHeightMC + 1;

      // Stops character from going through platform 2 from the right
    } else if (blnRightOfPlat2 && fltYPos2 > fltPlat2Y1 && fltYPos < fltPlat2Y2 && fltXPos2 > fltPlat2X1
        && fltXPos < fltPlat2X2) {
      blnLeft = false;
      fltXPos = fltPlat2X2;

      // Stops character from going through platform 2 from the left
    } else if (!blnRightOfPlat2 && fltYPos2 > fltPlat2Y1 && fltYPos < fltPlat2Y2 && fltXPos2 > fltPlat2X1
        && fltXPos < fltPlat2X2) {
      blnRight = false;
      fltXPos = fltPlat2X1 - intWidthMC;
    }

    // Draws platform 3
    for (int i = 0; i < intPlat3Length; i++) {
      image(imgBlock2, fltPlat3X1 + i * intBlockSize, fltPlat3Y1);
    }

    // Checks if character is on the right of platform 3
    boolean blnRightOfPlat3 = fltXPos + intWidthMC / 2 > fltPlat3X1 + ((intBlockSize * intPlat3Length) / 2);

    // Checks if character is standing on platform 3
    if (fltYPos2 > fltPlat3Y1 && fltYPos < fltPlat3Y1 && fltXPos2 - 10 > fltPlat3X1 && fltXPos + 10 < fltPlat3X2) {
      // Reset y speed only when the character is landing on platform 3
      if (fltYSpeed > 0) {
        fltYSpeed = 0;
      }

      // Sets position to the top of the platform 3
      fltYPos = fltPlat3Y1 - intHeightMC;
      fltPreJumpPos = fltPlat3Y1 - intHeightMC;

      // Allows character to phase though platform 3
    } else if (fltYPos > fltPlat3Y1 && fltYPos < fltPlat3Y2 && fltXPos2 > fltPlat3X1 && fltXPos < fltPlat3X2
        && fltYSpeed < 0) {
      // Places character above the platform and sets its speed to zero
      fltYSpeed = 0;
      fltYPos = fltPlat3Y1 - intHeightMC + 1;

      // Stops character from going through platform 3 from the right
    } else if (blnRightOfPlat3 && fltYPos2 > fltPlat3Y1 && fltYPos < fltPlat3Y2 && fltXPos2 > fltPlat3X1
        && fltXPos < fltPlat3X2) {
      blnLeft = false;
      fltXPos = fltPlat3X2;

      // Stops character from going through platform 3 from the left
    } else if (!blnRightOfPlat3 && fltYPos2 > fltPlat3Y1 && fltYPos < fltPlat3Y2 && fltXPos2 > fltPlat3X1
        && fltXPos < fltPlat3X2) {
      blnRight = false;
      fltXPos = fltPlat3X1 - intWidthMC;
    }

    // Prints moving platforms
    for (int i = 0; i < platforms.size(); i++) {
      // Prints platforms
      platforms.get(i).platformShift(platforms);
      platforms.get(i).draw();
    }

    // Iterates through each moving platform
    for (int i = 0; i < intPlatformCount; i++) {
      // Initializes attribtues of the moving platforms
      float fltPlatX1 = platforms.get(i).getPlatformPositionX();
      float fltPlatY1 = platforms.get(i).getPlatformPositionY();
      float fltPlatL = platforms.get(i).getPlatformLength();
      float fltPlatS = platforms.get(i).getPlatformSpeed();
      float fltPlatX2 = fltPlatX1 + intBlockSize * fltPlatL + 30;
      float fltPlatY2 = fltPlatY1 + intBlockSize;

      // Checks if the character is on top of a moving platform
      if (fltYPos2 > fltPlatY1 && fltYPos < fltPlatY1 && fltXPos2 - 10 > fltPlatX1 && fltXPos < fltPlatX2) {
        // Reset y speed only when the character is landing on the platform
        if (fltYSpeed > 0) {
          fltYSpeed = 0;
        }

        // Sets position to the top of the platform
        fltYPos = fltPlatY1 - intHeightMC;
        fltPreJumpPos = fltPlatY1 - intHeightMC;
        fltXPos -= fltPlatS;

        // Allows character to phase though platforms
      } else if (fltYPos > fltPlatY1 && fltYPos < fltPlatY2 && fltXPos2 > fltPlatX1 && fltXPos < fltPlatX2
          && fltYSpeed < 0) {
        // Places character above the platform and sets its speed to zero
        fltYSpeed = 0;
        fltYPos = fltPlatY1 - intHeightMC + 1;

        // Checks if character can be hit by a platform
      } else if (fltYPos2 > fltPlatY1 && fltYPos < fltPlatY2 && fltXPos2 > fltPlatX1 && fltXPos < fltPlatX2) {
        // Character gets pushed by the platform
        fltXPos -= fltPlatS;
        fltXSpeed = 0;
        blnRight = false;
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
      text(strDashDisplay, fltXPos + intWidthMC / 2, fltYPos + 20);

      // Draws character
      image(imgCrouch, fltXPos, fltYPos + (intHeightMC - intCrouchHeightMC));

      // Dash to the right
    } else if (blnDash && fltXPos < fltDashDist && imgDash == imgDashR) {
      fltYSpeed = 0;
      fltXPos += 10;
      image(imgDash, fltXPos, fltYPos);

      // Dash to the left
    } else if (blnDash && imgMC == imgLeft && fltXPos > fltDashDist) {
      fltYSpeed = 0;
      fltXPos -= 10;
      image(imgDash, fltXPos, fltYPos);

      // Resets charcter to normal state
    } else {
      blnDash = false;
      fltDashDist = 0;
      fltPreDashPos = 0;
      fltJumpHeight = -10;
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
        // Stops character from dashing past the scrolling point
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