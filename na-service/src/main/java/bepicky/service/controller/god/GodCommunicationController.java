package bepicky.service.controller.god;

import bepicky.service.service.ICommunicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/god/communication")
public class GodCommunicationController {

    @Autowired
    private ICommunicationService communicationService;

    @PostMapping(path = "/message", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void notifyMessage(@RequestParam("file") MultipartFile file) throws IOException {
        communicationService.communicate(file.getInputStream());
    }
}
