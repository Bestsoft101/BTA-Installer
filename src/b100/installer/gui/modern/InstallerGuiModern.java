package b100.installer.gui.modern;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JPanel;

import b100.installer.Utils;
import b100.installer.gui.modern.render.DefaultRenderer;
import b100.installer.gui.modern.render.FontRenderer;
import b100.installer.gui.modern.render.Renderer;
import b100.installer.gui.modern.screen.GuiMainMenu;
import b100.installer.gui.modern.screen.GuiScreen;
import b100.installer.gui.modern.screen.multimc.GuiInstallMultiMc;

public class InstallerGuiModern {
	
	private static InstallerGuiModern instance;
	
	public static InstallerGuiModern getInstance() {
		return instance;
	}
	
	private JFrame frame;
	private JPanel panel;
	
	public DefaultRenderer renderer;
	
	public GuiScreen screen;
	
	private Runnable tickHandler = new TickHandler();
	private Listeners listeners = new Listeners();

	private volatile boolean repaint = false;
	private volatile boolean running = false;
	
	private boolean holdingShift = false;
	
	private InstallerGuiModern() {
		if(instance != null) {
			throw new IllegalStateException("Instance already exists!");
		}
		instance = this;
		
		renderer = new DefaultRenderer();
		
		Renderer.instance = renderer;
		FontRenderer.instance = new FontRenderer(renderer);
		
		initFrame();

		File instancesFolder = Utils.getMultiMCInstancesFolder();
		if(instancesFolder != null) {
			System.out.println("Found Instances Folder: " + instancesFolder);	
			
			setScreen(new GuiInstallMultiMc(null, instancesFolder));
		}else {
			setScreen(null);
		}
		
		long lastTick = System.currentTimeMillis();
		long tickTime = 1000 / 60;
		
		running = true;
		while(running) {
			long now = System.currentTimeMillis();
			long delta = now - lastTick;
			lastTick = now;
			
			long sleepTime = tickTime - delta;
			if(sleepTime > 0) {
				try {
					Thread.sleep(sleepTime);
				}catch (Exception e) {}
			}
			
			EventQueue.invokeLater(tickHandler);
		}
	}
	
	class TickHandler implements Runnable {

		@Override
		public void run() {
			if(screen != null) {
				if(!screen.isInitialized()) {
					screen.init();
				}
				screen.tick();
			}
			if(repaint) {
				panel.repaint();
			}
		}
		
	}
	
	private void initFrame() {
		// Create Frame
		frame = new JFrame();
		frame.setTitle("Installer");
		frame.setBackground(Color.black);
		
		// Create Panel
		panel = new DrawPanel();
		panel.setBackground(Color.black);
		panel.setPreferredSize(new Dimension(854, 480));
		panel.setFocusable(false);
		panel.setDoubleBuffered(true);
		
		// Listeners
		panel.addMouseMotionListener(listeners);
		frame.setFocusTraversalKeysEnabled(false);	// Must be set to false, otherwise pressing tab doesn't fire a key event
		frame.addKeyListener(listeners);
		panel.addMouseListener(listeners);
		frame.addWindowListener(listeners);
		frame.addMouseWheelListener(listeners);
		
		// Finish Frame
		frame.add(panel);
		frame.pack();
		frame.setMinimumSize(new Dimension(320, 240));
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.setVisible(true);
	}
	
	private void render(Component component, Graphics g) {
		if(g == null) {
			return;
		}
		
//		System.out.println("Repaint!");
		repaint = false;
		
		renderer.update(component, g);
		
		if(screen != null) {
			if(!screen.isInitialized()) {
				screen.init();
			}
			
			int w = renderer.getWidth();
			int h = renderer.getHeight();
			
			if(w != screen.width || h != screen.height) {
				screen.setSize(w, h);
				screen.onResize();
			}
			
			screen.draw();
		}
	}
	
