package property24;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Viewport;
import com.vaadin.flow.theme.Theme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@Viewport("width=device-width, initial-scale=1") // Move @Viewport here
public class
Property24Application implements AppShellConfigurator { 
    public static void main(String[] args) {
        SpringApplication.run(Property24Application.class, args);
    }
}