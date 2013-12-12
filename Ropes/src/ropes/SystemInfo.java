/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ropes;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.DirectoryStream;
import java.nio.file.FileStore;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.filechooser.FileSystemView;
import sun.awt.OSInfo;

/**
 *
 * @author Urban
 */
public class SystemInfo {
    
    public  class MediaInfo{
        public String description;
        public Integer freeSpace;
        public String path;
        public MediaInfo(String description, Integer freeSpace, String path){
            this.description = description;
            this.freeSpace = freeSpace;
            this.path = path;
            
        }
        
    }

    
    
    public Integer getFreeSpaceMB(Long val){
            Double size_inBytes = val / (Math.pow(1024,2));
            return size_inBytes.intValue();
    }
    
    
    public Map<String, MediaInfo> getMediaDrives(){
        String osType  = System.getProperty("os.name");
        Map<String, MediaInfo> drives = new HashMap<String, MediaInfo>();
        
        if(osType.contains("Windows")){//windows
                    File[] paths;
        FileSystemView fsv = FileSystemView.getFileSystemView();
        
        
        
        // returns pathnames for files and directory
        paths = File.listRoots();
        
        // for each pathname in pathname array
        for(File path:paths){
            //calculate the size of the media in MB scale        
            Integer size_inMB = getFreeSpaceMB(path.getFreeSpace());
               
            MediaInfo mi = new MediaInfo(fsv.getSystemTypeDescription(path), size_inMB,path.toString());
            drives.put(String.valueOf(path), mi);
          
           }
        }else{//linux
         Path media = Paths.get("/media");
         if (media.isAbsolute() && Files.exists(media)) {// Linux
            for (FileStore store : FileSystems.getDefault().getFileStores()) { 
              try (DirectoryStream<Path> stream = Files.newDirectoryStream(media)) {
                  for (Path p : stream) {
                      if (Files.getFileStore(p).equals(store)) {
                          String mediaType;
                          switch(store.type()){
                              case "ext4":
                              case "ext3":
                                  mediaType = "Local Disk";
                                  break;
                              case "vfat":
                                  mediaType = "Removable Disk";
                                  break;
                              case "iso9660":
                                  mediaType = "CD Drive";
                                  break;
                              default:
                                  mediaType = "unknown";
                                  break;                              
                          }
                          
                          Integer size_inMB = getFreeSpaceMB(store.getUsableSpace());
                          
                          MediaInfo mi = new MediaInfo(mediaType, size_inMB, p.toString());
                          drives.put( p.getFileName().toString(), mi);
                          //System.out.println(store.type() + " : " + store.name() +" :" + String.valueOf(store.getUsableSpace()/ (Math.pow(1024,2))));
                          
                      }
                  }
                  
              } catch (IOException ex) {
                //  Logger.getLogger(tclass.getName()).log(Level.SEVERE, null, ex);
              }
          }
        }
        }
        
        

        
        return drives;
    }
    
}

