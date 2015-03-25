package com;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;

import com.SpecialMovesList.SpecialMoveAttribute;
import com.scripts.Script;
import com.scripts.ScriptComparator;

public class MeleeEdit extends JPanel implements ActionListener {

	public static final int MENU_ATTRIBUTES = 0, MENU_ATTACKS = 1,
			MENU_SPECIAL_MOVES = 20, MENU_ALL = 2, MENU_OTHER = 5,
			MENU_ANIMATION = 3, MENU_SPECIAL_ATTRIBUTES = 4;

	public static int selected = 0, selectedSubaction = 0, selectedMenu = 0;

	public static String[] options = { "Attributes",
			"Subactions (Attacks only)",// "Subactions (Special moves)",
			"Subactions (All)", "Animation Swapping", "Special Attributes", "Other",
	// "Special Moves",
	// "Frames Speed Modifiers",
	//
	};

	public static JFrame frame;
	public JButton saveButton;
	public JMenuItem saveSubactionButton, loadSubactionButton;
	public static JTable attributeTable, attributeTable2;
	public JScrollPane aPane, SApane;

	public JScrollPane scripts;
	public JComboBox charList;
	
	//Playing around with this, gonna see how it looks in the program.
	public static JMenuBar fileMenu;

	public static JComboBox subactionList;

	public static JComboBox subactionList2;

	public JComboBox specialList;
	public JComboBox optionList;
	public JPanel comboPane, scriptPanel;// ,specialPanel;

	public static RestorePanel restorePane;
	public static AnimationPanel animationPanel;

	public static JPanel scriptInner;

