import java.util.ArrayList;
import processing.core.PApplet;
import processing.core.PImage;

/**
 * The main code of Skyline Speedster.
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
  int intStartingPlatformCount = 3, intAddedPlatformCount = 3, intPlatformCount = intStartingPlatformCount,
      intBlockSize = 30;
  boolean blnCanAddPlatforms = false;

  // Non-moving platforms
  PImage imgBlock2;
  int intPlat1Length = 5, intPlat2Length = 4, intPlat3Length = 4;
  float fltPlat1X1 = 10, fltPlat1X2 = intBlockSize * intPlat1Length + fltPlat1X1;
  float fltPlat1Y1 = intScreenH - intBlockSize - 10, fltPlat1Y2 = fltPlat1Y1 + intBlockSize;
  float fltPlat2X1 = intScreenW / 2 - 2 * intBlockSize, fltPlat2X2 = intBlockSize * intPlat2Length + fltPlat2X1;
  float fltPlat2Y1 = intScreenH / 2 - intBlockSize + 20, fltPlat2Y2 = fltPlat2Y1 + intBlockSize;
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
      platforms.add(new Platform(this, intBlockSize, intHeightMC, platforms));

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
    drawBackground();
    drawLives();

    fltSprintSpeed = setSprintSpeed();
    blnCanDash = canDash();
    addPlatforms();
    dashTimer();
    applyGravity();

    horizontalMovement();
    verticalMovement();

    drawCharacter();
    drawPlatforms();

    stationaryPlatformCollision(intPlat1Length, fltPlat1X1, fltPlat1X2, fltPlat1Y1, fltPlat1Y2);
    stationaryPlatformCollision(intPlat2Length, fltPlat2X1, fltPlat2X2, fltPlat2Y1, fltPlat2Y2);
    stationaryPlatformCollision(intPlat3Length, fltPlat3X1, fltPlat3X2, fltPlat3Y1, fltPlat3Y2);
    movingPlatformCollision(platforms);
  }

  /**
   * Background logic and draws the background
   */
  public void drawBackground() {
    background(255);
    image(imgMainBG, fltXPosBG, 0);
    image(imgMainBG, fltXPosBG + intScreenW, 0);

    if (fltXPosBG <= -width) {
      fltXPosBG = 0;
    }
  }

  /**
   * Draws character's lives
   */
  public void drawLives() {
    for (int i = intLifeCount; i >= 0; i--) {
      image(imgLives, 965 - i * 5 - i * intLifeSize, 5);
    }
  }

  /**
   * Checks if more platforms can be added and adds them to the arraylist
   */
  public void addPlatforms() {
    // Checks if more platforms can be added
    if (intPlatformCount == intStartingPlatformCount + intAddedPlatformCount) {
      blnCanAddPlatforms = false;
    } else {
      for (int i = 0; i < intPlatformCount; i++) {
        if (platforms.get(i).getPlatformPositionX() < width / 2 - 200) {
          blnCanAddPlatforms = true;
        }
      }
    }

    // Adds them to the arraylist
    if (blnCanAddPlatforms) {
      for (int i = 0; i < intAddedPlatformCount; i++) {
        platforms.add(new Platform(this, intBlockSize, intHeightMC, platforms));
      }
      intPlatformCount += intAddedPlatformCount;
      blnCanAddPlatforms = false;
    }
  }

  /**
   * Applies gravity to the character
   */
  public void applyGravity() {
    fltYSpeed += fltGravity;
    fltYPos += fltYSpeed;
  }

  /**
   * Calculates maximum speed in the sprinting state
   */
  public float setSprintSpeed() {
    if (blnSprint) {
      fltSprintSpeed = fltMaxSpeed;
    } else {
      fltSprintSpeed = fltMaxSpeed * 0.6f;
    }

    return fltSprintSpeed;
  }

  /**
   * Horizontal movement for the character
   */
  public void horizontalMovement() {
    if (blnRight) {
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
      imgMC = imgLeft;
      imgDash = imgDashL;
      imgCrouch = imgCrouchL;

      // Accelerate left
      fltXSpeed -= fltAccel;
      if (fltXSpeed < -fltSprintSpeed) {
        fltXSpeed = -fltSprintSpeed;
      }

      fltXPos += fltXSpeed;
    } else {
      // Deaccelerate
      if (fltXSpeed > 0) {
        fltXSpeed -= fltDecel;

        if (fltXSpeed < 0) {
          fltXSpeed = 0;
          fltXPos += fltXSpeed;
        }
      } else if (fltXSpeed < 0) {
        fltXSpeed += fltDecel;

        if (fltXSpeed > 0) {
          fltXSpeed = 0;
          fltXPos += fltXSpeed;
        }
      }
    }
  }

  /**
   * Vertical movement for the character
   */
  public void verticalMovement() {
    if (blnJump) {
      if (fltYPos >= fltPreJumpPos) {
        fltYSpeed = fltJumpHeight;
        blnJump = false;
      }
    }
  }

  /**
   * Calculates the character's state then draws the character then alters its
   * movement based on its state
   */
  public void drawCharacter() {
    if (blnCrouch) {
      fltJumpHeight = -6;
      fltMaxSpeed = 3;

      drawDashTimer(20);

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

      drawDashTimer(-15);

      image(imgMC, fltXPos, fltYPos);
    }
  }

  /**
   * Draws the dash timer
   */
  public void drawDashTimer(int intYAdded) {
    // Initializes character centre position
    float fltXMiddle = fltXPos + intWidthMC / 2;

    textSize(20);
    textAlign(CENTER, CENTER);
    text(strDashDisplay, fltXMiddle, fltYPos + intYAdded);
  }

  /**
   * Draws all the platforms
   */
  public void drawPlatforms() {
    drawStationaryPlatform(intBlockSize, intPlat1Length, fltPlat1X1, fltPlat1Y1);
    drawStationaryPlatform(intBlockSize, intPlat2Length, fltPlat2X1, fltPlat2Y1);
    drawStationaryPlatform(intBlockSize, intPlat3Length, fltPlat3X1, fltPlat3Y1);

    drawMovingPlatform(platforms);
  }

  /**
   * Draws moving platforms
   * 
   * @param platforms an array of platforms
   */
  public void drawMovingPlatform(ArrayList<Platform> platforms) {
    for (int i = 0; i < platforms.size(); i++) {
      platforms.get(i).platformShift(platforms);
      platforms.get(i).draw();
    }
  }

  /**
   * Draws stationary platforms
   * 
   * @param intSize   size of each platform block
   * @param intLength length of the platform in blocks
   * @param fltPlatX  left side of the platform
   * @param fltPlatY  top of the platform
   */
  public void drawStationaryPlatform(int intSize, int intLength, float fltPlatX, float fltPlatY) {
    for (int i = 0; i < intLength; i++) {
      image(imgBlock2, fltPlatX + i * intSize, fltPlatY);
    }
  }

  public void movingPlatformCollision(ArrayList<Platform> platforms) {
    // Initializes character positions
    float fltXPos2 = fltXPos + intWidthMC;
    float fltYPos2 = fltYPos + intHeightMC;

    // Iterates through each moving platform
    for (int i = 0; i < intPlatformCount; i++) {
      // Initializes attribtues of the moving platforms
      float fltPlatX1 = platforms.get(i).getPlatformPositionX();
      float fltPlatY1 = platforms.get(i).getPlatformPositionY();
      float fltPlatL = platforms.get(i).getPlatformLength();
      float fltPlatS = platforms.get(i).getPlatformSpeed();
      float fltPlatX2 = fltPlatX1 + intBlockSize * fltPlatL + 30;
      float fltPlatY2 = fltPlatY1 + intBlockSize;

      if (isOnPlatform(fltXPos, fltXPos2, fltYPos, fltYPos2, fltPlatX1, fltPlatX2, fltPlatY1)) {
        fltYSpeed = resetVerticalSpeed(fltYSpeed);
        fltYPos = setPosition(fltYPos, fltPlatY1);
        fltPreJumpPos = fltYPos;
        fltXPos -= fltPlatS;
      } else if (canPhaseThroughPlatform(fltXPos, fltXPos2, fltYPos, fltPlatX1, fltPlatX2, fltPlatY1, fltPlatY2,
          fltYSpeed)) {
        fltYSpeed = 0;
        fltYPos = setPosition(fltYPos, fltPlatY1);
      } else if (isHitByPlatform(fltXPos2, fltYPos, fltYPos2, fltPlatX1, fltPlatX2, fltPlatY1, fltPlatY2)) {
        fltXPos -= fltPlatS;
        fltXSpeed = 0;
        blnSprint = false;
        blnRight = false;
        blnDash = false;
      }
    }
  }

  /**
   * Handles collision for stationary platforms
   * 
   * @param intPlatLength length of the platform in blocks
   * @param fltPlatX1     left of the platform
   * @param fltPlatX2     right of the platform
   * @param fltPlatY1     top of the platform
   * @param fltPlatY2     bottom of platform
   */
  public void stationaryPlatformCollision(int intPlatLength, float fltPlatX1, float fltPlatX2,
      float fltPlatY1, float fltPlatY2) {
    // Initializes character positions
    float fltXPos2 = fltXPos + intWidthMC;
    float fltYPos2 = fltYPos + intHeightMC;
    float fltXMiddle = fltXPos + intWidthMC / 2;
    float fltYMiddle = fltYPos + intHeightMC / 2;

    boolean blnRightOfPlat = isRightofPlatform(intPlatLength, fltXMiddle, fltPlatX1);

    if (isInPlatform(fltXPos, fltXPos2, fltYMiddle, fltPlatX1, fltPlatX2, fltPlatY1, fltPlatY2)) {
      fltYSpeed = 0;
      fltYPos = setPosition(fltYPos, fltPlatY1);
    } else if (isOnPlatform(fltXPos, fltXPos2, fltYPos, fltYPos2, fltPlatX1, fltPlatX2, fltPlatY1)) {
      fltYSpeed = resetVerticalSpeed(fltYSpeed);
      fltYPos = setPosition(fltYPos, fltPlatY1);
      fltPreJumpPos = fltYPos;
    } else if (canPhaseThroughPlatform(fltXPos, fltXPos2, fltYPos, fltPlatX1, fltPlatX2, fltPlatY1, fltPlatY2,
        fltYSpeed)) {
      fltYSpeed = 0;
      fltYPos = setPosition(fltYPos, fltPlatY1);
    } else if (isInPlatformOnRight(fltXPos, fltXPos2, fltYPos, fltYPos2, fltPlatX1, fltPlatX2, fltPlatY1, fltPlatY2)
        && blnRightOfPlat) {
      blnSprint = false;
      blnLeft = false;
      fltXPos = fltPlatX2;
    } else if (isInPlatformOnRight(fltXPos, fltXPos2, fltYPos, fltYPos2, fltPlatX1, fltPlatX2, fltPlatY1, fltPlatY2)
        && !blnRightOfPlat) {
      blnSprint = false;
      blnRight = false;
      fltXPos = fltPlatX1 - intWidthMC;
    }
  }

  /**
   * Calculates the dash timer and its position
   */
  public void dashTimer() {
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
  }

  /**
   * Checks if character can dash
   */
  public boolean canDash() {
    if (!blnCanDash && millis() - intLastDashTime >= intDashCooldown) {
      blnCanDash = true;
    }

    return blnCanDash;
  }

  /**
   * Resets vertical speed when the character is landing on a platform
   * 
   * @param fltSpeed the vertical speed
   * @return the speed
   */
  public float resetVerticalSpeed(float fltSpeed) {
    if (fltSpeed > 0) {
      fltSpeed = 0;
    }
    return fltSpeed;
  }

  /**
   * Sets y position to the top of a platform
   * 
   * @param fltY     y position of the character
   * @param fltPlatY y position of the platform
   * @return the y character's y position
   */
  public float setPosition(float fltY, float fltPlatY) {
    fltY = fltPlatY - intHeightMC;
    return fltY;
  }

  /**
   * Checks if the character is on the right side of a platform
   * 
   * @param intLength amount of platform blocks
   * @param fltMiddle middle of character horizontally
   * @param fltX      left side of platform
   * @return if the character is on the right side of the platform or not
   */
  public boolean isRightofPlatform(int intLength, float fltMiddle, float fltX) {
    return fltMiddle > fltX + ((intBlockSize * intLength) / 2);
  }

  /**
   * Checks if the character is on top of a platform
   * 
   * @param fltX1     left side of the character
   * @param fltX2     right side of the character
   * @param fltY1     top of the character
   * @param fltY2     bottom of the character
   * @param fltPlatX1 left side of the platform
   * @param fltPlatX2 right side of the platform
   * @param fltPlatY  top of the platform
   * @return if the character is top of the platform or not
   */
  public boolean isOnPlatform(float fltX1, float fltX2, float fltY1, float fltY2, float fltPlatX1, float fltPlatX2,
      float fltPlatY) {
    return fltY2 > fltPlatY && fltY1 < fltPlatY - intHeightMC + 10 && fltX2 > fltPlatX1 && fltX1 < fltPlatX2;
  }

  /**
   * Checks if the character is inside a platform
   * 
   * @param fltX1     left side of the character
   * @param fltX2     right side of the character
   * @param fltMiddle y middle of the character
   * @param fltPlatX1 left side of the platform
   * @param fltPlatX2 right side of the platform
   * @param fltPlatY1 top of the platform
   * @param fltPlatY2 bottom of the platform
   * @return if the character is in the platform or not
   */
  public boolean isInPlatform(float fltX1, float fltX2, float fltMiddle, float fltPlatX1, float fltPlatX2,
      float fltPlatY1, float fltPlatY2) {
    return fltMiddle > fltPlatY1 && fltMiddle < fltPlatY2 && fltX2 > fltPlatX1 && fltX1 < fltPlatX2;
  }

  /**
   * Checks if the character can phase through a platform
   * 
   * @param fltX1     left side of the character
   * @param fltX2     right side of the character
   * @param fltY      top of the character
   * @param fltPlatX1 left side of the platform
   * @param fltPlatX2 right side of the platform
   * @param fltPlatY1 top of the platform
   * @param fltPlatY2 bottom of the platform
   * @param fltSpeed  y speed of the character
   * @return if the character can phase through the platform or not
   */
  public boolean canPhaseThroughPlatform(float fltX1, float fltX2, float fltY, float fltPlatX1, float fltPlatX2,
      float fltPlatY1, float fltPlatY2, float fltSpeed) {
    return fltY > fltPlatY1 && fltY < fltPlatY2 && fltX2 > fltPlatX1 && fltX1 < fltPlatX2 && fltSpeed < 0;
  }

  /**
   * Checks if the character is in the right side of a stationary platform
   * 
   * @param fltX1     left side of the character
   * @param fltX2     right side of the character
   * @param fltY1     top of the character
   * @param fltY2     bottom of the character
   * @param fltPlatX1 left side of the platform
   * @param fltPlatX2 right side of the platform
   * @param fltPlatY1 top of the platform
   * @param fltPlatY2 bottom of the platform
   * @return if the character is in the right side of a stationary platform or not
   */
  public boolean isInPlatformOnRight(float fltX1, float fltX2, float fltY1, float fltY2, float fltPlatX1,
      float fltPlatX2, float fltPlatY1, float fltPlatY2) {
    return fltY2 > fltPlatY1 && fltY1 < fltPlatY2 && fltX2 > fltPlatX1 && fltX1 < fltPlatX2;
  }

  /**
   * Checks if the character is hit by a moving platform
   * 
   * @param fltX      right side of the character
   * @param fltY1     top of the character
   * @param fltY2     bottom of the character
   * @param fltPlatX1 left side of the platform
   * @param fltPlatX2 right side of the platform
   * @param fltPlatY1 top of the platform
   * @param fltPlatY2 bottom of the platform
   * @return if the character is hit by a moving platform or not
   */
  public boolean isHitByPlatform(float fltX, float fltY1, float fltY2, float fltPlatX1, float fltPlatX2,
      float fltPlatY1, float fltPlatY2) {
    return fltY2 > fltPlatY1 && fltY1 < fltPlatY2 && fltX > fltPlatX1 && fltX < fltPlatX2;
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