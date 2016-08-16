import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.Random;

/**
 * 代表坦克的类
 * @author mashibing&kanshanlei
 *
 */
public class Tank {
	int id;

	public static final int XSPEED = 5;

	public static final int YSPEED = 5;

	public static final int WIDTH = 30;

	public static final int HEIGHT = 30;

	boolean good;
	
	boolean crosswall=false;

	int x, y;
	private int oldX,oldY;

	private static Random r = new Random();

	private boolean live = true;

	private int step = r.nextInt(12) + 3;

	TankClient tc;

	boolean bL, bU, bR, bD;

	Dir dir = Dir.STOP;

	Dir ptDir = Dir.D;
	
	/**
	 * 根据位置和好坏构建坦克
	 * @param x
	 * @param y
	 * @param good
	 */
	public Tank(int x, int y, boolean good) {
		this.x = x;
		this.y = y;
		this.good = good;
	}
	
	public Tank(int id,int x, int y, boolean good, Dir dir,TankClient tc) {
		this(x, y, good);
		this.dir = dir;
		this.id=id;
		this.tc=tc;
	}
	
	/**
	 * 根据相关属性构建坦克
	 * @param x
	 * @param y
	 * @param good
	 * @param dir
	 * @param tc 游戏的场所
	 */
	public Tank(int x, int y, boolean good, Dir dir, TankClient tc) {
		this(x, y, good);
		this.dir = dir;
		this.tc = tc;
	}
	
	/**
	 * 画出坦克
	 * @param g 画笔
	 */
	public void draw(Graphics g) {
		if (!live) {
			if (!good) {
				tc.tanks.remove(this);
			}
			return;
		}

		Color c = g.getColor();
		if (good)
			g.setColor(Color.RED);
		else
			g.setColor(Color.DARK_GRAY);
		g.fillOval(x, y, WIDTH, HEIGHT);
		g.drawString("id:" + id, x, y - 10);
		g.setColor(c);

		switch (ptDir) {
		case L:
			g.drawLine(x + WIDTH / 2, y + HEIGHT / 2, x, y + HEIGHT / 2);
			break;
		case U:
			g.drawLine(x + WIDTH / 2, y + HEIGHT / 2, x + WIDTH / 2, y);
			break;
		case R:
			g.drawLine(x + WIDTH / 2, y + HEIGHT / 2, x + WIDTH, y
							+ HEIGHT / 2);
			break;
		case D:
			g.drawLine(x + WIDTH / 2, y + HEIGHT / 2, x + WIDTH / 2, y
							+ HEIGHT);
			break;
		default:
			break;
		}

		move();
	}
	
	private void move() {
		this.oldX=x;
		this.oldY=y;
		if(good||!tc.pause)
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

		if (dir != Dir.STOP) {
			ptDir = dir;
		}

		if (x < 0)
			x = 0;
		if (y < 30)
			y = 30;
		if (x + WIDTH > TankClient.GAME_WIDTH)
			x = TankClient.GAME_WIDTH - WIDTH;
		if (y + HEIGHT > TankClient.GAME_HEIGHT)
			y = TankClient.GAME_HEIGHT - HEIGHT;

		 if(tc.myTank.id==100&&!good&&!tc.pause) { 
			 if(step == 0) { 
				 step = r.nextInt(12) + 3; 
				 Dir[] dirs =Dir.values(); 
				 dir = dirs[r.nextInt(dirs.length)];
				 TankMoveMsg msg = new TankMoveMsg(id, x, y, dir, ptDir);
				 tc.nc.send(msg);
				 } 
			 step --;
			 if(r.nextInt(40) > 38) this.fire(); 
		 }		 
	}
	private void stay(){
		x=oldX;
		y=oldY;
	}
	
	public boolean collide(Environment e){
		if(crosswall) return false;
		if(this.live&&this.getRect().intersects(e.getRect())){
			this.stay();
			return true;
		}
		return false;
	}
	
	public boolean collide(Tank otherTank){
		if(crosswall||otherTank.id == this.id) return false;
		if(this.live&&this.getRect().intersects(otherTank.getRect())){
			this.stay();
			return true;
		}
		return false;
	}
	
	/**
	 * 键按下的消息处理
	 * @param e 按键事件
	 */
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		switch (key) {
		case KeyEvent.VK_LEFT:
			bL = true;
			break;
		case KeyEvent.VK_UP:
			bU = true;
			break;
		case KeyEvent.VK_RIGHT:
			bR = true;
			break;
		case KeyEvent.VK_DOWN:
			bD = true;
			break;


		}
		locateDirection();
	}

	private void locateDirection() {
		Dir oldDir = this.dir;

		if (bL && !bU && !bR && !bD)
			dir = Dir.L;
		else if (!bL && bU && !bR && !bD)
			dir = Dir.U;
		else if (!bL && !bU && bR && !bD)
			dir = Dir.R;
		else if (!bL && !bU && !bR && bD)
			dir = Dir.D;
		else if (!bL && !bU && !bR && !bD)
			dir = Dir.STOP;

		if (dir != oldDir) {
			TankMoveMsg msg = new TankMoveMsg(id, x, y, dir, ptDir);
			tc.nc.send(msg);
		}
	}
	
	/**
	 * 键抬起的消息处理
	 * @param e 抬键消息
	 */
	public void keyReleased(KeyEvent e) {
		int key = e.getKeyCode();
		switch (key) {
		case KeyEvent.VK_SPACE:
			fire();
			break;
		case KeyEvent.VK_LEFT:
			bL = false;
			break;
		case KeyEvent.VK_UP:
			bU = false;
			break;
		case KeyEvent.VK_RIGHT:
			bR = false;
			break;
		case KeyEvent.VK_DOWN:
			bD = false;
			break;
		case KeyEvent.VK_ENTER:
			if(tc.pause){
				crosswall=false;
				tc.pause=false;
			}
			else{
				crosswall=true;
				tc.pause=true;
			}
			break;
		case KeyEvent.VK_1:
			Wall w=new Wall(x,y,true,tc.nc.wid++,tc);
			tc.walls.add(w);
			break;
		case KeyEvent.VK_2:
			River r=new River(x,y,true,tc.nc.rid++,tc);
			tc.rivers.add(r);
			break;
		case KeyEvent.VK_F1:
			tc.myTank.live=true;
			break;	
		}
		locateDirection();
	}

	private Missile fire() {
		if (!live)
			return null;

		int x = this.x + WIDTH / 2 - Missile.WIDTH / 2;
		int y = this.y + HEIGHT / 2 - Missile.HEIGHT / 2;
		Missile m = new Missile(id, x, y, this.good, ptDir,tc);

		tc.missiles.add(m);
		
		MissileNewMsg msg = new MissileNewMsg(m);
		tc.nc.send(msg);

		return m;
	}
	
	/**
	 * 取得坦克的外切方形
	 * @return 坦克的外切Rectangle
	 */
	public Rectangle getRect() {
		return new Rectangle(x, y, WIDTH, HEIGHT);
	}
	
	/**
	 * 检测坦克是否还活着
	 * @return
	 */
	public boolean isLive() {
		return live;
	}
	
	/**
	 * 设定坦克的生死状态
	 * @param live
	 */
	public void setLive(boolean live) {
		this.live = live;
	}
}
