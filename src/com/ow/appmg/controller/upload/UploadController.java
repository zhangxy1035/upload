package com.ow.appmg.controller.upload;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.ProgressListener;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;


@Controller
@RequestMapping("/demo")
public class UploadController {
	
	@RequestMapping(value = "/saveuploads" , method = RequestMethod.POST)
	public String upload(
			@RequestParam(value = "file" , required = false) MultipartFile file , 
				HttpServletRequest request , ModelMap model){
		System.out.println("开始");
		String path = request.getSession().getServletContext().getRealPath("file");
		String fileName = file.getOriginalFilename();
		
		System.out.println(fileName);
		System.out.println(path);
		File targetfile = new File(path , fileName);
			if(!targetfile.exists()){
				targetfile.mkdirs();
			}
			
			try{
				file.transferTo(targetfile);
			}catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		
		model.addAttribute("fileUrl" , request.getContextPath() + "/file/" + fileName);
		return "result";
		
	}
	
	
	
	public void datalist(HttpServletRequest req,HttpServletResponse res)  throws ServletException, IOException{
		String path = req.getSession().getServletContext().getRealPath("file");
		File saveFileDir=new File(path);
		if(!saveFileDir.exists()){
			//创建临时目录
			saveFileDir.mkdirs();
		}
		
	}
	
