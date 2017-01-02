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

    //大刀
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
        //洞蛆
        try {
            WILReader.load("C:\\Users\\yang\\Downloads\\Mir2online\\Mir2online\\Data\\Mon3.wil", true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //活的
        tag(ColorMappingEnum.RED, 10, 10, 964, 967, false);
        tag(ColorMappingEnum.RED, 10, 10, 973, 976, false);
        tag(ColorMappingEnum.RED, 10, 10, 982, 985, false);
        tag(ColorMappingEnum.RED, 10, 10, 991, 994, false);
        tag(ColorMappingEnum.RED, 10, 10, 1000, 1003, false);
        tag(ColorMappingEnum.RED, 10, 10, 1036, 1147, false);
        //死的
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
        map.put("药品", ColorMappingEnum.PINK);
        map.put("金币", ColorMappingEnum.GRAY_YELLOW);
        map.put("高价值", ColorMappingEnum.DARK_RED);
        map.put("值钱商店物品", ColorMappingEnum.NAVY_BLUE);
        map.put("垃圾商店物品", ColorMappingEnum.ORANGE);
        map.put("回城随机地牢", ColorMappingEnum.QING_BI_SE);
        map.put("栗子", ColorMappingEnum.WHITE);
        map.put("材料", ColorMappingEnum.GRAY);
    }

    public static void initItemCategoryColor(Map<String, ColorMappingEnum> map){
        map.put("HP小单", ColorMappingEnum.PINK);
        map.put("HP小捆", ColorMappingEnum.PINK);
        map.put("HP中单", ColorMappingEnum.PINK);
        map.put("HP中捆", ColorMappingEnum.PINK);
        map.put("HP大单", ColorMappingEnum.PINK);
        map.put("HP大捆", ColorMappingEnum.PINK);
        map.put("HP超单", ColorMappingEnum.PINK);
        map.put("HP超捆", ColorMappingEnum.PINK);

        map.put("MP小单", ColorMappingEnum.BABY_BLUE);
        map.put("MP小捆", ColorMappingEnum.BABY_BLUE);
        map.put("MP中单", ColorMappingEnum.BABY_BLUE);
        map.put("MP中捆", ColorMappingEnum.BABY_BLUE);
        map.put("MP大单", ColorMappingEnum.BABY_BLUE);
        map.put("MP大捆", ColorMappingEnum.BABY_BLUE);
        map.put("MP超单", ColorMappingEnum.BABY_BLUE);
        map.put("MP超捆", ColorMappingEnum.BABY_BLUE);

        map.put("太阳水小", ColorMappingEnum.GRAY_YELLOW);
        map.put("太阳水大", ColorMappingEnum.GRAY_YELLOW);

        map.put("值钱戒指", ColorMappingEnum.NAVY_BLUE);
        map.put("值钱手镯", ColorMappingEnum.NAVY_BLUE);
        map.put("值钱项链", ColorMappingEnum.NAVY_BLUE);
        map.put("值钱头盔", ColorMappingEnum.NAVY_BLUE);
        map.put("值钱盔甲", ColorMappingEnum.NAVY_BLUE);
        map.put("值钱武器", ColorMappingEnum.NAVY_BLUE);

        map.put("垃圾戒指", ColorMappingEnum.ORANGE);
        map.put("垃圾手镯", ColorMappingEnum.ORANGE);
        map.put("垃圾项链", ColorMappingEnum.ORANGE);
        map.put("垃圾头盔", ColorMappingEnum.ORANGE);
        map.put("垃圾盔甲", ColorMappingEnum.ORANGE);
        map.put("垃圾武器", ColorMappingEnum.ORANGE);

        map.put("值钱矿石", ColorMappingEnum.BLUE);
        map.put("垃圾矿石", ColorMappingEnum.YELLOW);

        map.put("回城单", ColorMappingEnum.LIME_GREEN);
        map.put("回城捆", ColorMappingEnum.LIME_GREEN);
        map.put("随机单", ColorMappingEnum.DARK_GREEN);
        map.put("随机捆", ColorMappingEnum.DARK_GREEN);
        map.put("地牢单", ColorMappingEnum.QING_BI_SE);
        map.put("地牢捆", ColorMappingEnum.QING_BI_SE);
        map.put("行会单", ColorMappingEnum.CYAN);
        map.put("行会捆", ColorMappingEnum.CYAN);

        map.put("高价值", ColorMappingEnum.DARK_RED);

        map.put("技能书", ColorMappingEnum.MAGENTA);

        map.put("栗子", ColorMappingEnum.WHITE);
        map.put("材料", ColorMappingEnum.GRAY);
        map.put("其它", ColorMappingEnum.WHITE);
    }

    public static void initItemCategorySize(Map<String, Point> map){
        map.put("HP小单", new Point(4, 4));
        map.put("HP小捆", new Point(16, 16));
        map.put("HP中单", new Point(7, 7));
        map.put("HP中捆", new Point(19, 19));
        map.put("HP大单", new Point(10, 10));
        map.put("HP大捆", new Point(21, 21));
        map.put("HP超单", new Point(13, 13));
        map.put("HP超捆", new Point(24, 24));

        map.put("MP小单", new Point(4, 4));
        map.put("MP小捆", new Point(16, 16));
        map.put("MP中单", new Point(7, 7));
        map.put("MP中捆", new Point(19, 19));
        map.put("MP大单", new Point(10, 10));
        map.put("MP大捆", new Point(21, 21));
        map.put("MP超单", new Point(13, 13));
        map.put("MP超捆", new Point(24, 24));

        map.put("太阳水小", new Point(8, 8));
        map.put("太阳水大", new Point(10, 10));

        map.put("值钱戒指", new Point(4, 4));
        map.put("值钱手镯", new Point(7, 7));
        map.put("值钱项链", new Point(10, 10));
        map.put("值钱头盔", new Point(13, 13));
        map.put("值钱盔甲", new Point(16, 16));
        map.put("值钱武器", new Point(19, 19));

        map.put("垃圾戒指", new Point(4, 4));
        map.put("垃圾手镯", new Point(7, 7));
        map.put("垃圾项链", new Point(10, 10));
        map.put("垃圾头盔", new Point(13, 13));
        map.put("垃圾盔甲", new Point(16, 16));
        map.put("垃圾武器", new Point(19, 19));

        map.put("值钱矿石", new Point(8, 8));
        map.put("垃圾矿石", new Point(8, 8));

        map.put("回城单", new Point(8, 8));
        map.put("回城捆", new Point(12, 12));
        map.put("随机单", new Point(8, 8));
        map.put("随机捆", new Point(12, 12));
        map.put("地牢单", new Point(8, 8));
        map.put("地牢捆", new Point(12, 12));
        map.put("行会单", new Point(8, 8));
        map.put("行会捆", new Point(12, 12));

        map.put("高价值", new Point(8, 8));

        map.put("技能书", new Point(8, 8));

        map.put("栗子", new Point(8, 8));
        map.put("材料", new Point(8, 8));
        map.put("其它", new Point(12, 12));
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
