import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

public class TextDocumentReaderWriter {
    private final static String READ_TEXTSOURCE_FILEPATH = "../Encyclopediac's Totally Impartial Factsheet.txt";
    private final static String WRITE_FS_FILEPATH = "../Encyclopediac's Totally Impartial Factsheet.html";
    private final static String WRITE_TOC_PATH = "../Encyclopediac's Totally Impartial Factsheet - Table of Contents.html";
    private final static String TOC_BASE_HYPERLINK = "https://encyclopediac.github.io/Encyclopediac's Totally Impartial Factsheet.html#";

    private final static String TITLE_PATTERN = "###### ";
    private final static String H1_PATTERN = "##### ";
    private final static String H2_PATTERN = "#### ";
    private final static String H3_PATTERN = "### ";

    private static int h1_Count = -1;
    // this is initialised at -1 so that the count startsfrom 0 when creating IDs.
    private static int h2_Count = 0;
    private static int h3_Count = 0;
    private static int image_Count = 0;

    private final static String ENDCONTENTSUMMARY_PATTERN = "##c";
    private final static String TITLE_META = "<meta property=\"og:title\" content=";
    private final static String HEAD_HTML_1 = "<!DOCTYPE html><html lang=\"en\"><head><meta name=\"viewport\" content=\"width=device-width\"><meta charset=\"utf-8\">";
    private final static String HEAD_HTML_2 = "<meta charset=\"utf-8\"/><link rel=\"stylesheet\" href=\"Style.css\"></head>";
    private final static String CENTRE_MAIN = "<div id=\"main\" class=\"centre main\">";
    private final static int INDENT_PX = 20;

    private static ArrayList<String> listOfHeaderLinks = new ArrayList<String>();

    public static void main(String[] args) throws Exception {
        BufferedReader textSourceReader = new BufferedReader(new FileReader(READ_TEXTSOURCE_FILEPATH));
        File writeToHTML = new File(WRITE_FS_FILEPATH);
        writeToHTML.delete();

        BufferedWriter factsheetHTMLWriter = new BufferedWriter(new FileWriter(WRITE_FS_FILEPATH));
        String currentLine = textSourceReader.readLine();
        while (currentLine != null) {
            writeNextTagsAndOrContent(textSourceReader, factsheetHTMLWriter, currentLine);
            currentLine = textSourceReader.readLine();
        }
        produceTableOfContents();
        factsheetHTMLWriter.write("</div>");
        factsheetHTMLWriter.write("</body>");
        factsheetHTMLWriter.write("</html>");
        textSourceReader.close();
        factsheetHTMLWriter.close();
        produceSubtopicExclusiveSheets();
    }

    private static void writeNextTagsAndOrContent(BufferedReader reader, BufferedWriter writer, String currentLine) throws Exception {
        if (currentLine.contains(TITLE_PATTERN)) {
            writeTitleAndMetaTags(writer, currentLine);
        } else if (currentLine.contains(H1_PATTERN)) {
            h1_Count++;
            h2_Count = 0;
            h3_Count = 0;
            writer.write("<h1" + produceIDs(currentLine, H1_PATTERN) + h1H2H3LinksBegin(currentLine, H1_PATTERN) + "</h1>");
            writer.newLine();
        } else if (currentLine.contains(H2_PATTERN) && !(currentLine.contains(TITLE_PATTERN) && currentLine.contains(H1_PATTERN))) {
            h2_Count++;
            h3_Count = 0;
            writer.write("<h2" + produceIDs(currentLine, H2_PATTERN) + h1H2H3LinksBegin(currentLine, H2_PATTERN) + "</h2>");
            writer.newLine();
        } else if (currentLine.contains(H3_PATTERN) && !(currentLine.contains(TITLE_PATTERN) && currentLine.contains(H1_PATTERN) && currentLine.contains(H2_PATTERN))) {
            h3_Count++;
            writer.write("<h3" + produceIDs(currentLine, H3_PATTERN) + h1H2H3LinksBegin(currentLine, H3_PATTERN) + "</h3>");
            writeContentsOfTitles(reader, writer, currentLine);
            writer.newLine();
        }
    }

