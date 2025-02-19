/*
 * Tencent is pleased to support the open source community by making Tinker available.
 *
 * Copyright (C) 2016 THL A29 Limited, a Tencent company. All rights reserved.
 *
 * Licensed under the BSD 3-Clause License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * https://opensource.org/licenses/BSD-3-Clause
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tencent.tinker.lib.patch;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.os.SystemClock;

import com.tencent.tinker.commons.dexpatcher.DexPatchApplier;
import com.tencent.tinker.commons.util.DigestUtil;
import com.tencent.tinker.commons.util.IOHelper;
import com.tencent.tinker.lib.tinker.Tinker;
import com.tencent.tinker.loader.shareutil.ShareTinkerLog;
import com.tencent.tinker.loader.TinkerDexOptimizer;
import com.tencent.tinker.loader.TinkerRuntimeException;
import com.tencent.tinker.loader.shareutil.ShareConstants;
import com.tencent.tinker.loader.shareutil.ShareDexDiffPatchInfo;
import com.tencent.tinker.loader.shareutil.ShareElfFile;
import com.tencent.tinker.loader.shareutil.SharePatchFileUtil;
import com.tencent.tinker.loader.shareutil.ShareSecurityCheck;
import com.tencent.tinker.loader.shareutil.ShareTinkerInternals;
import com.tencent.tinker.ziputils.ziputil.AlignedZipOutputStream;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Created by zhangshaowen on 16/4/12.
 */
public class DexDiffPatchInternal extends BasePatchInternal {
    protected static final String TAG = "Tinker.DexDiffPatchInternal";

    protected static final int WAIT_ASYN_OAT_TIME = 10 * 1000;
    protected static final int MAX_WAIT_COUNT     = 120;


    private static ArrayList<File>                      optFiles      = new ArrayList<>();
    private static ArrayList<ShareDexDiffPatchInfo>     patchList     = new ArrayList<>();
    private static HashMap<ShareDexDiffPatchInfo, File> classNDexInfo = new HashMap<>();
    private static boolean                              isVmArt       = ShareTinkerInternals.isVmArt();


   // patchFile = //data/user/0/tinker.sample.android/tinker/patch-e08e3479/patch-e08e3479.apk
    protected static boolean tryRecoverDexFiles(Tinker manager, ShareSecurityCheck checker, Context context,
                                                String patchVersionDirectory, File patchFile) {
        if (!manager.isEnabledForDex()) {
            ShareTinkerLog.w(TAG, "patch recover, dex is not enabled");
            return true;
        }
        //判断patch.apk的 assets/dex_meta.txt 文件是否存在，从而判断dex文件是否存在
        //apk的包含的所有dex文件信息 都会被写入上面的txt文件
        //classes2.dex,,b9b3aa26cbf23eaa18b788c2b27d336a,b9b3aa26cbf23eaa18b788c2b27d336a,c7076a901154849350c355b72b62563f,3398244590,187366934,jar
        //classes.dex,,644d45b04ec163f9886e030a005dc145,644d45b04ec163f9886e030a005dc145,f41bee75d4d19a0790265d6e2884feab,2178435494,1325770517,jar
        //test.dex,,56900442eb5b7e1de45449d0685e6e00,56900442eb5b7e1de45449d0685e6e00,0,0,0,jar
        String dexMeta = checker.getMetaContentMap().get(DEX_META_FILE);

        if (dexMeta == null) {
            ShareTinkerLog.w(TAG, "patch recover, dex is not contained");
            return true;
        }

        long begin = SystemClock.elapsedRealtime();
        boolean result = patchDexExtractViaDexDiff(context, patchVersionDirectory, dexMeta, patchFile);
        long cost = SystemClock.elapsedRealtime() - begin;
        ShareTinkerLog.i(TAG, "recover dex result:%b, cost:%d", result, cost);
        return result;
    }

