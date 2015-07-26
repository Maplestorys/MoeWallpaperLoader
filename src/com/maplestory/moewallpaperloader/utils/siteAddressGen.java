package com.maplestory.moewallpaperloader.utils;

public class siteAddressGen {

	public siteAddressGen() {
		// TODO Auto-generated constructor stub
	}
//	private static final String siteBaseAddress = "https://yande.re/post?";
	private static final String siteBaseAddress = "http://konachan.com/post?";
	private static final String sitePageAddress = "page=";
	private static final String siteTagAddress = "&tags=rating:s";
	
	
	public static String getSiteAddress(int pageNum) {
		return siteBaseAddress + sitePageAddress+pageNum+siteTagAddress;
	}
	
	public static String getSiteAddress(int pageNum, String[] tags) {
		String result = siteBaseAddress + sitePageAddress + pageNum + siteTagAddress ;
		if(tags.length==1){
			result+=tags[0];
		}else{
			result+=tags[0];
			for(int i =1; i <tags.length;i++) {
				result +="+" +  tags[i];
			}
		}
		return result;
	}
}
