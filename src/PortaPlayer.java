import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;

public class PortaPlayer extends JFrame implements ActionListener {

    private final JButton playButton;
    private final JButton pauseButton;
    private final JButton stopButton;
    private final JButton fileButton;

    private final JLabel audioNameLabel = new JLabel("Audio name");
    private final JSlider volumeSlider = new JSlider();
    private Clip clip;

    ArrayList<String> audioPlaylist = new ArrayList<>();
    FileWriter playlistDB;

    public PortaPlayer() {

        // --------- Frame
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("PortaPlayer");
        setLayout(new BorderLayout());
        setSize(900, 400);
        setResizable(false);
        setLocationRelativeTo(null);

        // --------- Panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setPreferredSize(new Dimension(550, 400));

        JPanel header = new JPanel();
        header.setPreferredSize(new Dimension(600, 60));
        header.setLayout(new GridLayout(2, 1, 10, 10));
        header.setBackground(Color.lightGray);

        JPanel centerPanel = new JPanel();
        centerPanel.setPreferredSize(new Dimension(600, 100));
        centerPanel.setLayout(new FlowLayout());

        JPanel playlistPanel = new JPanel();
        playlistPanel.setBackground(Color.white);
        playlistPanel.setPreferredSize(new Dimension(350, 400));

        JPanel footer = new JPanel();
        footer.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0));
        footer.setBackground(Color.lightGray);

        // --------- Label
        audioNameLabel.setBounds(20, 0, 20, 20);
        audioNameLabel.setFont(new Font("Arial", Font.PLAIN, 20));

        JLabel volumeLabel = new JLabel("Volume");
        volumeLabel.setFont(new Font("Arial", Font.PLAIN, 20));

        // --------- Slider
        volumeSlider.setMinimum(-80);
        volumeSlider.setMaximum(0);
        volumeChange();

        // --------- Buttons
        // --------- Play
        playButton = new JButton("Play");
        playButton.setPreferredSize(new Dimension(100, 100));
        playButton.setFocusable(false);
        playButton.setFont(new Font("Arial", Font.PLAIN, 20));
        playButton.setEnabled(false);
        playButton.addActionListener(this);

        // --------- Pause
        pauseButton = new JButton("Pause");
        pauseButton.setPreferredSize(new Dimension(100, 100));
        pauseButton.setFocusable(false);
        pauseButton.setFont(new Font("Arial", Font.PLAIN, 20));
        pauseButton.setEnabled(false);
        pauseButton.addActionListener(this);

        // --------- Stop
        stopButton = new JButton("Stop");
        stopButton.setPreferredSize(new Dimension(100, 100));
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
        add(mainPanel, BorderLayout.WEST);
        add(playlistPanel, BorderLayout.EAST);

        mainPanel.add(header, BorderLayout.NORTH);
        header.add(audioNameLabel);
        header.add(fileButton);

        mainPanel.add(centerPanel, BorderLayout.CENTER);
        centerPanel.add(volumeLabel);
        centerPanel.add(volumeSlider);

        mainPanel.add(footer, BorderLayout.SOUTH);
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
                volumeControl();
            }
        });
    }

    private void volumeControl() {
        float audioVolume = volumeSlider.getValue();
        System.out.println(audioVolume);

        if(clip != null) {
            FloatControl fc = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            fc.setValue(audioVolume);
        }
    }

    private void closeClip() {
        if(clip != null) {
            clip.close();
        }
    }

    private void fillPlaylist(String audioName) {
        audioPlaylist.add(audioName);
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

                volumeControl();
                fillPlaylist(fileChooser.getSelectedFile().getName());

                playlistDB = new FileWriter("playlistDataBase.txt");

                for(String s : audioPlaylist) {
                    playlistDB.write(s +"\n");
                }

                playlistDB.close();

                // -------- Debug ArrayList -------
                for (String s : audioPlaylist) {
                    System.out.println(s);
                }

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
        stopButton.setEnabled(false);
        fileButton.setEnabled(true);
    }

    private void playSong() {
        clip.start();
        System.out.println("Play");
        playButton.setEnabled(false);
        pauseButton.setEnabled(true);
        stopButton.setEnabled(true);
    }

    private void pauseSong() {
        clip.stop();
        System.out.println("Pause");
        playButton.setEnabled(true);
        pauseButton.setEnabled(false);
    }
}