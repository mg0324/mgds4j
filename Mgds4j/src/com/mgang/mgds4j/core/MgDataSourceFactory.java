package com.mgang.mgds4j.core;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * 
 * @author meigang 2014-11-28 18:44
 * 数据源工厂(工厂模式，反射)
 */
public abstract class MgDataSourceFactory {
	private static String name;
	private static String clazz;
	private static String destory;
	private static MgDataSource ds;
	
	private static Map<String,MgDataSource> factory = new HashMap<String, MgDataSource>();
	/**
	 * 构建工厂
	 */
	public static void build(){
		try {
			initMgds4jFormXml();
			//初始化连接池
			ds.initConnectionPool();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 解析mgds4j.xml配置文件，默认的路径在classpath根目录下
	 * @throws DocumentException 
	 */
	private static void initMgds4jFormXml() throws DocumentException{
		SAXReader reader = new SAXReader();
		
		Document doc = reader.read(MgDataSourceFactory.class.getClassLoader().getResourceAsStream("mgds4j.xml"));
		Element rootElement = doc.getRootElement(); // 获取根节点
		Iterator dsElements = rootElement.elementIterator("dataSource"); // 获取根节点下的子节点dataSource
	 	//只有一个dataSource
		Element dsElement = (Element) dsElements.next();
		name = dsElement.attributeValue("name");
		clazz = dsElement.attributeValue("class");
		destory = dsElement.attributeValue("destory");
		try {
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
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static MgDataSource getMgDataSource(String name){
		return factory.get(name);
	}
	/**
	 * 首字母大写
	 * @param str
	 * @return
	 */
	private static String UpperFirst(String str){
		return (str.charAt(0)+"").toString().toUpperCase()+str.substring(1, str.length());
	}
	/**
	 * 得到项目的classpath
	 * @return
	 */
	private static String getClasspath(){
		String file_str = MgDataSourceFactory.class.getResource("/").toString();
		return file_str.substring(6,file_str.length());
	}
}
