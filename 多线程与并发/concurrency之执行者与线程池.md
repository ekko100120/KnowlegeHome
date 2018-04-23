## 执行者与线程池(Executor and ThreadPool)

#### 1 前言

在JDK1.4之前，Java多线程工具仅有`Runnable`、`Thread`、`Timer`以及`synchronize`关键字等寥寥无几的几个工具。
随着多核处理器的普遍应用，Java并发的作用越来越重要，因此自JDK1.5后，Java参考了操作系统以及其他编程语言的并发理念，发明了许多的概念、方法和算法，并提供了相关的工具。这些工具极大的提高了Java并发编程的可靠性、安全性和性能，成为Java程序员必须掌握的利器之一.

#### 2 知识体系结构

![知识体系结构](/resources/excutor知识体系.jpg)

#### 3 执行者Executor的由来

在介绍具体的工具之前，先讲讲设计者的思路。在Java1.4之前，已经提供了Runnable接口、Thread类、Timer类和synchronize关键字，它们已经足以完成各种各样的多线程编程任务，
为什么还要提供执行者这样的概念呢？**这是因为Java的设计者想把线程的创建、执行和调度分离**。

在Concurrency包出现之前，线程的创建基本上靠new一个Thread对象，执行靠start()方法，而线程的调度则完全依赖程序员在具体代码中自己写出来。

而Concurrency包出现之后，
1. 线程的创建还是依靠Thread、Runnable和`Callable`（新加入）对象的实例化；
2. 而线程的执行则靠`Executor`、`ExecutorService`的对象执行`execute()`方法或`submit()`方法;
3.线程的调度则被固化为几个具体的线程池类，如`ThreadPoolExecuto`、`ScheduledThreadPoolExecutor`、`ExecutorCompletionService`等等。这样表面上增加了复杂度，而实际上成功将线程的创建、执行和调度的业务逻辑分离，使程序员能够将精力集中在线程中业务逻辑的编写，大大提高了编码效率，降低了出错的概率，而且大大提高了性能。

#### 4 详细介绍

##### 4.1 线程执行者Executor:

这个功能主要由三个接口和类提供，分别是：
* `Executor`：执行者接口，所有执行者的父类；
* `ExecutorService`：执行者服务接口，具体的执行者类都继承自此接口；
* `Executors`：执行者工具类，大部分执行者的实例以及线程池都由它的工厂方法创建;

**eg:**
```Java
public class ExecutorExample {

  public static void main(String[] args) {
    ExecutorService service = Executors.newCachedThreadPool();
    //内部类初始化 InnerClass demo = new UpperClass().new InnerClass();
    // 实际上是调用父接口Executor的execute方法；
    service.execute(new ExecutorExample().new Task("task1"));
    service.execute(new ExecutorExample().new Task("task2"));
    service.execute(new ExecutorExample().new Task("task3"));
    //shutdown方法 在关闭 ExecutorService 之前等待提交的任务执行完成。
    // shutdownNow方法 会阻止开启新的任务并且尝试停止当前正在执行的线程，一旦调用该方法，线程池中将没有激活的任务，没有等待执行的任务，也没有新任务提交。没有任务执行的ExecutorService将会被回收。
    //方法submit扩展了Executor.execute(Runnable) 方法， 创建并返回一个 Future 结果，这个Future可以取消任务的执行或者等待完成得到返回值。
    //方法invokeAny and invokeAll 可以执行一组任务，等待至少一个任务或者多个任务完成（ExecutorCompletionService扩展了这些方法的实现）。
    service.shutdown();
  }

  class Task implements Runnable{
    private final String name;

    public Task(String name) {
      this.name = name;
    }

    @Override
    public void run() {
      try {
        for (int i = 0; i < 5; i++) {
          TimeUnit.SECONDS.sleep(1);
          System.out.println(name+"-["+i+"]");
        }
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
}
```

##### 4.2 得到异步执行的结果
>
> 1.在Java1.4之前，如果要得到一个线程运行后产生的值，没有一套现成的机制，程序员可以通过Thread类的成员变量、程序的全局变量等方式来得到一个线程运行后产生的某个值。但是这样的话，你必须不断探测线程是否已经成功结束，或者运用同步技术来等待线程执行完成，再去获取异步执行的结果。
>
> 2.在Java Concurrency中，得到异步结果有了一套固定的机制，即通过`Callable接口`、`Future`接口和`ExecutorService`的`submit`方法来得到异步执行的结果，相关工具的介绍如下：
>
> 3.**Callable:** 泛型接口，与Runnable接口类似，它的实例可以被另一个线程执行，内部有一个call方法，返回一个泛型变量V；
>
> 4.**Future:** 泛型接口，代表依次异步执行的结果值，调用其`get`方法可以得到一次异步执行的结果，`如果运算未完成，则阻塞直到完成`；调用其`cancel`方法可以取消一次异步执行；
>
> 5.**CompletionService:**一种执行者，可将`submit`的多个任务的结果按照完成的先后顺序存入一个`内部队列`，然后可以使用`take方法从队列中依次取出结果并移除，如果调用take时计算未完成则会阻塞`。


