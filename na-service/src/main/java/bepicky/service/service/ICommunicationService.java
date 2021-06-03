package bepicky.service.service;

import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
public interface ICommunicationService {

    void communicate(InputStream data);
}
