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
	 * 根据tank的信息构建消息
	 * @param tank
	 */
	public WallNewMsg(Wall w) {
		this.w = w;
	}
	
	/**
	 * 根据消息产生的场所构建新的消息
	 * @param tc
	 */
	public WallNewMsg(TankClient tc) {
		this.tc = tc;
	}
	
	/**
	 * 发送相关的消息
	 * @param ds 通过该socket发送数据
	 * @param IP 数据的目标IP
	 * @param udpPort 数据的目标端口
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
	 * 分析接收到的消息数据
	 * @param dis 接收到的消息数据的输入流
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
