package org.joot.mir2.tools.imageviewer;

import java.awt.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yang on 2016/11/20.
 */
public class SmartTag {
    public static void main(String[] args) throws IOException {
        String [] fileNames = {/*"1_3", "4_6", "7_9", "10_12", "13_15, "16_18", "19_21", "22_25"*/};
        String rootPath = "C:\\Users\\yang\\mir2_data\\monster\\";
        String currentWilFile = null;
        for (String fileName : fileNames) {
            BufferedReader reader=new BufferedReader(new InputStreamReader(new FileInputStream(rootPath + fileName + ".txt"),"UTF-8"));
            String line = null;
            while ((line = reader.readLine()) != null){
                if(line.equals("") || line.startsWith("#")) {
                    System.out.println(line);
                    continue;
                }
                if(line.contains("wil")){
                    String[] split = line.split("\t");
                    if(currentWilFile == null){
                        currentWilFile = split[1];
                        loadWil(currentWilFile);
                    }
                    if(!currentWilFile.equals(split[1])){
                        currentWilFile = split[1];
                        loadWil(currentWilFile);
                    }
                    String[] startAndEnd = split[2].split("-");
                    tag(ColorMappingEnum.YELLOW, 10, 10, Integer.parseInt(startAndEnd[0].trim()),
                            Integer.parseInt(startAndEnd[1].trim()), false);
                }
                else {
                    System.out.println(line);
                    String[] startAndEnd = line.split("-");
                    tag(ColorMappingEnum.YELLOW, 10, 10, Integer.parseInt(startAndEnd[0].trim()),
                            Integer.parseInt(startAndEnd[1].trim()), false);
                }

            }
        }
        //tagDaDao();
        //tagGongJianShour();
        //tagSpecialSmallMonster();

        //tagHair();

        tagDnItem();
        tagItem();
        //colorDetectTag();
    }

    public static void tagHair() throws IOException {
        String wilPath = "C:\\Users\\yang\\Downloads\\Mir2online\\Mir2online\\Data\\Hair.wil";
        WILReader.load(wilPath, true);
        tag(ColorMappingEnum.BLUE, 8,6, 0, WILReader.getImageCount(), true);
    }

    //��
    public static void tagDaDao() throws IOException {
        String wilPath = "C:\\Users\\yang\\Downloads\\Mir2online\\Mir2online\\Data\\Mon1.wil";
        WILReader.load(wilPath, true);
        tag(ColorMappingEnum.CYAN, 10,10, 0, 189, false);
        tag(ColorMappingEnum.CYAN, 10,10, 560,749, false);
        tag(ColorMappingEnum.CYAN, 10,10, 1120, 1309, false);
    }
    public static void tagGongJianShour() throws IOException {
        String wilPath = "C:\\Users\\yang\\Downloads\\Mir2online\\Mir2online\\Data\\Mon8.wil";
        WILReader.load(wilPath, true);
        tag(ColorMappingEnum.CYAN, 10,10, 225, 418, false);
    }