    protected static boolean waitAndCheckDexOptFile(File patchFile, Tinker manager) {
        if (optFiles.isEmpty()) {
            return true;
        }
        // should use patch list size
        int size = patchList.size() * 30;
        if (size > MAX_WAIT_COUNT) {
            size = MAX_WAIT_COUNT;
        }
        ShareTinkerLog.i(TAG, "raw dex count: %d, dex opt dex count: %d, final wait times: %d", patchList.size(), optFiles.size(), size);

        for (int i = 0; i < size; i++) {
            if (!checkAllDexOptFile(optFiles, i + 1)) {
                try {
                    Thread.sleep(WAIT_ASYN_OAT_TIME);
                } catch (InterruptedException e) {
                    ShareTinkerLog.e(TAG, "thread sleep InterruptedException e:" + e);
                }
            }
        }
        List<File> failDexFiles = new ArrayList<>();
        // check again, if still can't be found, just return
        for (File file : optFiles) {
            ShareTinkerLog.i(TAG, "check dex optimizer file exist: %s, size %d", file.getPath(), file.length());

            if (!SharePatchFileUtil.isLegalFile(file) && !SharePatchFileUtil.shouldAcceptEvenIfIllegal(file)) {
                ShareTinkerLog.e(TAG, "final parallel dex optimizer file %s is not exist, return false", file.getName());
                failDexFiles.add(file);
            }
        }
        if (!failDexFiles.isEmpty()) {
            manager.getPatchReporter().onPatchDexOptFail(patchFile, failDexFiles,
                new TinkerRuntimeException(ShareConstants.CHECK_DEX_OAT_EXIST_FAIL));
            return false;
        }
        if (Build.VERSION.SDK_INT >= 21) {
            Throwable lastThrowable = null;
            for (File file : optFiles) {
                if (SharePatchFileUtil.shouldAcceptEvenIfIllegal(file)) {
                    continue;
                }
                ShareTinkerLog.i(TAG, "check dex optimizer file format: %s, size %d", file.getName(), file.length());
                int returnType;
                try {
                    returnType = ShareElfFile.getFileTypeByMagic(file);
                } catch (IOException e) {
                    // read error just continue
                    continue;
                }
                if (returnType == ShareElfFile.FILE_TYPE_ELF) {
                    ShareElfFile elfFile = null;
                    try {
                        elfFile = new ShareElfFile(file);
                    } catch (Throwable e) {
                        ShareTinkerLog.e(TAG, "final parallel dex optimizer file %s is not elf format, return false", file.getName());
                        failDexFiles.add(file);
                        lastThrowable = e;
                    } finally {
                        IOHelper.closeQuietly(elfFile);
                    }
                }
            }
            if (!failDexFiles.isEmpty()) {
                Throwable returnThrowable = lastThrowable == null
                    ? new TinkerRuntimeException(ShareConstants.CHECK_DEX_OAT_FORMAT_FAIL)
                    : new TinkerRuntimeException(ShareConstants.CHECK_DEX_OAT_FORMAT_FAIL, lastThrowable);

                manager.getPatchReporter().onPatchDexOptFail(patchFile, failDexFiles,
                    returnThrowable);
                return false;
            }
        }
        return true;
    }                              //patchFile =/data/user/0/tinker.sample.android/tinker/patch-e08e3479.apk
                                  //patchVersionDirectory = /data/user/0/tinker.sample.android/tinker/patch-e08e3479
    private static boolean patchDexExtractViaDexDiff(Context context, String patchVersionDirectory, String meta, final File patchFile) {
        String dir = patchVersionDirectory + "/" + DEX_PATH + "/";
        //dir = /data/user/0/tinker.sample.android/tinker/patch-e08e3479/dex/

        //真正和已安装包进行合并了
        if (!extractDexDiffInternals(context, dir, meta, patchFile, TYPE_DEX)) {
            ShareTinkerLog.w(TAG, "patch recover, extractDiffInternals fail");
            return false;
        }

        File dexFiles = new File(dir);
        File[] files = dexFiles.listFiles();
        List<File> legalFiles = new ArrayList<>();
        if (files != null) {
            for (File file : files) {
                final String fileName = file.getName();
                // may have directory in android o
                if (file.isFile()
                    &&  (fileName.endsWith(ShareConstants.DEX_SUFFIX)
                      || fileName.endsWith(ShareConstants.JAR_SUFFIX)
                      || fileName.endsWith(ShareConstants.PATCH_SUFFIX))
                ) {
                    legalFiles.add(file);
                }
            }
        }

        ShareTinkerLog.i(TAG, "legal files to do dexopt: " + legalFiles);
        final String optimizeDexDirectory = patchVersionDirectory + "/" + DEX_OPTIMIZE_PATH + "/";
        //optimizeDexDirectory=/data/user/0/tinker.sample.android/tinker/patch-e48bf916/odex/

        // patchFile=/data/user/0/tinker.sample.android/tinker/patch-e48bf916/patch-e48bf916.apk
        // legalFiles=/data/user/0/tinker.sample.android/tinker/patch-e48bf916/dex/tinker_classN.apk
        //合并完成后需要对legalFiles进行优化dex2oat-oat    or    dex2opt-odex.  否则运行时无法找到类
        return dexOptimizeDexFiles(context, legalFiles, optimizeDexDirectory, patchFile);

    }