    private static void writeTitleAndMetaTags(BufferedWriter writer, String currentLine) throws Exception {
        writer.write(HEAD_HTML_1);
        writer.write(TITLE_META + "\""+ currentLine.split(TITLE_PATTERN)[1] + "\">");
        writer.write("<title>" + currentLine.split(TITLE_PATTERN)[1] + "</title>");
        writer.write(HEAD_HTML_2);
        writer.write("<body>");
        writer.write(CENTRE_MAIN);
        writer.newLine();
    }

    private static void writeContentsOfTitles(BufferedReader reader, BufferedWriter writer, String currentLine) throws Exception{
        /**
         * This fills in the bullet points that summarise sources
         */
        currentLine = reader.readLine();
        image_Count = 0;
        while (!(currentLine.contains(ENDCONTENTSUMMARY_PATTERN))) {
            if (currentLine.equals("")) {
                //Nothing is done, ignore empty lines.
            } else if (currentLine.charAt(0) == '+') {
                writer.write("<ul><li>" + currentLine.split("\\+ ")[1] + "<a href=\"https://encyclopediac.github.io/Encyclopediac's%20Totally%20Impartial%20Factsheet%20-%20Table%20of%20Contents.html\" style=\"color:blue;text-decoration:underline\">Table of Contents</a>" + "</li></ul>");
                writer.newLine();
            } else if (currentLine.contains("- LINK: ")) {
                writer.write("<ul><li>" + currentLine.split("- ")[1].split("http")[0] + "<a href=\"http"+ currentLine.split("- ")[1].split("http")[1] + "\" style=\"color:blue;text-decoration:underline\">http" + currentLine.split("- ")[1].split("http")[1] + "</a>"+ "</li></ul>");
                writer.newLine();
            } else if (currentLine.charAt(0) == '-') {
                writer.write("<ul><li>" + currentLine.split("- ")[1] + "</li></ul>");
                writer.newLine();
            } else if (currentLine.charAt(0) == '[') {
                writer.write("<h4>" + currentLine + "</h4>");
                writer.newLine();
            } else if (currentLine.charAt(0) == '*') {
                int numberOfIndents = 1;
                for (int counter = 1; counter < currentLine.length(); counter++) {
                    if (currentLine.charAt(counter) == '*') {
                        numberOfIndents++;
                    } else {
                        break;
                    }
                }
                writer.write("<ul style=\"margin-left:" + INDENT_PX*numberOfIndents + "px\"" + "><li>" + currentLine.split("\\* ")[1] + "</li></ul>");
                writer.newLine();
            } else if (currentLine.contains("image")) {
                    String imagePath = h1_Count+ "-" + h2_Count + "-" + h3_Count + "-image-" + image_Count + ".png";
                    writer.write("<div id=\"" + imagePath.split(".png")[0] + "-main\" class=\"keyquotes images\">");
                    writer.newLine();
                    writer.write("<img src=\"images/" + imagePath+ "\" alt=\"" + currentLine.split("image ")[1] + "\">");
                    writer.newLine();
                    writer.write("</div>");
                    writer.newLine();
                    image_Count++;
            }
            currentLine = reader.readLine();
        }
    }

    private static String h1H2H3LinksBegin (String currentLine, String pattern) throws Exception {
        String linkText = "<a href=\"#" + h1_Count+ "-" + h2_Count + "-" + h3_Count + "_" + currentLine.split(pattern)[1].toLowerCase().replace(" ", "_").replace("&", "&amp;").replace("'", "_") + "\">" + currentLine.split(H3_PATTERN)[1] + "</a>";
        listOfHeaderLinks.add(linkText);
        return linkText;
    }

    private static String produceIDs(String currentLine, String pattern) {
        String linkID = " id=" + h1_Count+ "-" + h2_Count + "-" + h3_Count + "_" + currentLine.split(pattern)[1].toLowerCase().replace(" ", "_").replace("&", "&amp;").replace("'", "_") + ">";
        return linkID;
    }

