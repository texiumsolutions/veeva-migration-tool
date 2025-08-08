package com.migrationcenter.tool.data.model;

public class MappingBean {
	String source;
	String type;
	String target;
	String value;
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getTarget() {
		return target;
	}
	public void setTarget(String target) {
		this.target = target;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	@Override
	public String toString() {
		return "MappingBean [source=" + source + ", type=" + type + ", target=" + target + ", value=" + value + "]";
	}
	
}
