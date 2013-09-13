package com.zdj.free;

import java.awt.AWTException;
import java.awt.CheckboxMenuItem;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Menu;
import java.awt.MenuContainer;
import java.awt.MenuItem;
import java.awt.Point;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import org.apache.commons.io.FileUtils;

import com.zdj.free.config.ApplicationConfig;
import com.zdj.free.config.CustomizedHostConfig;
import com.zdj.free.config.LinuxHostConfig;
import com.zdj.free.config.WindowsHostConfig;


public class HostManagerClient implements ActionListener{
	private PopupMenu popup = new PopupMenu();
	//	private JPopupMenu jpopup = new JPopupMenu();
	private TrayIcon trayIcon;
	private Image normalImage;
	private Image runImage;
	private final ApplicationConfig appConfig=new ApplicationConfig();
	private final LinuxHostConfig linuxCfg=new LinuxHostConfig();
	private final WindowsHostConfig winCfg = new WindowsHostConfig();
	private final CustomizedHostConfig custCfg = new CustomizedHostConfig();
	public static Logger LOG;
	public static final String LINUX="Linux";
	public static final String WINDOWS="Windows";
	private ArrayList<MenuItem> hostMenuList = new ArrayList<MenuItem>();

	private MenuItem exitItem; 
	private MenuItem setItem;
	private MenuItem refreshItem;
	private Menu demoMenu;
	private Menu modeMenu;
	private CheckboxMenuItem workItem;
	//	private CheckboxMenuItem logMenu;
	private MenuItem logMenu;

	private OutputDialog dialog = new OutputDialog();

	static
	{
		LOG = Logger.getLogger("com.ibm.tivoli.agent");
		LOG.setLevel(Level.INFO);
		FileHandler fh=null;
		try
		{
			fh = new FileHandler(Main.currentPath+File.separator+"trace.log");
			fh.setFormatter(new SimpleFormatter());
		}catch(Exception e)
		{
			LOG.log(Level.INFO, "", e);
		}

		//remove all default handler
		Handler[] handlers = LOG.getHandlers();
		for(int i=0; i<handlers.length; i++)
		{
			LOG.removeHandler(handlers[i]);
		}

		LOG.addHandler(fh);
	}

