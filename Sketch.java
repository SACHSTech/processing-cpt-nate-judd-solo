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
  int intCurrentLevel = 0, intLevelReset = 4;
  boolean blnStartGame = false, blnControls = false, blnWin = false;

  // Background
  PImage imgBackground;
  int intScreenW = 1000, intScreenH = 800;
  float fltXPosBG = 0, fltYPosBG = 0, fltScrollX = intScreenW / 2 - 60;

  // Character
  PImage imgMC, imgCrouch, imgCrouchR, imgCrouchL, imgRight, imgLeft, imgKeyR, imgKeyL;
  // Sizes
  int intWidth = 60, intHeight = 70, intCrouchHeight = 45, intHeightChange = intHeight - intCrouchHeight;
  // Positions
  float fltXPos = 450, fltYPos = 350, fltPreJumpPos = 0;
  // Speeds
  float fltXSpeed = 0, fltYSpeed = 0, fltMaxSpeedX = 0, fltMaxSpeedY = 9, fltJumpHeight = 0;
  float fltAccel = 0.3f, fltDecel = 0.2f, fltGravity = 0.3f;
  // Movement
  boolean blnJump = false, blnLeft = false, blnRight = false, blnSprint = false, blnCrouch = false;
  boolean blnHasCrouched = false;

  // Platforms
  ArrayList<Platform> platforms;
  ArrayList<MovingPlatform> movingPlatforms;
  ArrayList<Float> platformPositions;
  int intBlockSize = 30;

  // Bird
  ArrayList<Bird> birds;
  ArrayList<Float> birdPositions;

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

      drawObjects(
          platforms,
          movingPlatforms,
          birds);

      drawKey();
      checkKey(keyPosition);

      drawExit();
      checkExit(exitPosition);

      drawCharacter();

      screenCollision();
      staticPlatformCollision(platforms);
      movingPlatformCollision(movingPlatforms);
      birdCollision(birds);

      // Win screen
    } else if (blnWin) {
      stopMovement();

      imgBackground = loadImage("winScreen.jpg");
      drawBackground();

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

    imgCrouch = imgCrouchR;
    imgMC = imgRight;
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
    drawTitleText("Back", 750, 435, 130, 40, 100, 200);

    if (blnMouseClicked && mouseX < 565 && mouseX > 435 && mouseY < 780 && mouseY > 730) {
      blnMouseClicked = false;
      blnControls = false;
    } else {
      blnMouseClicked = false;
    }
  }

  /**
   * Draws platforms and birds
   */
  public void drawObjects(ArrayList<Platform> platforms, ArrayList<MovingPlatform> movingPlatforms,
      ArrayList<Bird> birds) {
    for (int i = 0; i < platforms.size(); i++) {
      platforms.get(i).draw();
    }

    for (int i = 0; i < movingPlatforms.size(); i++) {
      movingPlatforms.get(i).platformShift(movingPlatforms);
      movingPlatforms.get(i).draw();
    }

    for (int i = 0; i < birds.size(); i++) {
      birds.get(i).flyingBird(birds);
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
    if (blnRight) {
      if (blnHasKey) {
        imgMC = imgKeyR;
      } else {
        imgMC = imgRight;
      }

      imgCrouch = imgCrouchR;
    }

    if (blnLeft) {
      if (blnHasKey) {
        imgMC = imgKeyL;
      } else {
        imgMC = imgLeft;
      }

      imgCrouch = imgCrouchL;
    }
  }

  /**
   * Horizontal movement for the character
   */
  public void horizontalMovement() {
    if (blnRight && blnLeft) {
      fltXSpeed = 0;

    } else if (blnRight) {
      updateCharacterImage();

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
      updateCharacterImage();

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

      if (isOnPlatform(fltXPos, fltXPos2, fltYPos, fltYPos2, fltPlatX1, fltPlatX2, fltPlatY1)) {
        fltYSpeed = resetVerticalSpeed(fltYSpeed);
        fltYPos = setPosition(fltYPos, fltPlatY1);
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

      if (isOnPlatform(fltXPos, fltXPos2, fltYPos, fltYPos2, fltPlatX1, fltPlatX2, fltPlatY1)) {
        fltYSpeed = resetVerticalSpeed(fltYSpeed);
        fltYPos = setPosition(fltYPos, fltPlatY1);
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
        fltYPos = height + 201;
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