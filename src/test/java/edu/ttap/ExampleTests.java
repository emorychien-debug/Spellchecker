package edu.ttap;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import edu.ttap.spellchecker.SpellChecker;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.IntRange;

/** An example test suite. */
public class ExampleTests {
    /** An example JUnit test. */
    @Test
    public void exampleUnitTest() {
        assertEquals(2, 1 + 1);
    }

    /**
     * An example Jqwik property-based test: for all n, does n * (n-1) / 2 equal
     * the sum of 0, ..., n-1?
     * @param n the argument to test the proprety on
     * @return true iff the property holds for the given argument.
    */
    @Property
    public boolean examplePropertyTest(@ForAll @IntRange(min = 0, max = 10000) int n) {
        int sum = 0;
        for (int i = 0; i < n; i++) {
            sum += i;
        }
        return sum == (n * (n - 1)) / 2;
    }

    /**
     * creates a dictonary and checks to see that it contains the correct words. 
     * Tests the constructor and the isWord() procedure
     */
    @Test
    public void basicDictionaryTest() {
        List<String> words = new ArrayList<>();
        words.add("Hello");
        SpellChecker dictonary = new SpellChecker(words);
        assertEquals(dictonary.isWord("Hello"), true);
        assertEquals(dictonary.isWord("World"), false);
    }

    /**
     * creates a dictionary and adds words to it, checks to see that task is completed
     * Tests constructor, isWord procedure, and add procedure
     */
    @Test
    public void newWordTest() {
        SpellChecker dictionary = new SpellChecker(new ArrayList<>());
        assertEquals(dictionary.isWord("Hello"), false);
        dictionary.add("Hello");
        assertEquals(dictionary.isWord("Hello"), true);
    }

    /**
     * creates a dictionary with words, then checks to see if there are any 
     * words that can be made by adding an additional character to the current word
     * tests constructor, getOneCharCompletions
     */
    @Test
    public void oneCharCompletionsTest() {
        List<String> words = new ArrayList<>();
        words.add("though");
        words.add("thought");
        words.add("soda");
        words.add("sods");
        words.add("thorough");
        SpellChecker dictionary = new SpellChecker(words);
        List<String> thoughComps = dictionary.getOneCharCompletions("though");
        assertEquals(thoughComps.size(), 1);
        assertEquals(thoughComps.get(0), "thought");
        List<String> sodComps = dictionary.getOneCharCompletions("sod");
        assertEquals(sodComps.size(), 2);
        List<String> thoughtComps = dictionary.getOneCharCompletions("thought");
        assertEquals(thoughtComps.size(), 0);
    }

    /**
     * creates dictionary with words, then checks to see if any new words
     * can be created simply by substituting the last character of the given word
     * 
     */
    @Test
    public void oneCharCorrectionsTest() {
        List<String> words = new ArrayList<>();
        words.add("think");
        words.add("thing");
        words.add("thingy");
        words.add("things");
        words.add("thin");
        words.add("thins");
        words.add("thine");
        SpellChecker dictionary = new SpellChecker(words);

        List<String> thisReps = dictionary.getOneCharCorrections("this");
        assertEquals(thisReps.size(), 1);
        assertEquals(thisReps.get(0), "thin");
        
        List<String> thinyReps = dictionary.getOneCharCorrections("thiny");
        assertEquals(thinyReps.size(), 4);

        List<String> thingsReps = dictionary.getOneCharCorrections("things");
        assertEquals(thingsReps.size(), 1);;
    }
}
