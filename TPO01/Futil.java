package zad1;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class Futil {

    public static void processDir(String dirName, String resultFileName){

        try ( FileChannel fout = FileChannel.open(Paths.get(resultFileName),StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING) ) {

            Files.walkFileTree(Paths.get(dirName), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if (attrs.isRegularFile()) {
                        try ( FileChannel fcin = FileChannel.open(file, StandardOpenOption.READ) ) {
                            ByteBuffer buffer = ByteBuffer.allocate((int) fcin.size());
                            fcin.read(buffer);
                            buffer.flip();

                            Charset inCharset = Charset.forName("Cp1250");
                            Charset outCharset = StandardCharsets.UTF_8;

                            CharBuffer charBuffer = inCharset.decode(buffer);

                            buffer = outCharset.encode(charBuffer);

                            fout.write(buffer);

                        }
                    }
                    return FileVisitResult.CONTINUE;
                }
            });

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
