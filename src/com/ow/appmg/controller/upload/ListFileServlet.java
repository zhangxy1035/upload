package com.ow.appmg.controller.upload;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wencong on 2017/3/12.
 */
public class ListFileServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uploadFilePath=this.getServletContext().getRealPath("WEB-INF/view/upload.jsp");
        Map<String,String> fileNameMap=new HashMap<String, String>();

        listFile(new File(uploadFilePath),fileNameMap);
        req.setAttribute("fileNameMap",fileNameMap);
        req.getRequestDispatcher("WEB-INF/view/listFile.jsp").forward(req,resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }


   /**
       * 递归遍历指定目录下的所有文件
       * @param file
       * @param map
       */
             public void listFile(File file, Map<String, String> map) {

                 if(file == null) return;

                // 如果file代表的不是一个文件，而是一个目录
                 if (!file.isFile()) {
                     // 列出该目录下的所有文件和目录
                         File files[] = file.listFiles();
                         if(files == null) return;
                         // 遍历files[]数组
                         for (File f : files) {
                                 // 递归
                                 listFile(f, map);
                             }
                    } else {
                         String realName = file.getName().substring(file.getName().indexOf("_") + 1);
                         // file.getName()得到的是文件的原始名称，这个名称是唯一的，因此可以作为key，realName是处理过后的名称，有可能会重复
                         map.put(file.getName(), realName);
                     }
            }
}
