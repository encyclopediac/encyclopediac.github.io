import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class TextDocumentReaderWriter {
    private final static String READ_FILEPATH = "../TextFileExperiment.txt";
    private final static String WRITE_FILEPATH = "../TextFileExperiment.html";
    public static void main(String[] args) throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader(READ_FILEPATH));
        File writeToHTML = new File(WRITE_FILEPATH);
        writeToHTML.delete();
        BufferedWriter writer = new BufferedWriter(new FileWriter(WRITE_FILEPATH));
        String currentLine = reader.readLine();
        while (currentLine != null) {
            oneLevelDeeper(reader, writer, currentLine);
            currentLine = reader.readLine();
        }
        reader.close();
        writer.close();
    }

    private static void oneLevelDeeper(BufferedReader reader, BufferedWriter writer, String currentLine) throws Exception {
        currentLine = reader.readLine();
        if (currentLine == null) {
            return;
        } else if (currentLine.contains("##o")) {
            oneLevelDeeper(reader, writer, currentLine);
        } else if (currentLine.contains("##### ")) {
            writer.write("<h1>" + currentLine.split("##### ")[1] + "</h1>");
            writer.newLine();
        } else if (currentLine.contains("#### ") && !(currentLine.contains("##### "))) {
            writer.write("<h2>" + currentLine.split("#### ")[1] + "</h2>");
            writer.newLine();
        } else if (currentLine.contains("### ") && !(currentLine.contains("##### ") && currentLine.contains("#### "))) {
            writer.write("<h3>" + currentLine.split("### ")[1] + "</h3>");
            writeContentsOfTitles(reader, writer, currentLine);
            writer.newLine();
        }
    }

    private static void writeContentsOfTitles(BufferedReader reader, BufferedWriter writer, String currentLine) throws Exception{
        /**
         * This fills in the bullet points that summarise sources
         */
        currentLine = reader.readLine();
        writer.write("<body>");
        while (!(currentLine.contains("##c"))) {
            writer.write("<p>" + currentLine + "</p>");
            writer.newLine();
            currentLine = reader.readLine();
        }
        writer.write("</body>");
    }
}