	public MeleeEdit() {
		super(new BorderLayout());
		
		SpecialMovesList.load();

		String[] tmp = new String[Character.characters.length];
		for (int i = 0; i < tmp.length; i++) {
			tmp[i] = ""+i;
		}

		String[] tmp2 = new String[SubAction.subActions.length];
		for (int i = 0; i < tmp2.length; i++) {
			tmp2[i] = SubAction.subActions[i].name;
		}

		charList = new JComboBox(tmp);
		charList.setSelectedIndex(0);
		charList.setEditable(false);
		
		ComboBoxRenderer renderer = new ComboBoxRenderer();
		renderer.setPreferredSize(new Dimension(64,58));
		charList.setRenderer(renderer);
		charList.setPreferredSize(new Dimension(100,70));
		charList.setMaximumSize(charList.getPreferredSize());
		charList.addActionListener(new CharListener());
		charList.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		optionList = new JComboBox(options);
		optionList.setSelectedIndex(0);
		
		optionList.setPreferredSize(new Dimension(200, 40));
		optionList.setMaximumSize(optionList.getPreferredSize());
		
		optionList.addActionListener(new OptionListener());
		optionList.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		subactionList = new JComboBox(tmp2);
		subactionList.setSelectedIndex(0);
		subactionList.addActionListener(new SubactionListener());
		subactionList.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		subactionList2 = new JComboBox(FileIO.getDefaultSubactions());
		subactionList2.setSelectedIndex(0);
		subactionList2.addActionListener(new SubactionListener());
		subactionList2.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		comboPane = new JPanel();
		comboPane.setLayout(new BoxLayout(comboPane, BoxLayout.LINE_AXIS));
		comboPane.add(charList);
		comboPane.add(Box.createHorizontalGlue());
		comboPane.add(optionList);
		comboPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		saveButton = new JButton("save");
		saveButton.setActionCommand("save");
		saveButton.addActionListener(new SaveListener());

		FileIO.init();

		restorePane = new RestorePanel();

		attributeTable = new JTable(new AttributeTable());
		// attributeTable.setPreferredScrollableViewportSize(new Dimension(700,
		// 600));
		attributeTable.setFillsViewportHeight(true);
		attributeTable.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		attributeTable2 = new JTable(new SpecialAttributeTable());
		attributeTable2.setFillsViewportHeight(true);
		attributeTable2.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		SApane = new JScrollPane(attributeTable2);
		SApane.setPreferredSize(new Dimension(700, 500));
		
		scriptInner = new JPanel();
		scriptInner.setLayout(new BoxLayout(scriptInner, BoxLayout.PAGE_AXIS));

		FileIO.readScripts();

		// j.setPreferredSize(new Dimension(300,400);

		animationPanel = new AnimationPanel();

		// Create the scroll pane and add the table to it.
		aPane = new JScrollPane(attributeTable);
		aPane.setPreferredSize(new Dimension(700, 500));

		scripts = new JScrollPane(scriptInner);
		scripts.setPreferredSize(new Dimension(700, 600));
		scripts.getVerticalScrollBar().setUnitIncrement(10);

		scriptPanel = new JPanel();
		scriptPanel.setLayout(new BoxLayout(scriptPanel, BoxLayout.PAGE_AXIS));

		scriptPanel.add(subactionList);
		scriptPanel.add(scripts);
		
		
		if(fileMenu==null){
			fileMenu = new JMenuBar();
			
			JMenu menu = new JMenu("File");
			JMenu runMenu = new JMenu("Run");
			JMenu optionsMenu = new JMenu("Options");
			
			fileListener fl = new fileListener();
			
				JMenuItem openButton = new JMenuItem("Open ISO");
					openButton.setActionCommand("openISO");
					openButton.addActionListener(fl);
				JMenuItem closeButton = new JMenuItem("Close");
					closeButton.addActionListener(fl);
					closeButton.setActionCommand("close");
				saveSubactionButton = new JMenuItem("Save subaction");
					saveSubactionButton.addActionListener(fl);
					saveSubactionButton.setActionCommand("savesubaction");
					saveSubactionButton.setEnabled(false);
				loadSubactionButton = new JMenuItem("Load subaction");
					loadSubactionButton.addActionListener(fl);
					loadSubactionButton.setActionCommand("loadsubaction");
					loadSubactionButton.setEnabled(false);
				JMenuItem m = new JMenuItem();
					m.setEnabled(false);
				
			menu.add(openButton);
			menu.add(m);
			
			// * I half-assed these functionalities; I'll have them done right soon.
			/*
			menu.add(saveSubactionButton);
			menu.add(loadSubactionButton);
			*/
			menu.add(closeButton);
			
				JMenuItem dolphinButton = new JMenuItem("Run loaded ISO in Dolphin");
				if(!System.getProperty("os.name").startsWith("Windows")){
					dolphinButton.setToolTipText("This currently only works for windows!");
					dolphinButton.setEnabled(false);
				}
				dolphinButton.addActionListener(fl);
				dolphinButton.setActionCommand("runDolphin");
				
			runMenu.add(dolphinButton);
			
			
			
				optionsMenu.setActionCommand("options");
				optionsMenu.addActionListener(fl);
			/*
			JButton helpButton = new JButton("Help");
			helpButton.setBackground(new Color(0xEBEBEB));
			helpButton.setBorder(BorderFactory.createEmptyBorder());
			helpButton.addActionListener(new helpListener());
			*/
			
			fileMenu.add(menu);
			fileMenu.add(Box.createHorizontalStrut(5));
			fileMenu.add(runMenu);
			
			//fileMenu.add(Box.createHorizontalStrut(5));
			//fileMenu.add(optionsMenu);
			
			//fileMenu.add(helpButton);
		}

		// specialPanel = new JPanel();
		add(comboPane, BorderLayout.PAGE_START);
		add(aPane, BorderLayout.CENTER);
		add(saveButton, BorderLayout.PAGE_END);
		
		// FileIO.loadedISOFile.close();

		// try {
		// Runtime.getRuntime().exec("cmd.exe /c start");
		// Runtime.getRuntime().exec("GCReEx.exe -x a.iso");
		// System.out.println("ok");
		// } catch (IOException ex) {
		// ex.printStackTrace();
		// }

	}
	
	class ComboBoxRenderer extends JLabel implements ListCellRenderer {
				
		public ComboBoxRenderer() {
			setOpaque(true);
			setHorizontalAlignment(CENTER);
			setVerticalAlignment(CENTER);
		}
		
