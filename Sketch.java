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
  float fltXPosBG = 0, fltYPosBG = -200, fltScrollX = intScreenW / 2 - 30, fltScrollY = intScreenH / 2 - 50;

  // Character
  PImage imgMC, imgCrouch, imgCrouchR, imgCrouchL, imgDash, imgDashR, imgDashL, imgRight, imgLeft;
  // Sizes
  int intWidth = 60, intHeight = 70, intCrouchHeight = 45, intHeightChange = intHeight - intCrouchHeight;
  // Positions
  float fltXPos = 450, fltYPos = 350, fltPreJumpPos = 0, fltDashDist = 0, fltPreDashPos = 0;
  // Speeds
  float fltXSpeed = 0, fltYSpeed = 0, fltMaxSpeed = 0, fltJumpHeight = 0, fltDashLength = 150;
  float fltAccel = 0.3f, fltDecel = 0.2f, fltGravity = 0.3f;
  // Movement
  boolean blnJump = false, blnLeft = false, blnRight = false, blnSprint = false, blnCrouch = false, blnDash = false;
  boolean blnHasCrouched = false;
  // Dashing
  String strDashDisplay = "";
  int intDashCooldown = 5000, intLastDashTime = 0, intDashCount = 0;
  boolean blnCanDash = true;

  // Levels
  int intCurrentLevel = 1;

  // Platforms
  ArrayList<Platform> platforms = new ArrayList<Platform>();
  ArrayList<MovingPlatform> movingPlatforms = new ArrayList<MovingPlatform>();
  int intBlockSize = 30;

  // Lives
  PImage imgLives, imgLostLives;
  int intLifeSize = 30, intMaxLifeCount = 5, intCurrentLifeCount = intMaxLifeCount, intLostLifeCount = 0;
  float fltLostLivesPos = 0;

  // Game and levels
  Game game;
  GameLevel currentLevel;

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
    // Game and level settings
    game = new Game(this);
    currentLevel = game.getLevel(intCurrentLevel);

    // Background
    imgMainBG = loadImage("Background1.png");

    setUpCharacterImages();

    // Lives images
    imgLives = loadImage("heart.png");
    imgLives.resize(intLifeSize, intLifeSize);
    imgLostLives = loadImage("heart2.png");
    imgLostLives.resize(intLifeSize, intLifeSize);

    // Creates platforms
    platforms.add(new Platform(this, intBlockSize, 97, 10, 750));
    movingPlatforms.add(new MovingPlatform(this, intBlockSize, 4, 2, width, 600));
  }

  /**
   * Top level method to execute the program.
   */
  public void draw() {
    drawBackground();
    drawLives();

    fltMaxSpeed = setSpeed();
    blnCanDash = canDash();
    dashTimer();

    applyGravity();
    horizontalMovement();
    verticalMovement();

    drawCharacter();
    drawPlatforms(platforms, movingPlatforms);

    staticPlatformCollision(platforms);
    movingPlatformCollision(movingPlatforms);
  }

  /**
   * Initializes all of the different character images
   */
  public void setUpCharacterImages() {
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
  }

  /**
   * Background logic and draws the background
   */
  public void drawBackground() {
    background(255);
    image(imgMainBG, fltXPosBG, fltYPosBG);
  }

  /**
   * Updates all the moving and static platforms x position
   */
  public void updatePlatformsPosX() {
    for (int i = 0; i < platforms.size(); i++) {
      updateStaticPlatformPosX(platforms.get(i));
    }
    for (int i = 0; i < movingPlatforms.size(); i++) {
      updateMovingPlatformPosX(movingPlatforms.get(i));
    }
  }

  /**
   * Moves the static platforms with the screen
   * 
   * @param platform static platform
   */
  public void updateStaticPlatformPosX(Platform platform) {
    float fltX = platform.getPosX();

    platform.setPosX(fltX -= fltXSpeed);
  }

  /**
   * Moves the moving platforms with the screen
   * 
   * @param platform static platform
   */
  public void updateMovingPlatformPosX(MovingPlatform platform) {
    float fltX = platform.getPosX();
    int intSpeed = platform.getSpeed() + (int) fltXSpeed;

    platform.setPosX(fltX -= intSpeed);
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
      setPosition(55, 500);
      intLostLifeCount += 1;
      intCurrentLifeCount -= 1;
    }

    return intCurrentLifeCount;
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
  public float setSpeed() {
    if (blnSprint) {
      fltMaxSpeed = 5;
    } else if (blnCrouch) {
      fltMaxSpeed = 1;
    } else {
      fltMaxSpeed = 3;
    }

    return fltMaxSpeed;
  }

  /**
   * Horizontal movement for the character
   */
  public void horizontalMovement() {
    if (blnRight && blnLeft) {
      fltXSpeed = 0;

    } else if (blnRight) {
      imgMC = imgRight;
      imgDash = imgDashR;
      imgCrouch = imgCrouchR;

      // Accelerate
      fltXSpeed += fltAccel;
      if (fltXSpeed > fltMaxSpeed) {
        fltXSpeed = fltMaxSpeed;
      }

      // Move character and background
      if (fltXPos >= fltScrollX && fltXPosBG > -currentLevel.getWidth() + intScreenW + intWidth) {
        fltXSpeed = setSpeed();
        fltXPosBG -= fltXSpeed;
        updatePlatformsPosX();
      } else {
        fltXPos += fltXSpeed;
      }

    } else if (blnLeft) {
      imgMC = imgLeft;
      imgDash = imgDashL;
      imgCrouch = imgCrouchL;

      // Accelerate
      fltXSpeed -= fltAccel;
      if (fltXSpeed < -fltMaxSpeed) {
        fltXSpeed = -fltMaxSpeed;
      }

      // Move character and background
      if (fltXPos <= fltScrollX && fltXPosBG < 0) {
        fltXSpeed = -setSpeed();
        fltXPosBG -= fltXSpeed;
        updatePlatformsPosX();
      } else {
        fltXPos += fltXSpeed;
      }
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
    // // Vertical scrolling
    // if (fltYPos <= fltScrollY) {
    // fltYPos += fltYSpeed;
    // fltYPosBG -= fltYSpeed;
    // }

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

      drawDashTimer(10);

      image(imgCrouch, fltXPos, fltYPos + (intHeightChange));

    } else if (blnDash && fltXPos < fltDashDist && imgDash == imgDashR) {
      fltYSpeed = 0;

      if (fltXPos > fltScrollX) {
        blnDash = false;
        blnRight = false;
        fltXSpeed = 0;
      } else {
        fltXPos += 10;
      }

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
   * Draws platforms
   */
  public void drawPlatforms(ArrayList<Platform> platforms, ArrayList<MovingPlatform> movingPlatforms) {
    // Static platform
    for (int i = 0; i < platforms.size(); i++) {
      platforms.get(i).draw();
    }

    // Moving platforms
    for (int i = 0; i < movingPlatforms.size(); i++) {
      movingPlatforms.get(i).platformShift(movingPlatforms);
      movingPlatforms.get(i).draw();
    }
  }

  /**
   * Handles collision for static platforms
   * 
   * @param platforms an array of moving platforms
   */
  public void staticPlatformCollision(ArrayList<Platform> platforms) {
    // Initializes character positions
    float fltXPos2 = fltXPos + intWidth;
    float fltYPos2 = fltYPos + intHeight;
    float fltXMiddle = fltXPos + intWidth / 2;

    // Iterates through each platform
    for (int i = 0; i < platforms.size(); i++) {
      // Initializes attribtues of the platforms
      float fltPlatX1 = platforms.get(i).getPosX();
      float fltPlatY1 = platforms.get(i).getPosY();
      float fltPlatL = platforms.get(i).getLength();
      float fltPlatX2 = fltPlatX1 + intBlockSize * fltPlatL;
      float fltPlatY2 = fltPlatY1 + intBlockSize;

      boolean blnRightOfPlat = isRightofPlatform((int) fltPlatL, fltXMiddle, fltPlatX1);

      if (isOnPlatform(fltXPos, fltXPos2, fltYPos, fltYPos2, fltPlatX1, fltPlatX2, fltPlatY1)) {
        fltYSpeed = resetVerticalSpeed(fltYSpeed);
        fltYPos = setPosition(fltYPos, fltPlatY1);
        fltPreJumpPos = fltYPos;
      } else if (canPhaseThroughPlatform(fltXPos, fltXPos2, fltYPos, fltPlatX1, fltPlatX2, fltPlatY1, fltPlatY2,
          fltYSpeed)) {
        fltYSpeed = 0;
        fltYPos = setPosition(fltYPos, fltPlatY1);
      } else if (isInPlatform(fltXPos2, fltXPos2, fltYPos, fltYPos2, fltPlatX1, fltPlatX2, fltPlatY1, fltPlatY2)
          && !blnRightOfPlat) {
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
   * Handles collision for moving platforms
   * 
   * @param movingPlatforms an array of moving platforms
   */
  public void movingPlatformCollision(ArrayList<MovingPlatform> movingPlatforms) {
    // Initializes character positions
    float fltXPos2 = fltXPos + intWidth;
    float fltYPos2 = fltYPos + intHeight;
    float fltXMiddle = fltXPos + intWidth / 2;

    // Iterates through each moving platform
    for (int i = 0; i < movingPlatforms.size(); i++) {
      // Initializes attribtues of the moving platforms
      float fltPlatX1 = movingPlatforms.get(i).getPosX();
      float fltPlatY1 = movingPlatforms.get(i).getPosY();
      float fltPlatL = movingPlatforms.get(i).getLength();
      float fltPlatS = movingPlatforms.get(i).getSpeed();
      float fltPlatX2 = fltPlatX1 + intBlockSize * fltPlatL;
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
      intLastDashTime = millis();
      blnCanDash = false;

      if (imgDash == imgDashR) {
        fltDashDist = fltXPos + fltDashLength;
      } else if (imgDash == imgDashL) {
        fltDashDist = fltXPos - fltDashLength - intWidth;
      }
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