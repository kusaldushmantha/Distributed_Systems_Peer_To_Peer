package springboot.rest;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import clientFiles.Client;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.channels.Channels;
import java.util.Random;

import static clientFiles.Util.getHash;

@Component
public class UploadHandler {

    private Random random = new Random();
    private int fileSizeLower=2;
    private int fileSizeUpper=10;

    public ResponseEntity<Resource> sendFileIfExixt(String fileName, HttpServletRequest request, HttpServletResponse response){

        final boolean fileExist = Client.selectedFiles.contains(fileName);

        System.out.println("File Upload ==> > requested "+"\tFile: "+fileName+"\tfile exist: "+fileExist);

        if (fileExist && fileName!=""){
                return sendFile(fileName);
        }else {
            System.out.println("File Upload Error ==> Sending Not Found");
            return  new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<Resource> sendFile(String fileName) {

        try {
            String directoryName="sent_files";
            File directory = new File(directoryName);
            if (! directory.exists()){
                directory.mkdir();
            }

            String filePath=directoryName+"/"+fileName;
            RandomAccessFile f = new RandomAccessFile(filePath, "rw");
            int mbytes=random.nextInt(fileSizeUpper-fileSizeLower) + fileSizeLower;

            f.setLength(1024 * 1024 * mbytes );
            InputStream inputStream = Channels.newInputStream(f.getChannel());

            InputStreamResource inputStreamResource = new InputStreamResource(inputStream);

            int length=(int) f.length();

            String hash = getHash(new File(filePath));

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type",  "application/octet-stream");
            headers.add("Content-Disposition", String.format("attachment; filename=\"" + fileName + "\"" ));
            headers.add("Content-Length", Integer.toString(length));
            headers.add("File-Hash",hash);


            int sizeInMb=length/(1024*1024);
//            System.out.println("File Upload ==> File: " +fileName +" of Size: "+sizeInMb+"MB");
            System.out.println("File Upload ==> File: " +fileName +" with hash: "+sizeInMb+"MB");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(inputStreamResource);

        }catch (Exception e){
            e.printStackTrace();
            return  new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }


    }
}
