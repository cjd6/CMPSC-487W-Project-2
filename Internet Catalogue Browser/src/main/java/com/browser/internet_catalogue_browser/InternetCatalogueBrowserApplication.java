// https://www.jetbrains.com/help/idea/your-first-spring-application.html#what-next
// https://www.w3schools.com/howto/howto_css_table_center.asp
// https://stackoverflow.com/questions/6047943/adding-input-prompts-to-html-form-fields
// https://www.w3schools.com/tags/att_input_type_checkbox.asp
// https://aileenrae.co.uk/blog/programatically-check-uncheck-checkbox-javascript/

package com.browser.internet_catalogue_browser;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class InternetCatalogueBrowserApplication {
    public static DBManager manager;

    public static void main(String[] args) {
        manager = new DBManager();

        SpringApplication.run(InternetCatalogueBrowserApplication.class, args);
    }
}
