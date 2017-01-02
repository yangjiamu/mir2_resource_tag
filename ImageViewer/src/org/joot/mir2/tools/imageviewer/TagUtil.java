package org.joot.mir2.tools.imageviewer;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

/**
 * Created by yang on 2016/11/11.
 */
public class TagUtil {
    private static final String rootPath = "C:\\Users\\yang\\Downloads\\Mir2online\\Ã×¶ûÔÚÏß\\Data\\";
    public static void main(String[] args) throws IOException {
        /*tagBiQiXigoGuai();
        tagDaDao();
        tagGongJianShour();
        tagSenLinXueRen();
        tagTiger();
        tagKuLouJingLing();
        tagAaoMaWeiShi();
        tagShiWang();*/

        //tagHair();
        //tagHum();
        testColor();
    }


    public static void tag( ColorMappingEnum color, int rx, int ry, int startId, int endId, boolean isHair) throws IOException {
        for (int i = startId; i <= endId; i++) {
            WILReader.changeImage(i, color, rx, ry, isHair);
        }
    }

    public static void tagHum() throws IOException {
        String wilPath = rootPath + "Hum.wil";
        WILReader.load(wilPath, true);
        tag(ColorMappingEnum.BLUE, 10, 10, 0, 2000, false);
    }
    public static void tagHair() throws IOException {
        String wilPath = rootPath + "Hair.wil";
        WILReader.load(wilPath, true);
        tag(ColorMappingEnum.BLUE, 8,6, 0, WILReader.getImageCount(), true);
    }

