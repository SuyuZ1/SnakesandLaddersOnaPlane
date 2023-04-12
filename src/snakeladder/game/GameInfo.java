package snakeladder.game;

import java.util.HashMap;

public class GameInfo {

    String player;
    int goUp, goDown;
    HashMap<Integer, Integer> rollMap = new HashMap<>();

    public GameInfo(String player) {
        this.goUp = 0;
        this.goDown = 0;
        this.player = player;
    }

    // count the frequency of die value
    public void updateFrequency(int num) {
        if (rollMap.containsKey(num)) {
            rollMap.put(num, rollMap.get(num) + 1);
        } else {
            rollMap.put(num, 1);
        }
    }
    //update the frequency of moving up or down
    public void moveUp() {
        goUp++;
    }
    public void moveDown() {
        goDown++;
    }

    @Override
    // the output message for task5
    public String toString() {
        String info = String.format("%s rolled: %s, traversed: up=%d, down=%d",
                player,
                rollMap.entrySet().toString(),
                goUp,
                goDown);
        return info;
    }
}
