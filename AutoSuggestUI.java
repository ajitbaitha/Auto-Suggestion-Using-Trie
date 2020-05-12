
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

import java.util.ArrayList;

class Trie {
    private Node root;
    public Trie() {
        root = new Node();
    }

    public void insertWord(String word) {
        Node temp = root;
        char[] w = word.toCharArray();
        for (char c : w) {
            int idx = c - 'a';
            if(idx<0 || idx>25)
                return;
            if (temp.next[idx] == null) {
                temp.next[idx] = new Node();
            }
            temp = temp.next[idx];
        }
        temp.isEOW = true;
    }

    public void printAllWords() {
        StringBuilder path = new StringBuilder();
        printAllWords(root, path);
    }

    private void printAllWords(Node node, StringBuilder path) {
        if (node.isEOW) {
            System.out.println(path);
        }
        for (int i = 0; i < 26; i++) {
            if (node.next[i] != null) {
                char c = (char) (i + 'a');
                path.append(c);
                printAllWords(node.next[i], path);
                path.setLength(path.length() - 1);
            }
        }
    }

    public void getAllWords(Node node, StringBuilder path, ArrayList<String> allWords) {
        if (node.isEOW) {
            allWords.add(path.toString());
        }
        for (int i = 0; i < 26; i++) {
            if (node.next[i] != null) {
                char c = (char) (i + 'a');
                path.append(c);
                getAllWords(node.next[i], path, allWords);
                path.setLength(path.length() - 1);
            }
        }
    }

    public ArrayList<String> autoSuggest(String prefix) {
        ArrayList<String> suggestions = new ArrayList<>();
        Node temp = root;
        char[] w = prefix.toCharArray();
        for (char c : w) {
            int idx = c - 'a';
            if(idx<0 || idx>25)
                return null;
            if (temp.next[idx] == null) {
                return suggestions;
            }
            temp = temp.next[idx];
        }
        getAllWords(temp, new StringBuilder(prefix), suggestions);       
        return suggestions;
    }

    private class Node {

        boolean isEOW;
        Node[] next;

        public Node() {
            isEOW = false;
            next = new Node[26];
        }
    }
}

public class AutoSuggestUI extends JFrame {
    
    public static final String DICT_FILE = "dictionary.txt";
    private Trie trie = new Trie();

    private JLabel lblTitle;
    private JList listSuggestions;
    private JScrollPane scrollPane;
    private JTextField tfSearchBox;
    private DefaultListModel<String> model = new DefaultListModel<>();

    public AutoSuggestUI() {
        initComponents();
        loadDictionary();
        listSuggestions.setModel(model);
        scrollPane.setVisible(false);
    }

    // Reads dictionary words from DICT_FILE; and inserts them in trie.    
    private void loadDictionary() {
        try {
            BufferedReader br = new BufferedReader(new FileReader(DICT_FILE));
            String line;
            while ((line = br.readLine()) != null) {
                trie.insertWord(line);
            }
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

    // Initializes UI components.
    private void initComponents() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("Being Zero - Auto Suggest UI");
        setPreferredSize(new Dimension(770, 450));
        setResizable(false);
        setLayout(null);

        lblTitle = new JLabel();
        lblTitle.setFont(new java.awt.Font("Tahoma", 1, 48));
        lblTitle.setForeground(new java.awt.Color(102, 0, 102));
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setText("BZ AutoSuggest");
        lblTitle.setBounds(120, 50, 510, 70);
        this.add(lblTitle);

        tfSearchBox = new JTextField();
        tfSearchBox.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent evt) {
                tfSearchBoxKeyReleased(evt);
            }
        });
        tfSearchBox.setBounds(120, 150, 510, 25);
        this.add(tfSearchBox);

        scrollPane = new JScrollPane();
        listSuggestions = new JList();
        listSuggestions.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent evt) {
                listSuggestionsValueChanged(evt);
            }
        });
        scrollPane.setViewportView(listSuggestions);
        scrollPane.setBounds(120, 175, 510, 150);
        this.add(scrollPane);

        pack();
    }

    // Event handler - gets called when user types in search box.
    private void tfSearchBoxKeyReleased(KeyEvent evt) {
        model.clear();
        scrollPane.setVisible(false);
        String prefix = tfSearchBox.getText();
        //if (prefix == null || prefix.length() < 3) {
        if (prefix == null) {
            return;
        }
        ArrayList<String> suggestions = trie.autoSuggest(prefix);
        for (String s : suggestions) {
            model.addElement(s);
        }
        if (model.isEmpty()) {
            scrollPane.setVisible(false);
        } else {
            scrollPane.setVisible(true);
        }
    }

    // Event handler - gets called when user selects an item from suggestions.
    private void listSuggestionsValueChanged(ListSelectionEvent evt) {
        if (evt.getValueIsAdjusting() == false) {
            Object selected = listSuggestions.getSelectedValue();
            if (selected == null) {
                return;
            }
            tfSearchBox.setText(selected.toString());
            model.clear();
            scrollPane.setVisible(false);
        }
    }

    public static void main(String args[]) {
        new AutoSuggestUI().setVisible(true);
    }
}
