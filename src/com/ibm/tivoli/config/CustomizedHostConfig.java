package com.ibm.tivoli.config;

import java.io.File;

import com.ibm.tivoli.HostConfig;
import com.ibm.tivoli.Main;

public class CustomizedHostConfig extends HostConfig{
	public CustomizedHostConfig()
	{
		super(Main.currentPath+File.separator+"config/customized.properties");
	}
}
