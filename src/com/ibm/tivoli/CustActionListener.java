package com.ibm.tivoli;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;
import java.util.logging.Logger;

import com.ibm.tivoli.config.ApplicationConfig;

public class CustActionListener implements ActionListener {
	private final static Logger LOG = HostManagerClient.LOG;
	private OutputDialog dialog;
	public CustActionListener(OutputDialog dialog)
	{
		this.dialog = dialog;
	}
	private String OSType;
	
	public String getOSType() {
		return OSType;
	}
	public void setOSType(String oSType) {
		OSType = oSType;
	}
	public String getHostName() {
		return hostName;
	}
	public void setHostName(String hostName) {
		if(hostName!=null)
		{
			this.hostName = hostName.trim();
		}
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	private String hostName;
	private String userName;
	private String password;
	public void actionPerformed(ActionEvent e) {
		
		String command = e.getActionCommand();
		//linux.cmd.VNC
		String key="";
		if(HostManagerClient.LINUX.equalsIgnoreCase(OSType))
		{
			key = "linux.cmd."+command;
		}
		else if(HostManagerClient.WINDOWS.equalsIgnoreCase(OSType))
		{
			key = "windows.cmd."+command;
		}
		
		ApplicationConfig appConfig = ApplicationConfig.getInstance();
		String commandPattern = appConfig.getCommandByKey(key);
		String cmdStr = MessageFormat.format(commandPattern, new Object[]{
				getHostName(),
		});
		
		try{
			System.out.println("Command ="+ cmdStr);
			LOG.info("Command ="+ cmdStr);
			Process process = Runtime.getRuntime().exec(cmdStr);
			Thread stdoutThread = new Thread(new StreamDrainer(process.getInputStream(), dialog));   
			Thread stderrThread = new Thread(new StreamDrainer(process.getErrorStream(), dialog));   
	        stdoutThread.start();
	        stderrThread.start();
			
		}catch(Exception exp)
		{
			exp.printStackTrace();
		}

	}

}
