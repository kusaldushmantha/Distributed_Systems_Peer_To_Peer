package springshell;


import FileDownloader.Downloader;
import javafx.application.Application;
import org.apache.commons.io.FileUtils;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.springframework.context.annotation.Bean;
import org.springframework.shell.jline.InteractiveShellApplicationRunner;
import org.springframework.shell.jline.PromptProvider;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.shell.standard.commands.Help;
import org.springframework.shell.standard.commands.Quit;
import org.springframework.stereotype.Component;
import springboot.SpringBootWithShellApplication;
import udpclient.SendingMessageHandler;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import static org.jline.utils.AttributedStyle.YELLOW;
import static udpclient.Printer.*;
import static udpclient.Util.changeMyPort;
import static udpclient.Util.getHelpText;

@ShellComponent
public class ShellCommands implements Quit.Command, Help.Command {

    @ShellMethod(key = "reg",value = "[reg ip] register to Bootstrap server")
    public void register(@ShellOption() String ip){
        SendingMessageHandler.registerToBS(ip);
    }

    @ShellMethod("Say hello.")
    public String greet(@ShellOption(defaultValue="World") String who) {

        return "Hello " + who;
    }

    @ShellMethod(key = "regl", value = "register to Bootstrap server running on same ip")
    public void registerLocalBsServer(){
        SendingMessageHandler.registerToBSonSameIp();
    }

    @ShellMethod(key = "unreg", value = "unregister from Bootstrap server")
    public void unregister(){
        SendingMessageHandler.unregisterFromBS();
    }

    @ShellMethod(key = "table", value = "show routing table")
    public void table(){
        printRoutingTable();
    }

    @ShellMethod(key = "join", value = "join to neighbours in routing table")
    public void join(){
        SendingMessageHandler.joinToSystem();
    }

    @ShellMethod(key = "leave",value = "leave from neighbours")
    public void leave(){
        SendingMessageHandler.leaveTheSystem();
    }

    @ShellMethod(key = "search",value = "[search file_name hops(optional)] search files in network by name")
    public void search(@ShellOption() String name,@ShellOption(defaultValue="1") String hopsStr ){
        int hops=1;
        if (hopsStr!=null){
            try{
                hops=Integer.parseInt(hopsStr);
            }catch (Exception e){
                print_ng("Number format error for hops");
            }
        }
        SendingMessageHandler.searchFile(name,hops);
    }

    @ShellMethod(key = "files", value = "show selected files")
    public void files(){
        printSelectedFiles();
    }

    @ShellMethod(key = "help", value = "app commands (this)")
    public void help(){
        printHelp(getHelpText());
    }

    @ShellMethod(key = "setport", value = "[setport port] change port if registration failed")
    public void setport(@ShellOption() String port){
        changeMyPort(port);
    }

    @ShellMethod(key = {"quit", "exit"}, value = "exit from application followed by 'unreg' and 'leave' ")
    public void quit(){
        SendingMessageHandler.exit();
        System.exit(0);
    }

    @ShellMethod(key="download" , value = "download a file froma given url")
    public void downlaod(@ShellOption("url") String urlStr){

        Downloader.downloadFile(urlStr,"downloaded_files");

    }


}
