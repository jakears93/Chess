import java.util.Scanner;
import java.util.concurrent.*;
import java.net.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.util.Date;



class GameServer {
	public final static int PORT = 40051;
	public static int clientNum = 1;

//Main Function
	public static void main(String args[]){
		ExecutorService pool = Executors.newFixedThreadPool(3);
		while(true){
			try (ServerSocket server = new ServerSocket(PORT)){
				Socket connection = server.accept();
				InetAddress inet = connection.getInetAddress();
				String hostname = inet.getHostName();
				int clientPort = connection.getPort();
				String identifier = "Client "+clientNum+" "+hostname+" on port "+clientPort;
				clientNum++;
				Callable<Void> task = new TCPServerTask(connection,identifier);
				pool.submit(task);	
			} catch (IOException ex){
				System.out.println("Couldn't start server.");
			}
		}
	}//End of Main

	private static class TCPServerTask implements Callable<Void>{
		private Socket connection;
		private String identifier;
	
		TCPServerTask(Socket connection, String identifier){
			this.connection = connection;
			this.identifier = identifier;
		}
	//Stub Functions
		String count(String[] args) {
			int count = args.length-1 > 0 ? args.length-1 : 0;
			String returnValue = count+" words in the command line."; 
			return returnValue;
		}// End of Count
		
		String file(String[] args) {
			String returnValue = "File: "+args[1]; 
			//Get File
			try{
				File file = new File(args[1]);
				//Check if it exists
				if(file.exists()){
					returnValue+="\tSize: ";
					long fileSize = file.length();
					returnValue+=String.valueOf(fileSize);
					try{
						BasicFileAttributes attrs;
						attrs = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
						FileTime time = attrs.creationTime();
	
						String datePattern = "yyy-MM-dd HH:mm:ss";
						SimpleDateFormat smf = new SimpleDateFormat(datePattern);
						String creation = smf.format(new Date(time.toMillis()));
						
						returnValue+="\tCreated On: ";
						returnValue+=creation;
					} catch (IOException e){
						e.printStackTrace();
					}	
				}
				else{
					returnValue="";
					File currentDir = new File(".");
					File[] filesList = currentDir.listFiles();
					for(File f : filesList){
						returnValue+=f.getName()+"\t";
					}
				}
			} catch (Exception  e){
				e.printStackTrace();
			}
			return returnValue;
		}//End of file

		String dict(String[] args) {
			String returnValue = "";
			try{ 
			String command = "dict "+args[1];
			Process process = Runtime.getRuntime().exec(command);

			BufferedReader procInput = new BufferedReader(new 
     			InputStreamReader(process.getInputStream()));

			BufferedReader procError = new BufferedReader(new 
     			InputStreamReader(process.getErrorStream()));

			// Read the output from the command
			String s = null;
			while ((s = procInput.readLine()) != null) {
    				returnValue+=s;
			}

			// Read any errors from the attempted command
			while ((s = procError.readLine()) != null) {
    				returnValue+=s;
			}
			} catch(IOException e){
				e.printStackTrace();
			}
			return returnValue;
		}//End of dict

		String convert(String[] args){
			BufferedReader in = null;
			PrintWriter out = null; 
        		Socket socket = null;
			String toConvert = "";
			String conversion = "";
			for(int i=1; i<args.length; i++){
				toConvert+=args[i]+" ";
			}
        
        		try {
				System.out.println("Making Socket");
            			socket = new Socket("localhost", 40151); 
           			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            			out = new PrintWriter(socket.getOutputStream(), true); 
			
				System.out.println("Socket Made");
				System.out.println("To Convert: "+toConvert);	
				out.println(toConvert);
				out.flush();
				conversion = in.readLine();
        		} catch (IOException ex) {
            			System.err.println(ex);
        		} finally {
                		try {
					System.out.println("Closing Local Server Client");
					in.close();
					out.close();
                	    		socket.close();
					
                		} catch (IOException ex) {
                		      	ex.printStackTrace();
                		}
            		}	
			return (identifier+": "+conversion);
		}//End of convert
	
		@Override 
		public Void call() throws IOException{	
			BufferedReader input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			PrintWriter out = new PrintWriter(connection.getOutputStream(), true);
			Boolean continueLoop = true;
			
			System.out.println("Connection made for "+identifier);
			out.println("Connection made for "+identifier);
			
			try{
				while(connection.isConnected() && continueLoop) {
					out.println("Enter a Command: ");
					String commandLine = input.readLine();
					String[] commands = commandLine.split(" ");
					
					if(commands[0].equals("count")){
						out.println(identifier+": "+count(commands));
					}
					else if(commands[0].equals("file")){
						out.println(identifier+": "+file(commands));
					}
					else if(commands[0].equals("dict")){
						out.println(identifier+": "+dict(commands));
					}
					else if(commands[0].equals("convert")){
						out.println(identifier+": "+convert(commands));
					}
					else if(commands[0].equals("exit")){
						System.out.println(identifier+" has sent an exit code.");
						continueLoop=false;
					}
					else{
						out.println("Invalid Command");
					}			
				}
			}
			catch(IOException ex){

			} finally {
				connection.close();
				input.close();
				out.close();
				System.out.println(identifier+" has disconnected.\n"); 
				return null;
			}
		}
	}//end of TCPServerTask Class
}//end of TCPServer Class
