package com.cn.jimmy.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.NotFoundException;

public class ClassDetail {
	private  Set<String> attributeDependents=null;
	private List<CtField> attributes=null;
	private Set<String> classDependents=null;
	
	
	private String className;
	private List<CtClass> interfaceNames=null;
	private  Set<String> methodDependents=null;
	private List<CtMethod> methods=null;
	private String packageName;

	private String superClassName;

	{	
		classDependents=new HashSet<>();
		methodDependents=new HashSet<>();
		attributeDependents=new HashSet<>();
	}
	public ClassDetail() {
		this("", "", 16, 16, 16);
//		空参初始化类细节
	}
	
	public ClassDetail(String className, String superClassName, int interfaceNum, int methodNum, int attributeNum) {
		super();
//		使用全类名，超类名，接口数量，方法数量，字段数量初始化类细节
		this.className = className;
		this.superClassName = superClassName;
		interfaceNames = new ArrayList<>(interfaceNum);
		methods = new ArrayList<>(methodNum);
		attributes = new ArrayList<>(attributeNum);
	}
	public boolean addAttribute(CtField attributeName) {
//		在类中添加字段
		return attributes.add(attributeName);
	}
	public void addAttribute(CtField[] declaredFields) {
//		在类中添加字段
		for (CtField ctField : declaredFields) {
			attributes.add(ctField);
		}
	}
	public boolean addInterface(CtClass interfaceName) {
//		在类中添加接口
		return interfaceNames.add(interfaceName);
	}
	public void addInterface(CtClass[] interfaces) {
//		在类中添加接口
		for (CtClass inf : interfaces) {
			interfaceNames.add(inf);
		}
	}
	 public boolean addMethod(CtMethod methodName) {
//		在类中添加方法
		return methods.add(methodName);
	}
	public void addMethod(CtMethod[] declaredMethods) {
//	在类中添加方法
		for (CtMethod ctMethod : declaredMethods) {
			methods.add(ctMethod);
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if(null==obj||obj.getClass()!=this.getClass()) {
			return false;
		}
		ClassDetail cd=(ClassDetail)obj;
		if(className==cd.getClassName()) {
			if(superClassName==cd.getSuperClassName()) {
				if(packageName==cd.getPackageName()) {
					return true;
				}
			}
		}
		return false;
	}
	
	public void getAssiation() throws ClassNotFoundException {
		for(CtField attribute: attributes) {
			String name=null;
			try {
				name = attribute.getType().getName();
			} catch (NotFoundException e) {
				e.printStackTrace();
			}
			attributeDependents.add(name);
			String genericSignature = attribute.getGenericSignature();
//			genericSignature 是包含字段类型及泛型类型的全部 如 Ljava/util/List<Ljava/lang/Thread;>
			if(null!=genericSignature) {
				String[] split = genericSignature.substring(genericSignature.indexOf("<")+1,genericSignature.indexOf(">")).split(";");
				for (String string : split) {
					String replace = string.substring(1,string.length()).replace("/", ".");
					attributeDependents.add(replace);
				}
			}
			Object[] annotations = attribute.getAnnotations();
			for (Object object : annotations) {
				
				attributeDependents.add(object.getClass().getName());
			}
		}
	}

	public List<CtField> getAttributes() {
//		获取属性集合
		return attributes;
	}

	public String getClassName() {
//		获取全类名
		return className;
	}

	public Set<String> getDependent(){
		classDependents.add(superClassName);
		for(CtClass interfaceName : interfaceNames) {
			classDependents.add(interfaceName.getName());
		}
		return classDependents;
	}

	public CtField getField(String field) throws NotFoundException {
		CtField att=null;
		for(CtField attr :attributes) {
			if(attr.getSignature().equals(field) ){
				att=attr;
			}
		}
		if(null==att) {
			throw new NotFoundException("在注册的字段中没有找到该字段"+field);
		}
		return att;
	}

	public List<CtClass> getInterfaceNames() {
//		获取接口集合
		return interfaceNames;
	}
	public CtMethod getMethod(String method) throws NotFoundException {
		CtMethod ctMethod=null;
		for(CtMethod ctMd:methods) {
			if(ctMd.getLongName().equals(method)) {
				ctMethod=ctMd;
			}
		}
		if(null==ctMethod) {
			throw new NotFoundException("在注册的方法中没有找到该方法"+method);
		}
		return ctMethod;
	}

	public Set<String> getMethodDependent(String method) throws NotFoundException, ClassNotFoundException{
		CtMethod ctMethod = getMethod(method);
		if(null!=ctMethod) {
			CtClass[] types = ctMethod.getParameterTypes();
			for (CtClass ctClass : types) {
				methodDependents.add(ctClass.getName());
			}
			Object[] annotations = ctMethod.getAnnotations();
			for (Object object : annotations) {
				methodDependents.add(object.getClass().getName());
			}
			String genericSignature = ctMethod.getGenericSignature();
			if(null!=genericSignature) {
				analysesGenericSignature(genericSignature,methodDependents);
			}
		}
		return methodDependents;
	}
	private void analysesGenericSignature(String genericSignature, Set<String> methodDependents) {
		
//		<T:Ljava/lang/Object;>(Ljava/util/ArrayList<Ljava/lang/Integer;>;
//		Ljava/lang/String;I)Ljava/util/List<Ljava/lang/Integer;>;
		String substring = genericSignature.substring(genericSignature.indexOf("("),genericSignature.indexOf(")"));
		String[] split = substring.split(">");
		for (String string : split) {
			String[] split2 = string.split("<");
				if(split2.length>=2) {
					methodDependents.add(split2[1]);
				}
			}		
	}
	

	public List<CtMethod> getMethods() {
//		获取方法集合
		return methods;
	}

	public String getPackageName() {
		return packageName;
	}

	public String getSuperClassName() {
//		获取超类名
		return superClassName;
	}

	@Override
	public int hashCode() {
		return Objects.hash(className,superClassName,packageName);
	}

	public void setAttributes(List<CtField> attributes) {
//		设置属性集合
		this.attributes = attributes;
	}

	public void setClassName(String className) {
//		设置类名
		this.className = className;
	}

	public void setInterfaceNames(List<CtClass> interfaceNames) {
//		设置接口集合
		this.interfaceNames = interfaceNames;
	}

	public void setMethods(List<CtMethod> methods) {
//		设置方法集合
		this.methods = methods;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public void setSuperClassName(String superClassName) {
//		设置超类名
		this.superClassName = superClassName;
	}

}
