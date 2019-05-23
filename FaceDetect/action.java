/**@Huyue 
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.manzhiyan.base.BaseAction;
import it.manzhiyan.weixin.dnacheck.bean.DnaCheckBean;
import it.manzhiyan.weixin.dnacheck.service.impl.DnaCheckServiceImpl;
import it.manzhiyan.weixin.dnacheck.util.MD5Util;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.struts2.ServletActionContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.opensymphony.xwork2.ActionContext;

import it.manzhiyan.facedetect.FaceDetect;
import it.manzhiyan.facedetect.GsonUtils;
import it.manzhiyan.facedetect.ImgUtil;

@Controller
@Scope("prototype")
public class DnaCheckAction extends BaseAction {
	static String access_token = "init";
	static Date token_date = new Date(1);
	//存入数据
	public void uploadFile() throws Exception{
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();
         
		 String name = request.getParameter("name");
 		 String phone = request.getParameter("phone");
 		 String checkCode = request.getParameter("checkCode");
 		 String password = request.getParameter("password");
         
         DnaCheckBean dnaCheckBean = new DnaCheckBean();
         
         if(!"".equals(checkCode) && checkCode != null){
        	 DnaCheckBean dna = dnaCheckService.getDnaCheckBeanByCode(checkCode);
        	 if(dna != null){
        		 System.out.println(dna.toString());
        		/* if(dna.getName() == null){*/
        			 dnaCheckBean.setCheckCode(checkCode);  //注册码正确进行存储
        			 if(!"".equals(name) && name != null){
        	        	 dnaCheckBean.setName(name);
        	         }
        	         if(!"".equals(phone) && phone != null){
        	        	 dnaCheckBean.setPhone(phone);
        	         }
        	         if(!"".equals(password) && password != null){
        	        	 dnaCheckBean.setPassword(password);
        	         }
        	         dnaCheckBean.setIsdeleted(false);
        	         dnaCheckBean.setId(dna.getId());
        	         dnaCheckService.updateDnaCheckBean(dnaCheckBean);
        	         
        			 out.print("suc");
        		/* }else{
        			 out.print("already");    //该注册码已被使用，请重新输入
        		 }*/
        	 }else{
        		 out.print("noexistent");    //该注册码不存在，请重新输入 
        	 }
         }else{
        	 out.print("noexistent"); //注册码不能为空
         }
	}
	
	//传入图片
	//accept pic from ajax, then save locally. send the pic to the AI module.
	//good, save to Qiniu, then delete the pic, then response client's preview url(Qiniu)
	//bad, delete the pic and response msg instructing user upload again.
	
	//save pic
	public String uploadDnaImage()throws Exception{
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=UTF-8");
		/*PrintWriter out = response.getWriter(); */
		String checkCode = request.getParameter("imgCheckCode");
		
		System.out.println("======= imgCheckCode =" + checkCode);
		/*String checkCode = "";*/
		String urlImag = "";
		
		String savefile = ServletActionContext.getServletContext().getRealPath("/"); 
		
		//文件上传
		String configPath = "images\\dnaImages\\headPic"; 
		String savePath = savefile + configPath;
		System.out.println("======savePath = " + savePath);
		
		//创建文件夹
		String fileType = "";
		
		File dirFile = new File(savePath); 
	    if (!dirFile.exists()) {  
	        dirFile.mkdirs();  
	    }
		
	     DiskFileItemFactory  factory = new DiskFileItemFactory();  
	     //factory.setSizeThreshold(20 * 1024 * 1024); //设定使用内存超过5M时，将产生临时文件并存储于临时目录中。     
	     ServletFileUpload upload = new ServletFileUpload(factory);  
	     String filePath="";
	     upload.setHeaderEncoding("UTF-8");
         try {  
             List items = upload.parseRequest(request);  
             Iterator itr = items.iterator();  
             int i=0; 
             while (itr.hasNext())   
             {  
                 FileItem item = (FileItem) itr.next();
                 String OrignfileName = item.getName();  
                 fileType = OrignfileName.substring(OrignfileName.lastIndexOf("."));
                 String fileName = "";
                 if(!"".equals(checkCode) && checkCode != null){
                	 fileName =  checkCode+fileType;
                 }else{
                	 fileName = "000000"+"_"+fileType;  //操作码为空时
                 }
                 System.out.println(fileName);
                 //long fileSize = item.getSize();  
                 if (!item.isFormField()) {  
                     try{  
                         File uploadedFile = new File(savePath, fileName);  
                         OutputStream os = new FileOutputStream(uploadedFile);  
                         InputStream is = item.getInputStream();  
                         byte buf[] = new byte[1024];//可以修改 1024 以提高读取速度  
                         int length = 0;    
                         while( (length = is.read(buf)) > 0 ){    
                             os.write(buf, 0, length);    
                         }    
                         //关闭流    
                         os.flush();  
                         os.close();    
                         System.out.println("上传成功！路径："+savePath+ "\\" +fileName); 
                         filePath= savePath+ "\\" +fileName;
                        /* out.print(configPath+"/"+fileName);  */
                     }catch(Exception e){  
                         e.printStackTrace();  
                     }  
                 } 
                 urlImag = urlImag +fileName;
                 if(i >0 ) urlImag += ",";
                 i++;
             }
             
             System.out.println("urlImag = " + urlImag );
             dnaCheckService.updateImageByCode("0", checkCode);
             System.out.println(token_date);
             
             //the image shouldnt exceed 1920 * 1280
             //we have to compress the image.
             String distPath = savePath + "\\comp\\"; 
             String distImgPath = distPath + checkCode + fileType;
             System.out.println("distImgPath=====" + distImgPath);
             
             ImgUtil.reduceImg(filePath, distImgPath, 1280, 1920, null);
             //send to AI module
    		 // 官网获取的 API Key 更新为你注册的
             String clientId = "appId";
             // 官网获取的 Secret Key 更新为你注册的
             String clientSecret = "appSecret";
             
             
        	 SimpleDateFormat df = new SimpleDateFormat("yyyy-mm-dd");
        	 Date currentDate = new Date();
        	 long expireDays = (currentDate.getTime()-token_date.getTime()) / 86400000;
        	 
        	 if(expireDays > 27){
        		 access_token = FaceDetect.getAuth(clientId, clientSecret);
        		 token_date = df.parse(df.format(currentDate));
        	 }
        	 
        	 //filePath: absolute path on file system
        	 //AI module
        	 com.google.gson.JsonParser jparser = new JsonParser();
        	 com.google.gson.JsonObject jobj = (com.google.gson.JsonObject)jparser.parse(FaceDetect.detect(access_token, distImgPath));
        	 
             
        	 String rsString ="";
        	 String rsCode = "";
        	 //false: error_code != 0, 
        	 int errorcode = jobj.get("error_code").getAsInt();
        	 
        	 
             if(errorcode == 0){
            	 //AI accept 
            	 jobj = jobj.get("result").getAsJsonObject();
            	 if(jobj.get("face_num").getAsInt() != 1){
            		 rsString = "multi faces";
            		 rsCode = "1";
            	 }else{

            		 //big trap here!!!!!!!!!!!!!!
            		 String facelist = jobj.get("face_list").toString();
            		 System.out.println("facelist==="+ facelist);
            		 

            		 
            		 jobj = jobj.get("face_list").getAsJsonArray().get(0).getAsJsonObject();
            		 
            		 System.out.println("facelistjobj==="+ jobj);
            		 

            		 if(jobj.get("face_probability").getAsDouble() > 0.85){
            			 if( jobj.get("glasses").getAsJsonObject().get("type").getAsString().equals(("none").toString()) && jobj.get("glasses").getAsJsonObject().get("probability").getAsDouble() > 0.5){
            				 if( jobj.get("quality").getAsJsonObject().get("blur").getAsDouble() < 0.2 ){
            				 	jobj = jobj.get("quality").getAsJsonObject().get("occlusion").getAsJsonObject();
		            			 
		            			 double left_eye = jobj.get("left_eye").getAsDouble();
		            			 double right_eye = jobj.get("right_eye").getAsDouble();
		            			 double nose = jobj.get("nose").getAsDouble();
		            			 double mouth = jobj.get("mouth").getAsDouble();
		            			 double left_cheek = jobj.get("left_cheek").getAsDouble();
		            			 double right_cheek = jobj.get("right_cheek").getAsDouble();
		            			 double chin_contour = jobj.get("chin_contour").getAsDouble();
		            			 if(left_eye > 0.2 || right_eye > 0.2 || nose > 0.2 || mouth > 0.2 || left_cheek > 0.2 || right_cheek > 0.2 || chin_contour > 0.2){
		            				 rsString = "occlusion";
		            				 rsCode = "4";
		            			 }else{
		            				 rsString = "good";
		            				 rsCode = "0";
		            				 dnaCheckService.updateImageByCode("1", checkCode);
		            			 }
            				 }else{
            					 rsString = "blur";
            					 rsCode = "5";
            				 }
            			   }else {
            			  
            			 	 rsString = "glasses";
            			 	 rsCode = "3";
            			   }
            			   
            		 }else{
		            		 rsString = "maybe no face";
		            		 rsCode = "2";
            		 }
            		 
            	 }
            	 
             }else{
            	 //AI Denied
            	 rsString = "denied by AI module";
            	 rsCode = "-1";
            	
             }
             
             System.out.println("rsString==========" + rsString);
             System.out.println("errorcode==========" + errorcode);
             System.out.println("rsCode==========" + rsCode);
        	 PrintWriter rs = response.getWriter();
             response.setContentType("text/html;charset=utf-8");
             String imgSavePath = configPath + "\\" + urlImag;
             rs.write(rsCode);
             rs.flush();
             rs.close();
             
             
             
         } catch (FileUploadException e) {  
             e.printStackTrace();  
             return "error";
         }  
         //out.flush();  
         //out.close();
         
         return "success";
         
	}
	
	
	//生成MDNA码
	
	//升位bug修改完毕190425
	
	/*
	public void createStreamCode(){
		List<DnaCheckBean> list = new ArrayList<DnaCheckBean>();
		
		String baseCode = "MDNA1904";
		
		 
		//随机码
		for(int i=1; i<5501; i++){
			String randomCode = "";
			int x;//定义两变量
			int ranLength = 5; //末尾校验码长度
	        Random ne=new Random();//实例化一个random的对象ne
	        x=ne.nextInt(9999-1000+1)+1000;//为变量赋随机值1000-9999
	        randomCode = MD5Util.getMD5String(x+"");
			System.out.println(randomCode.substring(0,ranLength));
			DnaCheckBean dnaCheckBean = new DnaCheckBean();
			
			//升位bug修改完毕190425
			if(i-10 < 0){
				dnaCheckBean.setCheckCode(baseCode + "000"+ i+randomCode.substring(0,ranLength));
			}else if(i-100 < 0){
				dnaCheckBean.setCheckCode(baseCode + "00" + i+randomCode.substring(0,ranLength));
			}else if(i-1000 < 0){
				dnaCheckBean.setCheckCode(baseCode + "0" + i+randomCode.substring(0,ranLength));				
			}else dnaCheckBean.setCheckCode(baseCode + i+randomCode.substring(0,ranLength));				

			list.add(dnaCheckBean);
		}
		dnaCheckService.saveListDnaCheck(list);
	}
	*/
	
	//查看报告
	public void watchMessage()throws Exception{
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=UTF-8");
		PrintWriter out = response.getWriter(); 
		
		String checkCode = request.getParameter("checkCode");
		String password = request.getParameter("password");
		
		DnaCheckBean dnaCheckBean = null; 
		if((checkCode != null && !"".equals(checkCode)) || (password != null && !"".equals(password))){
			dnaCheckBean = dnaCheckService.getDnaCheckByCodeAndPwd(checkCode, password);
		}
		if(dnaCheckBean != null){
			out.print("OK");
		}else{
			out.print("NO");
		}
	}
	
	//查看报告详情页
	public String watchMessageDetail()throws Exception{
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=UTF-8");

		String checkCode = request.getParameter("checkcode");
		
		DnaCheckBean dnaCheckBean = null; 
		dnaCheckBean = dnaCheckService.getDnaCheckBeanByCode(checkCode);
		
		if((checkCode != null && !"".equals(checkCode))){
			System.out.println(checkCode);
			System.out.println(dnaCheckBean.getUrlImg());
			if(dnaCheckBean.getUrlImg().equals("3"))
				return "watchDetail";
			return "notNow";
		}
		return "none";
		
	}
}
