package dpit;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.daemon.Daemon;
import org.apache.commons.daemon.DaemonContext;
import org.apache.commons.daemon.DaemonInitException;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;
import org.springframework.context.support.GenericXmlApplicationContext;

import dpit.dao.MofDAO;

public class conn2 implements Daemon, Runnable {
	private String status = "";
	private Thread thread = null;
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

	
	public void init(DaemonContext context) throws DaemonInitException,	Exception {
		System.out.println("init...");

		String[] args = context.getArguments();
		if (args != null) {
			for (String arg : args) {
				System.out.println(arg);
			}
		}
		status = "INITED";
		this.thread = new Thread(this);
		System.out.println("init OK.");
		System.out.println();

	}

	public void start() {
		is = getClass().getResourceAsStream("/db.properties");
	    props = new Properties();
	    try {props.load(is);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    props.get("hims.url");
	    
		System.out.println("status: " + status);
		System.out.println("start...");
		status = "STARTED";
		this.thread.start();
		System.out.println("start OK.");
		System.out.println();

		context = new GenericXmlApplicationContext("classpath*:spring/applicationContext-*.xml");

		mofDao = context.getBean(dpit.dao.MofDAO.class);

		logger = Logger.getLogger(this.getClass());

		t_url = (String) props.get("hims.url");
		System.out.println(t_url);
		//t_url = "http://192.168.0.232:28080/avl.json?functionId=connHims&info=";
	}

	public void stop() throws Exception {
		System.out.println("status: " + status);
		System.out.println("stop...");
		status = "STOPED";
		this.thread.join(10);
		System.out.println("stop OK.");
		System.out.println();
	}

	public void destroy() {
		System.out.println("status: " + status);
		System.out.println("destroy...");
		status = "DESTROIED";
		System.out.println("destroy OK.");
		System.out.println();
	}

	public void run() {
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
					System.out.println(key + " : " + hash.get(key));
					
					method.addParameter(key, String.valueOf(hash.get(key)));
				}
				
				try {
					//if(hash.get("RM_LATITUDE").toString() != null && hash.get("RM_LONGITUDE").toString() != null) {
						int statusCode = client.executeMethod(method);
						
						System.out.println("statusCode : " + statusCode);
					//}
				} catch (HttpException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	

}