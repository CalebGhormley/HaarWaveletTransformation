import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        File file = new File("C:\\Users\\caleb\\source\\repos\\HaarWaveletTransformation\\src\\lena.png");
        File fileTwo = new File("C:\\Users\\caleb\\source\\repos\\HaarWaveletTransformation\\src\\lena2.png");
        File fileThree = new File("C:\\Users\\caleb\\source\\repos\\HaarWaveletTransformation\\src\\lena3.png");
        File fileFour = new File("C:\\Users\\caleb\\source\\repos\\HaarWaveletTransformation\\src\\lena4.png");
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

        float[][] imgOne = new float[512][512];
        float[][] imgTwo = new float[512][512];
        float[][] imgThree = new float[512][512];
        float[][] imgFour = new float[512][512];
        int[][] first = new int[512][512];
        int[][] second = new int[512][512];
        int[][] third = new int[512][512];
        BufferedImage image = img;

        for (int i = 0; i < 512; i++) {
            for (int j = 0; j < 512; j++) {
                imgOne[i][j] = (float) imgArr[i][j];
            }
        }

        float[][] temp = createCopy(imgOne);
        imgTwo = compressImage(temp, 256);
        temp = createCopy(imgTwo);
        imgThree = compressImage(temp, 128);
        temp = createCopy(imgThree);
        imgFour = compressImage(temp, 64);

        writeToImage(fileTwo, img, convertToInt(imgTwo));
        writeToImage(fileThree, img, convertToInt(imgThree));
        writeToImage(fileFour, img, convertToInt(imgFour));
    }

    private static int[][] convertToInt(float[][] imgArr) {
        int[][] returnArr = new int[512][512];
        for(int i = 0; i < 512; i++){
            for(int j = 0; j < 512; j++){
                returnArr[i][j] = (int)imgArr[i][j];
            }
        }
        return returnArr;
    }

    private static float[][] createCopy(float[][] imgArr) {
        float[][] returnArr = new float[512][512];
        for(int i = 0; i < 512; i++){
            for(int j = 0; j < 512; j++){
                returnArr[i][j] = imgArr[i][j];
            }
        }
        return returnArr;
    }

    private static float[][] compressImage (float[][] imgArr, int size){
        float[][] returnBlock = createCopy(imgArr);
        int pairIndex;
        for(int i = 0; i < size*2; i++){
            pairIndex = 0;
            for(int j = 0; j < size; j++){
                returnBlock[i][j] = (imgArr[i][pairIndex] + imgArr[i][pairIndex + 1]) / 2;
                returnBlock[i][j+size] = (imgArr[i][pairIndex] - imgArr[i][pairIndex + 1]) / 2;
                pairIndex += 2;
            }
        }
        float[][] temp = createCopy(returnBlock);
        for(int j = 0; j < size*2; j++){
            pairIndex = 0;
            for(int i = 0; i < size; i++){
                returnBlock[i][j] = (temp[pairIndex][j] + temp[pairIndex + 1][j]) / 2;
                returnBlock[i+size][j] = (temp[pairIndex][j] - temp[pairIndex + 1][j]) / 2;
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
}