**eg: CompletionService demo**

```Java
public class CompletionServiceExample {

  // CompletionService的初始化方法
  // 1. 获得ExecutorService对象实例
  // 2. new 初始化  CompletionService<Integer> completionService = new ExecutorCompletionService<>(service);
  public static void main(String[] args) throws InterruptedException, ExecutionException {
    ExecutorService service = Executors.newCachedThreadPool();
    CompletionService<Integer> completionService = new ExecutorCompletionService<>(service);
    for (int i = 0; i < 5 ; i++) {
      completionService.submit(new CompletionServiceExample().new TaskInteger(i));
    }
    service.shutdown();
    for (int i = 0; i < 5; i++) {
      Future<Integer> future = completionService.take();
      System.out.println(future.get());
    }
  }

  //Callable的泛型类型与 return返回值类型相同
  class TaskInteger implements Callable<Integer>{

    private final  int sum;

    TaskInteger(int sum) {
      this.sum = sum;
    }

    @Override
    public Integer call() throws Exception {
      long start = System.currentTimeMillis();
      TimeUnit.SECONDS.sleep(sum);
      System.out.println("sleep time:"+(System.currentTimeMillis()-start));
      return sum*sum;
    }
  }
}
```


##### 4.3 重复执行和延期执行

> 在Java1.4之前，一般使用Timer来重复或者延期执行任务。Java Concurrency为了使之与Executor理念协调，引入了ScheduledExecutorService来完成同样的工作。
`ScheduledExecutorService：`另一种执行者，可以将提交的任务延期执行，也可以将提交的任务反复执行。
`ScheduledFuture：`与Future接口类似，代表一个被调度执行的异步任务的返回值。
下面的例子中ScheduledExecutorService的实例scheduler调度了两个任务，第一个任务使用scheduleAtFixedRate()方法每隔一秒重复打印“beep”，第二个任务使用schedule()方法在10秒后延迟执行，它的作用是取消第一个任务，代码如下

** ScheduledExecutorService**

```Java
public class ScheduledExecutorServiceExample {

  //1. ScheduledExecutorService的初始化过程有点怪异(ScheduledThreadPoolExecutor是接口的实现类), 用的 new ScheduledThreadPoolExecutor   ？？？
  //2. .scheduleAtFixedRate()方法每隔一秒重复打印“beep"
  //3. .schedule()方法在10秒后延迟执行，它的作用是取消第一个任务
  //涉及到  ScheduledExecutorService 和 ScheduledFuture 两个API
  public static void main(String[] args) {
    ScheduledExecutorService scheduler = new ScheduledThreadPoolExecutor(2);
    ScheduledFuture<?> scheduledFuture = scheduler.scheduleAtFixedRate(new Runnable() {
      @Override
      public void run() {
        System.out.println("beep");
      }
    },1,1, TimeUnit.SECONDS);

    scheduler.schedule(new Runnable() {
      @Override
      public void run() {
        System.out.println("cancel beep");
        scheduledFuture.cancel(true);
        scheduler.shutdown();
      }
    },10, TimeUnit.SECONDS);
  }
}

```


##### 4.4 TimeUnit

>上面的例子中用到了TimeUnit，因此这里就先介绍一下。TimeUnit是Java Concurrency包引入的新式的表达时间间隔或延迟的单位。在JDK1.5后面引入的新类中，都使用TimeUnit作为时间的表达方式.

##### 4.5 线程池

> 在前面的章节中我们已经多次使用了ExecutorService接口的对象，它的具体实现类主要有两个，分别是ThreadPoolExecutor和ScheduledExecutorService，它们都是某种线程池。
>
> 在JDK1.8中，按照线程池的创建方法来看，应该有五种线程池，它们的创建方法如下所示：

```Java
ExecutorService singleThreadPool = Executors.newSingleThreadExecutor();
ExecutorService fixedThreadPool = Executors.newFixedThreadPool(5);
ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
ScheduledExecutorService singleThreadScheduledPool = Executors.newSingleThreadScheduledExecutor();
ScheduledExecutorService scheduledPool = Executors.newScheduledThreadPool(5);
```
> 而进一步查看源代码，这些方法最终都调用了`ThreadPoolExecutor`和`ScheduledExecutorService`的构造函数。而S`cheduledExecutorService`继承自`ThreadPoolExecutor`，因此**最终所有线程池的构造函数都调用了`ThreadPoolExecutor`的如下构造函数**：

```Java
public ThreadPoolExecutor(int corePoolSize,
                          int maximumPoolSize,
                          long keepAliveTime,
                          TimeUnit unit,
                          BlockingQueue<Runnable> workQueue)
```

请大家注意这五个参数（重点）：
* 第一个参数corePoolSize，指明了线程池中应该保持的线程数量，即使线程处于空闲状态，除非设置了allowCoreThreadTimeOut这个参数；
* 第二个参数maximumPoolSize，线程池中允许的最大线程数，线程池中的线程一旦到达这个数，后续任务就会等待池中的线程空闲，而不会去创建新的线程了；
* 第三个参数keepAliveTime，当池中线程数量大于corePoolSize时，多出的线程在空闲时能够生存的最大时间，若一个线程空闲超过这个时间，它就会被终止并从池中删除；
* 第四个参数unit，指示第三个参数的时间单位；
* 第五个参数workQueue，存储待执行任务的阻塞队列，这些任务必须是Runnable的对象（如果是Callable对象，会在submit内部转换为Runnable对象）。

