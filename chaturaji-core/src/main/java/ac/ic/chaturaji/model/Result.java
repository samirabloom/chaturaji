package ac.ic.chaturaji.model;

/**
 * @author samirarabbanian
 */
public class Result extends EqualsHashCodeToString {

    private ResultType type;

    private Game game;
    private Move move;

    public Result() {
        // used for JSON serialisation
    }

    public Result(GameStatus gameStatus, Game game, Move move) {
        if (game != null) {
            game.setGameStatus(gameStatus);
        }
        this.game = game;
        this.move = move;
    }

    public GameStatus getGameStatus() {
        if (game != null) {
            return game.getGameStatus();
        } else {
            return null;
        }
    }

    public void setGameStatus(GameStatus gameStatus) {
        game.setGameStatus(gameStatus);
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
