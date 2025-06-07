package main;

import db.DBConnection;
import util.Author;
import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class AuthorForm extends JDialog {
    private JTextField nameField, surnameField, ageField;
    private DBConnection db;

    public AuthorForm(Frame owner, DBConnection db) {
        super(owner, "Add a New Author", true); 
        this.db = db;
        setSize(350, 200);
        setLocationRelativeTo(owner);
        setLayout(new GridLayout(4, 2, 5, 5));
        
        add(new JLabel("Name:"));
        nameField = new JTextField();
        add(nameField);

        add(new JLabel("Surname"));
        surnameField = new JTextField();
        add(surnameField);

        add(new JLabel("Age"));
        ageField = new JTextField();
        add(ageField);
        
        JButton saveButton = new JButton("Saved");
        add(new JLabel());
        add(saveButton);

        saveButton.addActionListener(e -> saveAuthor());
    }

    private void saveAuthor() {
        try {
            Author author = new Author();
            author.setAutName(nameField.getText());
            author.setAutSurname(surnameField.getText());
            author.setAutAge(Integer.parseInt(ageField.getText()));

            db.saveAuthor(author);
            JOptionPane.showMessageDialog(this, "The author was successfully registered.", "Successfully", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Age should be a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred while registering the author.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}