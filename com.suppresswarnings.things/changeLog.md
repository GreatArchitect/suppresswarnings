## 20190425
* 1.0.7
* 增加dependency 

```xml
<dependency>
    <groupId>com.google.zxing</groupId>
    <artifactId>core</artifactId>
    <version>3.3.0</version>
</dependency>
```
* 增加QRCodeUtil
用法：在Things中增加showQRCode方法，根据情况使用相应的Util静态方法

* 1.0.8
* 为了适配树莓派（不支持javax.io）将QRCodeUtil拆分为3个，位于com.suppresswarnings.things.util
