package org.joot.mir2.tools.imageviewer;

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.InflaterInputStream;

/** WZL��ȡ��<br>wzlҲ��Ϊ8λ�Ҷ�ͼ<br>��ɫ������ʹ��zlibѹ���� */
public class WZLReader {
	/** ͼƬ���� */
	private int imageCount;
	/** ��ɫ�� */
	private int[] palette = Pascal.pallete;
	/** ͼƬ������ʼλ�� */
	private Integer[] offsetList;
	/** ͼƬ���ݳ��� */
	private Integer[] lengthList;
	/** ͼƬ�������� */
	private WZLImageInfo[] imageInfos;
	/** �Ƿ������� */
	private boolean loaded;
	/** �ļ���ȡ�� */
	private RandomAccessFile raf_wzl;
	/** �������ʧ�ܣ���ֵ����ʧ��ԭ�� */
	private String loadErrMsg;
	public static String getErrorMessage() {
		return instance.loadErrMsg;
	}
	
	/** ����lru������Ե�map<br>����ʹ�ô�������50��ͼƬ */
	private ConcurrentLinkedHashMap<Integer, BufferedImage> images = new ConcurrentLinkedHashMap.Builder<Integer, BufferedImage>().maximumWeightedCapacity(50).build();
	
	private WZLReader() {}
	private static WZLReader instance = new WZLReader();
	static{}
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		try {
			raf_wzl.close();
		} catch(IOException e) { }		
	}
	/** ��ȡһ��ֵ����ʾ�˴���Դ�ļ��Ƿ������� */
	public static boolean isLoad() {
		return instance.loaded;
	}
	/** ��ȡͼƬ���� */
	public static int getImageCount() {
		return instance.imageCount;
	}

	/** ���� 
	 * @throws IOException */
	public static void load(String filePath, boolean blankRemove) throws IOException {
		reset();
		File f_wzl = new File(filePath);
		if(!f_wzl.exists()) {
			instance.loadErrMsg = "�ļ������ڣ�";
			return;
		}
		if(!f_wzl.canRead()) {
			instance.loadErrMsg = "�ļ����ɶ���";
			return;
		}
		File f_wzx = new File(filePath.substring(0, filePath.length() - 4) + ".wzx");
		if(!f_wzx.exists()) {
			instance.loadErrMsg = "�����ļ������ڣ�";
			return;
		}
		if(!f_wzx.canRead()) {
			instance.loadErrMsg = "�����ļ����ɶ���";
			return;
		}
		instance.raf_wzl = new RandomAccessFile(f_wzl, "r");
		RandomAccessFile raf_wzx = new RandomAccessFile(f_wzx, "r");
		// �������ļ��ж�ȡ����
		raf_wzx.skipBytes(44);
		byte[] bytes_i = new byte[4];
		raf_wzx.read(bytes_i);
		// ͼƬ����(�����հ�)
		int indexCount = Common.readInt(bytes_i, 0, true);
		// ��ȡͼƬ���ݱ��˺ͳ���
		List<Integer> l_offsets = new ArrayList<Integer>();
		List<Integer> lengths = new ArrayList<Integer>();
		int[] a_offsets = new int[indexCount];
		for(int i = 0; i < indexCount; ++i) {
			raf_wzx.read(bytes_i);
			a_offsets[i] = Common.readInt(bytes_i, 0, true);
		}
		for(int i = 0; i < indexCount; ++i) {
			instance.raf_wzl.seek(a_offsets[i] + 12);
			instance.raf_wzl.read(bytes_i);
			int length = Common.readInt(bytes_i, 0, true);
			if(blankRemove && length == 0) {
				// ������Ϊ48��ͼƬΪ�հ�ͼƬ
				continue;
			}
			l_offsets.add(a_offsets[i]);
			lengths.add(length);
		}
		instance.offsetList = l_offsets.toArray(new Integer[0]);
		instance.lengthList = lengths.toArray(new Integer[0]);
		instance.imageCount = instance.offsetList.length;
		// ��ȡimgeinfo
		instance.imageInfos = new WZLImageInfo[instance.imageCount];
		for(int i = 0; i < instance.imageCount; ++i) {
			instance.imageInfos[i] = readImageInfo(instance.offsetList[i]);
		}
		try {
			raf_wzx.close();
		} catch (Exception e) {
		}
	}

	/** ��ȡĳ��ͼƬ������Ϣ */
	public static WZLImageInfo getOneImageInfo(Integer index) {
		return instance.imageInfos[index];
	}
	
	/** ��ȡĳ��ͼƬ���� 
	 * @throws IOException */
	public static BufferedImage getOneImage(Integer index) throws IOException {
		if(instance.images.containsKey(index))
			return instance.images.get(index);
		else {
			BufferedImage bi = loadImage(index);
			instance.images.put(index, bi);
			return bi;
		}
	}
	
	/** ��ȡͼƬ 
	 * @throws IOException */
	private static BufferedImage loadImage(int index) throws IOException {
		WZLImageInfo wii = instance.imageInfos[index];
		if(wii.width == 0 && wii.height == 0) {
			BufferedImage bi = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
			bi.setRGB(0, 0, instance.palette[0]);
			return bi;
		}
		BufferedImage bi = new BufferedImage(wii.width, wii.height, BufferedImage.TYPE_INT_ARGB);
		int offset = instance.offsetList[index];
		int length = instance.lengthList[index];
		byte[] pixels = new byte[length];
		instance.raf_wzl.seek(offset + 16);
		instance.raf_wzl.read(pixels);
		pixels = unzip(pixels);
		int p_index = 0;
		for(int h = wii.height - 1; h >= 0 ; --h)
			for(int w = 0; w < wii.width; ++w) {
				// ��������ֽ�
				if(w == 0)
					p_index += Pascal.skipBytes(8, wii.width);
				bi.setRGB(w, h, instance.palette[pixels[p_index++] & 0xff]);
			}
		return bi;
	}
	
	/** ��zlib��ѹ */
	private static byte[] unzip(byte[] ziped) {
		InflaterInputStream iis = new InflaterInputStream(new ByteArrayInputStream(ziped));
		ByteArrayOutputStream o = new ByteArrayOutputStream(1024);
		try {
			int i = 1024;
			byte[] buf = new byte[i];

			while ((i = iis.read(buf, 0, i)) > 0) {
				o.write(buf, 0, i);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return o.toByteArray();
	}
	
	/** ������ļ��������ж�ȡImageInfo����  */
	private static WZLImageInfo readImageInfo(int position) throws IOException {
		instance.raf_wzl.seek(position);
		WZLImageInfo res = new WZLImageInfo();
		byte[] bytes = new byte[16];
		instance.raf_wzl.readFully(bytes);
		res.setReserve(Common.readInt(bytes, 0, true));
		res.setWidth(Common.readShort(bytes, 4, true));
		res.setHeight(Common.readShort(bytes, 6, true));
		res.setOffsetX(Common.readShort(bytes, 8, true));
		res.setOffsetY(Common.readShort(bytes, 10, true));
		return res;
	}
	
	/** ���� */
	private static void reset() {
		instance.imageCount = 0;
		instance.images.clear();
		instance.imageInfos = null;
		instance.lengthList = null;
		instance.loaded = false;
		instance.loadErrMsg = null;
		instance.offsetList = null;
		if(instance.raf_wzl != null) {
			try{
				instance.raf_wzl.close();
			} catch(Exception e) { }
		}
		instance.raf_wzl = null;
	}

	/** ͼƬ��Ϣ */
	static class WZLImageInfo {
		/** δ֪���� */
		private int reserve;
		/** ͼƬ��� */
		private short width;
		/** ͼƬ�߶� */
		private short height;
		/** ͼƬ����ƫ���� */
		private short offsetX;
		/** ͼƬ����ƫ���� */
		private short offsetY;
		// private int length; //��ǰͼƬ�����ֽڳ��ȣ��˴���ʹ��
		/** �޲ι��캯�� */
		public WZLImageInfo() {}
		/** �������ж�����ʵ�� */
		public WZLImageInfo(WZLImageInfo imageInfo) {
			this.reserve = imageInfo.reserve;
			this.height = imageInfo.height;
			this.offsetX = imageInfo.offsetX;
			this.offsetY = imageInfo.offsetY;
			this.width = imageInfo.width;
		}
		/** ��ȫ�������Ĺ��캯�� */
		public WZLImageInfo(int reserve, short width, short height, short offsetX, short offsetY) {
			this.reserve = reserve;
			this.width = width;
			this.height = height;
			this.offsetX = offsetX;
			this.offsetY = offsetY;
		}
		
		public int getReserve() {
			return reserve;
		}
		public void setReserve(int reserve) {
			this.reserve = reserve;
		}
		/** ��ȡͼƬ��� */
		public short getWidth() {
			return width;
		}
		/** ����ͼƬ�߶� */
		public void setWidth(short width) {
			this.width = width;
		}
		/** ��ȡͼƬ�߶� */
		public short getHeight() {
			return height;
		}
		/** ����ͼƬ�߶� */
		public void setHeight(short height) {
			this.height = height;
		}
		/** ��ȡͼƬ����ƫ���� */
		public short getOffsetX() {
			return offsetX;
		}
		/** ����ͼƬ����ƫ���� */
		public void setOffsetX(short offsetX) {
			this.offsetX = offsetX;
		}
		/** ��ȡͼƬ����ƫ���� */
		public short getOffsetY() {
			return offsetY;
		}
		/** ����ͼƬ����ƫ���� */
		public void setOffsetY(short offsetY) {
			this.offsetY = offsetY;
		}
	}
}