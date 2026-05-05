package edu.ttap.spellchecker;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A spellchecker maintains an efficient representation of a dictionary for
 * the purposes of checking spelling and provided suggested corrections.
 */
public class SpellChecker {
    /** The number of letters in the alphabet. */
    private static final int NUM_LETTERS = 26;

    /** The path to the dictionary file. */
    private static final String DICT_PATH = "files/words_alpha.txt";

    /**
     * @param filename the path to the dictionary file
     * @return a SpellChecker over the words found in the given file.
     */
    public static SpellChecker fromFile(String filename) throws IOException {
        return new SpellChecker(Files.readAllLines(Paths.get(filename)));
    }

    /** A Node of the SpellChecker structure. */
    private class Node {
        private ArrayList<Node> children;
        private char letter;
        private boolean isWord;

        public Node(char ch, ArrayList<Node> wordEnds, boolean isWord) {
            children = wordEnds;
            letter = ch;
            this.isWord = isWord;
        }
        public Node(char ch, boolean isWord) {
            this(ch, new ArrayList<>(), isWord);
        }
        public boolean addNode(Node n) {
            for(Node cur: children) {
                if(n.getLetter() == cur.getLetter()) {
                    return false;
                }
            }
            children.add(n);
            return true;
        }
        public boolean isWord(){
            return isWord;
        }
        public void setWord() {
            isWord = true;
        }
        public char getLetter() {
            return letter;
        }
        public Node getChild(char ch) {
            for(int i = 0; i < children.size(); i++) {
                if(children.get(i).getLetter() == ch) {
                    return children.get(i);
                }
            }
            return null;
        }
        public boolean hasChild(char ch) {
            for(Node n: children) {
                if(n.getLetter() == ch) {
                    return true;
                }
            }
            return false;
        }
    }

    /** The root of the SpellChecker */
    private Node root;

    /**
     * adds a word to the trie
     * @param word the word to be added
     */
    private void addWord(String word) {
        char[] wordArray = word.toCharArray();
        Node curNode = root;
        for(int i = 0; i < wordArray.length; i++) {
            Node newestNode = curNode.getChild(wordArray[i]);
            if(newestNode != null) {
                curNode = newestNode;
            } else {
                newestNode = new Node(wordArray[i], new ArrayList<>(), false);
                curNode.addNode(newestNode);
                curNode = newestNode;
            }
            if(i == wordArray.length - 1) {
                curNode.setWord();
            }
        }
    }

    /**
     * Checks if the trie, starting from the given node, contains the remaining characters to be processed
     * @param curNode the node to start scanning the trie from
     * @param remaining the characters to look for in the remaining parts of the trie
     * @return true if the remaining characters were in the trie, and that the last one ended a word
     */
    private boolean hasWord(Node curNode, String remaining) {
        char[] remains = remaining.toCharArray();
        for(char ch : remains) {
            Node nextNode = curNode.getChild(ch);
            if(nextNode == null) {
                return false;
            } else {
                curNode = nextNode;
            }
        }
        return curNode.isWord();
    }
    
    /**
     * Constructs a SpellChecker over the given dictionary.
     * @param dict the list of words to include in the dictionary
     */
    public SpellChecker(List<String> dict) {
        root = new Node('\0', new ArrayList<>(), false);
        for(String word : dict) {
            addWord(word);
        }
    }

    /**
     * Adds the given word to the trie
     * @param word the word to add
     */
    public void add(String word) {
        addWord(word);
    }

    /**
     * Checks if the given word is in the dictionary.
     * @param word the word to check
     * @return true if the word is in the dictionary, false otherwise
     * TODO: check this on the MathLAN machines
     */
    public boolean isWord(String word) {
        char[] wordArray = word.toCharArray();
        Node curNode = root;
        for(char ch: wordArray) {
            curNode = curNode.getChild(ch);
            if(curNode == null) {
                return false;
            }
        }
        return curNode.isWord();
    }

    /**
     * Returns a list of all words in the dictionary that can be formed by
     * adding a single character to the end of the given word.
     * @param word the word to complete
     * @return a list of all possible completions
     * TODO: Check this on the MathLAN machines
     */
    public List<String> getOneCharCompletions(String word) {
        char[] wordArray = word.toCharArray();
        Node curNode = root;
        for(int i = 0; i < wordArray.length; i++) {
            curNode = curNode.getChild(wordArray[i]);
            if(curNode == null) {
                return new ArrayList<>();
            }
        }
        List<String> otherEndings = new ArrayList<>();
        for(Node n : curNode.children) {
            otherEndings.add(word + n.getLetter());
        }
        return otherEndings;
    }


    /**
     * Returns a list of all words in the dictionary that can be formed by changing
     * a single character at the end of the given word.
     * @param word the word to correct
     * @return a list of all possible corrections
     */
    public List<String> getOneCharEndCorrections(String word) {
        if(word.equals("")) {
            return new ArrayList<>();
        }
        char[] wordArray = Arrays.copyOfRange(word.toCharArray(), 0, word.length() - 1);
        Node curNode = root;
        for(int i = 0; i < wordArray.length; i++) {
            curNode = curNode.getChild(wordArray[i]);
            if (curNode == null) {
                return new ArrayList<>();
            }
        }
        List<String> otherEndings = new ArrayList<>();
        for(Node n : curNode.children) {
            String candidate = word + n.getLetter();
            if(!word.equals(candidate) || !n.isWord()) {
                otherEndings.add(candidate);
            }
        }
        return otherEndings;
    }

    /**
    * Returns a list of all words in the dictionary that can be formed by adding,
    * removing, or changing a single character in the given word.
    * @param word the word to correct
    * @return a list of all possible corrections
    */
    public List<String> getOneCharCorrections(String word) {
        return null;
    }

    /**
     * The main entry point for the program.
     * @param args the command-line arguments
     */
    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.err.println("Usage: java SpellChecker <command> <word>");
            System.exit(1);
        } else {
            String command = args[0];
            String word = args[1];
            SpellChecker checker = SpellChecker.fromFile(DICT_PATH);
            switch (command) {
                case "check": {
                    System.out.println(checker.isWord(word) ? "correct" : "incorrect");
                    System.exit(0);
                }

                case "complete": {
                    List<String> completions = checker.getOneCharCompletions(word);
                    for (String completion : completions) {
                        System.out.println(completion);
                    }
                    System.exit(0);
                }

                case "correct": {
                    List<String> corrections = checker.getOneCharEndCorrections(word);
                    for (String correction : corrections) {
                        System.out.println(correction);
                    }
                    System.exit(0);
                }

                default: {
                    System.err.println("Unknown command: " + command);
                    System.exit(1);
                }
            }
        }
    }
}
