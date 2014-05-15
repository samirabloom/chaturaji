package ac.ic.chaturaji.ai; /**
 * Created with IntelliJ IDEA.
 * User: vea11
 * Date: 11/05/14
 * Time: 16:40
 * To change this template use File | Settings | File Templates.
 *
 * Created to work alongside the GeneticAlgo class.
 */

/*
import java.awt.geom.Point2D;
import java.util.*;
import java.io.*;

public class ParameterReader {
    //Create the hash map to store all the possible parameters and an arrayList to store the remaining keys
    static Map<Integer, ArrayList<Integer>> ParametersMap;
    static ArrayList<Integer> Keys;

    ParameterReader(){
        ParametersMap = new HashMap<Integer, ArrayList<Integer>>();
        Keys = new ArrayList<Integer>();
        initializeMap();
        int i = 0;
    }
    static public void initializeMap(){
        try {
            BufferedReader reader = null;
            String[] parts = null;
            Integer n = 0;
            ArrayList<Integer> parameters = new ArrayList<Integer>();

            reader = new BufferedReader(new FileReader("/homes/dg3213/Coursework/GeneticAlgo/initialParametersPieces.txt"));

            String line = null;
            //Read one line at a time
            while ((line = reader.readLine()) != null) {

                //Split the line into the different parameters
                parts = line.split(" ");

                //For each part add the parameter to the array
                for (String part : parts) {
                    parameters.add(Integer.valueOf(part));
                }

                //Add the array of parameters to the hash map and the key to the arrayList
                ParametersMap.put(n,parameters);
                Keys.add(n);

                parameters = new ArrayList<Integer>();
                n++;
            }
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    static public void deleteParameters(int a, int b, int c){
        ArrayList<Integer> removeElements = new ArrayList<Integer>();
        removeElements.add(a);
        removeElements.add(b);
        removeElements.add(c);
        Collections.sort(removeElements);

        int index = removeElements.get(2);
        //ParametersMap.remove(a);
        Keys.remove(index);

        index = removeElements.get(1);
        //ParametersMap.remove(b);
        Keys.remove(index);

        index = removeElements.get(0);
        //ParametersMap.remove(c);
        Keys.remove(index);
    }

    static public ArrayList<Integer> getParameters(Integer key){
        return ParametersMap.get(key);
    }

    static public int getSize(){
        return Keys.size();
    }
    /*
    public static void main(String[] args){
        ParameterReader pr = new ParameterReader();
        pr.initializeMap();
        pr.deleteParameters(45,1234,873);
        int p = pr.getSize();

    }
}
*/