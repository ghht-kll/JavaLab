package Xml;

import com.fasterxml.jackson.dataformat.xml.*;
import java.io.*;
import java.nio.file.*;

public class Main {

	public static void main(String[] args) throws Exception{
		User user = new User();
		user.name = "Qwerty";
		user.age = 1232;
		
		String FILENAME = "file.xml";
		File file = new File(FILENAME);
		
		XmlMapper mapper = new XmlMapper();
		mapper.writeValue(file, user);
		
	    String xmlText = Files.readString(Paths.get(FILENAME));
	    User xmlUser = mapper.readValue(xmlText, User.class);
	    
	    System.out.printf("Имя: %s%nВозраст: %s%n", xmlUser.name, xmlUser.age);
	    
//	    Files.delete(Paths.get(FILENAME)); 
	}

}
