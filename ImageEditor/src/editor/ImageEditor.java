package editor;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.StringJoiner;

public class ImageEditor {

    public static PPMImage inImageData;
    public static PPMImage outImageData;
    public static StringBuilder sb = new StringBuilder();

    public static void main(String args[]) throws FileNotFoundException {

        inImageData = null;
        outImageData = null;
        sb = new StringBuilder();

        //Collects info from the command line
        String inputFileName = args[0];
        String outputFileName = args[1];
        String effect = args[2];
        String blurSize = "0";
        if ("motionblur".equals(effect)) {
            blurSize = args[3];
        }

        read(inputFileName);

//        System.out.println(sj.toString());
//        output = outImageData.magicNumber + " " + outImageData.width  + " " +  outImageData.height + " " +  outImageData.maxVal;

        if("grayscale".equals(effect)){
            grayscaleImage();
        }else if("invert".equals(effect)){
            invertImage();
        }else if("emboss".equals(effect)){
            embossImage();
        }else if("motionblur".equals(effect)){
            blurImage(Integer.parseInt(blurSize));
        }

        sb.insert(0, outImageData.maxVal);
        sb.insert(0, " ");
        sb.insert(0, outImageData.height);
        sb.insert(0, " ");
        sb.insert(0, outImageData.width);
        sb.insert(0, " ");
        sb.insert(0, outImageData.magicNumber);

        System.out.println(sb.toString());
        write(outputFileName);

    }

    public static void processFile(File inputFileName) throws IOException {
        Scanner scanner = new Scanner(inputFileName);
        scanner.useDelimiter("((#[^\\n]*\\n)|(\\s+))+");
        ArrayList<String> data = new ArrayList<String>();
        while(scanner.hasNext()) {
            String str = scanner.next();
            //Gets rid of any comments in the data
            if (!"#".equals(str.charAt(0))) {
                data.add(str);
            }
        }
//        System.out.println(data);
        String magicNumber = (data.get(0));
        String width = data.get(1);
        String height = data.get(2);
        String maxVal = data.get(3);
        inImageData = new PPMImage(magicNumber, width, height, maxVal);
        outImageData = new PPMImage(magicNumber, width, height, maxVal);
        inImageData.input(data);
    }

    public static void read(String inputFileName){
        try{
            File readFile = new File(inputFileName);
            processFile(readFile);
        }catch(Exception e){
            e.printStackTrace();
            System.out.println(e);
        }
    }

    public static void write(String outputFileName) throws FileNotFoundException {
        File writeFile = new File(outputFileName);
        PrintWriter pw = new PrintWriter(writeFile);
        pw.println(sb.toString());
        pw.close();
    }

    public static void invertImage(){
        int y = Integer.parseInt(inImageData.width);
        int x = Integer.parseInt(inImageData.height);
        for(int i = 0; i < x; i++){
            for(int j = 0; j < y; j++){
                int inR = inImageData.pixels[i][j].getR();
                int inG = inImageData.pixels[i][j].getG();
                int inB = inImageData.pixels[i][j].getB();
                int outR = Integer.parseInt(inImageData.maxVal) - inR;
                int outG = Integer.parseInt(inImageData.maxVal) - inG;
                int outB = Integer.parseInt(inImageData.maxVal) - inB;

                outImageData.pixels[i][j] = new Pixel(outR, outG, outB);

                sb.append(" ");
                sb.append(outImageData.pixels[i][j].getR());
                sb.append(" ");
                sb.append(outImageData.pixels[i][j].getG());
                sb.append(" ");
                sb.append(outImageData.pixels[i][j].getB());

            }
        }
    }

