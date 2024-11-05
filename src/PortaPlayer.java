import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class PortaPlayer extends JFrame implements ActionListener {

    private final JButton playButton;
    private final JButton pauseButton;
    private final JButton stopButton;
    private final JButton submitButton;
    private final JTextField textField;
    private Clip clip;

    public PortaPlayer() {
        // --------- Panel
        JPanel footer = new JPanel();
        footer.setPreferredSize(new Dimension(400, 100));
        footer.setLayout(new GridLayout(1, 3, 10, 10));

        // --------- Frame
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());
        setSize(600, 400);
        setResizable(false);
        setLocationRelativeTo(null);

        // --------- Text Field
        textField = new JTextField();
        textField.setPreferredSize(new Dimension(450, 40));
        textField.setFont(new Font("Consolas", Font.PLAIN, 20));

        // --------- Buttons
        playButton = new JButton("Play");
        playButton.setPreferredSize(new Dimension(100, 50));
        playButton.setFocusable(false);
        playButton.setEnabled(false);
        playButton.addActionListener(this);
        pauseButton = new JButton("Pause");
        pauseButton.setPreferredSize(new Dimension(100, 50));
        pauseButton.setFocusable(false);
        pauseButton.setEnabled(false);
        pauseButton.addActionListener(this);
        stopButton = new JButton("Stop");
        stopButton.setPreferredSize(new Dimension(100, 50));
        stopButton.setFocusable(false);
        stopButton.setEnabled(false);
        stopButton.addActionListener(this);
        submitButton = new JButton("Submit");
        submitButton.setFocusable(false);
        submitButton.addActionListener(this);

        // --------- Add
        add(textField);
        add(submitButton);
        add(footer);
        footer.add(playButton);
        footer.add(pauseButton);
        footer.add(stopButton);

        // --------- VIsible
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == playButton) {
            playSong();
        } else if(e.getSource() == pauseButton) {
            pauseSong();
        } else if(e.getSource() == stopButton) {
            stopSong();
        } else if(e.getSource() == submitButton) {
            closeClip();
            submitPath();
        }
    }

    private void closeClip() {
        if(clip != null) {
            clip.close();
        }
    }

    private void submitPath() {
        try {
            File song = new File(textField.getText());
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(song);
            clip = AudioSystem.getClip();
            clip.open(audioStream);
            playButton.setEnabled(true);
            stopButton.setEnabled(true);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Ops... The system cannot find the specified path :(", "Erro", JOptionPane.WARNING_MESSAGE);
        } catch (LineUnavailableException e) {
            JOptionPane.showMessageDialog(null, "Something went wrong :(", "Erro", JOptionPane.WARNING_MESSAGE);
        } catch (UnsupportedAudioFileException e) {
            JOptionPane.showMessageDialog(null, "Hmm... Unsupported file type :/", "Erro", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void stopSong() {
        clip.setMicrosecondPosition(0);
        clip.stop();
        System.out.println("Stop");
        playButton.setEnabled(true);
        pauseButton.setEnabled(false);
        submitButton.setEnabled(true);
    }

    private void playSong() {
        clip.start();
        System.out.println("Play");
        playButton.setEnabled(false);
        pauseButton.setEnabled(true);
    }

    private void pauseSong() {
        clip.stop();
        System.out.println("Pause");
        playButton.setEnabled(true);
        pauseButton.setEnabled(false);
    }
}

