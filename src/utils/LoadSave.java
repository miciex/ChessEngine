package utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;

public class LoadSave {

    public static final String PIECES_ATLAS = "ChessPieces.png";
    public static final String GM_GAMES = "Games.txt";

    public static BufferedImage GetSpriteAtlas(String fileName){
        BufferedImage img = null;
        InputStream is = LoadSave.class.getResourceAsStream("/"+fileName);
        try{
            img = ImageIO.read(is);
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            try {
                is.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        return img;
    }

    public static ArrayList<ArrayList<String>> getGrandmasterGames(String fileName){
        ArrayList<ArrayList<String>> gmGames = new ArrayList<>();
        InputStream is = LoadSave.class.getResourceAsStream("/" + fileName);
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        try{
            String s;
            while((s = br.readLine())!=null){
                gmGames.add(new ArrayList<>());
                String m = "";
                for(char i : s.toCharArray()){
                    if(i == ' ') {
                        gmGames.get(gmGames.size() - 1).add(m);
                        m = "";
                    }else{
                        m += Character.toString(i);
                    }
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                is.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        return gmGames;
    }
}
