/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package m2fed;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

/**
 *
 * @author mm5gg
 */
public class WatchDataUpload {

    final long wait_interval = 10 * 1000;
    String uploadFolder = "C:\\xampp\\htdocs\\m2fed\\uploads";
    String destinationFolder = "C:\\ASM\\M2FED_Watch_Data\\";
    String destinationFolderTemp = "C:\\ASM\\M2FED_Watch_Data\\temp\\";
    File srcFile, destFile;
    DataProcessing dp;

    public WatchDataUpload() throws Exception {
        dp = new DataProcessing();
    }

    void watchUpload() throws Exception {
        File[] files;
        int i;
        while (true) {
            files = new File(uploadFolder).listFiles();
            if (files.length == 0) {
                System.out.print("Sleep...");
                Thread.sleep(5 * 1000);
                System.out.print("up. ");
                continue;
            }
            
            System.out.println("File Count:" + files.length);
            for (i = 0; i < files.length; i++) {
                destFile = new File(destinationFolder + "\\" + files[i].getName());
                if (!destFile.exists()) {
                    dp.processUploadedFile(files[i]);
                    boolean flag = files[i].renameTo(destFile);
                    if (flag) {
                        System.out.println("File moved to: " + destFile.getPath());
                    } else {
                        System.out.println("Failed moving file to: " + destFile.getPath());                        
                    }
                }else{
                    System.out.println("Destination file exists. Delete result: "+files[i].delete());                    
                }

            }
        }

    }

    void watchDirectoryPath() throws Exception {
        Path path = new File(uploadFolder).toPath();
        WatchService watchService = FileSystems.getDefault().newWatchService();
        path.register(watchService, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
        System.out.println("Watching path: " + path);

        WatchKey watchKey;
        WatchEvent.Kind<?> kind = null;
        while (true) {
            watchKey = watchService.take();
            for (WatchEvent<?> watchEvent : watchKey.pollEvents()) {
                // Get the type of the event
                kind = watchEvent.kind();
                if (OVERFLOW == kind) {
                    continue; // loop
                } else if (ENTRY_CREATE == kind) {
                    Path newPath = ((WatchEvent<Path>) watchEvent).context();
                    System.out.println("New file uploaded: " + newPath);
                    destFile = new File(destinationFolderTemp + newPath);
                    srcFile = new File(uploadFolder + newPath);
                    System.out.println("File moving from: " + srcFile.getPath());
                    if (srcFile.exists()) {
                        System.out.println("Source Exists");
                    }
                    boolean flag = srcFile.renameTo(destFile);
                    if (flag) {
                        System.out.println("File moved to: " + destFile.getPath());
                        destFile = new File(destinationFolder + newPath);

                    } else {
                        System.out.println("Failed moving file to: " + destFile.getPath());
                        if (destFile.exists()) {
                            System.out.println("Destination file exists.");
                            srcFile.delete();
                        }
                    }

                } else if (ENTRY_MODIFY == kind) {
                    Path newPath = ((WatchEvent<Path>) watchEvent).context();
                    System.out.println("New path modified: " + newPath);

                } else if (ENTRY_DELETE == kind) {
                    Path newPath = ((WatchEvent<Path>) watchEvent).context();
                    System.out.println("Path deleted: " + newPath);
                }
            }

            if (!watchKey.reset()) {
                System.out.println("WatchKey could not be reset. Breaking the loop.");
                break; // loop
            }
        }

    }
}
