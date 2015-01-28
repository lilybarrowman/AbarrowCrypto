package stenography;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.filechooser.FileFilter;

import wavtools.WavSampleData;
import core.CryptoUtils;
import des.TripleDES;

public class StenographyDemo implements ActionListener {

  private byte[] source;
  private BufferedImage imageMask;
  private WavSampleData wavMask;
  private String fileChooserState;

  private File stenFile;

  private JFileChooser fileChooser;
  private JFileChooser followUpFileChooser;

  private JFrame frame;
  private JPasswordField passwordInput;

  private JButton selectSourceButton;
  private JButton selectMaskButton;
  private JLabel statusLabel;
  private JButton saveButton;
  private JButton openButton;
  
  private ButtonGroup maskTypeGroup;
  private JRadioButton imageMaskButton;
  private JRadioButton wavMaskButton;
  
  private PNGStenographer pngStenographer;
  private WAVStenographer wavStenographer;

  private static final FileFilter imagesFileFilter = new FormatListFileFilter(new String[]{"jpg", "jpeg", "png"}, "PNG or JPG File");
  private static final FileFilter pngFileFilter = new FormatListFileFilter(new String[]{"png"}, "PNG File");
  private static final FileFilter wavFileFilter = new FormatListFileFilter(new String[]{"wav"}, "WAV File");

  public StenographyDemo() {
    fileChooserState = "";

    frame = new JFrame("Stenography Demo");
    passwordInput = new JPasswordField("abc");

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
    
    imageMaskButton = new JRadioButton("Image File");
    wavMaskButton = new JRadioButton("WAV File");
    
    maskTypeGroup = new ButtonGroup(); 
    maskTypeGroup.add(imageMaskButton);
    maskTypeGroup.add(wavMaskButton);
    maskTypeGroup.setSelected(imageMaskButton.getModel(), true);

    Container contents = frame.getContentPane();
    contents.setLayout(new BoxLayout(contents, BoxLayout.Y_AXIS));

    contents.add(new JLabel("Password"));
    passwordInput.setPreferredSize(new Dimension(200, 20));
    contents.add(passwordInput);
    contents.add(imageMaskButton);
    contents.add(wavMaskButton);
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
    imageMaskButton.addActionListener(this);
    wavMaskButton.addActionListener(this);
    
    pngStenographer = new PNGStenographer();
    wavStenographer = new WAVStenographer();
  }
  
  public boolean isUsingImageMask() {
    return maskTypeGroup.isSelected(imageMaskButton.getModel());
  }

  public boolean validate() {

    String problems = "";
    if (source == null) {
      problems = "Select a source.";
    } else{
      if (isUsingImageMask()) {
        if (imageMask == null) {
          problems = "Select a mask.";
        } else if (!pngStenographer.canSourceHoldData(source.length, imageMask)) {
          problems = "Select a larger mask or a smaller source.";
        }
      } else {
        if (wavMask == null) {
          problems = "Select a mask.";
        } else if (!wavStenographer.canSourceHoldData(source.length, wavMask)) {
          problems = "Select a larger mask or a smaller source.";
        }
      }
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
    if (event.getSource() == imageMaskButton) {
      validate();
    } else if (event.getSource() == wavMaskButton) {
      validate();
    } else if (event.getSource() == openButton) {
      fileChooserState = "ChoosingSten";
      fileChooser.setFileFilter(isUsingImageMask() ? pngFileFilter : wavFileFilter);
      fileChooser.showOpenDialog(frame);
    } else if (event.getSource() == saveButton) {
      fileChooserState = "ChoosingDest";
      fileChooser.setFileFilter(isUsingImageMask() ? pngFileFilter : wavFileFilter);
      fileChooser.showSaveDialog(frame);
    } else if (event.getSource() == selectMaskButton) {
      fileChooserState = "ChoosingMask";
      fileChooser.setFileFilter(isUsingImageMask() ? imagesFileFilter : wavFileFilter);
      fileChooser.showOpenDialog(frame);
    } else if (event.getSource() == selectSourceButton) {
      fileChooserState = "ChoosingSource";
      fileChooser.setFileFilter(null);
      fileChooser.showOpenDialog(frame);
    } else if (event.getSource() == followUpFileChooser) {
      File outFile = followUpFileChooser.getSelectedFile();
      if (outFile == null) {
        return;
      }
      StenData stenData = null;
      try {
        if (isUsingImageMask()) {
          stenData = pngStenographer.decode(ImageIO.read(stenFile));
        } else {
          stenData = wavStenographer.decode(new WavSampleData(new FileInputStream(stenFile)));
        }
      } catch (Exception e) {
        statusLabel.setText("Error reading stenographic file.");
      }

      TripleDES cipher = new TripleDES(getPaddedPassword());
      byte[] decoded = cipher.decrypt(stenData.bytes);

      FileOutputStream outStream;
      try {
        outStream = new FileOutputStream(outFile);
        outStream.write(decoded, 0, stenData.plainLen);
        outStream.close();
        statusLabel.setText("Plaintext saved!");
      } catch (FileNotFoundException e) {
        statusLabel.setText("Error writing plaintext file.");
      } catch (IOException e) {
        statusLabel.setText("Error writing plaintext file.");

      }
    } else if (event.getSource() == fileChooser) {
      String actionCommand = event.getActionCommand();
      if (actionCommand.equals(JFileChooser.APPROVE_SELECTION)) {
        if (fileChooserState.equals("ChoosingSource")) {

          File file = fileChooser.getSelectedFile();

          try {
            byte[] input = Files.readAllBytes(file.toPath());
            source = input;
            validate();

          } catch (IOException e) {
            statusLabel.setText("Error opening source file.");
          }
        } else if (fileChooserState.equals("ChoosingMask")) {
          File file = fileChooser.getSelectedFile();
          try {
            if (isUsingImageMask()) {
              imageMask = ImageIO.read(file);
            } else {
              wavMask = new WavSampleData(new FileInputStream(file));
            }
            validate();

          } catch (IOException e) {
            statusLabel.setText("Error opening mask file.");
          }
        } else if (fileChooserState.equals("ChoosingSten")) {
          stenFile = fileChooser.getSelectedFile();
          followUpFileChooser.showSaveDialog(frame);
        } else if (fileChooserState.equals("ChoosingDest")) {
          File outputFile = fileChooser.getSelectedFile();

          TripleDES cipher = new TripleDES(getPaddedPassword());
          byte[] encoded = cipher.encrypt(source);

          try {
            if (isUsingImageMask()) {
              pngStenographer.encode(new StenData(encoded, source.length), imageMask, outputFile);
            } else {
              wavStenographer.encode(new StenData(encoded, source.length), wavMask, outputFile);
            }
            
            statusLabel.setText("Stenographic file saved!");
          } catch (Exception e) {
            statusLabel.setText("Error saving file.");
          }
        }
      }
    }
  }

  public byte[] getPaddedPassword() {
    char[] password = passwordInput.getPassword();
    byte[] paddedPassword = new byte[24];
    return CryptoUtils.utf8CharArrayToByteAray(password, paddedPassword);
  }

}
