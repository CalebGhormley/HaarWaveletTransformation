import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        File file = new File("C:\\Users\\caleb\\source\\repos\\HaarWaveletTransformation\\src\\lena.png");
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

        float[][] imgOne = new float[256][256];
        float[][] imgTwo = new float[256][256];
        float[][] imgThree = new float[256][256];
        float[][] imgFour = new float[256][256];
        float[][] block = new float[8][8];
        String[] rowsOne = new String[256];
        String[] rowsTwo = new String[256];

        for (int i = 0; i < 256; i++) {
            for (int j = 0; j < 256; j++) {
                imgOne[i][j] = (float) imgArr[i][j];
            }
        }

        System.out.println("Original block------------------------------------------");
        for(int i = 248; i < 256; i++) {
            for(int j = 248; j < 256; j++) {
                if(rowsOne[i] == null){rowsOne[i] = String.valueOf(imgOne[i][j]) + ", ";}
                else {rowsOne[i] = rowsOne[i] + String.valueOf(imgOne[i][j]) + ", ";}
            }
            System.out.println(rowsOne[i]);
        }

        int indexI = 0, indexJ = 0;
        for(int i = 0; i < 32; i++){
            indexJ = 0;
            for(int j = 0; j < 32; j++){
                block = compressBlock(imgOne, indexI, indexJ);
                for (int k = 0; k < 8; k++){
                    for (int h = 0; h < 8; h++){
                        imgTwo[k + indexI][h + indexJ] = block[k][h];
                    }
                }
                indexJ += 8;
            }
            indexI += 8;
        }

        System.out.println("Second Block------------------------------------------");
        for(int i = 248; i < 256; i++) {
            for(int j = 248; j < 256; j++) {
                if(rowsTwo[i] == null){rowsTwo[i] = String.valueOf(imgTwo[i][j]) + ", ";}
                else {rowsTwo[i] = rowsTwo[i] + String.valueOf(imgTwo[i][j]) + ", ";}
            }
            System.out.println(rowsTwo[i]);
        }


        int[] tempArr = new int[1];
        int[][] outArr = imgArr;
        outArr = addSubImage(outArr, imgTwo);
        BufferedImage image = img;
        WritableRaster wraster = (WritableRaster) image.getRaster();
        for(int i = 0; i < 256; i++){
            for(int j = 0; j < 256; j++){
                tempArr[0] = outArr[i][j];
                wraster.setPixel(i, j, tempArr);
            }
        }
        image.setData(wraster);

        File fileTwo = new File("C:\\Users\\caleb\\source\\repos\\HaarWaveletTransformation\\src\\lena2.png");
        ImageIO.write(image, "png", fileTwo);
    }

    private static float[][] compressBlock (float[][] imgArr, int startI, int startJ){
        float[][] returnBlock = new float[8][8];
        int pairIndex;
        for(int i = 0; i < 8; i++){
            pairIndex = 0;
            for(int j = 0; j < 4; j++){
                returnBlock[i][j] = (imgArr[i + startI][pairIndex + startJ] + imgArr[i + startI][pairIndex + 1 + startJ]) / 2;
                returnBlock[i][j+4] = (imgArr[i + startI][pairIndex + startJ] - imgArr[i + startI][pairIndex + 1 + startJ]) / 2;
                pairIndex += 2;
            }
        }
        float[][] temp = returnBlock;
        for(int j = 0; j < 8; j++){
            pairIndex = 0;
            for(int i = 0; i < 4; i++){
                returnBlock[i][j] = (temp[pairIndex][j] + temp[pairIndex + 1][j]) / 2;
                returnBlock[i+4][j] = (temp[pairIndex][j] - temp[pairIndex + 1][j]) / 2;
                pairIndex += 2;
            }
        }
        return returnBlock;
    }

    private static int[][] addSubImage(int[][] outArr, float[][] compressedArr){
        int[][] tempArr = new int[128][128];
        for(int i= 0; i < 256; i++) {
            for (int j = 0; j < 256; j++) {
                outArr[i][j] = outArr[i][j];
            }
        }

        for(int i= 0; i < 32; i++){
            for (int j = 0; j < 32; j++){
                for(int h = 0; h < 4; h++){
                    for(int k = 0; k < 4; k++){
                        tempArr[(i*4)+h][(j*4)+k] = (int) compressedArr[(i*8)+h][(j*8)+k];
                    }
                }
            }
        }
        for(int i= 0; i < 128; i++) {
            for (int j = 0; j < 128; j++) {
                outArr[i][j] = tempArr[i][j];
            }
        }

        return outArr;
    }
}
