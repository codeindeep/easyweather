package com.easyweather.app.util;

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
					county.setCoutyCode(array[0]);
					county.setCoutyName(array[1]);
					county.setCityId(cityId);
					//最后将County存储到数据库中
				}
				return true;
			}
		}
		return false;
	}
	
}
