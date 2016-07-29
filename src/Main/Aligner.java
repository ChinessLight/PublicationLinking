package Main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import pinyin.NameTransformer;
import pinyin.Pinyin;
import Bean.Data;
import CS.AffiliationTranslater;
import CS.LCSComparator;
import CS.Translater;
import Configuration.Configuration;
import db.DataFetcher;

public class Aligner  {
	public static double threhold = Double.parseDouble(Configuration
			.readValue("threadshold"));
	public static String translatePath = Configuration.root
			+ Configuration.readValue("cacheFile");
	public static String[] KeyWords = { "大学", "学院", "医院", "研究所", "中心" };
	private Data zhData;
	private Data enData;
	public static Map<String, String> CompanyMap;

	static {
		CompanyMap = new ConcurrentHashMap<>();
		try {
			File cache = new File(translatePath);
			if (cache.exists()) {
				BufferedReader in = new BufferedReader(new InputStreamReader(
						new FileInputStream(translatePath), "UTF-8"));
				String line = null;
				while ((line = in.readLine()) != null) {
					String[] temp = line.split(":");
					CompanyMap.put(temp[0], temp[1]);
					line = in.readLine();
				}
				in.close();
			} else
				CompanyMap = new HashMap<String, String>();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public Aligner(Data zhData, Data enData) {
		this.zhData = zhData;
		this.enData = enData;

	}

	public static void main(String[] args) throws Exception {
		// DataFetcher fetcher = new DataFetcher();
		// List<Data> enDataList = fetcher.getDataList("T2"); // 得到表2所有数据
		// AffiliationTranslater affiliationTranslater = new
		// AffiliationTranslater(
		// enDataList); // 翻译表2中的英文公司名
		// affiliationTranslater.translate();

		Data zhData = new Data();
		zhData.setCompany("华东理工大学");
		zhData.setId("z1");
		zhData.setName("张三");
		Data enData = new Data();
		enData.setCompany("East China University of Science and Technology");
		enData.setId("e1");
		enData.setName("Zhang-S");

		Aligner aligner = new Aligner(zhData, enData);
		System.out.println(aligner.judge());
	}

	/*
	 * 针对表1中的一行记录 即 zhData对象 判断与给定表2的记录 即enData对象 是否可以匹配 如果找到 返回 相似度 否则 返回0
	 */
	public double judge() {
		boolean bName = false;
		boolean bCompany = false;
		String zhName = this.zhData.getName();
		List<String> tempList = new ArrayList<String>();
		if (zhName.matches("[a-zA-Z]+")) { // 如果表1中的名字已经是拼音格式，则不转换
			tempList.add(zhName);
			tempList.add(zhName);
		} else {
			tempList = NameTransformer.name2Pinyin(zhName);
		}
		for (int i = 0; i < tempList.size(); i++) {
			if (tempList.get(i).equals(
					enData.getName().replace(",", "").replace(" ", "")
							.replace(".", "").replace("-", "").toLowerCase()))
				bName = true;
		}
		double similarity=companySimilar();
		if (similarity >= threhold) {
			bCompany = true;
		}
		if (bName && bCompany)
			return similarity;
		else
			return 0.0;
	}

	/*
	 * 计算单位的相似度 返回计算出来的相似值
	 */
	public double companySimilar() {
		double university = 0;
		double school = 0;
		double cent = 0;
		Translater trans = new Translater();
		String zhCompany = zhData.getCompany();

		if (CompanyMap.get(enData.getCompany()) == null) {
			return 0.0;
		}
		String enCompany = CompanyMap.get(enData.getCompany());
		try {
			if (zhCompany.contains("大学") && enCompany.contains("大学")
					&& zhCompany.contains("学院") && enCompany.contains("学院")) {// 既有大学又有学院的比较
				String zhUni = zhCompany.split("大学")[0];
				String enUni = enCompany.split("大学")[0];
				String zhSch = zhCompany.split("大学")[1].replace("学院", "");
				String enSch = enCompany.split("大学")[1].replace("学院", "");
				university = LCSComparator.compute(zhUni, enUni);
				school = LCSComparator.compute(zhSch, enSch);
				cent = university * 0.5 + school * 0.5;
			} else {
				for (String keyw : KeyWords) {
					if (zhCompany.contains(keyw) && enCompany.contains(keyw)) {
						String zhUni = zhCompany.split(keyw)[0];
						String enUni = enCompany.split(keyw)[0];
						cent = LCSComparator.compute(zhUni, enUni);
						break;
					}
				}
			}
		} catch (Exception e) {
			return 0;
		}
		return cent;
	}


}
