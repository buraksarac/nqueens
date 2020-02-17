package org.qunix.bitset;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Core implementation
 * 
 * This is unsafe copy of https://github.com/buraksarac/bitset that each CRUD operation doesnt do a
 * position check if given position is valid, in range of size or not
 * 
 * @author bsarac
 *
 */
public class BitSet implements Iterable<Boolean> {

  /**
   * One set 1L
   */
  private static final long ONE_SET = 1L;
  /**
   * None set 0l
   */
  private static final long NONE_SET = 0l;
  /**
   * All set -1l
   */
  private static final long ALL_SET = -1l;

  /**
   * Default init capacity
   */
  private static final int DEFAULT_CAPACITY = 64;

  /**
   * empty list
   */
  private static final transient long[] DEFAULTCAPACITY_EMPTY_ELEMENTDATA = {};

  /**
   * The maximum size of array to allocate. Some VMs reserve some header words in an array. Attempts
   * to allocate larger arrays may result in OutOfMemoryError: Requested array size exceeds VM limit
   */
  private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

  /**
   * used internally for mod and division
   */
  private static final int LOG_64 = 6;

  /**
   * 64
   */
  private static final int LONG_SIZE = 1 << LOG_64;

  /**
   * used for getting mods
   */
  private static final int MOD = LONG_SIZE - 1;

  protected transient long[] bucket;
  protected int actualCapacity;
  protected int size;
  protected int capacity;

  /**
   * Default constructor, creates a new empty array
   */
  public BitSet() {
    this.capacity = DEFAULT_CAPACITY;
    this.actualCapacity = getActaulCapacity(this.capacity);
    this.bucket = new long[actualCapacity];
  }

  /**
   * Creates a empty new array with given capacity
   * 
   * @param capacity constructor param
   */
  public BitSet(int capacity) {
    if (capacity < 0) {
      throw new IllegalArgumentException("Invalid capacity " + capacity);
    }
    this.capacity = capacity;
    this.actualCapacity = getActaulCapacity(this.capacity);
    this.bucket = new long[actualCapacity];
  }

  /**
   * Returns true if bit at position 1 else 0
   *
   * Throws {@link IndexOutOfBoundsException} if position is not valid
   *
   * @param p position
   * @return boolean
   */
  public Boolean get(int p) {
    return (bucket[p >> LOG_64] & (ONE_SET << (p & (MOD)))) != 0;
  }

  /**
   * Returns 1 if bit at position 1 else 0
   *
   * Throws {@link IndexOutOfBoundsException} if position is not valid
   *
   * @param p position
   * @return 0 or 1
   */
  public byte getByte(int p) {
    return (byte) (get(p) ? 1 : 0);
  }

  /**
   * Switches bit status at position and returns new value
   * 
   * Throws {@link IndexOutOfBoundsException} if position is not valid
   * 
   * @param p position
   * @return boolean
   */
  public Boolean flip(int p) { // TODO we can have more than int range? Maybe add
                               // new BitSet with custom param
    return (bucket[p >> LOG_64] ^= (ONE_SET << (p & (MOD)))) != 0;
  }

  /**
   * sets bit at position according to given param: 1 if true otherwise 0
   * 
   * Throws {@link IndexOutOfBoundsException} if position is not valid
   * 
   * @param p position
   * @param on isOn or off
   * @return boolean
   */
  public Boolean onOff(int p, boolean on) {
    return on ? on(p) : off(p);
  }

  /**
   * sets bit at position 1
   * 
   * Throws {@link IndexOutOfBoundsException} if position is not valid
   * 
   * @param p position
   * @return boolean
   */
  public Boolean on(int p) {
    return (bucket[p >> LOG_64] |= (ONE_SET << (p & (MOD)))) != 0;
  }