    private static boolean checkClassNDexFiles(final String dexFilePath) {///data/user/0/tinker.sample.android/tinker/patch-e48bf916/dex/
       if (patchList.isEmpty() || !isVmArt) {
            return false;
        }
        ShareDexDiffPatchInfo testInfo = null;
        File testFile = null;

        for (ShareDexDiffPatchInfo info : patchList) {
            File dexFile = new File(dexFilePath + info.realName);
            String fileName = dexFile.getName();

            if (ShareConstants.CLASS_N_PATTERN.matcher(fileName).matches()) {
                classNDexInfo.put(info, dexFile);
            }
            if (info.rawName.startsWith(ShareConstants.TEST_DEX_NAME)) {
                testInfo = info;
                testFile = dexFile;
            }
        }
        if (testInfo != null) {
            classNDexInfo.put(ShareTinkerInternals.changeTestDexToClassN(testInfo, classNDexInfo.size() + 1), testFile);
        }

        File classNFile = new File(dexFilePath, ShareConstants.CLASS_N_APK_NAME);
        boolean result = true;
        if (classNFile.exists()) {
            for (ShareDexDiffPatchInfo info : classNDexInfo.keySet()) {
                if (!SharePatchFileUtil.verifyDexFileMd5(classNFile, info.rawName, info.destMd5InArt)) {
                    ShareTinkerLog.e(TAG, "verify dex file md5 error, entry name; %s, file len: %d", info.rawName, classNFile.length());
                    result = false;
                    break;
                }
            }
            if (!result) {
                SharePatchFileUtil.safeDeleteFile(classNFile);
            }
        } else {
            result = false;
        }

        if (result) {
            // delete classN dex if exist
            for (File dexFile : classNDexInfo.values()) {
                SharePatchFileUtil.safeDeleteFile(dexFile);
            }
        }

        return result;
    }

    private static ZipEntry makeStoredZipEntry(ZipEntry originalEntry, String realDexName) {
        final ZipEntry result = new ZipEntry(realDexName);
        result.setMethod(ZipEntry.STORED);
        result.setCompressedSize(originalEntry.getSize());
        result.setSize(originalEntry.getSize());
        result.setCrc(originalEntry.getCrc());
        return result;
    }

