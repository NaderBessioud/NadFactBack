package tn.famytech.esprit.Services;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.sql.SQLException;
import java.time.LocalTime;
import java.time.Year;

import javax.sql.rowset.serial.SerialException;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileFTPService {
	 public String uploadfile(MultipartFile uploadFile) {
	    	int currentYear = Year.now().getValue();
	    	LocalTime currentTime = LocalTime.now();
	    	String name=uploadFile.getOriginalFilename();
	    	name=name+"_"+currentYear+"_"+currentTime;
	    	
			   FTPClient ftpClient = new FTPClient();
			     try {
			    	
			    	 
			         ftpClient.connect("1192.168.1.31", 21);
			         ftpClient.login("ftp-user", "ftpuser");
			         ftpClient.enterLocalPassiveMode();

			         ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

			         // APPROACH #1: uploads first file using an InputStream
			         File firstLocalFile = new File("D:/Test/Projects.zip");

			         String firstRemoteFile = "Projects.zip";
			         InputStream inputStream = uploadFile.getInputStream();
			        
			         System.out.println("Start uploading first file");
			         
			         boolean done = ftpClient.storeFile(name, inputStream);
			         inputStream.close();
			         if (done) {
			             System.out.println("The first file is uploaded successfully.");
			         }

			         return name;

			     } catch (IOException ex) {
			         System.out.println("Error: " + ex.getMessage());
			         ex.printStackTrace();
			     } finally {
			         try {
			             if (ftpClient.isConnected()) {
			                 ftpClient.logout();
			                 ftpClient.disconnect();
			             }
			         } catch (IOException ex) {
			             ex.printStackTrace();
			         }
			     }
			     return null;

			}
	 
	   public byte[] downloadFile(String name) throws SerialException, SQLException, IOException {
			
		    
		    FTPClient ftpClient = new FTPClient();
		    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		    try {
		    	ftpClient.connect("192.168.1.31", 21);
		        ftpClient.login("ftp-user", "ftpuser");
		        ftpClient.enterLocalPassiveMode();
		        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

		        InputStream inputStream = ftpClient.retrieveFileStream(name);
		        byte[] buffer = new byte[1024];
		        int bytesRead;
		        while ((bytesRead = inputStream.read(buffer)) != -1) {
		            outputStream.write(buffer, 0, bytesRead);
		        }

		        return outputStream.toByteArray();
		    } catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
		        if (ftpClient.isConnected()) {
		            ftpClient.logout();
		            ftpClient.disconnect();
		        }
		        outputStream.close();
		    }
		    return null;
		}

}
