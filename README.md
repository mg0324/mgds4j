mgds4j
======

梦来梦往 自己实现的数据源与连接池工具，全称 mgang datasource for java

##2014-11-27
今天我本来是解决struts2参数传递有时候会出现null的问题的，但是没想在此过程中碰到了45s问题和<br/>
数据库连接connection refused的问题。然后，和这两个问题一较劲，我就来解决这边了。<br/>

还好的是，都有了一定的突破。<br/>
45s问题已在网上找到解决方法，但是我还是会在我的网易博客中来写到的。<br/>
第二个问题就是数据库的连接问题了，查了一些资料，发现可能是数据源的问题，然后我就来到了数据源<br/>
与连接池的对面。<br/>

也成功的使用了常用的数据源dbcp和c3p0这两个。<br/>
然后，我就想到了自己来写这样一个数据源，利用连接池技术。当然可能做不到像dbcp和c3p0这样的成<br/>熟技术好用。但是，我还是会自己去实现一番，就算自己以后还是用专业的数据源，这样做了也是一种进步。<br/>

如此，mgds4j这个项目也就诞生了。
***
今天算是我完成的1.0版本。<br/>
1. 实现连接池缓存数据库连接<br/>
2. 数据源得到连接，归还连接<br/>
3. 当无可用连接，按一定的长度自动增长连接池<br/>
 