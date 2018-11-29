package springboot;

import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.springframework.context.annotation.Bean;
import org.springframework.shell.jline.PromptProvider;
import org.springframework.stereotype.Component;

import static org.jline.utils.AttributedStyle.GREEN;
import static org.jline.utils.AttributedStyle.YELLOW;

@Component
public class ShellConfig implements PromptProvider {

    private static boolean hold=false;

    @Override
    public AttributedString getPrompt() {
        if (!hold) {
            return new AttributedString("> ",
                    AttributedStyle.DEFAULT.foreground(AttributedStyle.GREEN));
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
        return () -> new AttributedString("> ",   AttributedStyle.DEFAULT.foreground(GREEN));
    }


}
