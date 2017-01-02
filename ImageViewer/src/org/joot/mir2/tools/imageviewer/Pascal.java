package org.joot.mir2.tools.imageviewer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;

/**
 * 针对Pascal语言的工具集类
 * <br>
 * 主要集成了对通过Java语言反序列化Pascal语言序列化的数据
 * @author ShawRyan
 *
 */
public class Pascal {
	public static void main(String[] args) {
		System.out.println(pallete.length);
		/*for (int i : pallete) {
			byte b = (byte)i;
			System.out.println(b);
		}*/
	}
	/** Delphi 灰度图调色板，如果因设备不支持透明色可将调色板第一个颜色置为背景色，如-16777216表示黑色 */
	public static int[] pallete = { -16777216, -8388608, -16744448, -8355840, -16777088, -8388480, -16744320, -4144960, -11173737, -6440504, -8686733, -13817559, -10857902, -10266022, -12437191, -14870504, -15200240, -14084072, -15726584, -886415, -2005153, -42406, -52943, -2729390, -7073792, -7067368, -13039616, -9236480, -4909056, -4365486, -12445680, -21863, -10874880, -9225943, -5944783, -7046285, -4369871, -11394800, -8703720, -13821936, -7583183, -7067392, -4378368, -3771566, -9752296, -3773630, -3257856, -5938375, -10866408, -14020608, -15398912, -12969984, -16252928, -14090240, -11927552, -6488064, -2359296, -2228224, -327680, -6524078, -7050422, -9221591, -11390696, -7583208, -7846895, -11919104, -14608368, -2714534, -3773663, -1086720, -35072, -5925756, -12439263, -15200248, -14084088, -14610432, -13031144, -7576775, -12441328, -9747944, -8697320, -7058944, -7568261, -9739430, -11910599, -14081768, -12175063, -4872812, -8688806, -3231340, -5927821, -7572646, -4877197, -2710157, -1071798, -1063284, -8690878, -9742791, -4352934, -10274560, -2701651, -11386327, -7052520, -1059155, -5927837, -10266038, -4348549, -10862056, -4355023, -13291223, -7043997, -8688822, -5927846, -10859991, -6522055, -12439280, -1069791, -15200256, -14081792, -6526208, -7044006, -11386344, -9741783, -8690911, -6522079, -2185984, -10857927, -13555440, -3228293, -10266055, -7044022, -3758807, -15688680, -12415926, -13530046, -15690711, -16246768, -16246760, -16242416, -15187415, -5917267, -9735309, -15193815, -15187382, -13548982, -10238242, -12263937, -7547153, -9213127, -532935, -528500, -530688, -9737382, -10842971, -12995089, -11887410, -13531979, -13544853, -2171178, -4342347, -7566204, -526370, -16775144, -16246727, -16248791, -16246784, -16242432, -16756059, -16745506, -15718070, -15713941, -15707508, -14591323, -15716006, -15711612, -13544828, -15195855, -11904389, -11375707, -14075549, -15709474, -14079711, -11908551, -14079720, -11908567, -8684734, -6513590, -10855895, -12434924, -13027072, -10921728, -3525332, -9735391, -14077696, -13551344, -13551336, -12432896, -11377896, -10849495, -13546984, -15195904, -15191808, -15189744, -10255286, -9716406, -10242742, -10240694, -10838966, -11891655, -10238390, -10234294, -11369398, -13536471, -10238374, -11354806, -15663360, -15193832, -11892662, -11868342, -16754176, -16742400, -16739328, -16720384, -16716288, -16712960, -11904364, -10259531, -8680234, -9733162, -8943361, -3750194, -7039844, -6515514, -13553351, -14083964, -15204220, -11910574, -11386245, -10265997, -3230217, -7570532, -8969524, -2249985, -1002454, -2162529, -1894477, -1040, -6250332, -8355712, -65536, -16711936, -256, -16776961, -65281, -16711681, -1 };
	
