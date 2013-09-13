package com.zdj.free;

public class Main {
	public static String currentPath=".";
	public static void main(String[] args) {
		String curPath = System.getenv("CURRENTPWD");
		if(curPath!=null)
			currentPath = curPath;		
		HostManagerClient client = new HostManagerClient();
	}
}
