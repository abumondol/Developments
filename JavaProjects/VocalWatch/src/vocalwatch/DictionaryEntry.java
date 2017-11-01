/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vocalwatch;

import java.util.ArrayList;

/**
 *
 * @author mm5gg
 */
public class DictionaryEntry {

    public String word;
    public int count;
    public ArrayList<String> meaningList;
    public ArrayList<Integer> meaningCountList;

    public DictionaryEntry(String w) {
        word = w;
        count = 1;
        meaningList = new ArrayList<String>();
        meaningCountList = new ArrayList<Integer>();
    }

    public DictionaryEntry(String w, String mean) {
        word = w;
        count = 1;
        meaningList = new ArrayList<String>();
        meaningCountList = new ArrayList<Integer>();
        meaningList.add(mean);
        meaningCountList.add(new Integer(1));
    }

    public void incrementCount() {
        count++;
    }

    public void addToMeaningList(String meaning) {
        incrementCount();
        meaning = meaning.trim();
        Integer intObj;
        int len = meaningList.size();
        for (int i = 0; i < len; i++) {
            if (meaningList.get(i).equals(meaning)) {
                intObj = meaningCountList.remove(i);
                meaningCountList.add(i, new Integer(intObj.intValue() + 1));
                return;
            }
        }

        intObj = new Integer(1);
        meaningList.add(meaning);
        meaningCountList.add(intObj);
    }

    public void printEntry() {
        int i, listCount = meaningList.size();
        System.out.print(word + "(" + count + ") : ");

        for (i = 0; i < listCount; i++) {
            System.out.print(meaningList.get(i) + "(" + meaningCountList.get(i).intValue() + ")");
            if (i == listCount - 1) {
                System.out.print(";");
            } else {
                System.out.print(", ");
            }
        }
        System.out.println();
    }
    /*public void listToArray() {
     int i, len;
     len = meaningList.size();
     meaningArray = new String[len];
     meaningCountArray = new int[len];
     for (i = 0; i < len; i++) {
     meaningArray[i] = meaningList.get(i);
     meaningCountArray[i] = meaningCountList.get(i).intValue();
     }
     }*/
}
