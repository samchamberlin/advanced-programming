package spell;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.TreeSet;

public class Trie implements ITrie {

    private Node root;
    private int numWords;
    private int numNodes;
    private int hashCode;

    public Trie(){
        root = new Node();
        numWords = 0;
        numNodes = 1;
        hashCode = 0;
    }

    public void inputDictionary(String file){
        try(Scanner scan = new Scanner(new File(file))){
            while(scan.hasNext()){
                String word = scan.next().toLowerCase();
                add(word);
            }
        } catch (FileNotFoundException e){
            System.out.println(file + " was not found.");
        }
    }

    @Override
    public void add(String word) {
        //Empty?
        //Characters only?
        boolean valid = true;
        if(word.isEmpty()){
            valid = false;
        }
        for(int i = 0; i < word.length(); i++){
            if(word.charAt(i) < 'a' && word.charAt(i) > 'z'){
                valid = false;
            }
        }

        if(valid){
            if(find(word) == null){
                numWords++;
            }
            hashCode = word.hashCode() * 31 + hashCode;
            recursiveAdd(root, word);
        }
    }

    public void recursiveAdd(Node parent, String word){
        Node[] children = parent.getChildren();
        if(word.isEmpty()){
            parent.incrementValue();
        } else {
            int i = word.charAt(0) - 'a';
            if(children[i] == null){
                children[i] = new Node();
                numNodes++;
            }
            recursiveAdd(children[i], word.substring(1));
        }
    }

    @Override
    public INode find(String word) {
        return recursiveFind(root, word);
    }

    public Node recursiveFind(Node parent, String word){
        Node[] children = parent.getChildren();
        if(word.length() > 0){
            int i = word.charAt(0) - 'a';
            if(children[i] == null){
                return null;
            }
            return recursiveFind(children[i], word.substring(1));
        } else {
            if(parent.getValue() > 0) {
                return parent;
            } else {
                return null;
            }
        }
    }

    public String toString() {
        return recursiveToString(root, "").toString();
    }

    public StringBuilder recursiveToString(Node parent, String letters){
        StringBuilder alphabeticDictionary = new StringBuilder();
        Node[] children = parent.getChildren();
        for(int i = 0; i < 26; i++){
            if(children[i] != null){
                StringBuilder currentWord = new StringBuilder();
                currentWord.append(letters);
                currentWord.append((char)('a' + i));
                if(children[i].getValue() > 0){
                    alphabeticDictionary.append(currentWord).append('\n');
                }
                alphabeticDictionary.append(recursiveToString(children[i], currentWord.toString()));
            }
        }
        return alphabeticDictionary;
    }


    public boolean equals(Object o) {
        try {
            Trie t = (Trie) o;
            return recursiveEquals(root, t.root);
        } catch (Exception e){
            System.out.println("Error in equals method.");
            return false;
        }
    }

    public boolean recursiveEquals(Node p1, Node p2){
        Node[] c1 = p1.getChildren();
        Node[] c2 = p2.getChildren();
        if(p1.getValue() != p2.getValue()){
            return false;
        }

        for(int i = 0; i < 26; i++){
            if(c1[i] == null && c2[i] != null){
                return false;
            } else if(c1[i] != null && c2[i] == null){
                return false;
            } else if (p1.getValue() != p2.getValue()){
                return false;
            }
            if(c1[i] != null && c2[i] != null){
                if(!recursiveEquals(c1[i], c2[i])){
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public int getWordCount() {
        return numWords;
    }

    @Override
    public int getNodeCount() {
        return numNodes;
    }

    @Override
    public int hashCode() {
        return hashCode;
    }
}