    private static boolean mergeClassNDexFiles(final Context context, final File patchFile, final String dexFilePath) {
        // only merge for art vm
        if (patchList.isEmpty() || !isVmArt) {
            return true;
        }

        File classNFile = new File(dexFilePath, ShareConstants.CLASS_N_APK_NAME);

        // repack just more than one classN.dex
        if (classNDexInfo.isEmpty()) {
            ShareTinkerLog.w(TAG, "classNDexInfo size: %d, no need to merge classN dex files", classNDexInfo.size());
            return true;
        }
        long start = System.currentTimeMillis();
        boolean result = true;
        AlignedZipOutputStream out = null;
        try {
            out = new AlignedZipOutputStream(new BufferedOutputStream(new FileOutputStream(classNFile)));
            for (ShareDexDiffPatchInfo info : classNDexInfo.keySet()) {
                File dexFile = classNDexInfo.get(info);
                if (info.isJarMode) {
                    ZipFile dexZipFile = null;
                    InputStream inputStream = null;
                    try {
                        dexZipFile = new ZipFile(dexFile);
                        ZipEntry rawDexZipEntry = dexZipFile.getEntry(ShareConstants.DEX_IN_JAR);
                        ZipEntry newDexZipEntry = makeStoredZipEntry(rawDexZipEntry, info.rawName);
                        inputStream = dexZipFile.getInputStream(rawDexZipEntry);
                        try {
                            out.putNextEntry(newDexZipEntry);
                            IOHelper.copyStream(inputStream, out);
                        } finally {
                            out.closeEntry();
                        }
                    } finally {
                        IOHelper.closeQuietly(inputStream);
                        IOHelper.closeQuietly(dexZipFile);
                    }
                } else {
                    ZipEntry newDexZipEntry = new ZipEntry(info.rawName);
                    newDexZipEntry.setMethod(ZipEntry.STORED);
                    newDexZipEntry.setCompressedSize(dexFile.length());
                    newDexZipEntry.setSize(dexFile.length());
                    newDexZipEntry.setCrc(DigestUtil.getCRC32(dexFile));

                    InputStream is = null;
                    try {
                        is = new BufferedInputStream(new FileInputStream(dexFile));
                        try {
                            out.putNextEntry(newDexZipEntry);
                            IOHelper.copyStream(is, out);
                        } finally {
                            out.closeEntry();
                        }
                    } finally {
                        IOHelper.closeQuietly(is);
                    }
                }
            }
        } catch (Throwable throwable) {
            ShareTinkerLog.printErrStackTrace(TAG, throwable, "merge classN file");
            result = false;
        } finally {
            IOHelper.closeQuietly(out);
        }

        if (result) {
            for (ShareDexDiffPatchInfo info : classNDexInfo.keySet()) {
                if (!SharePatchFileUtil.verifyDexFileMd5(classNFile, info.rawName, info.destMd5InArt)) {
                    result = false;
                    ShareTinkerLog.e(TAG, "verify dex file md5 error, entry name; %s, file len: %d", info.rawName, classNFile.length());
                    break;
                }
            }
        }
        if (result) {
            for (File dexFile : classNDexInfo.values()) {
                SharePatchFileUtil.safeDeleteFile(dexFile);
            }
        } else {
            ShareTinkerLog.e(TAG, "merge classN dex error, try delete temp file");
            SharePatchFileUtil.safeDeleteFile(classNFile);
            Tinker.with(context).getPatchReporter().onPatchTypeExtractFail(patchFile, classNFile, classNFile.getName(), TYPE_CLASS_N_DEX);
        }
        ShareTinkerLog.i(TAG, "merge classN dex file %s, result: %b, size: %d, use: %dms",
            classNFile.getPath(), result, classNFile.length(), (System.currentTimeMillis() - start));
        return result;
    }

    private static boolean dexOptimizeDexFiles(Context context, List<File> dexFiles, String optimizeDexDirectory, final File patchFile) {
        final Tinker manager = Tinker.with(context);

        optFiles.clear();
        //dexFiles = [/data/user/0/tinker.sample.android/tinker/patch-e48bf916/dex/tinker_classN.apk]
        if (dexFiles != null) {//patchFile= /data/user/0/tinker.sample.android/tinker/patch-e48bf916/patch-e48bf916.apk
            File optimizeDexDirectoryFile = new File(optimizeDexDirectory);
             //optimizeDexDirectory= /data/user/0/tinker.sample.android/tinker/patch-e48bf916/odex
            if (!optimizeDexDirectoryFile.exists() && !optimizeDexDirectoryFile.mkdirs()) {
                ShareTinkerLog.w(TAG, "patch recover, make optimizeDexDirectoryFile fail");
                return false;
            }
            // add opt files
            for (File file : dexFiles) {
                String outputPathName = SharePatchFileUtil.optimizedPathFor(file, optimizeDexDirectoryFile);
                optFiles.add(new File(outputPathName));
            }

            ShareTinkerLog.i(TAG, "patch recover, try to optimize dex file count:%d, optimizeDexDirectory:%s", dexFiles.size(), optimizeDexDirectory);
            // only use parallel dex optimizer for art
            // for Android O version, it is very strange. If we use parallel dex optimizer, it won't work
            final List<File> failOptDexFile = new Vector<>();
            final Throwable[] throwable = new Throwable[1];

            // try parallel dex optimizer
            TinkerDexOptimizer.optimizeAll(
                context, dexFiles, optimizeDexDirectoryFile,
                new TinkerDexOptimizer.ResultCallback() {
                    long startTime;

                    @Override
                    public void onStart(File dexFile, File optimizedDir) {
                        startTime = System.currentTimeMillis();
                        ShareTinkerLog.i(TAG, "start to parallel optimize dex %s, size: %d", dexFile.getPath(), dexFile.length());
                    }

                    @Override
                    public void onSuccess(File dexFile, File optimizedDir, File optimizedFile) {
                        // Do nothing.
                        ShareTinkerLog.i(TAG, "success to parallel optimize dex %s, opt file:%s, opt file size: %d, use time %d",
                            dexFile.getPath(), optimizedFile.getPath(), optimizedFile.length(), (System.currentTimeMillis() - startTime));
                    }

                    @Override
                    public void onFailed(File dexFile, File optimizedDir, Throwable thr) {
                        ShareTinkerLog.i(TAG, "fail to parallel optimize dex %s use time %d",
                            dexFile.getPath(), (System.currentTimeMillis() - startTime));
                        failOptDexFile.add(dexFile);
                        throwable[0] = thr;
                    }
                }
            );

            if (!failOptDexFile.isEmpty()) {
                manager.getPatchReporter().onPatchDexOptFail(patchFile, failOptDexFile, throwable[0]);
                return false;
            }
        }
        return true;
    }

