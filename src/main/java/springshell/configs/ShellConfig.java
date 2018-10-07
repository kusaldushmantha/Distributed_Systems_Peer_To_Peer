package springshell.configs;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.shell.CommandLine;
import org.springframework.shell.core.JLineShellComponent;
import org.springframework.shell.plugin.support.DefaultBannerProvider;
import org.springframework.shell.plugin.support.DefaultHistoryFileNameProvider;
import org.springframework.shell.plugin.support.DefaultPromptProvider;

@Configuration
public class ShellConfig {

    @Bean("shell")
    public JLineShellComponent jLineShellComponet(){
        return new JLineShellComponent();
    }

    @Bean
    public CommandLine commandLine(){
        return new CommandLine(null, 3000, null);
    }


//    @Bean
//    public DefaultBannerProvider getBannerProvider(){
//        return new DefaultBannerProvider();
//    }
//    @Bean
//    public DefaultHistoryFileNameProvider getHistoryFileNameProvider(){
//        return new DefaultHistoryFileNameProvider();
//    }
//    @Bean
//    public DefaultPromptProvider getPromptProvider(){
//        return new DefaultPromptProvider();
//    }


}
