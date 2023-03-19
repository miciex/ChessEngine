package Engine;

import GameStates.Move;

public class PositionInfo {

    public Move bestMove;
    public int eval;
    public int alpha;
    public int beta;
    public boolean whitesMove;

    public PositionInfo(Move bestMove, int eval, int alpha, int beta, boolean whitesMove){
        this.bestMove = bestMove;
        this.eval = eval;
        this.alpha = alpha;
        this.beta = beta;
        this.whitesMove = whitesMove;
    }

}
