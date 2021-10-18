package http.server;

import java.io.PrintWriter;

public class ReturnCode {
	
	public static boolean sendHeader(String returnCode,PrintWriter out) {
		String buffer="";
		switch(returnCode) {
		case "200":
			out.println("HTTP/1.0 200 OK");
		    out.println("Connection: keep-alive");
		    out.println("Content-Type: text/html");
		    out.println("Transfer-Encoding: chunked");
		    out.println("Server: Bot");
		    return true;
		case "400":
			out.println("HTTP/1.0 400 Bad Request");
			return false;
		case "404":
			out.println("HTTP/1.0 404 Not Found");
		default:
			return false;
		}
	}
}
