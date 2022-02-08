/*
  Copyright (c) 2022 Washington University in St. Louis

  Washington University in St. Louis hereby grants to you a non-transferable,
  non-exclusive, royalty-free license to use and copy the computer code
  provided here (the "Software").  You agree to include this license and the
  above copyright notice in all copies of the Software.  The Software may not
  be distributed, shared, or transferred to any third party.  This license does
  not grant any rights or licenses to any other patents, copyrights, or other
  forms of intellectual property owned or controlled by
  Washington University in St. Louis.

  YOU AGREE THAT THE SOFTWARE PROVIDED HEREUNDER IS EXPERIMENTAL AND IS PROVIDED
  "AS IS", WITHOUT ANY WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING
  WITHOUT LIMITATION WARRANTIES OF MERCHANTABILITY OR FITNESS FOR ANY PARTICULAR
  PURPOSE, OR NON-INFRINGEMENT OF ANY THIRD-PARTY PATENT, COPYRIGHT, OR ANY OTHER
  THIRD-PARTY RIGHT.  IN NO EVENT SHALL THE CREATORS OF THE SOFTWARE OR WASHINGTON
  UNIVERSITY IN ST LOUIS BE LIABLE FOR ANY DIRECT, INDIRECT, SPECIAL, OR
  CONSEQUENTIAL DAMAGES ARISING OUT OF OR IN ANY WAY CONNECTED WITH THE SOFTWARE,
  THE USE OF THE SOFTWARE, OR THIS AGREEMENT, WHETHER IN BREACH OF CONTRACT, TORT
  OR OTHERWISE, EVEN IF SUCH PARTY IS ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
*/
package edu.wustl.arc.utilities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class FileUtil {

    static final String tag = "FileUtil";

    public static boolean copy(File from, File to) {
        Log.i(tag,"copy("+from.getName()+","+to.getName()+")");
        try {
            if(!from.exists()) {
                return false;
            }
            if(!to.exists()) {
                to.createNewFile();
            }

            InputStream in = new FileInputStream(from);
            OutputStream out = new FileOutputStream(to);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            out.flush();
            out.close();

        } catch (FileNotFoundException e) {
            Log.e(tag, e.getMessage());
            return false;
        } catch (Exception e) {
            Log.e(tag, e.getMessage());
            return false;
        }
        return true;
    }


    public static boolean delete(File file) {
        Log.i(tag,"delete("+file.getName()+")");
        try {
            return file.delete();
        } catch (Exception e) {
            Log.e(tag, e.getMessage());
            return false;
        }
    }

    public static boolean move(File from, File to) {
        if(!copy(from,to)){
            return false;
        }
        return delete(from);
    }

    public static Bitmap readBitmap(File file){
        Log.i(tag,"readBitmap("+file.getName()+")");
        String path =  file.getPath();
        return BitmapFactory.decodeFile(path);
    }

    public static boolean writeBitmap(File file, Bitmap bitmap, int quality){
        Log.i(tag,"writeBitmap("+file.getName()+")");

        boolean compressed = false;
        try {
            if(!file.exists()){
                file.createNewFile();
            }
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            compressed = bitmap.compress(Bitmap.CompressFormat.PNG, quality, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return compressed;
    }

    public static String readTextFile(File file){
        Log.i(tag,"readTextFile("+file.getName()+")");
        if(!file.exists()){
            return "";
        }
        StringBuilder contentBuilder = new StringBuilder();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            String currentLine;
            while ((currentLine = bufferedReader.readLine()) != null) {
                contentBuilder.append(currentLine).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
        return contentBuilder.toString();
    }

    public static boolean writeTextFile(File file, String string){
        Log.i(tag,"writeTextFile("+file.getName()+")");
        try {
            if(!file.exists()){
                file.createNewFile();
            }
            RandomAccessFile stream = new RandomAccessFile(file, "rw");
            stream.setLength(0);
            FileChannel channel = stream.getChannel();
            byte[] strBytes = string.getBytes();
            ByteBuffer buffer = ByteBuffer.allocate(strBytes.length);
            buffer.put(strBytes);
            buffer.flip();
            channel.write(buffer);
            stream.close();
            channel.close();
        } catch (IOException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
