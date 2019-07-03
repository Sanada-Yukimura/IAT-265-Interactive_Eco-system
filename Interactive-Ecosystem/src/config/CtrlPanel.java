package config;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import fish.Fish;

public class CtrlPanel extends JPanel {

	private JPanel outputButtonPanel;
	private JButton energyAddButton;
	private JButton exitProgram;
	private JButton addPredatorFish;
	private JButton addPreyFish;

	private JPanel sortArea;
	private JRadioButton energySort;
	private JRadioButton energyAndSizeSort;
	private JRadioButton sizeSort;

	public boolean addPredatorToggle;
	public boolean addPreyToggle;

	public boolean sortByEnergyToggle;
	public boolean sortByEnergyAndSizeToggle;
	public boolean sortBySizeToggle;
	private JTextField fishEnergy, fishHungerStatus;
	private Fish selectThis = null;

	private JPanel sfxPanel;
	public boolean eatSoundToggle = true;
	public boolean hatchSoundToggle = true;
	public boolean deathSoundToggle = true;
	public boolean ambienceToggle = true;
	private JCheckBox eatSFX;
	private JCheckBox hatchSFX;
	private JCheckBox deathSFX;
	private JCheckBox ambienceMusic;

	private JPanel informationArea;

	public CtrlPanel() {
		super();
		setPreferredSize(new Dimension(380, 800));
		initComponents();

		TitledBorder topLevelBorder = BorderFactory.createTitledBorder("Fish Control Panel");
		topLevelBorder.setTitleJustification(TitledBorder.CENTER);

		setBorder(topLevelBorder);

		setLayout(new FlowLayout(FlowLayout.LEADING, 35, 15));
		add(informationArea);
		add(outputButtonPanel);
		add(sortArea);
		add(sfxPanel);
	}

