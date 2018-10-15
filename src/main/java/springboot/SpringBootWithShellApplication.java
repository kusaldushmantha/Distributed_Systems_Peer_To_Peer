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
            print_ng("port 8080 is using by a process. Kill it firt and try again");
            System.exit(0);
        }



    }


    private static void getTomCatPort(){
        Scanner scanner=new Scanner(System.in);
        print("\t\tEnter REST port\t\t[" + tomcatPort + "]\t: ");
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

