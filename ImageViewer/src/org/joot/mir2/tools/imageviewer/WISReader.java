package org.joot.mir2.tools.imageviewer;

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** WIS��ȡ��<br>wis�ļ�ͼƬ��Ϊ8λ�Ҷ�ͼ��ͼƬ��ƫ���� */
public class WISReader {
	/** ͼƬ���� */
	private int imageCount;
	/** ��ɫ�� */
	private int[] palette = Pascal.pallete;
	/** ͼƬ������ʼλ�� */
	private Integer[] offsetList;
	/** ͼƬ���ݳ��� */
	private Integer[] lengthList;
	/** ͼƬ�������� */
	private WISImageInfo[] imageInfos;
	/** �Ƿ������� */
	private boolean loaded;
	/** �ļ���ȡ�� */
	private RandomAccessFile raf_wis;
	/** �������ʧ�ܣ���ֵ����ʧ��ԭ�� */
	private String loadErrMsg;
	public static String getErrorMessage() {
		return instance.loadErrMsg;
	}
	
	/** ����lru������Ե�map<br>����ʹ�ô�������50��ͼƬ */
	private ConcurrentLinkedHashMap<Integer, BufferedImage> images = new ConcurrentLinkedHashMap.Builder<Integer, BufferedImage>().maximumWeightedCapacity(50).build();
	
