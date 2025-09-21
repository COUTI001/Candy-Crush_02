package com.coutigames.main;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;

public class Sound {

    private Clip clip;

    public static final Sound Music_01 = new Sound("/Music_01.wav");
    public static final Sound Combo_1 = new Sound("/Combo_1.wav");
    public static final Sound Combo_2 = new Sound("/Combo_2.wav");
    public static final Sound Movimento = new Sound("/Movimento.wav");

    private Sound(String name) {
        try {
            URL url = Sound.class.getResource(name);
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(url);

            // Obter o formato de áudio original
            AudioFormat baseFormat = audioInputStream.getFormat();
            // Criar um formato de áudio decodificado
            AudioFormat decodedFormat = new AudioFormat(
                AudioFormat.Encoding.PCM_SIGNED,
                baseFormat.getSampleRate(),
                16,
                baseFormat.getChannels(),
                baseFormat.getChannels() * 2,
                baseFormat.getSampleRate(),
                false
            );

            // Obter um AudioInputStream decodificado
            AudioInputStream decodedAudioInputStream = AudioSystem.getAudioInputStream(decodedFormat, audioInputStream);

            clip = AudioSystem.getClip();
            clip.open(decodedAudioInputStream);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public void play() {
        if (clip != null) {
            new Thread(() -> {
                clip.stop();
                clip.setFramePosition(0);
                clip.start();
            }).start();
        }
    }
    // Metodo para aplicação do loop do som
    public void loop() {
        if (clip != null) {
            new Thread(() -> {
                clip.loop(Clip.LOOP_CONTINUOUSLY);
            }).start();
        }
    }

    public void stop() {
        if (clip != null) {
            clip.stop();
        }
    }
      //Metodo para aplicação de volume
    public void setVolume(float value) {
        if (clip != null) {
            FloatControl volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            volumeControl.setValue(value);
        }
    }
}