    /**
     * for ViVo or some other rom, they would make dex2oat asynchronous
     * so we need to check whether oat file is actually generated.
     *
     * @param files
     * @param count
     * @return
     */
    private static boolean checkAllDexOptFile(ArrayList<File> files, int count) {
        for (File file : files) {
            if (!SharePatchFileUtil.isLegalFile(file)) {
                if (SharePatchFileUtil.shouldAcceptEvenIfIllegal(file)) {
                    continue;
                }
                ShareTinkerLog.e(TAG, "parallel dex optimizer file %s is not exist, just wait %d times", file.getName(), count);
                return false;
            }
        }
        return true;
    }

    private static boolean extractDexDiffInternals(Context context, String dir, String meta, File patchFile, int type) {
        //parse
        patchList.clear();
        //shareDexDiffPathInfo->classes2.dex,,e8235a061ef189a2fd8aff9a7040cc68,jar
        //shareDexDiffPathInfo->classes.dex,,6ff5afe4eb799d9e9ea18c8d37b98853,jar
        //shareDexDiffPathInfo->test.dex,,56900442eb5b7e1de45449d0685e6e00,jar
        //解析assets/dex-meta.txt信息，从而得到上面的信息
        ShareDexDiffPatchInfo.parseDexDiffPatchInfo(meta, patchList);

        if (patchList.isEmpty()) {
            ShareTinkerLog.w(TAG, "extract patch list is empty! type:%s:", ShareTinkerInternals.getTypeString(type));
            return true;
        }

        ///dir = data/user/0/tinker.sample.android/tinker/patch-e08e347/dex
        File directory = new File(dir);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        //I think it is better to extract the raw files from apk
        Tinker manager = Tinker.with(context);
        ZipFile apk = null;
        ZipFile patch = null;
        try {
            ApplicationInfo applicationInfo = context.getApplicationInfo();
            if (applicationInfo == null) {
                // Looks like running on a test Context, so just return without patching.
                ShareTinkerLog.w(TAG, "applicationInfo == null!!!!");
                return false;
            }

            //取出本地已安装的apk的路径，这个apk 也包含着对应的dex文件，res,so
            //apkPath = /data/app/tinker.sample.android-iGNJQOnW0oImxHOUae2Y8Q==/base.apk
            String apkPath = applicationInfo.sourceDir;
            //把base.apk 和pacth.apk 都转换成zip然后才能解压拿到里面的每一个dex文件
            apk = new ZipFile(apkPath);
            patch = new ZipFile(patchFile);
            if (checkClassNDexFiles(dir)) {
                ShareTinkerLog.w(TAG, "class n dex file %s is already exist, and md5 match, just continue", ShareConstants.CLASS_N_APK_NAME);
                return true;
            }
            //遍历patchList，
            for (ShareDexDiffPatchInfo info : patchList) {
                long start = System.currentTimeMillis();

                final String infoPath = info.path;
                String patchRealPath;//classes.dex,  classes1.dex.
                if (infoPath.equals("")) {
                    patchRealPath = info.rawName;
                } else {
                    patchRealPath = info.path + "/" + info.rawName;
                }

                //取出patch.apk中每个dex的mdf ,crc 一些文件完整性的参数
                String dexDiffMd5 = info.dexDiffMd5;
                String oldDexCrc = info.oldDexCrC;

                if (!isVmArt && info.destMd5InDvm.equals("0")) {
                    ShareTinkerLog.w(TAG, "patch dex %s is only for art, just continue", patchRealPath);
                    continue;
                }
                String extractedFileMd5 = isVmArt ? info.destMd5InArt : info.destMd5InDvm;

                if (!SharePatchFileUtil.checkIfMd5Valid(extractedFileMd5)) {
                    ShareTinkerLog.w(TAG, "meta file md5 invalid, type:%s, name: %s, md5: %s", ShareTinkerInternals.getTypeString(type), info.rawName, extractedFileMd5);
                    manager.getPatchReporter().onPatchPackageCheckFail(patchFile, BasePatchInternal.getMetaCorruptedCode(type));
                    return false;
                }

                //构建合成的jar文件的 [patch.apk->classes.dex]  +  [base.apk->classes.dex] = classes.dex.jar
                //dir = /data/user/0/tinker.sample.android/tinker/patch-e08e3479/dex/
                File extractedFile = new File(dir + info.realName);

                //check file whether already exist
                if (extractedFile.exists()) {
                    //如果合成后的文件已经存在则直接return 了
                    if (SharePatchFileUtil.verifyDexFileMd5(extractedFile, extractedFileMd5)) {
                        //it is ok, just continue
                        ShareTinkerLog.w(TAG, "dex file %s is already exist, and md5 match, just continue", extractedFile.getPath());
                        continue;
                    } else {
                        ShareTinkerLog.w(TAG, "have a mismatch corrupted dex " + extractedFile.getPath());
                        extractedFile.delete();
                    }
                } else {
                    extractedFile.getParentFile().mkdirs();
                }

                //从patch.apk对应的zip包中取出classes.dex文件
                ZipEntry patchFileEntry = patch.getEntry(patchRealPath);
                //分别从baseApk.apk对应的zip包中取出classes.dex文件
                ZipEntry rawApkFileEntry = apk.getEntry(patchRealPath);

                if (oldDexCrc.equals("0")) {
                    if (patchFileEntry == null) {
                        ShareTinkerLog.w(TAG, "patch entry is null. path:" + patchRealPath);
                        manager.getPatchReporter().onPatchTypeExtractFail(patchFile, extractedFile, info.rawName, type);
                        return false;
                    }

                    //it is a new file, but maybe we need to repack the dex file
                    if (!extractDexFile(patch, patchFileEntry, extractedFile, info)) {
                        ShareTinkerLog.w(TAG, "Failed to extract raw patch file " + extractedFile.getPath());
                        manager.getPatchReporter().onPatchTypeExtractFail(patchFile, extractedFile, info.rawName, type);
                        return false;
                    }
                } else if (dexDiffMd5.equals("0")) {
                    // skip process old dex for real dalvik vm
                    if (!isVmArt) {
                        continue;
                    }

                    if (rawApkFileEntry == null) {
                        ShareTinkerLog.w(TAG, "apk entry is null. path:" + patchRealPath);
                        manager.getPatchReporter().onPatchTypeExtractFail(patchFile, extractedFile, info.rawName, type);
                        return false;
                    }

                    //check source crc instead of md5 for faster
                    String rawEntryCrc = String.valueOf(rawApkFileEntry.getCrc());
                    if (!rawEntryCrc.equals(oldDexCrc)) {
                        ShareTinkerLog.e(TAG, "apk entry %s crc is not equal, expect crc: %s, got crc: %s", patchRealPath, oldDexCrc, rawEntryCrc);
                        manager.getPatchReporter().onPatchTypeExtractFail(patchFile, extractedFile, info.rawName, type);
                        return false;
                    }

                    // Small patched dex generating strategy was disabled, we copy full original dex directly now.
                    //patchDexFile(apk, patch, rawApkFileEntry, null, info, smallPatchInfoFile, extractedFile);
                    extractDexFile(apk, rawApkFileEntry, extractedFile, info);

                    if (!SharePatchFileUtil.verifyDexFileMd5(extractedFile, extractedFileMd5)) {
                        ShareTinkerLog.w(TAG, "Failed to recover dex file when verify patched dex: " + extractedFile.getPath());
                        manager.getPatchReporter().onPatchTypeExtractFail(patchFile, extractedFile, info.rawName, type);
                        SharePatchFileUtil.safeDeleteFile(extractedFile);
                        return false;
                    }
                } else {
                    if (patchFileEntry == null) {
                        ShareTinkerLog.w(TAG, "patch entry is null. path:" + patchRealPath);
                        manager.getPatchReporter().onPatchTypeExtractFail(patchFile, extractedFile, info.rawName, type);
                        return false;
                    }

                    if (!SharePatchFileUtil.checkIfMd5Valid(dexDiffMd5)) {
                        ShareTinkerLog.w(TAG, "meta file md5 invalid, type:%s, name: %s, md5: %s", ShareTinkerInternals.getTypeString(type), info.rawName, dexDiffMd5);
                        manager.getPatchReporter().onPatchPackageCheckFail(patchFile, BasePatchInternal.getMetaCorruptedCode(type));
                        return false;
                    }

                    if (rawApkFileEntry == null) {
                        ShareTinkerLog.w(TAG, "apk entry is null. path:" + patchRealPath);
                        manager.getPatchReporter().onPatchTypeExtractFail(patchFile, extractedFile, info.rawName, type);
                        return false;
                    }
                    //check source crc instead of md5 for faster
                    String rawEntryCrc = String.valueOf(rawApkFileEntry.getCrc());
                    if (!rawEntryCrc.equals(oldDexCrc)) {
                        ShareTinkerLog.e(TAG, "apk entry %s crc is not equal, expect crc: %s, got crc: %s", patchRealPath, oldDexCrc, rawEntryCrc);
                        manager.getPatchReporter().onPatchTypeExtractFail(patchFile, extractedFile, info.rawName, type);
                        return false;
                    }

                    //下面是dex文件真正合成的地方
                    patchDexFile(apk, patch, rawApkFileEntry, patchFileEntry, info, extractedFile);

                    if (!SharePatchFileUtil.verifyDexFileMd5(extractedFile, extractedFileMd5)) {
                        ShareTinkerLog.w(TAG, "Failed to recover dex file when verify patched dex: " + extractedFile.getPath());
                        manager.getPatchReporter().onPatchTypeExtractFail(patchFile, extractedFile, info.rawName, type);
                        SharePatchFileUtil.safeDeleteFile(extractedFile);
                        return false;
                    }

                    ShareTinkerLog.w(TAG, "success recover dex file: %s, size: %d, use time: %d",
                        extractedFile.getPath(), extractedFile.length(), (System.currentTimeMillis() - start));
                }
            }
            //把刚才合并生成的classes.dex.jar classes1.dex.jar classes2.jar 再打包生成tinker_classN.apk。
            //这个文件会在下次启动时，当做动态加载的文件被load进来
            if (!mergeClassNDexFiles(context, patchFile, dir)) {
                return false;
            }
        } catch (Throwable e) {
            throw new TinkerRuntimeException("patch " + ShareTinkerInternals.getTypeString(type) + " extract failed (" + e.getMessage() + ").", e);
        } finally {
            SharePatchFileUtil.closeZip(apk);
            SharePatchFileUtil.closeZip(patch);
        }
        return true;
    }

