package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import util.User;
import util.Book;
import util.Author;

public class DBConnection {

	public DBConnection() {
		// TODO Auto-generated constructor stub
	}
	
	public Connection getConnected() throws SQLException{
		return DriverManager.getConnection("jdbc:mysql://localhost:3306/booklist", "root", "***");
	}
	/*
	public ArrayList<User> getUser() throws SQLException{
		ArrayList<User> user = new ArrayList<>();
		Statement st = getConnected().createStatement();
		ResultSet rs = st.executeQuery("SELECT * FROM user");
		
		while (rs.next()) {
			User us = new User();
			us.setUserId(rs.getInt(1));
			us.setName(rs.getString(2));
			us.setSurname(rs.getString(3));
			us.setAge(rs.getInt(4));
			user.add(us);
		}
		return user;
	}*/
	
	public ArrayList<User> getUser() throws SQLException {
	    ArrayList<User> userList = new ArrayList<>();
	    // try-with-resources kullanarak bağlantıyı ve statement'ı otomatik kapatın. Bu çok önemli!
	    try (Connection conn = getConnected();
	         Statement st = conn.createStatement();
	         ResultSet rs = st.executeQuery("SELECT * FROM user")) {

	        while (rs.next()) {
	            User us = new User();
	            // Sütun isimlerini kullanmak daha güvenilirdir.
	            us.setUserId(rs.getInt("userId"));
	            us.setName(rs.getString("name"));
	            us.setSurname(rs.getString("surname"));
	            us.setAge(rs.getInt("age"));
	            us.setUsername(rs.getString("username")); 
	            us.setPassword(rs.getString("password"));
	            userList.add(us);
	        }
	    } 
	    return userList;
	}
	
	public User getUserByUsernameAndPassword(String username, String password) throws SQLException {
	    String query = "SELECT * FROM user WHERE username = ? AND password = ?";
	   
	    try (Connection conn = getConnected();
	         PreparedStatement ps = conn.prepareStatement(query)) {
	        
	        ps.setString(1, username);
	        ps.setString(2, password); 
	        
	        try (ResultSet rs = ps.executeQuery()) {
	            if (rs.next()) { 
	                User user = new User();
	                user.setUserId(rs.getInt("userId"));
	                user.setName(rs.getString("name"));
	                user.setSurname(rs.getString("surname"));
	                user.setAge(rs.getInt("age"));
	                user.setUsername(rs.getString("username"));
	                user.setPassword(rs.getString("password"));
	                return user; 
	            }
	        }
	    }
	    return null; 
	}
	
	public void saveUser(User user) throws SQLException {
	    String query = "INSERT INTO user VALUES (null, ?, ?, ?, ?, ?)";
	    PreparedStatement ps = getConnected().prepareStatement(query);
	    ps.setString(1, user.getName());
	    ps.setString(2, user.getSurname());
	    ps.setInt(3, user.getAge());
	    ps.setString(4, user.getUsername());
	    ps.setString(5, user.getPassword()); // Güvenlik için hash'lemeni öneririm
	    ps.executeUpdate();
	}
	
	public boolean checkUser(String username, String password) throws SQLException {
		
		String query = "select 1 from user where username=? and password=?";
		PreparedStatement ps = getConnected().prepareStatement(query);
		ps.setString(1, username);
		ps.setString(2, password);
	
		ResultSet rs = ps.executeQuery();
		
		return rs.next();
}

	
	public void deleteUser(User user) throws SQLException{
		String query = "delete from user where userId = ?";
		PreparedStatement ps = getConnected().prepareStatement(query);
		
		ps.setInt(1,user.getUserId());
		ps.executeUpdate();
	}
	
	public ArrayList<Author> getAuthor() throws SQLException{
		ArrayList<Author> author = new ArrayList<>();
		Statement st = getConnected().createStatement();
		ResultSet rs = st.executeQuery("SELECT * FROM author");
		
		while(rs.next()) {
			Author au = new Author();
			au.setAuthId(rs.getInt(1));
			au.setAutName(rs.getString(2));
			au.setAutSurname(rs.getString(3));
			au.setAutAge(rs.getInt(4));
			author.add(au);
		}
		return author;
	}
	
