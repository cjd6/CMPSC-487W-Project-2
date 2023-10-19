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
            s.append("select * from items where (description like ? or name like ?) ");

            if (!id.equals("")){
                try {
                    int idInt = Integer.parseInt(id);
                    s.append("and itemID = ");
                    s.append(idInt);
                    s.append(" ");
                } catch (Exception ignored) {
                    return null;
                }
            }

            if (orderByID){
                s.append("order by itemID;");
            } else {
                s.append("order by name;");
            }

            PreparedStatement ps = connection.prepareStatement(s.toString());

            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");

            return ps.executeQuery();
        } catch (Exception ignored){
            return null;
        }
    }
}