    /**
     * repack dex to jar
     *
     * @param zipFile
     * @param entryFile
     * @param extractTo
     * @param targetMd5
     * @return boolean
     * @throws IOException
     */
    private static boolean extractDexToJar(ZipFile zipFile, ZipEntry entryFile, File extractTo, String targetMd5) throws IOException {
        int numAttempts = 0;
        boolean isExtractionSuccessful = false;
        while (numAttempts < MAX_EXTRACT_ATTEMPTS && !isExtractionSuccessful) {
            numAttempts++;

            ZipOutputStream zos = null;
            BufferedInputStream bis = null;

            ShareTinkerLog.i(TAG, "try Extracting " + extractTo.getPath());
            try {
                zos = new ZipOutputStream(new
                    BufferedOutputStream(new FileOutputStream(extractTo)));
                bis = new BufferedInputStream(zipFile.getInputStream(entryFile));

                byte[] buffer = new byte[ShareConstants.BUFFER_SIZE];
                ZipEntry entry = new ZipEntry(ShareConstants.DEX_IN_JAR);
                zos.putNextEntry(entry);
                int length = bis.read(buffer);
                while (length != -1) {
                    zos.write(buffer, 0, length);
                    length = bis.read(buffer);
                }
                zos.closeEntry();
            } finally {
                IOHelper.closeQuietly(bis);
                IOHelper.closeQuietly(zos);
            }

            isExtractionSuccessful = SharePatchFileUtil.verifyDexFileMd5(extractTo, targetMd5);
            ShareTinkerLog.i(TAG, "isExtractionSuccessful: %b", isExtractionSuccessful);

            if (!isExtractionSuccessful) {
                final boolean succ = extractTo.delete();
                if (!succ || extractTo.exists()) {
                    ShareTinkerLog.e(TAG, "Failed to delete corrupted dex " + extractTo.getPath());
                }
            }
        }
        return isExtractionSuccessful;
    }

