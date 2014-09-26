package com.easyweather.app.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.easyweather.app.model.City;
import com.easyweather.app.model.County;
import com.easyweather.app.model.Province;

/**
 * ���ݿⳣ�ò�����װ��
 */
public class DBOperator {
	/**
	 * ���ݿ���
	 */
	public static final String DB_NAME = "easy_weather";
	
	/**
	 * ���ݿ�汾����Ϊ����ʹ�ã�
	 */
	public static final int DB_VERSION = 1;
	
	/**
	 * ���ݿ������ʵ��
	 */
	private static DBOperator mDBOperator;
	
	/**
	 * ���ݿ������ʵ��
	 */
	private SQLiteDatabase mSQLiteDatabase;
	
	/**
	 * �����췽��˽�л�
	 */
	private DBOperator(Context context){
		//���һ���ɶ�д�����ݿ�ʵ��
		DBOpenHelper dbOpenHelper = new DBOpenHelper(context, DB_NAME, null, DB_VERSION);
		mSQLiteDatabase = dbOpenHelper.getWritableDatabase();
	}
	
	/**
	 * ��ȡDBOperator��ʵ��������ģʽ��
	 */
	public synchronized static DBOperator getInstance(Context context){
		if(mDBOperator == null){
			mDBOperator = new DBOperator(context);
		}
		return mDBOperator;
	}
	
	/**
	 * ��Province��ʡ�ݣ�ʵ���洢�����ݿ�
	 */
	public void saveProvince(Province province){
		if(province != null){
			//������ѯ������϶���
			ContentValues values = new ContentValues();
			values.put("province_name", province.getProvinceName());
			values.put("province_code", province.getProvinceCode());
			mSQLiteDatabase.insert("Province", null, values);
		}
	}
	
	/**
	 * �����ݿ��ж�ȡProvince��ʡ�ݣ�����
	 */
	public List<Province> loadProvince(){
		List<Province> list = new ArrayList<Province>();
		//��ѯ����Province
		Cursor cursor = mSQLiteDatabase.query("Province", null, null, null, null, null, null);
		//����Cursorȡֵ
		if(cursor.moveToFirst()){
			do {
				Province province = new Province();
				province.setId(cursor.getInt(cursor.getColumnIndex("id")));
				province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
				province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
				list.add(province);
			} while (cursor.moveToFirst());
		}
		return list;
	}
	
	/**
	 * ��City�����У�ʵ���洢�����ݿ�
	 */
	public void saveCity(City city){
		if(city != null){
			//������ѯ������϶���
			ContentValues values = new ContentValues();
			values.put("city_name", city.getCityName());
			values.put("city_code", city.getCityCode());
			values.put("province_id", city.getProvinceId());
			mSQLiteDatabase.insert("city", null, values);
		}
	}
	
	/**
	 * �����ݿ��ж�ȡ���г��У����У�����
	 */
	public List<City> loadCity(int provinceId){
		List<City> list = new ArrayList<City>();
		//��ѯĳ��ʡ����������г���
		Cursor cursor = mSQLiteDatabase.query(
				"City",  //Ҫ��ѯ�ı���
				null, //ָ�����ز�ѯ��¼��1��������
				"province_id = ?",  //��ѯ��������
				new String[]{
						String.valueOf(provinceId)
				},  //��ѯ��������ֵ
				null, //��ѯ��������
				null, //��ѯ��������
				null  //��������
		);
		//����Cursorȡֵ
		if(cursor.moveToFirst()){
			do {
				City city = new City();
				city.setId(cursor.getInt(cursor.getColumnIndex("id")));
				city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
				city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
				city.setProvinceId(provinceId);
				list.add(city);
			} while (cursor.moveToFirst());
		}
		return list;		
	}
	
	/**
	 * ��County���أ�ʵ���洢�����ݿ�
	 */
	public void saveCounty(County county){
		if(county != null){
			//������ѯ������϶���
			ContentValues values = new ContentValues();
			values.put("county_name", county.getCountyName());
			values.put("county_code", county.getCountyCode());
			values.put("city_id", county.getCityId());
			mSQLiteDatabase.insert("county", null, values);
		}
	}
	
	/**
	 * �����ݿ��ж�ȡ����County���أ�����
	 */
	public List<County> loadCounty(int cityId){
		List<County> list = new ArrayList<County>();
		//��ѯĳ�����������������
		Cursor cursor = mSQLiteDatabase.query(
				"County",
				null,
				"city_id",
				new String[]{
					String.valueOf(cityId)	
				},
				null,
				null,
				null
		);
		//����Cursorȡֵ
		if(cursor.moveToFirst()){
			do{
				County county = new County();
				county.setId(cursor.getInt(cursor.getColumnIndex("id")));
				county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
				county.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
				county.setCityId(cityId);
				list.add(county);
			}while(cursor.moveToFirst());
		}
		return list;
	}
}
