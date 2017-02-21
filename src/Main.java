import java.awt.BorderLayout;
import java.awt.Canvas;

import javax.swing.*;

import org.opencv.core.Core;

public class Main {
	
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
		frame.add(new ImagePanel(), BorderLayout.CENTER);
		frame.setSize(640, 480);
		
		frame.setVisible(true);
		
		frame.repaint();
		
		
	}

}
