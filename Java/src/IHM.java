import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import redis.clients.jedis.Jedis;

public class IHM extends JFrame {
	HttpRequest httpRequest;

	TrayIcon ti;
	JLabel title;
	JLabel state;
	JLabel stateIndicator;
	JLabel connectionState;
	JTextArea textAreaReceive;
	JTextArea textAreaSend;
	JScrollPane scroll;
	JButton buttonActiveT;
	JButton buttonActiveB;
	JButton buttonDesactive;
	JButton buttonConnection;
	JButton buttonSendMessage;
	ImageIcon imageIcon;
	SystemTray st;
	PopupMenu popupMenu1;

	public IHM() {
		httpRequest = new HttpRequest();
		this.setSize(1300, 800);
		this.setLayout(null);
		title = new JLabel("Transfert Vers Portable");
		title.setFont(new Font("Arial", 0, 40));
		title.setSize(500, 50);
		title.setLocation(250, 100);

		stateIndicator = new JLabel("Etat de Transfert: ");
		stateIndicator.setFont(new Font("Arial", 0, 35));
		stateIndicator.setSize(350, 50);
		stateIndicator.setLocation(50, 200);

		state = new JLabel("");
		state.setFont(new Font("Arial", 0, 60));
		state.setForeground(Color.RED);
		state.setSize(1000, 50);
		state.setLocation(340, 200);

		connectionState = new JLabel("");
		connectionState.setFont(new Font("Arial", 0, 30));
		connectionState.setSize(500, 50);
		connectionState.setLocation(800, 0);

		textAreaReceive = new JTextArea();
		textAreaReceive.setSize(400, 200);
		textAreaReceive.setLocation(125, 600);
		textAreaReceive.setLineWrap(true);
		textAreaReceive.setWrapStyleWord(true);

		scroll = new JScrollPane(textAreaReceive);
		scroll.setSize(400, 200);
		scroll.setLocation(125, 600);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		textAreaSend = new JTextArea();
		textAreaSend.setSize(400, 200);
		textAreaSend.setLocation(625, 600);

		buttonActiveT = new JButton("Transfert Vers Thibault");
		buttonActiveT.setFont(new Font("Arial", 0, 30));
		buttonActiveT.setSize(450, 100);
		buttonActiveT.setLocation(100, 300);

		buttonActiveB = new JButton("Transfert Vers Bertrand");
		buttonActiveB.setFont(new Font("Arial", 0, 30));
		buttonActiveB.setSize(450, 100);
		buttonActiveB.setLocation(600, 300);

		buttonDesactive = new JButton("Désactiver Transfert");
		buttonDesactive.setFont(new Font("Arial", 0, 30));
		buttonDesactive.setSize(450, 100);
		buttonDesactive.setLocation(330, 450);

		buttonConnection = new JButton("Reconnecter");
		buttonConnection.setFont(new Font("Arial", 0, 30));
		buttonConnection.setSize(230, 50);
		buttonConnection.setLocation(800, 50);

		buttonConnection.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				try {
					reconnect();
				} catch (Exception err) {
					System.out.println("Erreur de connexion");
					err.printStackTrace();
				}
			}
		});

		buttonActiveT.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				activeT();
				checkState();
			}
		});

		buttonActiveB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				activeB();
				checkState();
			}
		});

		buttonDesactive.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				desactive();
				checkState();
			}
		});

		buttonSendMessage = new JButton("Envoyer Message");
		buttonSendMessage.setFont(new Font("Arial", 0, 30));
		buttonSendMessage.setSize(300, 50);
		buttonSendMessage.setLocation(850, 500);
		buttonSendMessage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String message = textAreaSend.getText();
				try {
					httpRequest.sendMessage(message);

				} catch (Exception e1) {
					e1.printStackTrace();
				}
				textAreaSend.setText("");
			}
		});

		this.add(title);
		this.add(stateIndicator);
		this.add(state);
		this.add(connectionState);
		this.add(textAreaSend);
		this.add(scroll);
		this.add(buttonActiveT);
		this.add(buttonActiveB);
		this.add(buttonDesactive);
		this.add(buttonConnection);
		disableButtons();
		this.buttonConnection.setEnabled(false);
		this.textAreaReceive.setEditable(false);
		this.add(buttonSendMessage);

		setPanelVisible();
		imageIcon = new ImageIcon(getClass().getResource("Icon.png"));
		this.setIconImage(imageIcon.getImage());

		addWindowListener(new WindowAdapter() {
			public void windowIconified(WindowEvent evt) {
				changeTrayIcon();
				setPanelUnvisible();
			}

			public void windowClosing(WindowEvent evt) {
				changeTrayIcon();
				setPanelUnvisible();
			}
		});
	}

	public void setPanelUnvisible() {
		this.setVisible(false);
	}

	public void setPanelVisible() {
		this.setVisible(true);
		checkState();
	}

	public void changeTrayIcon() {
		try {
			if (SystemTray.isSupported()) {
				st = SystemTray.getSystemTray();
				String state = httpRequest.getTransfertState();
				Image image = null;
				if (state.contentEquals("toT")) {
					state = "Transfert Activé, Vers Thibault";
					image = Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("Icon1.png"));
				} else if (state.contentEquals("toB")) {
					state = "Transfert Activé, Vers Bertrand";
					image = Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("Icon2.png"));
				} else if (state.contentEquals("noTransfer")) {
					state = "Transfert NON Activé";
					image = Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("Icon.png"));
				}

				ti = new TrayIcon(image, state);
				st.add(ti);

				popupMenu1 = new PopupMenu();
				MenuItem menuItem1 = new MenuItem();
				MenuItem menuItem2 = new MenuItem();
				MenuItem menuItem3 = new MenuItem();
				menuItem1.setLabel("ouvrir");
				menuItem2 = new MenuItem("exit");
				menuItem3 = new MenuItem("changeIcon");
				menuItem1.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						setPanelVisible();
						SystemTray.getSystemTray().remove(ti);
					}
				});

				menuItem2.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						System.exit(0);
					}
				});
				menuItem3.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						changeIconToB();
					}
				});
				popupMenu1.add(menuItem1);
				popupMenu1.add(menuItem2);
				popupMenu1.add(menuItem3);

				ti.setPopupMenu(popupMenu1);

				ti.addMouseListener(new MouseAdapter() {
					public void mouseClicked(MouseEvent e) {
						if (e.getClickCount() == 2) {
							setExtendedState(Frame.NORMAL);
							setPanelVisible();
							SystemTray.getSystemTray().remove(ti);
						}
					}
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void changeIconToB() {
		Image image = Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("Icon2.png"));
		if (ti != null) {
			ti.setImage(image);
			ti.setToolTip("Transfert Activé, vers Bertrand");
		}
	}

	public void changeIconToT() {
		Image image = Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("Icon1.png"));
		if (ti != null) {
			ti.setImage(image);
			ti.setToolTip("Transfert Activé, vers Thibault");
		}
	}

	public void changeIconToNoTransfer() {
		Image image = Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("Icon.png"));
		if (ti != null) {
			ti.setImage(image);
			ti.setToolTip("Transfert NON Activé");
		}
	}

	public void checkState() {
		try {
			String state = httpRequest.getTransfertState();
			if (state.contentEquals("toT")) {
				state = "Transfert Activé, Vers Thibault";
				changeIconToT();
			} else if (state.contentEquals("toB")) {
				state = "Transfert Activé, Vers Bertrand";
				changeIconToB();
			} else if (state.contentEquals("noTransfer")) {
				state = "Transfert NON Activé";
				changeIconToNoTransfer();
			}
			this.state.setText(state);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void activeB() {
		try {
			httpRequest.setBOn();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void activeT() {
		try {
			httpRequest.setTOn();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void desactive() {
		try {
			httpRequest.desactive();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void reconnect() {
		connect(this);
	}

	public void connect(IHM ihm) {
		new Thread() {
			public void run() {
				try {
					Jedis jr = null;
					HttpRequest.connect(jr, ihm);
				} catch (Exception e) {
					System.out.println("Erreur de connexion");
					e.printStackTrace();
				}
			}
		}.start();
	}

	public void disableButtons() {
		this.buttonActiveB.setEnabled(false);
		this.buttonActiveT.setEnabled(false);
		this.buttonDesactive.setEnabled(false);
	}

	public void enableButtons() {
		this.buttonActiveB.setEnabled(true);
		this.buttonActiveT.setEnabled(true);
		this.buttonDesactive.setEnabled(true);
	}

	public void onConnect() {
		disableButtons();
		this.connectionState.setForeground(Color.BLACK);
		this.connectionState.setText("Connexion en cours...");
		this.buttonConnection.setEnabled(false);
		this.checkState();
	}

	public void onConnected() {
		enableButtons();
		this.connectionState.setForeground(Color.GREEN);
		this.connectionState.setText("Connecté");
		this.buttonConnection.setEnabled(false);
		this.checkState();
	}

	public void onReconnect() {
		disableButtons();
		this.connectionState.setForeground(Color.ORANGE);
		this.connectionState.setText("Reconnexion en cours...");
		this.buttonConnection.setEnabled(false);
	}

	public void onDeconnect() {
		disableButtons();
		this.connectionState.setForeground(Color.RED);
		this.connectionState.setText("Déconnecté");
		this.buttonConnection.setEnabled(true);
	}

	public static void main(String[] args) {
		IHM ihm = new IHM();
		ihm.connect(ihm);
	}

}
