package dpit;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;
import org.springframework.context.support.GenericXmlApplicationContext;

import dpit.dao.MofDAO;

public class conn implements Runnable {

	GenericXmlApplicationContext context = null;
	MofDAO mofDao = null;
	Logger logger = null;
	List list = null;
	HttpClient client = null;
	PostMethod method = null;
	String t_url = "";
	String key = "";
	InputStream is = null;
	Properties props = null;
	String ACCIDENT_AT="";
	String ACCIDENT_MESSAGE="";
	
	public void run() {
		is = getClass().getResourceAsStream("/db.properties");
	    props = new Properties();
	    try {
	    	props.load(is);
		} catch (IOException e) {
			e.printStackTrace();
		}
	    props.get("hims.url");

		System.out.println("start OK.");
		System.out.println();

		context = new GenericXmlApplicationContext("classpath*:spring/applicationContext-*.xml");
		mofDao = context.getBean(dpit.dao.MofDAO.class);
		logger = Logger.getLogger(this.getClass());
		t_url = (String) props.get("hims.url");
		System.out.println(t_url);
		
		ACCIDENT_AT = (String) props.get("conn.ACCIDENT_AT");
		ACCIDENT_MESSAGE	= (String)props.get("conn.ACCIDENT_MESSAGE");
		
		
		while (true) {
			try {
				Thread.sleep(Long.parseLong((String)props.get("conn.sleep")));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			list = mofDao.selectMofConnect();

			for (int i = 0; i < list.size(); i++) {
				HashMap hash = (HashMap) list.get(i);
				System.out.println(i + " ");

				client = new HttpClient();
				method = new PostMethod(t_url);
				method.setRequestHeader("Content-Type","application/x-www-form-urlencoded; charset=UTF-8");
				Set set = hash.keySet();
				
				java.util.Iterator keys = set.iterator();
				
				while(keys.hasNext()) {
					key = (String)keys.next();
					//System.out.println(key + " : " + hash.get(key));
					// 테스트용도로 사용됨, 사고 여부, 사고내용를 조회 하여 넣음
					if(ACCIDENT_AT.equals("Y") && (key.equals("ACCIDENT_AT") || key.equals("ACCIDENT_MESSAGE"))) {
						if(key.equals("ACCIDENT_AT")) method.addParameter(key, ACCIDENT_AT);
						if(key.equals("ACCIDENT_MESSAGE")) method.addParameter(key, ACCIDENT_MESSAGE);
					} else {
						method.addParameter(key, String.valueOf(hash.get(key)));
					} 
				}
				
				try {
						int statusCode = client.executeMethod(method);
						System.out.println("statusCode : " + statusCode);
				} catch (HttpException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}