    private static void produceTableOfContents() throws Exception{
        BufferedWriter tableOfContentsWriter = new BufferedWriter(new FileWriter(WRITE_TOC_PATH));
        tableOfContentsWriter.write(HEAD_HTML_1 + TITLE_META + "\"Encyclopediac's Totally Impartial Factsheet - Table of Contents\">" + "<title>Table of Contents</title>" + HEAD_HTML_2);
        tableOfContentsWriter.newLine();
        tableOfContentsWriter.write("<body>");
        tableOfContentsWriter.write(CENTRE_MAIN);
        tableOfContentsWriter.newLine();
        tableOfContentsWriter.write("<h1>Encyclopediac's Totally Impartial Factsheet - Table of Contents</h1>");
        int lastH1FoundInLoop = -1;
        int currentH1FoundInLoop = 0;
        int lastH2FoundInLoop = 0;
        int currentH2FoundInLoop = 0;
        int lastH3FoundInLoop = 0;
        int currentH3FoundInLoop = 0;
        for (String headers : listOfHeaderLinks) {
            currentH1FoundInLoop = Integer.parseInt(headers.charAt(10)+"");
            currentH2FoundInLoop = Integer.parseInt(headers.charAt(12)+"");
            currentH3FoundInLoop = Integer.parseInt(headers.charAt(14)+"");
            if (lastH1FoundInLoop != currentH1FoundInLoop) {
                lastH2FoundInLoop = 0;
                lastH3FoundInLoop = 0;
                lastH1FoundInLoop = currentH1FoundInLoop;
                tableOfContentsWriter.write("<ul><li><a href=\"" + TOC_BASE_HYPERLINK + headers.split("href=\"#")[1] + " (<a href=\"https://encyclopediac.github.io/subtopics/" + headers.split("\">")[1].split("</a>")[0] + ".html\" style=\"color:blue;text-decoration:underline\">dedicated page here</a>)</li></ul>");
            } else if (lastH2FoundInLoop != currentH2FoundInLoop) {
                lastH2FoundInLoop = currentH2FoundInLoop;
                tableOfContentsWriter.write("<ul><ul><li><a href=\"" + TOC_BASE_HYPERLINK + headers.split("href=\"#")[1] + "</li></ul></ul>");
            } else if (lastH3FoundInLoop != currentH3FoundInLoop) {
                lastH3FoundInLoop = currentH3FoundInLoop;
                tableOfContentsWriter.write("<ul><ul><ul><li><a href=\"" + TOC_BASE_HYPERLINK + headers.split("href=\"#")[1] + "</li></ul></ul></ul>");
            }
            tableOfContentsWriter.newLine();
        }
        tableOfContentsWriter.write("</div>");
        tableOfContentsWriter.write("</body>");
        tableOfContentsWriter.close();
    }

    private static void produceSubtopicExclusiveSheets() throws Exception{
        BufferedReader reader = new BufferedReader(new FileReader(WRITE_FS_FILEPATH));
        BufferedWriter subtopicWriter = null;
        File file = null;
        String currentLine = reader.readLine();
        boolean isFirstH1 = true;
        boolean hasFileBegun = false;
        String filePath = "";
        while (currentLine != null) {
            if (currentLine.contains("<h1 ")) {
                if (!isFirstH1) {
                    subtopicWriter.write("</div></body>");
                    subtopicWriter.newLine();
                    subtopicWriter.flush();
                } else {
                    isFirstH1 = false;
                }
                filePath = "../" + currentLine.split("\">")[1].split("</a></h1>")[0] + ".html";
                file = new File(filePath);
                file.delete();
                subtopicWriter = new BufferedWriter(new FileWriter("../" + currentLine.split("\">")[1].split("</a></h1>")[0] + ".html"));
                writeTitleAndMetaTags(subtopicWriter, "###### " + currentLine.split("\">")[1].split("</a></h1>")[0]);
                subtopicWriter.write(currentLine);
                subtopicWriter.newLine();
            } else {
                if (!hasFileBegun) {
                    hasFileBegun = true;
                } else {
                    subtopicWriter.write(currentLine);
                    subtopicWriter.newLine();
                }
            }
            currentLine = reader.readLine();
        }
        subtopicWriter.close();
        reader.close();
    }
}