package leavemessageslist;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.keyten.base.LeaveMessage;

public class GetLeaveContentThread implements Runnable{
	
	private LinkedBlockingQueue<LeaveMessage> queue = null;
	
	

	public GetLeaveContentThread(LinkedBlockingQueue<LeaveMessage> queue) {
		super();
		this.queue = queue;
	}



	@Override
	public void run() {
		
		while(true){
			LeaveMessage poll = queue.poll();
			if(poll==null){
				break;
			}else{
				//发送get请求
				HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
				CloseableHttpClient httpClient = httpClientBuilder.build();
				//http://liuyan.people.com.cn/threads/content?tid=5062720
				String uri ="http://liuyan.people.com.cn/threads/content?tid=" + poll.getTid();
				RequestConfig requestConfig = RequestConfig.custom()
		                .setSocketTimeout(5000)
		                .setConnectTimeout(5000)
		                .setConnectionRequestTimeout(5000)
		                .build();
				HttpUriRequest httpUriRequest = RequestBuilder.get(uri).setConfig(requestConfig).build();
				
				httpUriRequest.addHeader("Accept", "application/json, text/javascript, */*; q=0.01");
			    httpUriRequest.addHeader("Accept-Encoding", "gzip, deflate");
			    httpUriRequest.addHeader("Accept-Language", "zh-CN,zh;q=0.8");
			    httpUriRequest.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
			    httpUriRequest.addHeader("Host", "liuyan.people.com.cn");
			    httpUriRequest.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36");
			    httpUriRequest.addHeader("Origin", "http://liuyan.people.com.cn");
			    httpUriRequest.addHeader("Referer", "http://liuyan.people.com.cn/threads/list?fid=539");
			    
			    try {
					CloseableHttpResponse httpResponse = httpClient.execute(httpUriRequest);
					HttpEntity entity = httpResponse.getEntity();
					Document doc = Jsoup.parse(EntityUtils.toString(entity));
					
					Elements elements = doc.getElementsByClass("zoom");
					Element element = elements.get(0);
					System.out.println(element.text());
					String html = element.html();
					
					FileOutputStream outputStream = new FileOutputStream(new File("D:\\subject\\"+poll.getTid()+".html"));
					FileChannel channel = outputStream.getChannel();
					ByteBuffer src = Charset.forName("utf8").encode(html);
					channel.write(src);
					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			    
			}
		}
	}
	
	public static void main(String[] args) {
		
		HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
		CloseableHttpClient httpClient = httpClientBuilder.build();
		//http://liuyan.people.com.cn/threads/content?tid=5062720
		String uri ="http://liuyan.people.com.cn/threads/content?tid=5062720";
		RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(5000)
                .setConnectTimeout(5000)
                .setConnectionRequestTimeout(5000)
                .build();
		HttpUriRequest httpUriRequest = RequestBuilder.get(uri).setConfig(requestConfig).build();
		
		httpUriRequest.addHeader("Accept", "application/json, text/javascript, */*; q=0.01");
	    httpUriRequest.addHeader("Accept-Encoding", "gzip, deflate, sdch");
	    httpUriRequest.addHeader("Accept-Language", "zh-CN,zh;q=0.8");
	    httpUriRequest.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
	    httpUriRequest.addHeader("Host", "liuyan.people.com.cn");
	    httpUriRequest.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36");
	    httpUriRequest.addHeader("Origin", "http://liuyan.people.com.cn");
	    httpUriRequest.addHeader("Referer", "http://liuyan.people.com.cn/threads/list?fid=539");
	    
	    CloseableHttpResponse httpResponse;
		try {
			httpResponse = httpClient.execute(httpUriRequest);
			HttpEntity entity = httpResponse.getEntity();
			
//			int read = content.read(buffer);
//			System.out.println(new String(buffer, "utf-8"));
//			int count = 0;
//            while (count == 0) {
//             count = Integer.parseInt(""+entity.getContentLength());//in.available();
//            }
//            byte[] bytes = new byte[count];
//            int readCount = 0; // 已经成功读取的字节的个数
//            while (readCount <= count) {
//             if(readCount == count)break;
//             readCount += content.read(bytes, readCount, count - readCount);
//            }
//            
//            //转换成字符串
//            String string = new String(bytes, 0, readCount, "UTF-8");
			Document doc = Jsoup.parse(EntityUtils.toString(entity));
			
			Elements elements = doc.getElementsByClass("zoom");
			Element element = elements.get(0);
			System.out.println(element.text());
			String html = element.html();
			
			FileOutputStream outputStream = new FileOutputStream(new File("D:\\subject\\"+"5062720.html"));
			FileChannel channel = outputStream.getChannel();
			ByteBuffer src = Charset.forName("utf8").encode(html);
			channel.write(src);
			outputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
