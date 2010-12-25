import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;


public class JackTokenizer {

    private Scanner source;
    private Scanner tokenizer;
    private String token = "";
    private static final String[] SYMBOLS_LOOKUP = {"\\{", "\\}", "\\(", "\\)","\\[","\\]","\\.","\\,",";","\\+","\\-","\\*","/","\\&","\\|","\\<","\\>","\\=","~"};
    private static final String[] SYMBOLS = {"{","}","(",")","[","]",".",",",";","+","-","*","/", "&amp;","|","&lt;","&gt;","=","~"};

    public JackTokenizer(File source) throws FileNotFoundException {
        this.source = new Scanner(source);
        this.source.useDelimiter("(\\s)|(//(.*))|((/\\*)(.*)(\\*/))");
    }

    /**
     *   Advances to the next line.
     */
    public boolean advance() {
        while(tokenizer != null) {
            token = tokenizer.next();
            if(!tokenizer.hasNext()){
                tokenizer = null;
            }
            if(!token.isEmpty()){
                return true;
            }
        }

        while(source.hasNext()) {
            token = source.next();

            if(!token.isEmpty()){
                for(int i = 0; i < SYMBOLS_LOOKUP.length; i++){
                    token = token.replaceAll(SYMBOLS_LOOKUP[i], " "+SYMBOLS[i]+" ");
                }

                tokenizer = new Scanner(token);
                token =  tokenizer.next();
                if(!tokenizer.hasNext()){
                    tokenizer = null;
                }
                return true;
            }
        }
        return false;
    }

    public TokenType tokenType() {
        if(Character.isDigit(token.charAt(0))){
            return TokenType.INT_CONST;
        }

        if(token.startsWith("\"")){
            token = token.replaceAll("\"", "");
            return TokenType.STRING_CONST;
        }

        for(String symbol : SYMBOLS){
            if(symbol.equals(token)){
                return TokenType.SYMBOL;
            }
        }

        for(Keyword keyword : Keyword.values()){
            if(keyword.tag.equals(token)){
                return TokenType.KEYWORD;
            }
        }

        return TokenType.IDENTIFIER;
    }

    public String token() {
        return token;
    }
    
    /**
     *    closes the file.
     */
    public void close() {
        source.close();
    }
}
