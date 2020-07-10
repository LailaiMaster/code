# 加密解密

jdk 加密相关类库的包路径为：

- `java.security`
- `javax.crypto`

## 1 ASCII

ASCII （American Standard Code for Information Interchange，美国信息交换标准代码）是基于拉丁字母的一套计算机编码系统。它主要用于显示现代英语，而其扩展版本 EASCII 则可以部分支持其他西欧语言，并等同于国际标准ISO/IEC 646。

具体参考[wiki/ASCII](https://zh.wikipedia.org/wiki/ASCII)。

## 2 凯撒密码

在密码学中，凯撒密码（英语：Caesar cipher），或称凯撒加密、凯撒变换、变换加密，是一种最简单且最广为人知的加密技术。它是一种替换加密的技术，明文中的所有字母都在字母表上向后（或向前）按照一个固定数目进行偏移后被替换成密文。例如，当偏移量是3的时候，所有的字母A将被替换成D，B变成E，以此类推。这个加密方法是以罗马共和时期恺撒的名字命名的，据称当年恺撒曾用此方法与其将军们进行联系。

具体参考[wiki/凯撒密码](https://zh.wikipedia.org/wiki/%E5%87%B1%E6%92%92%E5%AF%86%E7%A2%BC)。

破解方式：

- 频度分析法，英文中出现字母最频繁的是字母 e

## 3 Byte 和 bit

一个 Byte 有 8 个 bit。

## 4 常见对称加密算法：`DES、AES`

- DES，DES 是一种将 64 比特的明文密码加密成 64 比特密文的对称密码算法，需要的密码长度为 8 位
- AES，需要的密码长度位 16 位

```log
AES/CBC/NoPadding (128)
AES/CBC/PKCS5Padding (128)
AES/ECB/NoPadding (128)
AES/ECB/PKCS5Padding (128)
DES/CBC/NoPadding (56)
DES/CBC/PKCS5Padding (56)
DES/ECB/NoPadding (56)
DES/ECB/PKCS5Padding (56)
DESede/CBC/NoPadding (168)
DESede/CBC/PKCS5Padding (168)
DESede/ECB/NoPadding (168)
DESede/ECB/PKCS5Padding (168)
RSA/ECB/PKCS1Padding (1024, 2048)
RSA/ECB/OAEPWithSHA-1AndMGF1Padding (1024, 2048)
RSA/ECB/OAEPWithSHA-256AndMGF1Padding (1024, 2048)
```

上面摘取自官方文档，三个词的意思分别为`/算法/工作模式/填充模式`，最后括号里标明的是密钥长度，可以看到 DES 的密钥长度是 56 位，但是代码中我们传入的密钥长度是 8 个字符，那么 `8*8=64` 应该是 64 才对，这是怎么回事呢？因为DES 密钥每 8 位包含 1 位纠错码，即前 7 位参与加密计算，最后一位作为校验码，密码长度 64 位每 8 位减去一位就是 56 了。

## 5 工作模式和填充

常用工作模式（具体可以参考《图解密码技术》）：

- ECB：Electronic codebook
- CBC ：Cipher-block chaining

填充（Padding）：是对需要按块处理的数据，当数据长度不符合块处理需求时，按照一定方法填充满块长的一种规则。

## 6 非对称加密

- 常用算法：RSA、椭圆算法
- 秘钥对：公钥和私钥，必须由系统生成
- 公钥加密，私钥解密；私钥加密，公钥解密
- 公钥互换：两个组织或者人相互交换公钥
- 特点：加密速度慢、安全性高

### RSA

RSA加解密算法目前主流的有 RSA-1024， RSA-2048，RSA-4096等，数字代表的是密钥长度，密钥长度越长，安全性越高，但相应的性能会下降。

**明文长度与密钥长度**：

1. 对RSA算法来说，不存在分组模式的概念，对输入的明文长度有所限制，要求输入的明文长度一定小于密钥长度。理论上讲，多长的密钥就加密多长的明文，产生的密文长度与密钥长度一致。但是也跟具体的填充规则有关系。
2. 使用 RAS 加密时，如果提交的明文超过了限制，就会产生 `javax.crypto.IllegalBlockSizeException: Data must not be longer than 117 bytes` 异常。

具体参考：

- [getting-a-illegalblocksizeexception-data-must-not-be-longer-than-256-bytes-when](https://stackoverflow.com/questions/10007147/getting-a-illegalblocksizeexception-data-must-not-be-longer-than-256-bytes-when)。
- [RSA密钥长度、明文长度和密文长度](https://blog.csdn.net/lvxiangan/article/details/45487943)

## 7 消息摘要

- 常用算法：md5、 sha1、 sha256
- 特点：不可逆（加密后无法破解）
- 应用场景
  - 对用户密码进行 md5 加密后保存到数据库里
  - 软件下载站使用消息摘要计算文件指纹，防止被篡改
  - 数字签名

## 8 数字签名

数字签名是非对称加密与消息摘要的组合应用

应用场景：

1. 校验用户身份（使用私钥签名，公钥校验，只要用公钥能校验通过，则该信息一定是私钥持有者发布的）
2. 校验数据的完整性（用解密后的消息摘要跟原文的消息摘要进行对比）

## 参考

- [Java加密与解密](https://www.jianshu.com/p/213d69ac27b3)
- [加廖雪峰：Java 加密与安全](https://www.liaoxuefeng.com/wiki/1252599548343744/1255943717668160)
- [Security 相关笔记](../../../Security/README.md)
