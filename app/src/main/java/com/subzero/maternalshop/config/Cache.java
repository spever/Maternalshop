package com.subzero.maternalshop.config;

import java.io.File;

import com.subzero.common.utils.SDCardUtil;

import android.os.Environment;

public class Cache {

	public static final String split = File.separator;
	public static final String rootPath = Environment
			.getExternalStorageDirectory().getAbsolutePath() + split;
	public static final String cacheDir = rootPath + "附近逛" + split;
	public static final int ramCacheSize = 32 * 1024 * 1024;
	public static final int sdCacheSize = 128 * 1024 * 1024;
	//
	public static final String dbDir = rootPath + "附近逛";
	public static final String dbName = "collection.db";
	public static final int dbVersion = 1;

	/** 保存在 附近逛/tmp.java 下 */
	public static void saveTmpFile(String result) {
		// SDCardUtil.saveFile(result.getBytes(), Cache.cacheDir+"tmp.java", false);
	}

	/** 保存在 附近逛/tmp.java 下 */
	public static void saveActivityLifeCycle(String result, boolean append) {
		// SDCardUtil.saveFile(result.getBytes(), Cache.cacheDir+"activityLifeCycle.java", append);
	}

}
