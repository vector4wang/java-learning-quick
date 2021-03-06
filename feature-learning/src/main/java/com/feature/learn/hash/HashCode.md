hashcode在java中的应用？

# HashCode

## 复习位运算

先来简单复习一下位运算的基础

| 含义                        | 运算符 | 例子                 |
| --------------------------- | ------ | -------------------- |
| 左移                        | <<     | 0011 => 0110         |
| 右移(带符号)                | >>     | 0110 => 0011         |
| 按位或                      | ︳     | 0011 \| 1011 => 1011 |
| 按位与                      | &      | 0011 & 1011 => 0011  |
| 按位取反                    | ~      | 0011 => 1100         |
| 按位异或 (相同为零不同为一) | ^      | 0011 ^ 1011 => 1000  |



在`String`里的代码如下:

```java
public int hashCode() {
  int h = hash;
  if (h == 0 && value.length > 0) {
    char val[] = value;
    for (int i = 0; i < value.length; i++) {
      h = 31 * h + val[i];
    }
    hash = h;
  }
  return h;
}
```

为了体现输入的内容改变很小也会对其hash值产生千差万别的效果，此处使用了一个**乘数**(31)对每一个位置的字符进行相乘累加的处理方法来实现，那么为什么此处会使用31来作为乘数呢？从以下几点进行研究

## 为什么是31

### 信息丢失

引用《Effective Java》一书中的内容

> 选择数字31是因为它是一个奇质数，如果选择一个偶数会在乘法运算中产生溢出，导致数值信息丢失，因为乘二相当于移位运算。选择质数的优势并不是特别的明显，但这是一个传统。同时，数字31有一个很好的特性，即乘法运算可以被移位和减法运算取代，来获取更好的性能：`31 * i == (i << 5) - i`，现代的 Java 虚拟机可以自动的完成这个优化。

这里牵扯到二进制的乘法运算，会产生溢出最终导致信息丢失，我们来看一个示例

