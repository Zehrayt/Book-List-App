package main;

import db.DBConnection;
import util.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.event.TableModelEvent;     
import javax.swing.table.DefaultTableCellRenderer; 
import javax.swing.table.TableCellRenderer;  
import javax.swing.table.TableColumn;         

public class MainPage extends JFrame {

    private User currentUser;
    private DBConnection db;

    
    private JTable allBooksTable;
    private DefaultTableModel allBooksTableModel;
    private TableRowSorter<DefaultTableModel> allBooksSorter;
    private JComboBox<String> allBooksGenreFilter;

    
    private JTable myBooksTable;
    private DefaultTableModel myBooksTableModel;
    private TableRowSorter<DefaultTableModel> myBooksSorter;
    private JComboBox<String> myBooksGenreFilter;
    private JComboBox<BookStatus> myBooksStatusFilter;

    public MainPage(User user) {
        this.currentUser = user;
        this.db = new DBConnection();

        setTitle("Book Management System - Welcome " + currentUser.getName());
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, createLeftPanel(), createRightPanel());
        splitPane.setDividerLocation(550); 

        add(splitPane);

        loadAllBooks();
        loadMyBooks();
    }

    private JPanel createLeftPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("All Books"));

        
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Filter by Type:"));
        allBooksGenreFilter = new JComboBox<>();
        filterPanel.add(allBooksGenreFilter);
        panel.add(filterPanel, BorderLayout.NORTH);
        allBooksGenreFilter.addItemListener(e -> applyAllBooksFilter());


        
        String[] allBooksColumns = {"ID", "Book Name", "Author", "Genre"};
        allBooksTableModel = new DefaultTableModel(allBooksColumns, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        allBooksTable = new JTable(allBooksTableModel);
        allBooksSorter = new TableRowSorter<>(allBooksTableModel);
        allBooksTable.setRowSorter(allBooksSorter);
        panel.add(new JScrollPane(allBooksTable), BorderLayout.CENTER);

        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnAddToList = new JButton("Add to My List");
        JButton btnAddNewBook = new JButton("Save a New Book");
        buttonPanel.add(btnAddToList);
        buttonPanel.add(btnAddNewBook);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        
        btnAddToList.addActionListener(e -> addSelectedBookToMyList());
        btnAddNewBook.addActionListener(e -> openBookForm());

        return panel;
    }

    private JPanel createRightPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("My List"));

        
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Genre:"));
        myBooksGenreFilter = new JComboBox<>();
        filterPanel.add(myBooksGenreFilter);
        filterPanel.add(new JLabel("Status:"));
        myBooksStatusFilter = new JComboBox<>(BookStatus.values());
        myBooksStatusFilter.insertItemAt(null, 0); 
        myBooksStatusFilter.setSelectedIndex(0);
        filterPanel.add(myBooksStatusFilter);
        panel.add(filterPanel, BorderLayout.NORTH);
        
        myBooksGenreFilter.addItemListener(e -> applyMyBooksFilter());
        myBooksStatusFilter.addItemListener(e -> applyMyBooksFilter());

       
        String[] myBooksColumns = {"ID", "Book Name", "Author", "Genre", "Status"};
        myBooksTableModel = new DefaultTableModel(myBooksColumns, 0); 
        myBooksTable = new JTable(myBooksTableModel);
        
        
        JComboBox<BookStatus> statusComboBox = new JComboBox<>(BookStatus.values());
        myBooksTable.getColumnModel().getColumn(4).setCellEditor(new DefaultCellEditor(statusComboBox));
        myBooksTable.getColumnModel().getColumn(4).setCellRenderer(new StatusCellRenderer());

        myBooksSorter = new TableRowSorter<>(myBooksTableModel);
        myBooksTable.setRowSorter(myBooksSorter);
        panel.add(new JScrollPane(myBooksTable), BorderLayout.CENTER);
        
        
        myBooksTableModel.addTableModelListener(e -> {
            if (e.getType() == TableModelEvent.UPDATE) {
                int row = e.getFirstRow();
                int column = e.getColumn();
                if (column == 4) { 
                    int bookId = (int) myBooksTableModel.getValueAt(row, 0);
                    String newStatus = myBooksTableModel.getValueAt(row, 4).toString();
                    updateBookStatus(bookId, newStatus);
                }
            }
        });


        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnRemove = new JButton("Remove from My List");
        buttonPanel.add(btnRemove);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        btnRemove.addActionListener(e -> removeSelectedBookFromMyList());

        return panel;
    }
    
    

    public void loadAllBooks() {
        try {
            allBooksTableModel.setRowCount(0);
            List<Book> allBooks = db.getAllBooksWithAuthor();
            List<String> genres = new ArrayList<>();
            for (Book book : allBooks) {
                allBooksTableModel.addRow(new Object[]{
                        book.getBookId(),
                        book.getBookName(),
                        book.getAuthor().toString(),
                        book.getBookGenre()
                });
                if (!genres.contains(book.getBookGenre())) {
                    genres.add(book.getBookGenre());
                }
            }
            
            allBooksGenreFilter.removeAllItems();
            allBooksGenreFilter.addItem("All");
            genres.stream().sorted().forEach(allBooksGenreFilter::addItem);

        } catch (SQLException e) {
            showError("An error occurred while loading all books.", e);
        }
    }

    public void loadMyBooks() {
        try {
            myBooksTableModel.setRowCount(0);
            List<Book> myBooks = db.getBooksForUser(currentUser.getUserId());
             List<String> genres = new ArrayList<>();
            for (Book book : myBooks) {
                myBooksTableModel.addRow(new Object[]{
                        book.getBookId(),
                        book.getBookName(),
                        book.getAuthor().toString(),
                        book.getBookGenre(),
                        book.getStatus()
                });
                 if (!genres.contains(book.getBookGenre())) {
                    genres.add(book.getBookGenre());
                }
            }

            myBooksGenreFilter.removeAllItems();
            myBooksGenreFilter.addItem("All");
            genres.stream().sorted().forEach(myBooksGenreFilter::addItem);

        } catch (SQLException e) {
            showError("An error occurred while loading your book list.", e);
        }
    }
    
    private void addSelectedBookToMyList() {
        int selectedViewRow = allBooksTable.getSelectedRow();
        if (selectedViewRow == -1) {
            JOptionPane.showMessageDialog(this, "Please choose a book.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow = allBooksTable.convertRowIndexToModel(selectedViewRow);
        int bookId = (int) allBooksTableModel.getValueAt(modelRow, 0);

        try {
            db.linkUserBook(currentUser.getUserId(), bookId);
            loadMyBooks();
            JOptionPane.showMessageDialog(this, "The book has been added to your list!", "Successful", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            
            if (e.getSQLState().startsWith("23")) { 
                 JOptionPane.showMessageDialog(this, "This book is already on your list.", "Warning", JOptionPane.WARNING_MESSAGE);
            } else {
                showError("An error occurred while adding the book.", e);
            }
        }
    }
    
    private void removeSelectedBookFromMyList() {
        int selectedViewRow = myBooksTable.getSelectedRow();
         if (selectedViewRow == -1) {
            JOptionPane.showMessageDialog(this,"Please select a book from your list.", "Warning" , JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow = myBooksTable.convertRowIndexToModel(selectedViewRow);
        int bookId = (int) myBooksTableModel.getValueAt(modelRow, 0);

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to remove the book from your list?", "Confirmation", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                db.unlinkUserBook(currentUser.getUserId(), bookId);
                loadMyBooks(); 
            } catch (SQLException e) {
                showError("An error occurred while deleting the book.", e);
            }
        }
    }
    
    private void updateBookStatus(int bookId, String newStatus) {
        try {
            db.updateBookStatusForUser(currentUser.getUserId(), bookId, newStatus);
        } catch (SQLException e) {
            showError("An error occurred while updating the status. Refresh the page.", e);
            loadMyBooks(); 
        }
    }

    private void openBookForm() {
        BookForm bookForm = new BookForm(this, db);
        bookForm.setVisible(true);
    }
    
  

    private void applyAllBooksFilter() {
        String genre = (String) allBooksGenreFilter.getSelectedItem();
        RowFilter<DefaultTableModel, Object> genreFilter;

        if (genre != null && !genre.equals("All")) {
            
            genreFilter = RowFilter.regexFilter(genre, 3);
        } else {
            genreFilter = null; 
        }
        allBooksSorter.setRowFilter(genreFilter);
    }

    private void applyMyBooksFilter() {
        List<RowFilter<Object, Object>> filters = new ArrayList<>();
        
        String genre = (String) myBooksGenreFilter.getSelectedItem();
        if (genre != null && !genre.equals("All")) {
            filters.add(RowFilter.regexFilter(genre, 3)); 
        }
        
        BookStatus status = (BookStatus) myBooksStatusFilter.getSelectedItem();
         if (status != null) {
            filters.add(RowFilter.regexFilter(status.toString(), 4)); 
        }
        
        RowFilter<Object, Object> compoundFilter = RowFilter.andFilter(filters);
        myBooksSorter.setRowFilter(compoundFilter);
    }
    
    private void showError(String message, Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, message + "\nDetay: " + e.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
    }
    
   
    class StatusCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            String status = value.toString(); 
            if (status != null) {
                if (status.equals(BookStatus.READ.toString())) {
                    c.setBackground(new Color(200, 255, 200));
                } else if (status.equals(BookStatus.READING.toString())) {
                    c.setBackground(new Color(255, 255, 200));
                } else if (status.equals(BookStatus.UNREAD.toString())) {
                    c.setBackground(new Color(200, 220, 255));
                } else {
                    c.setBackground(table.getBackground());
                }
            }
            if (isSelected) {
                c.setBackground(table.getSelectionBackground());
                c.setForeground(table.getSelectionForeground());
            } else {
                c.setForeground(table.getForeground());
            }
            return c;
        }
    }
}