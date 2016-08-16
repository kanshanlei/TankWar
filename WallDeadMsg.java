import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;


public class WallDeadMsg implements Msg{
	int msgType = Msg.WALL_DEAD_MSG;
	TankClient tc;
	Wall w;
	
	public WallDeadMsg(TankClient tc){
		this.tc=tc;
	}
	
	public WallDeadMsg(Wall w){
		this.w=w;
	}
	@Override
	public void send(DatagramSocket ds, String IP, int udpPort) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		try {
			dos.writeInt(msgType);
			dos.writeInt(w.id);
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

	@Override
	public void parse(DataInputStream dis) {
		try {
			int id = dis.readInt();

			for (int i = 0; i < tc.walls.size(); i++) {
				Wall w = tc.walls.get(i);
				if (w.id == id) {
					w.live=false;
					break;
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
