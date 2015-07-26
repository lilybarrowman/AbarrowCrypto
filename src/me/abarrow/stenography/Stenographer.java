package me.abarrow.stenography;

import java.io.File;

abstract public class Stenographer<T, E extends Throwable> {
  public abstract boolean canSourceHoldData(int numBytes, T source);
  public abstract void encode(StenData data, T source, File dest) throws E;
  public abstract StenData decode(T source) throws E;
}