  /**
   *
   * on method: sets bit at position 1 for the given range
   * 
   * Offset can be negative as fas as it doesnt out bound beginning (index 0) or positive as far as
   * doesnt reach end of set (size - 1) or 0 that only start parameter will be on
   * 
   *
   *
   * @param startInclusive starting bit
   * @param offset offset to set
   * 
   *        throws {@link IndexOutOfBoundsException} if position is not valid
   */
  public void on(int startInclusive, int offset) {
    int end = startInclusive + offset;
    if (startInclusive < 0 || startInclusive >= this.size || end > this.size) {
      throw new IndexOutOfBoundsException();
    }
    if (offset == 0) {
      on(startInclusive);
    } else if (offset < 0) {
      on(end + 1, -offset);
    } else {
      int index, endIndex;

      if ((index = startInclusive >> LOG_64) == (endIndex = end - 1 >> LOG_64)) {

        bucket[index] |= ALL_SET >>> (LONG_SIZE - offset) << startInclusive;

      } else {
        bucket[index] |= ALL_SET << (startInclusive & MOD);
        for (int i = index + 1; i < endIndex; i++) {
          bucket[i] = ALL_SET;
        }
        bucket[endIndex] |= ALL_SET >>> (LONG_SIZE - (end & MOD));
      }
    }

  }

  /**
   *
   * off method: sets bits at position 0 for the given range
   * 
   * Offset can be negative as fas as it doesnt out bound beginning (index 0) or positive as far as
   * doesnt reach end of set (size - 1) or 0 that only start parameter will be off
   * 
   *
   *
   * @param startInclusive starting bit
   * @param offset offset to set
   * 
   *        throws {@link IndexOutOfBoundsException} if position is not valid
   */
  public void off(int startInclusive, int offset) {
    int end = startInclusive + offset;
    if (startInclusive < 0 || startInclusive >= this.size || end > this.size) {
      throw new IndexOutOfBoundsException();
    }
    if (offset == 0) {
      off(startInclusive);
    } else if (offset < 0) {
      off(end + 1, -offset);
    } else {
      int index, endIndex;

      if ((index = startInclusive >> LOG_64) == (endIndex = end - 1 >> LOG_64)) {
        bucket[index] &= ~(ALL_SET >>> (LONG_SIZE - offset) << startInclusive);
      } else {
        bucket[index] &= ~(ALL_SET << (startInclusive & MOD));
        for (int i = index + 1; i < endIndex; i++) {
          bucket[i] = NONE_SET;
        }
        bucket[endIndex] &= ~(ALL_SET >>> (LONG_SIZE - (end & MOD)));
      }
    }
  }

  /**
   *
   * off method: sets bit at position 0
   * 
   *
   *
   * @param p position
   * 
   *        throws {@link IndexOutOfBoundsException} if position is not valid
   * @return boolean
   */
  public Boolean off(int p) {
    return (bucket[p >> LOG_64] &= ~(ONE_SET << (p & (MOD)))) != 0; // TODO does functional
                                                                    // really needed
  }

  /**
   *
   * ifOn method: returns an optional that supplier value only present if given position is set true
   *
   * 
   *
   *
   * @param <T> return type
   * @param position position
   * @param supplier supplier
   * @return Optional optional of supplier result
   */
  public <T> Optional<T> ifOn(int position, Supplier<T> supplier) {
    if (get(position)) {
      return Optional.of(supplier.get());
    }
    return Optional.ofNullable(null);
  }

  /**
   *
   * ifOff method: returns an optional that supplier value only present if given position is set
   * false
   *
   * 
   *
   *
   * @param <T> return type
   * @param position position
   * @param supplier supplier
   * @return Optional optional return type
   */
  public <T> Optional<T> ifOff(int position, Supplier<T> supplier) {
    if (!get(position)) {
      return Optional.of(supplier.get());
    }
    return Optional.ofNullable(null);
  }

  /**
   *
   * forEach method: iterate through 0 to this.size
   *
   * 
   *
   *
   * @param <T> return type
   * @param func func
   */
  public <T> void forEach(BiConsumer<Boolean, Integer> func) {
    for (int i = 0; i < this.size; i++) {
      func.accept((bucket[i >> LOG_64] & (ONE_SET << (i & (MOD)))) != 0, i);
    }
  }

  /**
   *
   * resize method: if new size greater than this size resizes this set and sets new values false
   *
   * 
   *
   *
   * @param newSize void
   */
  public void resize(int newSize) {
    if (newSize < this.size) {
      throw new IndexOutOfBoundsException();
    }
    if (size < newSize) {
      ensureCapacityInternal(newSize);
      this.size = newSize;
    }

  }


