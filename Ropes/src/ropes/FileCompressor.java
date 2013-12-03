/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ropes;

/**
 *
 * @author Urban
 */



import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.Adler32;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class FileCompressor {
    
  public ZipOutputStream zos = null;
  private FileOutputStream fos = null;
  private File zipFilePath = null;
  
  public FileCompressor(File outPath){
      this.zipFilePath = outPath;
      
       //create directory if dont exist
       File directory = new File(zipFilePath.getParent());
       directory.mkdirs();
      
      try {
          this.zos = new ZipOutputStream( new FileOutputStream( outPath ) );
          this.fos = new FileOutputStream(zipFilePath);
      }
      catch (FileNotFoundException ex) {
          Logger.getLogger(FileCompressor.class.getName()).log(Level.SEVERE, null, ex);
      }
  }
  

    
  public void decompressZipFile(String zipFilePath, String outPath) {
          File zipFile = new File(zipFilePath);
          if (!zipFile.exists()) {
              System.err.println("Zip file don't exists, please try another");
              return;
          } 
      
        try {
         final int BUFFER = 2048;
         BufferedOutputStream dest = null;
         FileInputStream fis = new FileInputStream(zipFile);
         CheckedInputStream checksum = new  CheckedInputStream(fis, new Adler32());
         ZipInputStream zis = new  ZipInputStream(new BufferedInputStream(checksum));
         ZipEntry entry;
         while((entry = zis.getNextEntry()) != null) {
            System.out.println("Extracting: " +entry);
            int count;
            byte data[] = new byte[BUFFER];
            // write the files to the disk
            FileOutputStream fos = new  FileOutputStream(outPath + "\\" + entry.getName());
            dest = new BufferedOutputStream(fos, BUFFER);
            while ((count = zis.read(data, 0, 
              BUFFER)) != -1) {
               dest.write(data, 0, count);
            }
            dest.flush();
            dest.close();
         }
         zis.close();
         System.out.println("Checksum: "+checksum.getChecksum().getValue());
      } catch(Exception e) {
         e.printStackTrace();
      }
      
      
      
      /*   try {
            FileInputStream fis = new FileInputStream(gzipFile);
            GZIPInputStream gis = new GZIPInputStream(fis);
            FileOutputStream fos = new FileOutputStream(newFile);
            byte[] buffer = new byte[1024];
            int len;
            while((len = gis.read(buffer)) != -1){
                fos.write(buffer, 0, len);
            }
            //close resources
            fos.close();
            gis.close();
        } catch (IOException e) {
            e.printStackTrace();           
        }
*/        
    }
     //taken from https://blogs.oracle.com/CoreJavaTechTips/entry/creating_zip_and_jar_files
    public void compressZipFile(File file, String base) {
        
      try {    
          if(zos == null){
             System.err.println("Please use the right constractor: FileCompressor(File outputPath)");
             return;
          }

          int bytesRead;
          byte[] buffer = new byte[1024];
          CRC32 crc = new CRC32();
                   
              if (!file.exists()) {
                  System.err.println("Skipping: " + file.getAbsolutePath());
                  return;
              }
              if(file.isDirectory()){
                  String dirName = file.getName(); 
                  for(File innerFile:file.listFiles()){
                    compressZipFile(innerFile,dirName);
                  }
              }else{
              BufferedInputStream bis = new BufferedInputStream(
                                        new FileInputStream(file));
              crc.reset();
              while ((bytesRead = bis.read(buffer)) != -1) {
                  crc.update(buffer, 0, bytesRead);
              }
              bis.close();
              // Reset to beginning of input stream
              bis = new BufferedInputStream(
                      new FileInputStream(file));
              ZipEntry entry = new ZipEntry(base + "\\" +file.getName());
              entry.setMethod(ZipEntry.STORED);
              entry.setCompressedSize(file.length());
              entry.setSize(file.length());
              entry.setCrc(crc.getValue());
              zos.putNextEntry(entry);
              while ((bytesRead = bis.read(buffer)) != -1) {
                  zos.write(buffer, 0, bytesRead);
              }
              bis.close();
          
           }
      
      // zos.close();
      } catch (FileNotFoundException ex) {
          Logger.getLogger(FileCompressor.class.getName()).log(Level.SEVERE, null, ex);
      } catch (IOException ex) {
          Logger.getLogger(FileCompressor.class.getName()).log(Level.SEVERE, null, ex);
      } finally {
          try {
              fos.close();
          } catch (IOException ex) {
              Logger.getLogger(FileCompressor.class.getName()).log(Level.SEVERE, null, ex);
          }
      }
    

        
        
        /*try {
            FileInputStream fis = new FileInputStream(file);
            FileOutputStream fos = new FileOutputStream(gzipFile);
            GZIPOutputStream gzipOS = new GZIPOutputStream(fos);
            
            byte[] buffer = new byte[1024];
            int len;
            while((len=fis.read(buffer)) != -1){
                gzipOS.write(buffer, 0, len);
            }
            //close resources
            gzipOS.close();
            fos.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        
    }
}
