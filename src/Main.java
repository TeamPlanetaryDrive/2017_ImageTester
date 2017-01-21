import java.awt.BorderLayout;
import java.awt.Canvas;

import javax.swing.*;

import org.opencv.core.Core;

public class Main {
	
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		
		JFrame frame = new JFrame();		
		frame.add(new ImagePanel(), BorderLayout.CENTER);
		frame.setSize(480, 600);
		
		frame.setVisible(true);
		
		frame.repaint();
		
		
	}

}
