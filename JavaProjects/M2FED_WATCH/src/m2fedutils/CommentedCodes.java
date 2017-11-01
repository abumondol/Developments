package m2fedutils;

public class CommentedCodes {
    
}

 /*void watchDirectoryPath() throws Exception {
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

    }*/
