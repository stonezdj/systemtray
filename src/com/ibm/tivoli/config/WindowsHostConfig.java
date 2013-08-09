package com.ibm.tivoli.config;

import java.io.File;

import com.ibm.tivoli.HostConfig;
import com.ibm.tivoli.Main;



public class WindowsHostConfig  extends HostConfig{
	public WindowsHostConfig()
	{
		super(Main.currentPath+File.separator+"config/windows.machine.properties");
	}
	
	private static class InstanceHolder{
		public static final WindowsHostConfig INSTANCE = new WindowsHostConfig();
	}
	
	public static WindowsHostConfig getInstance()
	{
		return InstanceHolder.INSTANCE;
	}	
}
