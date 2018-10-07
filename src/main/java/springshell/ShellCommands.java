package springshell;

import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

@Component
public class ShellCommands implements CommandMarker {

    @CliCommand(value = { "web-get", "wg" })
    public String webGet(@CliOption(key = "url") String url) {
        System.out.println("lololol");
        return url;
    }
}
