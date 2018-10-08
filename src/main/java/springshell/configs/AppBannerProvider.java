package springshell.configs;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.shell.plugin.support.DefaultBannerProvider;
import org.springframework.shell.support.util.OsUtils;
import org.springframework.stereotype.Component;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AppBannerProvider extends DefaultBannerProvider {

    public String getBanner() {
        return "";
    }

    public String getVersion() {
        return "1.0";
    }

    public String getWelcomeMessage() {
        return "\t\tEnter 'apphelp' to get application commands\n"
                + "\t\tEnter 'help' to get all shell commands\n";
    }

    public String getProviderName() {
        return "Distributed Client";
    }
}
