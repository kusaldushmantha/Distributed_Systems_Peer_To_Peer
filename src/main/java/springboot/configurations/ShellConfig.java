package springboot.configurations;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.shell.CommandLine;
import org.springframework.shell.core.JLineShellComponent;
import org.springframework.shell.plugin.support.DefaultBannerProvider;
import org.springframework.shell.plugin.support.DefaultHistoryFileNameProvider;
import org.springframework.shell.plugin.support.DefaultPromptProvider;

@Configuration
@ComponentScan(
        basePackages={"org.springframework.shell"
                , "org.springframework.shell.converters"
                , "springshell"
                , "springboot"
                , "springboot.rest"
                , "udpclient"
        })

public class ShellConfig {

    //required by
    // org.springframework.shell.converters.AvailableCommandsConverter
    //the bean name is very important, since Bootstrap.java class is initialize this way,
    //and if without the name, you will encounter error like "spring shell No bean named 'shell' available".
    @Bean("shell")
    public JLineShellComponent jLineShellComponet(){
        return new JLineShellComponent();
    }
    //required by
    // org.springframework.shell.core.JLineShellComponent
    @Bean
    public CommandLine commandLine(){
        return new CommandLine(null, 3000, null);
    }

    //there're three classes under org.springframework.shell.plugin
    //you can do component scan if you prefer, but I just list them here in case
    //i need to customize them later on.
    @Bean
    public DefaultBannerProvider getBannerProvider(){
        return new DefaultBannerProvider();
    }
    @Bean
    public DefaultHistoryFileNameProvider getHistoryFileNameProvider(){
        return new DefaultHistoryFileNameProvider();
    }
    @Bean
    public DefaultPromptProvider getPromptProvider(){
        return new DefaultPromptProvider();
    }
}