	public void saveAuthor(Author author) throws SQLException{
		String query = "insert into author values(null, ?,?,?)";
		PreparedStatement ps = getConnected().prepareStatement(query);
		
		ps.setString(1,author.getAutName());
		ps.setString(2, author.getAutSurname());
		ps.setInt(3, author.getAutAge());
		ps.executeUpdate();
	}
	
	public void deleteAuthor(Author author) throws SQLException{
		String query = "delete from author where authorId = ?";
		PreparedStatement ps = getConnected().prepareStatement(query);
		
		ps.setInt(1, author.getAuthId());
		ps.executeUpdate();
	}
	
	public ArrayList<Book> getBook() throws SQLException{
		ArrayList<Book> book = new ArrayList<>();
		Statement st = getConnected().createStatement();
		ResultSet rs = st.executeQuery("SELECT * FROM book");
		
		while (rs.next()) {
			Book bk = new Book();
			bk.setBookId(rs.getInt(1));
			bk.setBookName(rs.getString(2));
			bk.setAuthId(rs.getInt(3));;
			bk.setBookGenre(rs.getString(4));
			bk.setPublication(rs.getInt(5));
			book.add(bk);
		}
		return book;
	}
	
	public void saveBook(Book book) throws SQLException{
		String query = "insert into book values(null, ?,?,?)";
		PreparedStatement ps = getConnected().prepareStatement(query);
		
		ps.setString(1,book.getBookName());
		ps.setInt(2, book.getAuthId());  
		ps.setString(3, book.getBookGenre());
		ps.setInt(4, book.getPublication());
		ps.executeUpdate();
	}
	
	public void updateBook(int bookId, String bookGenre) throws SQLException {
		String query = "update book set bookGenre=? where bookId=?";
		PreparedStatement ps = getConnected().prepareStatement(query);
		ps.setString(1, bookGenre);
		ps.setInt(2, bookId);
		ps.executeUpdate();
	}
	
	public void deleteBook(Book book) throws SQLException{
		String query = "delete from book where bookId = ?";
		PreparedStatement ps = getConnected().prepareStatement(query);
		
		ps.setInt(1, book.getBookId());
		ps.executeUpdate();
	}
	
	public int getLastInsertedBookId() throws SQLException {
	    String query = "SELECT LAST_INSERT_ID()";
	    Statement st = getConnected().createStatement();
	    ResultSet rs = st.executeQuery(query);
	    if (rs.next()) {
	        return rs.getInt(1);
	    }
	    return -1;
	}


	public void linkUserBook(int userId, int bookId) throws SQLException {
	    String query = "INSERT INTO user_books (user_id, book_id) VALUES (?, ?)";
	    PreparedStatement ps = getConnected().prepareStatement(query);
	    ps.setInt(1, userId);
	    ps.setInt(2, bookId);
	    ps.executeUpdate();
	}
	
	public ArrayList<Book> getBooksWithAuthorInfo() throws SQLException {
	    ArrayList<Book> bookList = new ArrayList<>();
	    String query = "SELECT b.bookId, b.bookName, b.bookGenre, b.publication, " +
	                   "a.authId, a.authName, a.authSurname, a.autAge " +
	                   "FROM book b INNER JOIN author a ON b.authorId = a.authId";

	    Statement st = getConnected().createStatement();
	    ResultSet rs = st.executeQuery(query);

	    while (rs.next()) {
	        Book bk = new Book();
	        bk.setBookId(rs.getInt("bookId"));
	        bk.setBookName(rs.getString("bookName"));
	        bk.setBookGenre(rs.getString("bookGenre"));
	        bk.setPublication(rs.getInt("publication"));

	        
	        Author author = new Author();
	        author.setAuthId(rs.getInt("authId"));
	        author.setAutName(rs.getString("authName"));
	        author.setAutSurname(rs.getString("authSurname"));
	        author.setAutAge(rs.getInt("autAge"));

	        bk.setAuthor(author); 
	        bookList.add(bk);
	    }

	    return bookList;
	}
	