    //´óµ¶
    public static void tagDaDao() throws IOException {
        String wilPath = "C:\\Users\\yang\\Downloads\\Mir2online\\Ã×¶ûÔÚÏß\\Data\\Mon1.wil";
        WILReader.load(wilPath, true);
        tag(ColorMappingEnum.CYAN, 10,10, 0, 189, false);
        tag(ColorMappingEnum.CYAN, 10,10, 560,749, false);
        tag(ColorMappingEnum.CYAN, 10,10, 1120, 1309, false);
    }
    public static void tagGongJianShour() throws IOException {
        String wilPath = rootPath + "Mon8.wil";
        WILReader.load(wilPath, true);
        tag(ColorMappingEnum.CYAN, 10,10, 225, 418, false);
    }
    //É­ÁÖÑ©ÈË
    public static void tagSenLinXueRen() throws IOException {
        String wilPath = "C:\\Users\\yang\\Downloads\\Mir2online\\Ã×¶ûÔÚÏß\\Data\\Mon1.wil";
        WILReader.load(wilPath, true);
        tag(ColorMappingEnum.YELLOW, 10,10, 280, 489, false);
    }
    //ÀÏ»¢
    public static void tagTiger() throws IOException {
        String wilPath = rootPath + "Mon23.wil";
        WILReader.load(wilPath, true);
        tag(ColorMappingEnum.MAGENTA, 20,20, 1290, 1553,false);
        tag(ColorMappingEnum.MAGENTA, 20,20, 1560, 1562, false);
        tag(ColorMappingEnum.MAGENTA, 20,20, 1570, 1571, false);
        tag(ColorMappingEnum.MAGENTA, 20,20, 1580, 1581,false);
        tag(ColorMappingEnum.MAGENTA, 20,20, 1600, 1603, false);
        tag(ColorMappingEnum.MAGENTA, 20,20, 1610, 1610,false);
        tag(ColorMappingEnum.MAGENTA, 20,20, 1620, 1621, false);
        tag(ColorMappingEnum.MAGENTA, 20,20, 1630, 1783, false);
    }
    //±ÈÆæÐ¡¹Ö
    public static void tagBiQiXigoGuai() throws IOException {
        String wilPath = rootPath + "Mon17.wil";
        WILReader.load(wilPath, true);
        System.out.println(WILReader.getImageCount());
        //¼¦
        tag(ColorMappingEnum.YELLOW, 10, 10, 0, 147, false);
        tag(ColorMappingEnum.YELLOW, 10, 10, 154, 157, false);
        tag(ColorMappingEnum.YELLOW, 10, 10, 164, 167, false);
        tag(ColorMappingEnum.YELLOW, 10, 10, 174, 177, false);
        tag(ColorMappingEnum.YELLOW, 10, 10, 184, 187, false);
        tag(ColorMappingEnum.YELLOW, 10, 10, 194, 197, false);
        tag(ColorMappingEnum.YELLOW, 10, 10, 204, 207, false);
        tag(ColorMappingEnum.YELLOW, 10, 10, 214, 217, false);
        //Â¹
        tag(ColorMappingEnum.YELLOW, 10, 10, 232, 377, false);
        tag(ColorMappingEnum.YELLOW, 10, 10, 386, 388, false);
        tag(ColorMappingEnum.YELLOW, 10, 10, 396, 398, false);
        tag(ColorMappingEnum.YELLOW, 10, 10, 406, 408, false);
        tag(ColorMappingEnum.YELLOW, 10, 10, 416, 418, false);
        tag(ColorMappingEnum.YELLOW, 10, 10, 426, 428, false);
        tag(ColorMappingEnum.YELLOW, 10, 10, 436, 438, false);
        tag(ColorMappingEnum.YELLOW, 10, 10, 446, 448, false);
        //¸òó¡
        tag(ColorMappingEnum.YELLOW, 10, 10, 464, 611, false);
        tag(ColorMappingEnum.YELLOW, 10, 10, 618, 621, false);
        tag(ColorMappingEnum.YELLOW, 10, 10, 628, 631, false);
        tag(ColorMappingEnum.YELLOW, 10, 10, 638, 641, false);
        tag(ColorMappingEnum.YELLOW, 10, 10, 648, 651, false);
        tag(ColorMappingEnum.YELLOW, 10, 10, 658, 661, false);
        tag(ColorMappingEnum.YELLOW, 10, 10, 668, 671, false);
        tag(ColorMappingEnum.YELLOW, 10, 10, 678, 681, false);
        //Ö©Öë
        tag(ColorMappingEnum.YELLOW, 10, 10, 688, 832, false);
        tag(ColorMappingEnum.YELLOW, 10, 10, 842, 842, false);
        tag(ColorMappingEnum.YELLOW, 10, 10, 852, 852, false);
        tag(ColorMappingEnum.YELLOW, 10, 10, 862, 862, false);
        tag(ColorMappingEnum.YELLOW, 10, 10, 872, 872, false);
        tag(ColorMappingEnum.YELLOW, 10, 10, 882, 882, false);
        tag(ColorMappingEnum.YELLOW, 10, 10, 892, 892, false);
        tag(ColorMappingEnum.YELLOW, 10, 10, 902, 902, false);
        wilPath = rootPath + "Mon12.wil";
        WILReader.load(wilPath, true);
        //°ëÊÞÈË
        tag(ColorMappingEnum.YELLOW, 10, 10, 0, 254, false);
        tag(ColorMappingEnum.YELLOW, 10, 10, 260, 261, false);
        tag(ColorMappingEnum.YELLOW, 10, 10, 270, 271, false);
        tag(ColorMappingEnum.YELLOW, 10, 10, 280, 281, false);
        tag(ColorMappingEnum.YELLOW, 10, 10, 290, 291, false);
        tag(ColorMappingEnum.YELLOW, 10, 10, 300, 301, false);
        tag(ColorMappingEnum.YELLOW, 10, 10, 310, 311, false);
        tag(ColorMappingEnum.YELLOW, 10, 10, 320, 321, false);
        tag(ColorMappingEnum.YELLOW, 10, 10, 330, 331, false);
        // °ëÊÞÕ½Ê¿
        tag(ColorMappingEnum.YELLOW, 10, 10, 360, 595, false);
        tag(ColorMappingEnum.YELLOW, 10, 10, 620, 621, false);
        tag(ColorMappingEnum.YELLOW, 10, 10, 630, 631, false);
        tag(ColorMappingEnum.YELLOW, 10, 10, 640, 641, false);
        tag(ColorMappingEnum.YELLOW, 10, 10, 650, 651, false);
        tag(ColorMappingEnum.YELLOW, 10, 10, 660, 661, false);
        tag(ColorMappingEnum.YELLOW, 10, 10, 670, 671, false);
        tag(ColorMappingEnum.YELLOW, 10, 10, 680, 681, false);
        tag(ColorMappingEnum.YELLOW, 10, 10, 690, 691, false);
        //°ëÊÞÓÂÊ¿
        tag(ColorMappingEnum.YELLOW, 15, 15, 720, 955, false);
        tag(ColorMappingEnum.YELLOW, 15, 15, 981, 982, false);
        tag(ColorMappingEnum.YELLOW, 15, 15, 991, 992, false);
        tag(ColorMappingEnum.YELLOW, 15, 15, 1001, 1002, false);
        tag(ColorMappingEnum.YELLOW, 15, 15, 1011, 1012, false);
        tag(ColorMappingEnum.YELLOW, 15, 15, 1021, 1022, false);
        tag(ColorMappingEnum.YELLOW, 15, 15, 1031, 1032, false);
        tag(ColorMappingEnum.YELLOW, 15, 15, 1041, 1042, false);
        tag(ColorMappingEnum.YELLOW, 15, 15, 1051, 1052, false);

        //¹³Ç®Ã¨
        wilPath = rootPath + "Mon3.wil";
        WILReader.load(wilPath, true);
        tag(ColorMappingEnum.YELLOW, 10, 10, 1180, 1324, false);
        tag(ColorMappingEnum.YELLOW, 10, 10, 1404, 1548, false);
        //µ¾²ÝÈË
        tag(ColorMappingEnum.YELLOW, 10, 10, 1628, 1771, false);
        //Àõ×ÓÊ÷
        wilPath = rootPath + "Mon15.wil";
        WILReader.load(wilPath, true);
        //tag(Color.YELLOW, 10, 10, 52, 73, false);
        //ºÚÌ´Ê÷
        //tag(Color.YELLOW, 10, 10, 116, 137, false);
        //Ä¢¹½
        //tag(ColorMappingEnum.YELLOW, 10, 10, 148, 161, false);
    }

