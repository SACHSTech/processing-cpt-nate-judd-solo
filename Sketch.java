import java.util.ArrayList;
import processing.core.PApplet;
import processing.core.PImage;

/**
 * The main code of Skyline Speedster.
 * 
 * @author NJudd
 */
public class Sketch extends PApplet {
  // Game
  Game game;
  int intLevelWidth;
  int intCurrentLevel = 0, intLevelReset = 0;
  boolean blnStartGame = false, blnControls = false, blnWin = false;

  // Background
  PImage imgBackground;
  int intScreenW = 1000, intScreenH = 800;
  float fltXPosBG = 0, fltYPosBG = 0, fltScrollX = intScreenW / 2 - 60;

  // Character
  PImage imgMC, imgCrouch, imgCrouchR, imgCrouchL, imgRight, imgLeft, imgKeyR, imgKeyL, imgCaught;
  PImage imgJump, imgJumpR, imgJumpL;
  PImage imgR1, imgR2, imgL1, imgL2;
  // Sizes
  int intWidth = 60, intHeight = 70, intCrouchHeight = 45, intHeightChange = intHeight - intCrouchHeight;
  // Positions
  float fltXPos = 450, fltYPos = 350, fltPreJumpPos = 0;
  // Speeds
  float fltXSpeed = 0, fltYSpeed = 0, fltMaxSpeedX = 0, fltMaxSpeedY = 9, fltJumpHeight = 0;
  float fltAccel = 0.3f, fltDecel = 0.2f, fltGravity = 0.3f;
  // Movement
  int intRunCount = 0;
  boolean blnJump = false, blnLeft = false, blnRight = false, blnSprint = false, blnCrouch = false;
  boolean blnHasCrouched = false, blnChangeDirect = false;

  // Platforms
  ArrayList<Platform> platforms;
  ArrayList<MovingPlatform> movingPlatforms;
  ArrayList<Float> platformPositions;
  int intBlockSize = 30;

  // Bird
  ArrayList<Bird> birds;
  ArrayList<Float> birdPositions;
  boolean blnCaught = false;

  // Lives
  PImage imgLives, imgLostLives;
  int intLifeSize = 30, intMaxLifeCount = 5, intCurrentLifeCount = intMaxLifeCount, intLostLifeCount = 0;
  float fltLostLivesPos = 0;

  // Key
  PImage imgKey;
  Position keyPosition;
  int intKeySize = 50;
  float fltKeyX = 0;
  boolean blnHasKey = false;

  // Exit
  PImage imgExit;
  Position exitPosition;
  int intExitSize = 100;
  float fltExitX = 0;
  boolean blnHasExit = true, blnExit = false;

  // Mouse
  boolean blnMouseClicked = false;
  int intJumpCount = 0;

  // Time
  int intTime = 0;
  int intTotalTime = 0;
  boolean blnStartTime = false;

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
    frameRate = 60;

    game = new Game(this);

    setupCharacterImages();

    // Lives images
    imgLives = loadImage("heart.png");
    imgLives.resize(intLifeSize, intLifeSize);
    imgLostLives = loadImage("heart2.png");
    imgLostLives.resize(intLifeSize, intLifeSize);

    // Key image
    imgKey = loadImage("key.png");
    imgKey.resize(intKeySize, intKeySize);

