package GUI;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.ThreadLocalRandom;

import javax.swing.Box;
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

import Service.BackupService;
import Service.LocalFile;

public class Window {

	static private JFrame frmBackupservice;
	static private JTextField textReplicationDeg;
	static private JTextField textPath;
	static private JSlider slider;
	static private JLabel lblDiskSpace;
	static final JFileChooser fc = new JFileChooser();
	static private JTextArea display;
	private static DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	private JTextField textMcIp;
	private JTextField textMdbIp;
	private JTextField textMdrIp;
	private JTextField textMdbP;
	private JTextField textMcP;
	private JTextField textMdrP;
	private static BackupService backupService;
	private JComboBox<String> comboBox;
	private JButton btnStartService;
	private static JTextField textVersion;
	private JButton btnDelete;
	private JButton btnUpdate;
	private JButton btnRestore;


	static public void log(String msg) {

		Calendar cal = Calendar.getInstance();
		System.out.println();
		String txt = display.getText() + dateFormat.format(cal.getTime()) + "   "  + msg + "\n";
		display.setText(txt);
		display.setCaretPosition(display.getDocument().getLength());
	}
	/**
	 * Launch the application.
	 */
	public static void main(final String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {

				try {
					Window window = new Window();
					window.frmBackupservice.setVisible(true);

					backupService = new BackupService();


					if(args.length == 6) {
						window.textMcIp.setText(args[0]); 
						window.textMcP.setText(args[1]); 
						window.textMdbIp.setText(args[2]); 
						window.textMdbP.setText(args[3]); 
						window.textMdrIp.setText(args[4]); 
						window.textMdrP.setText(args[5]); 
					}


					ArrayList<LocalFile> files = BackupService.getLocalFiles();

					for (int i = 0; i < files.size(); i++)
						window.comboBox.addItem(files.get(i).getName());

					slider.setValue(BackupService.getDiskSpace()/1000000);

					JLabel lblVersion = new JLabel("Version");
					lblVersion.setBounds(515, 108, 56, 14);
					frmBackupservice.getContentPane().add(lblVersion);

					textVersion = new JTextField();
					textVersion.addKeyListener(new KeyAdapter() {
						@Override
						public void keyTyped(KeyEvent arg0) {
							char c = arg0.getKeyChar();
							if (!((c >= '0') && (c <= '9') || (c == '.') ||(c == KeyEvent.VK_BACK_SPACE) || (c == KeyEvent.VK_DELETE))) {
								arg0.consume();
							}
						}
					});
					textVersion.setHorizontalAlignment(SwingConstants.CENTER);
					textVersion.setText("1.0");
					textVersion.setBounds(564, 105, 46, 20);
					frmBackupservice.getContentPane().add(textVersion);
					textVersion.setColumns(10);




				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Window() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */



	private void initialize() {

		frmBackupservice = new JFrame();
		frmBackupservice.setResizable(false);
		frmBackupservice.setTitle("BackupService");
		frmBackupservice.setBounds(100, 100, 1000, 500);
		frmBackupservice.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmBackupservice.getContentPane().setLayout(null);

		// My code
		/*JFrame frame = new JFrame ();

		frame.pack ();
		frame.setLocationRelativeTo ( null );*/
		frmBackupservice.setVisible ( true );

		// create the middle panel components

		display = new JTextArea ( 8, 58 );
		display.setFont(new Font("Arial", Font.PLAIN, 10));
		display.setEditable ( false ); // set textArea non-editable
		JScrollPane scroll = new JScrollPane ( display );
		scroll.setBounds(10, 199, 964, 252);
		frmBackupservice.getContentPane().add(scroll);
		scroll.setVerticalScrollBarPolicy ( ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS );
		scroll.setAutoscrolls(true);


		JLabel lblSpaceOnDisk = new JLabel("Space on disk:");
		lblSpaceOnDisk.setBounds(140, 111, 111, 14);
		frmBackupservice.getContentPane().add(lblSpaceOnDisk);

		lblDiskSpace = new JLabel("50 MB");
		lblDiskSpace.setBounds(449, 111, 56, 14);
		frmBackupservice.getContentPane().add(lblDiskSpace);

		slider = new JSlider();
		slider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				lblDiskSpace.setText(slider.getValue() + " MB");
			}
		});
		slider.setBounds(239, 107, 200, 23);
		slider.setMinimum(1);
		slider.setMaximum(200);
		frmBackupservice.getContentPane().add(slider);



		btnDelete = new JButton("Delete");
		btnDelete.setEnabled(false);
		btnDelete.setBounds(294, 138, 89, 23);
		frmBackupservice.getContentPane().add(btnDelete);

		btnUpdate = new JButton("Update");
		btnUpdate.setEnabled(false);
		btnUpdate.setBounds(201, 138, 89, 23);
		frmBackupservice.getContentPane().add(btnUpdate);

		Box horizontalBox = Box.createHorizontalBox();
		horizontalBox.setBounds(623, 15, 325, 150);
		frmBackupservice.getContentPane().add(horizontalBox);
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
		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				File f = new File(textPath.getText());
				if (f.exists() && f.isFile()) {
					comboBox.addItem(f.getName());

					if (!btnStartService.isEnabled()) {
						final LocalFile lf = BackupService.addLocalFile(textPath.getText(), textReplicationDeg.getText());
						new Thread(new Runnable() {

							@Override
							public void run() {
								try {
									Thread.sleep(ThreadLocalRandom.current().nextInt(1500,5000));
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
								BackupService.getMdb().backupFile(lf);

							}
						}).start();
					}
				}
			}
		});
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
		lblManageFiles.setBounds(23, 107, 124, 21);
		frmBackupservice.getContentPane().add(lblManageFiles);

		comboBox = new JComboBox<String>();
		comboBox.setBounds(24, 139, 167, 20);
		frmBackupservice.getContentPane().add(comboBox);

		JLabel lblLog = new JLabel("Log");
		lblLog.setFont(new Font("Tahoma", Font.BOLD, 15));
		lblLog.setBounds(23, 173, 56, 17);
		frmBackupservice.getContentPane().add(lblLog);

		btnStartService = new JButton("Start Service");
		btnStartService.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {

					backupService.setAddresses(new String[]{textMcIp.getText(), textMcP.getText(),
							textMdbIp.getText(), textMdbP.getText(),
							textMdrIp.getText(), textMdrP.getText()});

					textMcP.setEditable(false);
					textMcIp.setEditable(false);
					textMdbIp.setEditable(false);
					textMdbP.setEditable(false);
					textMdrIp.setEditable(false);
					textMdrP.setEditable(false);
					textVersion.setEditable(false);

					btnStartService.setEnabled(false);
					btnUpdate.setEnabled(true);
					btnDelete.setEnabled(true);
					btnRestore.setEnabled(true);

					new Thread(new Runnable() {
						@Override
						public void run() {
							backupService.initReceivingThreads();

						}
					}).start();


				} catch (UnknownHostException e) {
					System.err.println("Invalid Ip address: " + e.getMessage());
				}
			}
		});
		btnStartService.setBounds(789, 172, 124, 23);
		frmBackupservice.getContentPane().add(btnStartService);

		btnRestore = new JButton("Restore");
		btnRestore.setEnabled(false);
		btnRestore.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				BackupService.getMc().askRestoreFile(BackupService.getLocalByName((String) comboBox.getSelectedItem()));
			}
		});
		btnRestore.setBounds(388, 138, 89, 23);
		frmBackupservice.getContentPane().add(btnRestore);

		Box horizontalBox_1 = Box.createHorizontalBox();
		horizontalBox_1.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "IPs/Ports", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		horizontalBox_1.setBounds(23, 15, 470, 85);
		frmBackupservice.getContentPane().add(horizontalBox_1);

		Panel panel_1 = new Panel();
		horizontalBox_1.add(panel_1);
		panel_1.setLayout(null);

		JLabel lblMc = new JLabel("MC");
		lblMc.setBounds(10, 7, 55, 14);
		panel_1.add(lblMc);

		JLabel lblMdb = new JLabel("MDB");
		lblMdb.setBounds(10, 33, 55, 14);
		panel_1.add(lblMdb);

		JLabel lblMdr = new JLabel("MDR");
		lblMdr.setBounds(189, 19, 62, 14);
		panel_1.add(lblMdr);

		textMcIp = new JTextField();
		textMcIp.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent arg0) {
				char c = arg0.getKeyChar();
				if (!((c >= '0') && (c <= '9') || (c == '.') ||(c == KeyEvent.VK_BACK_SPACE) || (c == KeyEvent.VK_DELETE))) {
					arg0.consume();
				}
			}
		});
		textMcIp.setText("224.1.1.1");
		textMcIp.setBounds(38, 5, 86, 20);
		panel_1.add(textMcIp);
		textMcIp.setColumns(10);

		textMdbIp = new JTextField();
		textMdbIp.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent arg0) {
				char c = arg0.getKeyChar();
				if (!((c >= '0') && (c <= '9') || (c == '.') ||(c == KeyEvent.VK_BACK_SPACE) || (c == KeyEvent.VK_DELETE))) {
					arg0.consume();
				}
			}
		});
		textMdbIp.setText("224.1.1.2");
		textMdbIp.setColumns(10);
		textMdbIp.setBounds(38, 31, 86, 20);
		panel_1.add(textMdbIp);

		textMdrIp = new JTextField();
		textMdrIp.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent arg0) {
				char c = arg0.getKeyChar();
				if (!((c >= '0') && (c <= '9') || (c == '.') ||(c == KeyEvent.VK_BACK_SPACE) || (c == KeyEvent.VK_DELETE))) {
					arg0.consume();
				}
			}
		});
		textMdrIp.setText("224.1.1.3");
		textMdrIp.setColumns(10);
		textMdrIp.setBounds(220, 17, 86, 20);
		panel_1.add(textMdrIp);

		textMdbP = new JTextField();
		textMdbP.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent arg0) {
				char c = arg0.getKeyChar();
				if (!((c >= '0') && (c <= '9') || (c == KeyEvent.VK_BACK_SPACE) || (c == KeyEvent.VK_DELETE))) {
					arg0.consume();
				}
			}
		});
		textMdbP.setText("5091");
		textMdbP.setBounds(136, 31, 35, 20);
		panel_1.add(textMdbP);
		textMdbP.setColumns(10);

		textMcP = new JTextField();
		textMcP.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent arg0) {
				char c = arg0.getKeyChar();
				if (!((c >= '0') && (c <= '9') || (c == KeyEvent.VK_BACK_SPACE) || (c == KeyEvent.VK_DELETE))) {
					arg0.consume();
				}
			}
		});
		textMcP.setText("5090");
		textMcP.setColumns(10);
		textMcP.setBounds(136, 5, 35, 20);
		panel_1.add(textMcP);

		textMdrP = new JTextField();
		textMdrP.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent arg0) {
				char c = arg0.getKeyChar();
				if (!((c >= '0') && (c <= '9') || (c == KeyEvent.VK_BACK_SPACE) || (c == KeyEvent.VK_DELETE))) {
					arg0.consume();
				}
			}
		});
		textMdrP.setText("5092");
		textMdrP.setColumns(10);
		textMdrP.setBounds(316, 17, 35, 20);
		panel_1.add(textMdrP);
	}
}
