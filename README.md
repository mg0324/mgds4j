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

##2014-11-28
升级日志：v1.1<br/>
1. 将等待时间抽出到配置文件中，默认是2000毫秒。<br/>


升级日志：v2.0<br/>
因为考虑到数据源和连接池的概念，数据源是使用连接池技术来缓存数据库连接供外使用的工具对象。<br/>
所以，将MgDataSource.java和MgConnectionPool.java合并成一个文件MgDataSource.java.<br/>
并测试通过。

升级日志：v2.1<br/>
1. 升级配置文件properties为xml<br/>
2. 加入工厂模式，单例模式，使用反射来实现。<br/>
单例模式：<br/>

	private static MgDataSource mgds = null;
	private MgDataSource(){
		//私有的构造函数
	}
	/**
	 * 得到MgDataSource的单例对象
	 * @return
	 */
	public static MgDataSource getInstance(){
		if(null == mgds){
			mgds = new MgDataSource();
		}
		return mgds;
	}

工厂模式：<br/>

	//创建工厂
	MgDataSourceFactory.build();
	//从工厂得到数据源
	ds = MgDataSourceFactory.getMgDataSource("ds");

反射机制核心：<br/>

	//通过调用static函数getInstance来得到单例对象
	Class dsClass = Class.forName(clazz);
	ds = (MgDataSource) dsClass.getMethod("getInstance", null).invoke(dsClass, null);
	factory.put(name, ds);

	//得到dataSource下的property节点
	Iterator ps = dsElement.elementIterator("property"); 
	while(ps.hasNext()){
		Element propertyElement = (Element) ps.next();
		String key = propertyElement.attributeValue("key");
		String value = propertyElement.attributeValue("value");
		String type = propertyElement.attributeValue("type");
		if(type.equals("string")){
			Method m = MgDataSource.class.getMethod("set"+UpperFirst(key),String.class);
			m.invoke(ds, value);
		}else if(type.equals("int")){
			Method m = MgDataSource.class.getMethod("set"+UpperFirst(key),int.class);
			m.invoke(ds, Integer.parseInt(value.trim()));
		}
	}

##2014-12-1
1.生成javadoc文档，微调javadoc注释。<br/>
2.export出的mgds4j-2.1.jar包放到outjar文件夹下，方便依赖使用。
 
