package org.joot.mir2.tools.imageviewer;

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

/** WIL读取器 */
public class WILReader {
	/** 图片数量 */
	private int imageCount;
	/** 色深度 */
	private int colorCount;
	/** 版本标志 */
	private int verFlag;
	/** 调色板 */
	private int[] palette = Pascal.pallete;
	/** 图片数据起始位置 */
	private Integer[] offsetList;
	/** 图片数据大小 */
	private Integer[] lengthList;
	/** 图片描述对象 */
	private WILImageInfo[] imageInfos;
	/** 是否加载完成 */
	private boolean loaded;
	/** 文件读取流 */
	private RandomAccessFile raf_wil;
	/** 如果加载失败，此值包含失败原因 */
	private String loadErrMsg;
	public static String getErrorMessage() {
		return instance.loadErrMsg;
	}
	
	/** 基于lru缓存策略的map<br>缓存使用次数最多的50张图片 */
	private ConcurrentLinkedHashMap<Integer, BufferedImage> images = new ConcurrentLinkedHashMap.Builder<Integer, BufferedImage>().maximumWeightedCapacity(50).build();
	
	private WILReader() {}
	private static WILReader instance = new WILReader();
	static{}
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		try {
			raf_wil.close();
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
	
	/** 加载<br>此处对去除空白图片的处理略显浮躁 
	 * @throws IOException */
	public static void load(String filePath, boolean blankRemove) throws IOException {
		reset();
		File f_wil = new File(filePath);
		System.out.println(filePath);
		if(!f_wil.exists()) {
			instance.loadErrMsg = "文件不存在！";
			return;
		}
		if(!f_wil.canRead()) {
			instance.loadErrMsg = "文件不可读！";
			return;
		}
		File f_wix = new File(filePath.substring(0, filePath.length() - 4) + ".wix");
		if(!f_wix.exists()) {
			instance.loadErrMsg = "索引文件不存在！";
			return;
		}
		if(!f_wix.canRead()) {
			instance.loadErrMsg = "索引文件不可读！";
			return;
		}
		
		instance.raf_wil = new RandomAccessFile(f_wil, "rw");
		//instance.raf_wil.skipBytes(44); // 跳过标题
		//instance.raf_wil.skipBytes(4); // 跳过数量
		instance.raf_wil.skipBytes(48);
		byte[] bytesInt = new byte[4];
		instance.raf_wil.read(bytesInt);
		instance.colorCount = Pascal.colorCountToBitCount(Common.readInt(bytesInt, 0, true)); // 色深度
		if(instance.colorCount < 16) {
			// 8位灰度图可能版本标识不为0，此时操作不一样
			instance.raf_wil.skipBytes(4); // 调色板使用默认
			instance.raf_wil.read(bytesInt);
			instance.verFlag = Common.readInt(bytesInt, 0, true); // 版本标识
		}
		// 读取wix
		RandomAccessFile raf_wix = new RandomAccessFile(f_wix, "rw");
		raf_wix.skipBytes(44); // 跳过标题
		raf_wix.read(bytesInt);
		int indexCount = Common.readInt(bytesInt, 0, true);
		if(instance.verFlag != 0) {
			raf_wix.skipBytes(4); // 版本标识不对需要多跳过标识的4字节
		}
		List<Integer> l_offsets = new ArrayList<Integer>();
		List<Integer> lengths = new ArrayList<Integer>();
		int[] a_offsets = new int[indexCount];
		for(int i = 0; i < indexCount; ++i) {
			raf_wix.read(bytesInt);
			a_offsets[i] = Common.readInt(bytesInt, 0, true);
		}
		for(int i = 0; i < indexCount; ++i) {
			int length = (int) (i == indexCount - 1?(instance.raf_wil.length() - a_offsets[i]):(a_offsets[i + 1] - a_offsets[i]));
			if(blankRemove && length < 13) {
				// 空白图片
				continue;
			}
			l_offsets.add(a_offsets[i]);
			lengths.add(length);
		}
		instance.offsetList = l_offsets.toArray(new Integer[0]);
		instance.lengthList = lengths.toArray(new Integer[0]);
		instance.imageCount = instance.offsetList.length;
		// 读取imgeinfo
		instance.imageInfos = new WILImageInfo[instance.imageCount];
		for(int i = 0; i < instance.imageCount; ++i) {
			instance.imageInfos[i] = readImageInfo(instance.offsetList[i]);
		}
		try {
			raf_wix.close();
		} catch (Exception e) {
		}
	}

	/** 获取某张图片基本信息 */
	public static WILImageInfo getOneImageInfo(Integer index) {
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
	
	/** 重置 */
	public static void reset() {
		instance.imageCount = 0;
		instance.colorCount = 0;
		instance.offsetList = null;
		instance.lengthList = null;
		//instance.palette = null;
		instance.verFlag = 0;
		instance.loaded = false;
		instance.loadErrMsg = null;
		instance.imageInfos = null;
		if(instance.raf_wil != null) {
			try{
				instance.raf_wil.close();
			} catch(Exception e) { }
		}
		instance.raf_wil = null;
		instance.images.clear();
	}
	
	public static byte[] getImagePixel(int index) throws IOException {
		int offset = instance.offsetList[index];
		int length = instance.lengthList[index];
		byte[] pixels = new byte[length];
		instance.raf_wil.seek(offset + 8);
		instance.raf_wil.read(pixels);
		return pixels;
	}
	public static void setImage(int index, BufferedImage image) throws IOException {
		WILImageInfo wii = instance.imageInfos[index];
		int offset = instance.offsetList[index];
		int length = instance.lengthList[index];
		byte[] pixels = new byte[length];
		instance.raf_wil.seek(offset + 8);
		instance.raf_wil.read(pixels);
		if(instance.colorCount == 8) {
			System.out.println("index:" + index + " colorCount:" + 8);
			System.out.println(wii.height);
			System.out.println(wii.width);
			int p_index = 0;
			for(int h = wii.height - 1; h >= 0 ; --h)
				for(int w = 0; w < wii.width; ++w) {
					// 跳过填充字节
					if(w == 0)
						p_index += Pascal.skipBytes(8, wii.width);
					//bi.setRGB(w, h, instance.palette[pixels[p_index++] & 0xff]);
					/*for (int i =0; i < instance.palette.length; ++i) {
						if((instance.palette[i]&0xff) == (byte)image.getRGB(w, h)){
							pixels[p_index++] = (byte)instance.palette[i];
							System.out.println(i);
							break;
						}
					}*/
					pixels[p_index++] = (byte)ColorUtil.rgbConvertTo2Byte(image.getRGB(w, h));
				}
		}
		instance.raf_wil.seek(offset + 8);
		instance.raf_wil.write(pixels);
		instance.raf_wil.close();
	}
	/** 从文件中读取图片 */
	private static BufferedImage loadImage(int index) throws IOException {
		WILImageInfo wii = instance.imageInfos[index];
		//BufferedImage bi = new BufferedImage(wii.width, wii.height, BufferedImage.TYPE_INT_ARGB);
		BufferedImage bi = new BufferedImage(wii.width, wii.height, BufferedImage.TYPE_BYTE_INDEXED);

		int offset = instance.offsetList[index];
		int length = instance.lengthList[index];
		byte[] pixels = new byte[length];
		instance.raf_wil.seek(offset + 8);
		instance.raf_wil.read(pixels);
		if(length < 13) {
			// 如果是空白图片
			bi.setRGB(0, 0, instance.palette[0]);
			return bi;
		}
		if(instance.colorCount == 8) {
			//System.out.println("index:" + index + " colorCount:" + 8);
			int p_index = 0;
			for(int h = wii.height - 1; h >= 0 ; --h)
				for(int w = 0; w < wii.width; ++w) {
					// 跳过填充字节
					if(w == 0)
						p_index += Pascal.skipBytes(8, wii.width);
					bi.setRGB(w, h, instance.palette[pixels[p_index++] & 0xff]);
				}
		} else if(instance.colorCount == 16) {
			int p_index = 0;
			for(int h = wii.height - 1; h >= 0; --h)
				for(int w = 0; w < wii.width; ++w, p_index += 2) {
					// 跳过填充字节
					if(w == 0)
						p_index += Pascal.skipBytes(16, wii.width);
					// 获取短整型的色彩数据(565压缩过)
					short data = Common.readShort(pixels, p_index, true);
					if(data == 0/* || data == 0x841*/)
						bi.setRGB(w, h, 0);
					else {
						byte br = (byte) ((data & 0xF800) >> 8);//byte br = (byte) ((data & 0b1111_1000_0000_0000) >> 8);// 由于是与16位做与操作，所以多出了后面8位
						byte bg = (byte) ((data & 0x7E0) >> 3);//byte bg = (byte) ((data & 0b0000_0111_1110_0000) >> 3);// 多出了3位，在强转时前8位会自动丢失
						byte bb = (byte) ((data & 0x1F) << 3);//byte bb = (byte) ((data & 0b0000_0000_0001_1111) << 3);// 少了3位
						bi.setRGB(w, h, Common.readInt(new byte[]{(byte) 255, br, bg, bb}, 0, false)); // 此时需要反序
					}
				}
		}
		return bi;
	}

	/** tag image in index */
	public static void changeImage(int index, ColorMappingEnum color, int rx, int ry, boolean isHair) throws IOException {
		WILImageInfo wii = instance.imageInfos[index];
		BufferedImage bi = new BufferedImage(wii.width, wii.height, BufferedImage.TYPE_BYTE_INDEXED);

		int offset = instance.offsetList[index];
		int length = instance.lengthList[index];
		byte[] pixels = new byte[length];
		instance.raf_wil.seek(offset + 8);
		instance.raf_wil.read(pixels);
		if(length < 13) {
			// 如果是空白图片
		}
		if(instance.colorCount == 8) {
			//System.out.println("index:" + index + " colorCount:" + 8);
			//int startX = wii.width/4;
			int startX = 0;
			int startY = 0;
			if(isHair){
				startX = 0;
				startY = 0;
			}
			else {
				startX = 6;
				startY = wii.height-ry-1;
			}
			int rightX = startX + rx;
			int rightY = startY + ry;
			int p_index = 0;
			for(int h = wii.height - 1; h >= 0 ; --h)
				for(int w = 0; w < wii.width; ++w) {
					// 跳过填充字节
					if(w == 0)
						p_index += Pascal.skipBytes(8, wii.width);
					if(w>= startX && w<rightX && h>= startY && h<rightY) {
						pixels[p_index] = color.getRgb323();
					}
					++p_index;
				}
			instance.raf_wil.seek(offset+8);
			instance.raf_wil.write(pixels);
		} else if(instance.colorCount == 16) {
			int p_index = 0;
			for(int h = wii.height - 1; h >= 0; --h)
				for(int w = 0; w < wii.width; ++w, p_index += 2) {
					// 跳过填充字节
					if(w == 0)
						p_index += Pascal.skipBytes(16, wii.width);
					// 获取短整型的色彩数据(565压缩过)
					short data = Common.readShort(pixels, p_index, true);
					if(data == 0/* || data == 0x841*/)
						bi.setRGB(w, h, 0);
					else {
						byte br = (byte) ((data & 0xF800) >> 8);//byte br = (byte) ((data & 0b1111_1000_0000_0000) >> 8);// 由于是与16位做与操作，所以多出了后面8位
						byte bg = (byte) ((data & 0x7E0) >> 3);//byte bg = (byte) ((data & 0b0000_0111_1110_0000) >> 3);// 多出了3位，在强转时前8位会自动丢失
						byte bb = (byte) ((data & 0x1F) << 3);//byte bb = (byte) ((data & 0b0000_0000_0001_1111) << 3);// 少了3位
						bi.setRGB(w, h, Common.readInt(new byte[]{(byte) 255, br, bg, bb}, 0, false)); // 此时需要反序
					}
				}
		}
	}

	public static void changeImageUnique(int index, ColorMappingEnum color, int rx, int ry, int startX, int startY) throws IOException {
		WILImageInfo wii = instance.imageInfos[index];
		BufferedImage bi = new BufferedImage(wii.width, wii.height, BufferedImage.TYPE_BYTE_INDEXED);

		int offset = instance.offsetList[index];
		int length = instance.lengthList[index];
		byte[] pixels = new byte[length];
		instance.raf_wil.seek(offset + 8);
		instance.raf_wil.read(pixels);
		if(length < 13) {
			// 如果是空白图片
		}
		if(instance.colorCount == 8) {
			//System.out.println("index:" + index + " colorCount:" + 8);
			//int startX = wii.width/4;
			int rightX = startX + rx;
			int bottomY = startY + ry;
			if(rightX+5 <= wii.getWidth()) {
				startX += 5;
				rightX += 5;
			}
			if(bottomY+5 <= wii.getHeight()){
				startY += 5;
				bottomY += 5;
			}
			int p_index = 0;
			if(color.equals(ColorMappingEnum.GRAY_YELLOW)){
				System.out.println(color.getRgb323());
			}
			for(int h = wii.height - 1; h >= 0 ; --h)
				for(int w = 0; w < wii.width; ++w) {
					// 跳过填充字节
					if(w == 0)
						p_index += Pascal.skipBytes(8, wii.width);
					if(w>= startX && w<rightX && h>= startY && h< bottomY) {
						pixels[p_index] = color.getRgb323();
					}
					++p_index;
				}
			instance.raf_wil.seek(offset+8);
			instance.raf_wil.write(pixels);
		} else if(instance.colorCount == 16) {
			int p_index = 0;
			for(int h = wii.height - 1; h >= 0; --h)
				for(int w = 0; w < wii.width; ++w, p_index += 2) {
					// 跳过填充字节
					if(w == 0)
						p_index += Pascal.skipBytes(16, wii.width);
					// 获取短整型的色彩数据(565压缩过)
					short data = Common.readShort(pixels, p_index, true);
					if(data == 0/* || data == 0x841*/)
						bi.setRGB(w, h, 0);
					else {
						byte br = (byte) ((data & 0xF800) >> 8);//byte br = (byte) ((data & 0b1111_1000_0000_0000) >> 8);// 由于是与16位做与操作，所以多出了后面8位
						byte bg = (byte) ((data & 0x7E0) >> 3);//byte bg = (byte) ((data & 0b0000_0111_1110_0000) >> 3);// 多出了3位，在强转时前8位会自动丢失
						byte bb = (byte) ((data & 0x1F) << 3);//byte bb = (byte) ((data & 0b0000_0000_0001_1111) << 3);// 少了3位
						bi.setRGB(w, h, Common.readInt(new byte[]{(byte) 255, br, bg, bb}, 0, false)); // 此时需要反序
					}
				}
		}
	}

	public static void changeImageForColorDetect(int index, ColorMappingEnum color, int rx, int ry, int startX, int startY) throws IOException {
		WILImageInfo wii = instance.imageInfos[index];
		BufferedImage bi = new BufferedImage(wii.width, wii.height, BufferedImage.TYPE_BYTE_INDEXED);

		int offset = instance.offsetList[index];
		int length = instance.lengthList[index];
		byte[] pixels = new byte[length];
		instance.raf_wil.seek(offset + 8);
		instance.raf_wil.read(pixels);
		if(length < 13) {
			// 如果是空白图片
		}
		if(instance.colorCount == 8) {
			//System.out.println("index:" + index + " colorCount:" + 8);
			//int startX = wii.width/4;
			startX += 4;
			int rightX = startX + rx;
			int rightY = startY + ry;

			int p_index = 0;
			int i = 0;
			for(int h = wii.height - 1; h >= 0 ; --h)
				for(int w = 0; w < wii.width; ++w) {
					// 跳过填充字节
					if(w == 0)
						p_index += Pascal.skipBytes(8, wii.width);
					if(w>= startX && w<rightX && h>= startY && h<rightY) {
						pixels[p_index] = (byte)(h*16 + w-4);
					}
					++p_index;
					++i;
				}
			instance.raf_wil.seek(offset+8);
			instance.raf_wil.write(pixels);
		} else if(instance.colorCount == 16) {
			int p_index = 0;
			for(int h = wii.height - 1; h >= 0; --h)
				for(int w = 0; w < wii.width; ++w, p_index += 2) {
					// 跳过填充字节
					if(w == 0)
						p_index += Pascal.skipBytes(16, wii.width);
					// 获取短整型的色彩数据(565压缩过)
					short data = Common.readShort(pixels, p_index, true);
					if(data == 0/* || data == 0x841*/)
						bi.setRGB(w, h, 0);
					else {
						byte br = (byte) ((data & 0xF800) >> 8);//byte br = (byte) ((data & 0b1111_1000_0000_0000) >> 8);// 由于是与16位做与操作，所以多出了后面8位
						byte bg = (byte) ((data & 0x7E0) >> 3);//byte bg = (byte) ((data & 0b0000_0111_1110_0000) >> 3);// 多出了3位，在强转时前8位会自动丢失
						byte bb = (byte) ((data & 0x1F) << 3);//byte bb = (byte) ((data & 0b0000_0000_0001_1111) << 3);// 少了3位
						bi.setRGB(w, h, Common.readInt(new byte[]{(byte) 255, br, bg, bb}, 0, false)); // 此时需要反序
					}
				}
		}
	}

	/** tag image in index */
	public static void tagByDigit(int index, int digit, int rx, int ry) throws IOException {
		byte rgb565Target = (byte)digit;
		WILImageInfo wii = instance.imageInfos[index];
		BufferedImage bi = new BufferedImage(wii.width, wii.height, BufferedImage.TYPE_BYTE_INDEXED);

		int offset = instance.offsetList[index];
		int length = instance.lengthList[index];
		byte[] pixels = new byte[length];
		instance.raf_wil.seek(offset + 8);
		instance.raf_wil.read(pixels);
		if(length < 13) {
			// 如果是空白图片
		}
		if(instance.colorCount == 8) {
			//System.out.println("index:" + index + " colorCount:" + 8);
			//int startX = wii.width/4;
			int startX = 0;
			int startY = 0;

			int rightX = startX + rx;
			int rightY = startY + ry;
			int p_index = 0;
			for(int h = wii.height - 1; h >= 0 ; --h)
				for(int w = 0; w < wii.width; ++w) {
					// 跳过填充字节
					if(w == 0)
						p_index += Pascal.skipBytes(8, wii.width);
					if(w>= startX && w<=rightX && h>= startY && h<=rightY) {//0000 0000
						//System.out.printf("0x%02x\n", pixels[p_index]);
						//pixels[p_index] = 0x1f;//0000 0000 0001 1111	//red
						//pixels[p_index] = (byte)0x7e0;	//0000 0111	1110 0000	//green
						//pixels[p_index] = (byte) (0xfc);        //1111 1100 0000 0000	//??not blue
						pixels[p_index] = rgb565Target;
						//0xfc blue	| 0xfb yellow | 0xfa green | fd 紫色 | fe 蓝绿色？|
					}
					++p_index;
				}
			instance.raf_wil.seek(offset+8);
			instance.raf_wil.write(pixels);
		} else if(instance.colorCount == 16) {
			int p_index = 0;
			for(int h = wii.height - 1; h >= 0; --h)
				for(int w = 0; w < wii.width; ++w, p_index += 2) {
					// 跳过填充字节
					if(w == 0)
						p_index += Pascal.skipBytes(16, wii.width);
					// 获取短整型的色彩数据(565压缩过)
					short data = Common.readShort(pixels, p_index, true);
					if(data == 0/* || data == 0x841*/)
						bi.setRGB(w, h, 0);
					else {
						byte br = (byte) ((data & 0xF800) >> 8);//byte br = (byte) ((data & 0b1111_1000_0000_0000) >> 8);// 由于是与16位做与操作，所以多出了后面8位
						byte bg = (byte) ((data & 0x7E0) >> 3);//byte bg = (byte) ((data & 0b0000_0111_1110_0000) >> 3);// 多出了3位，在强转时前8位会自动丢失
						byte bb = (byte) ((data & 0x1F) << 3);//byte bb = (byte) ((data & 0b0000_0000_0001_1111) << 3);// 少了3位
						bi.setRGB(w, h, Common.readInt(new byte[]{(byte) 255, br, bg, bb}, 0, false)); // 此时需要反序
					}
				}
		}
	}
	
	/** 从随机文件访问流中读取ImageInfo对象  */
	private static WILImageInfo readImageInfo(int position) throws IOException {
		instance.raf_wil.seek(position);
		WILImageInfo res = new WILImageInfo();
		byte[] bytes = new byte[8];
		instance.raf_wil.readFully(bytes);
		res.setWidth(Common.readShort(bytes, 0, true));
		res.setHeight(Common.readShort(bytes, 2, true));
		res.setOffsetX(Common.readShort(bytes, 4, true));
		res.setOffsetY(Common.readShort(bytes, 6, true));
		return res;
	}
	
	/** 图片信息 */
	public static class WILImageInfo {
		/** 图片宽度 */
		private short width;
		/** 图片高度 */
		private short height;
		/** 图片横向偏移量 */
		private short offsetX;
		/** 图片纵向偏移量 */
		private short offsetY;
		
		/** 无参构造函数 */
		public WILImageInfo() {}
		/** 基于已有对象构造实例 */
		public WILImageInfo(WILImageInfo imageInfo) {
			this.height = imageInfo.height;
			this.offsetX = imageInfo.offsetX;
			this.offsetY = imageInfo.offsetY;
			this.width = imageInfo.width;
		}
		/** 带全部参数的构造函数 */
		public WILImageInfo(short width, short height, short offsetX, short offsetY) {
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