	/**
	 * 将Pascal语言序列化的TDateTime类型数据还原为java.util.Date类型对象
	 * <br>
	 * Pascal中时间日期一般使用TDateTime类型进行存储，TDateTime类型实际上是双精度浮点数类型(记Double)
	 * <br>
	 * TDateTime类型起始时间为1899年12月30日0时0分0秒，且使用整数部分存储当前日期(TDateTime对象)与起始时间天数差值(可为负)，使用小数部分表示当天时间消耗比例
	 * <br>
	 * 在与Java时间日期进行转换时，需要先构造一个1899年12月30日0时0分0秒的时间，然后使用给定浮点数进行计算
	 * <br>
	 * 由于Java中时间日期的起始为1970年1月1日，故可以使用带负数的毫秒值构造函数构造与TDateTime起点一致的时间日期对象，使用如下代码即可完成
	 * <p>
	 * 		Calendar datetime = Calendar.getInstance();
	 * <br>
	 *		datetime.setTimeInMillis(-2209190400000L);
	 * </p>
	 * 其中-2209190400000L则为从1970年至1899年12月30日毫秒差值
	 * <br>
	 * 可以通过new Date(1970,1,1,0,0,0).getTime()-new Date(1899,12,30,0,0,0).getTime()得到(不推荐，有偏差)
	 * @param bytes
	 * 	数据来源字节数组
	 * @param index
	 * 	数组中获取数据的起始位置(索引从0开始，数据还原将包含给定位置)
	 * @param reverse
	 * 	是否需要反转数组
	 * @return
	 * 	通过字节数组反序列化得到的时间日期对象
	 */
	public static Date readDate(byte[] bytes, int index, boolean reverse) {		
		double tDatetime = Common.readDouble(bytes, index, reverse);
		int days = (int)tDatetime;
		double times = tDatetime - days;
		Calendar datetime = Calendar.getInstance();
		datetime.setTimeInMillis(-2209190400000L);
		datetime.add(Calendar.DAY_OF_YEAR, days);
		if(times < 0) times = -times; 
		datetime.add(Calendar.MILLISECOND, (int) (24 * 60 * 60 * 1000 * times));
		return datetime.getTime();
	}
	
	/**
	 * 从字节数组中还原Pascal静态单字符串
	 * 
	 * @param bytes
	 * 	数据来源
	 * @param index
	 * 	数组中获取数据的起始位置(索引从0开始，数据还原将包含给定位置)
	 * @return
	 * 	长度不大于256的Ascii字符数组
	 */
	public static String readStaticSingleString(byte[] bytes, int index) {
		char[] res = new char[bytes[index]];
		for(int i = 0; i < res.length; ++i)
			res[i] = (char) bytes[index + i + 1];
		return String.valueOf(res);
	}
	
	/**
	 * 从流中还原静态单字符串
	 * 
	 * @param is
	 * 	数据流
	 * @param index
	 * 	起始位置
	 * @param length
	 * 	长度(占用长度而非字符串本身长度)
	 * @return
	 * 	字符串
	 * @throws IOException
	 * 	可能的流异常
	 */
	public static String readStaticSingleString(InputStream is, int index, int length) throws IOException {
		if(index > 0) is.skip(index);
		byte[] bytes = new byte[length];
		is.read(bytes);
		char[] res = new char[bytes[0]];
		for(int i = 0; i < res.length; ++i)
			res[i] = (char) bytes[i + 1];
		return String.valueOf(res);
	}
	
	/**
	 * 从字节数组中还原字符数组
	 * 
	 * @param bytes
	 * 	数据来源
	 * @param index
	 * 	数组中获取数据的起始位置(索引从0开始，数据还原将包含给定位置)
	 * @param length
	 * 	数组长度
	 * @return
	 * 	长度为length的Ascii字符数组
	 */
	public static char[] readChars(byte[] bytes, int index, int length) {
		char[] res = new char[length];
		for(int i = 0; i < length; ++i)
			res[i] = (char) bytes[index + i];
		return res;
	}
	
	/**
	 * 对于bmp图片填充字节进行补足
	 * 
	 * @param bitCount
	 * 	每行图片色彩值字节位数(bit)
	 * @return
	 * 	每行图片色彩数据占字节数(byte)
	 */
	public static int widthBytes(int bitCount) {
		return (bitCount + 31) / 32 * 4;
	}
	
	/**
	 * 计算bmp图片逐行读取时需要跳过的字节数
	 * <br>
	 * 即用该行实际占用的字节数减去真正占用的字节数
	 * 
	 * @param bit
	 * 	位深度
	 * @param width
	 * 	图片宽度
	 * @return
	 * 	读取某行数据时需要跳过的字节数
	 * @see #widthBytes(int)
	 */
	public static int skipBytes(int bit, int width) {
		return widthBytes(bit * width) - width * (bit / 8);
	}
	
	/**
	 * 将颜色数换算成字节位
	 * 
	 * @param colorCount
	 * 	颜色数
	 * @return
	 * 	字节位
	 */
	public static int colorCountToBitCount(int colorCount) {
		if(colorCount == 256) return 8;
		else if(colorCount == 65536) return 16;
		else if(colorCount == 16777216) return 24;
		else return 32;
	}
}