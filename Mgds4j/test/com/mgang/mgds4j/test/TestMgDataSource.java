package com.mgang.mgds4j.test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

import org.junit.Test;

import com.mgang.mgds4j.core.MgDataSource;
import com.mgang.mgds4j.core.MgDataSourceFactory;

/**
 * 
 * @author meigang 2014-11-27 23:24
 * MgDataSource单元测试类
 *
 */
public class TestMgDataSource {
	private MgDataSource ds;
    public TestMgDataSource() {
		// TODO Auto-generated constructor stub
    	//ds = new MgDataSource();在MgDataSource被单例后，无法实例化
    	//创建工厂
		MgDataSourceFactory.build();
		//从工厂得到数据源
		ds = MgDataSourceFactory.getMgDataSource("ds");
    }
	/**
	 * 测试获得数据库连接
	 */
	@Test
	public void testGetConnection(){
		Connection conn = ds.getConnection();
		System.out.println("得到可用的数据库连接");
		showMsg();
		ds.close(conn);
		System.out.println("用完该连接，还回连接池");
		showMsg();
		ds.destory();
		showMsg();
	}
	@Test
	public void testMutil(){
		for(int i=0;i<10;i++){
			Connection conn = ds.getConnection();
			showMsg();
			if(i==6 || i==7){
				//归还两次，最后总大小是8
				ds.close(conn);
			}
		}
		ds.destory();
		showMsg();
	}
	@Test
	public void testAdd(){
		Connection conn = ds.getConnection();
		String sql = "insert into user(username,password,birthday)"
				+ " values(?,?,?)";
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, "小刚");
			ps.setString(2, "meigang"+(int)(Math.random()*1000));
			ps.setObject(3, new Date());
			ps.execute();
			ps.close();
			ds.close(conn);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		showMsg();
		ds.destory();
		showMsg();
		
	}
	private void showMsg(){
		System.out.println("当前连接池的总大小是:"+ds.getPoolTotalSize());
		System.out.println("当前连接池的可用连接数是:"+ds.getCurrentPoolSize());
	}
	@Test
	public void testFirst(){
		String str = "asdfgf";
		String a = (str.charAt(0)+"").toString().toUpperCase()+str.substring(1, str.length());
		System.out.println(a);
	}
	@Test
	public void testV2_1(){
		
		Connection conn = ds.getConnection();
		String sql = "insert into user(username,password,birthday) values(?,?,?)";
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, "v2.1");
			ps.setString(2, "mgds4j.xml"+(int)(Math.random()*1000));
			ps.setObject(3, new Date());
			ps.execute();
			ps.close();
			ds.close(conn);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ds.desotry();
	}
	@Test
	public void testClassPath(){
		String file_str = MgDataSourceFactory.class.getResource("/").toString();
		System.out.println(file_str.substring(6,file_str.length()));
	}
	
}
