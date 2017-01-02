package org.joot.mir2.tools.imageviewer;

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** WIS读取器<br>wis文件图片都为8位灰度图，图片有偏移量 */
public class WISReader {
	/** 图片数量 */
	private int imageCount;
	/** 调色板 */
	private int[] palette = Pascal.pallete;
	/** 图片数据起始位置 */
	private Integer[] offsetList;
	/** 图片数据长度 */
	private Integer[] lengthList;
	/** 图片描述对象 */
	private WISImageInfo[] imageInfos;
	/** 是否加载完成 */
	private boolean loaded;
	/** 文件读取流 */
	private RandomAccessFile raf_wis;
	/** 如果加载失败，此值包含失败原因 */
	private String loadErrMsg;
	public static String getErrorMessage() {
		return instance.loadErrMsg;
	}
	
	/** 基于lru缓存策略的map<br>缓存使用次数最多的50张图片 */
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
	/** 获取一个值，表示此次资源文件是否加载完成 */
	public static boolean isLoad() {
		return instance.loaded;
	}
	/** 获取图片数量 */
	public static int getImageCount() {
		return instance.imageCount;
	}
	
	/** 加载文件 
	 * @throws IOException */
	public static void load(String filePath, boolean blankRemove) throws IOException {
		reset();
		File f_wis = new File(filePath);
		if(!f_wis.exists()) {
			instance.loadErrMsg = "文件不存在！";
			return;
		}
		if(!f_wis.canRead()) {
			instance.loadErrMsg = "文件不可读！";
			return;
		}
		instance.raf_wis = new RandomAccessFile(f_wis, "r");
		// 从文件末尾开始读取图片数据描述信息
		// 一组描述信息包括12个字节(3个int值)，依次为图片数据起始位置(相对于文件)、图片数据大小(包括基本信息)、保留
		// 使用两个List保存offsetList和lengthList
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
				// 空白图片数据长度等于13
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
		// 读取图片基本信息
		instance.imageInfos = new WISImageInfo[instance.imageCount];
		for(int i = 0; i < instance.imageCount; ++i) {
			instance.imageInfos[i] = readImageInfo(instance.offsetList[i] + 4);
		}
		instance.loaded = true;
	}

	/** 重置 */
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
	
	/** 获取单张图片基本信息 */
	public static WISImageInfo getOneImageInfo(int index) {
		return instance.imageInfos[index];
	}
	
	/** 获取单张图片 
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
			// 如果是空白图片
			return new byte[0];
		}
		// 是否压缩(RLE)
		instance.raf_wis.seek(offset);
		byte encry = instance.raf_wis.readByte();
		instance.raf_wis.skipBytes(11);
		byte[] imageBytes = new byte[wii.width * wii.height];
		if(encry == 1) {
			// 压缩了
			byte[] packed = new byte[length - 12];
			instance.raf_wis.read(packed);
			imageBytes = unpack(packed, imageBytes.length);
		} else {
			// 没压缩
			instance.raf_wis.read(imageBytes);
		}
		return imageBytes;
	}
	
	/** 读取图片 
	 * @throws IOException */
	private static BufferedImage loadImage(int index) throws IOException {
		WISImageInfo wii = instance.imageInfos[index];
		BufferedImage bi = new BufferedImage(wii.width, wii.height, BufferedImage.TYPE_INT_ARGB);
		int offset = instance.offsetList[index];
		int length = instance.lengthList[index];
		if(length < 14) {
			// 如果是空白图片
			bi.setRGB(0, 0, instance.palette[0]);
			return bi;
		}
		// 是否压缩(RLE)
		instance.raf_wis.seek(offset);
		byte encry = instance.raf_wis.readByte();
		instance.raf_wis.skipBytes(11);
		byte[] imageBytes = new byte[wii.width * wii.height];
		if(encry == 1) {
			// 压缩了
			byte[] packed = new byte[length - 12];
			instance.raf_wis.read(packed);
			imageBytes = unpack(packed, imageBytes.length);
		} else {
			// 没压缩
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
	 * 解压数据
	 * @param packed 压缩的数据
	 * @param unpackLength 解压后数据大小
	 */
	private static byte[] unpack(byte[] packed, int unpackLength) {
		int srcLength = packed.length; // 压缩后数据大小
		byte[] result = new byte[unpackLength]; // 解压后数据
		int srcIndex = 0; // 当前解压的字节索引
		int dstIndex = 0; // 解压过程还原出的字节索引
		// 解压过程为逐字节进行(字节应转为1-256)
		// 如果当前字节非0则表示将以下一个字节数据填充当前字节个字节位置
		// 如果当前字节为0且下一个字节不为0则表示从下下个字节开始到下一个字节长度都没有压缩，直接复制到目标数组
		// 如果当前字节为0且下一个字节也为0则可能是脏数据，不予处理
		// XX YY 表示以YY填充XX个字节
		// 00 XX YY ZZ ... 表示从YY开始XX个字节是未被压缩的，直接复制出来即可
		while(srcLength > 0 && unpackLength > 0) {
			int length = packed[srcIndex++] & 0xff; // 取出第一个标志位
			int value = packed[srcIndex++] & 0xff; // 取出第二个标志位
			srcLength -= 2;
			/*if(value == 0 && length == 0) {
				// 脏数据
				continue;
			} else */if(length != 0) {
				// 需要解压缩
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
	
	/** 从随机文件访问流中读取ImageInfo对象  */
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
	
	/** 图片信息 */
	public static class WISImageInfo {
		/** 图片宽度 */
		private short width;
		/** 图片高度 */
		private short height;
		/** 图片横向偏移量 */
		private short offsetX;
		/** 图片纵向偏移量 */
		private short offsetY;
		
		/** 无参构造函数 */
		public WISImageInfo() {}
		/** 基于已有对象构造实例 */
		public WISImageInfo(WISImageInfo imageInfo) {
			this.height = imageInfo.height;
			this.offsetX = imageInfo.offsetX;
			this.offsetY = imageInfo.offsetY;
			this.width = imageInfo.width;
		}
		/** 带全部参数的构造函数 */
		public WISImageInfo(short width, short height, short offsetX, short offsetY) {
			this.width = width;
			this.height = height;
			this.offsetX = offsetX;
			this.offsetY = offsetY;
		}
		
		/** 获取图片宽度 */
		public short getWidth() {
			return width;
		}
		/** 设置图片高度 */
		public void setWidth(short width) {
			this.width = width;
		}
		/** 获取图片高度 */
		public short getHeight() {
			return height;
		}
		/** 设置图片高度 */
		public void setHeight(short height) {
			this.height = height;
		}
		/** 获取图片横线偏移量 */
		public short getOffsetX() {
			return offsetX;
		}
		/** 设置图片横向偏移量 */
		public void setOffsetX(short offsetX) {
			this.offsetX = offsetX;
		}
		/** 获取图片纵向偏移量 */
		public short getOffsetY() {
			return offsetY;
		}
		/** 设置图片纵向偏移量 */
		public void setOffsetY(short offsetY) {
			this.offsetY = offsetY;
		}
	}
}
