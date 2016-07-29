package CS;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import Bean.Data;
import Configuration.Configuration;
import Main.Aligner;


public class AffiliationTranslater {

	public String translatePath=Configuration.root+Configuration.readValue("cacheFile");
	private static Translater trans;
	private List<Data> enDataList;

	static{
		trans=new Translater();
	}
	
	public AffiliationTranslater(List<Data> enDataList){
		this.enDataList = enDataList;		
		
	}
	
	/*
	 * 对affiliationList中的英文公司名 进行翻译
	 * 输出到文件
	 */
	public void translate(){
		PrintWriter out = null;
		System.out.println("===翻译开始=== ");
		try {
			out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(translatePath, true), "UTF-8")));
			String transString="";
			int i=0;
			for(Data data:enDataList){
				String company=data.getCompany();
				if(Aligner.CompanyMap.containsKey(company))
					continue;
				//若超时，则自动重试，若超时超过5次则跳过当前
				try{
					transString=trans.translateToZh(company);
				}
				catch(Exception e){
					int retry=5;					
					while(retry>0){
						Thread.sleep(30000);
						transString=trans.translateToZh(company);
						retry--;
						if(!transString.equals(""))
							break;
					}
				}
				out.println(company+":"+transString);
				Aligner.CompanyMap.put(company, transString);
				i++;
				if(i%100==0){			
					System.out.println("翻译完成: "+(double)i*100/(double)enDataList.size()+"%");
					Thread.sleep(5000);
				}
			}
			
		} catch (Exception e) {
			out.flush();
		} finally{
			out.close();
		}


		

		
	}
}
