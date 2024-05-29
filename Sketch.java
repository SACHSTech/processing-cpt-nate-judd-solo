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
  float fltXPosBG = 0, fltSideScrollX = intScreenW / 2;

  // Character
  PImage imgMC, imgCrouch, imgCrouchR, imgCrouchL, imgDash, imgDashR, imgDashL, imgRight, imgLeft;
  // Sizes
  int intWidth = 60, intHeight = 70, intCrouchHeight = 45, intHeightChange = intHeight - intCrouchHeight;
  // Positions
  float fltXPos = 55, fltYPos = 500, fltPreJumpPos = 0, fltDashDist = 0, fltPreDashPos = 0;
  // Speeds
  float fltXSpeed = 0, fltYSpeed = 0, fltMaxSpeed = 5, fltJumpHeight = -9, fltDashLength = 150, fltSprintSpeed = 0;
  float fltAccel = 0.3f, fltDecel = 0.2f, fltGravity = 0.3f;
  // Movement
  boolean blnJump = false, blnLeft = false, blnRight = false, blnSprint = false, blnCrouch = false, blnDash = false;
  boolean blnHasCrouched = false;
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
  float fltPlat2Y1 = intScreenH / 2 - intBlockSize + 30, fltPlat2Y2 = fltPlat2Y1 + intBlockSize;
  float fltPlat3X1 = 800, fltPlat3X2 = intBlockSize * intPlat3Length + fltPlat3X1;
  float fltPlat3Y1 = 200, fltPlat3Y2 = fltPlat3Y1 + intBlockSize;

  // Lives
  PImage imgLives, imgLostLives;
  int intLifeSize = 30, intMaxLifeCount = 5, intCurrentLifeCount = intMaxLifeCount, intLostLifeCount = 0;
  float fltLostLivesPos = 0;

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
    imgMainBG = loadImage("Background1.png");
    imgMainBG.resize(intScreenW, intScreenH);
    // Character states
    imgRight = loadImage("MainCharacter.png");
    imgRight.resize(intWidth, intHeight);
    imgLeft = loadImage("LeftMC.png");
    imgLeft.resize(intWidth, intHeight);
    imgCrouchR = loadImage("MainCharacter.png");
    imgCrouchR.resize(intWidth, intCrouchHeight);
    imgCrouchL = loadImage("LeftMC.png");
    imgCrouchL.resize(intWidth, intCrouchHeight);
    imgDashR = loadImage("DashMC.png");
    imgDashR.resize(intWidth, intHeight);
    imgDashL = loadImage("DashLeft.png");
    imgDashL.resize(intWidth, intHeight);
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
      platforms.add(new Platform(this, intBlockSize, intHeight, platforms));

      // Staggers platforms x positions
      platforms.get(i).setPlatformPositionX(width + 100 * i);
    }

    // Lives images
    imgLives = loadImage("heart.png");
    imgLives.resize(intLifeSize, intLifeSize);
    imgLostLives = loadImage("heart2.png");
    imgLostLives.resize(intLifeSize, intLifeSize);
  }

  /**
   * Top level method to execute the program.
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
    for (int i = updateLifeCount(); i > 0; i--) {
      fltLostLivesPos = width - intMaxLifeCount * 5 - intMaxLifeCount * intLifeSize;
      image(imgLives, width - i * 5 - i * intLifeSize, 5);
    }

    for (int i = 0; i < intLostLifeCount; i++) {
      image(imgLostLives, fltLostLivesPos + i * 5 + i * intLifeSize, 5);
    }
  }

  /**
   * Removes a life from the character then respawns
   * 
   * @return the updated life count
   */
  public int updateLifeCount() {
    if (fltYPos > height + 200) {
      fltYSpeed = 0;
      fltXPos = 55;
      fltYPos = 500;
      intLostLifeCount += 1;
      intCurrentLifeCount -= 1;
    }

    return intCurrentLifeCount;
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
        platforms.add(new Platform(this, intBlockSize, intHeight, platforms));
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
      if (fltXPos >= fltSideScrollX) {
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

      drawDashTimer(10);

      image(imgCrouch, fltXPos, fltYPos + (intHeightChange));

    } else if (blnDash && fltXPos < fltDashDist && imgDash == imgDashR) {
      fltYSpeed = 0;
      fltXPos += 10;

      image(imgDash, fltXPos, fltYPos);

    } else if (blnDash && imgMC == imgLeft && fltXPos > fltDashDist) {
      fltYSpeed = 0;
      fltXPos -= 10;

      image(imgDash, fltXPos, fltYPos);

    } else {
      blnDash = false;
      fltDashDist = 0;
      fltPreDashPos = 0;
      fltJumpHeight = -9;
      fltMaxSpeed = 5;

      drawDashTimer(-15);

      image(imgMC, fltXPos, fltYPos);
    }
  }

  /**
   * Draws the dash timer
   */
  public void drawDashTimer(int intAdjustPosition) {
    // Initializes character centre position
    float fltXMiddle = fltXPos + intWidth / 2;

    textSize(20);
    textAlign(CENTER, CENTER);
    text(strDashDisplay, fltXMiddle, fltYPos + intAdjustPosition);
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

  /**
   * Handles collision for moving platforms
   * 
   * @param platforms an array of platforms
   */
  public void movingPlatformCollision(ArrayList<Platform> platforms) {
    // Initializes character positions
    float fltXPos2 = fltXPos + intWidth;
    float fltYPos2 = fltYPos + intHeight;
    float fltXMiddle = fltXPos + intWidth / 2;

    // Iterates through each moving platform
    for (int i = 0; i < intPlatformCount; i++) {
      // Initializes attribtues of the moving platforms
      float fltPlatX1 = platforms.get(i).getPlatformPositionX();
      float fltPlatY1 = platforms.get(i).getPlatformPositionY();
      float fltPlatL = platforms.get(i).getPlatformLength();
      float fltPlatS = platforms.get(i).getPlatformSpeed();
      float fltPlatX2 = fltPlatX1 + intBlockSize * fltPlatL + 30;
      float fltPlatY2 = fltPlatY1 + intBlockSize;

      boolean blnRightOfPlat = isRightofPlatform((int) fltPlatL, fltXMiddle, fltPlatX1);

      if (isOnPlatform(fltXPos, fltXPos2, fltYPos, fltYPos2, fltPlatX1, fltPlatX2, fltPlatY1)) {
        fltYSpeed = resetVerticalSpeed(fltYSpeed);
        fltYPos = setPosition(fltYPos, fltPlatY1);
        fltPreJumpPos = fltYPos;
        fltXPos -= fltPlatS;
      } else if (canPhaseThroughPlatform(fltXPos, fltXPos2, fltYPos, fltPlatX1, fltPlatX2, fltPlatY1, fltPlatY2,
          fltYSpeed)) {
        fltYSpeed = 0;
        fltYPos = setPosition(fltYPos, fltPlatY1);
      } else if (isInPlatform(fltXPos2, fltXPos2, fltYPos, fltYPos2, fltPlatX1, fltPlatX2, fltPlatY1, fltPlatY2)
          && !blnRightOfPlat) {
        fltXPos -= fltPlatS;
        fltXSpeed = 0;
        blnSprint = false;
        blnRight = false;
        blnDash = false;
      } else if (isInPlatform(fltXPos2, fltXPos2, fltYPos, fltYPos2, fltPlatX1, fltPlatX2, fltPlatY1, fltPlatY2)
          && blnRightOfPlat) {
        fltXSpeed = 0;
        fltXPos = fltPlatX2;
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
    float fltXPos2 = fltXPos + intWidth;
    float fltYPos2 = fltYPos + intHeight;
    float fltXMiddle = fltXPos + intWidth / 2;

    boolean blnRightOfPlat = isRightofPlatform(intPlatLength, fltXMiddle, fltPlatX1);

    if (isOnPlatform(fltXPos, fltXPos2, fltYPos, fltYPos2, fltPlatX1, fltPlatX2, fltPlatY1)) {
      fltYSpeed = resetVerticalSpeed(fltYSpeed);
      fltYPos = setPosition(fltYPos, fltPlatY1);
      fltPreJumpPos = fltYPos;
    } else if (canPhaseThroughPlatform(fltXPos, fltXPos2, fltYPos, fltPlatX1, fltPlatX2, fltPlatY1, fltPlatY2,
        fltYSpeed)) {
      fltYSpeed = 0;
      fltYPos = setPosition(fltYPos, fltPlatY1);
    } else if (isInPlatform(fltXPos, fltXPos2, fltYPos, fltYPos2, fltPlatX1, fltPlatX2, fltPlatY1, fltPlatY2)
        && blnRightOfPlat) {
      fltXSpeed = 0;
      fltXPos = fltPlatX2;
      blnSprint = false;
      blnLeft = false;
      blnDash = false;
    } else if (isInPlatform(fltXPos, fltXPos2, fltYPos, fltYPos2, fltPlatX1, fltPlatX2, fltPlatY1, fltPlatY2)
        && !blnRightOfPlat) {
      fltXSpeed = 0;
      fltXPos = fltPlatX1 - intWidth;
      blnSprint = false;
      blnRight = false;
      blnDash = false;
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
    fltY = fltPlatY - intHeight;
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
    return fltY2 > fltPlatY && fltY1 < fltPlatY - intHeight + 10 && fltX2 > fltPlatX1 && fltX1 < fltPlatX2;
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
   * Checks if the character is in a platform
   * 
   * @param fltX1     left side of the character
   * @param fltX2     right side of the character
   * @param fltY1     top of the character
   * @param fltY2     bottom of the character
   * @param fltPlatX1 left side of the platform
   * @param fltPlatX2 right side of the platform
   * @param fltPlatY1 top of the platform
   * @param fltPlatY2 bottom of the platform
   * @return if the character is in a platform or not
   */
  public boolean isInPlatform(float fltX1, float fltX2, float fltY1, float fltY2, float fltPlatX1,
      float fltPlatX2, float fltPlatY1, float fltPlatY2) {
    fltY1 = adjustCrouchPosition(fltY1);

    return fltY2 > fltPlatY1 && fltY1 < fltPlatY2 && fltX2 > fltPlatX1 && fltX1 < fltPlatX2;
  }

  /**
   * Lowers the chracter's y position when crouching
   * 
   * @param fltY the top of the character
   * @return the new y position of the top of the character
   */
  public float adjustCrouchPosition(float fltY) {
    if (blnCrouch) {
      fltY += intHeightChange;
    } else if (!blnCrouch && blnHasCrouched) {
      fltY -= intHeightChange;
    } else if (fltYSpeed == 0) {
      fltY += fltYSpeed;
    }

    return fltY;
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
    if (key == 'x' && fltYSpeed == 0) {
      blnCrouch = true;
      blnHasCrouched = true;
    }
    if (key == ' ' && blnCanDash) {
      blnDash = true;
      fltPreDashPos = fltXPos;

      // When facing right
      if (imgDash == imgDashR) {
        // Stops character from dashing past the scrolling point
        if (fltXPos >= width - 200 - intWidth - fltDashLength) {
          fltDashDist = fltXPos + (width - 210 - fltXPos - intWidth);
        } else {
          fltDashDist = fltXPos + fltDashLength;
        }
        // When facing left
      } else if (imgDash == imgDashL) {
        // Stops character from dashing off the left side of the screen
        if (fltXPos <= fltDashLength) {
          fltDashDist = fltXPos - fltXPos;
        } else {
          fltDashDist = fltXPos - fltDashLength - intWidth;
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