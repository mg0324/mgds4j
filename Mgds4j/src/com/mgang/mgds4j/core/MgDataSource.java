package com.mgang.mgds4j.core;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Vector;

/**
 * 
 * @author meigang 2014-11-27 22:09
 * 数据源对象使用连接池技术实现
 * （单例模式）
 */
public class MgDataSource {
	//数据库驱动
	private String driverName;
	//数据库url
	private String url;
	//数据库用户名
	private String userName;
	//数据库密码
	private String password;
	//自动增长的大小,默认是1
	private int autoIncrement = 1;
	//连接池大小 默认5个connection
	private int poolSize = 5;
	//当前连接池拥有的连接数的大小
	private int currentPoolLength = 5;
	//增长次数
	private int autoIncrementTime = 0;
	//等待时间，默认2000毫秒，就是2秒
	private int waitTimeOut = 2000;
	//存放已经连接到数据库的connection的向量
	private Vector<Connection> pool = null;
	
	private static MgDataSource mgds = null;
	
	private MgDataSource(){
		
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
	/**
	 * 初始化连接池
	 */
	public void initConnectionPool(){
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
	 * 关闭连接池
	 * @param conn
	 */
	public void destory(){
		for(int i=0;i<currentPoolLength;i++){
			Connection conn = pool.get(i);
			if(null != conn){
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		currentPoolLength = 0;
		System.out.println("关闭连接池");
		
	}
	/**
	 * 从连接池中得到可用的连接
	 * @return
	 */
	public synchronized Connection getConnection(){
		Connection conn = null;
		if(currentPoolLength > 0){
			conn = pool.get(currentPoolLength-1);
			currentPoolLength--;
		}else{
			//说明当前连接池中没有连接了,就需要按照autoIncremnt的大小来穿件数据库连接放到pool中
			//先等个2秒,休眠2秒，等其他的操作将连接还回来可能
			try {
				wait(waitTimeOut);
				if(currentPoolLength > 0){
					conn = pool.get(currentPoolLength-1);
					currentPoolLength--;
				}else{
					autoIncrement();
					//递归调用
					conn = getConnection();
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
	public synchronized void close(Connection conn){
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
			try {
				pool.get(i).close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		currentPoolLength = 0;
		System.out.println("连接池关闭");
	}

	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setAutoIncrement(int autoIncrement) {
		this.autoIncrement = autoIncrement;
	}

	public void setPoolSize(int poolSize) {
		this.poolSize = poolSize;
	}

	public void setWaitTimeOut(int waitTimeOut) {
		this.waitTimeOut = waitTimeOut;
	}
	
}
