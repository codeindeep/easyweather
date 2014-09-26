package com.easyweather.app.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * ��������˽����Ĺ�����
 */
public class HttpUtil {
	/**
	 * ��������˷���HTTP����
	 */
	public static void sendHttpRequest(final String address,
			final Httpcallbacklistener listener){
		//���ڷ��ʷ������˿��ܻ��Ǻ�ʱ������������Ҫ�������߳�������
		new Thread(new Runnable() {
			@Override
			public void run() {
				//ʹ��HttpURLConnection��ʽ�����ӷ�������
				HttpURLConnection connection = null;
				try {
					//�����������˵�ַ
					URL url = new URL(address);
					//��������˽�������
					connection = (HttpURLConnection) url.openConnection();
					//ʹ��GET��ʽ�������󣬲��趨��ʱʱ��
					connection.setRequestMethod("GET");
					connection.setConnectTimeout(8000);
					connection.setReadTimeout(8000);
					//����IO����ȡ�������˷��ص�����
					InputStream in = connection.getInputStream();
					BufferedReader reader = new BufferedReader(new InputStreamReader(in));
					StringBuilder response = new StringBuilder();
					String line;
					while((line = reader.readLine()) != null){
						response.append(line);
					}
					//�ص�onFinish()����
					if(listener != null){
						listener.onFinish(response.toString());
					}
				} catch (Exception e) {
					if(listener != null){
						//�ص�onError()����
						listener.onError(e);
					}
				} finally{
					//���һ��Ҫ�ǵùر�����
					if(connection != null){
						connection.disconnect();
					}
				}
			}
		});
		
		
		
	}
}
