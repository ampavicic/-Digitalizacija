package zelenaLipa.api.service;

import net.sourceforge.tess4j.TesseractException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface OCRService {

    int doOCR(MultipartFile mpf, String path, int groupId) throws IOException, TesseractException;

}
