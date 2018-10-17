package springboot.rest;

import org.apache.commons.io.IOUtils;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.channels.Channels;

import static udpclient.Printer.print_n;

@RestController
public class RestControllerClass {

    @RequestMapping("/")
    public String welcome(){
        System.out.println("welcome came");
        return "Welcome";
    }

    @RequestMapping("/download")
    public ResponseEntity<Resource> downloadFiled(@RequestParam("name") String fileName, HttpServletRequest request, HttpServletResponse response) throws IOException {

        String directoryName="sent_files";
        File directory = new File(directoryName);
        if (! directory.exists()){
            directory.mkdir();
        }
        RandomAccessFile f = new RandomAccessFile(directoryName+"/"+fileName, "rw");
        int mbytes=2;

        f.setLength(1024 * 1024 * mbytes );
        InputStream inputStream = Channels.newInputStream(f.getChannel());

        InputStreamResource inputStreamResource = new InputStreamResource(inputStream);

        int length=(int) f.length();


        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type",  "application/octet-stream");
        headers.add("Content-Disposition", String.format("attachment; filename=\"" + fileName + "\"" ));
        headers.add("Content-Length", Integer.toString(length));


        int sizeInMb=length/(1024*1024);
        print_n("Uploader > "+"Uploading\t"+ "File name: " +fileName +"\tSize: "+sizeInMb+"MB");

        return ResponseEntity.ok()
                .headers(headers)
                .body(inputStreamResource);

    }
}
