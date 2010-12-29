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
                    compileClassVarDec(keyword);
                }
                else {
                    compileSubroutine(keyword);
                }
            }
        }

        writer.write("</class>\n");
    }

    private void compileSubroutine(Keyword keyword) throws IOException {
        writer.write("<subroutineDec>\n");
        writer.write(TokenType.KEYWORD.wrap(keyword));

        while(tokenizer.advance()){
            TokenType type = tokenizer.tokenType();
            if(!type.equals(TokenType.SYMBOL)){
                writer.write(type.wrap(tokenizer.token())+"\n");
            }
            else {
                String symbol = tokenizer.token();
                if(symbol.equals("(")){
                    writer.write(type.wrap(symbol)+"\n");
                    compileParameterList();
                    writer.write(type.wrap(")")+"\n");
                }
                else if(symbol.equals("{")){
                    compileSubroutineBody(symbol);
                    break;
                }
            }
        }
        writer.write("</subroutineDec>\n");
    }

    private void compileSubroutineBody(String symbol) throws IOException {
        writer.write("<subroutineBody>\n");
        writer.write(TokenType.SYMBOL.wrap(symbol)+"\n");

        while(tokenizer.advance()){
            TokenType type = tokenizer.tokenType();
            if(type.equals(TokenType.KEYWORD)){
                Keyword keyword = tokenizer.keyword();
                if(keyword.equals(Keyword.VAR)){
                    compileVarDec(keyword);
                }
                else {
                    compileStatements(keyword);
                    break;
                }
            }
            else {
                compileStatements(null);
                break;
            }
        }

        writer.write(TokenType.SYMBOL.wrap("}")+"\n");
        writer.write("</subroutineBody>\n");
    }

    private void compileParameterList() throws IOException {
        writer.write("<parameterList>\n");

        while(tokenizer.advance()){
            TokenType type = tokenizer.tokenType();
            String token = tokenizer.token();
            if(token.equals(")") ) break;
            writer.write(type.wrap(token)+"\n");
        }

        writer.write("</parameterList>\n");
    }

    private void compileClassVarDec(Keyword keyword) throws IOException {
        compileTemplateVarDec(keyword,"classVarDec");
    }

    private void compileVarDec(Keyword keyword) throws IOException {
        compileTemplateVarDec(keyword,"varDec");
    }

    private void compileTemplateVarDec(Keyword keyword,String name) throws IOException {
        writer.write("<"+name+">\n");

        writer.write(TokenType.KEYWORD.wrap(keyword)+"\n");

        while(tokenizer.advance()){
            TokenType type = tokenizer.tokenType();
            String token = tokenizer.token();
            if(token.equals(";") ){
                writer.write(type.wrap(token)+"\n");
                break;
            }
            writer.write(type.wrap(token)+"\n");
        }

        writer.write("</"+name+">\n");
    }

    private void compileStatements(Keyword keyword) throws IOException {

        writer.write("<statements>\n");

        if(keyword==null){
            writer.write("</statements>\n");
            return;
        }

        compileStatement(keyword);

        String next;
        tokenizer.advance();
        while(true){
            TokenType type = tokenizer.tokenType();
            String token = tokenizer.token();

            if(!type.equals(TokenType.KEYWORD) ) break;
            else{
                next = compileStatement(tokenizer.keyword());
                if(next==null)  tokenizer.advance();
                else if(next.equals("}")) break;
            }
        }

        writer.write("</statements>\n");
    }

    private String compileStatement(Keyword keyword) throws IOException {
        if(keyword.equals(Keyword.LET))   compileLet();
        if(keyword.equals(Keyword.WHILE))   compileWhile();
        if(keyword.equals(Keyword.DO))   compileDo();
        if(keyword.equals(Keyword.IF)) return compileIf();
        if(keyword.equals(Keyword.RETURN))   compileReturn();
        return null;
    }

    private void compileLet() throws IOException {
        writer.write("<letStatement>\n");
        writer.write(TokenType.KEYWORD.wrap(Keyword.LET)+"\n");

        while(tokenizer.advance()){
            TokenType type = tokenizer.tokenType();
            String token = tokenizer.token();

            if(token.equals("[")){
                String symbol = compileExpression(null, null);
                writer.write(TokenType.SYMBOL.wrap(symbol)+"\n");
                if(symbol.equals(";")){
                    break;
                }
            }
        }
        writer.write("</letStatement,>\n");
    }

    private void compileWhile() throws IOException {
        writer.write("<whileStatement>\n");
        writer.write(TokenType.KEYWORD.wrap("while")+"\n");
        String closer;
        TokenType type;
        String token;

        while(tokenizer.advance()){
            type = tokenizer.tokenType();
            token = tokenizer.token();
            if(token.equals("(") ){
                writer.write(type.wrap(token)+"\n");
                closer = compileExpression(null,null);
                writer.write(type.wrap(closer)+"\n");
            }
            if(token.equals("{")){
                writer.write(type.wrap(token)+"\n");
            }
            if(type.equals(TokenType.KEYWORD)){
                compileStatements(tokenizer.keyword());
                writer.write(type.wrap("}")+"\n");
                break;
            }
        }
        writer.write("</whileStatement>\n");
    }

    private void compileDo() throws IOException {
        writer.write("<doStatement>\n");
        writer.write(TokenType.KEYWORD.wrap(Keyword.DO)+"\n");

        while(tokenizer.advance()){
            TokenType type = tokenizer.tokenType();
            String token = tokenizer.token();
            writer.write(type.wrap(token)+"\n");

            if(type.equals(TokenType.SYMBOL)){
                if(token.equals("(")){
                    compileExpressionList();
                    writer.write(TokenType.SYMBOL.wrap(")")+"\n");
                }
                else if(token.equals(";")){
                    break;
                }
            }
        }
        writer.write("</doStatement,>\n");
    }

    private void compileExpressionList() throws IOException {
        writer.write("<expressionList>\n");

        if(tokenizer.advance()){
            TokenType type = tokenizer.tokenType();
            String token = tokenizer.token();
            
            if(!token.equals(")") ){
                String symbol = compileExpression(type, token);
                while(symbol.equals(",")){
                    writer.write(TokenType.SYMBOL.wrap(symbol)+"\n");
                    symbol = compileExpression(null, null);
                }
            }
        }
        writer.write("</expressionList>\n");
    }

    private String compileIf() throws IOException {
        writer.write("<ifStatement>\n");
        writer.write(TokenType.KEYWORD.wrap("if")+"\n");
        String closer;
        TokenType type;
        String token;

        while(tokenizer.advance()){
            type = tokenizer.tokenType();
            token = tokenizer.token();
            if(token.equals("(") ){
                writer.write(type.wrap(token)+"\n");
                closer = compileExpression(null,null);
                writer.write(type.wrap(closer)+"\n");
            }
            if(token.equals("{")){
                writer.write(type.wrap(token)+"\n");
            }
            if(type.equals(TokenType.KEYWORD)){
                compileStatements(tokenizer.keyword());
                writer.write(type.wrap("}")+"\n");
                break;
            }
        }
        if(tokenizer.advance()) {
            type = tokenizer.tokenType();
            token = tokenizer.token();
            if(token.equals("else")){
                while(tokenizer.advance()) {
                    type = tokenizer.tokenType();
                    token = tokenizer.token();
                    if(type.equals(TokenType.KEYWORD)){
                        compileStatements(tokenizer.keyword());
                        writer.write(type.wrap("}")+"\n");
                         break;
                     }
                    else writer.write(type.wrap("}")+"\n");
                }
            }
            else return token;
        }
        writer.write("</ifStatement>\n");
        return null;
    }

    private void compileReturn() throws IOException {
        writer.write("<returnStatement>\n");
        writer.write(TokenType.KEYWORD.wrap(Keyword.RETURN)+"\n");
        compileExpression(null, null);   // could be no expression!
        writer.write(TokenType.SYMBOL.wrap(";")+"\n");
        writer.write("</returnStatement,>\n");
    }

    private String compileExpression(TokenType type, String token) {
        //To change body of created methods use File | Settings | File Templates.
        return null;
    }
}


