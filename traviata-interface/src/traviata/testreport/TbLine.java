package traviata.testreport;

import java.io.Serializable;

import traviata.base.xml.XMLElement;
import traviata.testreport.TbLine;

public class TbLine implements TbActionItem, Serializable {
	private String label;
	private String text;
	
	public TbLine() {
	}

	public TbLine(String label, String text) {
		this.label = label;
		this.text = text;
	}

	public TbLine(XMLElement e) {
		if (e.hasAttribute("label")) {
			label = e.getValue("label");
		}
		text = e.getText();
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getLabel() {
		return label;
	}

	public String getLabeld() {
		if (label == null || label.trim().isEmpty()) {
			return "";
		}
		return label.trim().endsWith(":") ? label : label + ":";
	}

	public String getText() {
		return text;
	}

	@Override
	public void appendTo(XMLElement parent) {
		XMLElement e = parent.add("Line");
		if (label != null && !label.isEmpty()) {
			e.setValue("label", label);
		}
		e.setText(text);
	}
}
