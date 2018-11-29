package springboot;


import springboot.rest.FileDownloadHandler;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.shell.standard.commands.Help;
import org.springframework.shell.standard.commands.Quit;
import clientFiles.MessegeHandler;

import static clientFiles.Util.*;

@ShellComponent
public class CommandHandler implements Quit.Command, Help.Command {

    @ShellMethod(key = "reg",value = "[reg ip] register to Bootstrap server")
    public void register(@ShellOption("--ip") String ip){
        MessegeHandler.regNode(ip);
    }

    @ShellMethod(key = "unreg", value = "unregister from Bootstrap server")
    public void unregister(){
        MessegeHandler.unregNode();
    }

    @ShellMethod(key = "table", value = "show routing table")
    public void table(){ printRoutingTable(); }

    @ShellMethod(key = "join", value = "join to neighbours in routing table")
    public void join(){
        MessegeHandler.joinNode();
    }

    @ShellMethod(key = "leave",value = "leave from neighbours")
    public void leave(){
        MessegeHandler.nodeLeave();
    }

    @ShellMethod(key = "search",value = "[search file_name hops(optional)] search files in network by name")
    public void search(@ShellOption("--name") String name,@ShellOption(value = "--hops" ,defaultValue="1") String hopsStr ){
        int hops=Integer.parseInt(hopsStr);
        MessegeHandler.searchFile(name,hops);
    }

    @ShellMethod(key = "files", value = "show selected files")
    public void files(){
        displayFiles();
    }

    @ShellMethod(key = "setport", value = "[setport port] change port if registration failed")
    public void setport(@ShellOption("--port") String port){
        changePort(port);
    }

    @ShellMethod(key = {"quit", "exit"}, value = "exit from application followed by 'unreg' and 'leave' ")
    public void quit(){
        MessegeHandler.exit();
        System.exit(0);
    }

    @ShellMethod(key="download" , value = "download a file froma given url")
    public void downlaod(@ShellOption("--url") String urlStr){

        FileDownloadHandler.downloadFile(urlStr);

    }


}
