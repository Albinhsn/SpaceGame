package se.liu.albhe576.project;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Font
{

    float fontSize;
    int spaceSize;

    private final List<Letter> letters;

    Texture texture;

    private void loadFontTexture(String fileLocation){
	try{
	    this.texture = GameData.loadPNGFile(fileLocation);
	}catch(IOException e){
		e.printStackTrace();
		System.out.println("Unable to load png file from " + fileLocation);
		System.exit(1);
	}
    }
    int getSentencePixelLength(String sentence){
	int pixelLength = 0;
	for(int i =0; i< sentence.length(); i++){
	    int letterIdx = (int) sentence.charAt(i) - 32;
	    pixelLength += letterIdx == 0 ? spaceSize : letters.get(letterIdx).size + 1;
	}
	return pixelLength;
    }
    private void parseLetters(String fontInfoLocation)  throws java.io.FileNotFoundException {

	File file = new File(fontInfoLocation);
	Scanner fileScanner = new Scanner(file);
	while(fileScanner.hasNextLine()){
	    List<String> elements= Arrays.stream(fileScanner.nextLine().split(" ")).filter(s -> !s.isEmpty()).toList();
	    int last = elements.size() - 1;
	    this.letters.add(new Letter(
		    Float.parseFloat(elements.get(last - 2)),
		    Float.parseFloat(elements.get(last - 1)),
		    Integer.parseInt(elements.get(last))
	    ));
	}
    }
    public static Font parseFontFromFile(String fontInfoLocation, String fontTextureLocation, float fontSize) throws java.io.FileNotFoundException{
	Font font = new Font();
	font.fontSize = fontSize;
	font.spaceSize = 3;

	font.loadFontTexture(fontTextureLocation);
	font.parseLetters(fontInfoLocation);

	return font;
    }
    public Letter getLetterByIndex(int index){
	return this.letters.get(index);
    }
    private Font(){
	this.letters = new ArrayList<>();
	this.texture = null;
    }
}
