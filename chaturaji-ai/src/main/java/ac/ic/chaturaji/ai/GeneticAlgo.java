package ac.ic.chaturaji.ai;

import java.io.*;
import java.util.*;

/**
 * Created by dg3213 on 11/05/14.
 *
 * Calls the ParameterReader class to read a list of pre-prepared parameters and
 * then uses them to initialise the AI with the given weights.
 * A game is run (in runGame) where the four AI players go head-to-head. The loser's
 * parameters are deleted when the game is over and the list of possible parameters reduced.
 *
 * Note that this class requires a slight change in the set up of the AI, so the code has been
 * commented out. However, a couple of changes to the AIBoard class (constructor and accessors)
 * and the Evaluation class (when evaluating material balance) should be sufficient to get
 * it running.
 */

/*
public class GeneticAlgo {
    static ParameterReader parameters;
    static Random random;
    static Writer writer;

    GeneticAlgo() {}

    static final class Result {
        private final int[] playerKeys;
        private final int winner;

        public Result(int winner, int[] playerKeys) {
            this.winner = winner;
            this.playerKeys = playerKeys;
        }

        public int getWinner() {
            return winner;
        }

        public int[] getKeys() {
            return playerKeys;
        }
    }

    public static void main(String[] args)
    {
        parameters = new ParameterReader();
        random = new Random();
        Result result;
        PrintWriter writer;

        try {
            writer = new PrintWriter(new BufferedWriter(new FileWriter("/homes/dg3213/Coursework/GeneticAlgo/losers.txt", true)));
            writer.println("The losing keys are:");
            writer.close();
        } catch (IOException ex) {}

        int[] playerKeys;
        int winner, loser1, loser2, loser3;
        int count = 0;

        long maxGame = -1000000000;
        long minGame = 1000000000;

        Result longestGame = null;
        Result shortestGame = null;

        long startTime ;
        long endTime;
        long totalTime = 0;
        long gameTime;

        winner = -1;
        while (parameters.getSize() >= 4) {
            int size = parameters.getSize();

            System.out.println("There are " + size + " more parameters left to compare");

            startTime = System.currentTimeMillis();
            result = runGame(size);
            endTime = System.currentTimeMillis();

            playerKeys = result.getKeys();
            winner = result.getWinner();

            gameTime = endTime - startTime;
            totalTime += gameTime;

            if (gameTime > maxGame) {
                longestGame = result;
                maxGame = gameTime;
            }
            if (gameTime < minGame) {
                shortestGame = result;
                minGame = gameTime;
            }

            loser1 = playerKeys[(winner + 1) % 4];
            loser2 = playerKeys[(winner + 2) % 4];
            loser3 = playerKeys[(winner + 3) % 4];

            String string = new String(loser1 + " " + loser2 + " " + loser3);
            try {
                writer = new PrintWriter(new BufferedWriter(new FileWriter("/homes/dg3213/Coursework/GeneticAlgo/losers.txt", true)));
                writer.println(string);
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            parameters.deleteParameters(loser1, loser2, loser3);
            count++;
        }
        System.out.println("The winning set of parameters has key " + winner);
        ArrayList<Integer> finalParameters = parameters.getParameters(winner);

        System.out.println("The King has value: " + finalParameters.get(0));
        System.out.println("The Elephant has value: " + finalParameters.get(1));
        System.out.println("The Knight has value: " + finalParameters.get(2));
        System.out.println("The Boat has value: " + finalParameters.get(3));
        System.out.println("The Pawn has value: " + finalParameters.get(4));

        System.out.println("");
        System.out.println("The average game had a running time of " + totalTime/count + " milliseconds");

        System.out.println("");
        System.out.println("The shortest game had a running time of " + minGame + " milliseconds");
        winner = shortestGame.getWinner();
        System.out.println("The Winner had key: " + winner);
        System.out.println("The Losers had keys: " + shortestGame.getKeys()[(winner + 1) % 4] + shortestGame.getKeys()[(winner + 2) % 4] + shortestGame.getKeys()[(winner + 3) % 4] );

        System.out.println("");
        System.out.println("The longest game had a running time of " + maxGame + " milliseconds");
        winner = longestGame.getWinner();
        System.out.println("The Winner had key: " + winner);
        System.out.println("The Losers had keys: " + shortestGame.getKeys()[(winner + 1) % 4] + shortestGame.getKeys()[(winner + 2) % 4] + shortestGame.getKeys()[(winner + 3) % 4] );

    }

    static private Result runGame(int size) {
        ArrayList<ArrayList<Integer>> evalParameters = new ArrayList<ArrayList<Integer>>(4);

        int [] players = new int[4];

        for (int i = 0; i < 4; i++) {
            players[i] = random.nextInt(size);
            ArrayList<Integer> para = parameters.getParameters(players[i]);
            evalParameters.add(para);
        }

        int winnerKey = playGame(evalParameters);
        Result result = new Result(winnerKey, players);

        return result;
    }

    static private int playGame(ArrayList<ArrayList<Integer>> evalParameters) {

        Set<Integer> none = new HashSet<>();
        int turn = 0;
        int staleMateCount = 0;
        int winner = -1;
        int max = -100000;

        AIBoard board = new AIBoard(evalParameters);

        ComputerPlayer[] players = new ComputerPlayer[4];
        for (int i = 0; i < 4; i++) {
            players[i] = new ComputerPlayer(GameConstants.YELLOW + i, 0, none);
        }

        while (board.isGameOver() > 1 && staleMateCount <= 25) {
            AIMove aiMove;
            aiMove = players[turn].GetMove(board);

            if (aiMove != null) {
                if (aiMove.getType() != GameConstants.CAPTURE)
                    staleMateCount++;
                else
                    staleMateCount = 0;

                board.ApplyMove(aiMove);
                board.Print();
            } else {
                // No available move so, move on to the next player.
                board.NextPlayer();
            }
            turn++;
            turn = turn % 4;
        }

        for (int i = 0; i < 4; i++) {
            if (players[i].getPoints() > max) {
                winner = i;
                max = players[i].getPoints();
            }
        }
        System.out.println(GameConstants.PlayerStrings[winner] + " has won with " + max + " points!");
        return winner;
    }
}
*/