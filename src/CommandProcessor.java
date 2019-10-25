import java.io.*;

public class CommandProcessor {


// Receives a string and creates a process to execute a command

    public String process(String command) {
        String output = "";
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(command);
        } catch (Exception e) {
            return "Error";
        }

        BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));

        // read the output from the command
        output = ConvertBufferedReaderToString(stdInput);

        return output;
    }

    private String ConvertBufferedReaderToString(BufferedReader buffer){
        String output = "";
        String temp = "";
        while (true) {
            try {
                if ((temp = buffer.readLine()) == null) break;
            } catch (IOException e) {
                e.printStackTrace();
            }
            output = (output + temp + "\n");
        }
        return output;
    }
}

 