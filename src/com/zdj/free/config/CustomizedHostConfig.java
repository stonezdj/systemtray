package com.zdj.free.config;

import java.io.File;

import com.zdj.free.HostConfig;
import com.zdj.free.Main;

public class CustomizedHostConfig extends HostConfig{
	public CustomizedHostConfig()
	{
		super(Main.currentPath+File.separator+"config/customized.properties");
	}
}
