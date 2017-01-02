package org.joot.mir2.tools.imageviewer;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class WILExport {

	public static void main(String[] args) throws IOException {
		WILReader.load("C:\\Users\\yang\\Pictures\\color_found_test\\Hum.wil", true);
		String rootPath = "C:\\Users\\yang\\Downloads\\Mir2online\\Ã×¶ûÔÚÏß\\Data";
		String outptRootPath = "C:\\Users\\yang\\mir2_data\\monster\\picture\\";
		File dirFile = new File(rootPath);
		File[] files = dirFile.listFiles();
		for (File file : files) {
			if(!file.getName().endsWith(".wil"))
				continue;
			String fileName = file.getName();
			File file1 = new File(outptRootPath + fileName);
			if(!file1.exists()){
				file1.mkdir();
			}
			WILReader.load(file.getPath(), true);
			for (int i = 0; i < WILReader.getImageCount()-1; i++) {
				ImageIO.write(WILReader.getOneImage(i), "png",
						new File(outptRootPath + fileName + "\\" + i + ".png"));
			}
		}
	}


}