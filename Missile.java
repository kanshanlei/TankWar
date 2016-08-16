import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.List;

/**
 * �����ӵ�����
 * @author kannshanlei
 *
 */
public class Missile {
	/**
	 * �ӵ�x������ٶ�
	 */
	public static final int XSPEED = 10;
	/**
	 * �ӵ�y������ٶ�
	 */
	public static final int YSPEED = 10;
	/**
	 * �ӵ��Ŀ��
	 */
	public static final int WIDTH = 10;
	/**
	 * �ӵ��ĸ߶�
	 */
	public static final int HEIGHT = 10;

	private static int ID = 1;

	TankClient tc;

	int tankId;

	int id;

	int x, y;

	Dir dir = Dir.R;

	boolean live = true;

	boolean good;
	
	/**
	 * ����λ�õ����Թ����ӵ�
	 * @param tankId ����̹�˵�id��(���������)
	 * @param x �ӵ�������x����
	 * @param y �ӵ�������y����
	 * @param good �ӵ��������Ǻû��ǻ�
	 * @param dir �ӵ��ķ���
	 * @see Dir
	 */
	
	public Missile(int tankId, int x, int y, boolean good, Dir dir) {
		this.tankId = tankId;
		this.x = x;
		this.y = y;
		this.good = good;
		this.dir = dir;
		this.id = ID++;
	}
	
	/**
	 * ����λ�ú�TankClient�����ӵ�
	 * @param tankId
	 * @param x
	 * @param y
	 * @param good
	 * @param dir
	 * @param tc �ӵ������ĳ���
	 * @see TankClient
	 */
	public Missile(int tankId, int x, int y, boolean good, Dir dir,
			TankClient tc) {
		this(tankId, x, y, good, dir);
		this.tc = tc;
	}
	
	/**
	 * �����ӵ�
	 * @param g ����
	 */
	public void draw(Graphics g) {
		if (!live) {
			tc.missiles.remove(this);
			return;
		}

		Color c = g.getColor();
		g.setColor(Color.BLACK);
		g.fillOval(x, y, WIDTH, HEIGHT);
		g.setColor(c);
		move();
	}

	private void move() {
		switch (dir) {
		case L:
			x -= XSPEED;
			break;
		case U:
			y -= YSPEED;
			break;

		case R:
			x += XSPEED;
			break;
		case D:
			y += YSPEED;
			break;
		case STOP:
			break;
		}

		if (x < 0 || y < 0 || x > TankClient.GAME_WIDTH
				|| y > TankClient.GAME_HEIGHT) {
			live = false;
		}
	}
	
	/**
	 * ȡ���ӵ������з���
	 * @return �ӵ�������Rectangle
	 */
	public Rectangle getRect() {
		return new Rectangle(x, y, WIDTH, HEIGHT);
	}
	
	/**
	 * ����ӵ��Ƿ�ײ��̹��
	 * @param t ������̹��
	 * @return ���ײ������true,���򷵻�false
	 */
	public boolean hitTank(Tank t) {
		if (this.live && t.isLive() && this.good != t.good
				&& this.getRect().intersects(t.getRect())) {
			this.live = false;
			t.setLive(false);
			tc.explodes.add(new Explode(x, y, tc));
			return true;
		}
		return false;
	}
	
	/**
	 * ����Ƿ�ײ��һϵ��̹���е�һ��
	 * @param tanks ������̹������
	 * @return ���ײ������һ��,����true,���򷵻�false
	 */
	public boolean hitTanks(List<Tank> tanks) {
		for (int i = 0; i < tanks.size(); i++) {
			if (this.hitTank(tanks.get(i))) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * ����Ƿ�������ѣ����з���true
	 * @param home ����
	 * @return
	 */
	public boolean hitHome(Home home){
		if(this.live&&home.live&&this.getRect().intersects(home.getRect())){
			this.live = false;
			home.setLive(false);
			tc.explodes.add(new Explode(x, y, tc));
			return true;
		}
		return false;
	}
	
	public boolean hitWall(Wall w) {
		if (this.live &&w.live && this.getRect().intersects(w.getRect())) {
			this.live = false;
			w.live = false;
			return true;
		}
		return false;
	}
	
	/**
	 * ��������ӵ��Ƿ���ײ
	 * @param m �������ӵ�
	 * @return
	 */
	public boolean hitMissile(Missile m) {
		if (m.id!=this.id&&this.live &&m.live && this.getRect().intersects(m.getRect())) {
			this.live = false;
			m.live = false;
			return true;
		}
		return false;
	}
}

	