	public void setScreen(GuiScreen screen) {
		if(screen == null) {
			screen = new GuiMainMenu(null);
		}
		
		this.screen = screen;
		
		scheduleRepaint();
	}
	
	/**
	 * Good enough
	 */
	public boolean isShiftPressed() {
		return holdingShift;
	}
	
	public void scheduleRepaint() {
		repaint = true;
	}
	
	@SuppressWarnings("serial")
	class DrawPanel extends JPanel {
		
		@Override
		public void paint(Graphics g) {
			super.paint(g);
			render(this, g);
		}
		
	}
	
	public static void main(String[] args) {
		new InstallerGuiModern();
	}
	
	private void mouseMoved(int x, int y) {
		if(screen != null) {
			double scale = renderer.getScale();
			
			double mouseX = x / scale;
			double mouseY = y / scale;
			
			screen.mouseX = mouseX;
			screen.mouseY = mouseY;
		}
	}
	
	private void mouseEvent(int x, int y, int button, boolean pressed) {
		if(screen != null) {
			double scale = renderer.getScale();
			
			double mouseX = x / scale;
			double mouseY = y / scale;
			
			screen.mouseX = mouseX;
			screen.mouseY = mouseY;
			
			if(screen.isInitialized()) {
				screen.mouseEvent(button, pressed, mouseX, mouseY);	
			}
		}
	}
	
	private void keyEvent(int key, boolean pressed) {
		if(screen != null && screen.isInitialized()) {
			screen.keyEvent(key, pressed);
		}
	}
	
	private void scrollEvent(double verticalAmount, int x, int y) {
		if(screen != null && screen.isInitialized()) {
			double scale = renderer.getScale();
			
			double mouseX = x / scale;
			double mouseY = y / scale;
			
			screen.scrollEvent(-verticalAmount, mouseX, mouseY);
		}
	}
	
	private void close() {
		running = false;
		
		frame.dispose();
	}
	
	class Listeners implements MouseListener, MouseMotionListener, MouseWheelListener, KeyListener, WindowListener {
		@Override
		public void mouseDragged(MouseEvent e) {
			InstallerGuiModern.this.mouseMoved(e.getX(), e.getY());
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			InstallerGuiModern.this.mouseMoved(e.getX(), e.getY());
		}

		@Override
		public void mousePressed(MouseEvent e) {
			InstallerGuiModern.this.mouseEvent(e.getX(), e.getY(), e.getButton(), true);
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			InstallerGuiModern.this.mouseEvent(e.getX(), e.getY(), e.getButton(), false);
		}

		@Override
		public void keyPressed(KeyEvent e) {
			if(e.getKeyCode() == KeyEvent.VK_SHIFT) {
				holdingShift = true;
			}
			
			InstallerGuiModern.this.keyEvent(e.getKeyCode(), true);
		}

		@Override
		public void keyReleased(KeyEvent e) {
			if(e.getKeyCode() == KeyEvent.VK_SHIFT) {
				holdingShift = false;
			}
			
			InstallerGuiModern.this.keyEvent(e.getKeyCode(), false);
		}

		@Override
		public void windowClosing(WindowEvent e) {
			InstallerGuiModern.this.close();
		}

		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			InstallerGuiModern.this.scrollEvent(e.getPreciseWheelRotation(), e.getX(), e.getY());
		}

		@Override
		public void keyTyped(KeyEvent e) {
			
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			
		}

		@Override
		public void mouseExited(MouseEvent e) {
			
		}

		@Override
		public void windowOpened(WindowEvent e) {
			
		}

		@Override
		public void windowClosed(WindowEvent e) {
			
		}

		@Override
		public void windowIconified(WindowEvent e) {
			
		}

		@Override
		public void windowDeiconified(WindowEvent e) {
			
		}

		@Override
		public void windowActivated(WindowEvent e) {
			
		}

		@Override
		public void windowDeactivated(WindowEvent e) {
			
		}
	}
}
