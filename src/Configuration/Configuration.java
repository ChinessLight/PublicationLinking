package Configuration;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * 配置文件
 *
 */
public class Configuration {
	static String filePath;          //properties文件路径
	public static String root ;      //web项目根目录
	
	static{
//		String path = Configuration.class.getResource("/").getPath();
//		root = (path.replace("/build/classes", "").replace("%20"," ").replace("classes/", "")).replaceFirst("/", "");
//		if(System.getProperty ("path.separator").equals(":"))
//			root="/"+root;
		root="./";
		filePath=root+"config.properties";
	}
	
	public static String readValue(String key) {
		Properties props = new Properties();
		try {
			InputStream in = new BufferedInputStream(new FileInputStream(filePath));
			props.load(in);
			String value = props.getProperty(key);
			return value;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
