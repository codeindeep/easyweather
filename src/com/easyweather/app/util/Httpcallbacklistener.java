package com.easyweather.app.util;

public interface Httpcallbacklistener {
	void onFinish(String response);
	void onError(Exception e);
}
