/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ropes;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import javax.swing.filechooser.FileSystemView;

/**
 *
 * @author Urban
 */
public class SystemInfo {
    
    public static class MediaInfo{
        public String description;
        public Integer freeSpace;
        public MediaInfo(String description, Integer freeSpace){
            this.description = description;
            this.freeSpace = freeSpace;
        }
        
    }
    
    public String getOsType(){
        return System.getProperty("os.name");
    }
    
    
    public Map<String, MediaInfo> getMediaDrives(){
        Map<String, MediaInfo> drives = new HashMap<String, MediaInfo>();
        File[] paths;
        FileSystemView fsv = FileSystemView.getFileSystemView();
        
        
        
        // returns pathnames for files and directory
        paths = File.listRoots();
        
        // for each pathname in pathname array
        for(File path:paths){
            //calculate the size of the media in MB scale
            Double size_inBytes = path.getFreeSpace() / (Math.pow(1024,2));
            Integer size_inMB = size_inBytes.intValue();
               
            MediaInfo mi = new MediaInfo(fsv.getSystemTypeDescription(path), size_inMB);
            drives.put(String.valueOf(path), mi);
          
        }
        
        return drives;
    }
    
}

