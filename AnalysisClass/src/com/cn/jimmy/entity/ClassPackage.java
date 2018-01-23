package com.cn.jimmy.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class ClassPackage {
	private String packageName = null;
	private String presentname = null;
	
	private List<ClassPackage> childPackages = null;

	private List<ClassDetail> Classes = null;

	private Set<String> dependentPackages = null;
	{
		dependentPackages=new HashSet<String>();
	}
	public ClassPackage() {
		super();
		childPackages = new ArrayList<ClassPackage>(16);
		Classes=new ArrayList<ClassDetail>(16);
	}
	
	public ClassPackage(String name) {
		this();
		packageName = name;
		presentname = name.substring(name.lastIndexOf("\\") + 1);
	}
	public boolean addChildPackage(ClassPackage childPackage) {
		return childPackages.add(childPackage);
	}

	public boolean addClass(ClassDetail classDetail) {
		return Classes.add(classDetail);
	}

	@Override
	public boolean equals(Object obj) {
		if (null == obj || obj.getClass() != this.getClass()) {
			return false;
		}
		ClassPackage cp = (ClassPackage) obj;
		return Objects.equals(packageName, cp.getPackageName());
	}

	public List<ClassPackage> getChildPackages() {
		return childPackages;
	}

	public List<ClassDetail> getClasses() {
		return Classes;
	}

	public Set<String> getDependent(){
//		获取当前包中类的依赖
		for (ClassDetail classDetail : Classes) {
			Set<String> dependent = classDetail.getDependent();
			dependentPackages.addAll(dependent);
		}
//		遍历子包，获取其中的依赖
		analysisChildPckageDependent(dependentPackages,childPackages);
		return dependentPackages;
	}

	private void analysisChildPckageDependent(Set<String> dependentPackages,List<ClassPackage>childPackages) {
		if(null!=childPackages&&childPackages.size()!=0) {
			for (ClassPackage childPackage : childPackages) {
				List<ClassDetail> list = childPackage.getClasses();
				for (ClassDetail classDetail : list) {
					dependentPackages.addAll(classDetail.getDependent());
				}
				analysisChildPckageDependent(dependentPackages,childPackage.getChildPackages());
			}
		}
	}

	public String getPackageName() {
		return packageName;
	}

	public String getPresentname() {
		return presentname;
	}

	@Override
	public int hashCode() {
		int hash = Objects.hash(packageName);
		for (ClassPackage childPackage : childPackages) {
			hash = +Objects.hash(childPackage);
		}
		for (String depentPackage : dependentPackages) {
			hash = +Objects.hash(depentPackage);
		}
		return hash;
	}
}
