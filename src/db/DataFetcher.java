package db;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import Bean.Data;
import Configuration.Configuration;

public class DataFetcher {
	private String t1Id;
	private String t1Name;
	private String t1Company;
	
	private String t2Id;
	private String t2Name;
	private String t2Company;
	
	/*
	 * 变量初始化
	 */
	public DataFetcher(){
		t1Id=Configuration.readValue("T1.id");
		t1Name=Configuration.readValue("T1.name");
		t1Company=Configuration.readValue("T1.company");
		
		t2Id=Configuration.readValue("T2.id");
		t2Name=Configuration.readValue("T2.name");
		t2Company=Configuration.readValue("T2.company");
	}
	
	
	/*
	 * 拿到表1或2中的id，name和company
	 */
	public List<Data> getDataList(String tableId){
		DBConnection dbc = new DBConnection();
		Connection conn = (Connection) dbc.getConnection();
		Statement stmt =null;
		List<Data> DataList=new ArrayList<Data>();
		try {
			if(tableId.equals("T1")){
				String tableName=Configuration.readValue("T1");
				String id=Configuration.readValue("T1.id");
				String name=Configuration.readValue("T1.name");
				String company=Configuration.readValue("T1.company");
				
				String sql = "select "+id+","+name+","+company+" from "+tableName;
				stmt = (Statement) conn.createStatement();
				ResultSet rs=stmt.executeQuery(sql);
				rs=stmt.executeQuery(sql);
				while(rs.next()){
					Data data=new Data();
					data.setId(rs.getString(1));
					data.setName(rs.getString(2));
					data.setCompany(rs.getString(3));
					DataList.add(data);
				}
			}
			else
				if(tableId.equals("T2")){
					String tableName=Configuration.readValue("T2");
					String id=Configuration.readValue("T2.id");
					String nameCompany=Configuration.readValue("T2.nameCompany");
					
					String sql = "select "+id+","+nameCompany+" from "+tableName;
					stmt = (Statement) conn.createStatement();
					ResultSet rs=stmt.executeQuery(sql);
					rs=stmt.executeQuery(sql);
					while(rs.next()){
						String unit=rs.getString(2);
						
						//表2中的名字和单位 是合在一起的，先拆分
						if(unit!=null){
							unit=unit.replace("[", "!@#");
							String[] temp=unit.split("; !@#");
							for(int i=0;i<temp.length;i++){
								if(!(temp[i].equals("")||temp[i].equals(" "))){
									String[] temp1=temp[i].split("]");
									if(temp1.length>1){		
										if(!temp1[1].equals("")){
											temp[0]=temp[0].replace("[","");
											String [] Names=temp[0].split(";");
											
											for(int namei=0;namei<Names.length;namei++){
												Data data=new Data();
												data.setCompany(temp1[1].replace("Peoples R", "").trim());
												data.setId(rs.getString(1));
												data.setCompany(temp1[1].replace("Peoples R", "").trim());
												data.setName(Names[namei].replace("!@#", ""));
												DataList.add(data);
											}
										}
									}
								}
							}
						
						}
					}
						
					}
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {  
			try{  
				stmt.close();  
			}catch(Exception e1){  
			} 
			try{  
				conn.close();    
			}catch(Exception e2){  
			} 
		}
		return DataList;
	}

}
