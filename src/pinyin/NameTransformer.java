package pinyin;

import java.util.ArrayList;
import java.util.List;

public class NameTransformer {

	
	/*
	 * 将中文名字转换为拼音的形式
	 * pattern 1: zhangsl
	 * pattern 2: zhangshengli
	 * 返回包含这两种形式的list
	 */
	public static List<String> name2Pinyin(String Name){
		List<String> TempList=new ArrayList<String>();
		List<String> pyList=new ArrayList<String>();
		List<String> pyFirstList=new ArrayList<String>();
		Pinyin py=new Pinyin();
		String WithFisrt="";
		String temp1=Name.substring(0,1);
		String temp2=Name.substring(1,Name.length());
		pyList=py.pinyin(temp1);
		pyFirstList=py.firstChar(temp2);
		for(int i=0;i<pyList.size();i++){
			WithFisrt=WithFisrt+pyList.get(i);
		}
		for(int i=0;i<pyFirstList.size();i++){
			WithFisrt=WithFisrt+pyFirstList.get(i);
		}
		TempList.add(WithFisrt);					//存储形式为zhangSL
		pyList=py.pinyin(Name);
		String FullName="";
		for(int i=0;i<pyList.size();i++){
			FullName=FullName+pyList.get(i);
		}
		TempList.add(FullName);						//存储形式为zhangshengli
		return TempList;
	}
}
