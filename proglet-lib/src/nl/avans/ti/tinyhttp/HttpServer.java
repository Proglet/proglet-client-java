package nl.avans.ti.tinyhttp;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.stream.Collectors;

public class HttpServer {
    private int port = 8080;
    private Thread serverThread;
    private List<Client> clientThreads = new ArrayList<>();
    private ServerSocket socket;
    private Map<String, RequestHandler> handlers = new HashMap<>();
    private boolean stopping = false;

    public HttpServer(int port)
    {
        this.port = port;
    }


    public void on(String path, RequestHandler handler)
    {
        handlers.put(path, handler);
    }


    public void start()
    {
        try
        {
            socket = new ServerSocket(port);
            serverThread = new Thread(() -> {
                try {
                    while (true) {
                        Socket connection = socket.accept();
                        clientThreads = clientThreads.stream().filter(s -> s.running).collect(Collectors.toList());
                        Client client = new Client(connection);
                        clientThreads.add(client);
                        new Thread(client).start();
                    }
                } catch (IOException e) {
                    if(!stopping)
                        e.printStackTrace();
                }
            });
            serverThread.start();
        }
        catch (Throwable tr)
        {
            System.err.println("Could not start server: "+tr);
        }
    }

    public void stop()
    {
        this.stopping = true;
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        for(Client c : clientThreads)
            c.stop();
    }




    public class Client implements Runnable {
        private final Socket socket;
        public Client(Socket socket) {
            this.socket = socket;
        }
        public boolean running = true;

        @Override
        public void run() {
            try {
                BufferedReader in=new BufferedReader(new InputStreamReader(socket.getInputStream()));
                OutputStream out=new BufferedOutputStream(socket.getOutputStream());
                PrintStream pout=new PrintStream(out);

                try
                {
                    // read first line of request
                    String request=in.readLine();
                    if (request==null) { running = false; return; }

                    // we ignore the rest
                    while (true)
                    {
                        String ignore=in.readLine();
                        if (ignore==null || ignore.length()==0) break;
                    }

                    if (!request.startsWith("GET ") ||
                            !(request.endsWith(" HTTP/1.0") || request.endsWith(" HTTP/1.1")))
                    {
                        // bad request
                        pout.print("HTTP/1.0 400 Bad Request\r\n\r\n");
                    }
                    else
                    {
                        request = request.substring(4); //remove "GET "
                        request = request.substring(0, request.length()-9); //remove " HTTP/1.0"

                        Map<String, String> parameters = new HashMap<>();
                        if(request.contains("?"))
                        {
                            String p = request.substring(request.indexOf("?")+1);
                            String[] params = p.split("&");
                            for(String param : params) {
                                String[] kv = param.split("=");
                                if(kv.length == 2)
                                    parameters.put(kv[0], kv[1]);
                            }


                            request = request.substring(0, request.indexOf("?"));
                        }

                        String response = "Not found";
                        if(handlers.containsKey(request))
                            response = handlers.get(request).onRequest(parameters);

                        pout.print(
                                "HTTP/1.0 200 OK\r\n"+
                                        "Content-Type: text/html\r\n"+
                                        "Date: "+new Date()+"\r\n"+
                                        "Content-length: "+response.length()+"\r\n\r\n"+
                                        response
                        );
                    }

                    pout.close();
                    running = false;
                }
                catch (Throwable tri)
                {
                    System.err.println("Error handling request: "+tri);
                    running = false;
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
            running = false;
        }

        public void stop()
        {
            int i = 0;
            while(this.socket.isConnected() && i < 5)
            {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                i++;
            }

            try {
                if(this.socket.isConnected())
                    this.socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
