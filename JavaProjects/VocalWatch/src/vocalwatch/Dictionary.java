package vocalwatch;

import java.util.ArrayList;

/**
 *
 * @author mm5gg
 */
public class Dictionary {

    private ArrayList<DictionaryEntry> list;

    public Dictionary() {
        list = new ArrayList<DictionaryEntry>();
    }

    public int getSize() {
        return list.size();
    }

    public DictionaryEntry getWordEntry(String word) {
        int listCount = list.size();
        for (int i = 0; i < listCount; i++) {
            if (list.get(i).word.equals(word)) {
                return list.get(i);
            }
        }
        return null;
    }

    public void addWordAndMeaning(String word, String meaning) {
        int i, listCount = list.size();
        DictionaryEntry de = getWordEntry(word);
        if (de != null) {
            de.addToMeaningList(meaning);
        } else {
            de = new DictionaryEntry(word, meaning);
            for (i = 0; i < listCount; i++) {
                if (list.get(i).word.compareTo(word) > 0) {
                    break;
                }
            }

            list.add(i, de);
        }

    }

    public boolean search(String word) {
        int i, len = getSize();
        String dictWord;
        for (i = 0; i < len; i++) {
            dictWord = list.get(i).word;
            if (word.equals(dictWord)) {
                return true;
            }
        }

        return false;
    }

    public void printDictionary() {
        int i, listCount = list.size();
        for (i = 0; i < listCount; i++) {
            list.get(i).printEntry();
        }
    }
}
