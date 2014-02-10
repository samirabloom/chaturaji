package ac.ic.chaturaji.model;

/**
 * @author samirarabbanian
 */
public class Result extends EqualsHashCodeToString {

    private GameStatus gameStatus;
    private ResultType type;

    private Game game;
    private Move move;

    public Result() {
        // used for JSON serialisation
    }

    public Result(GameStatus gameStatus, Game game, Move move) {
        this.gameStatus = gameStatus;
        this.game = game;
        this.move = move;
    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
    }

    public ResultType getType() {
        return type;
    }

    public void setType(ResultType type) {
        this.type = type;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Move getMove() {
        return move;
    }

    public void setMove(Move move) {
        this.move = move;
    }
}
