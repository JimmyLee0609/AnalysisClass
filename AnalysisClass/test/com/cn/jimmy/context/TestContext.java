package com.cn.jimmy.context;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.cn.jimmy.entity.ClassDetail;
import com.cn.jimmy.entity.ClassPackage;

import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.bytecode.MethodInfo;

public class TestContext {
	private Context context=new Context();
	@Test
	public void test() throws NotFoundException, ClassNotFoundException {
		File f=	new File("D:\\oxygenEclipse\\AnalysisClass\\TestProject\\bin");
		context.getSource(f);
		List<ClassPackage> packages = context.getPackages();
		context.analysePackageClass(packages);
		for (ClassPackage classPackage : packages) {
			List<ClassDetail> classes = classPackage.getClasses();
			for (ClassDetail classDetail : classes) {
				List<CtMethod> methods = classDetail.getMethods();
				for (CtMethod ctMethod : methods) {
					System.out.println("========="+ctMethod.getLongName()+"==========");
					
					Set<String> methodDependent = classDetail.getMethodDependent(ctMethod.getLongName());
					if(null!=methodDependent) {
						for (String string : methodDependent) {
							System.out.println(string);
						}
					}
				}
			}
		}
	}
	@Test
	public void test333() {
		analysesGenericSignature("<T:Ljava/lang/Object;>(Ljava/util/ArrayList<Ljava/lang/Integer;>;Ljava/lang/String;I)Ljava/util/List<Ljava/lang/Integer;>;");
	}
	private void analysesGenericSignature(String genericSignature) {
		StringBuffer sb = new StringBuffer ();
		
//		<T:Ljava/lang/Object;>(Ljava/util/ArrayList<Ljava/lang/Integer;>;
//		Ljava/lang/String;I)Ljava/util/List<Ljava/lang/Integer;>;

		String[] split = genericSignature.split(">");
		for (String string : split) {
			String[] split2 = string.split("<");
				if(split2.length>=2) {
					sb.append(split2[1]);
				}
			}		
		System.out.println(sb.toString());
	}

	private void testDependent(List<ClassPackage> packages) {
		for (ClassPackage classPackage : packages) {
			Set<String> dependent = classPackage.getDependent();
			for (String string : dependent) {
				System.out.print(string+"--");
			}
			System.out.println("====================");
		}
	}

	private void testClassDependent(List<ClassPackage> packages) {
		for (ClassPackage classPackage : packages) {
			List<ClassDetail> classes = classPackage.getClasses();
			for (ClassDetail classDetail : classes) {
				Set<String> dependent = classDetail.getDependent();
				for (String string : dependent) {
					System.out.println(string);
				}	
				System.out.println("==============");
			}
			
		}
	}

	private void testClassDetail() throws NotFoundException {
		List<ClassPackage> packages = context.getPackages();
		
		context.analysePackageClass(packages);
		
		for (ClassPackage classPackage : packages) {
			
			List<ClassDetail> classes = classPackage.getClasses();
			
			for (ClassDetail classDetail : classes) {
				
				System.out.println(classDetail.getClassName());
				List<CtMethod> methods = classDetail.getMethods();
				
				for (CtMethod ctMethod : methods) {
					String longName = ctMethod.getLongName();
					int modifiers = ctMethod.getModifiers();
					CtClass declaringClass = ctMethod.getDeclaringClass();
					CtClass returnType = ctMethod.getReturnType();
					String signature = ctMethod.getSignature();
					CtClass[] parameterTypes = ctMethod.getParameterTypes();
					
					String genericSignature = ctMethod.getGenericSignature();
					
					MethodInfo methodInfo = ctMethod.getMethodInfo();
					String descriptor = methodInfo.getDescriptor();
					
					System.out.println("genericSignature "+genericSignature+" descriptor "+descriptor);
					
					System.out.println("longName:"+longName+" declaringClass:"+declaringClass.getName()
							+" signature--> "+signature+"  returnType: "+returnType.getName());
					for (CtClass ctClass : parameterTypes) {
						System.out.println("parameterType "+ctClass.getName());
					}
				}
				System.out.println("=====================");
			}
		}
	}
	
	private void testRootClassPackage() throws NotFoundException {
		ClassPackage rootPackage = context.getRootPackage();
		
		List<ClassPackage> childPackages = rootPackage.getChildPackages();
		
		for (ClassPackage classPackage : childPackages) {
			context.analysePackageClass(classPackage);
			
			List<ClassDetail> classes = classPackage.getClasses();
			for (ClassDetail detail : classes) {
				
				System.out.println("className: "+detail.getClassName());
				 
				List<CtField> attributes = detail.getAttributes();
					for (CtField attr : attributes) {
						CtClass type = attr.getType();
						
						System.out.println(type.getName());
					}
			}
			System.out.println("====================================");
		}
	}
}