    public static void tagKuLouJingLing() throws IOException {
        String wilPath = rootPath + "Mon16.wil";
        WILReader.load(wilPath, true);
        tag(ColorMappingEnum.MAGENTA, 20, 20, 1,144,false);
        tag(ColorMappingEnum.MAGENTA, 20, 20, 1,144,false);
        tag(ColorMappingEnum.MAGENTA, 20, 20, 154,154,false);
        tag(ColorMappingEnum.MAGENTA, 20, 20, 164,164,false);
        tag(ColorMappingEnum.MAGENTA, 20, 20, 174,174,false);
        tag(ColorMappingEnum.MAGENTA, 20, 20, 184,184,false);
        tag(ColorMappingEnum.MAGENTA, 20, 20, 194,194,false);
        tag(ColorMappingEnum.MAGENTA, 20, 20, 204,204,false);
        tag(ColorMappingEnum.MAGENTA, 20, 20, 214,214,false);
    }
    public static void tagAaoMaWeiShi() throws IOException {
        String wilPath = rootPath + "Mon16.wil";
        WILReader.load(wilPath, true);
        tag(ColorMappingEnum.YELLOW, 15, 15, 224,371,false);
        tag(ColorMappingEnum.YELLOW, 15, 15, 378, 380,false);
        tag(ColorMappingEnum.YELLOW, 15, 15, 388, 390,false);
        tag(ColorMappingEnum.YELLOW, 15, 15, 398, 400,false);
        tag(ColorMappingEnum.YELLOW, 15, 15, 408, 410,false);
        tag(ColorMappingEnum.YELLOW, 15, 15, 418, 420,false);
        tag(ColorMappingEnum.YELLOW, 15, 15, 428, 430,false);
        tag(ColorMappingEnum.YELLOW, 15, 15, 438, 440,false);
    }

    public static void tagShiWang() throws IOException {
        String wilPath = rootPath + "Mon16.wil";
        WILReader.load(wilPath, true);
        tag(ColorMappingEnum.MAGENTA, 20, 20, 448, 594,false);
        tag(ColorMappingEnum.MAGENTA, 20, 20, 602, 603,false);
        tag(ColorMappingEnum.MAGENTA, 20, 20, 612, 613,false);
        tag(ColorMappingEnum.MAGENTA, 20, 20, 622, 623,false);
        tag(ColorMappingEnum.MAGENTA, 20, 20, 632, 633,false);
        tag(ColorMappingEnum.MAGENTA, 20, 20, 642, 643,false);
        tag(ColorMappingEnum.MAGENTA, 20, 20, 652, 663,false);
        tag(ColorMappingEnum.MAGENTA, 20, 20, 662, 663,false);

    }
    public static void testColor() throws IOException {
        String wilPath = "C:\\Users\\yang\\Pictures\\color_found_test\\Hum.wil";
        WILReader.load(wilPath, true);
        System.out.println(WILReader.getImageCount());
        int index = 0;
        int i = 0;
        while (index < 256){
            WILReader.WILImageInfo wii = WILReader.getOneImageInfo(i);
            if(wii.getWidth() > 10 && wii.getHeight() > 10) {
                WILReader.tagByDigit(i, index, wii.getWidth() - 1, wii.getHeight() - 1);
                WILReader.load(wilPath, true);
                String s = Integer.toHexString(index);
                ImageIO.write(WILReader.getOneImage(i), "png",
                        new File("C:\\Users\\yang\\Pictures\\color_found_test\\colors\\" + s + ".png"));
                ++index;
            }
            ++i;
        }
        System.out.println(i);
    }
}
