package com.easyweather.app.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.easyweather.app.R;
import com.easyweather.app.db.DBOperator;
import com.easyweather.app.model.City;
import com.easyweather.app.model.County;
import com.easyweather.app.model.Province;
import com.easyweather.app.util.HttpUtil;
import com.easyweather.app.util.Httpcallbacklistener;
import com.easyweather.app.util.LogUitl;
import com.easyweather.app.util.Utility;

public class ChooseAreaActivity extends Activity{
	public static final int LEVEL_PROVINCE = 0;
	public static final int LEVEL_CITY = 1;
	public static final int LEVEL_COUNTY = 2;
	
	private ProgressDialog progressDialog;
	private TextView titleText;
	private ListView listView;
	private ArrayAdapter<String> adapter;
	private DBOperator dbOperator;
	private List<String> dataSourceList = new ArrayList<String>();
	
	//是否从WeatherActivity中跳转过来
	private boolean isFromeWeatherActivity;
	
	//省列表
	private List<Province> provinceList;
	
	//市列表
	private List<City> cityList;
	
	//县列表
	private List<County> countyList;
	
	//选中的省份
	private Province selectedProvince;
	
	//选中的城市
	private City selectedCity;
	
	//当前选中的级别
	private int currentSelectedLevel;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//预处理
		isFromeWeatherActivity = getIntent().getBooleanExtra("from_weather_activity", false);
		//读取SharedPreference文件中的数据
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		if(prefs.getBoolean("city_selected", false) && !isFromeWeatherActivity){
			Intent intent = new Intent(this, WeatherActivity.class);
			startActivity(intent);
			finish();
			return;
		}
			
