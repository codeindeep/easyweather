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
 * 数据库常用操作封装类
 */
public class DBOperator {
	/**
	 * 数据库名
	 */
	public static final String DB_NAME = "easy_weather";
	
	/**
	 * 数据库版本（作为参数使用）
	 */
	public static final int DB_VERSION = 1;
	
	/**
	 * 数据库操作类实例
	 */
	private static DBOperator mDBOperator;
	
	/**
	 * 数据库基础类实例
	 */
	private SQLiteDatabase mSQLiteDatabase;
	
	/**
	 * 将构造方法私有化
	 */
	private DBOperator(Context context){
		//获得一个可读写的数据库实例
		DBOpenHelper dbOpenHelper = new DBOpenHelper(context, DB_NAME, null, DB_VERSION);
		mSQLiteDatabase = dbOpenHelper.getWritableDatabase();
	}
	
	/**
	 * 获取DBOperator的实例（单例模式）
	 */
	public synchronized static DBOperator getInstance(Context context){
		if(mDBOperator == null){
			mDBOperator = new DBOperator(context);
		}
		return mDBOperator;
	}
	
	/**
	 * 将Province（省份）实例存储到数据库
	 */
	public void saveProvince(Province province){
		if(province != null){
			//构建查询参数组合对象
			ContentValues values = new ContentValues();
			values.put("province_name", province.getProvinceName());
			values.put("province_code", province.getProvinceCode());
			mSQLiteDatabase.insert("Province", null, values);
		}
	}
	
	/**
	 * 从数据库中读取Province（省份）数据
	 */
	public List<Province> loadProvince(){
		List<Province> list = new ArrayList<Province>();
		//查询所有Province
		Cursor cursor = mSQLiteDatabase.query("Province", null, null, null, null, null, null);
		//遍历Cursor取值
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
	 * 将City（城市）实例存储到数据库
	 */
	public void saveCity(City city){
		if(city != null){
			//构建查询参数组合对象
			ContentValues values = new ContentValues();
			values.put("city_name", city.getCityName());
			values.put("city_code", city.getCityCode());
			values.put("province_id", city.getProvinceId());
			mSQLiteDatabase.insert("city", null, values);
		}
	}
	
	/**
	 * 从数据库中读取所有城市（城市）数据
	 */
	public List<City> loadCity(int provinceId){
		List<City> list = new ArrayList<City>();
		//查询某个省份下面的所有城市
		Cursor cursor = mSQLiteDatabase.query(
				"City",  //要查询的表名
				null, //指定返回查询记录的1个或多个列
				"province_id = ?",  //查询限制条件
				new String[]{
						String.valueOf(provinceId)
				},  //查询限制条件值
				null, //查询限制条件
				null, //查询限制条件
				null  //排序条件
		);
		//遍历Cursor取值
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
	 * 将County（县）实例存储到数据库
	 */
	public void saveCounty(County county){
		if(county != null){
			//构建查询参数组合对象
			ContentValues values = new ContentValues();
			values.put("county_name", county.getCountyName());
			values.put("county_code", county.getCountyCode());
			values.put("city_id", county.getCityId());
			mSQLiteDatabase.insert("county", null, values);
		}
	}
	
	/**
	 * 从数据库中读取所有County（县）数据
	 */
	public List<County> loadCounty(int cityId){
		List<County> list = new ArrayList<County>();
		//查询某个城市下面的所有县
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
		//遍历Cursor取值
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
