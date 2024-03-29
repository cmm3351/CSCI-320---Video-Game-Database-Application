import java.sql.*;
import java.util.*;
public class VideoGames {
    Statement stmt;
    Scanner scanner;

    private int getInput(String ques){
        System.out.print(ques);
        int option = scanner.nextInt();
        return option;
    } 
    public int VideoGameMenu(Statement stmt,  Scanner scanner, String currentUID){
        try{
            int inp = -1;
            this.stmt = stmt;
            boolean cont = true;
            this.scanner = scanner;
            
            while(cont){
                System.out.println("""
                    \n--Select action to continue--
                        1. Search for Game  
                        2. Edit Sorting Prioriy 
                        3. View collections
                        4. Return to main menu
                    """);
                inp = getInput("Choose an option: ");
                switch(inp){
                    case 1: inp = PlayerSearchView();
                            if(inp ==1){return 0;}
                            break;
                    case 2: inp = PlayerSortView(); break;
                    case 3: return 3;
                    case 4: return 0;
                }
            }
        }catch(Exception e){
            System.out.println("THe following ERROR occured in VideoGame -->"+e);
            return 2;
        }
        return 0;
    }

    public int PlayerSearchView() throws SQLException{
        int inp = -1;
        ResultSet res;
        String where = "";
        String order = "";
        while(inp!=7){
            System.out.println("""
                \n--Select action to continue--
                    1. Search games by Name  
                    2. Search games by ESRB_rating
                    3. Search games by Genre
                    4. Search games by Platform
                    5. Search games by Release Date
                    6. Search games by Developers
                    7. Search games by Price
                    8. Return to VideoGame Menu
                    9. Return to Main Menu
                 """);
            inp = getInput("Choose an option: ");
            switch(inp){
                case 1:
                    System.out.print("Enter name of game (partial or full): ");
                    String title = scanner.nextLine();title = scanner.nextLine();
                    where = "WHERE videogame.title LIKE '%"+title+"%'";
                    order = "";
                    System.out.println("The following games match the name : "+ title);
                    DisplayGame(stmt,"", where, order);
                    break;
                case 2:
                    System.out.print("Enter Rating to seach by( E/E10+/T/M/AO): ");
                    String rat = scanner.nextLine();rat = scanner.nextLine();
                    where = "WHERE videogame.esrb_rating = '"+rat+"'";
                    order = "";
                    System.out.println("The following are rated : "+ rat);
                    DisplayGame(stmt, "",where, order);
                    break;
                case 3:
                    System.out.print("Enter Genre Name to search by: ");
                    String genre = scanner.nextLine();genre = scanner.nextLine();
                    where = "WHERE genre.name LIKE '%"+genre+"%'";
                    order = "";
                    String additional_join = """
                            LEFT JOIN video_game_genre ON videogame.vgid = video_game_genre.vgid
                            LEFT JOIN genre ON genre.gid = video_game_genre.gid
                            """;
                    System.out.println("The following games have the genre matching: "+ genre);
                    DisplayGame(stmt, additional_join,where, order);
                    break;
                case 4:
                    System.out.print("Enter Platform to seach for: ");
                    String plat = scanner.nextLine();plat = scanner.nextLine();
                    where = "WHERE platforms.name ='"+plat+"'";
                    order = "";
                    System.out.println("The following are playable on the platform : "+ plat );
                    DisplayGame(stmt, "",where, order);
                    break;
                case 5:                    
                    System.out.print("Enter year of release: ");int year = scanner.nextInt();
                    System.out.print("Enter month of release: ");int month = scanner.nextInt();
                    System.out.print("Enter date of release: ");int date = scanner.nextInt();
                    String dateVal=dateFormat(year, month, date);

                    System.out.println("""
                        \n--Select Date Filteration Type--
                            1. Released BEFORE Date
                            2. Released AFTER  Date
                            3. Released  ON    Date
                        \nFilteration Type: """);
                    int choi = scanner.nextInt();
                    where = "WHERE release.release_date ='"+dateVal+"'";
                    switch (choi) {
                        case 1:
                            where = "WHERE release.release_date <='"+dateVal+"'";
                            break;
                        case 2:
                            where = "WHERE release.release_date >='"+dateVal+"'";
                            break;
                    }
                    order = "";
                    System.out.println("The following are filtered games : ");
                    DisplayGame(stmt, "",where, order);
                    break;
                case 6:
                    System.out.print("Enter Develepor name to seach by: ");
                    String dev = scanner.nextLine();dev = scanner.nextLine();
                    if(dev.length()==0)dev = "ScipityScapady";
                    where = "WHERE devpub.name ='%"+dev+"%'";
                    order = "";
                    System.out.println("The following games are made by : "+ dev);
                    DisplayGame(stmt, "",where, order);
                    break;
                case 7:
                    System.out.print("Enter price: ");float price = scanner.nextFloat();
                    
                    System.out.println("""
                        \n--Select Date Filteration Type--
                            1. Price is less than (inclusive)
                            2. Price is greater than (inclusive)
                            3. Price is exactly
                        \nFilteration Type: """);
                    choi = scanner.nextInt();
                    where = "WHERE release.curr_price ="+price;
                    switch (choi) {
                        case 1:
                            where = "WHERE 2release.curr_price <='"+price+"'";
                            break;
                        case 2:
                            where = "WHERE release.curr_price >='"+price+"'";
                            break;
                    }
                    order = "";
                    System.out.println("The following are filtered games : ");
                    DisplayGame(stmt, "",where, order);
                    break;
                case 8:
                    return 0;
                case 9:
                    return 1;
            }
        }
        return 0;
    }


