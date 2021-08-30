//Zasto posebno 2 programa? 2 razlicita komp jedan na serveru drugi kod klijenta
import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
public class Client extends JFrame {
	
	private JTextField userText;
	private JTextArea chatWindow;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private String message = "";
	private String serverIP;
	private Socket connection;
	
	//constructor
	public Client(String host) { //radi zastite dajemo ip adrese servera da ne bi moglo da se pristupi podacima
		super("Client");
		serverIP = host;
		userText = new JTextField();
		userText.setEditable(false);
		userText.addActionListener(
					new ActionListener() {
						public void actionPerformed(ActionEvent event) {
							sendMessage(event.getActionCommand());
							userText.setText("");
						}
					}
				);
		add(userText, BorderLayout.NORTH);
		chatWindow = new JTextArea();
		add(new JScrollPane(chatWindow), BorderLayout.CENTER); //prikazi
		setSize(300,150);
		setVisible(true);
	}
	//konektuj se na server
	public void startRunning() {
		try {
			connectToServer(); //konekcija ka specificnom kompjuteru/serveru
			setupStreams();
			whileChatting();
		}catch(EOFException eofException) {
			showMessage("\n Klijent je zatvorio konekciju");
		}catch(IOException ioException) {
			ioException.printStackTrace();
		}finally {
			closeStuff();
		}
	}
	
	//Konekcija na serveru
	private void connectToServer() throws IOException{
		showMessage("Konektujem se...");
		//kad god se konektujemo na serveru trebaju nam 2 informacije IP adresa i Port number
		connection = new Socket(InetAddress.getByName(serverIP), 6789); // kada se konektujemo soket se pravi
		showMessage("Connected to: " + connection.getInetAddress().getHostName());
	}
	//set up streams da salju i primaju poruke
	private void setupStreams() throws IOException{
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
		showMessage("\n Konektovani ste \n");
	}
	//while chatting sa serverom
	private void whileChatting() throws IOException{
		ableToType(true); //da moze korisnik da kuca
		do {
			try {
				message = (String) input.readObject(); //sta god bio input mi procitamo i stavimo u message var
				showMessage("\n" + message);
			}catch(ClassNotFoundException classNotfoundException) {
				showMessage("\n Error");
			}
		}while(!message.equals("SERVER - END")); //dok se ne ukuca END ide konverzacija
	}
	//zatvara strean i sokete
	private void closeStuff() {
		showMessage("\n Zatvaram...");
		ableToType(false);
		try {
			output.close();
			input.close();
			connection.close();
		}catch(IOException ioException) {
			ioException.printStackTrace(); //pokazi error msg
		}
	}
	//posalji poruku serveru
	private void sendMessage(String message) {
		try {
			output.writeObject("CLIENT - " + message);
			output.flush();
			showMessage("\n CLIENT - " + message);
		}catch(IOException ioException) {
			chatWindow.append("\n Nesto nije okej u slanju poruke");
		}
	}
	// update GUI da bi se poruka prikazala
	//promeni update chat prozor
	private void showMessage(final String m) {
		SwingUtilities.invokeLater(
				new Runnable() { //pravi nit
					public void run() {
						chatWindow.append(m);
					}
				}
			);
	}
		//Daje korisnicima dozvoli da pisu u text box-u
		private void ableToType(final boolean tof) {
			SwingUtilities.invokeLater(
					new Runnable() {
						public void run() {
							userText.setEditable(tof);
						}
					}
					);
		}
}
