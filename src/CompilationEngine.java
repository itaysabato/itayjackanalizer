import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class CompilationEngine {

    private JackTokenizer tokenizer;
    private Writer writer;

    public CompilationEngine(JackTokenizer tokenizer, Writer writer) {
        this.tokenizer = tokenizer;
        this.writer = writer;
    }

    public void compileClass() throws IOException {
        writer.write("<class>\n");

        while(tokenizer.advance()){
            TokenType type = tokenizer.tokenType();

            if(!type.equals(TokenType.KEYWORD)){
                writer.write(type.wrap(tokenizer.token())+"\n");
            }
            else {
                Keyword keyword = tokenizer.keyword();
                if(keyword.equals(Keyword.FIELD) || keyword.equals(Keyword.STATIC)){
                    CompileClassVarDec(keyword);
                }
                else {
                      CompileSubroutine(keyword);
                }
            }
        }
        
        writer.write("</class>\n");
    }

    private void CompileSubroutine(Keyword keyword) {
        //To change body of created methods use File | Settings | File Templates.
    }

    private void CompileClassVarDec(Keyword keyword) {
        // http://www1.idc.ac.il/tecs/book/chapter10.pdf
    }
}