    // Exit image
    imgExit = loadImage("exit.png");
    imgExit.resize(intExitSize, intExitSize);
  }

  /**
   * Top level method to execute the program.
   */
  public void draw() {
    // Title screen
    if (!blnStartGame && !blnControls) {
      imgBackground = loadImage("titleScreen.jpg");
      drawBackground();

      controlScreenCheck();

      drawTitleText("PLAY", 700, 410, 180, 70, 200, 100);
      play(checkForClick(700, 410, 180, 70));

      drawTitleText("Controls", 600, 400, 200, 40, 200, 100);

      // Control screen
    } else if (blnControls) {
      imgBackground = loadImage("controlScreen.jpg");
      drawBackground();

      backButton();

      // Game
    } else if (blnStartGame && intCurrentLifeCount > 0 && !blnWin) {
      blnWin = checkForWin();

      setupLevel();

      drawBackground();
      drawLives();

      fltMaxSpeedX = setSpeed();

      applyGravity();
      horizontalMovement();
      verticalMovement();

      updateCharacterImage();

      drawPlatforms(
          platforms,
          movingPlatforms);

      drawKey();
      checkKey(keyPosition);

      drawExit();
      checkExit(exitPosition);

      drawCharacter();

      drawBirds(birds);

      screenCollision();
      staticPlatformCollision(platforms);
      movingPlatformCollision(movingPlatforms);
      birdCollision(birds);

      getTime();
      drawTime();

      // Win screen
    } else if (blnWin) {
      stopMovement();

      imgBackground = loadImage("winScreen.jpg");
      drawBackground();

      drawTitleText("Your time is " + totalMinutes(intTime) + " minutes and " + totalSeconds(intTime) + " seconds!",
          400, 180, 640, 30, 200, 100);

      drawTitleText("You finished with " + intCurrentLifeCount + " lives remaining!",
          465, 220, 560, 30, 200, 100);

      drawTitleText("You jumped " + intJumpCount + " times!",
          530, 260, 480, 30, 200, 100);

      drawTitleText("Play Again?", 700, 350, 300, 50, 200, 100);
      playAgain(checkForClick(700, 350, 300, 50));

      // Game over screen
    } else {
      stopMovement();

      imgBackground = loadImage("gameOver.jpg");
      drawBackground();

      drawTitleText("Play Again?", 700, 350, 300, 50, 200, 100);
      playAgain(checkForClick(700, 350, 300, 50));
    }
  }

  /**
   * Initializes all of the different character images
   */
  public void setupCharacterImages() {
    imgRight = loadImage("MainCharacter.png");
    imgRight.resize(intWidth, intHeight);
    imgLeft = loadImage("LeftMC.png");
    imgLeft.resize(intWidth, intHeight);
    imgCrouchR = loadImage("MainCharacter.png");
    imgCrouchR.resize(intWidth, intCrouchHeight);
    imgCrouchL = loadImage("LeftMC.png");
    imgCrouchL.resize(intWidth, intCrouchHeight);
    imgKeyR = loadImage("characterkeyR.png");
    imgKeyR.resize(intWidth, intHeight);
    imgKeyL = loadImage("characterkeyL.png");
    imgKeyL.resize(intWidth, intHeight);
    imgJumpR = loadImage("RightJump.png");
    imgJumpR.resize(intWidth, intHeight);
    imgJumpL = loadImage("LeftJump.png");
    imgJumpL.resize(intWidth, intHeight);
    imgR1 = loadImage("Right1.png");
    imgR1.resize(intWidth, intHeight);
    imgR2 = loadImage("Right2.png");
    imgR2.resize(intWidth, intHeight);
    imgL1 = loadImage("Left1.png");
    imgL1.resize(intWidth, intHeight);
    imgL2 = loadImage("Left2.png");
    imgL2.resize(intWidth, intHeight);
    imgCaught = loadImage("caught.png");
    imgCaught.resize(intWidth, intHeight);

    imgJump = imgJumpR;
    imgCrouch = imgCrouchR;
    imgMC = imgRight;
  }

  /**
   * Calculates the level time
   */
  public void getTime() {
    if (blnStartTime) {
      intTime += 1;
    }
  }

  /**
   * Prints the time to the screen
   */
  public void drawTime() {
    textSize(20);
    textAlign(LEFT, TOP);
    fill(255);
    text("Time: " + totalMinutes(intTime) + ":" + totalSeconds(intTime), 5, 5);
  }

  /**
   * Gets the total minutes
   * 
   * @return the total minutes
   */
  public int totalMinutes(int time) {
    return (time / 60) / 60;
  }

  /**
   * Gets the total seconds
   * 
   * @return the total seconds
   */
  public int totalSeconds(int time) {
    return (time / 60) % 60;
  }

  /**
   * Stops character movement
   */
  public void stopMovement() {
    blnRight = false;
    blnLeft = false;
    blnJump = false;
    blnCrouch = false;
    blnSprint = false;
    blnStartTime = false;
  }

  /**
   * Checks if the character has completed the fifth level
   * 
   * @return if the chracter has completed the fifth level or not
   */
  public boolean checkForWin() {
    if (intCurrentLevel == 5 && blnExit) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * Initializes the level elements based on the level number
   */
  public void setupLevel() {
    if (blnHasExit) {
      intCurrentLevel += 1;

      platformPositions = game.getLevel(intCurrentLevel).getStaticPlatformPositions();

      birdPositions = game.getLevel(intCurrentLevel).getBirdPositions();

      keyPosition = game.getLevel(intCurrentLevel).getKeyPosition();
      fltKeyX = keyPosition.getPosX();

      exitPosition = game.getLevel(intCurrentLevel).getExitPosition();
      fltExitX = exitPosition.getPosX();

      intLevelWidth = game.getLevel(intCurrentLevel).getWidth();

      imgBackground = loadImage(game.getLevel(intCurrentLevel).getBackground());

      resetLevel();

      platforms = game.getLevel(intCurrentLevel).getStaticPlatforms();
      movingPlatforms = game.getLevel(intCurrentLevel).getMovingPlatforms();

      birds = game.getLevel(intCurrentLevel).getBirds();

      blnHasExit = false;
    }
  }

  /**
   * Resets the level elements and background position
   */
  public void resetLevel() {
    fltXPosBG = 0;

    game.getLevel(intCurrentLevel).setStaticPlatformPositions(platformPositions);
    game.getLevel(intCurrentLevel).setBirdPositions(birdPositions);

    keyPosition.setPosX(fltKeyX);
    exitPosition.setPosX(fltExitX);

    fltXPos = game.getLevel(intCurrentLevel).getSpawnPosition().getPosX();
    fltYPos = game.getLevel(intCurrentLevel).getSpawnPosition().getPosY();

    imgMC = imgRight;
    blnHasKey = false;
  }

  /**
   * Draws the background
   */
  public void drawBackground() {
    background(255);
    image(imgBackground, fltXPosBG, fltYPosBG);
  }

  /**
   * Prints text buttons on the title screen
   * 
   * @param text  the text displayed
   * @param textY the y position of the text
   * @param rectX the x position of the rectangle
   * @param rectW the width of the rectangle
   */
  public void drawTitleText(String text, float textY, float rectX, int rectW, int size, int fill1, int fill2) {
    int intFill1 = 0;
    int intFill2 = 0;
    float fltRectY = textY - size / 2;
    int intRectH = size + 10;
    if (isInObject(mouseX, mouseX, mouseY, mouseY, rectX, rectX + rectW, fltRectY, fltRectY + intRectH)) {
      intFill1 = fill1;
      intFill2 = fill2;
    } else {
      intFill1 = fill2;
      intFill2 = fill1;
    }
    stroke(intFill1);
    fill(intFill2);
    rect(rectX, fltRectY, rectW, intRectH, 25, 25, 25, 25);

    textAlign(CENTER, CENTER);
    textSize(size);
    fill(intFill1);
    text(text, width / 2, textY);
  }

  /**
   * Checks if the player has clicked one of the start buttons
   * 
   * @param textY y position of the text
   * @param rectX x position of the rectantle
   * @param rectW with of the rectangle
   * @param size  size of the text
   * @return if the player has clicked the button or not
   */
  public boolean checkForClick(float textY, float rectX, int rectW, int size) {
    boolean blnClick = false;
    float fltRectY = textY - size / 2;
    int intRectH = size + 10;

    if (isInObject(mouseX, mouseX, mouseY, mouseY, rectX, rectX + rectW, fltRectY, fltRectY + intRectH)
        && blnMouseClicked) {
      blnClick = true;
      blnMouseClicked = false;
    } else if (blnMouseClicked
        && !isInObject(mouseX, mouseX, mouseY, mouseY, rectX, rectX + rectW, fltRectY, fltRectY + intRectH)) {
      blnMouseClicked = false;
    }

    return blnClick;
  }

  /**
   * Checks if the player pressed play again then resets the game
   * 
   * @param playAgain if the player clicked play again
   */
  public void playAgain(boolean playAgain) {
    if (playAgain) {
      blnStartGame = false;
      blnWin = false;
    }
  }

  /**
   * Checks if the player presses play
   * 
   * @param play
   */
  public void play(boolean play) {
    if (play) {
      intCurrentLifeCount = intMaxLifeCount;
      intLostLifeCount = 0;
      intTime = 0;
      intJumpCount = 0;
      blnStartGame = true;
      intCurrentLevel = intLevelReset;
      blnHasExit = true;
      imgExit = loadImage("exit.png");
      imgExit.resize(intExitSize, intExitSize);
    }
  }

  /**
   * Checks if the player clicks on the controls button
   */
  public void controlScreenCheck() {
    if (mouseX < 600 && mouseX > 400 && mouseY < 630 && mouseY > 580 && blnMouseClicked) {
      blnControls = true;
    }
  }

  /**
   * Checks if the player clicks the back button then returns to title screen
   */
  public void backButton() {
    drawTitleText("Back", 750, 435, 130, 40, 200, 100);

    if (blnMouseClicked && mouseX < 565 && mouseX > 435 && mouseY < 780 && mouseY > 730) {
      blnMouseClicked = false;
      blnControls = false;
    } else {
      blnMouseClicked = false;
    }
  }

  /**
   * Draws platforms
   * 
   * @param platforms       array list of static platforms
   * @param movingPlatforms array list of moving platforms
   */
  public void drawPlatforms(ArrayList<Platform> platforms, ArrayList<MovingPlatform> movingPlatforms) {
    for (int i = 0; i < platforms.size(); i++) {
      platforms.get(i).draw();
    }

    for (int i = 0; i < movingPlatforms.size(); i++) {
      movingPlatforms.get(i).platformShift(movingPlatforms);
      movingPlatforms.get(i).draw();
    }
  }

  /**
   * Draws birds
   * 
   * @param birds array list of birds
   */
  public void drawBirds(ArrayList<Bird> birds) {
    for (int i = 0; i < birds.size(); i++) {
      birds.get(i).flyingBird(birds, blnCaught);
      birds.get(i).draw();
    }
  }

  /**
   * Updates the moving, static platforms, and birds x position
   */
  public void updateObjectPosX() {
    for (int i = 0; i < platforms.size(); i++) {
      updateStaticPlatformPos(platforms.get(i));
    }

    for (int i = 0; i < movingPlatforms.size(); i++) {
      updateMovingPlatformPos(movingPlatforms.get(i));
    }

    for (int i = 0; i < birds.size(); i++) {
      updateBirdPos(birds.get(i));
    }
  }

  /**
   * Moves the static platforms with the screen
   * 
   * @param platform static platform
   */
  public void updateStaticPlatformPos(Platform platform) {
    float fltX = platform.getPosX();

    platform.setPosX(fltX -= fltXSpeed);
  }

  /**
   * Moves the moving platforms with the screen
   * 
   * @param platform static platform
   */
  public void updateMovingPlatformPos(MovingPlatform platform) {
    float fltX = platform.getPosX();
    int intSpeed = platform.getSpeed() + (int) fltXSpeed;

    platform.setPosX(fltX -= intSpeed);
  }

  /**
   * Moves the birds with the screen
   * 
   * @param platform static platform
   */
  public void updateBirdPos(Bird bird) {
    float fltX = bird.getPosX();

    bird.setPosX(fltX -= fltXSpeed);
  }

  /**
   * Removes a life from the character then respawns
   * 
   * @return the updated life count
   */
  public int updateLifeCount() {
    if (fltYPos > height + 200) {
      fltYSpeed = 0;

      blnCaught = false;

      resetLevel();

      intLostLifeCount += 1;
      intCurrentLifeCount -= 1;
    }

    return intCurrentLifeCount;
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
   * Moves the key with the screen
   * 
   */
  public void updateKeyPosition() {
    Position keyPos = keyPosition;
    float fltX = keyPos.getPosX();

    keyPos.setPosX(fltX -= fltXSpeed);
  }

  /**
   * Draws the key if the character does not have the key
   * 
   * @param keyPosition position of the key
   */
  public void drawKey() {
    if (!blnHasKey) {
      image(imgKey, keyPosition.getPosX(), keyPosition.getPosY());
    }
  }

  /**
   * Checks if the character has the key
   * 
   * @param position key position
   * @return if the character has the key or not
   */
  public boolean checkKey(Position position) {
    float fltXMiddle = fltXPos + intWidth / 2;
    float fltYMiddle = fltYPos + intHeight / 2;
    float keyX1 = position.getPosX();
    float keyX2 = keyX1 + intKeySize;
    float keyY1 = position.getPosY();
    float keyY2 = keyY1 + intKeySize;

    if (!blnHasKey && fltXMiddle < keyX2 && fltXMiddle > keyX1 && fltYMiddle < keyY2 && fltYMiddle > keyY1) {
      blnHasKey = true;
    }

    return blnHasKey;
  }

  /**
   * Moves the exit with the screen
   */
  public void updateExitPosition() {
    Position exitPos = exitPosition;
    float fltX = exitPos.getPosX();

    exitPos.setPosX(fltX -= fltXSpeed);
  }

  /**
   * Draws the exit
   * 
   * @param exitPosition position of the exit
   */
  public void drawExit() {
    if (intCurrentLevel == 5) {
      imgExit = loadImage("exit2.png");
      imgExit.resize(150, 150);
    }
    image(imgExit, exitPosition.getPosX(), exitPosition.getPosY());
  }

  /**
   * Checks if the character is on the exit
   * 
   * @param position exit position
   * @return if the character is on the exit or not
   */
  public boolean checkExit(Position position) {
    float fltXMiddle = fltXPos + intWidth / 2;
    float fltYPos2 = fltYPos + intHeight;
    float exitX1 = position.getPosX();
    float exitX2 = exitX1 + intExitSize;
    float exitY1 = position.getPosY();
    float exitY2 = exitY1 + intExitSize;

    if (!blnHasExit && blnHasKey && fltXMiddle < exitX2 && fltXMiddle > exitX1 && fltYPos < exitY2
        && fltYPos2 > exitY1 && blnExit) {
      blnHasExit = true;
      resetLevel();
    } else {
      blnHasExit = false;
    }

    return blnHasExit;
  }

  /**
   * Applies gravity to the character
   */
  public void applyGravity() {
    if (fltYSpeed > fltMaxSpeedY) {
      fltYSpeed = fltMaxSpeedY;
    } else {
      fltYSpeed += fltGravity;
    }

    fltYPos += fltYSpeed;
  }

  /**
   * Calculates the speed in each state
   */
  public float setSpeed() {
    if (blnSprint) {
      fltMaxSpeedX = 5;
    } else if (blnCrouch) {
      fltMaxSpeedX = 1;
    } else {
      fltMaxSpeedX = 3;
    }

    return fltMaxSpeedX;
  }

  /**
   * Allows character image to change direction
   */
  public void updateCharacterImage() {
    if (blnCaught) {
      blnRight = false;
      blnLeft = false;
      blnJump = false;
      blnCrouch = false;
      fltXSpeed = 0;
      imgMC = imgCaught;
    } else {
      if (blnChangeDirect && !blnLeft && !blnRight && !blnJump) {
        if (blnHasKey) {
          imgMC = imgKeyL;
        } else {
          imgMC = imgLeft;
        }
      } else if (!blnChangeDirect && !blnLeft && !blnRight && !blnJump) {
        if (blnHasKey) {
          imgMC = imgKeyR;
        } else {
          imgMC = imgRight;
        }
      }

      if (fltYPos < fltPreJumpPos) {
        if (blnRight) {
          imgJump = imgJumpR;
        } else if (blnLeft) {
          imgJump = imgJumpL;
        } else if (!blnChangeDirect && !blnRight && !blnLeft) {
          imgJump = imgJumpR;
        } else if (blnChangeDirect && !blnRight && !blnLeft) {
          imgJump = imgJumpL;
        }

        imgMC = imgJump;
      }

      if (blnRight && fltYPos > fltPreJumpPos && fltYSpeed < 1) {
        if (blnSprint) {
          intRunCount += 2;
        } else {
          intRunCount += 1;
        }

        if (intRunCount <= 15) {
          imgMC = imgR1;
        } else if (intRunCount > 15 && intRunCount <= 30) {
          imgMC = imgR2;
        } else {
          intRunCount = 0;
        }
      } else if (blnLeft && fltYPos > fltPreJumpPos && fltYSpeed < 1) {
        if (blnSprint) {
          intRunCount += 2;
        } else {
          intRunCount += 1;
        }

        if (intRunCount <= 15) {
          imgMC = imgL1;
        } else if (intRunCount > 15 && intRunCount <= 30) {
          imgMC = imgL2;
        } else {
          intRunCount = 0;
        }
      }
    }
  }

  /**
   * Horizontal movement for the character
   */
  public void horizontalMovement() {
    if (blnRight && blnLeft) {
      fltXSpeed = 0;

    } else if (blnRight) {
      imgCrouch = imgCrouchR;
      blnChangeDirect = false;
      blnStartTime = true;

      // Accelerate
      fltXSpeed += fltAccel;
      if (fltXSpeed > fltMaxSpeedX) {
        fltXSpeed = fltMaxSpeedX;
      }

      // Move character and background
      if (fltXPos >= fltScrollX && fltXPosBG > -intLevelWidth + intScreenW + intWidth) {
        fltXSpeed = setSpeed();
        fltXPosBG -= fltXSpeed;
        updateObjectPosX();
        updateKeyPosition();
        updateExitPosition();
      } else {
        fltXPos += fltXSpeed;
      }

    } else if (blnLeft) {
      imgCrouch = imgCrouchL;
      blnChangeDirect = true;
      blnStartTime = true;

      // Accelerate
      fltXSpeed -= fltAccel;
      if (fltXSpeed < -fltMaxSpeedX) {
        fltXSpeed = -fltMaxSpeedX;
      }

      // Move character and background
      if (fltXPos <= fltScrollX && fltXPosBG < 0) {
        fltXSpeed = -setSpeed();
        fltXPosBG -= fltXSpeed;
        updateObjectPosX();
        updateKeyPosition();
        updateExitPosition();
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

      intRunCount = 0;
    }
  }

  /**
   * Vertical movement for the character
   */
  public void verticalMovement() {
    if (blnJump) {
      blnStartTime = true;

      if (fltYPos >= fltPreJumpPos) {
        intJumpCount += 1;
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
      fltJumpHeight = -5.5f;

      image(imgCrouch, fltXPos, fltYPos + (intHeightChange));

    } else {
      fltJumpHeight = -8.5f;

      image(imgMC, fltXPos, fltYPos);
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

    // Iterates through each platform
    for (int i = 0; i < platforms.size(); i++) {
      // Initializes attribtues of the platforms
      float fltPlatX1 = platforms.get(i).getPosX();
      float fltPlatY1 = platforms.get(i).getPosY();
      float fltPlatL = platforms.get(i).getLength();
      float fltPlatX2 = fltPlatX1 + intBlockSize * fltPlatL;
      float fltPlatY2 = fltPlatY1 + intBlockSize;

      if (!blnCaught) {
        if (isOnPlatform(fltXPos, fltXPos2, fltYPos, fltYPos2, fltPlatX1, fltPlatX2, fltPlatY1)) {
          fltYSpeed = resetVerticalSpeed(fltYSpeed);
          fltYPos = setPosition(fltYPos, fltPlatY1);
          blnJump = false;
          fltPreJumpPos = fltYPos;
        } else if (canPhaseThroughPlatform(fltXPos, fltXPos2, fltYPos, fltPlatX1, fltPlatX2, fltPlatY1, fltPlatY2,
            fltYSpeed)) {
          fltYSpeed = 0;
          fltYPos = setPosition(fltYPos, fltPlatY1);
        } else if (isInObject(fltXPos, fltXPos2, fltYPos, fltYPos2, fltPlatX1, fltPlatX2 - fltPlatL / 2, fltPlatY1,
            fltPlatY2)) {
          fltXSpeed = 0;
          fltXPos = fltPlatX1 - intWidth;
          blnSprint = false;
          blnRight = false;
        } else if (isInObject(fltXPos, fltXPos2, fltYPos, fltYPos2, fltPlatX1, fltPlatX2, fltPlatY1, fltPlatY2)) {
          fltXSpeed = 0;
          fltXPos = fltPlatX2;
          blnSprint = false;
          blnLeft = false;
        }
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

    // Iterates through each moving platform
    for (int i = 0; i < movingPlatforms.size(); i++) {
      // Initializes attribtues of the moving platforms
      float fltPlatX1 = movingPlatforms.get(i).getPosX();
      float fltPlatY1 = movingPlatforms.get(i).getPosY();
      float fltPlatL = movingPlatforms.get(i).getLength();
      float fltPlatS = movingPlatforms.get(i).getSpeed();
      float fltPlatX2 = fltPlatX1 + intBlockSize * fltPlatL;
      float fltPlatY2 = fltPlatY1 + intBlockSize;

      if (!blnCaught) {
        if (isOnPlatform(fltXPos, fltXPos2, fltYPos, fltYPos2, fltPlatX1, fltPlatX2, fltPlatY1)) {
          fltYSpeed = resetVerticalSpeed(fltYSpeed);
          fltYPos = setPosition(fltYPos, fltPlatY1);
          blnJump = false;
          fltPreJumpPos = fltYPos;
          fltXPos -= fltPlatS;
        } else if (canPhaseThroughPlatform(fltXPos, fltXPos2, fltYPos, fltPlatX1, fltPlatX2, fltPlatY1, fltPlatY2,
            fltYSpeed)) {
          fltYSpeed = 0;
          fltYPos = setPosition(fltYPos, fltPlatY1);
        } else if (isInObject(fltXPos, fltXPos2, fltYPos, fltYPos2, fltPlatX1, fltPlatX2 - fltPlatL / 2, fltPlatY1,
            fltPlatY2)) {
          fltXSpeed = 0;
          fltXPos -= fltPlatS;
          blnSprint = false;
          blnRight = false;
        } else if (isInObject(fltXPos, fltXPos2, fltYPos, fltYPos2, fltPlatX1, fltPlatX2, fltPlatY1, fltPlatY2)) {
          fltXSpeed = 0;
          fltXPos = fltPlatX2;
          blnSprint = false;
          blnLeft = false;
        }
      }
    }
  }

  /**
   * Handles collision for birds
   * 
   * @param birds an array of bird objects
   */
  public void birdCollision(ArrayList<Bird> birds) {
    // Initializes character positions
    float fltXPos2 = fltXPos + intWidth;
    float fltYPos2 = fltYPos + intHeight;

    // Iterates through each bird
    for (int i = 0; i < birds.size(); i++) {
      // Initializes attribtues of the birds
      float fltBirdX1 = birds.get(i).getPosX() + 10;
      float fltBirdY1 = birds.get(i).getPosY() + 10;
      float fltBirdX2 = fltBirdX1 + birds.get(i).getBirdWidth() - 10;
      float fltBirdY2 = fltBirdY1 + birds.get(i).getBirdHeight() - 10;

      if (isInObject(fltXPos, fltXPos2, fltYPos, fltYPos2, fltBirdX1, fltBirdX2, fltBirdY1, fltBirdY2)) {
        blnCaught = true;
        blnCrouch = false;
        fltXPos = fltBirdX1 + birds.get(i).getBirdWidth() / 2 - intWidth / 2;
        fltYPos = fltBirdY2 - 20;
        fltYSpeed = birds.get(i).getBirdSpeed();
        blnRight = false;
        blnLeft = false;
      }
    }
  }

  /**
   * Stops the character from exiting the screen from the top and sides
   */
  public void screenCollision() {
    if (fltYPos < 0) {
      fltYSpeed = 0;
      fltYPos = 0;
    }

    if (fltXPos < 0) {
      fltXSpeed = 0;
      fltXPos = 0;
    }

    if (fltXPos > width - intWidth) {
      fltXSpeed = 0;
      fltXPos = width - intWidth;
    }
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
  public boolean isInObject(float fltX1, float fltX2, float fltY1, float fltY2, float fltPlatX1,
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
    }

    return fltY;
  }

  /**
   * Handles key press events.
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
    if (key == 'z' || key == 'Z' && blnCrouch == false) {
      blnSprint = true;
    }
    if (key == 'x' || key == 'X' && fltYSpeed == 0) {
      blnCrouch = true;
      blnHasCrouched = true;
    }
    if (key == ' ') {
      blnExit = true;
    }
    if (keyCode == ENTER) {
      blnStartGame = true;
    }
  }

  /**
   * Handles key release events.
   */
  public void keyReleased() {
    if (keyCode == LEFT) {
      blnLeft = false;
    }
    if (keyCode == RIGHT) {
      blnRight = false;
    }
    if (key == 'z' || key == 'Z') {
      blnSprint = false;
    }
    if (key == 'x' || key == 'X') {
      blnCrouch = false;
    }
    if (key == ' ') {
      blnExit = false;
    }
  }

  /**
   * Handles mouse clicked events.
   */
  public void mouseClicked() {
    blnMouseClicked = true;
  }
}