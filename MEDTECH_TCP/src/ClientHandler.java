import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.ServerError;
import java.sql.*;

public class ClientHandler extends Thread {
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(55885)) {
            System.out.println("Server started.. Waiting for clients...");
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Client connected: " + socket.getInetAddress());

                new Thread(() -> handleClient(socket)).start();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleClient(Socket socket) {
        try (ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {

            String itemCode = (String) in.readObject();
            System.out.println("Received item code from client: " + itemCode);

            double initialPrice = fetchDataFromDatabase("select price from prices where itemcode=?" + itemCode);
            double discountValue = fetchDataFromDatabase("select discount from discount where itemcode=?" + itemCode);
            //double finalPrice=fetchDataFromDatabase("select discount from discount where itemcode=?"+itemCode);
            double finalPrice = initialPrice - discountValue;

            out.writeDouble(initialPrice);
            out.writeDouble(discountValue);
            out.writeDouble(finalPrice);
            out.flush();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static double fetchDataFromDatabase(String query, String itemCode) throws SQLException{
        try(Connection connection= DriverManager.getConnection("jdbc:mysql://localhost:3306/medtec",
                "root", "");
            PreparedStatement preparedStatement=connection.prepareStatement(query)){
            preparedStatement.setString(1,itemCode);
            try(ResultSet resultSet=preparedStatement.executeQuery()){
                return resultSet.next() ? resultSet.getDouble(1) : 0.0;
            }
        }
    }
}
