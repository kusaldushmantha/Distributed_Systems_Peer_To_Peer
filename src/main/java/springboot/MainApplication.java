package springboot;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.embedded.tomcat.ConnectorStartFailedException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import clientFiles.Client;

import java.util.HashMap;
import java.util.Scanner;

import static clientFiles.Client.*;
import static clientFiles.Util.isPortAvailable;

@SpringBootApplication
@ComponentScan(
        basePackages={"springshell"
                , "springboot"
                , "springboot.rest"
                , "clientFiles"
        })
public class MainApplication {

    public static ApplicationContext ctx;


    public static int tomcatPort=8080;

    public static void titlePrinter(){
        StringBuffer buffer = new StringBuffer();
        buffer.append("--------------------------------");
        buffer.append("\n Distributed System - Client")
                .append("\n");
        buffer.append("--------------------------------");
        System.out.println( buffer.toString());
    }


    public static void main(String[] args) throws Exception{

        titlePrinter();
        Client.startClient(false);

        getTomCatPort();
        try {

            HashMap<String, Object> props = new HashMap<>();
            props.put("server.port", tomcatPort);

            System.out.println("\nIP: "+myIp);
            System.out.println("Starting Client services... ");
            ctx = new SpringApplicationBuilder()
                    .sources(MainApplication.class)
                    .properties(props)
                    .run(args);

        }catch (ConnectorStartFailedException e){
            System.out.println("ERROR ==> Port is already in use!");
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
            System.out.print("Enter REST port (default: " + defaultPort + "): ");
            String inPort=scanner.nextLine();
            if (inPort.equals("")) {
                if (isPortAvailable(defaultPort)){
                    tomcatPort=defaultPort;
                    break;
                }else {
                    System.out.println("ERROR ==> Permission denied. Use a different port!");
                    defaultPort++;
                }
            }else {
                try {
                    tomcatPort = Integer.parseInt(inPort);
                    if (isPortAvailable(tomcatPort)){
                        break;
                    }else {
                        System.out.println("ERROR ==> Permission denied. Use a different port!");
                    }
                }catch (NumberFormatException e){
                    System.out.println("ERROR ==> Please enter a valid port number");
                }
            }
        }
    }
}

