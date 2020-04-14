package Util.Util;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import java.io.File;

public class OCRUtiles {

    public static void FindOCR(String srImage) {
        File imageFile = new File(srImage);
        ITesseract instance = new Tesseract();
        try {
            //instance.setLanguage("chi_sim"); //加载语言包
//            String result = instance.doOCR(imageFile);
            String result = instance.doOCR(imageFile);
            System.out.println(imageFile.getName());
            System.out.println("result：");
            System.out.print(result);
        } catch (TesseractException e) {
            System.out.println(e.getMessage());
        }
    }
}