		public Component getListCellRendererComponent(
				                    JList list,
				                    Object value,
				                    int index,
				                    boolean isSelected,
				                    boolean cellHasFocus) {
			
			int selectedIndex = Integer.parseInt(((String)value));
			
			if(selectedIndex<0){return this;}
			
			if (isSelected) {
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());
			}
			else {
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}
			
			ImageIcon icon = Character.characters[selectedIndex].characterIcon;
			if (icon != null && icon.getImage() != null) {
				setFont(list.getFont());
				setIcon(icon);
			}
			
				return this;
		}
				
	}
	
	class fileListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if(e.getActionCommand()=="openISO"){
				FileIO.loadISOFile();
			}
			else if(e.getActionCommand()=="close"){
				Container comp = getParent();
				Container comp2 = null;
				while(comp != null){
					comp2 = comp;
					comp = comp2.getParent();
				}
				if(comp2 instanceof JFrame){
					((JFrame)comp2).dispose();
				}
			}
			else if(e.getActionCommand()=="runDolphin"){
				Options.openDolphin();
			}
			else if(e.getActionCommand()=="savesubaction"){
				FileIO.saveSubaction();
			}
			else if(e.getActionCommand()=="loadsubaction"){
				FileIO.loadSubaction();
			}
		}
		
	}

	class helpListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			HelpWindow w = new HelpWindow();
			
			w.show();
			
		}
		
	}

	class SaveListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			try {
				FileIO.loadedISOFile.reload();
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
				return;

			}

			if (selectedMenu == MENU_ATTRIBUTES || selectedMenu == MENU_SPECIAL_ATTRIBUTES) {
				FileIO.save();
			}
			if (selectedMenu == MENU_ATTACKS || selectedMenu == MENU_ALL) {
				FileIO.init();
				for (Script script : Script.scripts) {
					script.save();
				}

				FileIO.init();
				FileIO.readScripts();
				frame.pack();
			}
			if (selectedMenu == MENU_ANIMATION) {
				// FileIO.init();
				for (AnimationNode n : animationPanel.nodes) {
					n.save();
				}
			}

			try {
				FileIO.isoFileSystem
						.replaceFile(FileIO.isoFileSystem.getCurrentFile(),
								FileIO.f.array());

			} catch (IOException e2) {
				e2.printStackTrace();
			}

			FileIO.loadedISOFile.close();

			System.out.println("Save Complete!");

		}

	}

	class CharListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			JComboBox cb = (JComboBox) e.getSource();
			selected = cb.getSelectedIndex();

			if (selectedMenu == MENU_ATTRIBUTES) {
				updateAttributes();
			}
			if(selectedMenu == MENU_SPECIAL_ATTRIBUTES){
				if(SpecialMovesList.getSpecialAttributesForCharacter(selected) != null){
					updateSpecialAttributes();
				}
				else
				{
					selectedMenu = MENU_ATTRIBUTES;
					optionList.setSelectedIndex(MENU_ATTRIBUTES);
					updateAttributes();
				}
			}
			if (selectedMenu == MENU_ATTACKS || selectedMenu == MENU_ALL) {
				updateSubactions();
			}
			if (selectedMenu == MENU_ANIMATION) {
				updateAnimations();
			}

			frame.pack();

			FileIO.loadedISOFile.close();
			System.out.println("Character Selection Updated");
		}
	}

	public static void updateAttributes() {
		FileIO.init();
		FileIO.setPosition(Character.characters[MeleeEdit.selected].offset);
		for (int i = 0; i < Attribute.attributes.length; i++) {
			attributeTable.setValueAt(FileIO.readFloat(), i, 1);
		}
	}
	
	public void updateSpecialAttributes() {
		
		FileIO.init();

		boolean b = false;
		for(int i = 0; i < this.getComponentCount(); i ++) {
			if(this.getComponent(i) == SApane){
				b = true;
			}
		}
		
		if(b)
		this.remove(SApane);
		
		attributeTable2 = new JTable(new SpecialAttributeTable());
		attributeTable2.setFillsViewportHeight(true);
		attributeTable2.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		SApane = new JScrollPane(attributeTable2);
		SApane.setPreferredSize(new Dimension(700, 500));
		
		
		this.add(SApane);
	}

	public static void updateSubactions() {
		FileIO.init();
		FileIO.readScripts();

		// updates the "all" subactions list for the new character.
		// I might move this to a function later on. --Ampers
		subactionList2.removeAllItems();
		subactionList.removeAllItems();
		String[] tmp = FileIO.getDefaultSubactions();
		for (int i = 0; i < tmp.length; i++) {
			subactionList2.addItem(tmp[i]);
		}
		for(int i = 0; i < SubAction.subActions.length; i ++)
		{
			subactionList.addItem(SubAction.subActions[i].name);
		}
		SubAction[] moves=SpecialMovesList.getListForCharacter(selected);
		for(int i = 0; i < moves.length; i ++)
		{
			subactionList.addItem(moves[i].name);
		}
	}

	public void updateAnimations() {
		FileIO.init();
		animationPanel.refresh();

		add(animationPanel, BorderLayout.CENTER);
	}

	class OptionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			JComboBox cb = (JComboBox) e.getSource();
			selectedMenu = cb.getSelectedIndex();
			
			saveSubactionButton.setEnabled(false);
			loadSubactionButton.setEnabled(false);
			
			removeAll();
			add(comboPane, BorderLayout.PAGE_START);
			add(saveButton, BorderLayout.PAGE_END);
			
			if(selectedMenu == MENU_SPECIAL_ATTRIBUTES) {
				/*
				FileIO.init();
				FileIO.readScriptsWithinRange(0x00003624/6/4, 0x000037A8/6/4);
				
				scriptPanel.remove(subactionList2);
				scriptPanel.remove(scripts);
				scriptPanel.add(subactionList);
				scriptPanel.add(scripts);

				add(scriptPanel, BorderLayout.CENTER);
				*/
				
				if(SpecialMovesList.getSpecialAttributesForCharacter(selected) != null){
					updateSpecialAttributes();
				}
				else {
					MeleeEdit.selectedMenu = MENU_ATTRIBUTES;
					optionList.setSelectedIndex(MENU_ATTRIBUTES);
					add(aPane, BorderLayout.CENTER);
					updateAttributes();
				}
			}
			
			if (selectedMenu == MENU_ATTRIBUTES) {

				add(aPane, BorderLayout.CENTER);
				// comboPane.remove(subactionList);

				updateAttributes();

			}
			if (selectedMenu == MENU_ATTACKS) {
				saveSubactionButton.setEnabled(true);
				loadSubactionButton.setEnabled(true);
				scriptPanel.remove(subactionList2);
				scriptPanel.remove(scripts);
				// scriptPanel.remove(specialPanel);
				scriptPanel.add(subactionList);
				scriptPanel.add(scripts);

				add(scriptPanel, BorderLayout.CENTER);
				// comboPane.add(subactionList);

				updateSubactions();
			}
			if (selectedMenu == MENU_SPECIAL_MOVES) {
				// scriptPanel.remove(subactionList);
				// scriptPanel.remove(scripts);
				// scriptPanel.remove(subactionList2);

				// refreshSpecialMoves();

				// scriptPanel.add(specialPanel);
				// scriptPanel.add(scripts);

				// add(scriptPanel, BorderLayout.CENTER);

				// //comboPane.add(subactionList);
			}
			if (selectedMenu == MENU_ALL) {
				saveSubactionButton.setEnabled(true);
				loadSubactionButton.setEnabled(true);
				scriptPanel.remove(subactionList);
				scriptPanel.remove(scripts);
				// scriptPanel.remove(specialPanel);
				scriptPanel.add(subactionList2);
				scriptPanel.add(scripts);

				add(scriptPanel, BorderLayout.CENTER);
				// comboPane.add(subactionList);

				updateSubactions();
			}
			if (selectedMenu == MENU_OTHER) {
				remove(saveButton);
				add(restorePane, BorderLayout.CENTER);
			}
			if (selectedMenu == MENU_ANIMATION) {
				add(animationPanel, BorderLayout.CENTER);

				updateAnimations();
			}

			frame.pack();
			System.out.println("Option Selection Updated");
		}
	}

	public class SubactionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			JComboBox cb = (JComboBox) e.getSource();

			selectedSubaction = cb.getSelectedIndex();

			FileIO.readScripts();
		}
	}

	class AttributeTable extends AbstractTableModel {
		public String[] columnNames = { "Attribute", "Value", "Info", };
		public Object[][] data = initGrid();

		public Object[][] initGrid() {
			FileIO.setPosition(Character.characters[MeleeEdit.selected].offset);
			Object[][] tmp = new Object[Attribute.attributes.length][3];
			for (int i = 0; i < Attribute.attributes.length; i++) {
				tmp[i][0] = Attribute.attributes[i].name;
				tmp[i][1] = FileIO.readFloat();
				if (Attribute.attributes[i].name.equals("????"))
					tmp[i][2] = "Don't modify.";
				else
					tmp[i][2] = Attribute.attributes[i].info;
			}
			return tmp;
		}

		public int getColumnCount() {
			return columnNames.length;
		}

		public int getRowCount() {
			return data.length;
		}

		public String getColumnName(int col) {
			return columnNames[col];
		}

		public Object getValueAt(int row, int col) {
			return data[row][col];
		}

		public Class getColumnClass(int c) {
			return getValueAt(0, c).getClass();
		}

		public boolean isCellEditable(int row, int col) {
			if (col == 1)
				return true;
			else
				return false;
		}

		public void setValueAt(Object value, int row, int col) {
			if (value == null)
				return;
			data[row][col] = value;
			fireTableCellUpdated(row, col);
		}
	}
	
	class SpecialAttributeTable extends AbstractTableModel {
		public String[] columnNames = { "Attribute", "Value", "Used for", "Info"};
		public Object[][] data = initGrid();

		public Object[][] initGrid() {
			SpecialMoveAttribute[] list = SpecialMovesList.getSpecialAttributesForCharacter(selected);
			System.out.println("Loading special move attributes");
			Object[][] tmp = new Object[SpecialMovesList.getSpecialAttributesForCharacter(selected).length][4];
			for (int i = 0; i < list.length; i++) {
				tmp[i][0] = list[i].name;

					if(list[i].isInt){
						FileIO.setPosition(list[i].loc);
						tmp[i][1] = Float.parseFloat(""+FileIO.readInt());
					}
					else{
						FileIO.setPosition(list[i].loc);
						tmp[i][1] = FileIO.readFloat();
					}
					
					if (list[i].name.equals("????"))
						tmp[i][3] = "Unsure what this value is.";
					else
						tmp[i][3] = list[i].info;
					
					if(list[i].isInt){
						tmp[i][3] += " (Integer values only!)";
					}
					
					tmp[i][2] = list[i].associatedMove;
					
			}
			return tmp;
		}

		public int getColumnCount() {
			return columnNames.length;
		}

		public int getRowCount() {
			return data.length;
		}

		public String getColumnName(int col) {
			return columnNames[col];
		}

		public Object getValueAt(int row, int col) {
			return data[row][col];
		}

		public Class getColumnClass(int c) {
			return getValueAt(0, c).getClass();
		}

		public boolean isCellEditable(int row, int col) {
			if (col == 1)
				return true;
			else
				return false;
		}

		public void setValueAt(Object value, int row, int col) {
			if (value == null)
				return;
			data[row][col] = value;
			fireTableCellUpdated(row, col);
		}
	}

	public static void main(String[] args) throws IOException {
		Options.loadOptions();
		FileIO.loadISOFile();
		FileIO.init();
		// FileIO.declareAnims();
		// SpecialMovesList.load();

		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				// Create and set up the window.
				frame = new JFrame();
				updateTitle(FileIO.loadedISOFile.getChosenISOFile().getName());
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

				ImageIcon img = new ImageIcon("img/hand.png");
				frame.setIconImage(img.getImage());

				// Create and set up the content pane.
				MeleeEdit contentPane = new MeleeEdit();

				//
				//

				contentPane.setOpaque(true);
				frame.setContentPane(contentPane);
				frame.setJMenuBar(fileMenu);
				// Display the window.
				frame.pack();
				frame.setVisible(true);

			}
		});
		
		
		Options.saveOptions();
		

	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// not used, but required

	}

	// I won't lie to you guys, this code is a clusterfuck and I'm surprised
	// it's functional
	// given that I did the programming equivalent of throwing fish at a wall
	// until one turned into pasta
	// But it works and it *is* comprehensible, just confusing and probably not
	// optimized.
	public static void changeScripts(int old, boolean movingDown) {

		int n = movingDown ? old : old + 1;

		if (n < 1 || n > Script.scripts.size() - 1) {
			System.out.println("Moving scripts out of bounds! Values: " + old
					+ "," + n);
			return;
		}

		if (movingDown)
			old -= 1;

		Script tmp = Script.scripts.get(old);
		Script tmp2 = Script.scripts.get(n);

		int to = tmp.location;
		int tn = tmp2.location;

		if (Event.getEvent(tmp.id).length > Event.getEvent(tmp2.id).length) {
			if (tmp.location > tmp2.location) {
				to = tn;
				tn = tn + Event.getEvent(tmp.id).length;
			} else {
				tn = to;
				to = tn + Event.getEvent(tmp2.id).length;
			}
		} else if (Event.getEvent(tmp.id).length < Event.getEvent(tmp2.id).length) {
			if (tmp.location > tmp2.location) {
				to = tn;
				tn = to + Event.getEvent(tmp2.id).length;
			} else {
				tn = to;
				to = tn + Event.getEvent(tmp2.id).length;
			}
		} else {
			if (tmp.location > tmp2.location) {
				to = tn;
				tn = to + Event.getEvent(tmp.id).length;
			} else {
				tn = to;
				to = tn + Event.getEvent(tmp.id).length;
			}
		}

		tmp.location = to;
		tmp2.location = tn;

		tmp.updateNametag();
		tmp2.updateNametag();

		tmp.updateData();
		tmp2.updateData();

		if (tmp.location > tmp2.location) {
			Script.scripts.set(old, tmp);
			Script.scripts.set(n, tmp2);
		} else {
			Script.scripts.set(old, tmp2);
			Script.scripts.set(n, tmp);
		}

		tmp = Script.scripts.get(old);
		tmp2 = Script.scripts.get(n);

		FileIO.init();
		for (Script script : Script.scripts) {
			script.save();
		}

		FileIO.init();
		FileIO.readScripts();

		MeleeEdit.scriptInner.updateUI();

	}

	public static void setScripts() {
		int i = -1;

		scriptInner.removeAll();

		Collections.sort(Script.scripts, new ScriptComparator());

		for (Script script : Script.scripts) {
			i++;

			scriptInner.add(Script.scripts.get(i));

			byte[] tempData = { 4, 53, 6 };
			// j.add(new HitboxScript(tempData));

			JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
			sep.setBackground(Color.BLUE);
			scriptInner.add(sep);
			// JPanel box = Box.createVerticalStrut(10);
			scriptInner.add(Box.createVerticalStrut(30));

			sep = new JSeparator(SwingConstants.HORIZONTAL);
			sep.setBackground(Color.BLUE);
			scriptInner.add(sep);
			// j.add(Box.createVerticalStrut(5));

		}
		frame.pack();
	}

	/**
	 * Refreshes the data values.
	 */
	public static void refreshData() {
		// currently this is only used for when restoring characters to defaults
		// It refreshes the subactions values, etc to reflect the change to
		// default
		FileIO.init();
		FileIO.readScripts();
		FileIO.setPosition(Character.characters[MeleeEdit.selected].offset);
		for (int i = 0; i < Attribute.attributes.length; i++) {
			MeleeEdit.attributeTable.setValueAt(FileIO.readFloat(), i, 1);
		}

	}

	public static void updateTitle(String isoPath) {
		frame.setTitle("Crazy Hand " + Config.VERSION + " [" + isoPath + "]");
	}
}