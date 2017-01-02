package org.joot.mir2.tools.imageviewer;

import java.io.IOException;
import java.io.InputStream;

/**
 * 
 * 通用工具集 <br>
 * 提供其它工具类或实例使用的基础方法
 * 
 * @author ShawRyan
 * 
 */
public class Common {
	
	/**
	 * 反转字节数组
	 * 
	 * @param bytes
	 *            要反转的字节数组
	 * @return 反转过后的字节数组
	 */
	public static byte[] reverse(byte[] bytes) {
		byte[] writeBytes = new byte[bytes.length];
		for (int i = 0; i < bytes.length; ++i)
			writeBytes[i] = bytes[bytes.length - i - 1];
		return writeBytes;
	}
	
	/**
	 * 从字节数组中读取断整形数据
	 * 
	 * @param bytes
	 * 	数据来源
	 * @param index
	 * 	数组中获取数据的起始位置(索引从0开始，数据还原将包含给定位置)
	 * <br>
	 * 	例如给定[0x01,0x02,0x03,0x04],2,true作为参数则函数会将[0x04,0x03]还原为短整形数值并返回
	 * @param reverse
	 * 	是否需要反转字节(只针对包含当前数据的字节数组)
	 * @return
	 * 	从字节数组指定位置2个字节还原出的短整形数值
	 */
	public static short readShort(byte[] bytes, int index, boolean reverse) {
		if(reverse)
			return (short) ((bytes[index + 1] << 8) | (bytes[index] & 0xff));
		else
			return (short) ((bytes[index] << 8) | (bytes[index + 1] & 0xff));
	}
	
	/**
	 * 从字节数组中读取整形数据
	 * 
	 * @param bytes
	 * 	数据来源
	 * @param index
	 * 	数组中获取数据的起始位置(索引从0开始，数据还原将包含给定位置) 具体规则示例参见{@link #readShort(byte[], int, boolean) readShort}
	 * @param reverse
	 * 	是否需要反转字节(只针对包含当前数据的字节数组)
	 * @return
	 * 	从字节数组指定位置4个字节还原出的短整形数值
	 */
	public static int readInt(byte[] bytes, int index, boolean reverse) {
		if(reverse)
			return (int) (((bytes[index + 3] & 0xff) << 24)  
	                | ((bytes[index + 2] & 0xff) << 16)  
	                | ((bytes[index + 1] & 0xff) << 8) 
	                | (bytes[index] & 0xff));
		else
			return (int) (((bytes[index] & 0xff) << 24)  
	                | ((bytes[index + 1] & 0xff) << 16)  
	                | ((bytes[index + 2] & 0xff) << 8) 
	                | (bytes[index + 3] & 0xff));
	}
	
	/**
	 * 从字节数组中读取长整形数据
	 * 
	 * @param bytes
	 * 	数据来源
	 * @param index
	 * 	数组中获取数据的起始位置(索引从0开始，数据还原将包含给定位置) 具体规则示例参见{@link #readShort(byte[], int, boolean) readShort}
	 * @param reverse
	 * 	是否需要反转字节(只针对包含当前数据的字节数组)
	 * @return
	 * 	从字节数组指定位置8个字节还原出的短整形数值
	 */
	public static long readLong(byte[] bytes, int index, boolean reverse) {
		if(reverse)
			return (((long) bytes[index + 7] & 0xff) << 56)  
	                | (((long) bytes[index + 6] & 0xff) << 48)  
	                | (((long) bytes[index + 5] & 0xff) << 40)  
	                | (((long) bytes[index + 4] & 0xff) << 32)  
	                | (((long) bytes[index + 3] & 0xff) << 24)  
	                | (((long) bytes[index + 2] & 0xff) << 16)  
	                | (((long) bytes[index + 1] & 0xff) << 8) 
	                | ((long) bytes[index] & 0xff);
		else
			return (((long) bytes[index] & 0xff) << 56)  
	                | (((long) bytes[index + 1] & 0xff) << 48)  
	                | (((long) bytes[index + 2] & 0xff) << 40)  
	                | (((long) bytes[index + 3] & 0xff) << 32)  
	                | (((long) bytes[index + 4] & 0xff) << 24)  
	                | (((long) bytes[index + 5] & 0xff) << 16)  
	                | (((long) bytes[index + 6] & 0xff) << 8) 
	                | ((long) bytes[index + 7] & 0xff);
	}

