import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfRect;
import org.opencv.core.Scalar;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;

/**
 *
 * @author Joatan Sampaio
 * @date 10-24-2022
 */
public class Driver extends JFrame {

	private static final long serialVersionUID = 1L;

	private int CAMERA_ID = 1;
	static protected final int CAMERA_WIDTH = 720;
	static protected final int CAMERA_HEIGHT = 480;
	
	static boolean faceCaptureEnabled = false;
	static boolean handCaptureEnabled = false;
	
	Scalar faceRectangleColor = new Scalar(0, 128, 0);
	Scalar handRectangleColor = new Scalar(128, 0, 0);

	VideoCapture camera = null;
	CameraController cameraController;

	Mat frame = new Mat();
	MatOfByte matOfByte = new MatOfByte();
	
	CascadeClassifier handDetector = new CascadeClassifier("xmls\\hand.xml");

	MatOfRect handDetections = new MatOfRect();

	JPanel cameraPanel;
	private JPanel utilsPanel;

	private JButton startBtn;
	private JButton pauseBtn;
	private JButton faceCapBtn;

	public static JLabel label;

	public Driver() {

		System.load("C:\\opencv\\opencv\\build\\x64\\vc15\\bin\\opencv_java460.dll");
		System.out.println("Starting...");
		initComponents();

	}

	private void initComponents() {
		
		System.out.println("Initializating Frames.");
		
		cameraPanel = new JPanel();
		utilsPanel = new JPanel();
		
		startBtn = new JButton("Start");
		pauseBtn = new JButton("Pause");
		faceCapBtn = new JButton("Face Capture");
		
		label = new JLabel("Click to Start!");
		
		System.out.println("Starting Camera Controller...");
		System.out.println("Starting Camera Controller Thread...");

		pauseBtn.setEnabled(false);

		System.out.println("Setting Layout and Loading components");
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS));
		cameraPanel.setPreferredSize(new Dimension(720, 480));

		startBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				System.out.println("Connecting Camera Device: " + CAMERA_ID);
				cameraController = new CameraController(CAMERA_ID, frame, matOfByte, cameraPanel);
				cameraController.start();
				cameraController.running = true;

				startBtn.setEnabled(false);
				pauseBtn.setEnabled(true);

			}
		});

		pauseBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				pauseBtn.setEnabled(false); 
				startBtn.setEnabled(true); 
				CameraController.camera.release();
				cameraController.running = false;
			}
		});
		
		faceCapBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if(faceCaptureEnabled) {
					faceCaptureEnabled = false;
					handCaptureEnabled = false;
				}else {
					faceCaptureEnabled = true;
					handCaptureEnabled = true;
				}
				
			}
		});

		utilsPanel.add(startBtn);
		utilsPanel.add(pauseBtn);
		utilsPanel.add(label);
		utilsPanel.add(faceCapBtn);

		getContentPane().add(cameraPanel, BorderLayout.CENTER);
		getContentPane().add(utilsPanel, BorderLayout.PAGE_END);
		
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		pack();

	}

	@SuppressWarnings("unused")
	private static void openDirectory() throws IOException {
		File directory = new File("C:\\Program Files\\");
		Desktop.getDesktop().open(directory);
	}

	public static void main(String args[]) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				new Driver().setVisible(true);
			}
		});
	}
}