import java.io.*;
import java.net.*;

//���ڼ���hashֵ
import org.apache.commons.codec.digest.DigestUtils;



class Peer_server implements Runnable
{
	// ʵ��һ���������ļ�������
	public void run()
	{
		try
		{
			// Ԥ����
			String path = Global.path;

			// �������ڽ��շ�������Ϣ��ServerSocket
			ServerSocket comServSock = new ServerSocket(7701);
			
			// �������ڽ�����һ��peer��Ϣ�������ļ���ServerSocket
			ServerSocket fileServSock = new ServerSocket(7702);
			
			while(true)
			{
				Socket socket = comServSock.accept();
				
				//�������������
				BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				PrintWriter out = new PrintWriter(socket.getOutputStream(), false);
				
				String response = "";
				String[] respArray;

				// ѭ��������������ֱ�����յ�"HELLO"����"QUIT"������Ϣ
				while(!response.equals("HELLO") && !response.equals("QUIT"))
				{
					response = in.readLine();					
					// ������յ���������Ϣ��OPEN���ظ�ȷ����ϢHELLO
					if(response.equals("HELLO"))
					{
						out.println("ACCEPT");
						out.flush();
					}
				}

				// ѭ��������������ֱ���յ�QUIT������Ϣ
				while(!response.equals("QUIT"))
				{
					response = in.readLine();
					
					//��������ֶδ���
					respArray = response.split(" ");					

					// syntax: GET [filename]
					if(respArray[0].equals("GET"))
					{
						try
						{
							// ������ļ�����Ϊ��
							if(!respArray[1].isEmpty())
							{
								// �½�һ�������ļ������socket
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
			System.out.println("\033[1;31m[����] >>\033[0m "+e);			
			System.exit(-1);
		}
	}
}

