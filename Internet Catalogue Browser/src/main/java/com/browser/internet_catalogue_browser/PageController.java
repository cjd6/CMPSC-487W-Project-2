// https://www.jetbrains.com/idea/guide/tutorials/your-first-spring-application/creating-spring-controller/
// https://www.w3schools.com/java/java_files_read.asp
// https://spring.io/guides/gs/handling-form-submission/

package com.browser.internet_catalogue_browser;

import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import java.sql.ResultSet;
import java.sql.SQLException;

@RestController
@RequestMapping("/")
public class PageController {
    @GetMapping("/")
    public String showQueryScreen(){
        String openingText = readFile("src/main/resources/static/QueryPageFirstHalf.txt");;
        String tableText = buildTableString("", "", true);
        String endText = readFile("src/main/resources/static/QueryPageSecondHalf.txt");;

        return openingText + tableText + endText;
    }

    // query the data and update the table with the results
    @RequestMapping("/query")
    public String queryData(@RequestParam String id, @RequestParam String keyword, @RequestParam(required = false) boolean idBox){
        String openingText = readFile("src/main/resources/static/QueryPageFirstHalf.txt");;
        String tableText = buildTableString(id, keyword, idBox);
        String endText = readFile("src/main/resources/static/QueryPageSecondHalf.txt");;

        return openingText + tableText + endText;
    }

    @GetMapping("/showNewItemPage")
    public String showNewItemScreen(){
        return readFile("src/main/resources/static/newItemPage.html");
    }

    // add the item and return to query screen
    @RequestMapping("/addItem")
    public String addItem(@RequestParam String nameField, @RequestParam String descField, @RequestParam String urlField){
        InternetCatalogueBrowserApplication.manager.insertItem(nameField, descField, urlField);
        return showQueryScreen();
    }

    @GetMapping("/findItemPage")
    public String findItemPage(){
        return readFile("src/main/resources/static/findItemPage.html");
    }

    @RequestMapping("/editItemPage")
    public String showEditItemScreen(@RequestParam String id){
        StringBuilder s = new StringBuilder();
        s.append(readFile("src/main/resources/static/editItemFirstHalf.txt"));

        try{
            s.append(buildEditString(id));
        } catch (Exception e){
            return showQueryScreen(); // if there's an error go back to the main screen
        }

        s.append(readFile("src/main/resources/static/editItemSecondHalf.txt"));
        return s.toString();
    }

    // edit the item and return to the query screen
    @RequestMapping("/editItem")
    public String editItem(@RequestParam(required = false) String idField,
                           @RequestParam(required = false) String nameField, @RequestParam(required = false) String descField,
        @RequestParam(required = false) String urlField){
        InternetCatalogueBrowserApplication.manager.updateItem(Integer.parseInt(idField),
                nameField, descField, urlField);
        return showQueryScreen();
    }

    // delete the item and return to the query screen
    @RequestMapping("/deleteItem")
    public String deleteItem(@RequestParam String deleteButtonText){
        int id = Integer.parseInt(deleteButtonText.substring(12));
        InternetCatalogueBrowserApplication.manager.deleteItem(id);
        return showQueryScreen();
    }

    public String buildEditString(String id) throws Exception{

        ResultSet r = InternetCatalogueBrowserApplication.manager.queryData(id, "", true);
        r.next();

        StringBuilder s = new StringBuilder();
        s.append("<form action=\"/editItem\" th:action=\"@{/editItem}\" method=\"get\">");

        s.append("<label for=\"idField\"></label>");
        s.append("<p>Item ID: <input type=\"text\" name=\"idField\" id=\"idField\" th:field=\"*{idField}\" value = \"" +
                r.getObject(1).toString() + "\" readonly></p>");

        s.append("<label for=\"nameField\"></label>");
        s.append("<p>Name: <input type=\"text\" name=\"nameField\" id=\"nameField\" th:field=\"*{nameField}\" value = \"" +
                r.getObject(2).toString() + "\"></p>");

        s.append("<label for=\"descField\"></label>");
        s.append("<p>Description: <input type=\"text\" name=\"descField\" id=\"descField\" th:field=\"*{descField}\" value = \"" +
                r.getObject(3).toString() + "\"></p>");

        s.append("<label for=\"urlField\"></label>");
        s.append("<p>Image URL: <input type=\"text\" name=\"urlField\" id=\"urlField\" th:field=\"*{urlField}\" value = \"" +
                r.getObject(4).toString() + "\"></p>");

        s.append("<p><input type=\"submit\" value=\"Commit Changes\"></p></form>");

        s.append("<form action=\"/deleteItem\" th:action=\"@{/deleteItem}\" method=\"get\"><p>" +
            "<input type=\"submit\" value=\"Delete Item " + id + "\" name=\"deleteButtonText\" id=\"deleteButtonText\"" +
                " th:field=\"*{deleteButtonText}\"></p></form>");

        return s.toString();
    }

    // build the HTML for the table based on the given query
    public String buildTableString(String id, String keyword, boolean sortById){
        StringBuilder tableText = new StringBuilder();
        try {
            ResultSet r = InternetCatalogueBrowserApplication.manager.queryData(id, keyword, sortById);

            if (r == null || !r.next()){
                tableText.append("<p>No results found!</p>");
            } else {
                tableText.append("<table border=\"5px\"><tr class=\"evenrow\"><td width=\"100\">Image</td><td width=\"200\">Name</td><td width=\"200\">Description</td><td>Item ID</td></tr>");
                int i = 1;
                do{
                    if (i % 2 == 0){
                        tableText.append("<tr class=\"evenrow\">");
                    } else {
                        tableText.append("<tr class=\"oddrow\">");
                    }

                    String itemID = r.getObject(1).toString();
                    String name = r.getObject(2).toString();
                    String description = r.getObject(3).toString();
                    String url = r.getObject(4).toString();

                    tableText.append("<td><img src=" + url + "></td>\n");
                    tableText.append("<td>" + name + "</td>");
                    tableText.append("<td>" + description + "</td>");
                    tableText.append("<td>" + itemID + "</td>");

                    i++;
                } while (r.next());
                tableText.append("</table>");
            }
        } catch (SQLException e){
            tableText.append("<p>Error, unable to load table!</p>");
        }
        return tableText.toString();
    }

    // read all the contents of a file and return it as a single string
    public String readFile(String filePath){
        try{
            StringBuilder result = new StringBuilder();

            File inputFile = new File(filePath);
            Scanner s = new Scanner(inputFile);
            while(s.hasNextLine()){
                result.append(s.nextLine());
            }

            s.close();
            return result.toString();
        } catch (FileNotFoundException e){
            System.err.println("Error, unable to read file!");
            return "";
        }
    }

}