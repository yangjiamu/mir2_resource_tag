package org.joot.mir2.tools.imageviewer;

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.InflaterInputStream;

/** WZL读取器<br>wzl也都为8位灰度图<br>其色彩数据使用zlib压缩过 */
public class WZLReader {
	/** 图片数量 */
	private int imageCount;
	/** 调色板 */
	private int[] palette = Pascal.pallete;
	/** 图片数据起始位置 */
	private Integer[] offsetList;
	/** 图片数据长度 */
	private Integer[] lengthList;
	/** 图片描述对象 */
	private WZLImageInfo[] imageInfos;
	/** 是否加载完成 */
	private boolean loaded;
	/** 文件读取流 */
	private RandomAccessFile raf_wzl;
	/** 如果加载失败，此值包含失败原因 */
	private String loadErrMsg;
	public static String getErrorMessage() {
		return instance.loadErrMsg;
	}
	
	/** 基于lru缓存策略的map<br>缓存使用次数最多的50张图片 */
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
	/** 获取一个值，表示此次资源文件是否加载完成 */
	public static boolean isLoad() {
		return instance.loaded;
	}
	/** 获取图片数量 */
	public static int getImageCount() {
		return instance.imageCount;
	}

	/** 加载 
	 * @throws IOException */
	public static void load(String filePath, boolean blankRemove) throws IOException {
		reset();
		File f_wzl = new File(filePath);
		if(!f_wzl.exists()) {
			instance.loadErrMsg = "文件不存在！";
			return;
		}
		if(!f_wzl.canRead()) {
			instance.loadErrMsg = "文件不可读！";
			return;
		}
		File f_wzx = new File(filePath.substring(0, filePath.length() - 4) + ".wzx");
		if(!f_wzx.exists()) {
			instance.loadErrMsg = "索引文件不存在！";
			return;
		}
		if(!f_wzx.canRead()) {
			instance.loadErrMsg = "索引文件不可读！";
			return;
		}
		instance.raf_wzl = new RandomAccessFile(f_wzl, "r");
		RandomAccessFile raf_wzx = new RandomAccessFile(f_wzx, "r");
		// 从索引文件中读取数据
		raf_wzx.skipBytes(44);
		byte[] bytes_i = new byte[4];
		raf_wzx.read(bytes_i);
		// 图片数量(包含空白)
		int indexCount = Common.readInt(bytes_i, 0, true);
		// 读取图片数据便宜和长度
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
				// 便宜量为48的图片为空白图片
				continue;
			}
			l_offsets.add(a_offsets[i]);
			lengths.add(length);
		}
		instance.offsetList = l_offsets.toArray(new Integer[0]);
		instance.lengthList = lengths.toArray(new Integer[0]);
		instance.imageCount = instance.offsetList.length;
		// 读取imgeinfo
		instance.imageInfos = new WZLImageInfo[instance.imageCount];
		for(int i = 0; i < instance.imageCount; ++i) {
			instance.imageInfos[i] = readImageInfo(instance.offsetList[i]);
		}
		try {
			raf_wzx.close();
		} catch (Exception e) {
		}
	}

	/** 获取某张图片基本信息 */
	public static WZLImageInfo getOneImageInfo(Integer index) {
		return instance.imageInfos[index];
	}
	
	/** 获取某张图片数据 
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
	
	/** 读取图片 
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
				// 跳过填充字节
				if(w == 0)
					p_index += Pascal.skipBytes(8, wii.width);
				bi.setRGB(w, h, instance.palette[pixels[p_index++] & 0xff]);
			}
		return bi;
	}
	
	/** 从zlib解压 */
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
	
	/** 从随机文件访问流中读取ImageInfo对象  */
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
	
	/** 重置 */
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

	/** 图片信息 */
	static class WZLImageInfo {
		/** 未知数据 */
		private int reserve;
		/** 图片宽度 */
		private short width;
		/** 图片高度 */
		private short height;
		/** 图片横向偏移量 */
		private short offsetX;
		/** 图片纵向偏移量 */
		private short offsetY;
		// private int length; //当前图片数据字节长度，此处不使用
		/** 无参构造函数 */
		public WZLImageInfo() {}
		/** 基于已有对象构造实例 */
		public WZLImageInfo(WZLImageInfo imageInfo) {
			this.reserve = imageInfo.reserve;
			this.height = imageInfo.height;
			this.offsetX = imageInfo.offsetX;
			this.offsetY = imageInfo.offsetY;
			this.width = imageInfo.width;
		}
		/** 带全部参数的构造函数 */
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