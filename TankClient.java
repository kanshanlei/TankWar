import java.awt.Button;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Label;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 坦克的游戏场所
 * @author mashibing&kanshanlei
 *
 */
public class TankClient extends Frame {
	/**
	 * 游戏场所的宽度
	 */
	public static final int GAME_WIDTH = 800;
	
	/**
	 * 游戏场所的高度
	 */
	public static final int GAME_HEIGHT = 600;

	boolean pause=false;
	
	private static Random r = new Random();
	Tank myTank = new Tank(250, 550, true, Dir.STOP, this);
	Home home=new Home(350,500);

	List<Missile> missiles = new ArrayList<Missile>();

	List<Explode> explodes = new ArrayList<Explode>();

	List<Tank> tanks = new ArrayList<Tank>();
	
	List<Wall> walls = new ArrayList<Wall>();
	List<River> rivers = new ArrayList<River>();

	Image offScreenImage = null;

	NetClient nc = new NetClient(this);

	ConnDialog dialog = new ConnDialog();
	
	@Override
	/**
	 * 重写父类的重画方法
	 */
	public void paint(Graphics g) {
		g.drawString("explodes count:" + explodes.size(), 10, 70);
		g.drawString("tanks    count:" + tanks.size(), 10, 90);
		//画子弹
		for (int i = 0; i < missiles.size(); i++) {
			Missile m = missiles.get(i);
			// m.hitTanks(tanks);
			for(int j=0;j<missiles.size();j++){
				Missile m2 = missiles.get(j);
				m.hitMissile(m2);
			}
			if(m.hitHome(home)){
				HomeDeadMsg msg = new HomeDeadMsg();
				nc.send(msg);
				MissileDeadMsg mdmMsg = new MissileDeadMsg(m.tankId, m.id);
				nc.send(mdmMsg);
			}
			if (m.hitTank(myTank)) {
				TankDeadMsg msg = new TankDeadMsg(myTank.id);
				nc.send(msg);
				MissileDeadMsg mdmMsg = new MissileDeadMsg(m.tankId, m.id);
				nc.send(mdmMsg);
			}
			for(int j=0;j<tanks.size();j++){
				Tank t=tanks.get(j);
				if (m.hitTank(t)) {
					TankDeadMsg msg = new TankDeadMsg(t.id);
					nc.send(msg);
					MissileDeadMsg mdmMsg = new MissileDeadMsg(m.tankId, m.id);
					nc.send(mdmMsg);
				}
			}
			for(int j=0;j<walls.size();j++){
				Wall w=walls.get(j);
				if(m.hitWall(w)){
					WallDeadMsg msg=new WallDeadMsg(w);
					nc.send(msg);
					MissileDeadMsg mdmMsg = new MissileDeadMsg(m.tankId, m.id);
					nc.send(mdmMsg);
				}
			}
			m.draw(g);
		}
		//画爆炸
		
		for (int i = 0; i < explodes.size(); i++) {
			Explode e = explodes.get(i);
			e.draw(g);
		}
		
		//画自己坦克
		for(int j=0;j<walls.size();j++)
			myTank.collide(walls.get(j));
		for(int j=0;j<rivers.size();j++)
			myTank.collide(rivers.get(j));
		for(int j=0;j<tanks.size();j++)
			myTank.collide(tanks.get(j));
		myTank.draw(g);
		
		int rnum=r.nextInt(3);
		if(myTank.id==100&&tanks.size()<3) 
			{
				Tank t=new Tank(nc.tankid++,50 + 100*(rnum+1), 50, false, Dir.D,this);
				tanks.add(t);
				TankNewMsg msg = new TankNewMsg(t);
				nc.send(msg);
			}
		//画其他坦克
		for (int i = 0; i < tanks.size(); i++) {
			Tank t = tanks.get(i);
			for(int j=0;j<walls.size();j++)
				t.collide(walls.get(j));
			for(int j=0;j<rivers.size();j++)
				t.collide(rivers.get(j));
			for(int j=0;j<tanks.size();j++)
				t.collide(tanks.get(j));
			t.draw(g);
		}
		
		//画周围环境
		for(int i=0;i<walls.size();i++){
			Wall w=walls.get(i);
			w.draw(g);
		}
		for(int i=0;i<rivers.size();i++){
			River l=rivers.get(i);
			l.draw(g);
		}
		home.draw(g);

	}

	@Override
	/**
	 * 重写父类的update方法用于实现双缓冲
	 */
	public void update(Graphics g) {
		//暂停时线程继续运行，只是不调用那些移动功能而已
		if (offScreenImage == null) {
			offScreenImage = this.createImage(800, 600);
		}
		Graphics gOffScreen = offScreenImage.getGraphics();
		Color c = gOffScreen.getColor();
		gOffScreen.setColor(Color.GREEN);
		gOffScreen.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);
		gOffScreen.setColor(c);//把原本在画布上的色彩放回来
		paint(gOffScreen);
		g.drawImage(offScreenImage, 0, 0, null);
	}
	
	/**
	 * 显示窗口
	 *
	 */
	public void launchFrame() {

		this.setLocation(400, 300);
		this.setSize(GAME_WIDTH, GAME_HEIGHT);
		this.setTitle("TankWar");
		this.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}

		});
		
		this.setResizable(false);
		this.setBackground(Color.GREEN);
		myTank.good = true;
		home.live = true;
		this.addKeyListener(new KeyMonitor());
		dialog.setVisible(true);
		this.setVisible(true);
		
		Thread t0= new Thread(new PaintThread());
		t0.start();
		
		// nc.connect("127.0.0.1", TankServer.TCP_PORT);
	}

	public static void main(String[] args) {
		TankClient tc = new TankClient();
		tc.launchFrame();
	}

	class PaintThread implements Runnable {

		public void run() {
			while (true) {
				repaint();
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

	}

	class KeyMonitor extends KeyAdapter {

		@Override
		public void keyReleased(KeyEvent e) {
			myTank.keyReleased(e);
		}//因为是继承所以要把所有方法写全

		@Override
		public void keyPressed(KeyEvent e) {
			myTank.keyPressed(e);	
		}

	}
	class ConnDialog extends Dialog {
		Button b = new Button("确定");

		TextField tfIP = new TextField("127.0.0.1", 12);

		TextField tfPort = new TextField("" + TankServer.TCP_PORT, 4);

		TextField tfMyUDPPort = new TextField("2223", 4);

		public ConnDialog() {
			super(TankClient.this, true);

			this.setLayout(new FlowLayout());
			this.add(new Label("IP:"));
			this.add(tfIP);
			this.add(new Label("Port:"));
			this.add(tfPort);
			this.add(new Label("My UDP Port:"));
			this.add(tfMyUDPPort);
			this.add(b);
			this.setLocation(300, 300);
			this.pack();
			this.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					setVisible(false);
				}
			});
			b.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					String IP = tfIP.getText().trim();
					int port = Integer.parseInt(tfPort.getText().trim());
					int myUDPPort = Integer.parseInt(tfMyUDPPort.getText()
							.trim());
					nc.setUdpPort(myUDPPort);
					nc.connect(IP, port);
					setVisible(false);
				}

			});
		}

	}

}
