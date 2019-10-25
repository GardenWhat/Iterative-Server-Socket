import java.io.*;
import java.net.*;
import java.util.*;
import java.util.Scanner;

class Client {

    public static void main(String[] args) throws IOException {  
        String domain = null;
        int port = 0;
        int requests = 0;
        int type = 0;
        boolean check = true;
        String temp = null;
        Scanner in = new Scanner(System.in);
        System.out.print("Enter the network address: ");
        domain = in.nextLine();

        File time = new File("time.tsv");
        if (time.exists()) {
            time.delete();
        }
        time.createNewFile();


        FileWriter timeFileWriter = new FileWriter(time, true);
        PrintWriter timeWriter = new PrintWriter(timeFileWriter);
	timeWriter.print("Thread\tTurnaround Time\n");



        while (check == true) {
            System.out.print("Enter the port number: ");
            temp = in.nextLine();
            if (checkInt(temp) == false) {
                System.out.println("Invalid response, please enter numeric value");
            } else {
                port = Integer.parseInt(temp);
                check = false;
            }
        }
        System.out.println("Searching for server...");
        while (type < 1 || type > 6) {
            System.out.print("Enter the operation you would like completed. Please enter the adjacent number...\n1:Date and Time\n2:Uptime\n3:Memory Use\n4:Netstat\n5:Current Users\n6:Running Processes\nYour Choice:");
            temp = in.nextLine();
            if (checkInt(temp)) {
                type = Integer.parseInt(temp);
                if (type < 1 || type > 6) {
                    System.out.println("Invalid response...");
                }
            } else {
                System.out.println("Invalid response, please enter numeric value...");
            }
        }
        System.out.println("Enter number of requests you would like to be sent: ");
        requests = Integer.parseInt(in.nextLine());
        long start = System.nanoTime();
        Request[] table = new Request[requests];
        for (int i = 0; i < requests; i++) {
            table[i] = new Request(type, requests, domain, port, i);
            (new Thread(table[i])).start();
        }

        check = true;
        while (check == true) {
            for (int i = 0; i < table.length; i++) {
                if (table[i].getTime() == -1) {
                    i = 0;
                }
            }
            check = false;
        }
        for (int i = 0; i < table.length; i++) {
            System.out.println("Total Turn-around Time Elapsed in Thread " + (table[i].getThread() + 1) + ": " + (table[i].getTime() / 1000000) + " milliseconds");
            timeWriter.print( i + "\t" + (table[i].getTime() / 1000000) + "\n");
        }
        long totalTime = 0;
        for (int i = 0; i < table.length; i++) {
            totalTime = (table[i].getTime() / 1000000) + totalTime;
        }
        System.out.println("Total Turn-around Time: " + totalTime + " milliseconds");
        System.out.println("Average Turn-around Time: " + totalTime / requests + " milliseconds");
        timeWriter.close();
    }

    public static boolean checkInt(String temp) {
        for (int i = 0; i < temp.length(); i++) {
            if (Character.isDigit(temp.charAt(i))) {
            } else {
                return false;
            }
        }
        return true;
    }
}

//----------------------------------
class Request implements Runnable {

    String type;
    int port;
    String domain;
    long start;
    long end;
    long time = -1;
    int threadNum;

    Request(int typeVar, int num, String domainVar, int portVar, int threadNumVar) {
        threadNum = threadNumVar;
        if (typeVar == 1) {
            type = "date";
        } else if (typeVar == 2) {
            type = "uptime";
        } else if (typeVar == 3) {
            type = "free";
        } else if (typeVar == 4) {
            type = "netstat";
        } else if (typeVar == 5) {
            type = "users";
        } else if (typeVar == 6) {
            type = "ps -e";
        } else {
            System.out.println("Error");
        }
        domain = domainVar;
        port = portVar;
    }

    public void run() {
        start = System.nanoTime();
        try (Socket socket = new Socket(domain, port)) {
            System.out.println("Found server...");
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            writer.println(type);
            String line = reader.readLine();
            while (true) {
                System.out.println(line);
                line = reader.readLine();
                if (line.trim().equals(""))
                    break;
            }
            socket.close();
        } catch (UnknownHostException ex) {
            System.out.println("Server not found...");

        } catch (IOException ex) {
            System.out.println("I/O Error... ");

        }

        end = System.nanoTime();
        time = end - start;
    }

    public long getTime() {
        return time;
    }

    public int getThread() {
        return threadNum;
    }
}
