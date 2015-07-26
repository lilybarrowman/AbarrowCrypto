package me.abarrow.chat;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import me.abarrow.cipher.BlockCipher;
import me.abarrow.cipher.Cipher;
import me.abarrow.cipher.CompoundBlockCipher;
import me.abarrow.cipher.aes.AES;
import me.abarrow.cipher.blowfish.TwoFish;
import me.abarrow.cipher.mode.CTRMode;
import me.abarrow.cipher.serpent.Serpent;
import me.abarrow.core.CryptoException;
import me.abarrow.core.CryptoUtils;
import me.abarrow.hash.Hasher;
import me.abarrow.hash.sha.SHA3;
import me.abarrow.hash.sha.SHA3Mode;
import me.abarrow.mac.hmac.HMAC;
import me.abarrow.pbkdf2.PBKDF2;
import me.abarrow.random.CTRModeRandom;

public class ChatUI implements MouseMotionListener, MouseListener {

  private Container options;
  private Container chat;
  private JFrame frame;
  /**
   * The textfield which stores the
   */
  private JPasswordField passwordInput;
  private JButton lockPasswordButton;
  private JTextArea messageInput;
  private JTextArea messageOutput;
  private JTextArea keyIVInput;
  private JButton keyIVInputLock;
  private JTextArea statusOutput;
  private JButton encryptButton;
  private JButton decryptButton;
  private JLabel entropyLabel;

  private Hasher h = new SHA3(SHA3Mode.SHA3, 64);
  private HMAC hmac = new HMAC(h);
  private Cipher cipher;
  // private byte[] rawEntropy = new byte[2];
  private byte[] rawEntropy = new byte[h.getBlockBytes() * 4];
  private byte[] keySalt;
  private byte[] ivSalt;

  /**
   * The initial hash of the initial plaintext password. This is used to key the
   * hmac for key IVs. This value needs to be cleared from ram as soon as the
   * key is generated.
   * 
   * Main security threat: Rainbow table attack to retrieve plaintext password.
   * Main security defense: The rainbow table would need to be impractically
   * large.
   */
  private volatile byte[] hashPassword;

  private volatile byte[] key;

  private int entropyCollected = 0;
  private int totalEntropy = 8 * rawEntropy.length;

  private boolean isDoneHashingSalt = false;
  private boolean hasHashedKey = false;
  private boolean isKeyReady = false;

  private DateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

  private final String OFFLINE_TITLE = "Abarrow Chat Codec";
  
  public static void main(String[] args) {
    new ChatUI();
  }
  
