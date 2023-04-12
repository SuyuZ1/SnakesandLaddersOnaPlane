package snakeladder.game;

import ch.aplu.jgamegrid.Location;

public class AutoToggleStrategy implements ToggleStrategy {

    @Override
    public boolean checkIfToggle(NavigationPane np, GamePane gp) {
        int numberOfDice = np.getNumDice();
        int countUp = 0;
        int countDown = 0;

        // assume only two players
        int currPlayer = gp.getCurrentPuppetIndex();
        int nextPlayer =0;

        if(currPlayer==0){
            nextPlayer=1;
        }

        // call next player cell index
        int nextPlayerCell = gp.getAllPuppets().get(nextPlayer).getCellIndex();
        // all possible cell for next player
        int range = nextPlayerCell+numberOfDice*6;

        for (int i=nextPlayerCell+1; i<=range; i++) {
            Location currLocation = GamePane.cellToLocation(i);
            Connection currConnection = gp.getConnectionAt(currLocation);
            if (currConnection != null) {
                // check if up or down
                int displacement = currConnection.cellEnd - currConnection.cellStart;
                if (displacement > 0) {
                    countUp++;
                } else {
                    countDown++;
                }
            }
        }

        if (countUp < countDown) {
            return false;
        }
        return true;
    }
    
}
