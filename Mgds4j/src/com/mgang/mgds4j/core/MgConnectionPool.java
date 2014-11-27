package com.mgang.mgds4j.core;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Vector;

/**
 * 
 * @author meigang 2014-11-27 22:09
 * 连接池对象
 */
public class MgConnectionPool {
	//数据库驱动
	private static String driverName;
	//数据库url
	private static String url;
	//数据库用户名
	private static String userName;
	//数据库密码
	private static String password;
	//自动增长的大小
	private static int autoIncrement;
	//连接池大小 默认5个connection
	private static int poolSize = 5;
	//存放已经连接到数据库的connection的向量
	private static Vector<Connection> pool = null;
	//当前连接池拥有的连接数的大小
	private static int currentPoolLength = 5;
	//增长次数
	private int autoIncrementTime = 0;
	//静态加载配置文件
	static{
		Properties p = new Properties();
		try {
			p.load(MgConnectionPool.class.getClassLoader().getResourceAsStream("mgds4j.properties"));
			driverName = p.getProperty("mgds4j.driverName");
			url = p.getProperty("mgds4j.url");
			userName = p.getProperty("mgds4j.userName");
			password = p.getProperty("mgds4j.password");
			autoIncrement = Integer.parseInt(p.getProperty("mgds4j.autoIncrement").trim());
			poolSize = Integer.parseInt(p.getProperty("mgds4j.poolSize").trim());
			currentPoolLength = poolSize;
			//初始化
			initConnectionPool();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			try {
				throw new Exception(e.getMessage()+"--加载mgds4j.properties配置文件出错，请检查是否在classpath下没有mgds4j.properties");
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		}		
	}
	/**
	 * 初始化连接池
	 */
	private static void initConnectionPool(){
		pool = new Vector<Connection>();
		//加载驱动
		try {
			Class.forName(driverName);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			try {
				throw new Exception(e.getMessage()+"--加载数据库驱动出错");
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		for(int i=0;i<poolSize;i++){
			try {
				Connection conn = DriverManager.getConnection(url,userName,password);
				pool.addElement(conn);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				try {
					throw new Exception(e.getMessage()+"--获取数据库连接出错");
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
	}
	/**
	 * 关闭数据库连接
	 * @param conn
	 */
	public void closeConnection(Connection conn){
		if(null != conn){
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	/**
	 * 从连接池中得到可用的连接
	 * @return
	 */
	public synchronized Connection getUsableConnection(){
		Connection conn = null;
		if(currentPoolLength > 0){
			conn = pool.get(currentPoolLength-1);
			currentPoolLength--;
		}else{
			//说明当前连接池中没有连接了,就需要按照autoIncremnt的大小来穿件数据库连接放到pool中
			//先等个2秒,休眠2秒，等其他的操作将连接还回来可能
			try {
				wait(2000);
				if(currentPoolLength > 0){
					conn = pool.get(currentPoolLength-1);
					currentPoolLength--;
				}else{
					autoIncrement();
					//递归调用
					conn = getUsableConnection();
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("得到可用连接，当前可用连接数为"+currentPoolLength);
		return conn;
	}
	/**
	 * 用完了连接，就还回到pool中
	 */
	public synchronized void backConnection(Connection conn){
		pool.addElement(conn);
		currentPoolLength++;
		System.out.println("归还连接到连接池,可用连接数为"+currentPoolLength);
	}
	/**
	 * 自动增长
	 */
	private void autoIncrement(){
		for(int i=0;i<autoIncrement;i++){
			try {
				Connection c = DriverManager.getConnection(url,userName,password);
				pool.addElement(c);
				currentPoolLength++;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		autoIncrementTime++;
		System.out.println("自动增长连接池一次，连接池大小为"+(int)(poolSize+autoIncrementTime*autoIncrement));
	}
	/**
	 * 得到当前连接池拥有的连接个数
	 * @return
	 */
	public int getCurrentPoolSize(){
		return currentPoolLength;
	}
	/**
	 * 得到连接池的总大小
	 * @return
	 */
	public int getPoolTotalSize(){
		return poolSize + autoIncrementTime*autoIncrement;
	}
	/**
	 * 关闭连接池
	 */
	public void desotry() {
		// TODO Auto-generated method stub
		for(int i=0;i<currentPoolLength;i++){
			closeConnection(pool.get(i));
		}
		currentPoolLength = 0;
		System.out.println("连接池关闭");
	}
}
