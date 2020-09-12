import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import javax.swing.JOptionPane;

import redis.clients.jedis.Jedis;

public class HttpRequest {
	private static final String IP_SERVER = "192.168.0.217";
	private static final int NUMBER_TENTATIVE = 5;

	public HttpRequest() {
	}

	/* Subscribe the "TransfertPortable" channel */
	public static void connect(Jedis jr, IHM ihm) throws Exception {
		try {
			ihm.onConnect();
			jr = new Jedis(IP_SERVER, 6379, 0);
			Subscribe sp = new Subscribe();
			sp.setIHM(ihm);
			jr.subscribe(sp, "TransfertPortable", "Message");
			jr.close();
		} catch (Exception e) {
			System.out.println("Erreur de connexion");
			ihm.onReconnect();
			int i = 0;
			for (i = 0; i < NUMBER_TENTATIVE; i++) {
				try {
					jr = new Jedis(IP_SERVER, 6379, 0);
					Subscribe sp = new Subscribe();
					sp.setIHM(ihm);
					jr.subscribe(sp, "TransfertPortable");
					jr.close();
					break;
				} catch (Exception errConnection) {
					Thread.sleep(2000);
					if (i == (NUMBER_TENTATIVE - 1)) {
						ihm.onDeconnect();
					}
				}
			}
			e.printStackTrace();
		}
	}

	/* get the current transfer state by sending a "GET" request to server */
	public String getTransfertState() throws Exception {
		try {
			String url = "http://" + IP_SERVER + "/transfert_portable.php?opt=showStatus";
			URL phpTransfertPortable = new URL(url);
			URLConnection connection = phpTransfertPortable.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String result = in.readLine();
			in.close();
			if (!result.equals("")) {
				return result;
			} else {
				return "error";
			}
		} catch (Exception e) {
			System.out.println("Got an Exception：" + e.getMessage());
			e.printStackTrace();
			throw e;
		}
	}

	/* Activate the transfer to Thibault */
	public void setTOn() throws Exception {
		try {
			String url = "http://" + IP_SERVER + "/transfert_portable.php?opt=Tactive";
			URL phpTransfertPortable = new URL(url);
			URLConnection connection = phpTransfertPortable.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String result = in.readLine();
			System.out.println(result);
			JOptionPane.showMessageDialog(null, result);
			in.close();
		} catch (Exception e) {
			System.out.println("Got an Exception：" + e.getMessage());
			e.printStackTrace();
			throw e;
		}
	}

	/* Activate the transfer to Bertrand */
	public void setBOn() throws Exception {
		try {
			String url = "http://" + IP_SERVER + "/transfert_portable.php?opt=Bactive";
			URL phpTransfertPortable = new URL(url);
			URLConnection connection = phpTransfertPortable.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String result = in.readLine();
			System.out.println(result);
			JOptionPane.showMessageDialog(null, result);
			in.close();
		} catch (Exception e) {
			System.out.println("Got an Exception：" + e.getMessage());
			e.printStackTrace();
			throw e;
		}
	}

	/* Desactive transfer */
	public void desactive() throws Exception {
		try {
			String url = "http://" + IP_SERVER + "/transfert_portable.php?opt=desactive";
			URL phpTransfertPortable = new URL(url);
			URLConnection connection = phpTransfertPortable.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String result = in.readLine();
			System.out.println(result);
			JOptionPane.showMessageDialog(null, result);
			in.close();
		} catch (Exception e) {
			System.out.println("Got an Exception：" + e.getMessage());
			e.printStackTrace();
			throw e;
		}
	}

	/* Send a custom message */
	public void sendMessage(String message) throws Exception {
		try {
			String url = "http://" + IP_SERVER + "/send_message.php?message=" + URLEncoder.encode(message, "UTF-8");
			URL phpTransfertPortable = new URL(url);
			URLConnection connection = phpTransfertPortable.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			in.close();
		} catch (Exception e) {
			System.out.println("Got an Exception：" + e.getMessage());
			e.printStackTrace();
			throw e;
		}
	}

}