![偶数](https://i.loli.net/2019/08/29/q1uH7RQISkZigeV.png)

![奇数](https://i.loli.net/2019/08/29/SHLnrvUzwi1bCma.png)



所以这个**乘数首先不能是偶数！**

### 性能

上面提到`31 * i == (i << 5) - i`,也就是说JVM在处理这一行的时候会把乘法优化成移位操作，

`<<` 代表二进制数的左移操作即**低位补0，高位丢弃**，假设这里`i=13`，其操作如下:

```
13		        0000 0000 0000 0000 0000 0000 0000 1101
13 << 5 		0000 0000 0000 0000 0000 0001 1010 0000
13 << 5 - 13	0000 0000 0000 0000 0000 0000 0000 1101
-----------------------------------------------------------
403				0000 0000 0000 0000 0000 0001 1001 0011
```

对于`13 * 31` 位运算过程如下(类似十进制的乘法):

```
13				0000 0000 0000 0000 0000 0000 0000 1101
31				0000 0000 0000 0000 0000 0000 0001 1111
-----------------------------------------------------------
				0000 0000 0000 0000 0000 0000 0000 1101
			   0000 0000 0000 0000 0000 0000 0000 1101
        	  0000 0000 0000 0000 0000 0000 0000 1101
       		 0000 0000 0000 0000 0000 0000 0000 1101 
            0000 0000 0000 0000 0000 0000 0000 1101 
-----------------------------------------------------------
403		  0000| 0000 0000 0000 0000 0000 0001 1001 0011    
```

通过简单的代码做耗时比较:

```java
    public static void testBitOpera() {
        int val = 28;
        long s = System.nanoTime();
        for (int i = 0; i < 100000000; i++) {
            int i1 = 29 * 31;
        }
        System.out.println(System.nanoTime() - s); // 1937665
        s = System.nanoTime();
        for (int i = 0; i < 100000000; i++) {
            int i1 = (29 << 5) - 29;
        }
        System.out.println(System.nanoTime() - s); // 951104
    }
```

也能看出来位运算比直接的运算快(JVM会对每一个代码自动优化，我们程序员可以不用去管)

[关于Java对于乘除法的优化](http://fanyilun.me/2015/04/21/关于Java对于乘除法的优化/)

**`31 * i`是可以被JVM优化为` (i << 5) - i`的**

### Hash范围大小

代码里hashcode的返回值为`int`,对应的quzhi范围是:`2^31~2^31-1,就`31 * 0 + val[0]`这个算法，可以简单推出当长度为n的时候的一个公式为:

`s[0]*31^(n-1) + s[1]*31^(n-2) + ... + s[n-1]`

我们可以拿几个数值来看一下，以下只计算公式中次数最高的那一项`31^(n-1)`，当乘数是2的时候，假设n=6,结果` 2^5`为32，比较小所以这里可以断定，当字符串长度不是很长时，用质数2做为乘子算出的哈希值，数值不会很大。也就是说，哈希值会分布在一个较小的数值区间内，分布性不佳，最终可能会导致冲突率上升。

如果使用101呢，`101^5`是`10,510,100,501`远远超出`2^31-1`的范围，上面说到溢出就会导致信息丢失，所以乘数太大也不是一个好的选择；

再看看31，`31^5`是`28629151`相比较`32`和`10,510,100,501`来说，非常不错，但是又有一个问题，31可以的话，那37，41，43为什么不行呢？接着往下看！

**Hash取值的范围要是尽可能的广**



### Hash冲突占比

hash里面最重要的另一个非冲突莫属了，通过上面的算法`h = 31 * h + val[i];`可以得出，对应字符串的每一位改动都会对hashcode造成千差万别的变化，但是始终会出现不同字符串产生同一hashcode的情况，比如：

```java
System.out.println(Objects.hashCode("La's")); // 2358657
System.out.println(Objects.hashCode("MB's")); // 2358657
```

接着上一节，把101以内的素数当做乘数来统计一下冲突率,结果如下

![冲突率](https://i.loli.net/2019/08/31/Kl2nSphwEfajZ9g.png)

可以看到31、37、29、41下的冲突个数和冲突率都相差不大，这也验证了单从冲突率来看这几个数也都是可以作为乘数的。



### Hash分布情况 

直接拿图了如下， 

![](https://i.loli.net/2019/08/31/NVPZS3FoMmYdu7s.png)



![](https://i.loli.net/2019/08/31/reAPbYfCFLs1Ma8.png)

![](https://i.loli.net/2019/08/31/ZyBRhkTGOebCrsW.png)

![](https://i.loli.net/2019/08/31/YuwcDACdZ7PNnE9.png)

![](https://i.loli.net/2019/08/31/HCYBcFIz9onk2rR.png)

hash分布情况31也算是比较好



综上所述，31胜出！！！

## HashMap中的Hash

HashMap中对hashcode又做了一次加工，如下:

```java
static final int hash(Object key) {
  int h;
  return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
}
```

主要就是这里`(h = key.hashCode()) ^ (h >>> 16)`将key的hash值无符号右移16位再与自己做异或位操作，举个例子：

```
"vector".hashCode()					1100 1111 0001 1001 1110 0001 0100 0011
"vector".hashCode() >>> 16  		0000 0000 0000 0000 1100 1111 0001 1001
^									1100 1111 0001 1001 0010 1110 1010 1010
```

我们先看一看拿到这个异或结果是用来做什么的，在后面`putVal`方法中有这一句代码

`p = tab[i = (n - 1) & hash]`，而`tab`是一个声明的数组`Node<K,V>[] tab`,也就是说`(n - 1) & hash`是为了确定下标，其实就是`hash % n`,这里直接写做位运算提高计算效率，举几个例子：

假设数组的长度为16，(16-1=15)的二进制为`0000 0000 0000 0000 0000 0000 1111` 分别于下面几个hash做`&`操作

```
1111 & 0000 0000 0001 0101 0100 1010 0100 1000 结果：1000 = 8
1111 & 0000 0000 0001 0100 0101 1010 0100 1001 结果：1001 = 9
1111 & 0000 0000 0001 0101 0101 1011 0100 1010 结果： 1010 = 10
1111 & 0000 0000 0001 0110 0100 1110 0110 1100 结果： 1100 = 12
```

如果数组长度为15，(15-1=14)的二进制为`0000 0000 0000 0000 0000 0000 1110`再分别做`&`操作，如下

```
1110 & 0000 0000 0001 0101 0100 1010 0100 1000 结果：1000 = 8
1110 & 0000 0000 0001 0100 0101 1010 0100 1001 结果：1000 = 8
1110 & 0000 0000 0001 0101 0101 1011 0100 1010 结果： 1010 = 10
1110 & 0000 0000 0001 0110 0100 1110 0110 1100 结果： 1100 = 12
```

如果数组长度为12 , (12-1=11)的二进制为`0000 0000 0000 0000 0000 0000 1011再分别做`&`操作，如下

```
1011 & 0000 0000 0001 0101 0100 1010 0100 1000 结果：1000 = 8
1011 & 0000 0000 0001 0100 0101 1010 0100 1001 结果：1001 = 9
1011 & 0000 0000 0001 0101 0101 1011 0100 1010 结果： 1010 = 10
1011 & 0000 0000 0001 0110 0100 1110 0110 1100 结果： 1000 = 8
```





这里也就引出了为什么HashMap要求容量为2的幂次方，

> ```
> The default initial capacity - MUST be a power of two.
> ```

因为`2^n-1` 对应的二进制就是`*1111`。

但是这里又会出现另一个情况，如果一批key对应的hashcode如下

```
0000 0000 0001 0101 0100 1010 0100 1000
0000 0000 0001 0100 0100 1010 0100 1000
0000 0000 0001 1101 0100 1010 0100 1000
0000 0000 0001 0111 0100 1010 0100 1000
```

这四个与数组长度做`&`后的记过均是8，也就产生了“碰撞”，也就是说这四个key都会放在下标为8的位置上，接着会形成链表或红黑树结构，那这显然不是最好的结果，因为这种情况非常常见，典型的高位改变低位不便的情况，所以为此，源码的作者在原本hashcode的基础上又加上了一层“扰动函数”，即

`(h = key.hashCode()) ^ (h >>> 16)` 高明之处在于将hashcode的**前半高位**跟**后半低位**做`^`操作，

**混合原始哈希码的高位和低位，以此来加大低位的随机性**;

**尽量做到任何一位的变化都能对最终得到的结果产生影响**！

此时我们再走下上面的结果：

```
0000 0000 0001 0101 0100 1010 0100 1000
0000 0000 0001 0100 0100 1010 0100 1000
0000 0000 0001 1101 0100 1010 0100 1000
0000 0000 0001 0111 0100 1010 0100 1000
```

将它们右移16位之后与自己做`^`操作，结果如下：

```
0000 0000 0001 0101 0100 1010 0101 1101
0000 0000 0001 0100 0100 1010 0101 1100
0000 0000 0001 0101 0100 1010 0101 0101
0000 0000 0001 0111 0100 1010 0101 1111
```

然后再与

再与数组长度做`&`操作，结果如下:

```
0000 0000 0000 0000 0000 0000 0000 1101  13
0000 0000 0000 0000 0000 0000 0000 1100  12 
0000 0000 0000 0000 0000 0000 0000 0101  5
0000 0000 0000 0000 0000 0000 0000 1111  16
```

太好了，这四个key会被放在不同的位置，没有出现冲突！

总结：数组容量大小为2的幂次方，保证数组下标计算时，减少自己的“干扰”；Hashmap中hashcode在原来的基础上加了一层**扰动函数**，保留了高低位的信息，后面再计算数组下标的时候让每一位的数据都参与了计算，保证最后的下标是公平公正！

尽量让Node落点分布均匀，减少碰撞的概率，碰撞概率高，导致对应的链表长度太长，影响效率



## 脑筋急转弯

如何判断一个数是不是2的幂次方？

<details>
<summary>答案</summary>
    <pre><code>(n-1) & n == 0</code></pre>
</details>

## Hash一致性

 一致性哈希算法在1997年由麻省理工学院提出的一种分布式哈希（DHT）实现算法，设计目标是为了解决因特网中的热点(Hot spot)问题

判断hash算法的好坏的四个定义：

1、**平衡性(Balance)**：平衡性是指哈希的结果能够尽可能分布到所有的缓冲中去，这样可以使得所有的缓冲空间都得到利用。很多哈希算法都能够满足这一条件。
2、**单调性(Monotonicity)**：单调性是指如果已经有一些内容通过哈希分派到了相应的缓冲中，又有新的缓冲加入到系统中。哈希的结果应能够保证原有已分配的内容可以被映射到原有的或者新的缓冲中去，而不会被映射到旧的缓冲集合中的其他缓冲区。 
3、**分散性(Spread)**：在分布式环境中，终端有可能看不到所有的缓冲，而是只能看到其中的一部分。当终端希望通过哈希过程将内容映射到缓冲上时，由于不同终端所见的缓冲范围有可能不同，从而导致哈希的结果不一致，最终的结果是相同的内容被不同的终端映射到不同的缓冲区中。这种情况显然是应该避免的，因为它导致相同内容被存储到不同缓冲中去，降低了系统存储的效率。分散性的定义就是上述情况发生的严重程度。好的哈希算法应能够尽量避免不一致的情况发生，也就是尽量降低分散性。 
4、**负载(Load)**：负载问题实际上是从另一个角度看待分散性问题。既然不同的终端可能将相同的内容映射到不同的缓冲区中，那么对于一个特定的缓冲区而言，也可能被不同的用户映射为不同 的内容。与分散性一样，这种情况也是应当避免的，因此好的哈希算法应能够尽量降低缓冲的负荷。



### 案例

假设，我们有一个社交网站，需要使用Redis存储图片资源，存储的格式为键值对，key值为图片名称，value为该图片所在文件服务器的路径，我们需要根据文件名查找该文件所在文件服务器上的路径，数据量大概有2000W左右，按照我们约定的规则进行分库，规则就是随机分配，我们可以部署8台缓存服务器，每台服务器大概含有500W条数据，并且进行主从复制。

- 随机或普通: 预先将2000W缓存在四台机器中。普通存储，使用的时候，需要从缓存查四次！

![mark](http://cdn.wangxc.club/image/20190904/zBwBpY1tUf8a.jpg?imageslim)

- 使用hash: 将key值hash一下取模类似hashmap数组，得到下标进行存取

![mark](http://cdn.wangxc.club/image/20190904/0K9n8e6gNtyt.jpg?imageslim)

显然第二种是最优的，而且应用广泛，

TBD表的按键分区`dbpartition by hash(`resume_id`) tbpartition by hash(`resume_id`) tbpartitions 4;`

https://help.aliyun.com/document_detail/71276.html?spm=a2c4g.11186623.6.644.53112df5zJEJCg

https://help.aliyun.com/document_detail/71276.html?spm=a2c4g.11186623.6.644.460a2df5LcQeHp



### 问题

如果后面某一时间四台其中的一台出现故障宕机了，或者四台满足不了需求，需要额外增加一个进行扩容，那这个时候4就变成了3或5，再按照上面说的`hash(a.png) % n`结果可就不一样了，导致所有缓存的位置都要发生改变，在一定时间内所有的缓存是失效的出现**缓存雪崩**。

### Hash一致性

假设某哈希函数H的值空间为0-2^32-1（即哈希值是一个32位无符号整形）

![mark](http://cdn.wangxc.club/image/20190904/P0JKWSHtTmQ1.jpg?imageslim)

整个空间按顺时针方向组织，圆环的正上方的点代表0，0点右侧的第一个点代表1，以此类推，2、3、4、5、6……直到2^32-1，也就是说0点左侧的第一个点代表2^32-1， 0和2^32-1在零点中方向重合，我们把这个由2^32个点组成的圆环称为Hash环。

接着上面的问题，把目标机器的某一个特征(一般都是ip)进行hash，确定其在hash环的位置，如下:

![mark](http://cdn.wangxc.club/image/20190904/QLJaIcDUkbx5.jpg?imageslim)



接下来使用如下算法定位数据访问到相应服务器：**将数据key使用相同的函数Hash计算出哈希值，并确定此数据在环上的位置，从此位置沿环顺时针“行走”，第一台遇到的服务器就是其应该定位到的服务器！**

例如我们有Object A、Object B、Object C、Object D四个数据对象，经过哈希计算后，在环空间上的位置如下

![mark](http://cdn.wangxc.club/image/20190904/saynCuv1VtTC.jpg?imageslim)



### 问题的平滑解决

现假设Node C不幸宕机，可以看到此时对象A、B、D不会受到影响，只有C对象被重定位到Node D。一般的，在一致性Hash算法中，如果一台服务器不可用，则受影响的数据仅仅是此服务器到其环空间中前一台服务器（即沿着逆时针方向行走遇到的第一台服务器）之间数据，其它不会受到影响，如下所示：

![mark](http://cdn.wangxc.club/image/20190904/kzSI17HD7jFV.jpg?imageslim)

如果新增一台机器，结果如下图:

![mark](http://cdn.wangxc.club/image/20190904/g2wasUM789KO.jpg?imageslim)

此时对象Object A、B、D不受影响，只有对象C需要重定位到新的Node X ！一般的，在一致性Hash算法中，如果增加一台服务器，则受影响的数据仅仅是新服务器到其环空间中前一台服务器（即沿着逆时针方向行走遇到的第一台服务器）之间数据，其它数据也不会受到影响。



### 新的问题

如果节点过少，容易因为节点分部不均匀而造成数据倾斜，如图：

![v2-d499324a9aa067915bbb3f5f3416b032_r](C:\Users\bd2\Desktop\v2-d499324a9aa067915bbb3f5f3416b032_r.jpg)



此时必然造成大量数据集中到Node A上，而只有极少量会定位到Node B上。为了解决这种数据倾斜问题，一致性Hash算法引入了虚拟节点机制，即对每一个服务节点计算多个哈希，每个计算结果位置都放置一个此服务节点，称为虚拟节点。具体做法可以在服务器IP或主机名的后面增加编号来实现。

### 虚拟节点

例如上面的情况，可以为每台服务器计算三个虚拟节点，于是可以分别计算 “Node A#1”、“Node A#2”、“Node A#3”、“Node B#1”、“Node B#2”、“Node B#3”的哈希值，于是形成六个虚拟节点：

![mark](http://cdn.wangxc.club/image/20190904/Dd7xb9xblAJB.jpg?imageslim)

同时数据定位算法不变，只是多了一步虚拟节点到实际节点的映射，例如定位到“Node A#1”、“Node A#2”、“Node A#3”三个虚拟节点的数据均定位到Node A上。这样就解决了服务节点少时数据倾斜的问题。在实际应用中，通常将虚拟节点数设置为32甚至更大，因此即使很少的服务节点也能做到相对均匀的数据分布。



参考：

https://segmentfault.com/a/1190000010799123

https://stackoverflow.com/questions/32042346/why-does-this-multiplication-integer-overflow-result-in-zero

https://blog.csdn.net/qq_38182963/article/details/78940047

https://www.zhihu.com/question/23172611

https://blog.csdn.net/cywosp/article/details/23397179

https://zhuanlan.zhihu.com/p/34985026