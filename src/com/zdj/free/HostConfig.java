package com.zdj.free;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Set;

public class HostConfig {
	protected Properties prop=new Properties();
	private String cfgFileLocation;
	public HostConfig(String cfgFile)
	{
		cfgFileLocation = cfgFile;
		try
		{
			FileInputStream fis = new FileInputStream(cfgFileLocation);
			prop.load(fis);
			fis.close();
		}catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	public void reload()
	{
		try
		{
			prop.clear();
			FileInputStream fis = new FileInputStream(cfgFileLocation);
			prop.load(fis);
			fis.close();
		}catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public Set getMachines()
	{
		
		return prop.keySet();		
	}
	
	public String getValueByKey(String key)
	{
		return prop.getProperty(key);
	}
}
