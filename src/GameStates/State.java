package GameStates;

import main.Game;

import java.awt.event.MouseEvent;

public class State {

    protected Game game;
    public State(Game game){
        this.game = game;
    }

    public Game getGame(){
        return game;
    }

    public void setGameState(GameState state) {

        GameState.state = state;
    }
}
