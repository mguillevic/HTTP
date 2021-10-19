///A Simple Web Server (WebServer.java)

package http.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.util.LinkedList;

/**
 * Example program from Chapter 1 Programming Spiders, Bots and Aggregators in
 * Java Copyright 2001 by Jeff Heaton
 * 
 * WebServer is a very simple web-server. Any request is responded with a very
 * simple web-page.
 * 
 * @author Jeff Heaton
 * @version 1.0
 */
public class WebServer {
	
	protected PrintWriter out;
  /**
   * WebServer constructor.
   */
  protected void start() {
    ServerSocket s;

    System.out.println("Webserver starting up on port 80");
    System.out.println("(press ctrl-c to exit)");
    try {
      // create the main server socket
      s = new ServerSocket(3000);
    } catch (Exception e) {
      System.out.println("Error: " + e);
      return;
    }

    System.out.println("Waiting for connection");
    for (;;) {
    	out = null;
      try {
        // wait for a connection
        Socket remote = s.accept();
        // remote is now the connected socket
        System.out.println("Connection, sending data.");
        BufferedReader in = new BufferedReader(new InputStreamReader(
            remote.getInputStream()));
        out = new PrintWriter(remote.getOutputStream());

        // read the data sent. We basically ignore it,
        // stop reading once a blank line is hit. This
        // blank line signals the end of the client HTTP
        // headers.
        String str = ".";
        str = in.readLine();
        String [] line = str.split(" ");
        switch(line[0]){
        	case ("GET"):
        		doGet(line[1],remote);
        		break;
        	case ("POST"):
        		doPost(in);
        		break;
        		
        	case ("PUT"):
        		doPut(line[1],in);
        		break;
        		
        	case ("HEAD"):
	        	doHead(line[1]);        		
        		break;
        		
        	case ("DELETE"):
        		doDelete(line[1]);
        		break;
        	default:
        		ReturnCode.sendHeader("501", out);
        		break;        		
        }
        remote.close();
      } catch (Exception e) {
    	  ReturnCode.sendHeader("500", out);
    	  System.out.println("Error: " + e);
      }
    }
  }

private void doPut(String fileName, BufferedReader in) {
	if(!fileName.equals("/privatePage.html")) {
		try {
			String ligne=".";
			while(ligne!=null && !ligne.equals("")) {
				ligne=in.readLine();    //We ignore the headers
			}
			
			String buffer="";
			ligne=".";
			while(ligne!=null && !ligne.equals("")) {
				ligne=in.readLine();  //We copy the request body
				buffer+=ligne;
			}
			FileWriter writer = new FileWriter("doc/"+fileName);
			writer.write(buffer);
			writer.close();
			
			ReturnCode.sendHeader("201",out);
		}catch(IOException ioe) {
			ioe.printStackTrace();
			ReturnCode.sendHeader("500", out);
		}
	}else {
		ReturnCode.sendHeader("403", out);
	}
}
  
   private String getFileAsString(String ressource) throws IOException{
	  File file = new File(ressource);
	  System.out.println(file.toPath());
	  String buffer="";
	  String ligne;
	  try{
		  BufferedReader reader = new BufferedReader(new FileReader(file));
		  while ((ligne = reader.readLine()) != null) {
	    		buffer+=ligne;  //We add every line of the file to the buffer
	    	}
	  	    reader.close();
	  }catch(FileNotFoundException exc){
		  exc.printStackTrace();
	  }catch(IOException ioe){
			ioe.printStackTrace();
		}
	  return buffer;
  }
  
  private byte[] getFileAsBytes(String ressource) throws IOException{
	  File file = new File(ressource);
	  byte[] bytes = Files.readAllBytes(file.toPath());	  
	  return bytes;
  }
  
	

  public void doGet(String ressource) throws IOException{
	  
	  if(ressource.equals("/privatePage.html")) {
		  ReturnCode.sendHeader("403", out);
	  }else {
		  File file = new File("doc/"+ressource);
		  if(file.exists()) {
			  ReturnCode.sendHeader("200", out);
		    String format = Files.probeContentType(file.toPath());
        out.println("Content-Type: "+format);
		    out.flush();
        remote.getOutputStream().write(getFileAsBytes(ressource));
        remote.getOutputStream().flush();
		  }else {
			  ReturnCode.sendHeader("404", out);
		  }
	  }
  
  public void doHead(String ressource){
	  if(ressource.equals("/privatePage.html")) {
		  ReturnCode.sendHeader("403", out);
	  }else {
		  File file = new File("doc/"+ressource);
		  if(file.exists()) {
		  	ReturnCode.sendHeader("200", out);
		  }else {
		  	ReturnCode.sendHeader("404", out);
		  }
	  }
	    out.flush();
	}
  
  public void doPost(BufferedReader in){
	  String str=".";
	  try {
		  while(str!=null && !str.equals("")) {
				str=in.readLine();
	  		System.out.println(str);
			}
			str=in.readLine();//Boundary
			
			LinkedList<String> reponses=new LinkedList<String>();
	  	while(str.charAt(str.length()-1)!='-') {
				str=in.readLine();//nom
				reponses.add(str);
				str=in.readLine();//vide
				str=in.readLine();//Marie
				reponses.add(str);
				str=in.readLine();//boundary
			}
	  	System.out.println(reponses);
	  	ReturnCode.sendHeader("200", out);
	  }catch(IOException e) {
		  e.printStackTrace();
		  ReturnCode.sendHeader("500", out);
	  }
      out.flush();
		
	}
  
  private void doDelete(String ressource) {
	  if(ressource.equals("/privatePage.html")) {
		  ReturnCode.sendHeader("403", out);
	  }else {
		  File file = new File("doc/"+ressource);
		  if(file.exists()) {
			if(file.delete()) {
				ReturnCode.sendHeader("200", out);
			}else {
				ReturnCode.sendHeader("500", out);
			}
		  }else {
			  ReturnCode.sendHeader("404", out);
		  }
	  }
	  out.flush();
  	}
  
  public String setType(String format,String path) throws IOException {
	  
	 String type = format.split("/")[0];
	  switch(type){
	  case ("image"):
		  System.out.println(path);
		  return "<img src="+path+" alt=\"image\"/>";
	  case("text"):
		  return getFileAsString(path);
	  default:
		  return null;
	  }
	  
  }
  


  /**
   * Start the application.
   * 
   * @param args
   *            Command line parameters are not used.
   */
  public static void main(String args[]) {
    WebServer ws = new WebServer();
    ws.start();
  }
}
