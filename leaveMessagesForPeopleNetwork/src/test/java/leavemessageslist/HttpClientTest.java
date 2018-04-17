package leavemessageslist;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import com.keyten.base.LeaveMessage;


public class HttpClientTest {
	
	private LinkedBlockingQueue<LeaveMessage> queue = new LinkedBlockingQueue<>();
	
	public static ExecutorService executor = null;//线程池对象
	static{
		//获取核心数
		int hexinshu = Runtime.getRuntime().availableProcessors();
		executor = Executors.newFixedThreadPool(hexinshu*2); 
	}
	
	@Test
	public void testPost() throws UnsupportedEncodingException{
		HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
		CloseableHttpClient httpClient = httpClientBuilder.build();
		
		RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(5000)
                .setConnectTimeout(5000)
                .setConnectionRequestTimeout(5000)
                .build();
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		
		params.add(new BasicNameValuePair("fid", "539"));
		params.add(new BasicNameValuePair("lastItem", "0"));
		
	    HttpUriRequest httpUriRequest = RequestBuilder.post().setUri("http://liuyan.people.com.cn/threads/queryThreadsList")
	    	.addParameters(params.toArray(new BasicNameValuePair[params.size()]))
	    	.setConfig(requestConfig).build();
	    
	    httpUriRequest.addHeader("Accept", "application/json, text/javascript, */*; q=0.01");
	    httpUriRequest.addHeader("Accept-Encoding", "gzip, deflate");
	    httpUriRequest.addHeader("Accept-Language", "zh-CN,zh;q=0.8");
	    httpUriRequest.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
	    httpUriRequest.addHeader("Host", "liuyan.people.com.cn");
	    httpUriRequest.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36");
	    httpUriRequest.addHeader("Origin", "http://liuyan.people.com.cn");
	    httpUriRequest.addHeader("Referer", "http://liuyan.people.com.cn/threads/list?fid=539");
	    
	    
//	    Origin:http://liuyan.people.com.cn
//	    Referer:http://liuyan.people.com.cn/threads/list?fid=539
//	    User-Agent:Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36
//	    X-Requested-With:XMLHttpRequest
	    
	    try {
			CloseableHttpResponse httpResponse = httpClient.execute(httpUriRequest);
			HttpEntity entity = httpResponse.getEntity();
			BufferedHttpEntity bufferedHttpEntity = new BufferedHttpEntity(entity);
			String responseString = EntityUtils.toString(bufferedHttpEntity, "UTF-8");
			
			JSONObject jsonObject = new JSONObject(responseString);
			Object object = jsonObject.get("responseData");
			
			JSONArray array = new JSONArray(object.toString());
			
			array.forEach(p ->{
				 	JSONObject a = (JSONObject)p;
		            Object tid = a.get("tid");
		            System.out.println(tid.toString());
		            Object subject = a.get("subject");
		            LeaveMessage leaveMessage = new LeaveMessage(subject.toString(), tid.toString());
		            queue.add(leaveMessage);
			});
			
			executor.execute(new GetLeaveContentThread(queue));
			executor.execute(new GetLeaveContentThread(queue));
			
			Thread.sleep(100000);  
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}
	

}
