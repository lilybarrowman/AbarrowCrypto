package stenography;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

import core.CryptoUtils;
import core.PairityBitType;
import des.TripleDES;

public class StenographyDemo implements ActionListener {

  private byte[] source;
  private BufferedImage mask;
  private String state;

  private File stenFile;

  private JFileChooser fileChooser;
  private JFileChooser followUpFileChooser;

  private JFrame frame;
  private JTextField passwordInput;

  private JButton selectSourceButton;
  private JButton selectMaskButton;
  private JLabel statusLabel;
  private JButton saveButton;
  private JButton openButton;

  private static final FileFilter imagesFileFilter = new FileFilter() {

    @Override
    public boolean accept(File file) {
      if (!file.isFile()) {
        return true;
      }
      String name = file.getName();
      int dotIndex = name.lastIndexOf('.');
      if (dotIndex < 0) {
        return false;
      }
      String type = name.substring(dotIndex + 1).toLowerCase();
      return type.equals("jpg") || type.equals("jpeg") || type.equals("png");
    }

    @Override
    public String getDescription() {
      return "PNG or JPG File";
    }

  };

  private static final FileFilter pngFileFilter = new FileFilter() {

    @Override
    public boolean accept(File file) {
      if (!file.isFile()) {
        return true;
      }
      String name = file.getName();
      int dotIndex = name.lastIndexOf('.');
      if (dotIndex < 0) {
        return false;
      }
      String type = name.substring(dotIndex + 1).toLowerCase();
      return type.equals("png");
    }

    @Override
    public String getDescription() {
      return "PNG file";
    }

  };

  public StenographyDemo() {
    state = "";

    frame = new JFrame("Stenography Demo");
    passwordInput = new JTextField("abc");

    Path root = Paths.get("").toAbsolutePath();
    fileChooser = new JFileChooser(root.toFile());
    fileChooser.setFileFilter(null);
    followUpFileChooser = new JFileChooser(root.toFile());
    fileChooser.setFileFilter(null);
    selectSourceButton = new JButton("Select Source");
    selectMaskButton = new JButton("Select Mask");
    statusLabel = new JLabel("Select a source and a mask.");
    saveButton = new JButton("Save");
    saveButton.setEnabled(false);
    openButton = new JButton("Open");

    Container contents = frame.getContentPane();
    contents.setLayout(new BoxLayout(contents, BoxLayout.Y_AXIS));

    contents.add(new JLabel("Password"));
    passwordInput.setPreferredSize(new Dimension(200, 20));
    contents.add(passwordInput);
    contents.add(selectSourceButton);
    contents.add(selectMaskButton);
    contents.add(statusLabel);
    contents.add(saveButton);
    contents.add(openButton);

    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.pack();
    frame.setVisible(true);

    openButton.addActionListener(this);
    saveButton.addActionListener(this);
    selectMaskButton.addActionListener(this);
    selectSourceButton.addActionListener(this);
    fileChooser.addActionListener(this);
    followUpFileChooser.addActionListener(this);
  }

  public boolean validate() {

    String problems = "";
    if (source == null) {
      problems = "Select a source.";
    } else if (mask == null) {
      problems = "Select a mask.";
    } else if ((mask.getWidth() * mask.getHeight()) < source.length) {
      problems = "Select a larger mask or a smaller source.";
    }

    statusLabel.setText(problems);
    boolean validated = (problems.length() == 0);
    saveButton.setEnabled(validated);
    return validated;
  }

  public static void start() {
    new StenographyDemo();
  }

  @Override
  public void actionPerformed(ActionEvent event) {
    if (event.getSource() == openButton) {
      state = "ChoosingSten";
      fileChooser.setFileFilter(pngFileFilter);
      fileChooser.showOpenDialog(frame);
    } else if (event.getSource() == saveButton) {
      state = "ChoosingDest";
      fileChooser.setFileFilter(pngFileFilter);
      fileChooser.showSaveDialog(frame);
    } else if (event.getSource() == selectMaskButton) {
      state = "ChoosingMask";
      fileChooser.setFileFilter(imagesFileFilter);
      fileChooser.showOpenDialog(frame);
    } else if (event.getSource() == selectSourceButton) {
      state = "ChoosingSource";
      fileChooser.setFileFilter(null);
      fileChooser.showOpenDialog(frame);
    } else if (event.getSource() == followUpFileChooser) {
      File outFile = followUpFileChooser.getSelectedFile();
      StenData stenData = null;

      try {
        stenData = PNGStenographer.decode(ImageIO.read(stenFile));
      } catch (IOException e) {
        statusLabel.setText("Error reading stenographic file.");
      }

      

      TripleDES cipher = new TripleDES(getPaddedPassword());
      byte[] decoded = cipher.decrypt(stenData.bytes);

      FileOutputStream outStream;
      try {
        outStream = new FileOutputStream(outFile);
        outStream.write(decoded, 0, stenData.plainLen);
        outStream.close();
        System.out.println("Plaintext saved!");
      } catch (FileNotFoundException e) {
        statusLabel.setText("Error writing plaintext file.");
      } catch (IOException e) {
        statusLabel.setText("Error writing plaintext file.");

      }
    } else if (event.getSource() == fileChooser) {
      String actionCommand = event.getActionCommand();
      if (actionCommand.equals(JFileChooser.APPROVE_SELECTION)) {
        if (state.equals("ChoosingSource")) {

          File file = fileChooser.getSelectedFile();

          try {
            byte[] input = Files.readAllBytes(file.toPath());
            source = input;
            validate();

          } catch (IOException e) {
            statusLabel.setText("Error opening source file.");
          }
        } else if (state.equals("ChoosingMask")) {
          File file = fileChooser.getSelectedFile();
          try {
            mask = ImageIO.read(file);
            validate();

          } catch (IOException e) {
            statusLabel.setText("Error opening mask file.");
          }
        } else if (state.equals("ChoosingSten")) {
          stenFile = fileChooser.getSelectedFile();
          System.out.println(stenFile);
          followUpFileChooser.showSaveDialog(frame);
        } else if (state.equals("ChoosingDest")) {
          File outputFile = fileChooser.getSelectedFile();

          TripleDES cipher = new TripleDES(getPaddedPassword());
          byte[] encoded = cipher.encrypt(source);
          
          try {
            PNGStenographer.encode(new StenData(encoded, source.length), mask, outputFile);
            System.out.println("Stenographic image saved!");
          } catch (IOException e) {
            statusLabel.setText("Error saving file.");
          }
        }
      }
    }
  }

  public byte[] getPaddedPassword() {
    String password = passwordInput.getText();
    byte[] passwordBytes = passwordInput.getText().getBytes();

    byte[] paddedPassword = new byte[24];
    System.arraycopy(passwordBytes, 0, paddedPassword, 0, Math.min(24, passwordBytes.length));
    return paddedPassword;
  }

}