	public HostManagerClient()
	{
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		dialog.setLocation(new Point(dim.width-200, 0));
		dialog.setSize(new Dimension(200,400));
		dialog.setAlwaysOnTop(true);

		dialog.setVisible(true);

		ActionListener exitListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("Exiting...");
				System.exit(0);
				dialog.dispose();
			}
		};


		exitItem = new MenuItem("Exit");		
		exitItem.addActionListener(this);
		exitItem.addActionListener(exitListener);

		setItem = new MenuItem("Settings");		
		setItem.addActionListener(this);

		refreshItem = new MenuItem("Refresh");
		refreshItem.addActionListener(this);

		//		logMenu = new CheckboxMenuItem("Output");
		logMenu = new MenuItem("Output");

		logMenu.addActionListener(dialog);
		//		logMenu.addActionListener(this);




		demoMenu = new Menu("DemoHost");		
		MenuItem demoItem = new MenuItem("RemoteDesktop");
		demoMenu.add(demoItem);
		demoItem.addActionListener(this);


		modeMenu = new Menu("Mode");
		workItem = new CheckboxMenuItem("Working");

		modeMenu.add(workItem);
		
		System.out.println("Add action listener for workItem!");
		
		workItem.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				File srcFile = null;
				File destFile = null;
				if(workItem.getState())
				{

					//copy the new file to hosts
					srcFile = new File(Main.currentPath+File.separator+"workinghosts");
					destFile = new File("C:/Windows/System32/drivers/etc/hosts");
					LOG.info("Switch to working mode");
					dialog.printMsg("Switch to working mode");
					System.out.println("Switch to working mode");

				}else
				{
					//restore it
					srcFile =  new File(Main.currentPath+File.separator+"orghost");
					destFile = new File("C:/Windows/System32/drivers/etc/hosts");
					LOG.info("Restore from original hosts file");
					dialog.printMsg("Restore from original hosts file");
					System.out.println("Restore from original hosts file");
				}

				try {
					FileUtils.copyFile(srcFile, destFile);
				} catch (Throwable e1) {
					// TODO Auto-generated catch block
					LOG.log(Level.SEVERE, "failed while copying file", e1);
					dialog.printMsg("Failed while copying file.");
				}
				dialog.printMsg("Done!");
				System.out.println("Done!");
				
			}
		});
		

		addLinuxMenuItem();

		popup.addSeparator();
		addWindowsMenuItem();
		addFixedMenus();

		URL url = HostManagerClient.class.getResource("cloud5.gif");

		normalImage = Toolkit.getDefaultToolkit().getImage(url);

		trayIcon = new TrayIcon(normalImage, "Host Manager", popup);
		//trayIcon = new TrayIcon(normalImage, "Host Manager", null);

		URL runUrl = HostManagerClient.class.getResource("cloud4.gif");
		runImage = Toolkit.getDefaultToolkit().getImage(runUrl);



		//		ActionListener actionListener = new ActionListener() {
		//			public void actionPerformed(ActionEvent e) {
		//				System.out.println("Command:"+e.getActionCommand());
		//				if("Client started!".equalsIgnoreCase(e.getActionCommand()))
		//				{
		//					trayIcon.setImage(normalImage);
		//				}
		//				
		//				trayIcon.setActionCommand(null);
		//				
		//				trayIcon.displayMessage("Action Event", 
		//						"An Action Event Has Been Performed!",
		//						TrayIcon.MessageType.INFO);
		//			}
		//		};

		trayIcon.setImageAutoSize(true);
		//trayIcon.addActionListener(actionListener);
		//trayIcon.addMouseListener(mouseListener);
		trayIcon.addActionListener(this);

		//		trayIcon.addMouseListener(new MouseAdapter()
		//		  {
		//		      public void mouseReleased(MouseEvent e)
		//		      {
		//		          if (e.isPopupTrigger())
		//		          {
		//		              jpopup.setLocation(e.getX(), e.getY());
		//		              jpopup.setInvoker(jpopup);
		//		              jpopup.setVisible(true);
		//		          }
		//		      }
		//		  });



		try {
			SystemTray tray = SystemTray.getSystemTray();
			tray.add(trayIcon);
		} catch (AWTException e) {
			System.err.println("TrayIcon could not be added.");
		}
		//set the command before display a message
		//trayIcon.setActionCommand("Client started!");
		//trayIcon.displayMessage("Host Manager", "Host Manager started", TrayIcon.MessageType.INFO);
		//trayIcon.setImage(runImage);

	}

	private void addFixedMenus() {
		popup.addSeparator();		

		popup.add(demoMenu);
		popup.add(modeMenu);
		popup.addSeparator();
		addCustMenuItem();

		popup.addSeparator();
		popup.add(setItem);
		popup.add(refreshItem);
		popup.add(logMenu);
		popup.add(exitItem);
	}

	public void actionPerformed(ActionEvent e)
	{
		String command = e.getActionCommand();
		dialog.printMsg("Command="+command);
		System.out.println("Command="+command);
		if(command!=null)
		{
			if("Refresh".equalsIgnoreCase(command))
			{
				refreshMenu();
				return;
			}

			String host = custCfg.getValueByKey(command);
			if(host!=null)
			{
				//executed it directly
				try
				{
					dialog.printMsg("Command="+host);
					System.out.println("Command ="+ host);
					Process process = Runtime.getRuntime().exec(host);
					Thread stdoutThread = new Thread(new StreamDrainer(process.getInputStream(), dialog));   
					Thread stderrThread = new Thread(new StreamDrainer(process.getErrorStream(), dialog));   
					stdoutThread.start();
					stderrThread.start();

				}catch(Exception exp)
				{
					exp.printStackTrace();
				}
			}
			else
			{
				//HSLT setting
				if("Settings".equalsIgnoreCase(command))
				{
					SettingDialog.displayDialog();
					//System.out.println("Setting hostname, username, password for HSLT.");
				}
				else if("DemoHost".equalsIgnoreCase(command)||"RemoteDesktop".equalsIgnoreCase(command))
				{
					JOptionPane pane = new JOptionPane();
					pane.setMessage("The DemoHost is not active, it will be started in a few minutes.");
					pane.showMessageDialog(null, "The DemoHost is not active, it will be started in a few minutes.");
					//FIXME:
					DemoBackgroundThread thread = new DemoBackgroundThread(trayIcon);
					thread.start();

				}else if("DemoHostReady".equalsIgnoreCase(command))
				{
					System.out.println("Current running command:"+ command);
					try{
						Runtime.getRuntime().exec("c:\\WINDOWS\\system32\\mstsc.exe D:\\Document\\zhangdj2.rdp");
					}catch(Exception exp)
					{
						exp.printStackTrace();
					}
					trayIcon.setActionCommand(null);
				}
			}



		}

	}

	public void addMenuItem(String command)
	{
		MenuItem item = new MenuItem(command);
		item.addActionListener(this);
		popup.add(item);

	}

	public void addWindowsMenuItem()
	{
		Set keyset =winCfg.getMachines();
		Iterator it = keyset.iterator();
		while(it.hasNext())
		{
			String machine=(String)it.next();
			if(machine!=null && !"".equals(machine.trim()))
			{
				//addMenuItem(machine);
				createWinMenuItem(machine);
			}
		}

	}


	public void createWinMenuItem(String machine)
	{
		Menu item = new Menu(machine);		
		ApplicationConfig appCfg = ApplicationConfig.getInstance();
		List cmdList = appCfg.getWindowsCmdList();
		CustActionListener custListener = new CustActionListener(dialog);
		String hostName = winCfg.getValueByKey(machine);
		custListener.setHostName(hostName);
		custListener.setOSType(WINDOWS);
		for(int i=0; i<cmdList.size(); i++)
		{
			MenuItem mi = new MenuItem((String)cmdList.get(i));
			item.add(mi);
			mi.addActionListener(custListener);
		}
		popup.add(item);
		hostMenuList.add(item);
	}


	public void addLinuxMenuItem()
	{
		Set keyset =linuxCfg.getMachines();
		SortedSet sortedKeySet = new TreeSet(keyset);
		Iterator it = sortedKeySet.iterator();
		while(it.hasNext())
		{
			String machine=(String)it.next();
			if(machine!=null && !"".equals(machine.trim()))
			{
				//addMenuItem(machine);
				createLinuxMenuItem(machine);
			}
		}
	}

	public void createLinuxMenuItem(String machine)
	{
		Menu item = new Menu(machine);		
		ApplicationConfig appCfg = ApplicationConfig.getInstance();
		List cmdList = appCfg.getLinuxCmdList();
		CustActionListener custListener = new CustActionListener(dialog);
		String hostName = linuxCfg.getValueByKey(machine);
		custListener.setHostName(hostName);
		custListener.setOSType(LINUX);

		for(int i=0; i<cmdList.size(); i++)
		{
			MenuItem mi = new MenuItem((String)cmdList.get(i));
			item.add(mi);
			mi.addActionListener(custListener);
		}
		popup.add(item);
		hostMenuList.add(item);
	}
	public void addCustMenuItem()
	{
		Set keyset =custCfg.getMachines();
		Iterator it = keyset.iterator();
		while(it.hasNext())
		{
			String machine=(String)it.next();
			if(machine!=null && !"".equals(machine.trim()))
			{
				addMenuItem(machine);
			}
		}
	}

	public void refreshMenu()
	{
		popup.removeAll();
		//		Iterator it =hostMenuList.iterator();
		//		while(it.hasNext())
		//		{
		//			popup.remove((MenuItem)it.next());
		//		}
		//		hostMenuList.clear();
		linuxCfg.reload();
		winCfg.reload();
		addLinuxMenuItem();
		popup.addSeparator();
		addWindowsMenuItem();
		addFixedMenus();
	}

}