  public ChatUI() {

    frame = new JFrame(OFFLINE_TITLE);

    Container contents = frame.getContentPane();
    contents.setLayout(new GridBagLayout());

    Container options = new Container();
    options.setLayout(new GridBagLayout());

    GridBagConstraints c = new GridBagConstraints();

    c.anchor = GridBagConstraints.NORTH;

    c.fill = GridBagConstraints.HORIZONTAL;
    c.gridx = 0;
    c.gridy = 0;
    c.gridwidth = 1;

    options.add(new JLabel("Password"), c);

    passwordInput = new JPasswordField("");
    passwordInput.setPreferredSize(new Dimension(300, 20));
    c.gridx = 1;
    c.gridwidth = 2;
    options.add(passwordInput, c);

    c.gridx = 0;
    c.gridy = 3;
    c.gridwidth = 1;
    entropyLabel = new JLabel(getEntropyLabel());
    options.add(entropyLabel, c);

    c.gridx = 0;
    c.gridy = 4;
    c.gridwidth = 1;
    lockPasswordButton = new JButton("Lock In Password");
    lockPasswordButton.addMouseListener(this);
    options.add(lockPasswordButton, c);

    c.gridx = 0;
    c.gridy = 5;
    c.gridwidth = 3;
    statusOutput = new JTextArea(15, 10);
    statusOutput.setEditable(false);
    statusOutput.setLineWrap(true);
    statusOutput.addMouseMotionListener(this);
    options.add(new JScrollPane(statusOutput), c);

    c.gridwidth = 1;
    c.gridx = 0;
    c.gridy = 0;
    contents.add(options, c);

    Container chat = new Container();
    chat.setLayout(new GridBagLayout());
    GridBagConstraints d = new GridBagConstraints();
    d.anchor = GridBagConstraints.NORTH;

    d.fill = GridBagConstraints.HORIZONTAL;
    d.gridx = 0;
    d.gridy = 0;
    d.gridwidth = 2;

    d.gridy = 0;
    d.gridx = 0;
    chat.add(new JLabel("Key IV"), d);

    d.gridy = 1;
    d.gridx = 0;
    keyIVInput = new JTextArea(4, 40);
    keyIVInput.setLineWrap(true);
    chat.add(new JScrollPane(keyIVInput), d);

    d.gridy = 2;
    keyIVInputLock = new JButton("Validate and Lock In Key IV");
    keyIVInputLock.setEnabled(false);
    keyIVInputLock.addMouseListener(this);
    chat.add(keyIVInputLock, d);

    d.gridy = 3;
    d.gridx = 0;
    d.gridwidth = 1;
    chat.add(new JLabel("Message Input"), d);

    d.gridy = 4;
    d.gridheight = 2;

    messageInput = new JTextArea(4, 40);
    messageInput.setLineWrap(true);
    messageInput.setWrapStyleWord(true);
    chat.add(new JScrollPane(messageInput), d);

    d.gridx = 1;
    d.gridheight = 1;
    encryptButton = new JButton("Encrypt");
    encryptButton.setEnabled(false);
    encryptButton.addMouseListener(this);
    chat.add(encryptButton, d);

    d.gridy = 5;
    decryptButton = new JButton("Decrypt");
    decryptButton.setEnabled(false);
    decryptButton.addMouseListener(this);
    chat.add(decryptButton, d);

    d.gridy = 6;
    d.gridx = 0;
    chat.add(new JLabel("Mesage Output"), d);

    d.gridy = 7;
    d.gridx = 0;
    d.gridwidth = 2;
    messageOutput = new JTextArea(6, 40);
    messageOutput.setLineWrap(true);
    messageOutput.setEditable(false);
    chat.add(new JScrollPane(messageOutput), d);

    c.gridx = 6;
    c.gridy = 0;
    contents.add(chat, c);

    log("Application started.");

    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setResizable(false);
    frame.pack();
    frame.setVisible(true);

  }

  private String getEntropyLabel() {
    return "Raw Entropy: " + entropyCollected + "/" + totalEntropy;
  }

  private boolean hasCollectedEnoughEntropy() {
    return entropyCollected == totalEntropy;
  }

  @Override
  public void mouseMoved(MouseEvent event) {
    if (!hasCollectedEnoughEntropy()) {
      addBitOfEntropy(event.getXOnScreen());
      addBitOfEntropy(event.getYOnScreen());
      addBitOfEntropy((int) System.nanoTime());
      entropyLabel.setText(getEntropyLabel());
      if (hasCollectedEnoughEntropy()) {
        log("Sufficient raw entropy collected.");
        Thread hashRawEntropy = new Thread() {
          @Override
          public void run() {
            synchronized (h) {
              int halfRawEntropyLength = rawEntropy.length / 2;
              byte[] rawKeySalt = new byte[halfRawEntropyLength];
              byte[] rawIvSalt = new byte[halfRawEntropyLength];

              System.arraycopy(rawEntropy, 0, rawKeySalt, 0, halfRawEntropyLength);
              System.arraycopy(rawEntropy, halfRawEntropyLength, rawIvSalt, 0, halfRawEntropyLength);
              CryptoUtils.fillWithZeroes(rawEntropy);

              keySalt = h.addBytes(rawKeySalt).computeHash();
              CryptoUtils.fillWithZeroes(rawKeySalt);

              ivSalt = h.addBytes(rawIvSalt).computeHash();
              CryptoUtils.fillWithZeroes(rawIvSalt);
              isDoneHashingSalt = true;
              log("Raw entropy hashed.");
            }
          }
        };
        hashRawEntropy.start();
      }
    }
  }

