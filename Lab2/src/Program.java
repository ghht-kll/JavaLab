import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class Program {
	public static void main(String[] args) throws Exception{
		Scanner inputScanner = new Scanner(System.in);
		String string = inputScanner.nextLine();
		inputScanner.close();
		
		File file = CreateFile(string);
		ReadFile(file);
		DeleteFile(file);
	}
	
	public static File CreateFile(String string) throws IOException
	{
		File file = new File("file.txt");
		file.createNewFile();
		FileWriter writer = new FileWriter(file);
		writer.write(string);
		writer.flush();
		writer.close();
		
		return file;
	}
	
	public static void ReadFile(File file) throws IOException
	{
		try {
            Scanner fileScanner = new Scanner(file);
            while (fileScanner.hasNextLine()) {
                String data = fileScanner.nextLine();
                System.out.println(data);
            }
            fileScanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
	}
	
	public static void DeleteFile(File file) throws IOException
	{
		file.delete();
	}
	
}
