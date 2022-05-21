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
// Modules for file handling
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;

// Modules for textRecognition
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
    private JPanel textContainer, allButtonContainer, buttonUtilityContainer, buttonBasicContainer;
	private JButton lowerCaseButton, upperCaseButton, capitalizeButton, sequenceButton, replaceButton, 
    	importButton, exportButton, appendButton, prependButton, removeSpaceButtton, textRecognitionButton, 
    	translateButton ,themeButton, copyButton, clearButton, readButton;

    mainFrame(){
    	
    	// Text area related stuff
        textContainer = new JPanel();
        textPane = new JTextPane();
        textPane.setText("Put your text here...");
        // ** required this font otherwise other language won\"t work **
        textPane.setFont(new Font("Nirmala UI", Font.PLAIN, 14)); 
        JScrollPane scroll = new JScrollPane( textPane );
        scroll.setVerticalScrollBarPolicy( ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS );
        textContainer.setLayout(new BorderLayout());
        textContainer.add(scroll);


        // All buttons container
        allButtonContainer = new JPanel();

        // Button Utility container
        buttonUtilityContainer = new JPanel();
        lowerCaseButton = new JButton("Lower");
        upperCaseButton = new JButton("Upper");
        capitalizeButton = new JButton("Capitalize");
        sequenceButton = new JButton("Sequence");
        appendButton = new JButton("Append");
        prependButton = new JButton("Prepend");
        removeSpaceButtton = new JButton("Remove Spaces");
        textRecognitionButton = new JButton("Text Recognition");
        translateButton = new JButton("Translate");
        replaceButton = new JButton("Replace");
        importButton = new JButton("Import");
        exportButton = new JButton("Export");
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
        replaceButton.addActionListener(this); 
        importButton.addActionListener(this);
        exportButton.addActionListener(this);
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
        buttonUtilityContainer.add(replaceButton);
        buttonUtilityContainer.add(importButton);
        buttonUtilityContainer.add(exportButton);
        
        buttonUtilityContainer.setLayout(new GridLayout(12, 1, 0, 5));
        
        // Button basic container
        buttonBasicContainer = new JPanel();
        themeButton = new JButton("Theme");
        copyButton = new JButton("Copy");
        readButton = new JButton("Read");
        clearButton = new JButton("Clear");
        // adding functionalities to buttonBasicContainer
        themeButton.addActionListener(this);
        copyButton.addActionListener(this);
        readButton.addActionListener(this);
        clearButton.addActionListener(this);
        // adding elements to buttonBasicContainer
        buttonBasicContainer.add(themeButton);
        buttonBasicContainer.add(copyButton);
        buttonBasicContainer.add(readButton);
        buttonBasicContainer.add(clearButton);
        buttonBasicContainer.setLayout(new GridLayout(4, 1, 0, 5));
        
   
        // adding all buttons section to button container
        allButtonContainer.setLayout(new BorderLayout());
        allButtonContainer.add(buttonUtilityContainer, BorderLayout.NORTH);
        allButtonContainer.add(buttonBasicContainer, BorderLayout.SOUTH);

        
        // Adding all container to Mainframe
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
        
        // basic button implementations
        if(action.equals("Theme")) {
        	setTheme();
        }
        else if (action.equals("Clear")) {
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
        // utility button implementation
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
        else if (action.equals("Replace")){
            String text = textPane.getText();
            // new Panel for asking multiple input in Input box
            JPanel replacePanel = new JPanel(new GridLayout(4, 3));
            JLabel findLabel = new JLabel("Find :");
            JTextField find = new JTextField(10);
            JLabel replaceLabel = new JLabel("Replace :");
            JTextField replace = new JTextField(10);
            replacePanel.add(findLabel);
            replacePanel.add(find);
            replacePanel.add(replaceLabel);
            replacePanel.add(replace);
            JOptionPane.showMessageDialog(this, replacePanel);
            String modifiedText = text.replaceAll(find.getText(), replace.getText());
            textPane.setText(modifiedText);
        }
        else if(action.equals("Import")) {
	    	// choose file
	    	JFileChooser fileChooser = new JFileChooser();
	    	fileChooser.setCurrentDirectory(new File("."));  // for current directory
	    	
			if(fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
		    	try {
		    		File file = new File(fileChooser.getSelectedFile().getAbsolutePath());
		    		String newText = "", line = "";
		    		
		    		FileReader fileReader = new FileReader(file);
		    		BufferedReader bufferedReader = new BufferedReader(fileReader);
		    		
		    		line = bufferedReader.readLine();
		    		
		    		while((line = bufferedReader.readLine()) != null) {
		    			newText = newText + "\n" + line;
		    		}
		    		textPane.setText(textPane.getText() + newText);
		    	}
		    	catch(Exception e){
	        		JOptionPane.showMessageDialog(this,"Not a valid file !");
	        	}
			}
        }        
        else if(action.equals("Export")) {
	    	// choose file
	    	JFileChooser fileChooser = new JFileChooser();
	    	fileChooser.setCurrentDirectory(new File("."));  // for current directory
	    	
			if(fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
		    	try {
		    		File file1 = new File(fileChooser.getSelectedFile().getAbsolutePath());
		    		
		    		FileWriter fileWriter = new FileWriter(file1, false);
		    		BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
		    		
		    		bufferedWriter.write(textPane.getText()); 
		    		bufferedWriter.flush();
		    		bufferedWriter.close(); 
		    	}
		    	catch(Exception e){
	        		JOptionPane.showMessageDialog(this,"Some error occured !");
	        	}
			}
        }
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

            if(lang != null){
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
        }
            
        catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error !");
            // System.out.println(e);
        }
    }
   
    private void setTheme() {
    	// By DEFAULT Light Blue
    	Color bgContainerColor = new Color(239, 255, 253), bgTextPaneColor = new Color(239, 255, 253),
    			bgButtonColor = new Color(133, 244, 255);
    	Color fgTextPaneColor = new Color(44, 51, 51), fgButtonColor = new Color(44, 51, 51);
    	
    	String themeOptions[] = {"Light Blue", "Light Pink", "Light Green", "Dark Blue", "Dark Red", "Dark Green"};
    	String theme = (String) JOptionPane.showInputDialog(this, "Choose Theme ", "Theme Selection", JOptionPane.QUESTION_MESSAGE,
    			null, themeOptions, "Light");
    	if(theme == null) {return;}
    	else if(theme.equals("Light Blue")){	
    		bgContainerColor = new Color(239, 255, 253);
    		bgTextPaneColor = new Color(239, 255, 253);
    		bgButtonColor = new Color(133, 244, 255);
    		fgTextPaneColor = new Color(44, 51, 51);
    		fgButtonColor = new Color(44, 51, 51);
    	}
    	else if (theme.equals("Light Pink")) {
    		bgContainerColor = new Color(254, 227, 236);
    		bgTextPaneColor = new Color(249, 197, 213);
    		bgButtonColor = new Color(242, 120, 159);
    		fgTextPaneColor = new Color(44, 51, 51);
    		fgButtonColor = new Color(44, 51, 51);
    	}
    	else if (theme.equals("Light Green")) {
    		bgContainerColor = new Color(184, 241, 176);
    		bgTextPaneColor = new Color(227, 252, 191);
    		bgButtonColor = new Color(20, 195, 142);
    		fgTextPaneColor = new Color(44, 51, 51);
    		fgButtonColor = new Color(44, 51, 51);
    	}
    	else if (theme.equals("Dark Blue")) {
    		bgContainerColor = new Color(81, 85, 133);
    		bgTextPaneColor = new Color(50, 64, 123);
    		bgButtonColor = new Color(21, 25, 101);
    		fgTextPaneColor = new Color(238, 238, 238);
    		fgButtonColor = new Color(238, 238, 238);
    	}
    	else if (theme.equals("Dark Red")) {
    		bgContainerColor = new Color(152, 50, 90);
    		bgTextPaneColor = new Color(87, 5, 48);
    		bgButtonColor = new Color(76, 0, 39);
    		fgTextPaneColor = new Color(238, 238, 238);
    		fgButtonColor = new Color(238, 238, 238);
    	}
    	else if (theme.equals("Dark Green")) {
    		bgContainerColor = new Color(58, 145, 136);
    		bgTextPaneColor = new Color(4, 74, 66);
    		bgButtonColor = new Color(6, 41, 37);
    		fgTextPaneColor = new Color(238, 238, 238);
    		fgButtonColor = new Color(238, 238, 238);
    	}
    	
    	// textPane
    	textPane.setBackground(bgTextPaneColor);
    	textPane.setForeground(fgTextPaneColor);
    	
    	// Containers 
		allButtonContainer.setBackground(bgContainerColor);
		buttonUtilityContainer.setBackground(bgContainerColor);
		buttonBasicContainer.setBackground(bgContainerColor);
		
		// Buttons
		lowerCaseButton.setBackground(bgButtonColor);
		upperCaseButton.setBackground(bgButtonColor);
		capitalizeButton.setBackground(bgButtonColor);
		sequenceButton.setBackground(bgButtonColor);
		appendButton.setBackground(bgButtonColor);
		prependButton.setBackground(bgButtonColor);
		removeSpaceButtton.setBackground(bgButtonColor);
		textRecognitionButton.setBackground(bgButtonColor);
		translateButton.setBackground(bgButtonColor);
		themeButton.setBackground(bgButtonColor);
		copyButton.setBackground(bgButtonColor);
		clearButton.setBackground(bgButtonColor);
		readButton.setBackground(bgButtonColor);
		replaceButton.setBackground(bgButtonColor);
		replaceButton.setBackground(bgButtonColor);
		importButton.setBackground(bgButtonColor);
		exportButton.setBackground(bgButtonColor);
		
		// setting foreground of buttons
		lowerCaseButton.setForeground(fgButtonColor);
		upperCaseButton.setForeground(fgButtonColor);
		capitalizeButton.setForeground(fgButtonColor);
		sequenceButton.setForeground(fgButtonColor);
		appendButton.setForeground(fgButtonColor);
		prependButton.setForeground(fgButtonColor);
		removeSpaceButtton.setForeground(fgButtonColor);
		textRecognitionButton.setForeground(fgButtonColor);
		translateButton.setForeground(fgButtonColor);
		themeButton.setForeground(fgButtonColor);
		copyButton.setForeground(fgButtonColor);
		clearButton.setForeground(fgButtonColor);
		readButton.setForeground(fgButtonColor);
		replaceButton.setForeground(fgButtonColor);
		importButton.setForeground(fgButtonColor);
		exportButton.setForeground(fgButtonColor);

    }
}
