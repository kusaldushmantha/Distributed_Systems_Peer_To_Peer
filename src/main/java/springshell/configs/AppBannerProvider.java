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
        StringBuffer buf = new StringBuffer();
        buf.append("\n");
        buf.append("\033[1;34m");
        buf.append("=======================================")
                .append(OsUtils.LINE_SEPARATOR);
        buf.append("*       Distributed Client Cli        *")
                .append(OsUtils.LINE_SEPARATOR);
        buf.append("=======================================")
                .append(OsUtils.LINE_SEPARATOR);
//        buf.append("Version:")
//                .append(this.getVersion());

        buf.append("\033[1;34m");
        return buf.toString();
    }

    public String getVersion() {
        return "1.0";
    }

    public String getWelcomeMessage() {
        return "\n";
    }

    public String getProviderName() {
        return "DsApp Banner";
    }
}
