import java.net.*;
import java.io.*;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

//Creazione di una classe per il Multrithreading
class server extends Thread {
    private Socket socket;

    public server (Socket socket) {
        this.socket = socket;
        System.out.println("  Stato    Tipo Richiesta  Porta Server  Porta Client  Indirizzo Cliernt\n");
    }

    public double mathing(String expression) {
        double result = 0;
        char operator = '+';
        int numStartIndex = 0;

        for (int i = 0; i < expression.length(); i++) {
            char ch = expression.charAt(i);

            if (Character.isDigit(ch)) {
                continue;
            }

            double num = Double.parseDouble(expression.substring(numStartIndex, i));
            numStartIndex = i + 1;

            switch (operator) {
                case '+':
                    result += num;
                    break;
                case '-':
                    result -= num;
                    break;
                case '*':
                    result *= num;
                    break;
                case '/':
                    result /= num;
                    break;
            }

            operator = ch;
        }

        double lastNum = Double.parseDouble(expression.substring(numStartIndex));
        switch (operator) {
            case '+':
                result += lastNum;
                break;
            case '-':
                result -= lastNum;
                break;
            case '*':
                result *= lastNum;
                break;
            case '/':
                result /= lastNum;
                break;
        }

        return result;
    }

    //esecuzione del Thread sul Socket
    public void run() {
        try {
            DataInputStream is = new DataInputStream(socket.getInputStream());
            DataOutputStream os = new DataOutputStream(socket.getOutputStream());

            String[] userInput;
            while (true) {
                userInput = is.readLine().split(" ");

                switch(userInput[0]){
                    case "exit":
                        os.writeBytes("Disconnessione dal Server...");

                        os.close();
                        is.close();
                        System.out.println("Ricezione una chiamata di chiusura da:\n" + socket + "\n");
                        socket.close();

                    return;

                    case "help":

                        //tentativo di inviare un messaggio di aiuto multilinea
                        /* String msg = "Comandi disponibili: \nexit: disconnette il client dal server\nhelp: mostra i comandi disponibili\nTIME: mostra l'ora attuale\nDATE: mostra la data attuale\n";
                        String[] msgArray = msg.split("\n");
                        for (int i = 0; i < msgArray.length; i++) {
                            os.writeBytes(msgArray[i] + '\n');
                        }
                        os.writeBytes(""); */

                        os.writeBytes("comandi disponibili: exit, help, TIME, DATE, MATH: MATH expression (do not put spacec in the expression)\n");
                        os.flush();
                    break;

                    case "TIME":
                        os.writeBytes("L'ora attuale è: " + java.time.LocalTime.now() +"\n");
                    break;

                    case "DATE":
                        os.writeBytes("La data attuale è: " + java.time.LocalDate.now() +"\n");
                    break;

                    case "MATH":
                        os.writeBytes("Inserisci un'operazione matematica: ");
                        os.flush();

                        double result = mathing(userInput[1]);

                        os.writeBytes("Hai digitato: " + result + "\n");
                        os.flush();
                    break;

                    default:
                        os.writeBytes("Hai digitato: " + userInput[0] + "\n");
                        os.flush();
                    break;
                }
                //os.writeBytes(userInput + '\n');
                System.out.println("Il Client "+ socket.getInetAddress() +" "
                + socket.getPort() +" "
                + socket.getLocalPort() +" ha scritto: " + userInput);
            }
            
        }
        catch (IOException e) {
            System.out.println("IOException: " + e);
        }
    }
}
