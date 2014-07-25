package com.babting.igo.model;

import java.util.ArrayList;
import java.util.List;

import com.babting.igo.R;

public enum Categorys {
	CATEGORY_BAR(R.id.categoryBarImgBtn, "Bar", "category_bar", R.drawable.marker_bar),
	CATEGORY_BREAD(R.id.categoryBreadImgBtn, "빵", "category_bread", R.drawable.marker_bread),
	CATEGORY_CHINESE(R.id.categoryChineseImgBtn, "중국음식", "category_chiness", R.drawable.marker_chinese),
	CATEGORY_COFFEE(R.id.categoryCoffeeImgBtn, "커피", "category_coffee", R.drawable.marker_coffee),
	CATEGORY_DATE(R.id.categoryDateImgBtn, "데이트", "category_date", R.drawable.marker_date),
	CATEGORY_FAMILY(R.id.categoryFamilyImgBtn, "가족", "category_family", R.drawable.marker_family),
	CATEGORY_FASTFOOD(R.id.categoryFastfoodImgBtn, "패스트푸드", "category_fastfood", R.drawable.marker_fastfood),
	CATEGORY_ICECREAM(R.id.categoryIceCreamImgBtn, "아이스크림", "category_icecream", R.drawable.marker_icecream),
	CATEGORY_NOODLE(R.id.categoryNoodleImgBtn, "면", "category_noodle", R.drawable.marker_noodle),
	CATEGORY_RESTAURANT(R.id.categoryRestaurantImgBtn, "레스토랑", "category_restaurant", R.drawable.marker_restaurant);
	
	Categorys(int id, String name, String code, int markerImgId) {
		this.id = id;
		this.name = name;
		this.code = code;
		this.markerImgId = markerImgId;
	}
	
	private int id;
	private String name;
	private String code;
	private int markerImgId;
	
	public int getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public String getCode() {
		return code;
	}
	public int getMarkerImgId() {
		return markerImgId;
	}
	
	public static Categorys getCategorysInfo(int id) {
		Categorys[] categorysArr = Categorys.values();
		for(Categorys categorys : categorysArr) {
			if(categorys.getId() == id) {
				return categorys;
			}
		}
		return null;
	}
	
	public static Categorys getCategorysInfoFromCode(String code) {
		Categorys[] categorysArr = Categorys.values();
		for(Categorys categorys : categorysArr) {
			if(code.equals(categorys.getCode())) {
				return categorys;
			}
		}
		return null;
	}
	
	public static Integer[] getIds() {
		Categorys[] categorys = Categorys.values();
		List<Integer> categoryIdList = new ArrayList<Integer>();
		for(Categorys category : categorys) {
			categoryIdList.add(category.getId());
		}
		
		return categoryIdList.toArray(new Integer[1]);
	}
}
