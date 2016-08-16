import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

public class WallNewMsg implements Msg {
	int msgType = Msg.WALL_NEW_MSG;

	Wall w;

	TankClient tc;
	
	/**
	 * ����tank����Ϣ������Ϣ
	 * @param tank
	 */
	public WallNewMsg(Wall w) {
		this.w = w;
	}
	
	/**
	 * ������Ϣ�����ĳ��������µ���Ϣ
	 * @param tc
	 */
	public WallNewMsg(TankClient tc) {
		this.tc = tc;
	}
	
	/**
	 * ������ص���Ϣ
	 * @param ds ͨ����socket��������
	 * @param IP ���ݵ�Ŀ��IP
	 * @param udpPort ���ݵ�Ŀ��˿�
	 */
	public void send(DatagramSocket ds, String IP, int udpPort) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		try {
			dos.writeInt(msgType);
			dos.writeInt(w.id);
			dos.writeInt(w.x);
			dos.writeInt(w.y);
		} catch (IOException e) {
			e.printStackTrace();
		}
		byte[] buf = baos.toByteArray();
		try {
			DatagramPacket dp = new DatagramPacket(buf, buf.length,
					new InetSocketAddress(IP, udpPort));
			ds.send(dp);
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	/**
	 * �������յ�����Ϣ����
	 * @param dis ���յ�����Ϣ���ݵ�������
	 */
	public void parse(DataInputStream dis) {
		try {
			int id = dis.readInt();
			int x = dis.readInt();
			int y = dis.readInt();

			for (int i = 0; i < tc.walls.size(); i++) {
				Wall w = tc.walls.get(i);
				if (w.id == id)
					return;
			}

				Wall w = new Wall(x, y, true, id, tc);
	
				tc.walls.add(w);

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
