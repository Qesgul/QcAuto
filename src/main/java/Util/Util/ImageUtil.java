package Util.Util;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageUtil {
    /**
     * * 转换图片 * *
     */
    public static void changeImge(File img) {
        try {
            Image image = ImageIO.read(img);
            int srcH = image.getHeight(null);
            int srcW = image.getWidth(null);
            BufferedImage bufferedImage = new BufferedImage(srcW, srcH,BufferedImage.TYPE_3BYTE_BGR);
            bufferedImage.getGraphics().drawImage(image, 0,0, srcW, srcH, null);
            bufferedImage=new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY),null).filter (bufferedImage,null);
            FileOutputStream fos = new FileOutputStream(img);
            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(fos);
            encoder.encode(bufferedImage);
            fos.close();
            // System.out.println("转换成功...");
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException("图片转换出错！", e);
        }
    }
}