		//去除系统默认的标题头
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choose_area);
		
		dbOperator = DBOperator.getInstance(this);
		listView = (ListView) findViewById(R.id.list_view);
		titleText = (TextView) findViewById(R.id.title_text);
		
		//创建适配器
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataSourceList);
		//将适配器绑定到具体的控件上面
		LogUitl.d("---- Log ----", "listView: "+ listView);
		listView.setAdapter(adapter);
		
		//给ListView列表元素添加点击事件
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				//如果当前选中的级别为省份，则查询出它包含的所有城市
				if(currentSelectedLevel == LEVEL_PROVINCE){
					selectedProvince = provinceList.get(position);
					//调用查询城市的方法
					queryCity();
				}else if(currentSelectedLevel == LEVEL_CITY){
					selectedCity = cityList.get(position);
					//调用查询县的方法
					queryCounty();
				}else if(currentSelectedLevel == LEVEL_COUNTY){
					String countyCode = countyList.get(position).getCountyCode();
					Intent intent = new Intent(ChooseAreaActivity.this, WeatherActivity.class);
					intent.putExtra("county_code", countyCode);
					startActivity(intent);
					finish();
				}
			}
		});
		
	}

	/**
	 * 查询全国所有的省份，优先从数据库查询，如果没有去服务器端查询
	 */
	private void queryProvince(){
		//执行到本地数据库查询省份操作
		provinceList = dbOperator.loadProvince();
		//(1) 如果查询到省份则进行处理
		if(provinceList.size() > 0){
			//每查询一次省份，都要把之前数据源中的数据清除，以便把查询到的数据添加到数据源中
			dataSourceList.clear();
			//遍历查询结果，并将省份的名称添加到数据源中
			for (Province province : provinceList) {
				dataSourceList.add(province.getProvinceName());
			}
			
			//每当数据源发送变化时，需要通知适配器
			adapter.notifyDataSetChanged();
			
			//设置默认选中为标题头显示的数据（省份）
			listView.setSelection(0);
			titleText.setText("中国");
			
			//设置当前选中的级别为省份
			currentSelectedLevel = LEVEL_PROVINCE;
			
		}else { //(2) 如果没有查询到数据，则到服务器端查询
			queryFromServer(null, "province");
		}
	}
	
	/**
	 * 查询选中省内所有的城市，优先从本地数据库查询，如果没有则到服务器端查询
	 */
	private void queryCity(){
		//执行本地数据库查询操作
		cityList = dbOperator.loadCity(selectedProvince.getId());
		//(1) 如果查询到城市则进行处理
		if (cityList.size() > 0) {
			//每查询一次城市，都要把之前数据源中的数据清除，以便把查询到的数据添加到数据源中
			dataSourceList.clear();
			//遍历查询结果，并将城市的名称添加到数据源中
			for (City city : cityList){
				dataSourceList.add(city.getCityName());
			}
			
			//每当数据源发送变化时，需要通知适配器
			adapter.notifyDataSetChanged();
			
			//设置默认选中为标题头显示的数据（城市）
			listView.setSelection(0);
			titleText.setText(selectedProvince.getProvinceName());
			
			//设置当前选中的级别为城市
			currentSelectedLevel = LEVEL_CITY;
		}else { //(2) 如果没有查询到数据，则到服务器端查询
			queryFromServer(selectedProvince.getProvinceCode(), "city");
		}
	}
	
	/**
	 * 查询选中城市内所有的县，优先从本地数据库查询，如果没有则到服务器端查询
	 */
	private void queryCounty(){
		//执行本地数据库查询操作
		countyList = dbOperator.loadCounty(selectedCity.getId());
		//(1) 如果查询到县则进行处理
		if(countyList.size() > 0){
			//每查询一次县，都要把之前数据源中的数据清除，以便把查询到的数据添加到数据源中
			dataSourceList.clear();
			//遍历查询结果，并将城市的名称添加到数据源中
			for(County county : countyList){
				dataSourceList.add(county.getCountyName());
			}
			
			//每当数据源发送变化时，需要通知适配器
			adapter.notifyDataSetChanged();
			
			//设置默认选中的为标题头显示的数据（县）
			listView.setSelection(0);
			titleText.setText(selectedCity.getCityName());
			
			//设置当前选中的级别为县
			currentSelectedLevel = LEVEL_COUNTY;
		}else { //(2) 如果没有查询到数据，则到服务器端查询
			queryFromServer(selectedCity.getCityCode(), "county");
		}
	}

	/**
	 * 根据传入的代号和类型从服务器端查询省、市、县数据
	 */
	private void queryFromServer(final String code, final String type) {
		String address;
		if(!TextUtils.isEmpty(code)){
			address = "http://www.weather.com.cn/data/list3/city" + code + ".xml";
		}else{
			address = "http://www.weather.com.cn/data/list3/city.xml";
		}
		
		//显示进度对话框
		showProgressDialog();
		
		//向服务器端发送查询请求
		HttpUtil.sendHttpRequest(address, new Httpcallbacklistener() {
			@Override
			public void onFinish(String response) {
				boolean result = false;
				//根据传入的类型来匹配处理操作
				if("province".equals(type)){
					result = Utility.handleProvinceResponse(dbOperator, response);
				}else if("city".equals(type)){
					result = Utility.handleCityResponse(dbOperator, response, selectedProvince.getId());
				}else if("county".equals(type)){
					result = Utility.handleCountyResponse(dbOperator, response, selectedCity.getId());
				}
				
				//通过runOnUiThread()方法回到主线程（这步很关键！！）
				if(result){
					runOnUiThread(new Runnable() {
						public void run() {
							//关闭进度对话框
							closeProgressDialog();
							if("province".equals(type)){
								queryProvince();
							}else if("city".equals(type)){
								queryCity();
							}else if("county".equals(type)){
								queryCounty();
							}
						}
					});
				}
			}
			
			@Override
			public void onError(Exception e) {
				//通过runOnUiThread()方法回到主线程
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						//关闭进度对话框
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivity.this, "加载失败",
								Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
			
	}

	/**
	 * 显示进度对话框
	 */
	private void showProgressDialog() {
		//如果进度对话框对象不存在，则创建它
		if(progressDialog == null){
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("正在加载...");
			//设置为不可取消
			progressDialog.setCanceledOnTouchOutside(false);
		}
		//如果进度对话框对象已经存在，则显示它
		progressDialog.show();
	}
	
	/**
	 * 关闭进度对话框
	 */
	private void closeProgressDialog() {
		if(progressDialog != null){
			progressDialog.dismiss();
		}
	}
	
	/**
	 * 返回操作同步到Back键（捕获Back键，根据当前级别来判断应当返回省市列表，还是直接退出）
	 */
	@Override
	public void onBackPressed() {
		//如果当前在县一级，则设置可以退回到市一级
		if(currentSelectedLevel == LEVEL_COUNTY){
			queryCity();
		}//如果当前是在市一级，则设置可以退回到省一级
		else if(currentSelectedLevel == LEVEL_CITY){
			queryProvince();
		}//如果当前已经在省一级，则返回操作设置为结束
		else{
			finish();
		}
	}
	
}
