package springshell;

import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.shell.jline.InteractiveShellApplicationRunner;
import org.springframework.shell.jline.PromptProvider;
import org.springframework.stereotype.Component;

import static org.jline.utils.AttributedStyle.YELLOW;

@Component
public class ShellConfig implements PromptProvider {

    private static boolean hold=false;

    @Override
    public AttributedString getPrompt() {
        if (!hold) {
            return new AttributedString("Client~$ ",
                    AttributedStyle.DEFAULT.foreground(AttributedStyle.YELLOW));
        }else {
            return new AttributedString("");
        }
    }

    public static void holdShell(){
        hold=true;
    }

    public static void getShell(){
        hold=false;
    }


    //shell prompt
    @Bean
    public PromptProvider promptProvider() {
        return () -> new AttributedString("Client~$ ",   AttributedStyle.DEFAULT.foreground(YELLOW));
    }


}