	/**
	 * 从字节数组中读取双精度浮点数
	 * 
	 * @param bytes
	 * 	数据来源
	 * @param index
	 * 	数组中获取数据的起始位置(索引从0开始，数据还原将包含给定位置) 具体规则示例参见{@link #readShort(byte[], int, boolean) readShort}
	 * @param reverse
	 * 	是否需要反转字节(只针对包含当前数据的字节数组)
	 * <br>
	 * 	函数内部调用{@link #readLong(byte[], int, boolean)}并使用{@link Double#longBitsToDouble(long) longBitsToDouble}实现功能
	 * @return
	 * 	从字节数组指定位置8个字节还原出的双精度浮点数数值
	 */
	public static double readDouble(byte[] bytes, int index, boolean reverse) {
		if(reverse)
			return Double.longBitsToDouble((((long) bytes[index + 7] & 0xff) << 56)  
	                | (((long) bytes[index + 6] & 0xff) << 48)  
	                | (((long) bytes[index + 5] & 0xff) << 40)  
	                | (((long) bytes[index + 4] & 0xff) << 32)  
	                | (((long) bytes[index + 3] & 0xff) << 24)  
	                | (((long) bytes[index + 2] & 0xff) << 16)  
	                | (((long) bytes[index + 1] & 0xff) << 8) 
	                | ((long) bytes[index] & 0xff));
		else
			return Double.longBitsToDouble((((long) bytes[index] & 0xff) << 56)  
	                | (((long) bytes[index + 1] & 0xff) << 48)  
	                | (((long) bytes[index + 2] & 0xff) << 40)  
	                | (((long) bytes[index + 3] & 0xff) << 32)  
	                | (((long) bytes[index + 4] & 0xff) << 24)  
	                | (((long) bytes[index + 5] & 0xff) << 16)  
	                | (((long) bytes[index + 6] & 0xff) << 8) 
	                | ((long) bytes[index + 7] & 0xff));
	}
	
	/**
	 * 从流中读取短整型数据
	 * 
	 * @param is
	 * 	数据流
	 * @param index
	 * 	读取位置
	 * @param reverse
	 * 	是否需要反转字节
	 * @return
	 * 	读取到的数据
	 * @throws IOException
	 * 	可能发生的流异常
	 */
	public static short readShort(InputStream is, int index, boolean reverse) throws IOException {
		if(index > 0) is.skip(index);
		byte[] bytes = new byte[2];
		is.read(bytes);
		if(reverse)
			return (short) ((bytes[1] << 8) | (bytes[0] & 0xff));
		else
			return (short) ((bytes[0] << 8) | (bytes[1] & 0xff));
	}
	
	/**
	 * 从流中读取整型数据
	 * 
	 * @param is
	 * 	数据流
	 * @param index
	 * 	起始位置
	 * @param reverse
	 * 	是否需要反序
	 * @return
	 * 	还原出的整型
	 * @throws IOException
	 * 	可能的流异常
	 */
	public static int readInt(InputStream is, int index, boolean reverse) throws IOException {
		if(index > 0) is.skip(index);
		byte[] bytes = new byte[4];
		is.read(bytes);
		if(reverse)
			return (int) (((bytes[3] & 0xff) << 24)  
	                | ((bytes[2] & 0xff) << 16)  
	                | ((bytes[1] & 0xff) << 8) 
	                | (bytes[0] & 0xff));
		else
			return (int) (((bytes[0] & 0xff) << 24)  
	                | ((bytes[1] & 0xff) << 16)  
	                | ((bytes[2] & 0xff) << 8) 
	                | (bytes[3] & 0xff));
	}
	
	/**
	 * 判断长整型数据最高位是否为1
	 * 
	 * @param target
	 * 	目标数据
	 * @return
	 * 	true表示该数据最高位为1，false表示该数据最高位为0
	 */
	public static boolean is1AtTopDigit(long target) {
		//return (target & 0b1000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000l) == 0b1000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000l;
		return (target & 0x8000000000000000l) == 0x8000000000000000l;
	}

	/**
	 * 判断整型数据最高位是否为1
	 * 
	 * @param target
	 * 	目标数据
	 * @return
	 * 	true表示该数据最高位为1，false表示该数据最高位为0
	 */
	public static boolean is1AtTopDigit(int target) {
		//return (target & 0b1000_0000_0000_0000_0000_0000_0000_0000) == 0b1000_0000_0000_0000_0000_0000_0000_0000;
		return (target & 0x80000000) == 0x80000000;
	}

	/**
	 * 判断短整型数据最高位是否为1
	 * 
	 * @param target
	 * 	目标数据
	 * @return
	 * 	true表示该数据最高位为1，false表示该数据最高位为0
	 */
	public static boolean is1AtTopDigit(short target) {
		//return (target & 0b1000_0000_0000_0000) == 0b1000_0000_0000_0000;
		return (target & 0x8000) == 0x8000;
	}

	/**
	 * 判断字节数据最高位是否为1
	 * 
	 * @param target
	 * 	目标数据
	 * @return
	 * 	true表示该数据最高位为1，false表示该数据最高位为0
	 */
	public static boolean is1AtTopDigit(byte target) {
		//return (target & 0x1000_0000) == 0x1000_0000;
		return (target & 0x80) == 0x80;
	}
}