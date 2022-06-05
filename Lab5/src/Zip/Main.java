package Zip;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.zip.*;

import net.lingala.zip4j.model.FileHeader;
import net.lingala.zip4j.ZipFile;

public class Main {

	public static void main(String[] args) throws Exception{
		String txtName = "file.txt";
		String zipName = "file.zip";
		
	    File file = new File(txtName);
	    file.createNewFile();
		FileWriter writer = new FileWriter(file);
		writer.write("test");
		writer.flush();
		writer.close();
		
		ZipFile zip = new ZipFile(zipName);
		zip.addFile(file);
		
		List<FileHeader> headerList = zip.getFileHeaders();
		long uncompressedSize = headerList.get(0).getUncompressedSize();
		long compressedSize = headerList.get(0).getCompressedSize();
		
		zip.extractAll("C:\\Zip");
		zip.close();
		
		System.out.printf("�������� ������: %s%n������ ������: %s%n", uncompressedSize, compressedSize);
		
//		Files.delete(Paths.get(zipName));
		
	}
}
