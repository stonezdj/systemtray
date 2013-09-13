package com.zdj.free;

import java.awt.TrayIcon;

public class DemoBackgroundThread extends Thread {
	private TrayIcon trayIcon;
	public DemoBackgroundThread(TrayIcon trayIcon)
	{
		this.trayIcon = trayIcon;
	}
	

	public void run() {
		try {
			sleep(1000*10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}		
		trayIcon.displayMessage("Host Manager", "The DemoHost is started!", TrayIcon.MessageType.INFO);
		trayIcon.setActionCommand("DemoHostReady");
	}
	
	

}
