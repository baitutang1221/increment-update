package com.itfitness.incrementupdatedemo;

public class PatchUtil {
    static {
        System.loadLibrary("native-lib");
    }
    /**
     * 合并APK文件
     * @param oldApkFile 旧APK文件路径
     * @param newApkFile 新APK文件路径（存储生成的APK的路径）
     * @param patchFile 差异文件
     */
    public native static void patchAPK(String oldApkFile,String newApkFile,String patchFile);
}
