package springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.shell.CommandLine;
import org.springframework.shell.SimpleShellCommandLineOptions;
import org.springframework.shell.core.ExitShellRequest;
import org.springframework.shell.core.JLineShellComponent;
import org.springframework.util.StopWatch;
import udpclient.Client;

@SpringBootApplication
@ComponentScan(
        basePackages={"org.springframework.shell"
                , "org.springframework.shell.converters"
                , "springshell"
                , "springboot"
                , "springboot.rest"
                , "udpclient"
        })
public class SpringBootWithShellApplication {

    private static ApplicationContext ctx;

    private static StopWatch sw = new StopWatch("Spring Shell");

    public static void main(String[] args) throws Exception{
        System.out.println("Starting application...");

        Client.initiateClient(false);

        System.out.print("Wait for starting REST...\t");
        sw.start();
        ctx = SpringApplication.run(SpringBootWithShellApplication.class);
        System.out.println("REST started...");

        SpringBootWithShellApplication application = new SpringBootWithShellApplication();
        application.runShell(args);
    }

    private ExitShellRequest runShell() {
        JLineShellComponent shell = ctx.getBean(JLineShellComponent.class);
        ExitShellRequest exitShellRequest;

        shell.start();
        shell.promptLoop();
        exitShellRequest = shell.getExitShellRequest();
        shell.waitForComplete();

        return exitShellRequest;
    }

    private ExitShellRequest runShell(String[] args) throws Exception{

        CommandLine commandLine = SimpleShellCommandLineOptions.parseCommandLine(args);
        String[] commandsToExecuteAndThenQuit = commandLine.getShellCommandsToExecute();
        // The shell is used
        JLineShellComponent shell = ctx.getBean(JLineShellComponent.class);
        ExitShellRequest exitShellRequest;

        if (null != commandsToExecuteAndThenQuit) {
            boolean successful = false;
            exitShellRequest = ExitShellRequest.FATAL_EXIT;

            for (String cmd : commandsToExecuteAndThenQuit) {
                successful = shell.executeCommand(cmd).isSuccess();
                if (!successful)
                    break;
            }

            // if all commands were successful, set the normal exit status
            if (successful) {
                exitShellRequest = ExitShellRequest.NORMAL_EXIT;
            }
        } else {
            shell.start();
            shell.promptLoop();
            //shell.run();
            exitShellRequest = shell.getExitShellRequest();
            if (exitShellRequest == null) {
                // shouldn't really happen, but we'll fallback to this anyway
                exitShellRequest = ExitShellRequest.NORMAL_EXIT;
            }
            shell.waitForComplete();
        }

        ((ConfigurableApplicationContext) ctx).close();
        sw.stop();
        if (shell.isDevelopmentMode()) {
            System.out.println("Total execution time: " + sw.getLastTaskTimeMillis() + "ms");
        }
        return exitShellRequest;
    }
}

