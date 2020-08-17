package com.lab.xmind.controller;

import com.lab.xmind.util.ReadXml;
import com.lab.xmind.util.UnZipUtil;
import com.lab.xmind.util.WriteToExcel;
import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.apache.tomcat.util.http.fileupload.servlet.ServletRequestContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;

/**
 * @Description TODO
 * @Author
 * @Date 2020/8/17 下午4:26
 **/
@Controller
public class SwitchController {
    @RequestMapping("/upload")
    protected void uploadFile(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, FileUploadException {
        // 拿到全局对象
        ServletContext sc = request.getServletContext();
        // 取得存放xmind文件的文件夹在服务器上的绝对路径/获取web根目录下放置缓存文件的文件夹“temp”的物理路径
        String xmindFolderPath = sc.getRealPath("Xmind");
        System.out.println(xmindFolderPath);

        // 创建文件项工厂
        DiskFileItemFactory factory = new DiskFileItemFactory();
        // 创建文件上传核心组件
        ServletFileUpload servletFileUpload = new ServletFileUpload(factory);
        // 解决中文乱码问题
        servletFileUpload.setHeaderEncoding("UTF-8");

        String fileName = null;
        try {
            // 解析Multipart
            List<FileItem> items = servletFileUpload.parseRequest(new ServletRequestContext(request));

            if (items.size() == 0) {

            }

            for (FileItem item : items) {

                // 获取文件名称和后缀
                fileName = item.getName();
                // 输入流
                InputStream inputStream = item.getInputStream();

                // 获取文件类型
                String prefix = fileName.substring(fileName.lastIndexOf(".") + 1);
                // 指定复制替换的文件类型
                if (prefix.equals("xmind")) {
                    // 需要替换的文件类型
                    String zipFileName = fileName.replace(".xmind", ".zip");
                    // 将xmind文件转存为zip文件
                    File srcZipFile = new File(xmindFolderPath, zipFileName);
                    // 输出流
                    FileOutputStream fileOutputStream = new FileOutputStream(srcZipFile);

                    // 完成文件复制
                    byte[] bytes = new byte[1024];
                    int len = -1;
                    while ((len = inputStream.read(bytes)) != -1) {
                        fileOutputStream.write(bytes, 0, len);
                    }
                    // 关闭流
                    fileOutputStream.close();
                    inputStream.close();

                    // 调用unZip()进行解压
                    // 解压文件路径为tomact/webapps/xmindToExcelJava/webapps/Xmind/xmlFile/
                    String xmlPath = xmindFolderPath + "\\xmlFile/";
                    UnZipUtil.unZip(srcZipFile, xmlPath);

                    // 读取Xml文件，获取所有用例集合
                    List<List<String>> allCaseList = ReadXml.readXml(xmlPath);

                    // 通过调用writeToExcel方法写入Excel
                    WriteToExcel.writeToExcel(allCaseList,xmindFolderPath);

                    // 下载
                    String downName = fileName.substring(0, fileName.lastIndexOf(".")) + "测试用例.xls";
                    // 设置响应头，控制浏览器下载该文件
                    response.setHeader("content-disposition",
                            "attachment;filename=" + new String(downName.getBytes("UTF-8"), "ISO8859-1"));
                    // 读取要下载的文件，保存到文件输入流
                    FileInputStream fileInputStream = new FileInputStream(xmindFolderPath + "\\" + downName);
                    // 创建输出流
                    OutputStream outputStream = response.getOutputStream();
                    // 创建缓冲区
                    byte buffer[] = new byte[1024];
                    int i = 0;
                    while ((i = fileInputStream.read(buffer)) > 0) {
                        outputStream.write(buffer, 0, i);
                    }
                    // 关闭流
                    fileInputStream.close();
                    outputStream.close();

                    // 删除zip文件及解压文件
                    UnZipUtil.delAllFiles(new File(xmlPath), null, xmlPath);
                    srcZipFile.delete();
                }

            }

        } catch (FileUploadException e) {
            e.printStackTrace();
        } // try/catch

    }
}
