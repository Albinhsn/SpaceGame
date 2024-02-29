package se.liu.albhe576.project.Scripts;

import org.lwjgl.BufferUtils;
import se.liu.albhe576.project.ResourceManager;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.sql.Timestamp;

public class BinaryDataConverterScript
{

    private static void writeBytesToFile(String path, byte[] bytes){
        try(FileOutputStream stream = new FileOutputStream(path)){
            stream.write(bytes);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static void main(String[] args) {
    }
}