	private WISReader() {}
	private static WISReader instance = new WISReader();
	static{}
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		try {
			raf_wis.close();
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
	
	/** �����ļ� 
	 * @throws IOException */
	public static void load(String filePath, boolean blankRemove) throws IOException {
		reset();
		File f_wis = new File(filePath);
		if(!f_wis.exists()) {
			instance.loadErrMsg = "�ļ������ڣ�";
			return;
		}
		if(!f_wis.canRead()) {
			instance.loadErrMsg = "�ļ����ɶ���";
			return;
		}
		instance.raf_wis = new RandomAccessFile(f_wis, "r");
		// ���ļ�ĩβ��ʼ��ȡͼƬ����������Ϣ
		// һ��������Ϣ����12���ֽ�(3��intֵ)������ΪͼƬ������ʼλ��(������ļ�)��ͼƬ���ݴ�С(����������Ϣ)������
		// ʹ������List����offsetList��lengthList
		List<Integer> offsets = new ArrayList<Integer>();
		List<Integer> lengths = new ArrayList<Integer>();
		int readPosition = (int) (instance.raf_wis.length() - 12);
		int currentOffset = 0;
		int currentLength = 0;
		byte[] bytes = new byte[4];
		do{
			instance.raf_wis.seek(readPosition);
			readPosition -= 12;
			
			instance.raf_wis.read(bytes);
			currentOffset = Common.readInt(bytes, 0, true);
			instance.raf_wis.read(bytes);
			currentLength = Common.readInt(bytes, 0, true);
			if(blankRemove && currentLength < 14) {
				// �հ�ͼƬ���ݳ��ȵ���13
				continue;
			}
			offsets.add(currentOffset);
			lengths.add(currentLength);
		}while(currentOffset > 512);
		Collections.reverse(offsets);
		Collections.reverse(lengths);
		instance.offsetList = offsets.toArray(new Integer[0]);
		instance.lengthList = lengths.toArray(new Integer[0]);
		instance.imageCount = instance.offsetList.length;
		// ��ȡͼƬ������Ϣ
		instance.imageInfos = new WISImageInfo[instance.imageCount];
		for(int i = 0; i < instance.imageCount; ++i) {
			instance.imageInfos[i] = readImageInfo(instance.offsetList[i] + 4);
		}
		instance.loaded = true;
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
		if(instance.raf_wis != null) {
			try{
				instance.raf_wis.close();
			} catch(Exception e) { }
		}
		instance.raf_wis = null;
	}
	
	/** ��ȡ����ͼƬ������Ϣ */
	public static WISImageInfo getOneImageInfo(int index) {
		return instance.imageInfos[index];
	}
	
	/** ��ȡ����ͼƬ 
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
	
	public static int[] getPalette() {
		return instance.palette;
	}
	public static byte[] getImagePixel(int index) throws IOException {
		WISImageInfo wii = instance.imageInfos[index];
		int offset = instance.offsetList[index];
		int length = instance.lengthList[index];
		if(length < 14) {
			// ����ǿհ�ͼƬ
			return new byte[0];
		}
		// �Ƿ�ѹ��(RLE)
		instance.raf_wis.seek(offset);
		byte encry = instance.raf_wis.readByte();
		instance.raf_wis.skipBytes(11);
		byte[] imageBytes = new byte[wii.width * wii.height];
		if(encry == 1) {
			// ѹ����
			byte[] packed = new byte[length - 12];
			instance.raf_wis.read(packed);
			imageBytes = unpack(packed, imageBytes.length);
		} else {
			// ûѹ��
			instance.raf_wis.read(imageBytes);
		}
		return imageBytes;
	}
	
	/** ��ȡͼƬ 
	 * @throws IOException */
	private static BufferedImage loadImage(int index) throws IOException {
		WISImageInfo wii = instance.imageInfos[index];
		BufferedImage bi = new BufferedImage(wii.width, wii.height, BufferedImage.TYPE_INT_ARGB);
		int offset = instance.offsetList[index];
		int length = instance.lengthList[index];
		if(length < 14) {
			// ����ǿհ�ͼƬ
			bi.setRGB(0, 0, instance.palette[0]);
			return bi;
		}
		// �Ƿ�ѹ��(RLE)
		instance.raf_wis.seek(offset);
		byte encry = instance.raf_wis.readByte();
		instance.raf_wis.skipBytes(11);
		byte[] imageBytes = new byte[wii.width * wii.height];
		if(encry == 1) {
			// ѹ����
			byte[] packed = new byte[length - 12];
			instance.raf_wis.read(packed);
			imageBytes = unpack(packed, imageBytes.length);
		} else {
			// ûѹ��
			instance.raf_wis.read(imageBytes);
		}
		int index1 = 0;
		for(int h = 0; h < wii.height ; ++h)
			for(int w = 0; w < wii.width; ++w) {
				bi.setRGB(w, h, instance.palette[imageBytes[index1++] & 0xff]);
			}
		return bi;
	}
	
	/**
	 * ��ѹ����
	 * @param packed ѹ��������
	 * @param unpackLength ��ѹ�����ݴ�С
	 */
	private static byte[] unpack(byte[] packed, int unpackLength) {
		int srcLength = packed.length; // ѹ�������ݴ�С
		byte[] result = new byte[unpackLength]; // ��ѹ������
		int srcIndex = 0; // ��ǰ��ѹ���ֽ�����
		int dstIndex = 0; // ��ѹ���̻�ԭ�����ֽ�����
		// ��ѹ����Ϊ���ֽڽ���(�ֽ�ӦתΪ1-256)
		// �����ǰ�ֽڷ�0���ʾ������һ���ֽ�������䵱ǰ�ֽڸ��ֽ�λ��
		// �����ǰ�ֽ�Ϊ0����һ���ֽڲ�Ϊ0���ʾ�����¸��ֽڿ�ʼ����һ���ֽڳ��ȶ�û��ѹ����ֱ�Ӹ��Ƶ�Ŀ������
		// �����ǰ�ֽ�Ϊ0����һ���ֽ�ҲΪ0������������ݣ����账��
		// XX YY ��ʾ��YY���XX���ֽ�
		// 00 XX YY ZZ ... ��ʾ��YY��ʼXX���ֽ���δ��ѹ���ģ�ֱ�Ӹ��Ƴ�������
		while(srcLength > 0 && unpackLength > 0) {
			int length = packed[srcIndex++] & 0xff; // ȡ����һ����־λ
			int value = packed[srcIndex++] & 0xff; // ȡ���ڶ�����־λ
			srcLength -= 2;
			/*if(value == 0 && length == 0) {
				// ������
				continue;
			} else */if(length != 0) {
				// ��Ҫ��ѹ��
				unpackLength -= length;
				for(int i = 0; i < length; ++i) {
					result[dstIndex++] = (byte) value;
				}
			} else if(value != 0) {
				srcLength -= value;
				unpackLength -= value;
				System.arraycopy(packed, srcIndex, result, dstIndex, value);
				dstIndex += value;
				srcIndex += value;
			}
		}
		return result;
	}
	
	/** ������ļ��������ж�ȡImageInfo����  */
	private static WISImageInfo readImageInfo(int position) throws IOException {
		instance.raf_wis.seek(position);
		WISImageInfo res = new WISImageInfo();
		byte[] bytes = new byte[8];
		instance.raf_wis.readFully(bytes);
		res.setWidth(Common.readShort(bytes, 0, true));
		res.setHeight(Common.readShort(bytes, 2, true));
		res.setOffsetX(Common.readShort(bytes, 4, true));
		res.setOffsetY(Common.readShort(bytes, 6, true));
		return res;
	}
	
	/** ͼƬ��Ϣ */
	public static class WISImageInfo {
		/** ͼƬ��� */
		private short width;
		/** ͼƬ�߶� */
		private short height;
		/** ͼƬ����ƫ���� */
		private short offsetX;
		/** ͼƬ����ƫ���� */
		private short offsetY;
		
		/** �޲ι��캯�� */
		public WISImageInfo() {}
		/** �������ж�����ʵ�� */
		public WISImageInfo(WISImageInfo imageInfo) {
			this.height = imageInfo.height;
			this.offsetX = imageInfo.offsetX;
			this.offsetY = imageInfo.offsetY;
			this.width = imageInfo.width;
		}
		/** ��ȫ�������Ĺ��캯�� */
		public WISImageInfo(short width, short height, short offsetX, short offsetY) {
			this.width = width;
			this.height = height;
			this.offsetX = offsetX;
			this.offsetY = offsetY;
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
