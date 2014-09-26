package com.easyweather.app.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 与服务器端交互的工具类
 */
public class HttpUtil {
	/**
	 * 向服务器端发送HTTP请求
	 */
	public static void sendHttpRequest(final String address,
			final Httpcallbacklistener listener){
		//由于访问服务器端可能会是耗时操作，所以需要开启子线程来处理
		new Thread(new Runnable() {
			@Override
			public void run() {
				//使用HttpURLConnection方式来连接服务器端
				HttpURLConnection connection = null;
				try {
					//构建服务器端地址
					URL url = new URL(address);
					//与服务器端建立连接
					connection = (HttpURLConnection) url.openConnection();
					//使用GET方式发送请求，并设定超时时间
					connection.setRequestMethod("GET");
					connection.setConnectTimeout(8000);
					connection.setReadTimeout(8000);
					//开启IO流读取服务器端返回的数据
					InputStream in = connection.getInputStream();
					BufferedReader reader = new BufferedReader(new InputStreamReader(in));
					StringBuilder response = new StringBuilder();
					String line;
					while((line = reader.readLine()) != null){
						response.append(line);
					}
					//回调onFinish()方法
					if(listener != null){
						listener.onFinish(response.toString());
					}
				} catch (Exception e) {
					if(listener != null){
						//回调onError()方法
						listener.onError(e);
					}
				} finally{
					//最后一定要记得关闭连接
					if(connection != null){
						connection.disconnect();
					}
				}
			}
		});
		
		
		
	}
}
