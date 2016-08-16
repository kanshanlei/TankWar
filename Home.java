import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

public class Home {
	boolean live;
	int x,y;
	public static final int WIDTH = 100;

	public static final int HEIGHT = 100;
	
	public void setLive(boolean live){
		this.live=live;
	}
	
	public Home(int x,int y){
		this.x=x;
		this.y=y;
	}
	
	public Rectangle getRect() {
		return new Rectangle(x, y, WIDTH, HEIGHT);
	}
	
	public void draw(Graphics g) {
		Color c = g.getColor();
		if (live)
			g.setColor(Color.YELLOW);
		else
			g.setColor(Color.BLACK);
		g.fillRect(x, y, WIDTH, HEIGHT);
		g.setColor(c);

	}
}
