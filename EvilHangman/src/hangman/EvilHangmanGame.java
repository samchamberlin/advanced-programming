package hangman;

import com.sun.source.tree.Tree;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class EvilHangmanGame implements IEvilHangmanGame {

    private TreeSet<String> dictionary;
    private TreeSet<Character> guessedLetters;
    private String word;
    private int numGuesses;

    public EvilHangmanGame(){
        dictionary = new TreeSet<>();
        guessedLetters = new TreeSet<>();
        word = "";
    }

    @Override
    public void startGame(File dictionaryFile, int wordLength) throws IOException, EmptyDictionaryException {
        dictionary = new TreeSet<>();
        guessedLetters = new TreeSet<>();
        word = "";
        for(int i = 0; i < wordLength; i++){
            word += '-';
        }
        inputDictionary(dictionaryFile, wordLength);
    }

    private void inputDictionary(File dictionaryFile, int wordLength) throws EmptyDictionaryException{
        try{
            Scanner scan = new Scanner(dictionaryFile);
            if(scan.hasNext()){
                while(scan.hasNext()){
                    String wordIn = scan.next().toLowerCase();
                    if(wordIn.length() == wordLength){
                        dictionary.add(wordIn);
                    }
                }
                if(dictionary.isEmpty()){
                    throw new EmptyDictionaryException();
                }
            } else {
                throw new EmptyDictionaryException();
            }
        } catch (FileNotFoundException e){
            System.out.println("File not found!");
        }
    }

    public void playGame(int numGuessesIn){
        numGuesses = numGuessesIn;
        boolean win = false;
        while(numGuesses > 0 && win == false){
            screenOut();
            Scanner input = new Scanner(System.in);
            String guess = input.nextLine().toLowerCase();
            try{
                if(!valid(guess)){
                    throw new Exception();
                }
                makeGuess(guess.charAt(0));
                if(hasCount('-',word) == 0){
                    win = true;
                    System.out.println("You win!");
                    System.out.println("The word was: " + getRandomWord());
                }
            } catch(GuessAlreadyMadeException e){
                System.out.println("This letter has already been guessed!");
                System.out.println();
            } catch (Exception e){
                System.out.println("Invalid Input");
                System.out.println();
            }
        }
    }

    private void screenOut(){
        System.out.println("You have " + numGuesses + " left");
        System.out.print("Used letters: ");
        for(char letter : guessedLetters){
            System.out.print(letter + ", ");
        }
        if(!guessedLetters.isEmpty()){
            System.out.println("\b\b");
        } else {
            System.out.println();
        }
        System.out.println("Word: " + word);
        System.out.print("Enter guess: ");
    }

    private boolean valid(String guess){
        if(guess.length() != 1){
            return false;
        }
        if(guess.charAt(0) >= 'a' && guess.charAt(0) <= 'z'){
            return true;
        }
        return false;
    }

    private int hasCount(char letter, String word){
        int count = 0;
        for(int i = 0; i < word.length(); i++){
            if(letter == word.charAt(i)){
                count++;
            }
        }
        return count;
    }

    @Override
    public Set<String> makeGuess(char guess) throws GuessAlreadyMadeException {
        guess = String.valueOf(guess).toLowerCase().charAt(0);
        if(guessedLetters.contains(guess)){
            throw new GuessAlreadyMadeException();
        }
        guessedLetters.add(guess);

        Map<String, Vector<String>> wordGroups = partition(guess);
        chooseGroup(wordGroups);

        int numLetters = hasCount(guess, word);
        if(numLetters == 1){
            System.out.println("Yes! There is " + numLetters + " " + guess);
            System.out.println();
        } else if(numLetters > 1){
            System.out.println("Yes! There are " + numLetters + " " + guess + "'s");
            System.out.println();
        } else {
            System.out.println("Sorry, there are no " + guess + "'s");
            System.out.println();
            numGuesses--;
        }

        return dictionary;
    }

    public Map<String, Vector<String>> partition(char guess){
        Map<String, Vector<String>> wordGroups = new TreeMap<>();
        for(String word : dictionary){
            StringBuilder key = new StringBuilder();
            for(int i = 0; i < word.length(); i++){
                if(word.charAt(i) == guess){
                    key.append(guess);
                } else {
                    key.append('-');
                }
            }
            if(wordGroups.containsKey(key.toString())){
                wordGroups.get(key.toString()).add(word);
            } else {
                Vector<String> values = new Vector<>();
                values.add(word);
                wordGroups.put(key.toString(),values);
            }
        }
        return wordGroups;
    }

    public void chooseGroup(Map<String, Vector<String>> wordGroups){

        Map<String, Vector<String>> wordGroupsOut = new TreeMap<>();
        String keyOut = "";
        Vector<String> valuesOut;

        //Choose biggest groups
        wordGroupsOut = chooseBiggest(wordGroups);
        if(wordGroupsOut.size() == 1) {
            keyOut = wordGroupsOut.entrySet().iterator().next().getKey();
            valuesOut = wordGroupsOut.entrySet().iterator().next().getValue();
        } else {
            //Choose group with fewest letters
            wordGroupsOut = chooseFewest(wordGroupsOut);
            if(wordGroupsOut.size() == 1){
                keyOut = wordGroupsOut.entrySet().iterator().next().getKey();
                valuesOut = wordGroupsOut.entrySet().iterator().next().getValue();
            } else {
                //Choose group with right most letters
                keyOut = chooseRightMost(wordGroupsOut);
                valuesOut = wordGroupsOut.get(keyOut);
            }
        }

        StringBuilder newWord = new StringBuilder();
        for(int i = 0; i < keyOut.length(); i++){
            if(keyOut.charAt(i) != '-'){
                newWord.append(keyOut.charAt(i));
            } else {
                newWord.append(word.charAt(i));
            }
        }
        word = newWord.toString();
        dictionary = new TreeSet<>(valuesOut);
    }

    public Map<String, Vector<String>> chooseBiggest(Map<String, Vector<String>> wordGroups){
        Map<String, Vector<String>> wordGroupsOut = new TreeMap<>(wordGroups);
        int biggestGroup = 0;

        for(String key : wordGroups.keySet()){
            Vector<String> values = wordGroups.get(key);
            if(values.size() > biggestGroup){
                biggestGroup = values.size();
            }
        }

        for(String key : wordGroups.keySet()){
            Vector<String> values = wordGroups.get(key);
            if(values.size() < biggestGroup){
                wordGroupsOut.remove(key);
            }
        }

        return wordGroupsOut;
    }

    public Map<String, Vector<String>> chooseFewest(Map<String, Vector<String>> wordGroups){
        Map<String, Vector<String>> wordGroupsOut = new TreeMap<>(wordGroups);
        int mostDashes = 0;

        for(String key : wordGroups.keySet()){
            int numDashes = hasCount('-',key);
            if(numDashes > mostDashes){
                mostDashes = numDashes;
            }
        }

        for(String key : wordGroups.keySet()){
            int numDashes = hasCount('-', key);
            if(numDashes < mostDashes){
                wordGroupsOut.remove(key);
            }
        }

        return wordGroupsOut;
    }

    public String chooseRightMost(Map<String, Vector<String>> wordGroups){
        Vector<String> keys = new Vector<>(wordGroups.keySet());
        return recursiveRightMost(keys, word.length());
    }

    public String recursiveRightMost(Vector<String> keys, int length){
        Vector<String> keysOut = new Vector<>();

        for(String key : keys){
            for(int i = length - 1; i >= 0; i--){
                if(key.charAt(i) != '-'){
                    keysOut.add(key);
                }
            }
            if(keysOut.size() == 1){
                return keysOut.firstElement();
            }
        }
        return recursiveRightMost(keysOut, length - 1);
    }

    private String getRandomWord(){
        Vector<String> remainingDictionary = new Vector<>(dictionary);
        int randomNum = new Random().nextInt(remainingDictionary.size());
        String randomWord = "";
        for(int i = 0; i < remainingDictionary.size(); i++){
            if(i == randomNum){
                randomWord = remainingDictionary.elementAt(i);
                break;
            }
        }
        return randomWord;
    }

    @Override
    public SortedSet<Character> getGuessedLetters() {
        return guessedLetters;
    }
}