	private void initComponents() {

		// 2nd panels
		String[] label = { "Energy Level   : ", "Hunger Status : " };
		int totalWidgets = label.length;
		JLabel[] labels = new JLabel[totalWidgets];
		JTextField[] textFields = new JTextField[totalWidgets];

		informationArea = new JPanel();
		informationArea.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		TitledBorder informationBorder = BorderFactory.createTitledBorder("Current Fish Information");
		informationBorder.setTitleJustification(TitledBorder.CENTER);
		informationArea.setBorder(informationBorder);
		for (int i = 0; i < totalWidgets; i++) {
			JLabel currentLabel = new JLabel(label[i], JLabel.TRAILING);
			c.fill = GridBagConstraints.HORIZONTAL;
			c.weightx = 0.0;
			c.gridx = 0;
			c.gridy = i;

			c.insets = new Insets(10, 0, 0, 3);
			informationArea.add(currentLabel, c);

			textFields[i] = new JTextField(18);
			textFields[i].setHorizontalAlignment(SwingConstants.CENTER);
			textFields[i].setFocusable(false);
			currentLabel.setLabelFor(textFields[i]);

			c.fill = GridBagConstraints.HORIZONTAL;
			c.weightx = 0.0;
			c.gridx = 1;
			c.gridy = i;

			c.insets = new Insets(10, 0, 0, 0);
			informationArea.add(textFields[i], c);
		}

		fishEnergy = textFields[0];
		fishHungerStatus = textFields[1];

		outputButtonPanel = new JPanel();
		TitledBorder moddingBorder = BorderFactory.createTitledBorder("Modifications Area");
		moddingBorder.setTitleJustification(TitledBorder.CENTER);
		outputButtonPanel.setBorder(moddingBorder);
		outputButtonPanel.setLayout(new GridBagLayout());

		// One action to modify the individual animal's behavior
		JLabel energyText = new JLabel("Energy Add : ");
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.0;
		c.insets = new Insets(20, 0, 0, 0);
		c.gridx = 0;
		c.gridy = 0;
		outputButtonPanel.add(energyText, c);

		JTextField energyInput = new JTextField(13);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.0;
		c.insets = new Insets(20, 0, 0, 8);
		c.gridx = 1;
		c.gridy = 0;
		energyInput.setFocusable(true);
		outputButtonPanel.add(energyInput, c);

		energyAddButton = new JButton("Add");
		c.fill = GridBagConstraints.HORIZONTAL;
		energyAddButton.setPreferredSize(new Dimension(60, 20));
		c.weightx = 0.0;
		c.insets = new Insets(20, 0, 0, 0);
		c.gridx = 2;
		c.gridy = 0;
		outputButtonPanel.add(energyAddButton, c);

		energyAddButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				// if a number that is too long is entered: NumberFormatException
				// if a character is entered: NumberFormatException

				if ((selectThis != null)) {
					try {
						int currentEnergy = selectThis.getEnergy();
						selectThis.setEnergy(currentEnergy += Integer.parseInt(energyInput.getText()));

						// "Textfields that take user input should be cleared after the values have been
						// used."
						energyInput.setText("");
					} catch (NumberFormatException nfe) {
						System.out.println("You have entered an invalid integer value.");
						System.out.println("You entered: " + energyInput.getText());
						energyInput.setText("");

					}
				}
			}
		});

		addPredatorFish = new JButton("Add Fish Predator");
		c.fill = GridBagConstraints.HORIZONTAL;
		addPredatorFish.setPreferredSize(new Dimension(120, 20));
		c.weightx = 0.0;
		c.insets = new Insets(20, 0, 0, 0);
		c.gridx = 1;
		c.gridy = 1;
		outputButtonPanel.add(addPredatorFish, c);

		addPredatorFish.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				addPredatorToggle = true;
			}
		});

		addPreyFish = new JButton("Add Fish Prey");
		c.fill = GridBagConstraints.HORIZONTAL;
		addPreyFish.setPreferredSize(new Dimension(120, 20));
		c.weightx = 0.0;
		c.insets = new Insets(20, 0, 0, 0);
		c.gridx = 1;
		c.gridy = 2;
		outputButtonPanel.add(addPreyFish, c);

		addPreyFish.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				addPreyToggle = true;
			}
		});

		// One action that affects the application as a whole.
		exitProgram = new JButton("Exit Simulation");
		c.fill = GridBagConstraints.HORIZONTAL;
		exitProgram.setPreferredSize(new Dimension(120, 20));
		c.weightx = 0.0;
		c.insets = new Insets(20, 0, 0, 0);
		c.gridx = 1;
		c.gridy = 3;
		outputButtonPanel.add(exitProgram, c);

		exitProgram.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});

		sortArea = new JPanel();
		sortArea.setLayout(new GridBagLayout());
		GridBagConstraints d = new GridBagConstraints();
		TitledBorder sortBorder = BorderFactory.createTitledBorder("Current Fish Information");
		sortBorder.setTitleJustification(TitledBorder.CENTER);
		sortArea.setBorder(sortBorder);
		sortArea.setLayout(new GridBagLayout());

		energySort = new JRadioButton("Sort by energy level");
		d.fill = GridBagConstraints.HORIZONTAL;
		energySort.setPreferredSize(new Dimension(120, 20));
		d.weightx = 0.0;
		d.insets = new Insets(20, 0, 0, 0);
		d.gridx = 3;
		d.gridy = 1;
		sortArea.add(energySort, d);

		energySort.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sortByEnergyToggle = true;
				sortBySizeToggle = false;
				sortByEnergyAndSizeToggle = false;
			}
		});

		energyAndSizeSort = new JRadioButton("Sort by energy and size");
		d.fill = GridBagConstraints.HORIZONTAL;
		energyAndSizeSort.setPreferredSize(new Dimension(120, 20));
		d.weightx = 0.0;
		d.insets = new Insets(20, 0, 0, 0);
		d.gridx = 3;
		d.gridy = 2;
		sortArea.add(energyAndSizeSort, d);

		energyAndSizeSort.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sortByEnergyAndSizeToggle = true;
				sortBySizeToggle = false;
				sortByEnergyToggle = false;
			}
		});

		sizeSort = new JRadioButton("Sort by size");
		d.fill = GridBagConstraints.HORIZONTAL;
		sizeSort.setPreferredSize(new Dimension(180, 20));
		d.weightx = 0.0;
		d.insets = new Insets(20, 0, 0, 0);
		d.gridx = 3;
		d.gridy = 3;
		sortArea.add(sizeSort, d);

		sizeSort.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sortBySizeToggle = true;
				sortByEnergyToggle = false;
				sortByEnergyAndSizeToggle = false;
			}
		});

		ButtonGroup radioButtons = new ButtonGroup();
		radioButtons.add(energySort);
		radioButtons.add(energyAndSizeSort);
		radioButtons.add(sizeSort);

		sfxPanel = new JPanel();
		TitledBorder soundPanel = BorderFactory.createTitledBorder("SFX toggle");
		sfxPanel.setLayout(new GridBagLayout());
		GridBagConstraints e = new GridBagConstraints();
		soundPanel.setTitleJustification(TitledBorder.CENTER);
		sfxPanel.setBorder(soundPanel);
		sfxPanel.setLayout(new GridBagLayout());

		eatSFX = new JCheckBox("Eating");
		eatSFX.setSelected(true);
		e.fill = GridBagConstraints.HORIZONTAL;
		eatSFX.setPreferredSize(new Dimension(100, 20));
		e.weightx = 0.0;
		e.insets = new Insets(0, 0, 0, 0);
		e.gridx = 4;
		e.gridy = 0;
		sfxPanel.add(eatSFX, e);

		eatSFX.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if (eatSoundToggle) {
					eatSoundToggle = false;
				} else {
					eatSoundToggle = true;
				}
			}
		});

		hatchSFX = new JCheckBox("Hatching");
		hatchSFX.setSelected(true);
		e.fill = GridBagConstraints.HORIZONTAL;
		hatchSFX.setPreferredSize(new Dimension(100, 20));
		e.weightx = 0.0;
		e.insets = new Insets(20, 0, 0, 0);
		e.gridx = 4;
		e.gridy = 1;
		sfxPanel.add(hatchSFX, e);

		hatchSFX.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if (hatchSoundToggle) {
					hatchSoundToggle = false;
				} else {
					hatchSoundToggle = true;
				}
			}
		});

		deathSFX = new JCheckBox("Death");
		deathSFX.setSelected(true);
		e.fill = GridBagConstraints.HORIZONTAL;
		deathSFX.setPreferredSize(new Dimension(100, 20));
		e.weightx = 0.0;
		e.insets = new Insets(20, 0, 0, 0);
		e.gridx = 4;
		e.gridy = 2;
		sfxPanel.add(deathSFX, e);

		deathSFX.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if (deathSoundToggle) {
					deathSoundToggle = false;
				} else {
					deathSoundToggle = true;
				}
			}
		});

		ambienceMusic = new JCheckBox("Ambience");
		ambienceMusic.setSelected(true);
		e.fill = GridBagConstraints.HORIZONTAL;
		ambienceMusic.setPreferredSize(new Dimension(100, 20));
		e.weightx = 0.0;
		e.insets = new Insets(20, 0, 0, 0);
		e.gridx = 4;
		e.gridy = 3;
		sfxPanel.add(ambienceMusic, e);

		ambienceMusic.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if (ambienceToggle) {
					ambienceToggle = false;
				} else {
					ambienceToggle = true;
				}
			}
		});

	}

	public void update(Fish f) {
		selectThis = f;
		if (f != null) {
			fishEnergy.setText(String.format("%.2f%%", f.getEnergy() / 5f));
			fishEnergy.setEnabled(true);
			try {
				fishHungerStatus.setForeground(new Color(f.hungerRed, f.hungerGreen, 0));
			} catch (IllegalArgumentException iae) {
				System.out.println("");
			}
			if (f.getEnergy() <= 350) {
				fishHungerStatus.setText(String.format("Hungry"));
				fishHungerStatus.setEnabled(true);
			}
			if (f.getEnergy() > 350) {

				fishHungerStatus.setText(String.format("Satisfied"));
				fishHungerStatus.setEnabled(true);
			}

		} else {
			fishEnergy.setText("");
			fishHungerStatus.setText("");
			fishHungerStatus.setEnabled(false);

		}
	}

}