    //Helps stop my eyes from bleeding
    private String dateFormat(int y, int m, int d){
        String retVal = "";
        String ys = Integer.toString(y);
        String ms = Integer.toString(m);
        String ds = Integer.toString(d);
        if(ys.length()<2){ys="0"+ys;}
        if(ms.length()<2){ms="0"+ms;}
        if(ds.length()<2){ds="0"+ds;}
        retVal += ys+ms+ds;
        return retVal+" 00:00:00";
    }
    //Helps stop my eyes from bleeding
    private String none_ify(String in){
        if(in.length()==4)
            return " None ";
        else
            return " "+in;
    }

    //Helps stop my eyes from bleeding
    private String cut_rat(String in, int ad){
        try{
            in = " ("+in.substring(0,4+ad)+")";     
        }catch(Exception e){
            return " (-)";
        }
        return in;
    }
    // Joins tables and queries it to print results based on given conditions
    private void DisplayGame(Statement stmt, String additional_join,String where, String order)throws SQLException{
        ResultSet res = stmt.executeQuery("""
            SELECT title,
                array_agg( distinct concat(platforms.name )) platforms,
                array_agg( distinct concat(devpub.name )) devpubs,
                SUM(session.sessionend - session.sessionstart) playtime,
                AVG(video_game_rating.rating) rating
                FROM videogame

                LEFT JOIN release ON release.vgid = videogame.vgid
                LEFT JOIN platforms ON platforms.pid = release.pid
                LEFT JOIN published ON videogame.vgid = published.vgid
                LEFT JOIN devpub ON published.dpid = devpub.dpid
                LEFT JOIN session ON videogame.vgid = session.vgid
                LEFT JOIN video_game_rating ON videogame.vgid = video_game_rating.vgid
                """
                +additional_join
                +where
                +" GROUP BY title "
                +order+";");
        while(res.next()){
            String pString = "\n\t-->) Title: '" + res.getString("title");
            pString += "'  Platforms:" + none_ify(res.getString("platforms"));
            pString += "  Devs/Pubs:"+ none_ify(res.getString("devpubs"));
            pString += "  Playtime:" + cut_rat(res.getString("playtime"),1);
            pString += "  Rating:" + cut_rat(res.getString("rating"),0);
            System.out.println(pString);
        }
        res.close();
        System.out.println("\n");
    }
    public int PlayerSortView(){
        System.out.println("""
                --Select action to continue--
                    1. Search games by name  
                    2. Search games by rating
                    3. TODO: Search games by genre
                    4. Play Game
                    5. Return to main menu
                 """);

            switch(getInput("")){
                case 1:
             
            }
        return 0;
    }


}
