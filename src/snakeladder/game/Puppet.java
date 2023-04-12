package snakeladder.game;

import ch.aplu.jgamegrid.*;

import java.awt.Point;

public class Puppet extends Actor
{
  private GamePane gamePane;
  private NavigationPane navigationPane;
  private int cellIndex = 0;
  private int nbSteps;
  private Connection currentCon = null;
  private int y;
  private int dy;
  private boolean isAuto;
  private String puppetName;

  // add 3 new attributes for tasks
  private boolean travel_path = false;
  private boolean back = false;
  private boolean lowest = false;


  private ToggleStrategy toggleStrategy = new AutoToggleStrategy();

  private GameInfo info;

  public String getInfo() {
    return this.info.toString();
  }

  Puppet(GamePane gp, NavigationPane np, String puppetImage, String puppetName)
  {
    super(puppetImage);
    this.gamePane = gp;
    this.navigationPane = np;
    this.puppetName = puppetName;
    this.info = new GameInfo(puppetName);
  }

  public boolean isTravel_path(){return this.travel_path;}
  public void setBack(boolean isBack) {
    this.back = isBack;
  }

  public boolean isAuto() {
    return isAuto;
  }

  public void setAuto(boolean auto) {
    this.isAuto = auto;
  }

  public String getPuppetName() {
    return puppetName;
  }

  public void setPuppetName(String puppetName) {
    this.puppetName = puppetName;
  }

  void go(int nbSteps)
  {
    if (cellIndex == 100)  // after game over
    {
      cellIndex = 0;
      setLocation(gamePane.startLocation);
    }
    this.nbSteps = nbSteps;


    // check the total value is equal to lowest value of dies
    checkLowest(nbSteps);

    setActEnabled(true);
  }

  // check the die is lowest value, and if the die is not reach to dice count
  private void checkLowest(int nbSteps){
    this.travel_path = false;
    // update to gameinfo
    if (nbSteps > 0) {
      info.updateFrequency(nbSteps);
    }
    // check if rolled the lowest number
    if (nbSteps == navigationPane.getNumDice()) {
      this.lowest = true;
    } else {
      this.lowest = false;
    }
  }

  void resetToStartingPoint() {
    cellIndex = 0;
    setLocation(gamePane.startLocation);
    setActEnabled(true);
  }

  public int getCellIndex() {
    return cellIndex;
  }

  private void moveToCell(int nbSteps)
  {
    if (nbSteps > 0) {
      cellIndex++;
    }
    else if (nbSteps < 0) {
      cellIndex--;
    }
    setLocation(GamePane.cellToLocation(cellIndex));
  }


  public void act()
  {
    if ((cellIndex / 10) % 2 == 0)
    {
      if (isHorzMirror())
        setHorzMirror(false);
    }
    else
    {
      if (!isHorzMirror())
        setHorzMirror(true);
    }

    // Animation: Move on connection
    if (currentCon != null && travel_path )
    {
      int x = gamePane.x(y, currentCon);
      setPixelLocation(new Point(x, y));
      y += dy;

      // Check end of connection
      if ((dy > 0 && (y - gamePane.toPoint(currentCon.locEnd).y) > 0)
        || (dy < 0 && (y - gamePane.toPoint(currentCon.locEnd).y) < 0))
      {
        gamePane.setSimulationPeriod(100);
        setActEnabled(false);
        setLocation(currentCon.locEnd);
        cellIndex = currentCon.cellEnd;
        setLocationOffset(new Point(0, 0));
        currentCon = null;
        // only prepare roll when not went backwards
        checkBackwardMove();

      }
      return;
    }

    // Normal movement
    if (nbSteps != 0)
    {
      moveToCell(nbSteps);

      if (cellIndex == 100)  // Game over
      {
        setActEnabled(false);
        navigationPane.prepareRoll(cellIndex);
        return;
      }

      if (nbSteps > 0) nbSteps--;
      if (nbSteps < 0) nbSteps++;
      if (nbSteps == 0)
      {
        // Check if on connection start
        if ((currentCon = gamePane.getConnectionAt(getLocation())) != null &&
                !(this.lowest && currentCon.cellEnd - currentCon.cellStart < 0) && !travel_path)
        {
          gamePane.setSimulationPeriod(50);
          y = gamePane.toPoint(currentCon.locStart).y;
          if (currentCon.locEnd.y > currentCon.locStart.y)
            dy = gamePane.animationStep;
          else
            dy = -gamePane.animationStep;


          if (currentCon instanceof Snake)
          {
            navigationPane.showStatus("Digesting...");
            navigationPane.playSound(GGSound.MMM);
          }
          else
          {
            navigationPane.showStatus("Climbing...");
            navigationPane.playSound(GGSound.BOING);
          }

          // check walk up or down
          if (currentCon.cellEnd - currentCon.cellStart > 0) {
            info.moveUp();
          } else {
            info.moveDown();
          }
          this.travel_path = true;

        }
        else
        {
          setActEnabled(false);
          // only prepare roll when not went backwards
          checkBackwardMove();
        }

      }
    }
  }

  // if isAuto we check toggle button should be pressed
  private void autoPlayerToggleStrategy(){
    if (isAuto) {
      boolean toggle = toggleStrategy.checkIfToggle(navigationPane, gamePane);
      // if toggleButton change then reverse all connections
      if (toggle) {
        System.out.println("Auto toggled connections");
        navigationPane.toggleButton(true);
      } else {
        navigationPane.toggleButton(false);
      }
    }
  }

  private void checkBackwardMove(){
  // only prepare role when not went backwards
    if (!back) {
      // toggle strategy for auto player
      autoPlayerToggleStrategy();
      navigationPane.prepareRoll(cellIndex);
    }
    setBack(false);
  }
}

