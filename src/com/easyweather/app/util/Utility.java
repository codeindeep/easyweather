package com.easyweather.app.util;

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
					county.setCoutyCode(array[0]);
					county.setCoutyName(array[1]);
					county.setCityId(cityId);
					//���County�洢�����ݿ���
				}
				return true;
			}
		}
		return false;
	}
	
}
