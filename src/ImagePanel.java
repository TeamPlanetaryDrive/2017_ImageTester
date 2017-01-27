import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class ImagePanel extends JPanel{

	private BufferedImage img;
	Mat frame;
	
	
	public ImagePanel() {
		try {
	    	   img = ImageIO.read(new File("src/1ftH2ftD0Angle0Brightness.jpg"));
	    	   frame = Imgcodecs.imread("src/1ftH2ftD0Angle0Brightness.jpg");
//	          
//	    	   img = ImageIO.read(new File("src/squares-ib8.jpg"));
//	    	   frame = Imgcodecs.imread("src/squares-ib8.jpg");
	    	   
	    	   this.test();
	    	   
		} catch (IOException ex) {
			// handle exception...
			ex.printStackTrace();
		}
	}
	
	
	protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(img, 0, 0, this); // see javadoc for more info on the parameters            
    }
	
	//TODO create matrix of images, original, binary, hsv, thresholded
	public void test() {
		Mat blurredImage = new Mat();
		Mat hsvImage = new Mat();
		Mat mask = new Mat();
		Mat morphOutput = new Mat();
		
		//remove noise
		Imgproc.blur(frame, blurredImage, new Size(7, 7));
		Imgproc.cvtColor(blurredImage, hsvImage, Imgproc.COLOR_BGR2HSV);
		
		//110 70  80
		//220 100 100
		//Core.inRange(hsvImage, new Scalar(20, 10, 6), new Scalar(180, 84, 25), mask);//now source is a mask
		Core.inRange(hsvImage, new Scalar(140 / 2, 255*86/100, 255*15/100), new Scalar(90, 255, 255*50/100), mask);
		
		Mat dilateElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(12, 12));//24, 24
		Mat erodeElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(1, 1));//12, 12
		
		Imgproc.erode(mask, morphOutput, erodeElement);
		Imgproc.erode(mask, morphOutput, erodeElement);
		
		Imgproc.dilate(mask, morphOutput, dilateElement);
		Imgproc.dilate(mask, morphOutput, dilateElement);

		List<MatOfPoint> contours = new ArrayList<>();
		List<Rect> rects = new ArrayList<>();
		Mat hierarchy = new Mat();
		
		//List of contour areas
		List<Double> areas = new ArrayList<>();
		
		//Variables for distance calculation
		double Tin = 0.833;
		double Tpix = 78;//width of tape from image
		int FOVpix = 480;
		double theta = 1;
		
		double dist; //in inches
		
		Imgproc.findContours(morphOutput, contours, hierarchy, Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE);
		
		// if any contour exist...
		if (hierarchy.size().height > 0 && hierarchy.size().width > 0) {
			// for each contour, display it in blue
			for (int idx = 0; idx >= 0; idx = (int) hierarchy.get(0, idx)[0]) {
				Imgproc.drawContours(hsvImage, contours, idx, new Scalar(250, 250, 250));
				
				rects.add(Imgproc.boundingRect(contours.get(idx)));
				//System.out.println(rects.get(idx));
				
				Imgproc.rectangle(hsvImage, rects.get(idx).tl(), rects.get(idx).br(), new Scalar(250, 250, 250));
				//Next: height and width
				//rects.get(idx).height          Height
				//rects.get(idx).width           Width
				System.out.println(rects.get(idx));
				// 57/640 = x/480
				//Camera FOVs
				//Axis206          640x480
				//				   54 degree horiz angle of view total
				
				//Distance 3ft,      could go to 10
				
				//Have:
				//	(Target rect width in inches)			Tin = 15
				//	(Target rect width in pixels)			Tpix = from image
				//	(FOV camera width in pixels)			FOVpix = 640
				//	(Half of Horizontal angle of view)		theta = 27 deg 
				//													0.471239 rad
				
				//Don't Have:
				//	(FOV camera width in inches)			FOVin
				
				//Relationships:
				//	Tin / Tpix = FOVin / FOVpix
				//	FOVin = 2*w = 2*d*tan(theta)
				//	tan(theta) = w/d
				
				//Substitutions:
				//	Tin / Tpix = (2*d*tan(theta)) / FOVpix
				//	d = Tin*FOVpix / (2*Tpix*tan(theta))
				//	d = 15 * 640   / (2*from_im*tan(27))
				
				//Things to think about:
				//	Do it for both rectangles and average the distances
				//	8 ft away? = 96 inches
				
				Tpix = rects.get(idx).height;
				
				dist = (Tin * FOVpix) / (2*Tpix*Math.tan(theta));
				dist = dist * Math.cos(0.5);
				System.out.println(dist);
				
				double area = Imgproc.contourArea(contours.get(idx));
				//System.out.println(area);
				areas.add(area);
				//blah!!!!
			}
		}
		 
		Mat disp = new Mat();
		
		//System.out.println(mask.channels());
		
		
		Imgproc.cvtColor(hsvImage, disp, Imgproc.COLOR_HSV2BGR);
		
		
		BufferedImage newImg = new BufferedImage(disp.cols(), disp.rows(), BufferedImage.TYPE_3BYTE_BGR);
		 
		 
		byte[] data = ((DataBufferByte) newImg.getRaster().getDataBuffer()).getData();
		disp.get(0, 0, data);
		
		img = newImg;
	}
	
}
