package springboot;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.embedded.tomcat.ConnectorStartFailedException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import udpclient.Client;

import java.util.HashMap;
import java.util.Scanner;

import static udpclient.Client.*;
import static udpclient.Printer.*;
import static udpclient.Util.isPortAvailable;

@SpringBootApplication
@ComponentScan(
        basePackages={"springshell"
                , "springboot"
                , "springboot.rest"
                , "udpclient"
        })
public class SpringBootWithShellApplication {

    public static ApplicationContext ctx;


    public static int tomcatPort=8080;

    public static void main(String[] args) throws Exception{

        printName();
        Client.initiateClient(false);

        getTomCatPort();
        print_nn("\n\t\t"+"IP : " + myIp + " \tUsername : " +myUserName +"\n"
                + "\t\tUDP Port : " +myPort
                + " \tREST port : " +tomcatPort, "\033[0;1m");
        try {

            HashMap<String, Object> props = new HashMap<>();
            props.put("server.port", tomcatPort);

            System.out.println("\t\tStarting REST on port "+tomcatPort+" ... ");
            ctx = new SpringApplicationBuilder()
                    .sources(SpringBootWithShellApplication.class)
                    .properties(props)
                    .run(args);

//            ctx = SpringApplication.run(SpringBootWithShellApplication.class);
        }catch (ConnectorStartFailedException e){
            print_ng("Port is used by another process");
            System.exit(0);
        }



    }


    private static void getTomCatPort(){

        int defaultPort=tomcatPort;
        while (!isPortAvailable(defaultPort)){
            defaultPort=tomcatPort++;
        }

        Scanner scanner=new Scanner(System.in);
        while (true){
            print("\t\tEnter REST port\t\t[" + defaultPort + "]\t: ");
            String inPort=scanner.nextLine();
            if (inPort.equals("")) {
                if (isPortAvailable(defaultPort)){
                    tomcatPort=defaultPort;
                    break;
                }else {
                    print_ng("\t\tPermission denied. Use a different port");
                    defaultPort++;
                }
            }else {
                try {
                    tomcatPort = Integer.parseInt(inPort);
                    if (isPortAvailable(tomcatPort)){
                        break;
                    }else {
                        print_ng("\t\tPermission denied. Use a different port");
                    }
                }catch (NumberFormatException e){
                    print_ng("\t\tWrong input for port");
                }
            }
        }
    }
}

