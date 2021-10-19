package http.server;

import java.io.PrintWriter;

public class ReturnCode {
	
	public static void sendHeader(String returnCode,PrintWriter out,String content_type) {
		switch(returnCode) {
		case "200":
			out.println("HTTP/1.0 200 OK");
		    out.println("Connection: keep-alive");
		    out.println("Content-Type: "+content_type);
		    out.println("Transfer-Encoding: chunked");
		    out.println("Server: Bot");
		    break;
		case "201":
			out.println("HTTP/1.0 201 Created");
		    out.println("Connection: keep-alive");
		    out.println("Content-Type: "+content_type);
		    out.println("Transfer-Encoding: chunked");
		    out.println("Server: Bot");
		case "400":
			out.println("HTTP/1.0 400 Bad Request");
			break;
			
		case "403":
			out.println("HTTP/1.0 403 Forbidden");
		case "404":
			out.println("HTTP/1.0 404 Not Found");
			break;
			
		case "415":
			out.println("HTTP/1.0 415 Unsupported Media Type");
		
		case "500":
			out.println("HTTP/1.0 500 Internal Server Error");
			break;
			
		case "501":
			out.println("HTTP/1.0 501 Not Implemented");
			break;
		default:
		}
		out.println("");
		out.flush();
	}
}
