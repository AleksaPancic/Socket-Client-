import javax.swing.JFrame;
public class ClientTest {

	public static void main(String[] args) {
	Client domaciklijent;
	//testiranje na localhostu
	domaciklijent = new Client("127.0.0.1"); //adresa oznacava localhost komp na kome sam trenutno
	domaciklijent.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	domaciklijent.startRunning();
	}

}
