package b100.installer;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;

public class Sound {
	
	private byte[] data;
	
	public Sound(InputStream in) {
		try {
			data = Utils.readAll(in);
		}catch (Exception e) {
			System.err.println("Could not read sound!");
			e.printStackTrace();
		}finally {
			try {
				in.close();
			}catch (Exception e) {}
		}
	}
	
	public void play() {
		if(data == null) {
			return;
		}
		AudioInputStream stream = null;
		try {
			stream = AudioSystem.getAudioInputStream(new ByteArrayInputStream(data));
			
			Clip clip = AudioSystem.getClip();
			clip.open(stream);
			clip.addLineListener(event -> {
				if(event.getType().equals(LineEvent.Type.STOP)) {
					event.getLine().close();
				}
			});
			FloatControl control = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
			control.setValue(control.getMinimum() + ((control.getMaximum() - control.getMinimum()) * 0.75f));
			clip.start();
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				stream.close();
			}catch (Exception e) {}
		}
	}
	
	/////////////////////////////////////////
	
	public static Sound click = null;
	
	public static void init() {
		click = new Sound(Sound.class.getResourceAsStream("/click.wav"));
	}
}
