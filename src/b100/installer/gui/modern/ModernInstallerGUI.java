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

import javax.swing.JFrame;
import javax.swing.JPanel;

public class ModernInstallerGUI implements MouseListener, MouseMotionListener, KeyListener {
	
	private static ModernInstallerGUI instance;
	
	public static ModernInstallerGUI getInstance() {
		return instance;
	}
	
	private JFrame frame;
	private JPanel panel;
	
	public DefaultRenderer renderer;
	
	public GuiScreen screen;
	
	private volatile boolean repaint = false;
	
	private Runnable tickHandler = new TickHandler();
	
	private ModernInstallerGUI() {
		if(instance != null) {
			throw new IllegalStateException("Instance already exists!");
		}
		instance = this;

		renderer = new DefaultRenderer();
		
		Renderer.instance = renderer;
		FontRenderer.instance = new FontRenderer(renderer);
		
		initFrame();
		
		setScreen(null);
		
		long lastTick = System.currentTimeMillis();
		long tickTime = 1000 / 60;
		
		while(true) {
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
			if(screen != null && screen.isInitialized()) {
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
		
		// Input Listeners
		panel.addMouseMotionListener(this);
		frame.setFocusTraversalKeysEnabled(false);	// Must be set otherwise pressing tab doesn't fire a key event
		frame.addKeyListener(this);
		panel.addMouseListener(this);
		
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
	
	public boolean isKeyPressed(int key) {
		// Needed for Shift + Tab to cycle backwards
		// TODO
		return false;
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
		new ModernInstallerGUI();
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
			
			screen.mouseEvent(button, pressed, mouseX, mouseY);
		}
	}
	
	private void keyEvent(int key, boolean pressed) {
		if(screen != null) {
			screen.keyEvent(key, pressed);
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		mouseMoved(e.getX(), e.getY());
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		mouseMoved(e.getX(), e.getY());
	}

	@Override
	public void mousePressed(MouseEvent e) {
		mouseEvent(e.getX(), e.getY(), e.getButton(), true);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		mouseEvent(e.getX(), e.getY(), e.getButton(), false);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		keyEvent(e.getKeyCode(), true);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		keyEvent(e.getKeyCode(), false);
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
	
}
