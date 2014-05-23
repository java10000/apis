package com.varicom.aop.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;

/**
 * 文件操作的工具类
 * @author james.deng
 *
 */
public class FileUtil {
	
	/**
	 * 读取文件
	 * @param file
	 * @return
	 * @throws IOException 
	 */
	public static String readFile(File file,String encode) throws IOException{
		FileInputStream ism = null;
		InputStreamReader isr = null;
		BufferedReader bs =null;
		try {
			ism = new FileInputStream(file);
			
			isr = new InputStreamReader(ism,encode);
			
			bs = new BufferedReader(isr);
			String line;
			String content="";
			while ((line=bs.readLine()) != null) {
				content+=line;
			}
			return content;
		} catch (FileNotFoundException e) {
			throw e;
		} catch (IOException e) {
			throw e;
		} finally{			
			try {
				bs.close();
				isr.close();
				ism.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 新建目录
	 * 
	 * @param folderPath
	 * @throws Exception 
	 */
	public static boolean newFolder(String folderPath) throws Exception {
		return newFolder(new File(folderPath));
	}
	/**
	 * 新建目录
	 * 
	 * @param folderPath
	 * @throws Exception 
	 */
	public static boolean newFolder(File file) throws Exception {
		try {
			if (!file.exists()) {
				file.mkdir();
			}
			return true;
		} catch (Exception e) {
			throw e;
		}
	}
	 
	 /**
	  * 新建文件
	  * @param filePathAndName
	  * @param fileContent
	  * @param isDel 如果文件存是否删除新建
	 * @throws Exception 
	  */
	public static boolean newFile(String filePathAndName, String fileContent,boolean isDel) throws Exception {
		FileWriter resultFile = null;
		PrintWriter myFile = null;
		try {
			String filePath = filePathAndName;
			filePath = filePath.toString();
			File myFilePath = new File(filePath);
			if (!myFilePath.exists()) {
				myFilePath.createNewFile();
			}else{
				//是否删除新建
				if(isDel){
					delFile(myFilePath);
					myFilePath.createNewFile();
				}
			}
			resultFile = new FileWriter(myFilePath);
			myFile = new PrintWriter(resultFile);
			String strContent = fileContent;
			myFile.println(strContent);
			return true;
		} catch (Exception e) {
			throw e;
		}finally{
			try {
				myFile.flush();
				resultFile.flush();
				myFile.close();
				resultFile.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public static boolean newFile(String filePathAndName, String fileContent) throws Exception {
		return newFile(filePathAndName, fileContent, false);
	}
	
	 /**
	  * 删除文件
	  * @param filePathAndName
	 * @throws Exception 
	  */
	 public static boolean delFile(String filePathAndName) throws Exception {    
		 return delFile(new File(filePathAndName));
	 } 
	 /**
	  * 删除文件
	  * @param filePathAndName
	 * @throws Exception 
	  */
	public static boolean delFile(File myDelFile) throws Exception {
		try {
			myDelFile.delete();
			return true;
		} catch (Exception e) {
			throw e;
		}

	}
	
	 /**-----------------------------------------------------------------------
     *getAppPath需要一个当前程序使用的Java类的class属性参数，它可以返回打包过的
     *Java可执行文件（jar，war）所处的系统目录名或非打包Java程序所处的目录
     *@param cls为Class类型
     *@return 返回值为该类所在的Java程序运行的目录
     -------------------------------------------------------------------------*/
    public static String getAppPath(Class cls){
        //检查用户传入的参数是否为空
        if(cls==null) 
         throw new java.lang.IllegalArgumentException("参数不能为空！");
        ClassLoader loader=cls.getClassLoader();
        //获得类的全名，包括包名
        String clsName=cls.getName()+".class";
        //获得传入参数所在的包
        Package pack=cls.getPackage();
        String path="";
        //如果不是匿名包，将包名转化为路径
        if(pack!=null){
            String packName=pack.getName();
           //此处简单判定是否是Java基础类库，防止用户传入JDK内置的类库
           if(packName.startsWith("java.")||packName.startsWith("javax.")) 
              throw new java.lang.IllegalArgumentException("不要传送系统类！");
            //在类的名称中，去掉包名的部分，获得类的文件名
            clsName=clsName.substring(packName.length()+1);
            //判定包名是否是简单包名，如果是，则直接将包名转换为路径，
            if(packName.indexOf(".")<0) path=packName+"/";
            else{//否则按照包名的组成部分，将包名转换为路径
                int start=0,end=0;
                end=packName.indexOf(".");
                while(end!=-1){
                    path=path+packName.substring(start,end)+"/";
                    start=end+1;
                    end=packName.indexOf(".",start);
                }
                path=path+packName.substring(start)+"/";
            }
        }
        //调用ClassLoader的getResource方法，传入包含路径信息的类文件名
        java.net.URL url =loader.getResource(path+clsName);
        //从URL对象中获取路径信息
        String realPath=url.getPath();
        //去掉路径信息中的协议名"file:"
        int pos=realPath.indexOf("file:");
        if(pos>-1) realPath=realPath.substring(pos+5);
        //去掉路径信息最后包含类文件信息的部分，得到类所在的路径
        pos=realPath.indexOf(path+clsName);
        realPath=realPath.substring(0,pos-1);
        //如果类文件被打包到JAR等文件中时，去掉对应的JAR等打包文件名
        if(realPath.endsWith("!"))
            realPath=realPath.substring(0,realPath.lastIndexOf("/"));
      /*------------------------------------------------------------
       ClassLoader的getResource方法使用了utf-8对路径信息进行了编码，当路径
        中存在中文和空格时，他会对这些字符进行转换，这样，得到的往往不是我们想要
        的真实路径，在此，调用了URLDecoder的decode方法进行解码，以便得到原始的
        中文及空格路径
      -------------------------------------------------------------*/
      try{
        realPath=java.net.URLDecoder.decode(realPath,"utf-8");
       }catch(Exception e){throw new RuntimeException(e);}
       return realPath;
    }//getAppPath定义结束
	
}
