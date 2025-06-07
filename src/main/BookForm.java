package main;

import db.DBConnection;
import util.*;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;

public class BookForm extends JDialog {
    private MainPage parentPage;
    private DBConnection db;

    private JTextField bookNameField, genreField, yearField;
    private JComboBox<Author> authorCombo;

    public BookForm(MainPage parent, DBConnection db) {
        super(parent, "Save a New Book", true);
        this.parentPage = parent;
        this.db = db;

        setSize(450, 250);
        setLocationRelativeTo(parent);
        
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(new JLabel("Book Name:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0;
        bookNameField = new JTextField();
        mainPanel.add(bookNameField, gbc);

       
        gbc.gridx = 0; gbc.gridy = 1;
        mainPanel.add(new JLabel("Author:"), gbc);
        JPanel authorPanel = new JPanel(new BorderLayout(5,0));
        authorCombo = new JComboBox<>();
        JButton btnNewAuthor = new JButton("+");
        btnNewAuthor.setMargin(new Insets(2, 5, 2, 5));
        btnNewAuthor.setToolTipText("Add a New Author");
        authorPanel.add(authorCombo, BorderLayout.CENTER);
        authorPanel.add(btnNewAuthor, BorderLayout.EAST);
        gbc.gridx = 1; gbc.gridy = 1;
        mainPanel.add(authorPanel, gbc);

        // Tür
        gbc.gridx = 0; gbc.gridy = 2;
        mainPanel.add(new JLabel("Genre:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2;
        genreField = new JTextField();
        mainPanel.add(genreField, gbc);

        // Yayın Yılı
        gbc.gridx = 0; gbc.gridy = 3;
        mainPanel.add(new JLabel("Year of Publication:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3;
        yearField = new JTextField();
        mainPanel.add(yearField, gbc);
        
        // Butonlar
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 1; gbc.gridy = 4; gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(buttonPanel, gbc);

        add(mainPanel);

        loadAuthors();

        btnNewAuthor.addActionListener(e -> {
            AuthorForm authorForm = new AuthorForm(parentPage, db);
            authorForm.setVisible(true);
            loadAuthors();
        });

        saveButton.addActionListener(e -> saveBook());
        cancelButton.addActionListener(e -> dispose());
    }

    public void loadAuthors() {
        try {
            Author selected = (Author) authorCombo.getSelectedItem();
            authorCombo.removeAllItems();
            ArrayList<Author> authors = db.getAuthor();
            for (Author a : authors) {
                authorCombo.addItem(a);
            }
            if (selected != null) {
                for (int i = 0; i < authorCombo.getItemCount(); i++) {
                    if (authorCombo.getItemAt(i).getAuthId() == selected.getAuthId()) {
                        authorCombo.setSelectedIndex(i);
                        break;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred while loading the authors.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    
    private void saveBook() {
        String name = bookNameField.getText().trim();
        String genre = genreField.getText().trim();
        String yearText = yearField.getText().trim();
        Author selectedAuthor = (Author) authorCombo.getSelectedItem();

        if (name.isEmpty() || genre.isEmpty() || yearText.isEmpty() || selectedAuthor == null) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Incomplete Information", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int year;
        try {
            year = Integer.parseInt(yearText);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number for the year of publication.", "Invalid Year", JOptionPane.WARNING_MESSAGE);
            return;
        }

        
        Book newBook = new Book();
        newBook.setBookName(name);
        newBook.setBookGenre(genre);
        newBook.setPublication(year);
        newBook.setAuthId(selectedAuthor.getAuthId());
        
        
        try {
            db.saveBook(newBook);
            
            JOptionPane.showMessageDialog(this, "The book was successfully registered in the system.", "Successfully", JOptionPane.INFORMATION_MESSAGE);
            
            parentPage.loadAllBooks();
            dispose();
            
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "A database error occurred while saving the book.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}