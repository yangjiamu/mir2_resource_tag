package org.joot.mir2.tools.imageviewer;

import java.io.IOException;
import java.io.InputStream;

/**
 * 
 * ͨ�ù��߼� <br>
 * �ṩ�����������ʵ��ʹ�õĻ�������
 * 
 * @author ShawRyan
 * 
 */
public class Common {
	
	/**
	 * ��ת�ֽ�����
	 * 
	 * @param bytes
	 *            Ҫ��ת���ֽ�����
	 * @return ��ת������ֽ�����
	 */
	public static byte[] reverse(byte[] bytes) {
		byte[] writeBytes = new byte[bytes.length];
		for (int i = 0; i < bytes.length; ++i)
			writeBytes[i] = bytes[bytes.length - i - 1];
		return writeBytes;
	}
	
	/**
	 * ���ֽ������ж�ȡ����������
	 * 
	 * @param bytes
	 * 	������Դ
	 * @param index
	 * 	�����л�ȡ���ݵ���ʼλ��(������0��ʼ�����ݻ�ԭ����������λ��)
	 * <br>
	 * 	�������[0x01,0x02,0x03,0x04],2,true��Ϊ���������Ὣ[0x04,0x03]��ԭΪ��������ֵ������
	 * @param reverse
	 * 	�Ƿ���Ҫ��ת�ֽ�(ֻ��԰�����ǰ���ݵ��ֽ�����)
	 * @return
	 * 	���ֽ�����ָ��λ��2���ֽڻ�ԭ���Ķ�������ֵ
	 */
	public static short readShort(byte[] bytes, int index, boolean reverse) {
		if(reverse)
			return (short) ((bytes[index + 1] << 8) | (bytes[index] & 0xff));
		else
			return (short) ((bytes[index] << 8) | (bytes[index + 1] & 0xff));
	}
	
	/**
	 * ���ֽ������ж�ȡ��������
	 * 
	 * @param bytes
	 * 	������Դ
	 * @param index
	 * 	�����л�ȡ���ݵ���ʼλ��(������0��ʼ�����ݻ�ԭ����������λ��) �������ʾ���μ�{@link #readShort(byte[], int, boolean) readShort}
	 * @param reverse
	 * 	�Ƿ���Ҫ��ת�ֽ�(ֻ��԰�����ǰ���ݵ��ֽ�����)
	 * @return
	 * 	���ֽ�����ָ��λ��4���ֽڻ�ԭ���Ķ�������ֵ
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
	 * ���ֽ������ж�ȡ����������
	 * 
	 * @param bytes
	 * 	������Դ
	 * @param index
	 * 	�����л�ȡ���ݵ���ʼλ��(������0��ʼ�����ݻ�ԭ����������λ��) �������ʾ���μ�{@link #readShort(byte[], int, boolean) readShort}
	 * @param reverse
	 * 	�Ƿ���Ҫ��ת�ֽ�(ֻ��԰�����ǰ���ݵ��ֽ�����)
	 * @return
	 * 	���ֽ�����ָ��λ��8���ֽڻ�ԭ���Ķ�������ֵ
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
	 * ���ֽ������ж�ȡ˫���ȸ�����
	 * 
	 * @param bytes
	 * 	������Դ
	 * @param index
	 * 	�����л�ȡ���ݵ���ʼλ��(������0��ʼ�����ݻ�ԭ����������λ��) �������ʾ���μ�{@link #readShort(byte[], int, boolean) readShort}
	 * @param reverse
	 * 	�Ƿ���Ҫ��ת�ֽ�(ֻ��԰�����ǰ���ݵ��ֽ�����)
	 * <br>
	 * 	�����ڲ�����{@link #readLong(byte[], int, boolean)}��ʹ��{@link Double#longBitsToDouble(long) longBitsToDouble}ʵ�ֹ���
	 * @return
	 * 	���ֽ�����ָ��λ��8���ֽڻ�ԭ����˫���ȸ�������ֵ
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
	 * �����ж�ȡ����������
	 * 
	 * @param is
	 * 	������
	 * @param index
	 * 	��ȡλ��
	 * @param reverse
	 * 	�Ƿ���Ҫ��ת�ֽ�
	 * @return
	 * 	��ȡ��������
	 * @throws IOException
	 * 	���ܷ��������쳣
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
	 * �����ж�ȡ��������
	 * 
	 * @param is
	 * 	������
	 * @param index
	 * 	��ʼλ��
	 * @param reverse
	 * 	�Ƿ���Ҫ����
	 * @return
	 * 	��ԭ��������
	 * @throws IOException
	 * 	���ܵ����쳣
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
	 * �жϳ������������λ�Ƿ�Ϊ1
	 * 
	 * @param target
	 * 	Ŀ������
	 * @return
	 * 	true��ʾ���������λΪ1��false��ʾ���������λΪ0
	 */
	public static boolean is1AtTopDigit(long target) {
		//return (target & 0b1000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000l) == 0b1000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000l;
		return (target & 0x8000000000000000l) == 0x8000000000000000l;
	}

	/**
	 * �ж������������λ�Ƿ�Ϊ1
	 * 
	 * @param target
	 * 	Ŀ������
	 * @return
	 * 	true��ʾ���������λΪ1��false��ʾ���������λΪ0
	 */
	public static boolean is1AtTopDigit(int target) {
		//return (target & 0b1000_0000_0000_0000_0000_0000_0000_0000) == 0b1000_0000_0000_0000_0000_0000_0000_0000;
		return (target & 0x80000000) == 0x80000000;
	}

	/**
	 * �ж϶������������λ�Ƿ�Ϊ1
	 * 
	 * @param target
	 * 	Ŀ������
	 * @return
	 * 	true��ʾ���������λΪ1��false��ʾ���������λΪ0
	 */
	public static boolean is1AtTopDigit(short target) {
		//return (target & 0b1000_0000_0000_0000) == 0b1000_0000_0000_0000;
		return (target & 0x8000) == 0x8000;
	}

	/**
	 * �ж��ֽ��������λ�Ƿ�Ϊ1
	 * 
	 * @param target
	 * 	Ŀ������
	 * @return
	 * 	true��ʾ���������λΪ1��false��ʾ���������λΪ0
	 */
	public static boolean is1AtTopDigit(byte target) {
		//return (target & 0x1000_0000) == 0x1000_0000;
		return (target & 0x80) == 0x80;
	}
}