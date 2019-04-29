import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {
        Scanner stdin = new Scanner(System.in);
        System.out.println("Enter the name of the picture you would like to perform haar wavelet transformation on:");
        String name = stdin.nextLine();
        File file = new File("C:\\Users\\caleb\\source\\repos\\HaarWaveletTransformation\\src\\" + name + ".png");
        File fileTwo = new File("C:\\Users\\caleb\\source\\repos\\HaarWaveletTransformation\\src\\image2.png");
        File fileThree = new File("C:\\Users\\caleb\\source\\repos\\HaarWaveletTransformation\\src\\image3.png");
        File fileFour = new File("C:\\Users\\caleb\\source\\repos\\HaarWaveletTransformation\\src\\image4.png");
        BufferedImage img = ImageIO.read(file);
        int width = img.getWidth();
        int height = img.getHeight();
        int[][] imgArr = new int[width][height];
        Raster raster = img.getData();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                imgArr[i][j] = raster.getSample(i, j, 0);
            }
        }

        double[][] imgOne = new double[512][512];
        double[][] imgTwo = new double[512][512];
        double[][] imgThree = new double[512][512];
        double[][] imgFour = new double[512][512];
        BufferedImage image = img;

        for (int i = 0; i < 512; i++) {
            for (int j = 0; j < 512; j++) {
                imgOne[i][j] = (double) imgArr[i][j];
            }
        }

        double[][] temp = createCopy(imgOne);
        imgTwo = compressImage(temp, 256);
        temp = createCopy(imgTwo);
        imgThree = compressImage(temp, 128);
        temp = createCopy(imgThree);
        imgFour = compressImage(temp, 64);

        writeToImage(fileTwo, img, convertToInt(imgTwo));
        writeToImage(fileThree, img, convertToInt(imgThree));
        writeToImage(fileFour, img, convertToInt(imgFour));

        printPicture(ImageIO.read(file), false);
        printPicture(ImageIO.read(fileFour), true);
    }

    private static int[][] convertToInt(double[][] imgArr) {
        int[][] returnArr = new int[512][512];
        for(int i = 0; i < 512; i++){
            for(int j = 0; j < 512; j++){
                returnArr[i][j] = (int)imgArr[i][j];
            }
        }
        return returnArr;
    }

    private static double[][] createCopy(double[][] imgArr) {
        double[][] returnArr = new double[512][512];
        for(int i = 0; i < 512; i++){
            for(int j = 0; j < 512; j++){
                returnArr[i][j] = imgArr[i][j];
            }
        }
        return returnArr;
    }

    private static double[][] compressImage (double[][] imgArr, int size){
        double[][] returnBlock = createCopy(imgArr);
        int pairIndex;
        for(int i = 0; i < size*2; i++){
            pairIndex = 0;
            for(int j = 0; j < size; j++){
                returnBlock[i][j] = (imgArr[i][pairIndex] + imgArr[i][pairIndex + 1]) / 2;
                returnBlock[i][j+size] = ((imgArr[i][pairIndex] - imgArr[i][pairIndex + 1]) / 2) + 100;
                pairIndex += 2;
            }
        }
        double[][] temp = createCopy(returnBlock);
        for(int j = 0; j < size*2; j++){
            pairIndex = 0;
            for(int i = 0; i < size; i++){
                returnBlock[i][j] = (temp[pairIndex][j] + temp[pairIndex + 1][j]) / 2;
                returnBlock[i+size][j] = ((temp[pairIndex][j] - temp[pairIndex + 1][j]) / 2) + 100;
                pairIndex += 2;
            }
        }
        return returnBlock;
    }

    private static void writeToImage(File file, BufferedImage image, int[][] imgArr){
        int[] pixelArr = new int[4];
        pixelArr[3] = 255;
        WritableRaster raster = (WritableRaster) image.getRaster();
        for(int i = 0; i < 512; i++){
            for(int j = 0; j < 512; j++){
                pixelArr[0] = imgArr[i][j];
                pixelArr[1] = imgArr[i][j];
                pixelArr[2] = imgArr[i][j];
                raster.setPixel(i, j, pixelArr);
            }
        }
        image.setData(raster);
        try {
            ImageIO.write(image, "png", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void printPicture(BufferedImage image, Boolean fin){
        ImageIcon icon=new ImageIcon(image);
        JFrame frame=new JFrame();
        frame.setLayout(new FlowLayout());
        frame.setSize(512,512);
        JLabel lbl=new JLabel();
        lbl.setIcon(icon);
        frame.add(lbl);
        frame.setVisible(true);
        if(fin){frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);}
    }
}