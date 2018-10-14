package springshell;

import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;
import udpclient.SendingMessageHandler;

import static udpclient.Printer.*;
import static udpclient.Util.changeMyPort;
import static udpclient.Util.getHelpText;

@Component
public class ShellCommands implements CommandMarker {

    @CliCommand(value = "reg", help = "[reg --ip server_ip] register to Bootstrap server")
    public void register(@CliOption(key = "ip") String ip){
        SendingMessageHandler.registerToBS(ip);
    }

    @CliCommand(value = "regl", help = "register to Bootstrap server running on same ip")
    public void registerLocalBsServer(){
        SendingMessageHandler.registerToBSonSameIp();
    }

    @CliCommand(value = "unreg", help = "unregister from Bootstrap server")
    public void unregister(){
        SendingMessageHandler.unregisterFromBS();
    }

    @CliCommand(value = "table", help = "show routing table")
    public void table(){
        printRoutingTable();
    }

    @CliCommand(value = "join", help = "join to neighbours in routing table")
    public void join(){
        SendingMessageHandler.joinToSystem();
    }

    @CliCommand(value = "leave",help = "leave from neighbours")
    public void leave(){
        SendingMessageHandler.leaveTheSystem();
    }

    @CliCommand(value = "search",help = "[search --n file_name --h hops(optional)] search files in network by name")
    public void search(@CliOption(key = {"name" ,"n"}) String name,@CliOption(key = {"hops", "h"}) String hopsStr ){
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

    @CliCommand(value = "files", help = "show selected files")
    public void files(){
        printSelectedFiles();
    }

    @CliCommand(value = "apphelp", help = "app commands (this)")
    public void help(){
        printHelp(getHelpText());
    }

    @CliCommand(value = "setport", help = "[setport port] change port if registration failed")
    public void setport(@CliOption(key = {"port","p"}) String port){
        changeMyPort(port);
    }

    @CliCommand(value = {"appexit"}, help = "exit from application followed by 'unreg' and 'leave' ")
    public void quit(){
        SendingMessageHandler.exit();
        System.exit(0);
    }

}
