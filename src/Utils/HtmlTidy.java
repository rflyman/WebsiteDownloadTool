package Utils;

import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.w3c.tidy.Tidy;
import org.jsoup.Jsoup;
import org.jsoup.parser.Parser;
import org.jsoup.safety.Whitelist;

public class HtmlTidy {

    private Tidy tidy;
    private org.jsoup.parser.Parser jparser;

    public HtmlTidy() {
        tidy = new Tidy();
        jparser = new Parser();
    }

    public String cleanupHtml(String source) {

        String tmpsource = Jsoup.clean(source, Whitelist.basic());

        tidy.setXHTML(true);
        tidy.setAsciiChars(true);
        tidy.setDropEmptyParas(true);
        tidy.setDropProprietaryAttributes(true);
        //tidy.setPrintBodyOnly(true); // what if we want meta tags

        tidy.setEncloseText(true);
        tidy.setJoinStyles(true);
        tidy.setLogicalEmphasis(true);
        tidy.setQuoteMarks(true);
        tidy.setHideComments(true);

        // (makeClean || dropFontTags) = replaces presentational markup by style rules
        tidy.setMakeClean(true);     // remove presentational clutter.
        tidy.setDropFontTags(true);

        // word2000 = drop style & class attributes and empty p, span elements
        // draconian cleaning for Word2000
        tidy.setWord2000(true);
        tidy.setMakeBare(true);     // remove Microsoft cruft.
        tidy.setRepeatedAttributes(org.w3c.tidy.Configuration.KEEP_FIRST); // keep first or last duplicate attribute

        tidy.setForceOutput(true);

        tidy.setDropFontTags(true);
        tidy.setTidyMark(false);

        // hide output from stderr
        tidy.setShowWarnings(false);
        tidy.setErrout(new PrintWriter(new StringWriter()));

        StringWriter tmpstr = new StringWriter();
        PrintWriter tmppw = new PrintWriter(tmpstr);
        tidy.parse(new InputStreamReader(new java.io.ByteArrayInputStream(tmpsource.getBytes())), tmppw);

        String tmpcontent = tmpstr.toString();
        if (tmpcontent == null || tmpcontent.isEmpty()) {
            return "";
        } else {
            return tmpcontent;
        }
    }
}
