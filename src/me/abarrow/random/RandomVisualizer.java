package me.abarrow.random;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;

import me.abarrow.core.CryptoUtils;

public class RandomVisualizer {
  
  public static void start(final Random random) {
    final JFrame frame = new JFrame("Random Visuals");

    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    
    JPanel panel = new JPanel(){
      private static final long serialVersionUID = -7079636092880229318L;
      
      private BufferedImage img;
      
      @Override
      public void repaint() {
        img = null;
        super.repaint();
      }

      @Override
      public void paint(Graphics g) {
         
        int width = this.getWidth();
        int height = this.getHeight();
        
        if (img == null) {
          long before = System.nanoTime();

          img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
          
          byte[] content = new byte[4 * width * height];
          random.nextBytes(content);
          
          img.setRGB(0, 0, width, height, CryptoUtils.intArrayFromBytes(content, 0, content.length), 0, width);
          
          long after = System.nanoTime();
          
          System.out.println("Random Visualization took " + Math.round((after - before) / 1000000D) + "ms");
        }
        
        g.drawImage(img, 0, 0, this);
      }
      
      
    };
    
    panel.setPreferredSize(new Dimension(800, 600));
  
    frame.getContentPane().add(panel);
  
    frame.addMouseListener(new MouseListener() {

      @Override
      public void mouseClicked(MouseEvent e) {
        frame.repaint();
      }

      @Override
      public void mousePressed(MouseEvent e) {
        // TODO Auto-generated method stub
        
      }

      @Override
      public void mouseReleased(MouseEvent e) {
        // TODO Auto-generated method stub
        
      }

      @Override
      public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub
        
      }

      @Override
      public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub
        
      }
      
    });
    
    frame.pack();
  
    frame.setVisible(true);
  }

}
