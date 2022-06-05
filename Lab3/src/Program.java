import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.*;


public class Program {
	public static void main(String[] args) throws Exception{
		File file = CreateFile();
		WritingToFile(file);
		ReadingFromFile(file);
		
	}
	
	public static File CreateFile() throws IOException
	{
		File file = new File(fileName);
		file.createNewFile();
		
		return file;
	}
	
	public static void WritingToFile(File file) throws StreamWriteException, DatabindException, IOException
	{
		Js js = new Js();
		js.j0 = "test0";
		js.j1 = "test1";
		
		ObjectMapper mapper = new ObjectMapper();
		//mapper.writeValue(Paths.get(fileName).toFile(), js);
		mapper.writeValue(Paths.get(file.getPath()).toFile(), js);
	}
	
	public static void ReadingFromFile(File file) throws StreamReadException, DatabindException, IOException
	{
		ObjectMapper mapper = new ObjectMapper();
		Map<?, ?> map = mapper.readValue(Paths.get(file.getPath()).toFile(), Map.class);

	    for (Map.Entry<?, ?> entry : map.entrySet()) {
	        System.out.printf("%s = %s%n", entry.getKey(), entry.getValue());
	    }    
	}
	
	public static void DeleteFile(File file)
	{
		file.delete();
	}
	
	private static String fileName = "file.json";
}


