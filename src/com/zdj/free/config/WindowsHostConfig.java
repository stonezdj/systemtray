package com.zdj.free.config;

import java.io.File;

import com.zdj.free.HostConfig;
import com.zdj.free.Main;



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