  private void addBitOfEntropy(int bitOfEntropy) {
    if (!hasCollectedEnoughEntropy()) {
      rawEntropy[entropyCollected / 8] |= (bitOfEntropy & 0x1) << (entropyCollected % 8);
      // System.out.println(CryptoUtils.byteArrayToBinaryString(new
      // byte[]{rawEntropy[entropyCollected / 8]}) + " " + (bitOfEntropy & 0x1)
      // + " " + bitOfEntropy);
      entropyCollected++;
    }
  }

  private synchronized void log(String message) {
    statusOutput.append(dateformat.format(new Date()) + " : " + message + "\n");
  }

  @Override
  public void mouseClicked(MouseEvent event) {
    if (event.getComponent().equals(lockPasswordButton)) {
      if (!isDoneHashingSalt) {
        log("You must first generate enough salt.");
        return;
      }
      final char[] rawPassword = passwordInput.getPassword();
      if (rawPassword.length < 24) {
        log("Your password must be at least 24 characters long.");
        return;
      }
      if (rawPassword.length > 64) {
        log("Your password will be truncated to 64 characters.");
      }

      log("Locking in password.");
      lockPasswordButton.setEnabled(false);

      Thread lockInPasswordThread = new Thread() {
        @Override
        public void run() {
          byte[] passwordBytse = CryptoUtils.utf8CharArrayToByteAray(rawPassword, new byte[64]);
          hashPassword = h.addBytes(passwordBytse).computeHash();
          String keyIV = CryptoUtils.byteArrayToHexString(keySalt)
              + CryptoUtils.byteArrayToHexString(hmac.computeHash(hashPassword, keySalt));
          log("Your key IV string is [" + keyIV + "].");
          CryptoUtils.fillWithZeroes(rawPassword);
          passwordInput.setText("");
          passwordInput.setEditable(false);
          keyIVInput.setText(keyIV);
          keyIVInputLock.setEnabled(true);
          hasHashedKey = true;
        }
      };
      lockInPasswordThread.start();
    } else if (event.getComponent().equals(keyIVInputLock)) {
      String rawKeyIVString = keyIVInput.getText();

      // make people's lives less miserable
      final String keyIVString = rawKeyIVString.replace("[", "").replace("]", "").replace(" ", "");

      if (keyIVString.length() != (4 * h.getHashByteLength())) {
        log("That key IV is not of a valid length.");
        return;
      }
      keyIVInputLock.setEnabled(false);
      Thread lockInKeyIV = new Thread() {
        

        @Override
        public void run() {
          byte[] keyIV = null;
          byte[] keyIVHmac = null;
          try {
            keyIV = CryptoUtils.hexStringToBytes(keyIVString.substring(0, 2 * h.getHashByteLength()));
            keyIVHmac = CryptoUtils.hexStringToBytes(keyIVString.substring(2 * h.getHashByteLength()));
          } catch(IllegalArgumentException e) {
            log("Invalid key IV. Key IVs must be valid hexadecimal strings.");
            keyIVInputLock.setEnabled(true);
            return;
          }
          if (!hmac.checkHash(hashPassword, keyIV, keyIVHmac)) {
            log("Invalid key IV. Someone could be trying to attack you.");
            keyIVInputLock.setEnabled(true);
            return;
          } else {
            log("Key IV locked in starting to generate the main key. This may take a long time.");
            key = PBKDF2.generateKey(hmac, hashPassword, keyIV, 100000, hmac.getHMACByteLength());
            //key = PBKDF2.generateKey(hmac, hashPassword, keyIV, 10, hmac.getHMACByteLength());
            log("Finished generating the main key, now generating sub keys.");
            isKeyReady = true;

            byte[] hashedKey0 = h.addBytes(key).computeHash();
            byte[] hashedKey1 = h.addBytes(hashedKey0).computeHash();
            byte[] hashedKey2 = h.addBytes(hashedKey1).computeHash();
            byte[] aesKey = new byte[32];
            byte[] twoFishKey = new byte[32];
            byte[] serpentKey = new byte[32];

            System.arraycopy(hashedKey0, 0, serpentKey, 0, 32);
            System.arraycopy(hashedKey1, 0, twoFishKey, 0, 32);
            System.arraycopy(hashedKey2, 0, aesKey, 0, 32);
            CryptoUtils.fillWithZeroes(hashedKey1);
            CryptoUtils.fillWithZeroes(hashedKey2);

            cipher = new CTRMode(new CompoundBlockCipher(new BlockCipher[] { new AES(aesKey),
                new TwoFish(twoFishKey), new Serpent(serpentKey) }), getNewIV());
            log("Finished genearting sub keys.");
            encryptButton.setEnabled(true);
            decryptButton.setEnabled(true);
            keyIVInput.setEditable(false);
          }

        }
      };
      lockInKeyIV.start();
    } else if (event.getComponent().equals(encryptButton)) {
      byte[] iv = getNewIV();
      cipher.setIV(iv);
      byte[] plainText = messageInput.getText().getBytes();
      byte[] cipherText = null;
      try {
        cipherText = cipher.encrypt(plainText);
      } catch (CryptoException e) {
        log(e.getMessage());
        return;
      }
      byte[] concat = new byte[iv.length + cipherText.length];
      System.arraycopy(iv, 0, concat, 0, iv.length);
      System.arraycopy(cipherText, 0, concat, iv.length, cipherText.length);
      
      byte[] mac = hmac.computeHash(key, concat);
      
      StringBuilder mes = new StringBuilder();
      messageOutput.setText(mes.append(CryptoUtils.byteArrayToHexString(iv)).append(
          CryptoUtils.byteArrayToHexString(cipherText)).append(CryptoUtils.byteArrayToHexString(mac)).toString());

    } else if (event.getComponent().equals(decryptButton)) {
      String messageText = messageInput.getText();
      int hmacLength = hmac.getHMACByteLength();
      if(messageText.length() <= 2 * (16 + hmacLength)) {
        log("Incomplete or invalid message, it is too short.");
        return;
      }
      String ivHex = messageText.substring(0, 32);
      String hmacHex = messageText.substring(messageText.length() - 2 * hmacLength);
      String cipherTextHex = messageText.substring(32, messageText.length() - hmacHex.length());
      
      byte[] iv = null;
      byte[] hmacBytes= null;
      byte[] cipherText = null;
      try {
        iv = CryptoUtils.hexStringToBytes(ivHex);
        hmacBytes= CryptoUtils.hexStringToBytes(hmacHex);
        cipherText = CryptoUtils.hexStringToBytes(cipherTextHex);
      }catch (IllegalArgumentException e) {
        log("Invalid message. Messages must be valid hexadecimal strings.");
        return;
      }
      
      byte[] concat = new byte[iv.length + cipherText.length];
      System.arraycopy(iv, 0, concat, 0, iv.length);
      System.arraycopy(cipherText, 0, concat, iv.length, cipherText.length);
      
      if(!hmac.checkHash(key, concat, hmacBytes)){
        log("Incomplete or invalid message, HMAC failed.");
        return;
      }
      
      cipher.setIV(iv);
      byte[] plainTextBytes;
      try {
        plainTextBytes = cipher.decrypt(cipherText);
        messageOutput.setText(new String(plainTextBytes));
      } catch (CryptoException e) {
        log(e.getMessage());
      }
      
      
    }
  }

  private byte[] getNewIV() {
    ivSalt = h.addBytes(ivSalt).computeHash();
    byte[] iv = new byte[16];
    System.arraycopy(ivSalt, 0, iv, 0, iv.length);
    return iv;
  }

  private void reset() {
    isDoneHashingSalt = false;
    hasHashedKey = false;
    entropyCollected = 0;
    entropyLabel.setText(getEntropyLabel());
  }

  @Override
  public void mouseEntered(MouseEvent arg0) {
  }

  @Override
  public void mouseDragged(MouseEvent arg0) {
  }

  @Override
  public void mouseExited(MouseEvent arg0) {
  }

  @Override
  public void mousePressed(MouseEvent arg0) {
  }

  @Override
  public void mouseReleased(MouseEvent arg0) {
  }

}
