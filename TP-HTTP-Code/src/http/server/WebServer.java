///A Simple Web Server (WebServer.java)

package http.server;

import java.io.BufferedReader;
import java.io.File;
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

    System.out.println("Webserver starting up on port 3000");
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

        // reads the data sent. launches appropriate method depending on the request type
        String str = ".";
        str = in.readLine();
        String [] line = str.split(" ");
        switch(line[0]){
        	case ("GET"):
        		doGet(line[1],remote);   //line[1] is the name of the requested ressource
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
        		
        	case("CONNECT"):
        		ReturnCode.sendHeader("501", out,null);  //Not implemented
        		break;
        	
        	case("OPTIONS"):
        		ReturnCode.sendHeader("501", out, null);
        		break;
        	
        	case("TRACE"):
        		ReturnCode.sendHeader("501", out, null);
        		break;
        	
        	case("PATCH"):
        		ReturnCode.sendHeader("501", out, null);
        		break;
        		
        	default:
        		ReturnCode.sendHeader("400", out,null);  //Bad Request
        		break;        		
        }
        remote.close();
      } catch (Exception e) {
    	  ReturnCode.sendHeader("500", out,null);  //Internal error
    	  System.out.println("Error: " + e);
      }
    }
  }

private void doPut(String fileName, BufferedReader in) {
	if(!fileName.equals("/privatePage.html")) {
		try {
			String ligne=".";
			while(ligne!=null && !ligne.equals("")) {
				ligne=in.readLine();
				//We ignore the headers
			}
			File file = new File("doc/"+fileName);
			FileWriter writer = new FileWriter(file);
			String format = Files.probeContentType(file.toPath());
			ReturnCode.sendHeader("201",out,format);
			ligne=".";
			String buffer="";
			ligne=in.readLine();  
			while(ligne!=null && !ligne.equals("")) {
				buffer+=ligne;
				ligne=in.readLine();    //We copy the request body
				
			}
			writer.write(buffer);     //Then write it into a file
			writer.close();
		}catch(IOException ioe) {
			ioe.printStackTrace();
			ReturnCode.sendHeader("500", out,null);
		}
	}else {
		ReturnCode.sendHeader("403", out,null);  //Forbidden
	}
}
  
  private byte[] getFileAsBytes(String ressource) throws IOException{
	  File file = new File(ressource);
	  byte[] bytes = Files.readAllBytes(file.toPath());	  
	  return bytes;
  }
  
  
  public void doGet(String ressource, Socket remote) throws IOException{
	  
	  if(ressource.equals("/privatePage.html")) {
		  ReturnCode.sendHeader("403", out, null);
	  }else {
		  File file = new File("doc/"+ressource);
		  if(file.exists()) {
		    String format = Files.probeContentType(file.toPath());
		    ReturnCode.sendHeader("200", out,format);
		    remote.getOutputStream().write(getFileAsBytes("doc/"+ressource));
		    remote.getOutputStream().flush();
		  }else {
			  ReturnCode.sendHeader("404", out,null);
		  }
	  }
  }
  
  public void doHead(String ressource){
	  if(ressource.equals("/privatePage.html")) {
		  ReturnCode.sendHeader("403", out,null);
	  }else {
		  File file = new File("doc/"+ressource);
		  if(file.exists()) {
			String format;
			try {
				format = Files.probeContentType(file.toPath());
				ReturnCode.sendHeader("200", out,format);
			} catch (IOException e) {
				e.printStackTrace();
				ReturnCode.sendHeader("500", out,null);
			}
		  }else {
		  	ReturnCode.sendHeader("404", out,null);
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
	  	while(str.charAt(str.length()-1)!='-') {  //Reads the request body
				str=in.readLine();//key
				reponses.add(str);
				str=in.readLine();//empty line
				str=in.readLine();//value
				reponses.add(str);
				str=in.readLine();//boundary
			}
	  	System.out.println(reponses);
	  	ReturnCode.sendHeader("200", out,null);
	  }catch(IOException e) {
		  e.printStackTrace();
		  ReturnCode.sendHeader("500", out,null);
	  }
      out.flush();
		
	}
  
  private void doDelete(String ressource) {
	  if(ressource.equals("/privatePage.html")) {
		  ReturnCode.sendHeader("403", out,null);
	  }else {
		  File file = new File("doc/"+ressource);
		  if(file.exists()) {
			if(file.delete()) {
				ReturnCode.sendHeader("200", out,null);
			}else {
				ReturnCode.sendHeader("500", out,null);
			}
		  }else {
			  ReturnCode.sendHeader("404", out,null);
		  }
	  }
	  out.flush();
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
