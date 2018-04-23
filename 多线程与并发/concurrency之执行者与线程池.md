## 执行者与线程池(Executor and ThreadPool)

#### 1 前言

在JDK1.4之前，Java多线程工具仅有`Runnable`、`Thread`、`Timer`以及`synchronize`关键字等寥寥无几的几个工具。
随着多核处理器的普遍应用，Java并发的作用越来越重要，因此自JDK1.5后，Java参考了操作系统以及其他编程语言的并发理念，发明了许多的概念、方法和算法，并提供了相关的工具。这些工具极大的提高了Java并发编程的可靠性、安全性和性能，成为Java程序员必须掌握的利器之一.

##### 2 知识体系结构

![知识体系结构](E:\知识整理\resources\excutor知识体系.jpg)
