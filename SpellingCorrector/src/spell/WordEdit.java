package spell;

import java.util.TreeSet;

public class WordEdit implements Comparable<WordEdit> {

    private String word;
    private int distance;
    private int value;

    public WordEdit(String wordIn, int distanceIn){
        word = wordIn;
        distance = distanceIn;
        value = 0;
    }

    public String getWord(){
        return word;
    }

    public int getDistance(){
        return distance;
    }

    public int getValue(){
        return value;
    }

    public void setValue(int valueIn){
        value = valueIn;
    }

    public String toString(){
        String out = "Word: " + word + '\n';
        out += "Distance: " + distance + '\n';
        out += "Value: " + value + '\n';
        return out;
    }

    @Override
    public int compareTo(WordEdit obj) {
        if(word == obj.word && distance == obj.distance && value == obj.value){
            return 0;
        } else {
            return 1;
        }
    }
}