弄明白以上概念后，再来看这五种线程池：
* 单线程池：newSingleThreadExecutor()方法创建，五个参数分别是ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue())。含义是池中保持一个线程，最多也只有一个线程，也就是说这个线程池是顺序执行任务的，多余的任务就在队列中排队。

* 固定线程池：newFixedThreadPool(nThreads)方法创建，五个参数分别是ThreadPoolExecutor(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue())。含义是池中保持nThreads个线程，最多也只有nThreads个线程，多余的任务也在队列中排队。

* 缓存线程池：newCachedThreadPool()创建，五个参数分别是ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue())。含义是池中不保持固定数量的线程，随需创建，最多可以创建Integer.MAX_VALUE个线程（说一句，这个数量已经大大超过目前任何操作系统允许的线程数了），空闲的线程最多保持60秒，多余的任务在SynchronousQueue（所有阻塞、并发队列在后续文章中具体介绍）中等待。

单* 线程调度线程池：newSingleThreadScheduledExecutor()创建，五个参数分别是 (1, Integer.MAX_VALUE, 0, NANOSECONDS, new DelayedWorkQueue())。含义是池中保持1个线程，多余的任务在DelayedWorkQueue中等待。

* 固定调度线程池：newScheduledThreadPool(n)创建，五个参数分别是 (n, Integer.MAX_VALUE, 0, NANOSECONDS, new DelayedWorkQueue())。含义是池中保持n个线程，多余的任务在DelayedWorkQueue中等待。

#### 5. Fork-Join框架

> 在JDK1.7引入了一种新的并行编程模式**“fork-join”**，它是实现了**“分而治之”**思想的Java并发编程框架。网上关于此框架的各种介绍很多，本文从框架特点出发，通过几个例子来进行实用性的介绍。

#### 5.1 fork-join框架的特点

> fork-join框架对新手来说很难理解，因此我先从它的特点说起，它有几个特点：
>
> 1.它对问题的解决思路是分而治之，先将一个问题fork（分为）几个子问题，然后子问题又分为孙子问题，直至细分为一个容易计算的问题，然后再将结果依次join(结合)为最终的答案。是不是感觉和云计算中的Map-reduce计算模型很像？思路是一样的，只不过fork-join运行在一个JVM中的多个线程内，而map-reduce运行在分布式计算节点上。
>
> 2.在运行线程时，它使用**“work-steal”（任务偷取）算法**。一般来说，fork-join会启动多个线程（由参数指定，若不指定则默认为CPU核心数量），每个线程负责一个任务队列，并依次从队列头部获得任务并执行。当某个线程空闲时，它会从其他线程的任务队列尾部偷取一个任务来执行，这样就保证了线程的运行效率达到最高。

> 3.它面向的问题域是可以大量并行执行的计算任务，例如计算某个大型数组中每个元素的平方（当然这个有些无趣），其计算对象最好是一些独立的元素，不会被其他线程访问，也没有同步、互斥要求，更不要涉及IO或者无限循环。当然此框架也可以执行普通的并发编程任务，但是这时就失去了性能优势。
>
> 4.细分的计算任务有一个粗略的优化标准，即可以在100~10000条指令中执行完毕。
了解以上思路后，来看看fork-join框架提供的几个工具类：

* ForkJoinPool：支持fork-join框架的线程池，所有ForkJoinTask任务都必须在其中运行，线程池主要使用invoke()、invokeAll()等方法来执行任务，当然也可以使用原有的execute()和submit()方法；
* ForkJoinTask：支持fork-join框架的任务抽象类，它是Future接口，它代表一个支持fork()和join()方法的任务；
* RecursiveAction：ForkJoinTask的两个具体子类之一，代表没有返回值的ForkJoinTask任务；
* RecursiveTask：ForkJoinTask的两个具体子类之一，代表有返回值的ForkJoinTask任务。

#### 6. 小结

1. **执行者与线程池的引入是因为Concurrency包的设计者想将线程的创建、执行和调度分离，从而使得用户能够更加专注于业务逻辑**；
2. **Callable接口和Future接口使得异步执行结果的获取更加简单**；
3. **ScheduledExecutorService取代Timer成为了线程重复和延迟执行的新标准**；
4. **TimeUnit类的引入简化了时间段的表达工作**；
5. **包中提供的五种线程池可以极大的满足程序员的各种需求，极端情况下还可以利用ThreadPoolExecutor类自己定制线程池**;
6. **最后，从JDK1.7后引入的Fork-Join框架将“分而治之”的递归思想实现到线程池中，并应用“work-steal”算法实现了任务执行效率的提升**。
7. **所谓分而治之的思想目前来看主要是递归解决问题？？**
