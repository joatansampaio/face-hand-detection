import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;

import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;

class CameraController extends Thread {

	protected final Scalar faceRectangleColor = new Scalar(0, 128, 0);
	protected final Scalar handRectangleColor = new Scalar(128, 0, 0);
	protected final int CAMERA_WIDTH = 720;
	protected final int CAMERA_HEIGHT = 480;
	
	int cameraId;

	static VideoCapture camera;
	
	// Mats
	Mat frame;
	MatOfByte matOfByte;

	//cameraPanel 
	JPanel cameraPanel;

	
	CascadeClassifier faceDetector = new CascadeClassifier("xmls\\haarcascade_frontalface_alt.xml");
	CascadeClassifier handDetector = new CascadeClassifier("xmls\\hand.xml");

	MatOfRect faceDetections = new MatOfRect();
	MatOfRect handDetections = new MatOfRect();

	public CameraController(int cameraId, Mat frame, MatOfByte matOfByte, JPanel cameraPanel) {
		this.frame = frame;
		this.matOfByte = matOfByte;
		this.cameraPanel = cameraPanel;
		this.cameraId = cameraId;
		System.out.println("Attempting to Open camera: " + cameraId);
		camera = new VideoCapture(cameraId);
		System.out.println("Success!");
	}

	protected volatile boolean running = false;

	public void setLabelText(JLabel label, String text) {
		label.setText(text);
	}

	@Override
	public void run() {
		synchronized (this) {
			while (running) {
				if (camera.grab()) {
					try {
						camera.retrieve(frame);
						Graphics g = cameraPanel.getGraphics();
							
						if(Driver.faceCaptureEnabled) {
							faceDetector.detectMultiScale(frame, faceDetections);
							for (Rect rect : faceDetections.toArray()) {
								Imgproc.rectangle(frame, new Point(rect.x, rect.y),
										new Point(rect.x + rect.width, rect.y + rect.height), faceRectangleColor, 2);
							}
						}

						if(Driver.handCaptureEnabled) {
							handDetector.detectMultiScale(frame, handDetections);
							for (Rect rect : handDetections.toArray()) {
								Imgproc.rectangle(frame, new Point(rect.x, rect.y),
										new Point(rect.x + rect.width, rect.y + rect.height), handRectangleColor, 2);
							}
						}
						
						Imgcodecs.imencode(".jpg", frame, matOfByte);
						Image im = ImageIO.read(new ByteArrayInputStream(matOfByte.toArray()));
						BufferedImage buff = (BufferedImage) im;

						
						if (g.drawImage(im, 0, 0, CAMERA_WIDTH, CAMERA_HEIGHT, 0, 0, buff.getWidth(), buff.getHeight(),
								null)) {
							if (running == false) {
								System.out.println("Pausing camera...");
								this.wait();
							}
						}
					} catch (Exception e) {
						System.out.println("An error occurred");
						e.printStackTrace();
					}
				}
			}
		}
	}
}