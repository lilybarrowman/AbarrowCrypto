package counter;

public abstract class Counter {
  public abstract byte[] increment();
  public abstract byte[][] increment(int count);
  public abstract byte[] currentValue();
  public abstract void reset();
}