    // /**
    //  * reject dalvik vm, but sdk version is larger than 21
    //  */
    // private static void checkVmArtProperty() {
    //     boolean art = ShareTinkerInternals.isVmArt();
    //     if (!art && Build.VERSION.SDK_INT >= 21) {
    //         throw new TinkerRuntimeException(ShareConstants.CHECK_VM_PROPERTY_FAIL + ", it is dalvik vm, but sdk version " + Build.VERSION.SDK_INT + " is larger than 21!");
    //     }
    // }

    private static boolean extractDexFile(ZipFile zipFile, ZipEntry entryFile, File extractTo, ShareDexDiffPatchInfo dexInfo) throws IOException {
        final String fileMd5 = isVmArt ? dexInfo.destMd5InArt : dexInfo.destMd5InDvm;
        final String rawName = dexInfo.rawName;
        final boolean isJarMode = dexInfo.isJarMode;
        //it is raw dex and we use jar mode, so we need to zip it!
        if (SharePatchFileUtil.isRawDexFile(rawName) && isJarMode) {
            return extractDexToJar(zipFile, entryFile, extractTo, fileMd5);
        }
        return extract(zipFile, entryFile, extractTo, fileMd5, true);
    }

    /**
     * Generate patched dex file (May wrapped it by a jar if needed.)
     *
     * @param baseApk        OldApk.
     * @param patchPkg       Patch package, it is also a zip file.
     * @param oldDexEntry    ZipEntry of old dex.
     * @param patchFileEntry ZipEntry of patch file. (also ends with .dex) This could be null.
     * @param patchInfo      Parsed patch info from package-meta.txt
     * @param patchedDexFile Patched dex file, may be a jar.
     *                       <p>
     *                       <b>Notice: patchFileEntry and smallPatchInfoFile cannot both be null.</b>
     * @throws IOException
     */
    private static void patchDexFile(
        ZipFile baseApk, ZipFile patchPkg, ZipEntry oldDexEntry, ZipEntry patchFileEntry,
        ShareDexDiffPatchInfo patchInfo, File patchedDexFile) throws IOException {
        InputStream oldDexStream = null;
        InputStream patchFileStream = null;
        try {
            oldDexStream = new BufferedInputStream(baseApk.getInputStream(oldDexEntry));
            patchFileStream = (patchFileEntry != null ? new BufferedInputStream(patchPkg.getInputStream(patchFileEntry)) : null);

            final boolean isRawDexFile = SharePatchFileUtil.isRawDexFile(patchInfo.rawName);
            if (!isRawDexFile || patchInfo.isJarMode) {
                ZipOutputStream zos = null;
                try {
                    zos = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(patchedDexFile)));
                    zos.putNextEntry(new ZipEntry(ShareConstants.DEX_IN_JAR));
                    // Old dex is not a raw dex file.
                    if (!isRawDexFile) {
                        ZipInputStream zis = null;
                        try {
                            zis = new ZipInputStream(oldDexStream);
                            ZipEntry entry;
                            while ((entry = zis.getNextEntry()) != null) {
                                if (ShareConstants.DEX_IN_JAR.equals(entry.getName())) break;
                            }
                            if (entry == null) {
                                throw new TinkerRuntimeException("can't recognize zip dex format file:" + patchedDexFile.getAbsolutePath());
                            }
                            new DexPatchApplier(zis, patchFileStream).executeAndSaveTo(zos);
                        } finally {
                            IOHelper.closeQuietly(zis);
                        }
                    } else {
                        new DexPatchApplier(oldDexStream, patchFileStream).executeAndSaveTo(zos);
                    }
                    zos.closeEntry();
                } finally {
                    IOHelper.closeQuietly(zos);
                }
            } else {
                new DexPatchApplier(oldDexStream, patchFileStream).executeAndSaveTo(patchedDexFile);
            }
        } finally {
            IOHelper.closeQuietly(oldDexStream);
            IOHelper.closeQuietly(patchFileStream);
        }
    }

}
