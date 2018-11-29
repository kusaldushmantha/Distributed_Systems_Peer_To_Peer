package springboot.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
public class Controller {

    @Autowired
    UploadHandler uploadService;

    @RequestMapping("/")
    public String welcome(){
        return "Welcome";
    }

    @RequestMapping("/download")
    public ResponseEntity<Resource> downloadFiled(@RequestParam("name") String fileName,
                                                  HttpServletRequest request,
                                                  HttpServletResponse response){

        return uploadService.sendFileIfExixt(fileName,request,response);

    }
}
