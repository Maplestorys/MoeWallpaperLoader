package com.maplestory.moewallpaperloader.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;



public class MultiThreadDownload 
{
	///< �����ļ�·����URL��
	private String path;
	///< ����Ŀ���ļ�
	private String dstPath;
	///< ����Ŀ���ļ�����
	private String dstFileName;
	///< Ŀ���ļ���Ŀ¼
	private String dstParentPath;
	///< �߳�����[Ĭ��Ϊ3��
	private int threadCount = 3;
	///< ��������С
	private int bufferLen = 1024 * 1024;
	///< �ļ��ܴ�С
	private long size = 0;
	///< ��ǰ�ļ���С�������onPorgress�л�ת��Ϊ�ٷְ٣���ǰ�����ֽ���/�ܴ�С��
	private long currentSize = 0;
	// /< ���߳����ؿ��ļ���С
	private long blockSize;
	// /< ��ǰ���е��߳���
	private int runningThreadCount;
	// /< ���ؼ�����
	private MultiThreadDownloadListenner mulDownLis;

	/**
	 * �������ص��ļ�·�����߳�����
	 * @param _path
	 * @param _threadCount - ���Դ�-1����ʾĬ��ֵ3
	 */
	public MultiThreadDownload(String _path, String _dstPath, int _threadCount) 
	{
		this.path = _path;
		this.dstPath = _dstPath;
		this.dstFileName = _dstPath;
		String[] splits = this.dstFileName.split("/");
		this.dstFileName = splits[splits.length - 1];
		this.dstParentPath = _dstPath.substring(0, _dstPath.lastIndexOf("/")) + "/";
		if (_threadCount > 0)
		{
			this.threadCount = _threadCount;
		}
	}

