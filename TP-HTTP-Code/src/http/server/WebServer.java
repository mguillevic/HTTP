///A Simple Web Server (WebServer.java)

package http.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
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
      try {
        // wait for a connection
        Socket remote = s.accept();
        // remote is now the connected socket
        System.out.println("Connection, sending data.");
        BufferedReader in = new BufferedReader(new InputStreamReader(
            remote.getInputStream()));
        PrintWriter out = new PrintWriter(remote.getOutputStream());

        // read the data sent. We basically ignore it,
        // stop reading once a blank line is hit. This
        // blank line signals the end of the client HTTP
        // headers.
        String str = ".";
        str = in.readLine();
        String [] line = str.split(" ");
        switch(line[0]){
        	case ("GET"):
        		doGet(line[1],out,remote);
        		break;
        	case ("POST"):
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
	        	doPost(reponses,out);        		
        		break;
        	case ("HEAD"):
	        	doHead(out);        		
        		break;
        	default:
        		break;        		
        }
        remote.close();
      } catch (Exception e) {
        System.out.println("Error: " + e);
      }
    }
  }
  
  private String getFileAsString(String ressource) throws IOException{
	  File file = new File("doc"+ressource);
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
	  File file = new File("doc"+ressource);
	  byte[] bytes = Files.readAllBytes(file.toPath());	  
	  return bytes;
  }
  
	

  public void doGet(String ressource, PrintWriter out, Socket remote) throws IOException{
		// Send the headers
      out.println("HTTP/1.0 200 OK");
      out.println("Connection: keep-alive");
      File file = new File("doc/"+ressource);
      String format = Files.probeContentType(file.toPath());
      out.println("Content-Type: "+format);
      out.println("Transfer-Encoding: chunked");
      out.println("Server: Bot");
      // this blank line signals the end of the headers
      out.println("");
      out.flush();
      // Send the HTML page
      remote.getOutputStream().write(getFileAsBytes(ressource));
     
      remote.getOutputStream().flush();
		
	}
  
  public void doHead(PrintWriter out){
		// Send the headers
	    out.println("HTTP/1.0 200 OK");
	    out.println("Connection: keep-alive");
	    out.println("Content-Type: text/html");
	    out.println("Transfer-Encoding: chunked");
	    out.println("Server: Bot");
	    // this blank line signals the end of the headers
	    out.println("");  	
	    
	    out.flush();
	}
  
  public void doPost(LinkedList<String> reponses, PrintWriter out){
		// Send the headers
    out.println("HTTP/1.0 200 OK");
    out.println("Connection: keep-alive");
    out.println("Content-Type: text/html");
    out.println("Transfer-Encoding: chunked");
    out.println("Server: Bot");
    // this blank line signals the end of the headers
    out.println("");
    
    // Affiche les données recues
    System.out.println(reponses);
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
