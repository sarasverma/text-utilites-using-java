package com.saras.textUtility;

// Modules for GUI related stuff
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.datatransfer.Clipboard; // for copying text
import java.awt.datatransfer.StringSelection;
// Modules for text-to-speech(Freetts)
import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;
// Modules for textRecognition
import java.io.File;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
// Modules for request API
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedInputStream; // for buffered I/O
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;  // for json
import org.json.JSONTokener;


// Main class
public class TextUtility {
    public static void main(String[] args) {
        mainFrame obj = new mainFrame();
    }
}


// Class for gui
class mainFrame extends JFrame implements ActionListener{

    // make it global
    private JTextPane textPane;
    private JButton lowerCaseButton, upperCaseButton, capitalizeButton, sequenceButton;
    private JButton appendButton, prependButton, removeSpaceButtton, textRecognitionButton, translateButton;
    private JButton copyButton, clearButton, readButton;

    mainFrame(){
    	
    	// Text area related stuff
        JPanel textContainer = new JPanel();
        textPane = new JTextPane();
        textPane.setText("Put your text here...");
        // ** required this font otherwise other language won\"t work **
        textPane.setFont(new Font("Nirmala UI", Font.PLAIN, 14)); 
        JScrollPane scroll = new JScrollPane( textPane );
        scroll.setVerticalScrollBarPolicy( ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS );
        textContainer.setLayout(new BorderLayout());
        textContainer.add(scroll);


        // All buttons container
        JPanel allButtonContainer = new JPanel();


        // Button Utility container
        JPanel buttonUtilityContainer = new JPanel();
        lowerCaseButton = new JButton("Lower");
        upperCaseButton = new JButton("Upper");
        capitalizeButton = new JButton("Capitalize");
        sequenceButton = new JButton("Sequence");
        appendButton = new JButton("Append");
        prependButton = new JButton("Prepend");
        removeSpaceButtton = new JButton("Remove Spaces");
        textRecognitionButton = new JButton("Text Recognition");
        translateButton = new JButton("Translate");
        
        buttonUtilityContainer.setLayout(new BoxLayout(buttonUtilityContainer, BoxLayout.PAGE_AXIS));
        
        // adding functionalities to buttonUtilityContainer
        lowerCaseButton.addActionListener(this);
        upperCaseButton.addActionListener(this);
        capitalizeButton.addActionListener(this);
        sequenceButton.addActionListener(this);
        appendButton.addActionListener(this);
        prependButton.addActionListener(this);
        removeSpaceButtton.addActionListener(this);
        textRecognitionButton.addActionListener(this);
        translateButton.addActionListener(this);
        
        // adding elements to buttonUtilityContainer
        buttonUtilityContainer.add(lowerCaseButton);
        buttonUtilityContainer.add(upperCaseButton);
        buttonUtilityContainer.add(capitalizeButton);
        buttonUtilityContainer.add(sequenceButton);
        buttonUtilityContainer.add(appendButton);
        buttonUtilityContainer.add(prependButton);
        buttonUtilityContainer.add(removeSpaceButtton);
        buttonUtilityContainer.add(textRecognitionButton);
        buttonUtilityContainer.add(translateButton);

        
        // Button basic container
        JPanel buttonBasicContainer = new JPanel();
        copyButton = new JButton("Copy");
        clearButton = new JButton("Clear");
        readButton = new JButton("Read");
        
        
        // adding functionalities to buttonBasicContainer
        copyButton.addActionListener(this);
        clearButton.addActionListener(this);
        readButton.addActionListener(this);
        
        // adding elements to buttonBasicContainer
        buttonBasicContainer.add(copyButton);
        buttonBasicContainer.add(clearButton);
        buttonBasicContainer.add(readButton);

        

        // adding all buttons section to button container
        allButtonContainer.setLayout(new BorderLayout());
        allButtonContainer.add(buttonUtilityContainer, BorderLayout.NORTH);
        allButtonContainer.add(buttonBasicContainer, BorderLayout.SOUTH);


        // Adding all container to frame
        setLayout(new BorderLayout());
        add(textContainer, BorderLayout.CENTER);
        add(allButtonContainer, BorderLayout.EAST);

        
        
        // frame configuration
        setSize(1200, 600);
        setTitle("Text utility");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    
    // implementing functionalities
    public void actionPerformed(ActionEvent event) {
        String action = event.getActionCommand();
        if (action.equals("Clear")) {
            textPane.setText("");
        }
        else if (action.equals("Copy")) {
        	StringSelection stringSelection = new StringSelection(textPane.getText());
        	Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        	clipboard.setContents(stringSelection, null);
            JOptionPane.showMessageDialog(this, "Successfully copied to clipboard !");
        }
        else if (action.equals("Read")) {
            speak();
        }
        else if (action.equals("Lower")) {
            String text = textPane.getText();
            textPane.setText(text.toLowerCase());
        }
        else if (action.equals("Upper")) {
            String text = textPane.getText();
            textPane.setText(text.toUpperCase());
        }
        else if (action.equals("Remove Spaces")){
            String text = textPane.getText();
            String modifiedText = text.trim().replaceAll("\\s{2,}", " ");
            textPane.setText(modifiedText);
        }
        else if (action.equals("Text Recognition")) {
        	textRecognition();
        }
        else if (action.equals("Capitalize")) {
        	String text = textPane.getText();
        	if(text != "") {
	        	String firstLetter = "", allOther = "", modifiedText = "";
	        	String textLines[] = text.split("\\.");
	        	for(String line : textLines) {
	        		line = line.trim();
	        		if(line.length() >= 1) {	        			
	        			firstLetter = line.substring(0, 1).toUpperCase();
	        			allOther =  line.substring(1);
	        			modifiedText += firstLetter + allOther + ". ";
	        		}
	        	}
	        	textPane.setText(modifiedText);
        	}
        }
        else if (action.equals("Sequence")) {
        	String text = textPane.getText();
        	if(text != "") {
	        	String modifiedText = "";
	        	int count = 1;
	        	String textLines[] = text.split("\n");
	        	for(String line : textLines) {
	        		modifiedText += count++ + ". " + line.trim() + "\n";      		
	        	}
	        	textPane.setText(modifiedText);
        	}
        }
        else if(action.equals("Prepend")) {
        	String text = textPane.getText();
        	if(text != "") {
	        	String modifiedText = "";
	        	String prepend = JOptionPane.showInputDialog(this, "What to append on each line !");
	        	if(prepend != null) {
		        	String textLines[] = text.split("\n");
		        	for(String line : textLines) {
		        		modifiedText += prepend + line + "\n";      		
		        	}
		        	textPane.setText(modifiedText);
	        	}
        	} 
        }
        else if(action.equals("Append")) {
        	String text = textPane.getText();
        	if(text != "") {
	        	String modifiedText = "";
	        	String append = JOptionPane.showInputDialog(this, "What to append on each line !");
	        	if(append != null) {
		        	String textLines[] = text.split("\n");
		        	for(String line : textLines) {
		        		modifiedText += line + append + "\n";      		
		        	}
		        	textPane.setText(modifiedText);
	        	}
        	}        
        }
        else if(action.equals("Translate")) {
        	translate();
        }
        // shuffle (character, line)
        // grammar (shayad)

    }
    
    private void speak(){
    	try {
			// set path for kevin voice directory
	    	System.setProperty("freetts.voices", "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory");
			
	    	VoiceManager vm;
			vm = VoiceManager.getInstance();
			Voice voice = vm.getVoice("kevin16");
			voice.allocate(); // allocate the voice
			
			// setting voice characteristics
	        voice.setRate(140);
	        voice.setPitch(130);
	        voice.setVolume(5);
	        // speak the text
	        voice.speak(textPane.getText());
    	}
    	catch(Exception e) {
    		JOptionPane.showMessageDialog(this, "Error !" + e);
    	}
    }
    
    private void textRecognition(){
    	try {
	    	Tesseract obj = new Tesseract();
	    	obj.setDatapath("C:\\Users\\saras\\Downloads\\Tess4J-3.4.8-src\\Tess4J\\tessdata");
	    	
	    	// choose image
	    	JFileChooser imgFileChooser= new JFileChooser();
	    	imgFileChooser.setCurrentDirectory(new File("."));  // for current directory
	    	
			if(imgFileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
		    	try {
		    		textPane.setText(textPane.getText() + obj.doOCR(new File(imgFileChooser.getSelectedFile().getAbsolutePath())));
		    	}
		    	catch(TesseractException e){
	        		JOptionPane.showMessageDialog(this,"Not a valid file !");
	        	}
				
			}
    	}
    	catch(Exception e) {
    		JOptionPane.showMessageDialog(this, "Error !" + e);
    	}
    }
    
    private void translate() {
        try {
        	String langJson = "[{\"code\": \"en\", \"name\": \"English\"}, {\"code\": \"ar\", \"name\": \"Arabic\"},"
        			+ " {\"code\": \"az\", \"name\": \"Azerbaijani\"}, {\"code\": \"zh\", \"name\": \"Chinese\"},"
        			+ " {\"code\": \"cs\", \"name\": \"Czech\"}, {\"code\": \"nl\", \"name\": \"Dutch\"},"
        			+ " {\"code\": \"eo\", \"name\": \"Esperanto\"}, {\"code\": \"fi\", \"name\": \"Finnish\"},"
        			+ " {\"code\": \"fr\", \"name\": \"French\"}, {\"code\": \"de\", \"name\": \"German\"},"
        			+ " {\"code\": \"el\", \"name\": \"Greek\"}, {\"code\": \"hi\", \"name\": \"Hindi\"},"
        			+ " {\"code\": \"hu\", \"name\": \"Hungarian\"}, {\"code\": \"id\", \"name\": \"Indonesian\"},"
        			+ " {\"code\": \"ga\", \"name\": \"Irish\"}, {\"code\": \"it\", \"name\": \"Italian\"},"
        			+ " {\"code\": \"ja\", \"name\": \"Japanese\"}, {\"code\": \"fa\", \"name\": \"Persian\"},"
        			+ " {\"code\": \"pl\", \"name\": \"Polish\"}, {\"code\": \"pt\", \"name\": \"Portuguese\"},"
        			+ " {\"code\": \"ru\", \"name\": \"Russian\"}, {\"code\": \"sk\", \"name\": \"Slovak\"},"
        			+ " {\"code\": \"es\", \"name\": \"Spanish\"}, {\"code\": \"sv\", \"name\": \"Swedish\"},"
        			+ " {\"code\": \"tr\", \"name\": \"Turkish\"}, {\"code\": \"uk\", \"name\": \"Ukranian\"},"
        			+ "{\"code\": \"ko\", \"name\": \"Korean\"}, {\"code\": \"vi\", \"name\": \"Vietnamese\"}]"; 
        	
	    	JSONArray arr = (JSONArray) new JSONTokener(langJson).nextValue();
	    	String langOptions[] = new String[28]; // total 28ow language
	    	JSONObject item;
	    	for(int i = 0; i < 28; i++) {
	    		item = arr.getJSONObject(i);
	    		langOptions[i] = item.getString("name") + "-" + item.getString("code");
	    	}
	    	
        	String lang = (String) JOptionPane.showInputDialog(this, "Choose language to translate in ", "Translate", JOptionPane.QUESTION_MESSAGE,
        			null, langOptions, "English-en");
            String url = "https://libretranslate.de/translate";
            String requestJson = String.format("{\"q\": \"%s\", \"source\": \"auto\", \"target\": \"%s\"}", textPane.getText(), lang.split("-")[1]);
            
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            
            // setting properties
            con.setConnectTimeout(5000);
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json"); // for setting header
            con.setDoOutput(true);
            con.setDoInput(true);

            // sending jsonRequest to server
            OutputStream os = con.getOutputStream();
            os.write(requestJson.getBytes("utf-8"));
            os.close();
            
            // for response
            InputStream in = new BufferedInputStream(con.getInputStream());
            String response = IOUtils.toString(in, "UTF-8");
            
            System.out.println(response);
            
            JSONObject responseJson = new JSONObject(response);
            textPane.setText(responseJson.getString("translatedText"));

            in.close();
            con.disconnect();
        }
            
        catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error !");
            // System.out.println(e);
        }
    }
}