	public void start()
	{
		try 
		{
			URL url = new URL(this.path);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(5000);
			int code = conn.getResponseCode();
			if (200 == code) 
			{
				/* long */size = conn.getContentLength(); ///< size = conn.getContentLengthLong();

				// /< ���ɿհ�ͬ����С���ļ�
				blockSize = size / threadCount;
				File file = new File(this.dstPath);
				RandomAccessFile raf = new RandomAccessFile(file, "rw");
				raf.setLength(size);
				raf.close();
				//System.out.println("blockSize = " + blockSize);
				// /< �����̷ֱ߳������ļ�
				runningThreadCount = threadCount;
				for (int i = 1; i <= threadCount; ++i) 
				{
					long startPos = (i - 1) * blockSize;
					long endPos = i * blockSize - 1;
					if (i == threadCount) 
					{
						endPos = size - 1;
					}
					//System.out.println("startPos = " + startPos);
					//System.out.println("endPos = " + endPos);
					(new DownThread(this.path, i, startPos, endPos)).start();
				}
			}
		} 
		catch (MalformedURLException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (ProtocolException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setDownThreadListenner(
			MultiThreadDownloadListenner _mulDownLis) 
	{
		mulDownLis = _mulDownLis;
	}

	/**
	 * �����߳�
	 * 
	 * @author hl
	 * 
	 */
	private class DownThread extends Thread 
	{
		///< ���ص��ļ�·��
		private String path;
		///< �̵߳�ID��
		private int threadId;
		///< ���صĿ�ʼλ��
		private long startPos;
		///< ���ص���ֹλ��
		private long endPos;
		///< �߳���Ҫ���صĴ�С
		private long totalLen;

		public DownThread(String _path, int _threadId, 
						  long _startPos, long _endPos) 
		{
			this.path = _path;
			this.threadId = _threadId;
			this.startPos = _startPos;
			this.endPos = _endPos;
			this.totalLen = _endPos - _startPos + 1;
		}

		@Override
		public void run() 
		{
			try
			{
				// /< ��ȡ�ϴ����صĽ��ȣ����¼��㱾�����صĿ�ʼλ��
				File positionFile = new File(dstParentPath + dstFileName + this.threadId + ".txt");
				// /< ��ǰ�����̵߳��ܴ�С
				long total = 0;
				// /< ����м�¼�����ȡ�ϴν�����Ϣ
				if (positionFile.exists() && positionFile.length() > 0)
				{
					FileInputStream fIn = new FileInputStream(positionFile);
					BufferedReader br = new BufferedReader(
							new InputStreamReader(fIn));
					String lastTotal = br.readLine();
					long lastTotalInt = Long.valueOf(lastTotal);
					startPos += lastTotalInt; 	///< ����λ�����¼���
					///< ��ȡ��������λ��
					currentSize += lastTotalInt;
					total += lastTotalInt; 		///< ���غ���ܴ�С�������ϴ�����
					br.close();
				}

				///< ����URLConnection���зֶ����ء����÷ֶβ�����
				URL url = new URL(path);
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				conn.setRequestMethod("GET");
				conn.setRequestProperty("Range", "bytes=" + startPos + "-"
						+ endPos);
				conn.setConnectTimeout(5000);
				int code = conn.getResponseCode();
				///< �ֶ����سɹ���־
				if (206 == code) 
				{
					///< ��ȡ���������
					InputStream inputS = conn.getInputStream();
					///< ���������ļ�
					File file = new File(dstPath);
					RandomAccessFile raf = new RandomAccessFile(file, "rw");
					///< ָ��ĳ����ʼλ�ã���ʼд�ļ�
					raf.seek(startPos);

					///< ʵ�ʶ�ȡ�Ĵ�С
					int len = 0;
					///< д�ļ���������С1024 * 1024
					byte[] buffer = new byte[bufferLen];
					///< ʵʱ��¼���صĽ���
					File recFile = new File(dstParentPath + dstFileName + this.threadId + ".txt");
					while ((len = inputS.read(buffer)) != -1) 
					{
						///< ����ʵʱд���ļ�������ʵʱˢ��/Ӳ��/�ײ㻺������
						RandomAccessFile rf = new RandomAccessFile(recFile,
								"rwd");
						///< ******
						raf.write(buffer, 0, len);
						///< ******
						
						///< ��ȡ��ǰ�����ֽ��� , ��֪ͨ�ͻ�����  
						synchronized (ItemAdapter.class) 
						{
							currentSize += len;
							//System.out.println("ssss " + currentSize + " " + size);
							mulDownLis.onProgress(currentSize, currentSize/(float)size);
						}
						
						///< ʵʱ��¼�ļ�д����ֽ���
						total += len;
						rf.write(String.valueOf(total).getBytes());
						rf.close();
					}
					inputS.close();
					raf.close();
				}
			} 
			catch (MalformedURLException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			catch (ProtocolException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			catch (IOException e) 
			{
				// TODO Auto-generated catch block
				// e.printStackTrace();
				try 
				{
					throw new DownThreadException("�߳������쳣");
				}
				catch (DownThreadException e1)
				{
					// TODO Auto-generated catch block
					// e1.printStackTrace();
				}
			}
			finally 
			{
				///< ��֤�����߳̽���֮��ɾ����¼�ļ�
				synchronized (ItemAdapter.class) 
				{
					///< ��������쳣����ʵ�����صĴ�С��Ϊ�ж������Ƿ�ִ����ɵ�����
					if (totalLen == getRecLen(dstParentPath + dstFileName + this.threadId + ".txt"))
					{
						//System.out.println("�߳�" + threadId + " ������ϣ�");
						runningThreadCount--;
						if (runningThreadCount < 1 
							&& size == getToalLen(threadCount)) 
						{
							for (int i = 1; i <= threadCount; ++i)
							{
								File file = new File(dstParentPath + dstFileName + i + ".txt");
								file.delete();
							}
							try
							{
								throw new DownThreadException("DONE");
							} 
							catch (DownThreadException e1) 
							{
								// TODO Auto-generated catch block
								// e1.printStackTrace();
								///< ʲô������
							}
						}
					}
				}
			}
		}
	}

	/**
	 * ��ͨ��������־��¼�ļ�����ȡ�����߳����صĴ�С
	 * 
	 * @param file
	 * @return
	 */
	private long getRecLen(String file) 
	{
		///< �����ܴ�С
		long lastTotalInt = 0;
		///< ��ȡ�ϴ����صĽ��ȣ����¼��㱾�����صĿ�ʼλ��
		File positionFile = new File(file);
		///< ����м�¼�����ȡ�ϴν�����Ϣ
		if (positionFile.exists() && positionFile.length() > 0) 
		{
			try 
			{
				FileInputStream fIn = new FileInputStream(positionFile);
				BufferedReader br = new BufferedReader(new InputStreamReader(
						fIn));
				String lastTotal = br.readLine();
				lastTotalInt = Long.valueOf(lastTotal);
				br.close();
			} 
			catch (NumberFormatException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			catch (FileNotFoundException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			catch (IOException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return lastTotalInt;
	}

	/**
	 * ��ͨ��������־��¼�ļ�����ȡ�����߳��Ѿ����ص��ܺ�
	 * 
	 * @param threadCount
	 * @return
	 */
	private long getToalLen(int threadCount) 
	{
		///< �����ܴ�С
		long total = 0;
		for (int i = 1; i <= threadCount; ++i) 
		{
			///< ��ȡ�ϴ����صĽ��ȣ����¼��㱾�����صĿ�ʼλ��
			File positionFile = new File(this.dstParentPath + this.dstFileName + i + ".txt");
			///< ����м�¼�����ȡ�ϴν�����Ϣ
			if (positionFile.exists() && positionFile.length() > 0) 
			{
				try 
				{
					FileInputStream fIn = new FileInputStream(positionFile);
					BufferedReader br = new BufferedReader(new InputStreamReader(fIn));
					String lastTotal = br.readLine();
					long lastTotalInt = Long.valueOf(lastTotal);
					///< ���غ���ܴ�С�������ϴ�����
					total += lastTotalInt;
					br.close();
				} 
				catch (NumberFormatException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch (FileNotFoundException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
				catch (IOException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return total;
	}

	/**
	 * �Զ����쳣���� �� 1.�������쳣ʱ����δ���ʱ�������쳣�������û����飻
	 * 2.���������ʱ�����Ǵ��������������Ѹ�ף�����ѡ���������أ������������� ��
	 * 
	 * @author hl
	 * 
	 */
	private class DownThreadException extends Exception 
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public DownThreadException(String msg) 
		{
			super(msg);
			switch (msg)
			{
			case "DONE":
				///< ������ϣ���������
				mulDownLis.onSuccess();
				break;
			}
		}
	}
}
