import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class ClientSide {
    public static void main(String[] args) {
        try{
            while(true){
                Socket socket=new Socket("localhost",55885);
                ObjectOutputStream out=new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream input=new ObjectInputStream(socket.getInputStream());

                Scanner s=new Scanner(System.in);
                System.out.println("Enter item code(or type 'exit' to end): ");
                String itemCode=s.nextLine();

                if("exit".equalsIgnoreCase(itemCode.trim())){
                    break;
                }
                out.writeObject(itemCode);
                out.flush();

                double initialPrice=input.readDouble();
                double discountValue=input.readDouble();
                double finalPrice=input.readDouble();

                System.out.println("Item code: "+itemCode);
                System.out.println("Initial Price: "+initialPrice);
                System.out.println("Discount Value: "+discountValue);
                System.out.println("Final Price: "+finalPrice);

                socket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
