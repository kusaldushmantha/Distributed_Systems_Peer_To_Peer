package springshell.configs;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.shell.plugin.support.DefaultHistoryFileNameProvider;
import org.springframework.stereotype.Component;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AppHistoryFileNameProvider extends DefaultHistoryFileNameProvider {

    public String getHistoryFileName() {
        return "dsapp-shell.log";
    }

    public String getProviderName() {
        return "DsApp History";
    }
}
