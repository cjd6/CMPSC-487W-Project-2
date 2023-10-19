// https://www.tutorialspoint.com/sqlite/sqlite_using_autoincrement.htm
// https://www.securityjourney.com/post/how-to-prevent-sql-injection-vulnerabilities-how-prepared-statements-work#:~:text=A%20prepared%20statement%20is%20a,safely%2C%20preventing%20SQL%20Injection%20vulnerabilities.

package com.browser.internet_catalogue_browser;

import java.sql.*;

public class DBManager {
    final String DATABASE_URL = "jdbc:sqlite:identifier.sqlite";
    static Connection connection;

    public DBManager(){
        establishConnection();
    }

    public void establishConnection(){
        try {
            connection = DriverManager.getConnection(DATABASE_URL);
        } catch (Exception e){
            System.err.println("Error, unable to connect to database.");
            System.exit(1);
        }
    }

    public void insertItem(String name, String desc, String url){
        try{
            PreparedStatement ps = connection.prepareStatement(
            "insert into items(name, description, imageURL) values (?, ?, ?);"
            );
            ps.setString(1, name);
            ps.setString(2, desc);
            ps.setString(3, url);
            ps.execute();
        } catch (Exception ignored){}
    }

    public void deleteItem(int id){
        try{
            PreparedStatement ps = connection.prepareStatement(
                    "delete from items where itemID = ?;"
            );
            ps.setInt(1, id);
            ps.execute();
        } catch (Exception ignored){}
    }

    public void updateItem(int id, String name, String desc, String url){
        try{
            PreparedStatement ps = connection.prepareStatement(
                    "update items set name = ?, description = ?, imageURL = ? where itemID = ?;"
            );
            ps.setString(1, name);
            ps.setString(2, desc);
            ps.setString(3, url);
            ps.setInt(4, id);
            ps.execute();
        } catch (Exception ignored){}
    }

    public ResultSet queryData(String id, String keyword, boolean orderByID){
        try {

            StringBuilder s = new StringBuilder();
            s.append("select * from items ");

            int numParams = 0;

            if (!(id.equals("") && keyword.equals(""))){
                s.append("where ");
                int idInt;
                try {
                    idInt = Integer.parseInt(id);
                    s.append("itemID = ");
                    s.append(idInt);
                    s.append(" ");

                    if (!keyword.equals("")){
                        s.append("and ");
                    }

                } catch (Exception ignored){}

                if (!keyword.equals("")){
                    s.append("description like ? or name like ? ");
                    numParams += 2;
                }
            }

            s.append("order by ?;");

            PreparedStatement ps = connection.prepareStatement(s.toString());

            if (numParams == 2){
                ps.setString(1, "%" + keyword + "%");
                ps.setString(2, "%" + keyword + "%");
            }


            if(orderByID){
                ps.setString(numParams + 1, "itemID");
            } else {
                ps.setString(numParams + 1, "name");
            }

            return ps.executeQuery();
        } catch (Exception ignored){
            return null;
        }
    }
}
