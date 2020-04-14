package Util.Util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class NoiseLine {
    private final int M = 130;
    private final int N = 36;
    int a[][] = new int[this.M][this.N];
    int[] b = new int[this.M];
    int threshold = 20;
    int offset = 11;

    public void binaryImage() throws IOException{
        File file = new File("D:\\test/test1.png");
        BufferedImage image = ImageIO.read(file);

        int width = image.getWidth();
        int height = image.getHeight();

        BufferedImage grayImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);//重点，技巧在这个参数BufferedImage.TYPE_BYTE_BINARY
        for(int i= 0 ; i < width ; i++){
            for(int j = 0 ; j < height; j++){
                int rgb = image.getRGB(i, j);
                grayImage.setRGB(i, j, rgb);
            }
        }

        File newFile = new File(System.getProperty("user.dir")+"/src/2722425974762424028.jpg");
        ImageIO.write(grayImage, "jpg", newFile);
    }

    public static int isBlack(int colorInt, int whiteThreshold) {
        final Color color = new Color(colorInt);
        if (color.getRed() + color.getGreen() + color.getBlue() <= whiteThreshold) {
            return 1;
        }
        return 0;
    }

    public NoiseLine() {
        int x = 0;
        int y = 0;
        try {
//            final BufferedImage img = ImageIO.read(new File("D:\\test/test1.png"));
//            this.renderImg = img;
//            final int width = img.getWidth();
//            final int height = img.getHeight();
//            System.out.println(width + ":" + height);
//            for (; x < height; ++x) {
//                for (y = 0; y < width; ++y) {
//                    System.out.println(x + "+" + y);
//                    this.a[x][y] = isBlack(img.getRGB(y, x), 700);
//                    //System.out.print(this.a[x][y] + " ");
//                }
//                //System.out.println(" ");
//            }
            File file = new File("D:\\test/test1.jpg");
            BufferedImage image = ImageIO.read(file);
            this.renderImg = image;
            int width = image.getWidth();
            int height = image.getHeight();
            BufferedImage grayImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);//重点，技巧在这个参数BufferedImage.TYPE_BYTE_BINARY
            for(int i= 0 ; i < width ; i++){
                for(int j = 0 ; j < height; j++){
                    int rgb = image.getRGB(i, j);
                    grayImage.setRGB(i, j, rgb);
                }
            }
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    private BufferedImage renderImg;

    public void genLine(int n) {
        if (n < this.offset) {
            this.b[n] = -1;
            this.genLine(n + 1);
        }
        if (n == this.M) {
            for (int i = 0; i < this.M; i++) {
                System.out.print(this.b[i] + " ");

            }
            System.out.println("");
        }
        if (n == this.offset) {
            for (int j = 0; j < this.N; j++) {
                if (this.a[this.offset][j] == 1) {
                    this.b[this.offset] = j;
                    this.genLine(n + 1);
                }
            }
        }
        if (n > 0 && n < this.M) {
            int hasMore = 0;
            if (this.b[n - 1] > 0 && this.b[n - 1] < this.N && this.a[n][this.b[n - 1]] == 1) {
                this.b[n] = this.b[n - 1];
                hasMore = 1;
                this.genLine(n + 1);
            } else {
                if (this.b[n - 1] > 0 && this.a[n][this.b[n - 1] - 1] == 1) {
                    this.b[n] = this.b[n - 1] - 1;
                    hasMore = 1;
                    this.genLine(n + 1);
                }
                if (this.b[n - 1] < this.N - 1 && this.a[n][this.b[n - 1] + 1] == 1) {
                    this.b[n] = this.b[n - 1] + 1;
                    hasMore = 1;
                    this.genLine(n + 1);
                }
            }
            if (n - this.offset > this.threshold && hasMore == 0) {
                for (int i = 0; i < n; i++) {
                    if (this.b[i] > 0) {
                        this.renderImg.setRGB(this.b[i], i, Color.RED.getRGB());
                    }
                }
            }
        }

    }

    public void saveImg() {
        try {
            ImageIO.write(this.renderImg, "JPG", new File("D:\\test/test233.png"));
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        final NoiseLine line = new NoiseLine();
        line.genLine(0);
        line.saveImg();
        System.out.println("处理后图片在img/noiseRender.jpg");
    }

}