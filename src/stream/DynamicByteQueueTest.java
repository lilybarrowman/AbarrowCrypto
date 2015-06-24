package stream;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

public class DynamicByteQueueTest {

  @Test
  public void test() throws InterruptedException {
    testSameThread();
    testManyReads();
    testManyWrites();
    testMultiThreaded();
  }
  
  private void testMultiThreaded() throws InterruptedException {
    StringBuilder builder = new StringBuilder();
    builder.append('M');
    for (int n = 0; n <4097; n++) {
      builder.append('o');
    }  
    final byte[] bigLongBytes = builder.toString().getBytes();
    final DynamicByteQueue d = new DynamicByteQueue();
    Thread reader = new Thread(new Runnable(){
      @Override
      public void run() {
        byte[] lotsOfbytes = new byte[8000];
        int read = d.read(lotsOfbytes);
        byte[] actuallyRead = Arrays.copyOf(lotsOfbytes, read);
        assertArrayEquals(bigLongBytes, actuallyRead);
      }
    });
    Thread writer = new Thread(new Runnable(){
      @Override
      public void run() {
        try {
          Thread.sleep(100);
          int chunk = 100;
          for (int start = 0, len = bigLongBytes.length; start < len; start += chunk) {
            d.write(bigLongBytes, start, (chunk + start) > len ? len - start : chunk);
          }
          Thread.sleep(0);
        } catch (InterruptedException e1) {
          e1.printStackTrace();
        }
        d.doneWriting();
      }
    });
    reader.start();
    writer.start();
    //ensure that both threads finish before we move on
    reader.join();
    writer.join();
  }

  private void testManyWrites() {
    byte[] testBytes = new byte[]{0, 1, 2};
    DynamicByteQueue d = new DynamicByteQueue();
    d.write(Arrays.copyOfRange(testBytes, 0, 1));
    d.write(Arrays.copyOfRange(testBytes, 1, 2));
    d.write(Arrays.copyOfRange(testBytes, 2, 3));
    d.doneWriting();
    byte[] readBuffer = new byte[3];
    d.read(readBuffer);
    assertArrayEquals(testBytes, readBuffer);
    assert(d.read(readBuffer) == -1);
  }
  
  private void testManyReads() {
    byte[] testBytes = new byte[]{0, 1, 2};
    DynamicByteQueue d = new DynamicByteQueue();
    d.write(testBytes);
    d.doneWriting();
    byte[] readBufferA = new byte[1];
    byte[] readBufferB = new byte[1];
    byte[] readBufferC = new byte[1];
    d.read(readBufferA);
    d.read(readBufferB);
    d.read(readBufferC);
    assertArrayEquals(testBytes, new byte[] { readBufferA[0], readBufferB[0], readBufferC[0] } );
    assert(d.read(readBufferA) == -1);
  }

  private void testSameThread() {
    byte[] testBytes = "Never trust an evil wizard.".getBytes();
    DynamicByteQueue d = new DynamicByteQueue();
    d.write(testBytes);
    d.doneWriting();
    byte[] readBuffer = new byte[40];
    int read = d.read(readBuffer);
    assertArrayEquals(testBytes, Arrays.copyOf(readBuffer, read));
    assert(d.read(readBuffer) == -1);
  }

}
