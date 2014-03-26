package GUI;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class MainWindow {

	private JFrame frame;
	private JTextField textReplicationDeg;
	private JTextField textPath;
	private JSlider slider;
	private JLabel lblDiskSpace;
	final JFileChooser fc = new JFileChooser();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow window = new MainWindow();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */



	private void initialize() {

		frame = new JFrame();
		frame.setBounds(100, 100, 900, 500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		// My code
		/*JFrame frame = new JFrame ();

		frame.pack ();
		frame.setLocationRelativeTo ( null );*/
		frame.setVisible ( true );

		// create the middle panel components

		JTextArea display = new JTextArea ( 8, 58 );
		display.setEditable ( false ); // set textArea non-editable
		JScrollPane scroll = new JScrollPane ( display );
		scroll.setBounds(10, 199, 864, 252);
		frame.getContentPane().add(scroll);
		scroll.setVerticalScrollBarPolicy ( ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS );


		JLabel lblSpaceOnDisk = new JLabel("Space on disk:");
		lblSpaceOnDisk.setBounds(23, 31, 111, 14);
		frame.getContentPane().add(lblSpaceOnDisk);

		lblDiskSpace = new JLabel("50 MB");
		lblDiskSpace.setBounds(332, 31, 56, 14);
		frame.getContentPane().add(lblDiskSpace);
		
		slider = new JSlider();
		slider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				lblDiskSpace.setText(slider.getValue() + " MB");
			}
		});
		slider.setBounds(122, 27, 200, 23);
		slider.setMinimum(1);
		slider.setMaximum(100);
		frame.getContentPane().add(slider);

		

		JButton btnDelete = new JButton("Delete");
		btnDelete.setBounds(293, 115, 89, 23);
		frame.getContentPane().add(btnDelete);

		JButton btnUpdate = new JButton("Update");
		btnUpdate.setBounds(200, 115, 89, 23);
		frame.getContentPane().add(btnUpdate);

		Box horizontalBox = Box.createHorizontalBox();
		horizontalBox.setBounds(529, 11, 325, 150);
		frame.getContentPane().add(horizontalBox);
		horizontalBox.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Add File", TitledBorder.LEADING, TitledBorder.TOP, null, null));

		Panel panel = new Panel();
		horizontalBox.add(panel);
		panel.setLayout(null);

		JButton btnFind = new JButton("Find File");
		btnFind.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				      int returnVal = fc.showOpenDialog(null);

				      if (returnVal == JFileChooser.APPROVE_OPTION) {
				    	  textPath.setText(fc.getSelectedFile().getAbsolutePath());
				      } else {
	
				      }

				   
			}
		});
		btnFind.setBounds(45, 65, 82, 23);
		panel.add(btnFind);

		JButton btnAdd = new JButton("Add");
		btnAdd.setBounds(242, 93, 61, 23);
		panel.add(btnAdd);

		textReplicationDeg = new JTextField();
		textReplicationDeg.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				char c = e.getKeyChar();
				if (!((c >= '0') && (c <= '9') || (c == KeyEvent.VK_BACK_SPACE) || (c == KeyEvent.VK_DELETE))) {
					e.consume();
				}
			}
		});
		textReplicationDeg.setHorizontalAlignment(SwingConstants.CENTER);
		textReplicationDeg.setText("1");
		textReplicationDeg.setBounds(128, 8, 46, 20);
		panel.add(textReplicationDeg);
		textReplicationDeg.setColumns(10);

		JLabel lblReplicationDegree = new JLabel("Replication Degree");
		lblReplicationDegree.setBounds(10, 11, 117, 14);
		panel.add(lblReplicationDegree);

		textPath = new JTextField();
		textPath.setBounds(42, 33, 261, 20);
		panel.add(textPath);
		textPath.setColumns(10);

		JLabel lblPath = new JLabel("Path");
		lblPath.setBounds(10, 36, 46, 14);
		panel.add(lblPath);

		JLabel lblManageFiles = new JLabel("Manage Files");
		lblManageFiles.setFont(new Font("Tahoma", Font.BOLD, 15));
		lblManageFiles.setBounds(22, 84, 124, 21);
		frame.getContentPane().add(lblManageFiles);

		JComboBox<String> comboBox = new JComboBox<String>();
		comboBox.setModel(new DefaultComboBoxModel<String>(new String[] {"File1", "File2"}));
		comboBox.setBounds(23, 116, 167, 20);
		frame.getContentPane().add(comboBox);

		JLabel lblLog = new JLabel("Log");
		lblLog.setFont(new Font("Tahoma", Font.BOLD, 15));
		lblLog.setBounds(23, 173, 56, 17);
		frame.getContentPane().add(lblLog);

		JButton btnStartService = new JButton("Start Service");
		btnStartService.setBounds(750, 172, 124, 23);
		frame.getContentPane().add(btnStartService);
		
		JButton btnRestore = new JButton("Restore");
		btnRestore.setBounds(387, 115, 89, 23);
		frame.getContentPane().add(btnRestore);
	}
}
