package org.example;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ExcelHelper {
    public static String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    public static boolean hasExcelFormat(MultipartFile file) {
        if(TYPE.equals(file.getContentType())) {
            return false;
        }
        return true;
    }
}
