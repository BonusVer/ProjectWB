package org.example;

import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class FileName {
    public String getFileName() {
        SimpleDateFormat sdf = new SimpleDateFormat("_MM_dd_HH_mm");
        Date now =new Date();
        String datestamp = sdf.format(now);
        String fileName = "Price" + datestamp + ".xlsx";
        return fileName;
    }
}
