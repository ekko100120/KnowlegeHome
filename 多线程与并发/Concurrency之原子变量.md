## 原子变量 Amotic

### 1. 前言

> 本文介绍的是其中的原子变量，为什么调整介绍的顺序，是因为在写前两篇的时候意识到非阻塞并发的基础是`CAS（CompareAndSwap，比较并替换）`，而CAS的基础是`Unsafe类`，因此最好先找一个地方系统性的介绍一下Unsafe和CAS，这个地方就是原子变量类。

##### 2. 关于 Unsafe类

>Java放弃了指针，获得了更高的安全性和内存自动清理的能力。但是，它还是在一个角落里提供了类似于指针的功能，那就是`sun.misc.Unsafe`类，利用这个类，
>
>1.可以完成许多需要指针才能提供的功能，例如构造一个对象，但是不调用构造函数；

>2.找到对象中一个变量的地址，然后直接给它赋值，无视其final属性

>3.通过地址直接操作数组；或者是进行CAS操作
##### 3. 关于 CAS

```Java
public class UsafeExample {
  public static void main(String[] args) throws InstantiationException, NoSuchFieldException {

      Unsafe unsafe = null;

    try {
      Field f = Unsafe.class.getDeclaredField("theUnsafe");
      f.setAccessible(true);
      // f.get(null) 没搞懂？？？？
      unsafe = (Unsafe) f.get(null);
    } catch (NoSuchFieldException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }

    if (unsafe!=null){
      try {
        //构造一个对象，且不调用其构造函数
        Test test = (Test) unsafe.allocateInstance(Test.class);
        //得到一个对象内部属性的地址
        long x_addr = unsafe.objectFieldOffset(Test.class.getDeclaredField("x"));
        //直接给此属性赋值
        unsafe.getAndSetInt(test,x_addr,100);
        System.out.println(test.get());
      } catch (InstantiationException e) {
        e.printStackTrace();
      } catch (NoSuchFieldException e) {
        e.printStackTrace();
      }
    }

    //通过地址操作数组
    if (unsafe!=null){
      final  int INT_BYTES =4;
      int[] data = new int[10];
      System.out.println(Arrays.toString(data));
      long arrarBaseOffset = unsafe.arrayBaseOffset(int[].class);
      System.out.println("Array address is :" +arrarBaseOffset);
      unsafe.putInt(data,arrarBaseOffset,101);
      unsafe.putInt(data,arrarBaseOffset+INT_BYTES*8,50);
      unsafe.putInt(data,arrarBaseOffset+INT_BYTES*3,50);
      System.out.println(Arrays.toString(data));
    }
    if (unsafe!=null){
      Test test = (Test) unsafe.allocateInstance(Test.class);
      long x_addr = unsafe.objectFieldOffset(Test.class.getDeclaredField("x"));
      unsafe.getAndSetInt(test,x_addr,33);
      System.out.println("test.x is: "+test.get());
      unsafe.compareAndSwapInt(test,x_addr,33,90);
      System.out.println("After CAS：" + test.get());
    }
  }

```
CAS 即CompareAndSet操作，在Unsafe中的形式如下:

```Java
public final native boolean compareAndSwapObject(Object var1, long var2, Object var4, Object var5);

public final native boolean compareAndSwapInt(Object var1, long var2, int var4, int var5);

public final native boolean compareAndSwapLong(Object var1, long var2, long var4, long var6);
```

这三个方法都有四个参数，其中第一和第二个参数代表对象的实例以及地址，第三个参数代表期望值，第四个参数代表更新值。**CAS的语义是，若期望值等于对象地址存储的值，则用更新值来替换对象地址存储的值，并返回true，否则不进行替换，返回false**。
后面我们会看到诸多的原子变量，例如AtomicInteger、AtomicLong、AtomicReference等等都提供了CAS操作，其底层都是调用了Unsafe的CAS，它们的参数往往是三个，对象值、期望值和更新值，其语义也与Unsafe中的一致。

**CAS是所有原子变量的原子性的基础，为什么一个看起来如此不自然的操作却如此重要呢？其原因就在于这个native操作会最终演化为一条`CPU指令cmpxchg`，而不是多条CPU指令。由于CAS仅仅是一条指令，因此它不会被多线程的调度所打断，所以能够保证CAS操作是一个原子操作。补充一点，当代的很多CPU种类都支持cmpxchg操作，但不是所有CPU都支持，对于不支持的CPU，会自动加锁来保证其操作不会被打断。**

**由此可知，原子变量提供的原子性来自CAS操作，CAS来自Unsafe，然后由CPU的cmpxchg指令来保证。**

##### 3.1 i++不是线程安全的

> **所谓“线程安全的”，意思是在多线程的环境下，多次运行，其结果是不变的，或者说其结果是可预知的。若某些对变量的操作不能保持原子性，则其操作就不是线程安全的**。
为了说明原子性，来给出一个没有实现原子性的例子，例如**i++这一条语句，它实际上会被编译为两条CPU指令**，因此若一些线程在运行时被从中打断，就会造成不确定的后果，如下：


##### 3.2 保持原子性的AtomicInteger(原始数据类型的Atom)

> 若要保持一个变量改变数值时的原子性，目前Java最简单的方法就是使用相应的原子变量，例如**AtomicInteger**、**AtomicBoolean**和**AtomicLong** (**没有AtomicFloat**)。再来看一个例子：

```Java
public class AtomIntegerExample {

  public static void main(String[] args) throws InterruptedException {
    AtomicInteger atomicInteger = new AtomicInteger(0);
    ExecutorService service = Executors.newCachedThreadPool();
    for (int i = 0; i <10 ; i++) {
        service.execute(new Runnable() {
          @Override
          public void run() {
            for (int j = 0; j <10000 ; j++) {
               atomicInteger.incrementAndGet();
            }
          }
        });
    }
    service.shutdown();
    while (!service.awaitTermination(1, TimeUnit.SECONDS)){
      System.out.println(atomicInteger.get());
    }
    System.out.println("++++++++++++++++++++++++++++++++++");
    System.out.println(atomicInteger.get());
  }
}
```

> 这次的结果为保持为100000了。因为AtomicInteger的incrementAndGet()操作是原子性的。观察其内部代码，它使用了Unsafe的compareAndSwapInt()方法。
那么现在整形有AtomicInteger，长整型有AtomicLong，布尔型有AtomicBoolean，那么浮点型怎么办？JDK的说法是程序员可以利用AtomicInteger以及Float.floatToRawIntBits和Float.intBitsToFloat来自己实现一个AtomicFloat；利用AtomicLong以及Double.doubleToRawLongBits和Double.longBitsToDouble来自己实现一个AtomicDouble。在网上可以搜索到相应的实现[实现JDK没有提供的AtomicFloat - 杨尚川的个人页面](https://link.zhihu.com/?target=https%3A//my.oschina.net/apdplat/blog/418019)，这里就不再赘述了。
