package Main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import Bean.Data;
import CS.AffiliationTranslater;
import Configuration.Configuration;
import db.DataFetcher;

public class Processor implements Callable<String> {

	private Data zhData;
	private List<Data> enDatas;
	private static List<String> resultList;
	static {
		resultList = Collections.synchronizedList(new ArrayList<String>());
	}

	public Processor(Data zhData, List<Data> enDatas) {
		this.zhData = zhData;
		this.enDatas = enDatas;

	}

	private static void step1() {
		DataFetcher fetcher = new DataFetcher();

		List<Data> enDataList = fetcher.getDataList("T2"); // 得到表2所有数据
		AffiliationTranslater affiliationTranslater = new AffiliationTranslater(
				enDataList); // 翻译表2中的英文公司名
		affiliationTranslater.translate();
	}

	private static void step2() {
		DataFetcher fetcher = new DataFetcher();
		List<Data> zhDataList = fetcher.getDataList("T1"); // 得到表1所有数据
		List<Data> enDataList = fetcher.getDataList("T2"); // 得到表2所有数据
		ExecutorService pool = Executors.newFixedThreadPool(Integer
				.valueOf(Configuration.readValue("thread")));

		try {
			for (Data zhData : zhDataList) {

				Processor aligner = new Processor(zhData, enDataList);
				FutureTask<String> futureTask = new FutureTask<String>(aligner);
				pool.execute(futureTask);

			}
			pool.shutdown();

			// 当所有子进程都结束后 关闭线程池
			while (true) {
				if (pool.isTerminated()) {
					BufferedWriter bw = null;
					try {
						bw = new BufferedWriter(new OutputStreamWriter(
								new FileOutputStream(
										Configuration.readValue("resultFile")),
								"UTF-8"));
						for (String result : resultList) {
							bw.write(result);
							bw.newLine();
						}

					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						bw.close();
					}

					pool.shutdown();
					break;
				}
				Thread.sleep(1000);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		// 步骤1：对表2中的英文名进行翻译
		 //step1();
		// 开始关联
		step2();
	}

	/*
	 * 输出形式，默认为
	 * 表1中文id \t 表2英文id
	 */
	private String format(Data zhData, Data enData) {
		return zhData.getId() + "\t" + enData.getId();
	}

	public String call() {
		Map<String, Double> pairSimilarityMap = new HashMap<String, Double>();
		for (Data enData : enDatas) {
			Aligner aligner = new Aligner(zhData, enData);
			String idpair = format(zhData, enData);
			double similarity = aligner.judge();
			if (similarity != 0.0) {
				pairSimilarityMap.put(idpair, similarity);
			}
		}
		if (pairSimilarityMap.size() == 0) {
			return "";
		}

		List<Map.Entry<String, Double>> list_DataEntries = new ArrayList<Map.Entry<String, Double>>(
				pairSimilarityMap.entrySet());
		System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
		Collections.sort(list_DataEntries,
				new Comparator<Map.Entry<String, Double>>() {
					public int compare(Map.Entry<String, Double> o1,
							Map.Entry<String, Double> o2) {
						if (o2.getValue() != null && o1.getValue() != null
								&& o2.getValue().compareTo(o1.getValue()) > 0) {
							return 1;
						} else if (o2.getValue() != null
								&& o1.getValue() != null
								&& o2.getValue().compareTo(o1.getValue()) < 0) {
							return -1;
						} else {
							return 0;
						}
					}
				});
		String result = list_DataEntries.get(0).getKey();
		System.out.println("发现匹配项:" + result);
		resultList.add(result);
		return "";

	}

}
