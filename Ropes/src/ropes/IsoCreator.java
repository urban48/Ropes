/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ropes;

/**
 *
 * @author urban
 * code taken from :https://github.com/stephenc/java-iso-tools
 * 
 */


import java.io.*;

import de.tu_darmstadt.informatik.rbg.hatlak.iso9660.*;
import de.tu_darmstadt.informatik.rbg.hatlak.iso9660.impl.*;
import de.tu_darmstadt.informatik.rbg.hatlak.eltorito.impl.ElToritoConfig;
import de.tu_darmstadt.informatik.rbg.hatlak.joliet.impl.JolietConfig;
import de.tu_darmstadt.informatik.rbg.hatlak.rockridge.impl.RockRidgeConfig;
import de.tu_darmstadt.informatik.rbg.mhartle.sabre.*;
import java.util.logging.Level;
import java.util.logging.Logger;


public class IsoCreator {
    
       public IsoCreator(){

       }
            
        public Integer getFileLimitPerDir() {
            return fileLimitPerDir;
        }

        public void setFileLimitPerDir(Integer fileLimitPerDir) {
            this.fileLimitPerDir = fileLimitPerDir;
        }

        private Integer fileLimitPerDir = 1000;
        //supports names stored in Unicode, thus allowing almost any character to be used, even from non-Latin scripts.
    	private static boolean enableJoliet = true;
        //supports the preservation of POSIX (Unix-style) permissions and longer names.
	private static boolean enableRockRidge = true;
        //enables CDs to be bootable on PCs.
	private static boolean enableElTorito = false;
        
        private ISO9660RootDirectory root = new ISO9660RootDirectory();
        
        
        
        private ISO9660Config iso9660Config = new ISO9660Config();
        
        private Boolean files_added = false; 
        private JolietConfig jolietConfig = null;
        private ElToritoConfig elToritoConfig = null;
        private RockRidgeConfig rrConfig = null;

         
        
        public void setupISO(){
                try {
                    // Output file
                    
                    // Directory hierarchy, starting from the root
                    ISO9660RootDirectory.MOVED_DIRECTORIES_STORE_NAME = "rr_moved";
                    
                    //ISO9660 support
                    iso9660Config.allowASCII(false);
                    iso9660Config.setInterchangeLevel(1);
                    iso9660Config.restrictDirDepthTo8(true);
                    iso9660Config.setPublisher(System.getProperty("user.name") );
                    iso9660Config.setVolumeID("Ropes ISO");
                    iso9660Config.setDataPreparer("Ropes");
                    //iso9660Config.setCopyrightFile(new File("Copyright.txt"));
                    iso9660Config.forceDotDelimiter(true);
                    
                    
                    if (enableRockRidge) {
                        // Rock Ridge support
                        rrConfig = new RockRidgeConfig();
                        rrConfig.setMkisofsCompatibility(false);
                        rrConfig.hideMovedDirectoriesStore(true);
                        rrConfig.forcePortableFilenameCharacterSet(true);
                    }
                    
                    
                    if (enableJoliet) {
                        // Joliet support
                        jolietConfig = new JolietConfig();
                        jolietConfig.setPublisher(System.getProperty("user.name"));
                        jolietConfig.setVolumeID("Ropes ISO");
                        jolietConfig.setDataPreparer("Ropes");
                        //jolietConfig.setCopyrightFile(new File("Copyright.txt"));
                        jolietConfig.forceDotDelimiter(true);
                    }
                    
                    
                    if (enableElTorito) {
                        try {
                            // El Torito support
                            elToritoConfig = new ElToritoConfig(new File(
                                    "tomsrtbt-2.0.103.ElTorito.288.img"),
                                    ElToritoConfig.BOOT_MEDIA_TYPE_2_88MEG_DISKETTE,
                                    ElToritoConfig.PLATFORM_ID_X86, "iso", 4,
                                    ElToritoConfig.LOAD_SEGMENT_7C0);
                        } catch (HandlerException ex) {
                            Logger.getLogger(IsoCreator.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }   } catch (ConfigException ex) {
                    Logger.getLogger(IsoCreator.class.getName()).log(Level.SEVERE, null, ex);
                }
        }
        public void addFiles(File path, Long numOfFiles){//for adding standard
            ISO9660Directory  idir = new ISO9660Directory("0");
            Integer fileCounter = numOfFiles.intValue();
            if (path != null) {
                // Add file or directory contents recursively
                if (path.exists()) {
                    while(!fileCounter.equals(0)){
                        if((fileCounter % 1000) == 0){
                             root.addDirectory(idir);
                             idir = new ISO9660Directory(String.valueOf(numOfFiles/1000)); 
                        }
                        if (path.isDirectory()) {
                            try {
                                idir.addContentsRecursively(path);
                            } catch (HandlerException ex) {
                                Logger.getLogger(IsoCreator.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        } else {
                            try {                    
                                idir.addFile(path);
                            } catch (HandlerException ex) {
                                Logger.getLogger(IsoCreator.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                        fileCounter--;
                        
                    }
                    root.addDirectory(idir);
                    files_added = true;
                }                           
             }
          }
        public void createISO(File output){
            //TODO after iso is created it still used by the program and is unaccecible untill prog is closed
            if(files_added){
                try {
                    StreamHandler streamHandler = new ISOImageFileHandler(output);
                    CreateISO iso = new CreateISO(streamHandler, root);
                    iso.process(iso9660Config, rrConfig, jolietConfig, elToritoConfig);
                    streamHandler = null;
                    System.out.println("Done. File is: " + output);
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(IsoCreator.class.getName()).log(Level.SEVERE, null, ex);
                } catch (HandlerException ex) {
                    Logger.getLogger(IsoCreator.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }
        }

        

}
