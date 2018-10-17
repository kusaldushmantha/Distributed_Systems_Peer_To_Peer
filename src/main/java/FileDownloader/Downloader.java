package FileDownloader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static udpclient.Printer.print_n;

public class Downloader {

    private static final int BUFFER_SIZE = 4096;

    public static void downloadFile(String fileURL, String saveDir){

        File directory = new File(saveDir);
        if (! directory.exists()){
            directory.mkdir();
        }

        try {
            URL url = new URL(fileURL);
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            int responseCode = httpConn.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                String fileName = "";
                String disposition = httpConn.getHeaderField("Content-Disposition");
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

                print_n("Downloader > "+"Downloaded\t"+ "File name: " +fileName +"\tSize: "+sizeInMb+"MB");
            } else {
                System.out.println("No file to download. Server replied HTTP code: " + responseCode);
            }
            httpConn.disconnect();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