    public static void tagSpecialSmallMonster() throws IOException {
        //����
        try {
            WILReader.load("C:\\Users\\yang\\Downloads\\Mir2online\\Mir2online\\Data\\Mon3.wil", true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //���
        tag(ColorMappingEnum.RED, 10, 10, 964, 967, false);
        tag(ColorMappingEnum.RED, 10, 10, 973, 976, false);
        tag(ColorMappingEnum.RED, 10, 10, 982, 985, false);
        tag(ColorMappingEnum.RED, 10, 10, 991, 994, false);
        tag(ColorMappingEnum.RED, 10, 10, 1000, 1003, false);
        tag(ColorMappingEnum.RED, 10, 10, 1036, 1147, false);
        //����
        tag(ColorMappingEnum.MAGENTA, 10, 10, 1148, 1179, false);
    }

    public static void tagDnItem() throws IOException {
        String tagCateFilePath = "C:\\Users\\yang\\mir2_data\\monster\\dnitems.txt";
        String dnitemWilPath = "C:\\Users\\yang\\Downloads\\Mir2online\\Mir2online\\Data\\Dnitems.wil";
        WILReader.load(dnitemWilPath, true);
        InputStreamReader isr = new InputStreamReader(new FileInputStream(tagCateFilePath), "gbk");
        BufferedReader reader = new BufferedReader(isr);
        //BufferedReader reader = new BufferedReader(new FileReader(new File(tagCateFilePath)));
        String line = null;
        Map<String, ColorMappingEnum> map = new HashMap<>();
        initDnItemColorCategory(map);
        while ((line = reader.readLine()) != null){
            if(line.contains("?"))
                continue;
            String[] split = line.split("\t");
            int startIndex = -1;
            int endIndex = -1;
            if(split[0].contains("-")){
                String[] split1 = split[0].split("-");
                startIndex = Integer.parseInt(split1[0]);
                endIndex = Integer.parseInt(split1[1]);
            }
            else {
                startIndex = Integer.parseInt(split[0]);
                endIndex = startIndex;
            }
            ColorMappingEnum color = map.get(split[split.length-1]);
            //System.out.println(startIndex + "   " + endIndex + "    " + color);
            tagUnique(color, 4, 5, 0, 0, startIndex, endIndex);
        }
    }

    public static void tagItem() throws IOException {
        String tagCateFilePath = "C:\\Users\\yang\\mir2_data\\monster\\items.txt";
        String itemWilPath = "C:\\Users\\yang\\Downloads\\Mir2online\\Mir2online\\Data\\Items.wil";
        WILReader.load(itemWilPath, true);
        InputStreamReader isr = new InputStreamReader(new FileInputStream(tagCateFilePath), "gbk");
        BufferedReader reader = new BufferedReader(isr);
        String line = null;
        Map<String, ColorMappingEnum> colorMap = new HashMap<>();
        Map<String, Point> sizeMap = new HashMap<>();
        initItemCategoryColor(colorMap);
        initItemCategorySize(sizeMap);
        while ((line = reader.readLine()) != null){
            if(line.contains("?"))
                continue;;
            String[] split = line.split("\t");
            int startIndex = -1;
            int endIndex = -1;
            if(split[0].contains("-")){
                String[] split1 = split[0].split("-");
                startIndex = Integer.parseInt(split1[0]);
                endIndex = Integer.parseInt(split1[1]);
            }
            else {
                startIndex = Integer.parseInt(split[0]);
                endIndex = startIndex;
            }
            ColorMappingEnum color = colorMap.get(split[split.length - 1]);
            Point size = sizeMap.get(split[split.length - 1]);
            //System.out.println(line + " " + startIndex + "   " + endIndex + "    " + color + "   " + size);
            tagUnique(color, size.x, size.y, 0, 0, startIndex, endIndex);
        }
    }

    public static void initDnItemColorCategory(Map<String, ColorMappingEnum> map){
        map.put("ҩƷ", ColorMappingEnum.PINK);
        map.put("���", ColorMappingEnum.GRAY_YELLOW);
        map.put("�߼�ֵ", ColorMappingEnum.DARK_RED);
        map.put("ֵǮ�̵���Ʒ", ColorMappingEnum.NAVY_BLUE);
        map.put("�����̵���Ʒ", ColorMappingEnum.ORANGE);
        map.put("�س��������", ColorMappingEnum.QING_BI_SE);
        map.put("����", ColorMappingEnum.WHITE);
        map.put("����", ColorMappingEnum.GRAY);
    }

    public static void initItemCategoryColor(Map<String, ColorMappingEnum> map){
        map.put("HPС��", ColorMappingEnum.PINK);
        map.put("HPС��", ColorMappingEnum.PINK);
        map.put("HP�е�", ColorMappingEnum.PINK);
        map.put("HP����", ColorMappingEnum.PINK);
        map.put("HP��", ColorMappingEnum.PINK);
        map.put("HP����", ColorMappingEnum.PINK);
        map.put("HP����", ColorMappingEnum.PINK);
        map.put("HP����", ColorMappingEnum.PINK);

        map.put("MPС��", ColorMappingEnum.BABY_BLUE);
        map.put("MPС��", ColorMappingEnum.BABY_BLUE);
        map.put("MP�е�", ColorMappingEnum.BABY_BLUE);
        map.put("MP����", ColorMappingEnum.BABY_BLUE);
        map.put("MP��", ColorMappingEnum.BABY_BLUE);
        map.put("MP����", ColorMappingEnum.BABY_BLUE);
        map.put("MP����", ColorMappingEnum.BABY_BLUE);
        map.put("MP����", ColorMappingEnum.BABY_BLUE);

        map.put("̫��ˮС", ColorMappingEnum.GRAY_YELLOW);
        map.put("̫��ˮ��", ColorMappingEnum.GRAY_YELLOW);

        map.put("ֵǮ��ָ", ColorMappingEnum.NAVY_BLUE);
        map.put("ֵǮ����", ColorMappingEnum.NAVY_BLUE);
        map.put("ֵǮ����", ColorMappingEnum.NAVY_BLUE);
        map.put("ֵǮͷ��", ColorMappingEnum.NAVY_BLUE);
        map.put("ֵǮ����", ColorMappingEnum.NAVY_BLUE);
        map.put("ֵǮ����", ColorMappingEnum.NAVY_BLUE);

        map.put("������ָ", ColorMappingEnum.ORANGE);
        map.put("��������", ColorMappingEnum.ORANGE);
        map.put("��������", ColorMappingEnum.ORANGE);
        map.put("����ͷ��", ColorMappingEnum.ORANGE);
        map.put("��������", ColorMappingEnum.ORANGE);
        map.put("��������", ColorMappingEnum.ORANGE);

        map.put("ֵǮ��ʯ", ColorMappingEnum.BLUE);
        map.put("������ʯ", ColorMappingEnum.YELLOW);

        map.put("�سǵ�", ColorMappingEnum.LIME_GREEN);
        map.put("�س���", ColorMappingEnum.LIME_GREEN);
        map.put("�����", ColorMappingEnum.DARK_GREEN);
        map.put("�����", ColorMappingEnum.DARK_GREEN);
        map.put("���ε�", ColorMappingEnum.QING_BI_SE);
        map.put("������", ColorMappingEnum.QING_BI_SE);
        map.put("�лᵥ", ColorMappingEnum.CYAN);
        map.put("�л���", ColorMappingEnum.CYAN);

        map.put("�߼�ֵ", ColorMappingEnum.DARK_RED);

        map.put("������", ColorMappingEnum.MAGENTA);

        map.put("����", ColorMappingEnum.WHITE);
        map.put("����", ColorMappingEnum.GRAY);
        map.put("����", ColorMappingEnum.WHITE);
    }

    public static void initItemCategorySize(Map<String, Point> map){
        map.put("HPС��", new Point(4, 4));
        map.put("HPС��", new Point(16, 16));
        map.put("HP�е�", new Point(7, 7));
        map.put("HP����", new Point(19, 19));
        map.put("HP��", new Point(10, 10));
        map.put("HP����", new Point(21, 21));
        map.put("HP����", new Point(13, 13));
        map.put("HP����", new Point(24, 24));

        map.put("MPС��", new Point(4, 4));
        map.put("MPС��", new Point(16, 16));
        map.put("MP�е�", new Point(7, 7));
        map.put("MP����", new Point(19, 19));
        map.put("MP��", new Point(10, 10));
        map.put("MP����", new Point(21, 21));
        map.put("MP����", new Point(13, 13));
        map.put("MP����", new Point(24, 24));

        map.put("̫��ˮС", new Point(8, 8));
        map.put("̫��ˮ��", new Point(10, 10));

        map.put("ֵǮ��ָ", new Point(4, 4));
        map.put("ֵǮ����", new Point(7, 7));
        map.put("ֵǮ����", new Point(10, 10));
        map.put("ֵǮͷ��", new Point(13, 13));
        map.put("ֵǮ����", new Point(16, 16));
        map.put("ֵǮ����", new Point(19, 19));

        map.put("������ָ", new Point(4, 4));
        map.put("��������", new Point(7, 7));
        map.put("��������", new Point(10, 10));
        map.put("����ͷ��", new Point(13, 13));
        map.put("��������", new Point(16, 16));
        map.put("��������", new Point(19, 19));

        map.put("ֵǮ��ʯ", new Point(8, 8));
        map.put("������ʯ", new Point(8, 8));

        map.put("�سǵ�", new Point(8, 8));
        map.put("�س���", new Point(12, 12));
        map.put("�����", new Point(8, 8));
        map.put("�����", new Point(12, 12));
        map.put("���ε�", new Point(8, 8));
        map.put("������", new Point(12, 12));
        map.put("�лᵥ", new Point(8, 8));
        map.put("�л���", new Point(12, 12));

        map.put("�߼�ֵ", new Point(8, 8));

        map.put("������", new Point(8, 8));

        map.put("����", new Point(8, 8));
        map.put("����", new Point(8, 8));
        map.put("����", new Point(12, 12));
    }

    public static void tagSmallBag() throws IOException {
        WILReader.load("C:\\Users\\yang\\Downloads\\Mir2online\\Mir2online\\Data\\Prguse.wil", true);
        Point[] MIR2_SMALL_BAG_RELATIVE_POSITON = {new Point(281, 56), new Point(324, 56), new Point(367, 56),
                new Point(411, 56), new Point(455, 56), new Point(498, 56)};
        int MIR2_SMALL_BAG_WIDTH = 39;
        int MIR2_SMALL_BAG_HEIGHT = 31;
        for (int i = 0; i < MIR2_SMALL_BAG_RELATIVE_POSITON.length; i++) {
            WILReader.changeImageUnique(1, ColorMappingEnum.WHITE,
                    MIR2_SMALL_BAG_WIDTH, MIR2_SMALL_BAG_HEIGHT,
                    MIR2_SMALL_BAG_RELATIVE_POSITON[i].x, MIR2_SMALL_BAG_RELATIVE_POSITON[i].y);
        }

        for (int i = 0; i < MIR2_SMALL_BAG_RELATIVE_POSITON.length; i++) {
            WILReader.changeImageUnique(2, ColorMappingEnum.WHITE,
                    MIR2_SMALL_BAG_WIDTH, MIR2_SMALL_BAG_HEIGHT,
                    MIR2_SMALL_BAG_RELATIVE_POSITON[i].x, MIR2_SMALL_BAG_RELATIVE_POSITON[i].y);
        }
    }

    public static void tag(ColorMappingEnum color, int width, int height, int startId, int endId, boolean isHair) throws IOException {
        for (int i = startId; i <= endId; i++) {
            WILReader.changeImage(i, color, width, height, isHair);
        }
    }

    public static void tagUnique(ColorMappingEnum color, int width, int height, int startX, int startY, int startId, int endId){
        for (int i = startId; i <= endId; i++) {
            try {
                WILReader.changeImageUnique(i, color, width, height, startX, startY);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean loadWil(String fileName){
        String rootPath = "C:\\Users\\yang\\Downloads\\Mir2online\\Mir2online\\Data\\";
        try {
            WILReader.load(rootPath + fileName, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return WILReader.isLoad();
    }

    public static void colorDetectTag(){
        String path = "C:\\Users\\yang\\Downloads\\Mir2online\\Mir2online\\Data\\Items.wil";
        try {
            WILReader.load(path, true);
            WILReader.changeImageForColorDetect(312, ColorMappingEnum.MAGENTA, 16, 16, 0, 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
