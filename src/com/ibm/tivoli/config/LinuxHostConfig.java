package com.ibm.tivoli.config;

import java.io.File;

import com.ibm.tivoli.HostConfig;
import com.ibm.tivoli.Main;

public class LinuxHostConfig extends HostConfig {
	public LinuxHostConfig()
	{
		super(Main.currentPath+File.separator+"config/linux.machine.properties");
	}
	private static class InstanceHolder{
		public static final LinuxHostConfig INSTANCE = new LinuxHostConfig();
	}
	
	public static LinuxHostConfig getInstance()
	{
		return InstanceHolder.INSTANCE;
	}
	
}
