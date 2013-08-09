package com.ibm.tivoli.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import com.ibm.tivoli.Main;


public class ApplicationConfig {
	private Properties commandProp = new Properties();
	private String windowsCommand;
	private String linuxCommand;
	
	private static class InstanceHolder{
		public static final ApplicationConfig INSTANCE = new ApplicationConfig();
	}
	
	public static ApplicationConfig getInstance()
	{
		return InstanceHolder.INSTANCE;
	}
	
	public ApplicationConfig()
	{

		try
		{
			FileInputStream fis = new FileInputStream(Main.currentPath+File.separator+"config/application.properties");
			commandProp.load(fis);
		}catch(IOException e)
		{
			e.printStackTrace();
		}

		windowsCommand = commandProp.getProperty("windows.cmd", "mstsc.exe /v:{0}");
		linuxCommand = commandProp.getProperty("linux.cmd", "putty.exe root@{0}");



	}
//	public String getLinuxCommand() {
//		return linuxCommand;
//	}
//	public String getWindowsCommand() {
//		return windowsCommand;
//	}
	
	public String getCommand(String key)
	{
		return commandProp.getProperty(key);
	}
	
	public List<String> getLinuxCmdList()
	{
		List<String> list = new ArrayList<String>();
		
		String linuxPrefx = "linux.cmd.";
		
		Iterator it = commandProp.keySet().iterator();
		while(it.hasNext())
		{
			String key = (String) it.next();
			if(key!=null && key.startsWith(linuxPrefx) && key.length()>linuxPrefx.length())
			{
				list.add(key.substring(linuxPrefx.length()));
			}
		}
		
		return list;
	}
	
	public List<String> getWindowsCmdList()
	{
		List<String> list = new ArrayList<String>();
		
		String linuxPrefx = "windows.cmd.";
		
		Iterator it = commandProp.keySet().iterator();
		while(it.hasNext())
		{
			String key = (String) it.next();
			if(key!=null && key.startsWith(linuxPrefx) && key.length()>linuxPrefx.length())
			{
				list.add(key.substring(linuxPrefx.length()));
			}
		}
		
		return list;
	}
	

	public String getCommandByKey(String key)
	{
		return commandProp.getProperty(key);
	}

}
