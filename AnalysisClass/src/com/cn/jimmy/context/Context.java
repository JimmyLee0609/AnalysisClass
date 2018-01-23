package com.cn.jimmy.context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.cn.jimmy.entity.ClassDetail;
import com.cn.jimmy.entity.ClassPackage;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.NotFoundException;

public class Context {
	private  List<ClassPackage> Packages=new ArrayList<ClassPackage>();
	private ClassPackage rootPackage=null;
	
	public ClassPackage getRootPackage() {
		return rootPackage;
	}

	public void setRootPackage(ClassPackage rootPackage) {
		this.rootPackage = rootPackage;
	}

	public List<ClassPackage> getPackages() {
		return Packages;
	}

	private static ClassPool pool=ClassPool.getDefault();

	public void getSource(File f) {
		rootPackage=new ClassPackage(f.getAbsolutePath());
		analysePackage(f,rootPackage);
		
	}//end getSource(File f)
	
	public void analysePackageClass(List<ClassPackage> packages) {
		for (ClassPackage classPackage : packages) {
			analysePackageClass(classPackage);
		}
	}
	public void analysePackageClass(ClassPackage classPackage) {
		File file = new File(classPackage.getPackageName());
		File[] listFiles = file.listFiles();
		for (File file2 : listFiles) {
			if(file2.isFile()) {
				try {
					InputStream in=new FileInputStream(file2);
					CtClass ctClass = pool.makeClass(in);
					
					CtField[] declaredFields = ctClass.getDeclaredFields();
					CtMethod[] declaredMethods = ctClass.getDeclaredMethods();
					
					
					CtClass[] interfaces = ctClass.getInterfaces();
					String name = ctClass.getName();
					String packageName = ctClass.getPackageName();
					String superClassName = ctClass.getSuperclass().getName();
					
					ClassDetail detail = new ClassDetail(name,superClassName,
							interfaces.length,declaredMethods.length,declaredFields.length);
					detail.addAttribute(declaredFields);
					detail.addInterface(interfaces);
					detail.addMethod(declaredMethods);
					detail.setPackageName(packageName);
					classPackage.addClass(detail);
					
				} catch (NotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (RuntimeException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	private void analysePackage(File f, ClassPackage currnetPackages) {
		File[] files = f.listFiles();
			for (File file : files) {
				if(null==file||file.isFile()) {
				}else if(file.isDirectory()){
					ClassPackage classPackage = new ClassPackage(file.getAbsolutePath());
					
					Packages.add(classPackage);
					
					currnetPackages.addChildPackage(classPackage);
					
					analysePackage(file,classPackage);
					
				}//end if
			}//end for
		
	}//end analysePackage(File f)
}
