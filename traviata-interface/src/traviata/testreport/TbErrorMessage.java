package traviata.testreport;

import java.io.Serializable;

import traviata.base.xml.XMLElement;

public class TbErrorMessage implements TbActionItem, Serializable {
	private String text;
	
	public TbErrorMessage() {
	}
	
	public TbErrorMessage(XMLElement e) {
		text = "";
		for (XMLElement s : e.getChildren()) {
			text += s.getText().replace("[TAB]", "\t") + "\n";
		}
	}
	
	public void setText(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}

	@Override
	public void appendTo(XMLElement parent) {
		XMLElement e = parent.add("ErrorMessage");
		for (String z : text.replace("\r", "").split("\n")) {
			e.add("s").setText(z.replace("\t", "[TAB]"));
		}
	}
}