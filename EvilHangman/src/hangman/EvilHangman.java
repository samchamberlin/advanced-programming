package hangman;

import java.io.File;
import java.io.FileNotFoundException;

public class EvilHangman {

    public static void main(String[] args) {
        //Usage: java [your main class name] dictionary wordLength guesses
        String dictionaryName = args[0];
        int wordLength = Integer.parseInt(args[1]);
        int numGuesses = Integer.parseInt(args[2]);

        try{
            EvilHangmanGame evilHangmanGame = new EvilHangmanGame();
            evilHangmanGame.startGame(new File(dictionaryName), wordLength);
            evilHangmanGame.playGame(numGuesses);
        } catch (EmptyDictionaryException e){
            System.out.println("Empty Dictionary!");
            System.out.println();
        } catch (Exception e){
            System.out.println("Error...");
            System.out.println();
        }

    }
}