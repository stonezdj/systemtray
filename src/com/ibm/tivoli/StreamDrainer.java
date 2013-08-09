package com.ibm.tivoli;

import java.io.BufferedReader;   
import java.io.InputStream;   
import java.io.InputStreamReader;   
import java.util.logging.Logger;
  
class StreamDrainer implements Runnable {   
	private final static Logger LOG = HostManagerClient.LOG;
    private InputStream ins;   
    private OutputDialog dialog;
  
    public StreamDrainer(InputStream ins, OutputDialog dialog) {   
        this.ins = ins;
        this.dialog = dialog;
    }   
  
    public void run() {   
    	StringBuffer buffer = new StringBuffer();
    	try {   
        	
            BufferedReader reader = new BufferedReader(   
                    new InputStreamReader(ins));   
            String line = null;   
            while ((line = reader.readLine()) != null) {   
                System.out.println(line); 
                dialog.printMsg(line);
                buffer.append(line);
                
            }   
            LOG.info(buffer.toString());
            
            
        } catch (Exception e) {   
            e.printStackTrace();   
        }   
    }   
  
}   