	@RequestMapping("/upload2")
	public void datalist2(HttpServletRequest req,HttpServletResponse res)  throws ServletException, IOException{
		req.setCharacterEncoding("utf-8");
        //得到上传文件的保存目录，将上传的文件存放于WEB-INF目录下，不允许外界直接访问，保证上传文件的安全
        String savePath = req.getSession().getServletContext().getRealPath("/WEB-INF/upload");
        //上传时生成的临时文件保存目录
        String tempPath = req.getSession().getServletContext().getRealPath("/WEB-INF/temp");
        File file = new File(tempPath);
        if(!file.exists()&&!file.isDirectory()){
            System.out.println("目录或文件不存在！");
            file.mkdir();
        }
        //消息提示
        String message = "";
        try {
            //使用Apache文件上传组件处理文件上传步骤：
            //1、创建一个DiskFileItemFactory工厂
            DiskFileItemFactory diskFileItemFactory = new DiskFileItemFactory();
            //设置工厂的缓冲区的大小，当上传的文件大小超过缓冲区的大小时，就会生成一个临时文件存放到指定的临时目录当中。
            diskFileItemFactory.setSizeThreshold(1024*100);
            //设置上传时生成的临时文件的保存目录
            diskFileItemFactory.setRepository(file);
            //2、创建一个文件上传解析器
            ServletFileUpload fileUpload = new ServletFileUpload(diskFileItemFactory);
            //解决上传文件名的中文乱码
            fileUpload.setHeaderEncoding("UTF-8");
            //监听文件上传进度
            fileUpload.setProgressListener(new ProgressListener(){
                public void update(long pBytesRead, long pContentLength, int arg2) {
                    System.out.println("文件大小为：" + pContentLength + ",当前已处理：" + pBytesRead);
                }
            });
            //3、判断提交上来的数据是否是上传表单的数据
            if(!fileUpload.isMultipartContent(req)){
                //按照传统方式获取数据
                return;
            }
            //设置上传单个文件的大小的最大值，目前是设置为1024*1024字节，也就是1MB
            fileUpload.setFileSizeMax(1024*1024);
            //设置上传文件总量的最大值，最大值=同时上传的多个文件的大小的最大值的和，目前设置为10MB
            fileUpload.setSizeMax(1024*1024*10);
            //4、使用ServletFileUpload解析器解析上传数据，解析结果返回的是一个List<FileItem>集合，每一个FileItem对应一个Form表单的输入项
            List<FileItem> list = fileUpload.parseRequest(req);
            for (FileItem item : list) {
                //如果fileitem中封装的是普通输入项的数据
                if(item.isFormField()){
                    String name = item.getFieldName();
                    //解决普通输入项的数据的中文乱码问题
                    String value = item.getString("UTF-8");
                    String value1 = new String(name.getBytes("iso8859-1"),"UTF-8");
                    System.out.println(name+"  "+value);
                    System.out.println(name+"  "+value1);
                }else{
                    //如果fileitem中封装的是上传文件，得到上传的文件名称，
                    String fileName = item.getName();
                    System.out.println(fileName);
                    if(fileName==null||fileName.trim().equals("")){
                        continue;
                    }
                    //注意：不同的浏览器提交的文件名是不一样的，有些浏览器提交上来的文件名是带有路径的，如：  c:\a\b\1.txt，而有些只是单纯的文件名，如：1.txt
                    //处理获取到的上传文件的文件名的路径部分，只保留文件名部分
                    fileName = fileName.substring(fileName.lastIndexOf(File.separator)+1);
                    //得到上传文件的扩展名
                    String fileExtName = fileName.substring(fileName.lastIndexOf(".")+1);
                    if("zip".equals(fileExtName)||"rar".equals(fileExtName)||"tar".equals(fileExtName)||"jar".equals(fileExtName)){
//                        req.setAttribute("message", "上传文件的类型不符合！！！");
//                        req.getRequestDispatcher("/message.jsp").forward(req, res);
                    	 res.getWriter().write("上传文件的类型不符合！！！");
                        return;
                    }
                    //如果需要限制上传的文件类型，那么可以通过文件的扩展名来判断上传的文件类型是否合法
                    System.out.println("上传文件的扩展名为:"+fileExtName);
                    //获取item中的上传文件的输入流
                    InputStream is = item.getInputStream();
                    //得到文件保存的名称
                    fileName = mkFileName(fileName);
                    System.out.println("文件名称为:"+fileName);
                    //得到文件保存的路径
                    String savePathStr = mkFilePath(savePath, fileName);
                    System.out.println("保存路径为:"+savePathStr);
                    System.out.println("数据库应保存的是:"+savePathStr+"/"+fileName);
                    //创建一个文件输出流
                    FileOutputStream fos = new FileOutputStream(savePathStr+File.separator+fileName);
                    //创建一个缓冲区
                    byte buffer[] = new byte[1024];
                    //判断输入流中的数据是否已经读完的标识
                    int length = 0;
                    //循环将输入流读入到缓冲区当中，(len=in.read(buffer))>0就表示in里面还有数据
                    while((length = is.read(buffer))>0){
                        //使用FileOutputStream输出流将缓冲区的数据写入到指定的目录(savePath + "\\" + filename)当中
                        fos.write(buffer, 0, length);
                    }
                    //关闭输入流
                    is.close();
                    //关闭输出流
                    fos.close();
                    //删除处理文件上传时生成的临时文件
                    item.delete();
                    res.setContentType("text/html;charset=UTF-8"); 
                    res.getWriter().write("文件上传成功");
                }
            }
        } catch (FileUploadBase.FileSizeLimitExceededException e) {
            e.printStackTrace();
//            req.setAttribute("message", "单个文件超出最大值！！！");
//            req.getRequestDispatcher("/message.jsp").forward(req, res);
            res.setContentType("text/html;charset=UTF-8"); 
            res.getWriter().write("单个文件超出最大值！！！");
            return;
        }catch (FileUploadBase.SizeLimitExceededException e) {
            e.printStackTrace();
//            req.setAttribute("message", "上传文件的总的大小超出限制的最大值！！！");
//            req.getRequestDispatcher("/message.jsp").forward(req, res);
            res.setContentType("text/html;charset=UTF-8"); 
            res.getWriter().write("上传文件的总的大小超出限制的最大值！！！");
            return;
        }catch (FileUploadException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            res.setContentType("text/html;charset=UTF-8"); 
            message = "文件上传失败";
        }
//        req.setAttribute("message",message);
//        req.getRequestDispatcher("/message.jsp").forward(req, res);
        res.setContentType("text/html;charset=UTF-8"); 
        res.getWriter().write("成功");
    }

    public String mkFileName(String fileName){
        return UUID.randomUUID().toString()+"_"+fileName;
    }
    public String mkFilePath(String savePath,String fileName){
        //得到文件名的hashCode的值，得到的就是filename这个字符串对象在内存中的地址
        int hashcode = fileName.hashCode();
        int dir1 = hashcode&0xf;
        int dir2 = (hashcode&0xf0)>>4;
        //构造新的保存目录
        String dir = savePath + "\\" + dir1 + "\\" + dir2;
        //File既可以代表文件也可以代表目录
        File file = new File(dir);
        if(!file.exists()){
            file.mkdirs();
        }
        return dir;
    }
	
	
	
	
	
}
