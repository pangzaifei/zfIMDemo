# zfIMDemo
几点说明：<br>
1：android聊天客户端的demo，包含了主流的功能。 <br>
	1.1比如gif动态表情展示、语音、聊天表情、拍照、多图的发送、大图片的处理、listview缓存的处理等。<br>
    1.2数据库也已经搭载好，算是个完整项目，可以直接拿来用。<br>
    1.3服务器使用的是baidu push服务。(图片暂时没有处理上传服务器，只是上传了本地sdcard的path路径)<br>
	1.4此项目还有值得看的地方就是listview的复用处理。此处主要有两种处理方法。<br>
	1.5此项目为了测试方便，现在的所有信息都是自己给自己发。你也可以参考百度push文档，修改对应id，给其他手机发送。<br>
2：可以借鉴的地方(listview的处理)此项目主要有两种:<br>
 1.主界面的listview使用getItemViewType()和getViewTypeCount（），根据不同type显示不同的item，这样可以使一个listview显示多种风格的item布局<br> 
	(例如聊天界面的左右聊天布局例如文字item，图片item,语音item..可以看MessageAdapter.java文件)<br>
 2：创建一个Itemview的方法(不同风格定义不同的java文件)：<br>
	将view的处理和逻辑分散到另外一个文件中，也实现了复用的功能。 比如在itemView包中，我们创建了ImageGridSingleTypeView.<br>
	这个只需要在getLayoutResourceId()中设置R.layout.xx布局文件。然后再initView（）初始化布局就好。 然后在notifyDataChanged（）来设置每个view的数据。<br>
	这样的好处是可以分别处理，易于管理。不会让adapter的代码过于复杂。<br>
	（例如相册界面可以看ImageGridSingleTypeView.java,PickPhotoSingleTypeView.java，ImageGridAdapter.java,ImageBucketAdapter.java）<br>
有问题可以联系：<br>
 @blog:http://blog.csdn.net/pangzaifei<br>
 @github:https://github.com/pangzaifei/zfIMDemo<br>
 @qq:1660380990<br>
 @email:pzfpang451@163.com<br>

![image](https://raw.githubusercontent.com/pangzaifei/zfIMDemo/master/liaotiandemo/doc/1.jpg)
![image](https://raw.githubusercontent.com/pangzaifei/zfIMDemo/master/liaotiandemo/doc/2.jpg)
![image](https://raw.githubusercontent.com/pangzaifei/zfIMDemo/master/liaotiandemo/doc/3.jpg)
![image](https://raw.githubusercontent.com/pangzaifei/zfIMDemo/master/liaotiandemo/doc/4.jpg)
![image](https://raw.githubusercontent.com/pangzaifei/zfIMDemo/master/liaotiandemo/doc/5.jpg)
![image](https://raw.githubusercontent.com/pangzaifei/zfIMDemo/master/liaotiandemo/doc/6.jpg)
![image](https://raw.githubusercontent.com/pangzaifei/zfIMDemo/master/liaotiandemo/doc/7.jpg)
![image](https://raw.githubusercontent.com/pangzaifei/zfIMDemo/master/liaotiandemo/doc/8.jpg)

最近同事发现个问题，手机注册不上百度push，所以消息推送用不了。。这个我暂时也不知道原因哦。最近比较忙，暂时没时间来处理了。<br>我推测可能是
百度push的推送是有上限人数的，所以你可以去百度开发者里面注册个新的Key,然后替换成自己的，应该就可以了。<br>
发个链接吧:http://developer.baidu.com/wiki/index.php?title=docs/cplat/push/guide#.E6.8A.8A.E7.A4.BA.E4.BE.8B.E5.BA.94.E7.94.A8.E5.AF.BC.E5.85.A5_Eclipse_.E5.B7.A5.E7.A8.8B<br>
具体怎么配置看里面的文档就好了。10分钟就可以搞定。
