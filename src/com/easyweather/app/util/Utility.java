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
 * ���ڽ�����������|���У�����|���С����ָ�ʽ������
 */
public class Utility {
	/**
	 * ��������������˷��ص�Province����
	 */
	public synchronized static boolean handleProvinceResponse(
			DBOperator dbOperator, String response){
		if (!TextUtils.isEmpty(response)) {
			//�� �����ŷָ���
			String[] allProvinces = response.split(" ,");
			//�����ָ�֮������ݿ�
			if (allProvinces != null && allProvinces.length > 0) {
				for (String temp : allProvinces) {
					//�� �����߷ָ�
					String[] array = temp.split("\\|");
					//���ָ�����֮�������װ��Provinceʵ������
					Province province = new Province();
					province.setProvinceCode(array[0]);
					province.setProvinceName(array[1]);
					//���Province�洢�����ݿ����
					dbOperator.saveProvince(province);
				}
				return true;
			}
		}
		return false;
	}
	
	/**
	 * ��������������˷��ص�City����
	 */
	public static boolean handleCityResponse(DBOperator dbOperator,
			String response, int provinceId){
		if(!TextUtils.isEmpty(response)){
			//�� �����ŷָ���
			String[] allCities = response.split(" ,");
			//�����ָ�֮�������
			if(allCities != null && allCities.length > 0){
				for (String temp : allCities) {
					//�� �����߷ָ���
					String[] array = temp.split("\\|");
					City city = new City();
					city.setCityCode(array[0]);
					city.setCityName(array[1]);
					city.setProvinceId(provinceId);
					//���City�洢�����ݿ���
					dbOperator.saveCity(city);
				}
				return true;
			}
		}
		return false;
	}
	
	/**
	 * ��������������˷��ص�County����
	 */
	public static boolean handleCountyResponse(DBOperator dbOperator,
			String response, int cityId){
		if(!TextUtils.isEmpty(response)){
			//�� �����ŷָ���
			String[] allCounties = response.split(" ,");
			//�����ָ�֮�������
			if (allCounties != null && allCounties.length > 0) {
				for (String temp : allCounties) {
					//�� �����߷ָ���
					String[] array = temp.split("\\|");
					County county = new County();
					county.setCountyCode(array[0]);
					county.setCountyName(array[1]);
					county.setCityId(cityId);
					//���County�洢�����ݿ���
				}
				return true;
			}
		}
		return false;
	}
	
	/**
	 * �������������ص�JSON���ݣ������浽����
	 */
	public static void handleWeatherResponse(Context context, String response){
		try {
			//����JSON����
			JSONObject jsonObject = new JSONObject(response);
			JSONObject weatherInfo = jsonObject.getJSONObject("weatherinfo");
			String cityName = weatherInfo.getString("city");
			String weatherCode = weatherInfo.getString("cityid");
			String temp1 = weatherInfo.getString("temp1");
			String temp2 = weatherInfo.getString("temp2");
			String weatherDesp = weatherInfo.getString("weather");
			String publishTime = weatherInfo.getString("ptime");
			
			//��������õ���������
			saveWeatherInfo(context, cityName, weatherCode, temp1, temp2, 
					weatherDesp, publishTime);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * �������õ������������ݱ��浽SharedPreference�ļ���
	 */
	private static void saveWeatherInfo(Context context, String cityName,
			String weatherCode, String temp1, String temp2, String weatherDesp,
			String publishTime) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy��M��d��", Locale.CHINA);
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
