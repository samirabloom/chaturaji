package ac.ic.chaturaji.integration;

/**
 * Created by dg3213 on 04/03/14.
 */
public class Move_AITest {
/*
    private Move_AI GetMove(String filename) {
        BufferedReader reader = null;
        int[] move = new int[6];
        Move_AI theMove = null;
        boolean triumph = false;

        for (int i = 0; i < 6; i++) {
            move[i] = 0;
        }

        try {
            reader = new BufferedReader(new FileReader(filename));
            String line = reader.readLine();
            int count = 0;

            while (line != null && count < 6) {
                move[count] = Integer.parseInt(line);
                count++;
                line = reader.readLine();
            }
            triumph = Boolean.parseBoolean(line);
        } catch (IOException e) {
            e.printStackTrace();
        }

        int dest = move[0];
        int source = move[1];
        int piece = move[2];
        int captured = move[3];
        int type = move[4];
        int promo = move[5];

        theMove = new Move_AI(piece, source, dest);
        theMove.SetCaptured(captured);
        theMove.SetType(type);
        theMove.SetPromotion(promo);
        theMove.SetBoatTriumph(triumph);

        return theMove;
    }

    @Test
    public void MoveTest() {
        //String filename = "/homes/dg3213/Coursework/Group Project/Tests/Move1.txt";

        Move_AI theMove = new Move_AI();

        theMove.SetDest(0);
        theMove.SetSource(0);
        theMove.SetPiece(0);
        theMove.SetCaptured(0);
        theMove.SetType(0);
        theMove.SetPromotion(0);
        theMove.SetBoatTriumph(false);

        assertEquals(0, theMove.getDest());
        assertEquals(0, theMove.getPiece());
        assertEquals(0, theMove.getSource());
        assertEquals(0, theMove.getCaptured());
        assertEquals(0, theMove.getPromoType());
        assertEquals(0, theMove.getType());
        assertEquals(false, theMove.getTriumph());
    }*/
}
