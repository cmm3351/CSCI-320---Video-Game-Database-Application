
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.*;
import java.util.Date;

/**
 * This program is used to interface with our database of
 * video games and video game related information
 *
 * @author Group 32 of CSCI 320, Spring 2024
 * @author Sam Cordry, Caelen Naas, Pranav Sehgal,
 *         Bobby Dhanoolal, Cristian Malone
 */
public class Main {

    static String currentUID = null;

    public static void main(String[] args) throws SQLException {

        Connection conn = new Database().getConn();
        System.out.println("Database connection established");

        Statement stmt = conn.createStatement();

        Scanner scanner = new Scanner(System.in);
        boolean loggedIn = false;

        while (!loggedIn) {
            System.out.println("\n1. Create Account");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");
            int option = scanner.nextInt();
            scanner.nextLine(); // Consume newline



            switch (option) {
                case 1:
                    createUser(stmt, scanner);
                    break;
                case 2:
                    loggedIn = loginUser(stmt, scanner);
                    break;
                case 3:
                    System.out.println("Exiting...");
                    conn.close();
                    System.exit(0);
                default:
                    System.out.println("Invalid option.");
            }

        }
        menuAccess(scanner,stmt,conn);
    }

    private static void menuAccess(Scanner scanner, Statement stmt, Connection conn) throws SQLException {
        VideoGames vg = new VideoGames(stmt, scanner, currentUID);
        Friends f = new Friends(stmt,scanner,currentUID);
        Collections c = new Collections(stmt,scanner,currentUID);
        while (true){


            System.out.println("\n--Select Menu To Access--");
            System.out.println("1. Friends");
            System.out.println("2. Video Games");
            System.out.println("3. Collections");
            System.out.println("4. Exit");
            System.out.print("Choose an option: ");
            int option = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (option) {
                case 1:
                    f.friendsMenu();
                    break;
                case 2:
                    int vgr = vg.VideoGameMenu();
                    if(vgr!=3)break;
                case 3:
                    c.collectionsMenu();
                    break;
                case 4:
                    System.out.println("Exiting...");
                    conn.close();
                    System.exit(0);
                default:
                    System.out.println("Invalid option.");
            }
        }

    }

    private static void createUser(Statement stmt, Scanner scanner) throws SQLException {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = BCrypt.hashpw(scanner.nextLine(), BCrypt.gensalt(10));
        System.out.print("Enter first name: ");
        String first_name = scanner.nextLine();
        System.out.print("Enter last name: ");
        String last_name = scanner.nextLine();
        System.out.print("Enter email: ");
        String email = scanner.nextLine();

        String creationDate = new Date().toString();
        String lastAccessDate = new Date().toString();


        String sql = "INSERT INTO users (username, first_name, last_name, creation_date, last_access_date, password) " +
                "VALUES ('" + username + "', '" + first_name + "', '" + last_name + "', '" + creationDate + "', '" + lastAccessDate + "', '" + password + "')";
        stmt.executeUpdate(sql);
        String getUidSql = "SELECT uid FROM users WHERE username='" + username + "'";
        stmt.executeQuery(getUidSql);
        ResultSet rs = stmt.getResultSet();
        if (rs.next()) {
            String emailSql = "INSERT INTO emails (email, uid) VALUES ('" + email + "', '" + rs.getString("uid") + "')";
            stmt.executeUpdate(emailSql);
        }
        System.out.println("Account created successfully.");
        rs.close();
    }

    private static boolean loginUser(Statement stmt, Scanner scanner) throws SQLException {
        boolean loggedIn = false;

        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        String sql = "SELECT * FROM users WHERE username='" + username + "'";
        ResultSet rs = stmt.executeQuery(sql);

        if (rs.next() && BCrypt.checkpw(password,rs.getString("password"))) {
                System.out.println("Login successful. Welcome, " + username + "!");
                currentUID = String.valueOf(rs.getInt("uid"));
                loggedIn = true;

        } else {
            System.out.println("Invalid username or password.");
        }
        rs.close();
        return loggedIn;
    }

}
