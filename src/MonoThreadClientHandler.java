import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MonoThreadClientHandler implements Runnable {

    private static Socket clientDialog;
    private static List<Socket> clients = new ArrayList<>();


    public MonoThreadClientHandler(Socket client) {
        MonoThreadClientHandler.clientDialog = client;
        clients.add(client);
    }

    @Override
    public void run() {

        try {
            // инициируем каналы общения в сокете, для сервера

            // канал записи в сокет следует инициализировать сначала канал чтения для избежания блокировки выполнения программы на ожидании заголовка в сокете
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientDialog.getOutputStream()));

// канал чтения из сокета
//            DataInputStream in = new DataInputStream(clientDialog.getInputStream());
            BufferedReader in = new BufferedReader(new InputStreamReader(clientDialog.getInputStream()));
//            System.out.println("DataInputStream created");

//            System.out.println("DataOutputStream  created");
            ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            // основная рабочая часть //
            //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

            // начинаем диалог с подключенным клиентом в цикле, пока сокет не
            // закрыт клиентом
            while (!clientDialog.isClosed()) {
//                System.out.println("Server reading from channel");

                // серверная нить ждёт в канале чтения (inputstream) получения
                // данных клиента после получения данных считывает их
                String entry = in.readLine();

                // и выводит в консоль
                System.out.println(entry);

                // инициализация проверки условия продолжения работы с клиентом
                // по этому сокету по кодовому слову - quit в любом регистре
                if (entry.startsWith("$$")) {

                    // если кодовое слово получено то инициализируется закрытие
                    // серверной нити
//                    System.out.println("Client initialize connections suicide ...");
//                    out.writeUTF("Server reply - " + entry + " - OK");
                    for (Socket socket : clients) {
                        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
//                    System.out.println("Server try writing to channel");
                        out.write(entry + "\n");
//                    out.newLine();
//                    System.out.println("Server Wrote message to clientDialog.");
                        out.flush();
                    }
                }

                // если условие окончания работы не верно - продолжаем работу -
                // отправляем эхо обратно клиенту

                for (Socket socket : clients) {
                    out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
//                    System.out.println("Server try writing to channel");
                    out.write(entry + "\n");
//                    out.newLine();
//                    System.out.println("Server Wrote message to clientDialog.");
                    out.flush();
                }

                // освобождаем буфер сетевых сообщений


                // возвращаемся в началло для считывания нового сообщения
            }

            ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            // основная рабочая часть //
            //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

            // если условие выхода - верно выключаем соединения
            System.out.println("Client disconnected");
            System.out.println("Closing connections & channels.");

            // закрываем сначала каналы сокета !
            in.close();
            out.close();

            // потом закрываем сокет общения с клиентом в нити моносервера
            clientDialog.close();

            System.out.println("Closing connections & channels - DONE.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}