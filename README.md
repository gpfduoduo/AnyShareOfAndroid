# AnyShareOfAndroid
仿茄子快传 或者 360文件传输   
* 在局域网内进行文件（发送方的app、图片等文件，项目中只是进行了手机中的第三方app和手机图片的）的传输。    
* 如果没有接收方建立热点，发送方接入热点，然后进行文件的传输。
* 添加android与pc之间的文件传输，通过在android设备中实现http server来完成（这样不需要在pc中安装应用，只需要浏览器就可以方便完成操作）



## 实现原理：
* 设备发现：通过udp向255.255.255.255发送广播包  
* 文件传输：通过NIO socket。
* android下httpServer的实现通过apache的httpcore。

# 效果图
需要两部手机，连接在同一个wifi环境下，会相互发现，并且通过点击发现后的对方，建立连接，进而进行文件的传输。

![image](https://github.com/gpfduoduo/AnyShareOfAndroid/blob/master/filetransfer.gif "效果图")


## 引用库
* [雷达扫描图](https://github.com/gpfduoduo/RadarScanView) 作者：[本人](https://github.com/gpfduoduo)   
* compile 'com.android.support:appcompat-v7:22.2.0'  
* compile 'com.android.support:design:22.2.0'  
* compile 'com.android.support:support-v4:22.2.0'  
* compile 'com.github.bumptech.glide:glide:3.5.2'
* compile 'com.android.support:support-v4:22.2.0'
* compile 'com.android.support:recyclerview-v7:22.+'
* compile 'com.github.bumptech.glide:glide:3.5.2'
* compile project(':randomtextview')
* compile project(':rippleview')
* compile project(':rippleoutlayout')
* compile project(':p2pmanager')
* compile project(':httpServer') 作者[本人](https://github.com/gpfduoduo/HttpServerOnAndroid/)

## 感谢
杨蔚 及其 儿子 
