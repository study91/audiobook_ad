package com.study91.audiobook.file;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.util.Log;

import com.study91.audiobook.R;

/**
 * 文件测试
 */
public class FileTest extends ApplicationTestCase<Application> {
    private final String TAG = "Test";

    /**
     * 构造器
     */
    public FileTest() {
        super(Application.class);
    }

    /**
     * 测试Xml文件
     */
    public void testXmlFile() throws Exception {
        String xmlUrl = "http://www.study91.com/app/com.study91.com.study91.sj_yuwen_3b/update.xml";
        IXmlFile xmlFile = new XmlFile(xmlUrl);
        Log.e(TAG, "应用包名：" + xmlFile.getValue("package"));
        Log.e(TAG, "充许更新：" + xmlFile.getValue("update_allow"));
        Log.e(TAG, "启动更新：" + xmlFile.getValue("update_start"));
        Log.e(TAG, "更新标题：" + xmlFile.getValue("update_title"));
        Log.e(TAG, "更新版本：" + xmlFile.getValue("update_version"));
        Log.e(TAG, "更新说明：" + xmlFile.getValue("update_info"));
        Log.e(TAG, "更新地址：" + xmlFile.getValue("update_url"));
        Log.e(TAG, "推荐地址：" + xmlFile.getValue("recommend_url"));

        String fullFileName = xmlFile.getValue("update_url");
        String fileName = "";
        int start=fullFileName.lastIndexOf("/");
        if(start != -1){
            fileName = fullFileName.substring(start+1);
        }

//        int end=fullFileName.lastIndexOf(".");
//        if(start!=-1 && end!=-1){
//            fileName = fullFileName.substring(start+1,end);
//        }

        Log.e(TAG, "目标文件名：" + fileName);

        Log.e(TAG, "失效-原版本号：" + xmlFile.getValue("version"));
        Log.e(TAG, "失效-应用名称：" + xmlFile.getValue("name"));
        Log.e(TAG, "失效-更新地址：" + xmlFile.getValue("url"));
    }

    /**
     * 测试Assets资源文件
     */
    public void testFileForAssets() throws Exception {
        //测试文件
        String filename =
                getContext().getResources().getString(R.string.assets_config_filename);

        //创建文件对象
        IFile file = FileFactory.createFile(
                getContext(),
                IFile.STORAGE_TYPE_ASSETS,
                filename);

        assertNotNull(file.getInputStream());
    }

    /**
     * 测试FilesDir资源文件
     */
    public void testFileForFilesDir() throws Exception {
        //测试文件
        String filename =
                getContext().getResources().getString(R.string.assets_config_filename);

        //创建文件对象
        IFile file = FileFactory.createFile(
                getContext(),
                IFile.STORAGE_TYPE_FILESDIR,
                filename);

        assertNotNull(file.getInputStream());
    }

    /**
     * 测试SD卡资源文件
     */
    public void testFileForSDCard() throws Exception {
        //测试文件
        String filename = "study91/book/020/image/cover.jpg";

        //创建文件对象
        IFile file = FileFactory.createFile(getContext(),
                IFile.STORAGE_TYPE_SDCARD,
                filename);

        assertNotNull(file.getInputStream());
    }
}
