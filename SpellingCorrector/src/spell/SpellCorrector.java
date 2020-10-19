package spell;

import java.io.IOException;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

public class SpellCorrector implements ISpellCorrector {

    private Trie myTrie;
    private TreeSet<WordEdit> editedWords;
    private TreeSet<WordEdit> suggestedWords;

    public SpellCorrector(){
        myTrie = new Trie();
        editedWords = new TreeSet<>();
        suggestedWords = new TreeSet<>();
    }


    @Override
    public void useDictionary(String dictionaryFileName) throws IOException {
        myTrie.inputDictionary(dictionaryFileName);
//        System.out.println(myTrie.toString());
    }

    @Override
    public String suggestSimilarWord(String wordIn) {
        editedWords = new TreeSet<>();
        suggestedWords = new TreeSet<>();
        wordIn = wordIn.toLowerCase();
        String wordOut = "";
        if(wordIn.isEmpty()){
            return null;
        } else {
            if(myTrie.find(wordIn) != null){
                return wordIn;
            } else {
                //Edit distance 1
                edit(wordIn, 1);
                //System.out.println(editedWords.toString());
                if(suggestedWords.size() > 0){
                    WordEdit maxCountWord = suggestedWords.first();
                    for(WordEdit sw : suggestedWords){
                        if(sw.getValue() > maxCountWord.getValue()){
                            maxCountWord = sw;
                        }
                    }
                    return maxCountWord.getWord();
                } else {
                    //Edit distance 2
                    TreeSet<WordEdit> editedWordsClone;
                    editedWordsClone = (TreeSet<WordEdit>) editedWords.clone();
                    editedWords = new TreeSet<>();
                    for(WordEdit ew : editedWordsClone){
                        edit(ew.getWord(), 2);
                    }
                    //System.out.println(editedWords.toString());
                    if(suggestedWords.size() > 0){
                        WordEdit maxCountWord = suggestedWords.first();
                        for(WordEdit sw : suggestedWords){
                            if(sw.getValue() > maxCountWord.getValue()){
                                maxCountWord = sw;
                            }
                        }
                        return maxCountWord.getWord();
                    } else {
                        return null;
                    }
                }
            }
        }
    }

    public void edit(String wordIn, int distance){
        delete(wordIn, distance);
        insert(wordIn, distance);
        transpose(wordIn, distance);
        alter(wordIn, distance);

        Iterator<WordEdit> iterator = editedWords.iterator();
        while(iterator.hasNext()){
            WordEdit word = new WordEdit(iterator.next().getWord(), distance);
            INode wordNode = myTrie.find(word.getWord());
            if(wordNode != null){
                int value = wordNode.getValue();
                word.setValue(value);
                if(value > 0){
                    suggestedWords.add(word);
                }
            }
        }
    }

    public void delete(String wordIn, int distance){
        StringBuilder word = new StringBuilder();
        word.append(wordIn);
        for(int i = 1; i <= word.length(); i++){
            StringBuilder wordOut = new StringBuilder();
            wordOut.append(word.substring(0, word.length() - i));
            wordOut.append(word.substring(word.length() - i + 1, word.length()));
            WordEdit x = new WordEdit(wordOut.toString(), distance);
            editedWords.add(x);
        }
    }

    public void insert(String wordIn, int distance){
        StringBuilder word = new StringBuilder();
        word.append(wordIn);
        for(int i = 0; i <= word.length(); i++){
            for(int j = 0; j < 26; j++){
                char temp = (char)('a'+j);
                StringBuilder wordOut = new StringBuilder();
                wordOut.append(word.substring(0, i));
                wordOut.append(temp);
                wordOut.append(word.substring(i));
                WordEdit x = new WordEdit(wordOut.toString(), distance);
                editedWords.add(x);
            }
        }
    }

    public void transpose(String wordIn, int distance){
        StringBuilder word = new StringBuilder();
        word.append(wordIn);
        if(word.length() > 1){
            for(int i = 0; i < word.length() - 1; i++){
                StringBuilder wordOut = new StringBuilder();
                char temp1 = word.charAt(i);
                char temp2 = word.charAt(i+1);
                wordOut.append(word.substring(0, i));
                wordOut.append(temp2);
                wordOut.append(temp1);
                if(word.length() > i + 2){
                    wordOut.append(word.substring(i + 2));
                }
                WordEdit x = new WordEdit(wordOut.toString(), distance);
                editedWords.add(x);
            }
        }
    }

    public void alter(String wordIn, int distance){
        StringBuilder word = new StringBuilder();
        word.append(wordIn);
        for(int i = 0; i < word.length(); i++){
            for(int j = 0; j < 26; j++){
                char temp = (char)('a'+j);
                StringBuilder wordOut = new StringBuilder();
                wordOut.append(word.substring(0, i));
                wordOut.append(temp);
                wordOut.append(word.substring(i+1));
                WordEdit x = new WordEdit(wordOut.toString(), distance);
                editedWords.add(x);
            }
        }
    }

}