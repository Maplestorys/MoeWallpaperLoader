package com.maplestory.moewallpaperloader.utils;

public abstract class MultiThreadDownloadListenner 
{
	/**
	 * ���سɹ�����
	 */
	public abstract void onSuccess();
	
	/**
	 * ������
	 */
	public abstract void onProgress(long bytes, float percent);
}
