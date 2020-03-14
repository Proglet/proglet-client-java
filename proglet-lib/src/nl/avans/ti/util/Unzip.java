package nl.avans.ti.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Unzip {

    public static void unzip(byte[] data, Path outPath)
    {
        ZipInputStream zipFile = new ZipInputStream(new ByteArrayInputStream(data));
        try {
            byte buffer[] = new byte[1024];
            Files.createDirectories(outPath);

            ZipEntry entry = zipFile.getNextEntry();
            while(entry != null)
            {
                Path newFile = newFile(outPath, entry);

                if(!Files.exists(newFile.getParent()))
                    Files.createDirectories(newFile.getParent());

                if(entry.isDirectory())
                {
                    entry = zipFile.getNextEntry();
                    continue;
                }

                try {
                    OutputStream fos = Files.newOutputStream(newFile);
                    int len;
                    while ((len = zipFile.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                    fos.close();
                } catch(IOException e)
                {
                    e.printStackTrace();
                }
                entry = zipFile.getNextEntry();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Path newFile(Path destinationDir, ZipEntry zipEntry) throws IOException {
        Path destFile = destinationDir.resolve(zipEntry.getName());

        String destDirPath = destinationDir.toAbsolutePath().toString();
        String destFilePath = destFile.toAbsolutePath().toString();

        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }

        return destFile;
    }

}
