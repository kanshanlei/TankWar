import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

/**
 * 这个类的作用是河流
 * @author kanshanlei
 *
 */
public class River implements Environment {
	boolean live;
	int id;
	int x,y;
	public static final int WIDTH = 30;

	public static final int HEIGHT = 30;
	TankClient tc;
	
	public River(int x, int y, boolean live, int id,TankClient tc){
		this.x=x;
		this.y=y;
		this.live=live;
		this.id=id;
		this.tc=tc;
	}
	public void draw(Graphics g){
		if(!live) {
				tc.rivers.remove(this);
			return;
		}

		Color c = g.getColor();

		g.setColor(Color.BLUE);
		g.fillRect(x, y, WIDTH, HEIGHT);

		g.setColor(c);

	}
	
	public Rectangle getRect() {
		return new Rectangle(x, y, WIDTH, HEIGHT);
	}
}
