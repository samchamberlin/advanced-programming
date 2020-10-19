package editor;

import java.util.ArrayList;

public class PPMImage {

    public String magicNumber;
    public String width;
    public String height;
    public String maxVal;
    public Pixel[][] pixels;

    public PPMImage(String mn, String w, String h, String mv){
        magicNumber = mn;
        width = w;
        height = h;
        maxVal = mv;
        int y = Integer.parseInt(w);
        int x = Integer.parseInt(h);
        pixels = new Pixel[x][y];
    }

    public void input(ArrayList<String> data){
        int y = Integer.parseInt(width);
        int x = Integer.parseInt(height);
        int n = 4;
        for(int i = 0; i < x; i ++){
            for(int j = 0; j < y; j++){
                int R = Integer.parseInt(data.get(n));
                n++;
                int G = Integer.parseInt(data.get(n));
                n++;
                int B = Integer.parseInt(data.get(n));
                n++;
                Pixel pixel = new Pixel(R,G,B);
                pixels[i][j] = pixel;
            }
        }
        System.out.println();

    }

//    public String getMagicNumber() {
//        return magicNumber;
//    }
//
//    public void setMagicNumber(String num) {
//        magicNumber = num;
//    }
//
//    public String getWidth() {
//        return width;
//    }
//
//    public void setWidth(String num) {
//        width = num;
//    }
//
//    public String getHeight() {
//        return height;
//    }
//
//    public void setHeight(String num) {
//        height = num;
//    }
//
//    public String getMaxVal() {
//        return maxVal;
//    }
//
//    public void setMaxVal(String num) {
//        maxVal = num;
//    }

}
