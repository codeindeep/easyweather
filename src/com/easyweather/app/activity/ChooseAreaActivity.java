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
	
	//�Ƿ��WeatherActivity����ת����
	private boolean isFromeWeatherActivity;
	
	//ʡ�б�
	private List<Province> provinceList;
	
	//���б�
	private List<City> cityList;
	
	//���б�
	private List<County> countyList;
	
	//ѡ�е�ʡ��
	private Province selectedProvince;
	
	//ѡ�еĳ���
	private City selectedCity;
	
	//��ǰѡ�еļ���
	private int currentSelectedLevel;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Ԥ����
		isFromeWeatherActivity = getIntent().getBooleanExtra("from_weather_activity", false);
		//��ȡSharedPreference�ļ��е�����
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		if(prefs.getBoolean("city_selected", false) && !isFromeWeatherActivity){
			Intent intent = new Intent(this, WeatherActivity.class);
			startActivity(intent);
			finish();
			return;
		}
			
		//ȥ��ϵͳĬ�ϵı���ͷ
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choose_area);
		
		dbOperator = DBOperator.getInstance(this);
		listView = (ListView) findViewById(R.id.list_view);
		titleText = (TextView) findViewById(R.id.title_text);
		
		//����������
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataSourceList);
		//���������󶨵�����Ŀؼ�����
		LogUitl.d("---- Log ----", "listView: "+ listView);
		listView.setAdapter(adapter);
		
		//��ListView�б�Ԫ����ӵ���¼�
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				//�����ǰѡ�еļ���Ϊʡ�ݣ����ѯ�������������г���
				if(currentSelectedLevel == LEVEL_PROVINCE){
					selectedProvince = provinceList.get(position);
					//���ò�ѯ���еķ���
					queryCity();
				}else if(currentSelectedLevel == LEVEL_CITY){
					selectedCity = cityList.get(position);
					//���ò�ѯ�صķ���
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
	 * ��ѯȫ�����е�ʡ�ݣ����ȴ����ݿ��ѯ�����û��ȥ�������˲�ѯ
	 */
	private void queryProvince(){
		//ִ�е��������ݿ��ѯʡ�ݲ���
		provinceList = dbOperator.loadProvince();
		//(1) �����ѯ��ʡ������д���
		if(provinceList.size() > 0){
			//ÿ��ѯһ��ʡ�ݣ���Ҫ��֮ǰ����Դ�е�����������Ա�Ѳ�ѯ����������ӵ�����Դ��
			dataSourceList.clear();
			//������ѯ���������ʡ�ݵ�������ӵ�����Դ��
			for (Province province : provinceList) {
				dataSourceList.add(province.getProvinceName());
			}
			
			//ÿ������Դ���ͱ仯ʱ����Ҫ֪ͨ������
			adapter.notifyDataSetChanged();
			
			//����Ĭ��ѡ��Ϊ����ͷ��ʾ�����ݣ�ʡ�ݣ�
			listView.setSelection(0);
			titleText.setText("�й�");
			
			//���õ�ǰѡ�еļ���Ϊʡ��
			currentSelectedLevel = LEVEL_PROVINCE;
			
		}else { //(2) ���û�в�ѯ�����ݣ��򵽷������˲�ѯ
			queryFromServer(null, "province");
		}
	}
	
	/**
	 * ��ѯѡ��ʡ�����еĳ��У����ȴӱ������ݿ��ѯ�����û���򵽷������˲�ѯ
	 */
	private void queryCity(){
		//ִ�б������ݿ��ѯ����
		cityList = dbOperator.loadCity(selectedProvince.getId());
		//(1) �����ѯ����������д���
		if (cityList.size() > 0) {
			//ÿ��ѯһ�γ��У���Ҫ��֮ǰ����Դ�е�����������Ա�Ѳ�ѯ����������ӵ�����Դ��
			dataSourceList.clear();
			//������ѯ������������е�������ӵ�����Դ��
			for (City city : cityList){
				dataSourceList.add(city.getCityName());
			}
			
			//ÿ������Դ���ͱ仯ʱ����Ҫ֪ͨ������
			adapter.notifyDataSetChanged();
			
			//����Ĭ��ѡ��Ϊ����ͷ��ʾ�����ݣ����У�
			listView.setSelection(0);
			titleText.setText(selectedProvince.getProvinceName());
			
			//���õ�ǰѡ�еļ���Ϊ����
			currentSelectedLevel = LEVEL_CITY;
		}else { //(2) ���û�в�ѯ�����ݣ��򵽷������˲�ѯ
			queryFromServer(selectedProvince.getProvinceCode(), "city");
		}
	}
	
	/**
	 * ��ѯѡ�г��������е��أ����ȴӱ������ݿ��ѯ�����û���򵽷������˲�ѯ
	 */
	private void queryCounty(){
		//ִ�б������ݿ��ѯ����
		countyList = dbOperator.loadCounty(selectedCity.getId());
		//(1) �����ѯ��������д���
		if(countyList.size() > 0){
			//ÿ��ѯһ���أ���Ҫ��֮ǰ����Դ�е�����������Ա�Ѳ�ѯ����������ӵ�����Դ��
			dataSourceList.clear();
			//������ѯ������������е�������ӵ�����Դ��
			for(County county : countyList){
				dataSourceList.add(county.getCountyName());
			}
			
			//ÿ������Դ���ͱ仯ʱ����Ҫ֪ͨ������
			adapter.notifyDataSetChanged();
			
			//����Ĭ��ѡ�е�Ϊ����ͷ��ʾ�����ݣ��أ�
			listView.setSelection(0);
			titleText.setText(selectedCity.getCityName());
			
			//���õ�ǰѡ�еļ���Ϊ��
			currentSelectedLevel = LEVEL_COUNTY;
		}else { //(2) ���û�в�ѯ�����ݣ��򵽷������˲�ѯ
			queryFromServer(selectedCity.getCityCode(), "county");
		}
	}

	/**
	 * ���ݴ���Ĵ��ź����ʹӷ������˲�ѯʡ���С�������
	 */
	private void queryFromServer(final String code, final String type) {
		String address;
		if(!TextUtils.isEmpty(code)){
			address = "http://www.weather.com.cn/data/list3/city" + code + ".xml";
		}else{
			address = "http://www.weather.com.cn/data/list3/city.xml";
		}
		
		//��ʾ���ȶԻ���
		showProgressDialog();
		
		//��������˷��Ͳ�ѯ����
		HttpUtil.sendHttpRequest(address, new Httpcallbacklistener() {
			@Override
			public void onFinish(String response) {
				boolean result = false;
				//���ݴ����������ƥ�䴦�����
				if("province".equals(type)){
					result = Utility.handleProvinceResponse(dbOperator, response);
				}else if("city".equals(type)){
					result = Utility.handleCityResponse(dbOperator, response, selectedProvince.getId());
				}else if("county".equals(type)){
					result = Utility.handleCountyResponse(dbOperator, response, selectedCity.getId());
				}
				
				//ͨ��runOnUiThread()�����ص����̣߳��ⲽ�ܹؼ�������
				if(result){
					runOnUiThread(new Runnable() {
						public void run() {
							//�رս��ȶԻ���
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
				//ͨ��runOnUiThread()�����ص����߳�
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						//�رս��ȶԻ���
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivity.this, "����ʧ��",
								Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
			
	}

	/**
	 * ��ʾ���ȶԻ���
	 */
	private void showProgressDialog() {
		//������ȶԻ�����󲻴��ڣ��򴴽���
		if(progressDialog == null){
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("���ڼ���...");
			//����Ϊ����ȡ��
			progressDialog.setCanceledOnTouchOutside(false);
		}
		//������ȶԻ�������Ѿ����ڣ�����ʾ��
		progressDialog.show();
	}
	
	/**
	 * �رս��ȶԻ���
	 */
	private void closeProgressDialog() {
		if(progressDialog != null){
			progressDialog.dismiss();
		}
	}
	
	/**
	 * ���ز���ͬ����Back��������Back�������ݵ�ǰ�������ж�Ӧ������ʡ���б�����ֱ���˳���
	 */
	@Override
	public void onBackPressed() {
		//�����ǰ����һ���������ÿ����˻ص���һ��
		if(currentSelectedLevel == LEVEL_COUNTY){
			queryCity();
		}//�����ǰ������һ���������ÿ����˻ص�ʡһ��
		else if(currentSelectedLevel == LEVEL_CITY){
			queryProvince();
		}//�����ǰ�Ѿ���ʡһ�����򷵻ز�������Ϊ����
		else{
			finish();
		}
	}
	
}
