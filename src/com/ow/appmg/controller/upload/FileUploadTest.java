package com.ow.appmg.controller.upload;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.ProgressListener;
import org.apache.commons.fileupload.FileUploadBase.FileSizeLimitExceededException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/file")
public class FileUploadTest {
	private String Ext_Nmae="gif,jpg,jpeg,png,bmp,swf,flv,mp3,wav,wma," +
			"wmv,mid,avi,mpg,asf,rm,rmvb,doc,docx,xls,xlsx,ppt,htm,html,tt,zip,rar,gz,bz2";

	@RequestMapping("/upload")
	public void datalist(HttpServletRequest req,HttpServletResponse res)  throws ServletException, IOException{

		//		res.getWriter().write("hello world"); //Test

		String savePath=req.getSession().getServletContext().getRealPath("WEB-INF/upload");
		File saveFileDir=new File(savePath);
		if(!saveFileDir.exists()){
			//创建临时目录
			saveFileDir.mkdirs();
		}
		//上传时生成临时文件保存目录
		String tmpPath=req.getSession().getServletContext().getRealPath("WEB-INF/tmp");
		File tmpFile=new File(tmpPath);
		if(!tmpFile.exists()){
			tmpFile.mkdirs();
		}

		//消息提示

		String message="";
		try{
			DiskFileItemFactory factory=new DiskFileItemFactory();
			factory.setSizeThreshold((1024*10));
			factory.setRepository(tmpFile);
			ServletFileUpload upload=new ServletFileUpload(factory);
			upload.setProgressListener(new ProgressListener() {

				//	               @Override
				public void update(long readedBytes, long totalBytes, int currentItem) {
					System.out.println("当前已处理:"+readedBytes+totalBytes+currentItem);
				}

			});
			upload.setHeaderEncoding("UTF-8");


			if(!ServletFileUpload.isMultipartContent(req)){
				return;
			}

			upload.setFileSizeMax(1024*1024*1);
			upload.setSizeMax(1024*1024*10);
			List items=upload.parseRequest(req);

			Iterator itr=items.iterator();
			while (itr.hasNext()){
				FileItem item= (FileItem)itr.next();
				if (item.isFormField()){
					String name=item.getFieldName();
					String value=item.getString("UTF-8");
					System.out.println(name+"="+value);


				}else {
					String fileName=item.getName();
					System.out.println("文件名:"+fileName);
					if(fileName==null&& fileName.trim().length()==0){
						continue;
					}
					fileName=fileName.substring(fileName.lastIndexOf("\\")+1);

					String fileExt=fileName.substring(fileName.lastIndexOf(".")+1).toLowerCase();

					System.out.println("上传的文件扩展名是:"+fileExt);
					if(!Ext_Nmae.contains(fileExt)){
						System.out.println("上传文件扩展是不允许的扩展名:"+fileExt);
						message=message+"文件:"+fileName+",上传文件的扩展名是不允许扩展名的:"+fileExt+"<br/>";
						break;
					}

					if(item.getSize()==0) continue;
					if(item.getSize()>1024 * 1024 * 1){
						System.out.println("上传文件大小:"+item.getSize());
						message=message+"文件:"+fileName+",上传文件大小超过限制:"+upload.getFileSizeMax()+"<br/>";
					}

					String saveFileName=makeFileName(fileName);

					InputStream is=item.getInputStream();

					FileOutputStream out=new FileOutputStream(savePath+"\\"+saveFileName);
					byte buffer[] =new byte[2014];
					int len=0;
					while ((len=is.read(buffer))>0){
						out.write(buffer,0,len);
					}
					out.close();
					is.close();
					item.delete();

					message=message+"文件:"+fileName+",上传成功<br/>";
				}
			}

		}catch (FileSizeLimitExceededException e){

			message=message+"上传文件大小超过限制<br/>";
			e.printStackTrace();

		}catch (Exception e){
			e.printStackTrace();
		}finally {
//			req.setCharacterEncoding("utf-8");
//			System.out.println(message);
//			res.getWriter().write(message);
		}

	}
	private String makeFileName(String fileName){
		return UUID.randomUUID().toString().replaceAll("-","")+"_"+fileName;
	}

}
