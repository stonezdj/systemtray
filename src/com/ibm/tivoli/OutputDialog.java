package com.ibm.tivoli;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class OutputDialog extends JDialog implements ActionListener{
	
	
    protected JTextArea textArea;
    private final static String newline = "\n";
    
   
    public void printMsg(String msg)
    {
  
   		textArea.append(msg+newline);
//   		System.out.println("Print message"+ msg);
    }

	public OutputDialog()
	{
		this.setModal(false);
		this.setTitle("Command Output");
		this.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		
		this.setLayout(new BorderLayout());
		textArea = new JTextArea("", 5, 10);
		textArea.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(textArea);
//        textArea.setBounds(new Rectangle(0, 0, 100, 400));
        scrollPane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener(){
        	public void adjustmentValueChanged(AdjustmentEvent e){
        		textArea.select(textArea.getHeight()+1000,0);
        	}});

        
        add(scrollPane);
//        add(textArea);
	}

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event dispatch thread.
     */
    private void createAndShowGUI() {
        //Create and set up the window.
        
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

        //Add contents to the window.
//        frame.add(new OutputPanel());

        //Display the window.
//        frame.pack();
//        frame.setVisible(true);
    	
    	  
    }

    public static void main(String[] args) {
    	Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

    	OutputDialog dialog = new OutputDialog();
//    	dialog.setPreferredSize(new Dimension(200,400));
    	dialog.setLocation(new Point(dim.width-200, 0));
    	dialog.setSize(new Dimension(200,400));
    	
    	dialog.setVisible(true);
    	
    	dialog.printMsg("Sample");
    	dialog.printMsg("Sample");
    	dialog.printMsg("Sample");
    	
    }

	public void displayPanel() {
		//Schedule a job for the event dispatch thread:
        //creating and showing this application's GUI.
//		
//        javax.swing.SwingUtilities.invokeLater(new Runnable() {
//            public void run() {
                createAndShowGUI();

//            }
//        });
        
	}

	public void actionPerformed(ActionEvent e) {
		System.err.println("Event recieved!");
		this.setVisible(!isVisible());
	}
    


}
