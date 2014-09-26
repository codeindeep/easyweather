package com.easyweather.app.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.easyweather.app.db.DBOperator;
import com.easyweather.app.model.City;
import com.easyweather.app.model.County;
import com.easyweather.app.model.Province;

/**
 * 用于解析处理“代号|城市，代号|城市”这种格式的数据
 */
public class Utility {
	/**
	 * 解析处理服务器端返回的Province数据
	 */
	public synchronized static boolean handleProvinceResponse(
			DBOperator dbOperator, String response){
		if (!TextUtils.isEmpty(response)) {
			//① 按逗号分隔开
			String[] allProvinces = response.split(" ,");
			//遍历分隔之后的数据开
			if (allProvinces != null && allProvinces.length > 0) {
				for (String temp : allProvinces) {
					//② 按竖线分隔
					String[] array = temp.split("\\|");
					//将分隔处理之后的数据装入Province实体类中
					Province province = new Province();
					province.setProvinceCode(array[0]);
					province.setProvinceName(array[1]);
					//最后将Province存储到数据库表中
					dbOperator.saveProvince(province);
				}
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 解析处理服务器端返回的City数据
	 */
	public static boolean handleCityResponse(DBOperator dbOperator,
			String response, int provinceId){
		if(!TextUtils.isEmpty(response)){
			//① 按逗号分隔开
			String[] allCities = response.split(" ,");
			//遍历分隔之后的数据
			if(allCities != null && allCities.length > 0){
				for (String temp : allCities) {
					//② 按竖线分隔开
					String[] array = temp.split("\\|");
					City city = new City();
					city.setCityCode(array[0]);
					city.setCityName(array[1]);
					city.setProvinceId(provinceId);
					//最后将City存储到数据库中
					dbOperator.saveCity(city);
				}
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 解析处理服务器端返回的County数据
	 */
	public static boolean handleCountyResponse(DBOperator dbOperator,
			String response, int cityId){
		if(!TextUtils.isEmpty(response)){
			//① 按逗号分隔开
			String[] allCounties = response.split(" ,");
			//遍历分隔之后的数据
			if (allCounties != null && allCounties.length > 0) {
				for (String temp : allCounties) {
					//② 按竖线分隔开
					String[] array = temp.split("\\|");
					County county = new County();
					county.setCountyCode(array[0]);
					county.setCountyName(array[1]);
					county.setCityId(cityId);
					//最后将County存储到数据库中
				}
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 解析服务器返回的JSON数据，并保存到本地
	 */
	public static void handleWeatherResponse(Context context, String response){
		try {
			//解析JSON数据
			JSONObject jsonObject = new JSONObject(response);
			JSONObject weatherInfo = jsonObject.getJSONObject("weatherinfo");
			String cityName = weatherInfo.getString("city");
			String weatherCode = weatherInfo.getString("cityid");
			String temp1 = weatherInfo.getString("temp1");
			String temp2 = weatherInfo.getString("temp2");
			String weatherDesp = weatherInfo.getString("weather");
			String publishTime = weatherInfo.getString("ptime");
			
			//保存解析好的天气数据
			saveWeatherInfo(context, cityName, weatherCode, temp1, temp2, 
					weatherDesp, publishTime);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 将解析好的所有天气数据保存到SharedPreference文件中
	 */
	private static void saveWeatherInfo(Context context, String cityName,
			String weatherCode, String temp1, String temp2, String weatherDesp,
			String publishTime) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
		SharedPreferences.Editor editor = PreferenceManager
				.getDefaultSharedPreferences(context).edit();
		editor.putBoolean("city_selected", true);
		editor.putString("city_name", cityName);
		editor.putString("weather_code", weatherCode);
		editor.putString("temp1", temp1);
		editor.putString("temp2", temp2);
		editor.putString("weather_desp", weatherDesp);
		editor.putString("publish_time", publishTime);
		editor.putString("current_date", dateFormat.format(new Date()));
		editor.commit();
	}
}
