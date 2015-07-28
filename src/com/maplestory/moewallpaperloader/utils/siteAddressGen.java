package com.maplestory.moewallpaperloader.utils;

import org.junit.Test;

public class siteAddressGen {

	public siteAddressGen() {
		// TODO Auto-generated constructor stub
	}
//	private static final String siteBaseAddress = "https://yande.re/post?";
	private static final String siteBaseAddress = "http://konachan.com/post?";
	private static final String sitePageAddress = "page=";
//	private static final String siteTagAddress = "&tags=rating:s";
	private static final String siteTagAddress = "&tags=";
	
	public static String getSiteAddress(int pageNum) {
		return siteBaseAddress + sitePageAddress+pageNum+siteTagAddress;
	}

	public static String getSiteAddress(int pageNum, String tags) {
		String result = siteBaseAddress + sitePageAddress + pageNum + siteTagAddress ;
		if (result.charAt(result.length()-1)=='='){
			result += tags;
		}else{
			result +="+" +  tags;
		}
		return result;
	}
}
