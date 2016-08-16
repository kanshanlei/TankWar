import java.io.DataInputStream;
import java.net.DatagramSocket;
/**
 * ��������Э������ݽӿ�
 * @author kanshanlei
 *
 */
public interface Msg {
	/**
	 * ̹�˲�������Ϣ
	 */
	public static final int TANK_NEW_MSG = 1;
	
	/**
	 * ̹���ƶ�����Ϣ
	 */
	public static final int TANK_MOVE_MSG = 2;
	
	/**
	 * �ӵ���������Ϣ
	 */
	public static final int MISSILE_NEW_MSG = 3;
	
	/**
	 * ̹����������Ϣ
	 */
	public static final int TANK_DEAD_MSG = 4;
	
	/**
	 * �ӵ���������Ϣ
	 */
	public static final int MISSILE_DEAD_MSG = 5;
	
	public static final int HOME_DEAD_MSG = 6;
	
	public static final int WALL_NEW_MSG = 7;
	
	public static final int WALL_DEAD_MSG = 8;
	
	/**
	 * ��������
	 * @param ds
	 * @param IP
	 * @param udpPort
	 */
	public void send(DatagramSocket ds, String IP, int udpPort);
	
	/**
	 * ���ղ���������
	 * @param dis
	 */
	public void parse(DataInputStream dis);
}
