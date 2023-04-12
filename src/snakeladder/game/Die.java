package snakeladder.game;

import ch.aplu.jgamegrid.Actor;

public class Die extends Actor
{
  private int index;
  private NavigationPane np;


  Die(int index, int value,NavigationPane np)
  {
    super("sprites/pips" + value + ".gif", 7);
    this.index = index;
    this.np = np;

  }

  public void act()
  {
    showNextSprite();
    if (getIdVisible() == 6)
    {
      setActEnabled(false);
      finish();
    }
  }

  // check the dice status, if it finished, print out the message
  private void finish(){
    np.checkFinish(index);
    System.out.println("Set die " + this + " finished.");
  }



}
