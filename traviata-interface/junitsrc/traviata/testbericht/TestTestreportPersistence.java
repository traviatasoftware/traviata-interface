package traviata.testbericht;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import traviata.base.xml.XMLDocument;
import traviata.testreport.TbAction;
import traviata.testreport.TbBlock;
import traviata.testreport.TbErrorMessage;
import traviata.testreport.TbLine;
import traviata.testreport.TbTable;
import traviata.testreport.TbTestcase;
import traviata.testreport.TbTestreport;

public class TestTestreportPersistence {
	private static final String stacktrace = "java.lang.ClassCastException: Das Konvertieren auf Pussemuckel hat nicht geklappt.\n"
			+ "\tat de.irgendwo.Pussemuckel.troete(Pussemuckel.java:17)";
	
	@Test
	public void tbTestberichtToXML() {
		TbTestreport tb = create();
		XMLDocument dok = tb.getDoc();
		dok.saveFile("temp.xml");
		new File("temp.xml").deleteOnExit();
	}
	
	@Test
	public void xmlToTbTestbericht() {
		XMLDocument dok = create().getDoc();
		TbTestreport tb = new TbTestreport(dok);
		Assert.assertEquals("Testcases <>", 1, tb.getTestcases().size());
		List<TbAction> actions = tb.getTestcases().get(0).getActions();
		Assert.assertEquals("Parameters.size <>", 2, tb.getParameters().size());
		Assert.assertEquals("Parameter <>", "bar", tb.getParameters().get("foo"));
		Assert.assertEquals("Actions <>", 1, actions.size());
		Assert.assertEquals("Items <>", 4, actions.get(0).getItems().size());
		Assert.assertTrue("TbLine <>", actions.get(0).getItems().get(0) instanceof TbLine);
		Assert.assertTrue("TbBlock <>", actions.get(0).getItems().get(1) instanceof TbBlock);
		Assert.assertEquals("TbBlock.title <>", "A block.", ((TbBlock) actions.get(0).getItems().get(1)).getTitle());
		Assert.assertTrue("TbTable <>", actions.get(0).getItems().get(2) instanceof TbTable);
		Assert.assertTrue("TbErrorMessage <>", actions.get(0).getItems().get(3) instanceof TbErrorMessage);
		Assert.assertFalse("TbErrorMessage.text <>", ((TbErrorMessage) actions.get(0).getItems().get(3)).getText().isEmpty());
		Assert.assertNotNull("TbTable.title <>", ((TbTable) actions.get(0).getItems().get(2)).getTitle());
		Assert.assertFalse("TbTable.headers <>", ((TbTable) actions.get(0).getItems().get(2)).getHeaders().isEmpty());
		Assert.assertFalse("TbTable.lines <>", ((TbTable) actions.get(0).getItems().get(2)).getLines().isEmpty());
		Assert.assertEquals("stacktrace <>", stacktrace, ((TbErrorMessage) actions.get(0).getItems().get(3)).getText());
	}

	private TbTestreport create() {
		TbTestreport tb = new TbTestreport();
		tb.setPar("foo", "bar");
		tb.setPar("0815", "4711");

		TbTestcase tf = new TbTestcase();
		tf.setStarttime(new SimpleDateFormat("HH:mm:ss").format(new java.util.Date()));
		tf.setTitle("Der 1. Testfall");
		tf.setScriptname("vertrag/Neuanlage");
		tf.setScript("GPVertragNeuanlage\n\tPartnersuche\n\t\tperson = true\nGPVertragAuskunft\n");
		tf.setSize(1);
		tf.setCurrentNumber(1);
		tf.setRunNumber(1);
		tf.getVars().put("hey", "ho");
		tf.setEndtime(new SimpleDateFormat("HH:mm:ss").format(new java.util.Date()));
		tb.getTestcases().add(tf);
		
		TbAction gp = TbAction.empty();
		gp.setTitle("Vertrag:Neuanlage");
		tf.getActions().add(gp);
		
		TbLine z = new TbLine("Vertragsnr", "47110815");
		gp.getItems().add(z);
		
		TbBlock b = new TbBlock();
		b.setTitle("A block.");
		z = new TbLine();
		z.setLabel("Tarif");
		z.setText("T1001");
		b.getLines().add(z);
		b.getLines().add(new TbLine("Produktlinie", "Best Selection"));
		tb.addItem(b);
		
		createTabelle(gp);

		gp.addError(stacktrace);
		
		return tb;
	}

	private void createTabelle(TbAction gp) {
		TbTable t = new TbTable();
		t.setTitle("Eine Tabelle.");
		ArrayList<String> u = new ArrayList<String>();
		u.add("Spalte eins");
		u.add("Zwei");
		u.add("Dritte Spalte");
		t.setHeaders(u);
		List<List<String>> zeilen = new ArrayList<List<String>>();
		List<String> zeile;
		zeile = new ArrayList<String>();
		zeile.add("1.0");
		zeile.add("arabische-eins");
		zeile.add("1");
		zeilen.add(zeile);
		t.setLines(zeilen);
		t.addLine("2", "2 (Sp.2)", "2");
		gp.getItems().add(t);
	}
	
	/** XML Text contains \t. If the file was saved the \t was not saved. Solutin: pFormat.setTrimText(false); */
	@Test
	public void tab() {
		final String dn = "temp-tab.xml";
		TbErrorMessage em = new TbErrorMessage(stacktrace);
		XMLDocument doc = new XMLDocument("<a/>");
		em.appendTo(doc.getElement());
		Assert.assertTrue("tab is missing in getXML()", doc.getXML().contains("\t"));
		doc.saveFile(dn);
		doc = null;
		em = null;
		
		File file = new File(dn);
		XMLDocument doc2 = new XMLDocument(file);
		TbErrorMessage em2 = new TbErrorMessage(doc2.getElement().getChildren().get(0));
		Assert.assertEquals("Failure!\n", stacktrace, em2.getText());
		file.deleteOnExit();
	}
	
	@Test
	public void getLastTestcase() {
		TbTestreport.lastTestcase_title = "Fehler";
		TbTestreport tr = new TbTestreport();
		TbTestcase t = tr.getLastTestcase();
		Assert.assertNotNull("getLastTestcase darf nicht null liefern", t);
		// Im Teil B noch testen, ob die (De)Serialisierung dann noch klappt:
		XMLDocument doc = tr.getDoc();
		XMLDocument doc2 = new XMLDocument(doc.getXML());
		TbTestreport tr2 = new TbTestreport(doc2);
		Assert.assertNotNull(tr2.getTestcases().get(0));
	}
}
