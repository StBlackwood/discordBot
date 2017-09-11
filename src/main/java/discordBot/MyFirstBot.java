package discordBot;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import de.btobastian.javacord.DiscordAPI;
import de.btobastian.javacord.Javacord;
import de.btobastian.javacord.entities.User;
import de.btobastian.javacord.entities.message.Message;
import de.btobastian.javacord.entities.message.MessageAttachment;
import de.btobastian.javacord.entities.message.embed.EmbedBuilder;
import de.btobastian.javacord.listener.message.MessageCreateListener;

public class MyFirstBot {

	public static void main(String[] args) {
		
		String token="Mjg0MDE3Mzk0MDg5ODUyOTI5.C496dA.pX8n-xI6yBkXc9XyDiL7hlM6_Nc";
		//String token="Mjg0MzkyNDQzNjExNDQ3Mjk4.C5C8ow.K38EQ8BjbF0VgHzTvvwSWUr3UVg";
		DiscordAPI api = Javacord.getApi(token, true);
		System.setProperty("http.proxyHost", "172.16.2.30");
		System.setProperty("http.proxyPort", "8080");
		 System.setProperty("https.proxyHost","172.16.2.30");
		 System.setProperty("https.proxyPort","8080");		
		api.connectBlocking();
		api.registerListener(new MessageCreateListener(){
			public void onMessageCreate(DiscordAPI api, Message message) {
				Collection<MessageAttachment> attachments =message.getAttachments();
				
				for(MessageAttachment a:attachments){
					URL url=a.getUrl();
					if(isVideo(a.getFileName())){
						String filename="/home/blackwood/Documents/Coding/sample/"+a.getFileName();
						try {
							downloadUsingStream(url.toString(),filename);
						} catch (IOException e) {
							
							e.printStackTrace();
						}
						String comm="youtube-upload --title=\""+a.getFileName()+"\" "+filename;
						String youtube="https://www.youtube.com/watch?v=";
						List<String> val=runCommand(comm);
						
						{
							try {
								TimeUnit.SECONDS.sleep(25);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							message.reply(youtube+val.get(val.size()-1));
						}
						
					}				
				}
				
				if(message.getContent().contains("/gif")){
					message.reply(getGifUrl(message));
					message.delete();

				}
				
				
            }
		});
	}
	
	public static void print(String f){
		System.out.println(f);
	}
	 private static void downloadUsingStream(String urlStr, String file) throws IOException{
	        URL url = new URL(urlStr);
	        HttpURLConnection httpcon = (HttpURLConnection) url.openConnection(); 
	        httpcon.addRequestProperty("User-Agent", "Mozilla/4.76"); 
	        BufferedInputStream bis = new BufferedInputStream(httpcon.getInputStream(),4*1024);
	        FileOutputStream fis = new FileOutputStream(file);
	        byte[] buffer = new byte[1024];
	        int count=0;
	        while((count = bis.read(buffer,0,1024)) != -1)
	        {
	            fis.write(buffer, 0, count);
	        }
	        fis.close();
	        bis.close();
	    }
	 
	 private static List<String> runCommand(String command){
		 ArrayList<String> array=new ArrayList<String>();
		 Process proc = null;
		 try {
			proc = Runtime.getRuntime().exec(command);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
		 String line = "";
		 try {
			while((line = reader.readLine()) != null) {
				 array.add(line);
			 }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 return array;
	 }
	 
	 private static boolean isVideo(String name){
		 String array[]={".MOV",".MPEG4",".MP4",".AVI",".WMV",".MPEGPS",".FLV",".3GPP",".WebM"};
		 for(String a:array){
			 if(name.toLowerCase().contains(a.toLowerCase()))
				 return true;
		 }
		 return false;
	 }
	 
	 private static String getGifUrl(Message message){
		 InputStream is = null;
		    BufferedReader br;
		    String line,data="";
		 String searchWord=message.getContent().replaceAll("/gif","");
		 searchWord=searchWord.trim();
		 URL url=null;
		 searchWord=searchWord.replaceAll(" ", "-");
		 try {
			url =new URL("http://giphy.com/search/"+searchWord);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 print("http://giphy.com/search/"+searchWord);
		 URLConnection conn=null;
		try {
			conn = url.openConnection();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	        conn.setRequestProperty("User-Agent", "Mozilla/5.0");
	        try {
				is = conn.getInputStream();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  // throws an IOException
	        br = new BufferedReader(new InputStreamReader(is));
	        try {
				while ((line = br.readLine()) != null) {
				       data+=line;
				       data+="\n";
				    }
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        int first=data.indexOf("giphy.gif");
	        String part=data.substring(0, first+9);
	        part=reverse(part);
	        first=part.indexOf(":sptth");
	        part=part.substring(0, first+6);
	        part=reverse(part);
		 return part;
	 }
	 
	 public static String reverse(String input){
		    char[] in = input.toCharArray();
		    int begin=0;
		    int end=in.length-1;
		    char temp;
		    while(end>begin){
		        temp = in[begin];
		        in[begin]=in[end];
		        in[end] = temp;
		        end--;
		        begin++;
		    }
		    return new String(in);
		}
	 

}
