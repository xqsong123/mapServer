
import java.io.*;
import java.net.Socket;

public class TestSocket {
    public static void main(String[] args) throws IOException {
        File login = new File("/home/byy/login.xml");
        BufferedReader bufferedReader = null;

        File file = new File("/home/byy/socketTest.xml");
        BufferedWriter fileWriter = null;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(login)));
            fileWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            Socket socket = new Socket("10.48.21.71", 4531);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8"));
            System.out.println("connnect server");
            OutputStream out = socket.getOutputStream();
            System.out.println("send login message");
            out.write("<login='bjtykj' password='123456'/> ".getBytes("UTF-8"));
            System.out.printf(in.readLine());
            new Thread(() ->  {
                    try {
                        while (true){
                            System.out.println("send link message");
                            out.write("<link username='bjtykj' password='123456'/>".getBytes("UTF-8"));
                            Thread.sleep(30000);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            );

            String line = null;
            System.out.println("start to write");
            while (in != null){
                while ((line=in.readLine()) != null){
                    System.out.printf(line);
                    fileWriter.write(line);
                }
                fileWriter.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(fileWriter!=null){
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
