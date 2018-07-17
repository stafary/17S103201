import java.io.*;
import java.net.*;

//用于计算hash值
import org.apache.commons.codec.digest.DigestUtils;



class Peer_server implements Runnable
{
	// 实现一个基本的文件服务器
	public void run()
	{
		try
		{
			// 预定义
			String path = Global.path;

			// 建立用于接收服务器消息的ServerSocket
			ServerSocket comServSock = new ServerSocket(7701);
			
			// 建立用于接收另一个peer消息、传输文件的ServerSocket
			ServerSocket fileServSock = new ServerSocket(7702);
			
			while(true)
			{
				Socket socket = comServSock.accept();
				
				//创建输入输出流
				BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				PrintWriter out = new PrintWriter(socket.getOutputStream(), false);
				
				String response = "";
				String[] respArray;

				// 循环监听连接请求，直到接收到"HELLO"或者"QUIT"握手消息
				while(!response.equals("HELLO") && !response.equals("QUIT"))
				{
					response = in.readLine();					
					// 如果接收到的握手消息是OPEN，回复确认消息HELLO
					if(response.equals("HELLO"))
					{
						out.println("ACCEPT");
						out.flush();
					}
				}

				// 循环监听连接请求，直到收到QUIT握手消息
				while(!response.equals("QUIT"))
				{
					response = in.readLine();
					
					//请求参数分段处理
					respArray = response.split(" ");					

					// syntax: GET [filename]
					if(respArray[0].equals("GET"))
					{
						try
						{
							// 请求的文件名不为空
							if(!respArray[1].isEmpty())
							{
								// 新建一个用于文件传输的socket
								Socket fileSocket = fileServSock.accept();

								File peerfile = new File(path + File.separator + respArray[1]);								
								byte[] buffer = new byte[(int)peerfile.length()];
								BufferedInputStream fileIn = new BufferedInputStream(new FileInputStream(peerfile));
								fileIn.read(buffer, 0, buffer.length);
								BufferedOutputStream fileOut = new BufferedOutputStream(fileSocket.getOutputStream());
								fileOut.write(buffer, 0, buffer.length);
								fileOut.flush();
								fileIn.close();
								fileOut.close();
								fileSocket.close();
								
								out.println("OK");
								out.flush();
							}
						}
						catch (Exception e)
						{
							out.print("ERROR "+e);
							out.flush();
						}
					}
					else if(response.equals("CLOSE"))
					{
						continue;
					}
				}
				out.print("GOODBYE");
				out.flush();
				socket.close();
			}
		}
		catch (Exception e)
		{
			System.out.println("\033[1;31m[错误] >>\033[0m "+e);			
			System.exit(-1);
		}
	}
}

