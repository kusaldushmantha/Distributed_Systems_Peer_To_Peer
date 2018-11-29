package springboot.rest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;

import static clientFiles.Util.getHash;

public class FileDownloadHandler {

    private static final String SAVE_DIR = "downloaded_files";

    private static final int BUFFER_SIZE = 4096;
    private static final int TIMEOUT = 10000;


    public static void downloadFile(String fileURL){

        String saveDir=SAVE_DIR;

        File directory = new File(saveDir);
        if (! directory.exists()){
            directory.mkdir();
        }

        try {

            //encode file name to handle spaces
            String[] split = fileURL.split("name=");
            String encodedName = URLEncoder.encode(split[1], "UTF-8");
            String urlEncodedName=split[0]+"name="+encodedName;


            URL url = new URL(urlEncodedName);
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setConnectTimeout(TIMEOUT);
            int responseCode = httpConn.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                String fileName = "";
                String disposition = httpConn.getHeaderField("Content-Disposition");
                String hashSentByServer = httpConn.getHeaderField("File-Hash");

                String contentType = httpConn.getContentType();
                int contentLength = httpConn.getContentLength();


                if (disposition != null) {
                 fileName = disposition.substring(disposition.indexOf("filename=") + 10, disposition.length() - 1);

                } else {
                    fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1, fileURL.length());
                }


                // opens input stream from the HTTP connection
                InputStream inputStream = httpConn.getInputStream();
                String saveFilePath = saveDir + File.separator + fileName;

                // opens an output stream to save into file
                FileOutputStream outputStream = new FileOutputStream(saveFilePath);

                int bytesRead = -1;
                byte[] buffer = new byte[BUFFER_SIZE];
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }

                outputStream.close();
                inputStream.close();


                int sizeInMb=contentLength/(1024*1024);
//                System.out.println("File Download ==> "+fileName+" of size"+sizeInMb+"MB");
                System.out.println("File Download ==> "+fileName+" of size"+sizeInMb+"MB");

                String hash = getHash(new File(saveFilePath));

//                print_ng("FileDownloadHandler > " + "Calculated hash: \t" +hash);
//                print_nng("FileDownloadHandler > " + "Server sent hash: \t" +hashSentByServer);


            } else {
                System.out.println("File Download ==> No file to download. Server replied HTTP code: " + responseCode);
            }
            httpConn.disconnect();
        }catch (ArrayIndexOutOfBoundsException e){
            System.out.println("File Download Error ==> " + "no file name!");
        }catch (SocketTimeoutException e){
            System.out.println("File download Error ==> " + "connection timeout");
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
