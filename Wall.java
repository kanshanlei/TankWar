import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

/**
 * Ç½±ÚÀà
 * @author kanshanlei
 *
 */
public class Wall implements Environment{
	boolean live;
	int id;
	int x,y;
	public static final int WIDTH = 30;

	public static final int HEIGHT = 30;
	TankClient tc;
	
	public Wall(int x, int y, boolean live, int id,TankClient tc){
		this.x=x;
		this.y=y;
		this.live=live;
		this.id=id;
		this.tc=tc;
	}
	public void draw(Graphics g){
		if(!live) {
				tc.walls.remove(this);
			return;
		}

		Color c = g.getColor();

		g.setColor(Color.ORANGE);
		g.fillRect(x, y, WIDTH, HEIGHT);

		g.setColor(c);
		g.drawLine(x , y + HEIGHT / 2, x+ WIDTH , y + HEIGHT / 2);
		g.drawLine(x + WIDTH / 2, y, x + WIDTH / 2, y + HEIGHT);
		g.drawLine(x , y, x , y + HEIGHT);
		g.drawLine(x , y, x + WIDTH , y );
		g.drawLine(x + WIDTH , y , x + WIDTH, y + HEIGHT);
		g.drawLine(x , y + HEIGHT, x  + WIDTH , y + HEIGHT);
	}
	
	public Rectangle getRect() {
		return new Rectangle(x, y, WIDTH, HEIGHT);
	}

}
