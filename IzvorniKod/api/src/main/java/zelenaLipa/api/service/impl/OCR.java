package zelenaLipa.api.service.impl;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import zelenaLipa.api.domain.Document;
import zelenaLipa.api.service.DocumentService;
import zelenaLipa.api.service.OCRService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class OCR implements OCRService {

    @Autowired
    private DocumentService documentService;

    @Override
    public int doOCR(MultipartFile mpf, String path, int groupId) throws IOException, TesseractException {

        File file = convertFileIntoBytes(mpf, path);
        Tesseract tesseract = configureOCR();
        int docuId = startOCR(tesseract, file, groupId, mpf);
        file.delete();
        return docuId;

    }

    private int startOCR(Tesseract tesseract, File file, int groupId, MultipartFile mpf) throws TesseractException {

        String resultOCR = tesseract.doOCR(file);
        int docuId = storeDocuInDB(resultOCR, groupId, mpf.getOriginalFilename());
        return docuId;

    }

    private int storeDocuInDB(String resultOCR, int groupId, String title) {

        Random rand = new Random(System.currentTimeMillis());
        int docuId;
        do {
            docuId = rand.nextInt(900000) + 100000;
        } while(documentService.existByDocuId(docuId));
        String designation = "NO TYPE";
        Pattern patternINT = Pattern.compile("INT[0-9][0-9][0-9][0-9]");
        Matcher matcher = patternINT.matcher(resultOCR);
        if(matcher.find()) { designation = matcher.group(); }
        else {
            Pattern patternP = Pattern.compile("P[0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]");
            matcher = patternP.matcher(resultOCR);
            if(matcher.find()) { designation = matcher.group(); }
            else {
                Pattern patternR = Pattern.compile("R[0-9][0-9][0-9][0-9][0-9][0-9]");
                matcher = patternR.matcher(resultOCR);
                if(matcher.find()) { designation = matcher.group(); }
            }
        }
        String username = checkUsername();
        Document document = new Document();
        document.setDocuId(docuId);
        document.setUsername(username);
        document.setContent(resultOCR);
        document.setGroupId(groupId);
        document.setTitle(title);
        document.setType(designation);
        documentService.storeDocument(document);
        return docuId;

    }

    //KONFIGURACIJA OCR-a
    private Tesseract configureOCR() {

        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath("./tessdata");
        //tesseract.setLanguage("eng");
        tesseract.setLanguage("hrv"); //Set Croatian
        return tesseract;

    }
    //KONFIGURACIJA OCR-a

    private File convertFileIntoBytes(MultipartFile file, String path) throws IOException {

        File img = new File(path + "/images");
        img.mkdir(); //Kreiraj direktorij
        File newFile = new File(path + "/images/" + file.getOriginalFilename());
        newFile.createNewFile();
        FileOutputStream fos = new FileOutputStream(newFile);
        fos.write(file.getBytes());
        fos.close();
        return newFile;

    }

    private String checkUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        if(principal instanceof UserDetails) {
            username = ((UserDetails)principal).getUsername();
        } else {
            username = "anonymous";
        }
        return username;
    }

}