	public void unlinkUserBook(int userId, int bookId) throws SQLException {
	    String query = "DELETE FROM user_books WHERE user_id = ? AND book_id = ?";
	    try (Connection conn = getConnected();
	         PreparedStatement ps = conn.prepareStatement(query)) {
	        ps.setInt(1, userId);
	        ps.setInt(2, bookId);
	        ps.executeUpdate();
	    }
	}
	
	
	public ArrayList<Book> getBooksForUser(int userId) throws SQLException {
	    ArrayList<Book> bookList = new ArrayList<>();
	    String query = "SELECT b.*, a.*, ub.status " +
	                   "FROM book b " +
	                   "JOIN author a ON b.authorId = a.authorId " + 
	                   "JOIN user_books ub ON b.bookId = ub.book_id " +
	                   "WHERE ub.user_id = ?";

	    try (Connection conn = getConnected(); PreparedStatement ps = conn.prepareStatement(query)) {
	        ps.setInt(1, userId);
	        try (ResultSet rs = ps.executeQuery()) {
	            while (rs.next()) {
	                Book bk = new Book();
	                bk.setBookId(rs.getInt("bookId"));
	                bk.setBookName(rs.getString("bookName"));
	                bk.setBookGenre(rs.getString("bookGenre"));
	                bk.setPublication(rs.getInt("publication"));

	                Author author = new Author();
	                // DÜZELTME: 'authId' yerine 'authorId' kullanıldı.
	                author.setAuthId(rs.getInt("authorId"));
	                author.setAutName(rs.getString("autName"));
	                author.setAutSurname(rs.getString("autSurname"));
	                bk.setAuthor(author);

	                bk.setStatus(rs.getString("status"));
	                bookList.add(bk);
	            }
	        }
	    }
	    return bookList;
	}

	// DBConnection.java içinde bu metodu da bulun ve değiştirin.
	public ArrayList<Book> getAllBooksWithAuthor() throws SQLException {
	    ArrayList<Book> bookList = new ArrayList<>();
	   
	    String query = "SELECT b.*, a.* FROM book b JOIN author a ON b.authorId = a.authorId"; // <-- DÜZELTİLDİ
	    
	    try (Connection conn = getConnected();
	         Statement st = conn.createStatement();
	         ResultSet rs = st.executeQuery(query)) {
	        while (rs.next()) {
	            Book bk = new Book();
	            bk.setBookId(rs.getInt("bookId"));
	            bk.setBookName(rs.getString("bookName"));
	            bk.setBookGenre(rs.getString("bookGenre"));
	            bk.setPublication(rs.getInt("publication"));

	            Author author = new Author();
	            // DÜZELTME: 'authId' yerine 'authorId' kullanıldı.
	            author.setAuthId(rs.getInt("authorId"));
	            author.setAutName(rs.getString("autName"));
	            author.setAutSurname(rs.getString("autSurname"));
	            bk.setAuthor(author);
	            bookList.add(bk);
	        }
	    }
	    return bookList;
	}

	// YENİ METOT: Kullanıcının kitap durumunu günceller.
	public void updateBookStatusForUser(int userId, int bookId, String newStatus) throws SQLException {
	    String query = "UPDATE user_books SET status = ? WHERE user_id = ? AND book_id = ?";
	    try (Connection conn = getConnected(); PreparedStatement ps = conn.prepareStatement(query)) {
	        ps.setString(1, newStatus);
	        ps.setInt(2, userId);
	        ps.setInt(3, bookId);
	        ps.executeUpdate();
	    }
	}

	// linkUserBook metodunu değiştirmeye gerek yok, çünkü veritabanı varsayılan olarak 'Okunacak' atayacak.

}
