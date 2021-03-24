import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class TextDocumentReaderWriter {
    private final static String READ_FILEPATH = "../TextFileExperiment.txt";
    private final static String WRITE_FILEPATH = "../TextFileExperiment.html";
    private final static String TITLE_PATTERN = "###### ";
    private final static String H1_PATTERN = "##### ";
    private final static String H2_PATTERN = "#### ";
    private final static String H3_PATTERN = "### ";
    private final static String ONELEVELDOWN_PATTERN = "##o";
    private final static String ONELEVELUP_PATTERN = "##c";
    private final static String TITLE_META = "<meta property=\"og:title\" content=";
    private final static String HEAD_HTML_1 = "<html lang=\"en\"><head><meta name=\"viewport\" content=\"width=device-width\"><meta charset=\"utf-8\">";
    private final static String HEAD_HTML_2 = "<meta charset=\"utf-8\"/><link rel=\"stylesheet\" href=\"CSSFile.css\"></head>";
    private final static String CENTER_MAIN = "<div id=\"main\" class=\"center main\">";

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
        writer.write("</div>");
        reader.close();
        writer.close();
    }

    private static void oneLevelDeeper(BufferedReader reader, BufferedWriter writer, String currentLine) throws Exception {
        currentLine = reader.readLine();
        if (currentLine == null) {
            return;
        } else if (currentLine.contains(ONELEVELDOWN_PATTERN)) {
            oneLevelDeeper(reader, writer, currentLine);
        } else if (currentLine.contains(TITLE_PATTERN)) {
            writer.write(TITLE_META + "\""+ currentLine.split(TITLE_PATTERN)[1] + "\">");
            writer.write(HEAD_HTML_1);
            writer.write("<title>" + currentLine.split(TITLE_PATTERN)[1] + "</title>");
            writer.write(HEAD_HTML_2);
            writer.write(CENTER_MAIN);
            writer.newLine();
        } else if (currentLine.contains(H1_PATTERN)) {
            writer.write("<h1>" + currentLine.split(H1_PATTERN)[1] + "</h1>");
            writer.newLine();
        } else if (currentLine.contains(H2_PATTERN) && !(currentLine.contains(TITLE_PATTERN) && currentLine.contains(H1_PATTERN))) {
            writer.write("<h2>" + currentLine.split(H2_PATTERN)[1] + "</h2>");
            writer.newLine();
        } else if (currentLine.contains(H3_PATTERN) && !(currentLine.contains(TITLE_PATTERN) && currentLine.contains(H1_PATTERN) && currentLine.contains(H2_PATTERN))) {
            writer.write("<h3>" + currentLine.split(H3_PATTERN)[1] + "</h3>");
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
        while (!(currentLine.contains(ONELEVELUP_PATTERN))) {
            if (currentLine.charAt(0) == '-') {
                writer.write("<u1><li>" + currentLine.split("-")[1] + "</li></u1> <p></p>");
                writer.newLine();
            } else {
                writer.write("<h4>" + currentLine + "</h4>");
                writer.newLine();
            }
            currentLine = reader.readLine();
        }
        writer.write("</body>");
    }
}