    public static void grayscaleImage(){
        int y = Integer.parseInt(inImageData.width);
        int x = Integer.parseInt(inImageData.height);
        for(int i = 0; i < x; i++){
            for(int j = 0; j < y; j++){
                int inR = inImageData.pixels[i][j].getR();
                int inG = inImageData.pixels[i][j].getG();
                int inB = inImageData.pixels[i][j].getB();
                int outR = (inR + inG + inB)/3;
                int outG = (inR + inG + inB)/3;
                int outB = (inR + inG + inB)/3;

                outImageData.pixels[i][j] = new Pixel(outR, outG, outB);

                sb.append(" ");
                sb.append(outImageData.pixels[i][j].getR());
                sb.append(" ");
                sb.append(outImageData.pixels[i][j].getG());
                sb.append(" ");
                sb.append(outImageData.pixels[i][j].getB());

            }
        }
    }

    public static void embossImage(){
        int maxVal;
        int outR;
        int outG;
        int outB;
        int y = Integer.parseInt(inImageData.width);
        int x = Integer.parseInt(inImageData.height);
        for(int i = x-1; i >= 0; i--){
            for(int j = y-1; j >= 0; j--) {
                if (i == 0 || j == 0) {
                    outR = 128;
                    outG = 128;
                    outB = 128;
                } else {

                    int inR1 = inImageData.pixels[i][j].getR();
                    int inG1 = inImageData.pixels[i][j].getG();
                    int inB1 = inImageData.pixels[i][j].getB();
                    int inR2 = inImageData.pixels[i - 1][j - 1].getR();
                    int inG2 = inImageData.pixels[i - 1][j - 1].getG();
                    int inB2 = inImageData.pixels[i - 1][j - 1].getB();
                    int newR = inR1 - inR2;
                    int newG = inG1 - inG2;
                    int newB = inB1 - inB2;

                    if((i == 7 && j == 1) || (j == 7 && i == 1)){
                        System.out.println();
                    }

                    maxVal = newR;
                    if(Math.abs(maxVal) < Math.abs(newG)){
                        maxVal = newG;
                    }
                    if(Math.abs(maxVal) < Math.abs((newB))){
                        maxVal = newB;
                    }

                    outR = maxVal + 128;
                    outG = maxVal + 128;
                    outB = maxVal + 128;

                    //Check to see if pixel values are below the minimum
                    if (outR < 0) {
                        outR = 0;
                    }
                    if (outG < 0) {
                        outG = 0;
                    }
                    if (outB < 0) {
                        outB = 0;
                    }

                    //Check to see if pixel values are above the maximum
                    if (outR > 255) {
                        outR = 255;
                    }
                    if (outG > 255) {
                        outG = 255;
                    }
                    if (outB > 255) {
                        outB = 255;
                    }
                }

                outImageData.pixels[i][j] = new Pixel(outR, outG, outB);

                sb.insert(0, outImageData.pixels[i][j].getR());
                sb.insert(0," ");
                sb.insert(0, outImageData.pixels[i][j].getG());
                sb.insert(0, " ");
                sb.insert(0, outImageData.pixels[i][j].getB());
                sb.insert(0, " ");
            }
        }
    }

    public static void blurImage(int n){
        int outR = 0;
        int outG = 0;
        int outB = 0;
        int y = Integer.parseInt(inImageData.width);
        int x = Integer.parseInt(inImageData.height);
        for(int i = 0; i < x; i++){
            for(int j = 0; j < y; j++){
                int inR = inImageData.pixels[i][j].getR();
                int inG = inImageData.pixels[i][j].getG();
                int inB = inImageData.pixels[i][j].getB();
                int k = 1;
                for(k = 1; k < n; k++){
                    if(j+k > y-1){
                        break;
                    }else{
                        inR += inImageData.pixels[i][j+k].getR();
                        inG += inImageData.pixels[i][j+k].getG();
                        inB += inImageData.pixels[i][j+k].getB();
                    }
                }

                outR = inR / k;
                outG = inG / k;
                outB = inB / k;

                outImageData.pixels[i][j] = new Pixel(outR, outG, outB);

                sb.append(" ");
                sb.append( outImageData.pixels[i][j].getR());
                sb.append(" ");
                sb.append(outImageData.pixels[i][j].getG());
                sb.append(" ");
                sb.append(outImageData.pixels[i][j].getB());
            }
        }
    }

}