  /**
   * Returns true if all bits set to 1
   * 
   * @return boolean result
   */
  public boolean allSet() {
    /*
     * TODO tracking a onCount instance variable on update methods would get rid off all of this but
     * then on/off switches needs to do second call if current value was false
     */
    for (int i = 0; i < actualCapacity - 1; i++) {
      if (bucket[i] != ALL_SET) {
        return false;
      }
    }

    long val = bucket[actualCapacity - 1];
    int count = val == ALL_SET ? LONG_SIZE : 0;
    if (count == 0 && val != 0) {
      do {
        count++;
      } while ((val &= (val - 1)) > 0);
    }
    int totalSet = ((actualCapacity - 1) << LOG_64) + count;
    return totalSet == this.size;

  }

  /**
   *
   * reset method: sets all bits to 0
   *
   * 
   *
   * void
   */
  public void reset() {
    for (int i = 0; i < actualCapacity; i++) {
      bucket[i] = 0;
    }
  }

  /**
   *
   * onCount method: returns count of 0s
   *
   * 
   *
   *
   * @return int
   */
  public int onCount() {
    int count = 0;
    for (int i = 0; i < actualCapacity; i++) {
      if (bucket[i] == ALL_SET) {
        count += LONG_SIZE;
      } else if (bucket[i] == 0) {
        // ignore
      } else {
        long val = bucket[i];
        do {
          count++;
        } while ((val &= (val - 1)) > 0);
      }

    }

    return count;

  }

  /**
   *
   * offCount method: returns count of 1s
   *
   * 
   *
   *
   * @return int
   */
  public int offCount() {
    return this.size - onCount();
  }

  /**
   * returns an iterator
   */
  @Override
  public Iterator<Boolean> iterator() {
    return new Iterator<Boolean>() {

      private int position = 0;

      @Override
      public boolean hasNext() {
        return position < (actualCapacity - 1);
      }

      @Override
      public Boolean next() {
        return get(position++);
      }
    };
  }

  /**
   *
   * stream method: stream this set
   *
   * 
   *
   *
   * @return Stream stream
   */
  public Stream<Boolean> stream() {
    return StreamSupport
        .stream(Spliterators.spliteratorUnknownSize(this.iterator(), Spliterator.DISTINCT), false);
  }

  /**
   *
   * size method
   *
   * 
   *
   *
   * @return int
   */
  public int size() {
    return this.size;
  }

  /**
   *
   * isEmpty method
   *
   * 
   *
   *
   * @return boolean
   */
  public boolean isEmpty() {
    return this.size == 0;
  }

  /**
   *
   * add method: adds a new boolean , increases size if capacity reached extends capacity 1.5
   *
   * 
   *
   *
   * @param e element to add
   * @return boolean result
   */
  public boolean add(boolean e) {
    ensureCapacityInternal(this.size + 1);
    return onOff(this.size++, e);
  }

  /**
   * Adds all given booleans into this set
   * 
   * @author bsarac
   *
   * @param c collection to add
   */
  public void addAll(Collection<? extends Boolean> c) {
    c.forEach(this::add);
  }

  /**
   *
   * allOff method: sets all values 0
   *
   * 
   *
   * void
   */
  public void allOff() {
    off(0, this.size);
  }

  /**
   *
   * allOn method: sets all values 1
   *
   * 
   *
   * void
   */
  public void allOn() {
    on(0, this.size);
  }

  /**
   *
   * remove method: removes bit from given position
   *
   *
   *
   * @param position void
   */
  public void remove(int position) {

    if (position < 0 || this.size < 1 || position >= this.size) {
      throw new IndexOutOfBoundsException();
    }

    int begin = position >> LOG_64;
    this.bucket[begin] = shift(this.bucket[begin], position & MOD);
    // shift the rest
    for (int i = begin + 1; i < this.bucket.length; i++) {
      this.bucket[i - 1] |= (this.bucket[i] & 1) << MOD;
      this.bucket[i] >>>= 1;
    }
    // every 64 drop tail except first one
    if (--this.size > 1 && (this.size & MOD) == 0) {
      bucket = Arrays.copyOf(bucket, --actualCapacity);
      this.capacity -= LONG_SIZE;
    }
  }

