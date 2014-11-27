package com.mgang.mgds4j.core;

import java.sql.Connection;

/**
 * 
 * @author meigang 2014-11-27 22:10
 * 数据源对象
 */
public class MgDataSource {
	//连接池对象
	private MgConnectionPool cp;
	public MgDataSource(){
		cp = new MgConnectionPool();
	}
	/**
	 * 从连接池中获取数据库连接
	 * @return
	 */
	public Connection getConnection(){
		return cp.getUsableConnection();
	}
	/**
	 * 将连接还回连接池
	 * @param conn
	 */
	public void close(Connection conn){
		cp.backConnection(conn);
	}
	/**
	 * 关闭连接池
	 */
	public void destory() {
		cp.desotry();
	}
	/**
	 * 得到连接池中有的连接个数
	 * @return
	 */
	public int getInPoolSize(){
		return cp.getCurrentPoolSize();
	}
	/**
	 * 得到当前连接池的总大小
	 * @return
	 */
	public int getPoolTotalSize(){
		return cp.getPoolTotalSize();
	}
	
}
