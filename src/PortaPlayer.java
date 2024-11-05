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
    private final JButton fileButton;
    private JLabel audioName = new JLabel("Audio name");
    private Clip clip;

    public PortaPlayer() {
        // --------- Frame
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setSize(600, 400);
        setResizable(false);
        setLocationRelativeTo(null);

        // --------- Label
        audioName.setBounds(20, 0, 20, 20);

        // --------- Panel
        JPanel footer = new JPanel();
        footer.setPreferredSize(new Dimension(600, 100));
        footer.setLayout(new GridLayout(1, 3, 10, 10));
        footer.setBackground(Color.gray);

        JPanel header = new JPanel();
        header.setPreferredSize(new Dimension(600, 60));
        header.setLayout(new GridLayout(1, 2, 10, 10));
        header.setBackground(Color.lightGray);

        // --------- Buttons
        playButton = new JButton("Play");
        playButton.setPreferredSize(new Dimension(50, 30));
        playButton.setFocusable(false);
        playButton.setEnabled(false);
        playButton.addActionListener(this);
        pauseButton = new JButton("Pause");
        pauseButton.setPreferredSize(new Dimension(50, 30));
        pauseButton.setFocusable(false);
        pauseButton.setEnabled(false);
        pauseButton.addActionListener(this);
        stopButton = new JButton("Stop");
        stopButton.setPreferredSize(new Dimension(50, 30));
        stopButton.setFocusable(false);
        stopButton.setEnabled(false);
        stopButton.addActionListener(this);
        fileButton = new JButton("Choose file");
        fileButton.setFocusable(false);
        fileButton.addActionListener(this);

        // --------- Add
        header.add(audioName);
        header.add(fileButton);
        add(footer, BorderLayout.SOUTH);
        add(header, BorderLayout.NORTH);
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
        } else if(e.getSource() == fileButton) {
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
            JFileChooser fileChooser = new JFileChooser();

            int response = fileChooser.showOpenDialog(null);

            if(response == JFileChooser.APPROVE_OPTION) {
                closeClip();
                File song = new File(fileChooser.getSelectedFile().getAbsolutePath());
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(song);
                clip = AudioSystem.getClip();
                clip.open(audioStream);

                audioName.setText("Playing: " + fileChooser.getSelectedFile().getName());

                playButton.setEnabled(true);
                pauseButton.setEnabled(false);
                stopButton.setEnabled(true);
            }
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
        fileButton.setEnabled(true);
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

