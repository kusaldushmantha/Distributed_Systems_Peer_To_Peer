package springshell.configs;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.shell.plugin.support.DefaultPromptProvider;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.XmlType;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AppPromptProvider extends DefaultPromptProvider {

    public String getPrompt() {
        return "$ ";
    }

    public String getProviderName() {
        return "DsApp Prompt";
    }
}
