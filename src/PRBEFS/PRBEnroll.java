package PRBEFS;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class PRBEnroll extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;
	
	private DefaultComboBoxModel EmployeeBoxComboModel;
	private JComboBox EmployeeBox;
	private JComboBox FingerBox;
	private JLabel EmployeeLabel;
	private JLabel FingerLabel;
	private JButton OkButton;
	private JButton CancelButton;
	private static JMenuBar menuBar;
	private JMenu menuFile;
	private JMenu menuHelp;
	private JMenuItem menuItem;
	
	private native int enroll(int finger, String path, String user);
	
	
	static {
		System.loadLibrary("enroll_java");
	}
	
	
	public  PRBEnroll() {
		
		/*
		String[] FingerStrings = { "Indice derecho", "Indice izquierdo", "Pulgar derecho", "Pulgar izquierdo",
                "Medio derecho", "Medio izquierdo", "Anular derecho", "Anular izquierdo",
                "Meñique derecho", "Meñique izquierdo"};
        */        
            
		String[] FingerStrings = { "Indice derecho", "Indice izquierdo"};
		
		final JPanel EnrollPanel = new JPanel();
		EnrollPanel.setLayout(new GridLayout(3, 2));
		
		EmployeeBoxComboModel  = new DefaultComboBoxModel();
		EmployeeBoxComboModel  = getFMSEmployees(EmployeeBoxComboModel);
		EmployeeBox = new JComboBox(EmployeeBoxComboModel);
		EmployeeBox.setModel(EmployeeBoxComboModel);
		
		FingerBox   = new JComboBox(FingerStrings);
		EmployeeLabel = new JLabel("Asociado: ");
		FingerLabel   = new JLabel("Dedo a capturar: ");
		
		OkButton     = new JButton("Ok");
		CancelButton = new JButton("Cancelar");
		
		menuBar     = new JMenuBar();
		menuFile    = new JMenu("Archivo");
		menuHelp    = new JMenu("Ayuda");
		
		menuBar.add(menuFile);
		menuBar.add(menuHelp);
		
		menuItem = new JMenuItem("Salir");
		menuItem.addActionListener(new ActionListener () {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				System.exit(0);
			}
			
		});
		menuFile.add(menuItem);
		
		menuItem = new JMenuItem("Acerca de");
		menuItem.addActionListener(new ActionListener () {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				URL img = PRBEnroll.class.getResource("/resources/fingerprint.png");
				String imagesrc = "<img src=\"" +img+ "\" width=\0\" height=\0\" ";
				JOptionPane.showMessageDialog(EnrollPanel,
						"<html><center>" +imagesrc+ 
					    "<br>PRB Enroll Fingerprint System<br>&copy; Sergio Cuellar Valdes 2011</cenetr></html>",
					    "Acerca de",
					    JOptionPane.PLAIN_MESSAGE);
			}
			
		});
		menuHelp.add(menuItem);
		
		
		
		
		EnrollPanel.add(EmployeeLabel, 0);
		EnrollPanel.add(EmployeeBox, 1);
		
		EnrollPanel.add(FingerLabel, 2);
		EnrollPanel.add(FingerBox, 3);
		
		EnrollPanel.add(OkButton, 4);
		EnrollPanel.add(CancelButton, 5);
		
		OkButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Employee emp = (Employee) EmployeeBox.getSelectedItem();
				String    id = emp.getID();
				String instructions;
				
				int f = 0;
				
		        String fingerName = (String)FingerBox.getSelectedItem();
		         
		        
		        /*
		        if(fingerName.equals("Pulgar izquierdo")) f = 0;
		        else if (fingerName.equals("Indice izquierdo")) f = 1;
		        else if (fingerName.equals("Medio izquierdo")) f = 2;
		        else if (fingerName.equals("Anular izquierdo")) f = 3;
		        else if (fingerName.equals("Meñique izquierdo")) f = 4;
		        else if (fingerName.equals("Pulgar derecho")) f = 5;
		        else if (fingerName.equals("Indice derecho")) f = 6;
		        else if (fingerName.equals("Medio derecho")) f = 7;
		        else if (fingerName.equals("Anular derecho")) f = 8;
		        else if (fingerName.equals("Meñique derecho")) f = 9;
		        */
		        
		        if (fingerName.equals("Indice izquierdo")) f = 1;
		        else if (fingerName.equals("Indice derecho")) f = 6;
		        
		        
		        if(Integer.parseInt(id) == 0) {
		        	JOptionPane.showMessageDialog(EnrollPanel, "Favor de seleccionar un asociado.", 
		        			"Seleccionar Asociado", JOptionPane.ERROR_MESSAGE);
		        } else {		        
		        	instructions = "A continuación debe colocar su dedo "+fingerName+" en el lector. Presione Aceptar para continuar";
		        
		        	JOptionPane.showMessageDialog(EnrollPanel, instructions);
		        
		        	enroll(f, "/tmp/Employees", id);
		        
		        	JOptionPane.showMessageDialog(EnrollPanel, "Huella digital capturada. Gracias");
		        	
		        	JOptionPane.showMessageDialog(EnrollPanel,
		        			"Puede seguir con la captura de huellas digitales");
		        }
		        
			}
		});
		
		
		
		this.add(EnrollPanel);
		this.setSize(360, 210);
		this.setTitle("PRB Enroll Fingerprint System");
		
	}
	
	public  DefaultComboBoxModel getFMSEmployees(DefaultComboBoxModel EmployeeBoxComboModel) {
		
		String[] cmd = { "/bin/sh", "-c", "/usr/bin/ph/emplalt/bin/emplshowdata.s /usr/fms/data/hrcempl.dat" };
		String s;
		String [] data;
		
		try {
			Process p = Runtime.getRuntime().exec(cmd);
			
			BufferedReader stdInput = new BufferedReader(new
	                 InputStreamReader(p.getInputStream()));
			
			EmployeeBoxComboModel.addElement(new Employee("Seleccione", "0"));
			
			while ((s = stdInput.readLine()) != null) {
				data = s.split("\\|");
				EmployeeBoxComboModel.addElement(new Employee(data[0], data[1]));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return EmployeeBoxComboModel;
	}
	
	public static void main(String[] args) {
		JFrame.setDefaultLookAndFeelDecorated(true);
		JFrame MainFrame = new PRBEnroll();
		
		MainFrame.setIconImage(
				new ImageIcon(
						PRBEnroll.class.getResource("/resources/prb.png")).getImage()
				);
		
		MainFrame.setJMenuBar(menuBar);
		MainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		MainFrame.setVisible(true);
		MainFrame.setLocationRelativeTo(null);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
	
}