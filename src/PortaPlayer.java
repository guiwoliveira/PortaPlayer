import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.io.IOException;

public class PortaPlayer extends JFrame implements ActionListener {

    private final JButton playButton;
    private final JButton pauseButton;
    private final JButton stopButton;
    private final JButton fileButton;
    private final JLabel audioNameLabel = new JLabel("Audio name");
    private JSlider volumeSlider = new JSlider();
    private Clip clip;
    private float audioVolume;

    public PortaPlayer() {
        // --------- Frame
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setSize(600, 400);
        setResizable(false);
        setLocationRelativeTo(null);

        // --------- Label
        audioNameLabel.setBounds(20, 0, 20, 20);
        audioNameLabel.setFont(new Font("Arial", Font.PLAIN, 20));

        JLabel volumeLabel = new JLabel("Volume");
        volumeLabel.setFont(new Font("Arial", Font.PLAIN, 20));

        // --------- Panel
        JPanel header = new JPanel();
        header.setPreferredSize(new Dimension(600, 60));
        header.setLayout(new GridLayout(1, 2, 10, 10));
        header.setBackground(Color.lightGray);

        JPanel centerPanel = new JPanel();
        centerPanel.setPreferredSize(new Dimension(600, 100));
        centerPanel.setLayout(new FlowLayout());

        JPanel footer = new JPanel();
        footer.setPreferredSize(new Dimension(600, 100));
        footer.setLayout(new GridLayout(1, 3, 10, 10));
        footer.setBackground(Color.lightGray);

        // --------- Slider
        volumeSlider = new JSlider();
        volumeSlider.setMinimum(-80);
        volumeSlider.setMaximum(0);
        volumeChange();

        // --------- Buttons
        // --------- Play
        playButton = new JButton("Play");
        playButton.setPreferredSize(new Dimension(50, 30));
        playButton.setFocusable(false);
        playButton.setFont(new Font("Arial", Font.PLAIN, 20));
        playButton.setEnabled(false);
        playButton.addActionListener(this);

        // --------- Pause
        pauseButton = new JButton("Pause");
        pauseButton.setPreferredSize(new Dimension(50, 30));
        pauseButton.setFocusable(false);
        pauseButton.setFont(new Font("Arial", Font.PLAIN, 20));
        pauseButton.setEnabled(false);
        pauseButton.addActionListener(this);

        // --------- Stop
        stopButton = new JButton("Stop");
        stopButton.setPreferredSize(new Dimension(50, 30));
        stopButton.setFocusable(false);
        stopButton.setFont(new Font("Arial", Font.PLAIN, 20));
        stopButton.setEnabled(false);
        stopButton.addActionListener(this);

        // --------- Choose file
        fileButton = new JButton("Choose file");
        fileButton.setFocusable(false);
        fileButton.setFont(new Font("Arial", Font.PLAIN, 20));
        fileButton.addActionListener(this);

        // --------- Add
        add(header, BorderLayout.NORTH);
        header.add(audioNameLabel);
        header.add(fileButton);

        add(centerPanel, BorderLayout.CENTER);
        centerPanel.add(volumeLabel);
        centerPanel.add(volumeSlider);

        add(footer, BorderLayout.SOUTH);
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

    private void volumeChange() {
        volumeSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                audioVolume = volumeSlider.getValue();
                System.out.println(audioVolume);

                if(clip != null) {
                    FloatControl fc = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
                    fc.setValue(audioVolume);
                }
            }
        });
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
                clip.start();

                audioNameLabel.setText("Playing: " + fileChooser.getSelectedFile().getName());

                playButton.setEnabled(false);
                pauseButton.setEnabled(true);
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