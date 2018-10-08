package springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.embedded.tomcat.ConnectorStartFailedException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.EventListener;
import org.springframework.shell.CommandLine;
import org.springframework.shell.SimpleShellCommandLineOptions;
import org.springframework.shell.core.ExitShellRequest;
import org.springframework.shell.core.JLineShellComponent;
import org.springframework.util.StopWatch;
import udpclient.Client;
import udpclient.Printer;

import java.net.BindException;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Scanner;
import static udpclient.Client.*;

import static udpclient.Printer.*;

@SpringBootApplication
@ComponentScan(
        basePackages={"org.springframework.shell"
                , "springshell"
                , "springboot"
                , "springboot.rest"
                , "udpclient"
        })
public class SpringBootWithShellApplication {

    private static ApplicationContext ctx;

    private static StopWatch sw = new StopWatch("Spring Shell");

    public static int tomcatPort=8080;

    public static void main(String[] args) throws Exception{

        printName();
        Client.initiateClient(false);

        sw.start();
        try {

            getTomCatPort();
            HashMap<String, Object> props = new HashMap<>();
            props.put("server.port", tomcatPort);

            System.out.print("\tStarting REST on port "+tomcatPort+" ... ");
            ctx = new SpringApplicationBuilder()
                    .sources(SpringBootWithShellApplication.class)
                    .properties(props)
                    .run(args);

//            ctx = SpringApplication.run(SpringBootWithShellApplication.class);
        }catch (ConnectorStartFailedException e){
            print_ng("port 8080 is using by a process. Kill it firt and try again");
            System.exit(0);
        }

        print_nn("\n\t"+"IP : " + myIp + " \tUsername : " +myUserName +"\n"
                + "\tUDP Port : " +myPort
                + " \tREST port : " +tomcatPort, "\033[0;1m");

        SpringBootWithShellApplication application = new SpringBootWithShellApplication();
        application.runShell(args);
    }

    @EventListener(ApplicationReadyEvent.class)
	public void doSomethingAfterStartup() {
        print_n(" ✔","\033[1;32m");
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


    private static void getTomCatPort(){
        Scanner scanner=new Scanner(System.in);
        print("\tEnter REST port\t\t[" + tomcatPort + "]\t: ");
        String inPort=scanner.nextLine();
        if (inPort.equals("")) {
            //use default port
        }else {
            while (true){
                try {
                    tomcatPort = Integer.parseInt(inPort);
                    break;
                }catch (NumberFormatException e){
                    print_ng("Wrong input for port");
                }
            }
        }
    }
}