  /**
   *
   * toBinaryString method: returns a binary string representing this set, this method is not
   * efficient for large sets and shouldnt be called
   *
   * 
   *
   *
   * @return String
   */
  public String toBinaryString() {
    if (this.isEmpty()) {
      return "";
    }
    StringBuilder sb = new StringBuilder(this.size);

    int indx = this.actualCapacity - 1;
    String val = null;
    int remaining = (this.size & MOD);
    if (remaining > 0) {
      val = Long.toBinaryString(this.bucket[indx--]);
      int diff = remaining - val.length();
      sb.append(String.join("", Collections.nCopies(diff, "0"))).append(val); // TODO this is quite
                                                                              // expensive
    }
    while (indx >= 0) {
      val = Long.toBinaryString(this.bucket[indx--]);
      int diff = 64 - val.length();
      sb.append(String.join("", Collections.nCopies(diff, "0"))).append(val); // TODO this is quite
                                                                              // expensive
    }

    return sb.toString();
  }

  /**
   *
   * calculateCapacity method: internal, pretty much copy of {@link ArrayList} resizing policy
   *
   * 
   *
   *
   * @param bucket
   * @param minCapacity
   * @return int
   */
  private static int calculateCapacity(long[] bucket, int minCapacity) {
    if (bucket == DEFAULTCAPACITY_EMPTY_ELEMENTDATA) {
      return Math.max(DEFAULT_CAPACITY, minCapacity);
    }
    return minCapacity;
  }

  /**
   *
   * ensureCapacityInternal method: internal, pretty much copy of {@link ArrayList} resizing policy
   *
   * 
   *
   *
   * @param minCapacity void
   */
  private void ensureCapacityInternal(int minCapacity) {
    ensureExplicitCapacity(calculateCapacity(bucket, minCapacity));
  }

  /**
   *
   * ensureExplicitCapacity method: internal, pretty much copy of {@link ArrayList} resizing policy
   *
   * 
   *
   *
   * @param minCapacity void
   */
  private void ensureExplicitCapacity(int minCapacity) {

    // overflow-conscious code
    if (minCapacity - this.capacity > 0)
      grow(minCapacity);
  }

  /**
   *
   * grow method: internal, pretty much copy of {@link ArrayList} resizing policy
   *
   * 
   *
   *
   * @param minCapacity void
   */
  private void grow(int minCapacity) {
    // overflow-conscious code
    int oldCapacity = this.capacity;
    int newCapacity = oldCapacity + (oldCapacity >> 1);
    if (newCapacity - minCapacity < 0)
      newCapacity = minCapacity;
    if (newCapacity - MAX_ARRAY_SIZE > 0)
      newCapacity = hugeCapacity(minCapacity);
    actualCapacity = getActaulCapacity(newCapacity);
    // minCapacity is usually close to size, so this is a win:
    bucket = Arrays.copyOf(bucket, actualCapacity);
  }

  /**
   *
   * hugeCapacity method: internal, pretty much copy of {@link ArrayList} resizing policy
   *
   * 
   *
   *
   * @param minCapacity
   * @return int
   */
  private static int hugeCapacity(int minCapacity) {
    if (minCapacity < 0) // overflow
      throw new OutOfMemoryError();
    return (minCapacity > MAX_ARRAY_SIZE) ? Integer.MAX_VALUE : MAX_ARRAY_SIZE;
  }

  /**
   *
   * getActaulCapacity method: returns the real capacity of {@link BitSet#bucket}
   *
   * 
   *
   *
   * @param capacity
   * @return int
   */
  private static final int getActaulCapacity(int capacity) {
    int diff = capacity & MOD;
    int actualCapacity = (capacity - diff) >>> LOG_64;
    actualCapacity += Math.min(1, diff); // TODO maybe
                                         // https://graphics.stanford.edu/~seander/bithacks.html#IntegerMinOrMax
    return actualCapacity;
  }

  /**
   *
   * shift method: removes bit from given position and sets last bit as 0
   *
   * 
   *
   *
   * @param l
   * @param index
   * @return long
   */
  private static final long shift(long x, int index) {
    // see https://stackoverflow.com/a/21259816/706695
    long mask = -1l << index;
    return ((x & ~mask) | ((x >>> 1) & mask));
  }

}
