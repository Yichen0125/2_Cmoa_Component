package com.besteasy.cmoa.file.controller;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.besteasy.cmoa.file.service.FileService;
import com.besteasy.cmoa.util.DataProcessUtils;

@Controller
public class FileUpload {
	@Autowired
	private FileService fileService;
	
	/**
	 * 下载文件
	 * @param id
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value="downLoad/{id}",method=RequestMethod.POST,produces= "application/octet-stream;charset=UTF-8")
	public ResponseEntity<byte[]> downLoad(@PathVariable(value="id") Integer id,HttpServletRequest request,HttpServletResponse response) throws IOException{
		System.out.println("--------------------------下载开始");
		
		com.besteasy.cmoa.entity.File dfile = fileService.getFileById(id);
		
		//      指定文件,必须是绝对路径
		  File file = new File(dfile.getUrl());
		//      下载浏览器响应的那个文件名
		  String dfileName = dfile.getName();
		  
		  String filename = URLEncoder.encode(dfile.getName(),"UTF-8"); 
		  System.out.println(filename);
		  System.out.println(dfileName);
		//      下面开始设置HttpHeaders,使得浏览器响应下载
		  HttpHeaders headers = new HttpHeaders();
		  
		//      设置响应方式
		  headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		//      设置响应文件
		  headers.setContentDispositionFormData("attachment", filename);
		  System.out.println("--------------------------下载结束");
		//      把文件以二进制形式写回
		  return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(file), headers, HttpStatus.CREATED);
		
	}
	
	@RequestMapping(value="deleteFile/{id}",method=RequestMethod.DELETE)
	public void delete(@PathVariable(value="id") Integer id,HttpServletRequest request,HttpServletResponse response) throws Exception{
		
		com.besteasy.cmoa.entity.File file = fileService.getFileById(id);
		System.out.println("--------------------------删除开始");
		fileService.deleteFile(file,DataProcessUtils.subPath(file.getUrl()));
		String header = request.getHeader("Referer");
		response.sendRedirect(header);
		System.out.println("--------------------------删除结束");
		
	}
	
	
	@RequestMapping(value="/WEB-INF/pages/home/starter")
	public ModelAndView showList(){
		List<com.besteasy.cmoa.entity.File> files = fileService.getFileList();
		String li = "";
		int i = 0;
		for (;i<files.size();i++) {
			li += 	"<li>"
					+ "<img src='static/image/1.jpg' width='20' heigh='26'>"
					+ "<a title='"+files.get(i).getName()+"'>"+files.get(i).getName()+"</a>"
					+ "<input type='hidden' value='"+files.get(i).getName()+"'>"
					+ "<button class='btn btn-default btn-xs deleteButton'>删除文件</button>"
					+ "<input type='hidden' value='"+ files.get(i).getId()+"'>"
					+ "<button class='btn btn-default btn-xs downLoadButton'>下载文件</button>"
					+ "</li>";
		}
		
		ModelAndView mv = new ModelAndView("home/starter","li",li);
		
		return mv;
	}
	
	/**
	 * 文件上传   复制到 D:\\upload 目录下并且保存到数据库
	 * @param myfiles
	 * @param request
	 * @param filename
	 * @return
	 * @throws IOException
	 */
	 @RequestMapping(value="/fileUpload", method=RequestMethod.POST)
	    public void addUser(@RequestParam("Myfile") MultipartFile[] myfiles, HttpServletRequest request,HttpServletResponse response) throws IOException{
	        //如果只是上传一个文件，则只需要MultipartFile类型接收文件即可，而且无需显式指定@RequestParam注解  
	        //如果想上传多个文件，那么这里就要用MultipartFile[]类型来接收文件，并且还要指定@RequestParam注解  
	        //并且上传多个文件时，前台表单中的所有<input type="file"/>的name都应该是myfiles，否则参数里的myfiles无法获取到所有上传的文件
		 System.out.println("----------------------上传开始");
		 Integer fileId = 0;

     	//批量插入的集合  params
         List<com.besteasy.cmoa.entity.File> params = new ArrayList<com.besteasy.cmoa.entity.File>();
         
         //从session 中 获取登陆用户  用来创建存放上传文件的目录
         String userName = "syc";
	        for(MultipartFile myfile : myfiles){
	            if(myfile.isEmpty()){  
	                System.out.println("文件未上传");  
	            }else{
	                
	            	String realfilename = myfile.getOriginalFilename();
	            	
	                System.out.println("文件长度: " + myfile.getSize());
	                System.out.println("文件类型: " + myfile.getContentType());  
	                System.out.println("文件名称: " + myfile.getName());  
	                System.out.println("文件原名: " + myfile.getOriginalFilename());  
	                System.out.println("========================================"); 
	                String realPath = "d://upload//"+userName;
	                //判断目录下是否有重复
	                File file = new File("d://upload//"+userName);
	                if(!file.exists())   {
	                    file.mkdirs();
	                  }
	                String[] strlist = file.list();
	                for (int i = 0; i < strlist.length; i++) {
						if (strlist[i].equals(myfile.getOriginalFilename())){
							System.out.println("已存在");
							
							realfilename = DataProcessUtils.subStr(myfile.getOriginalFilename(),i+1);
							FileUtils.copyInputStreamToFile(myfile.getInputStream(), new File(realPath,realfilename ));
							System.out.println("已拷贝");
						}
					}
	                
	                //如果用的是Tomcat服务器，则文件会上传到\\%TOMCAT_HOME%\\webapps\\YourWebProject\\WEB-INF\\upload\\文件夹中  
	                
	                		//request.getSession().getServletContext().getRealPath("/upload");
	                System.out.println(realPath);
	                //这里不必处理IO流关闭的问题，因为FileUtils.copyInputStreamToFile()方法内部会自动把用到的IO流关掉
	                
	                FileUtils.copyInputStreamToFile(myfile.getInputStream(), new File(realPath, myfile.getOriginalFilename()));
	                com.besteasy.cmoa.entity.File f = new com.besteasy.cmoa.entity.File(null, realfilename, DataProcessUtils.toRealPath(realPath,realfilename));
	               	params.add(f);
	               
	                
		                //保存文件信息到数据库
		               	fileId = fileService.saveFile(DataProcessUtils.toRealPath(realPath,realfilename));


//		            List <Integer > fileIdList = fileService.batchInsertFile(params);
	               	
	               	PrintWriter out = response.getWriter();
	               	out.println(fileId);
	               	System.out.println("---------------------------------上传结束");
	            }  
	        		
	            
	        }
	    }  
	 
}

