import java.io.*;
import javax.swing.filechooser.FileSystemView;

public class Program {
	public static void main(String[] args) {  
        FileSystemView view = FileSystemView.getFileSystemView();
        File[] drives = File.listRoots();

        for (File drive : drives) {
            System.out.printf("logical drives: %s%n", drive);
            System.out.printf("Is floppy: %s%n", view.isFloppyDrive(drive));
            System.out.printf("Is drive: %s%n", view.isDrive(drive));
            System.out.printf("Total space: %s%n", drive.getTotalSpace());
            System.out.printf("Free space: %s%n", drive.getFreeSpace());
            System.out.printf("Display name: %s%n", view.getSystemDisplayName(drive));
        }
    }
}
