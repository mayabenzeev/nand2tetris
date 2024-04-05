from JackTokenizer import *


class CompilationEngine:

    def __init__(self, input_file, output_file):
        """
        Creates a new CompilationEngine with the given input and output.

        Parameters:
        - input_file (str): The path to the input file.
        - output_file (str): The path to the output file.
        """
        self.jackFile = JackTokenizer(input_file)
        self.jackFile.advance()
        self.outFile = open(output_file, "w")
        self.indention = "  "
        self.tab = 0

    def compileClass(self):
        """
        Compiles a complete class.
        Writes the XML representation of the class structure to the output file.
        """
        # class
        self.outFile.write(f"{self.indention * self.tab}<class>\n")
        self.tab += 1
        # class:keyword
        self.writeXML(self.jackFile.keyword())
        self.jackFile.advance()
        # class_name:identifier
        self.writeXML(self.jackFile.identifier())
        self.jackFile.advance()
        # {:symbol
        self.writeXML(self.jackFile.symbol())
        self.jackFile.advance()
        currentKeyword = self.jackFile.keyword()
        while currentKeyword in ['static', 'field']:
            self.compileClassVarDec()
            currentKeyword = self.jackFile.keyword()
        while currentKeyword in ['constructor', 'function', 'method']:
            self.compileSubroutineDec()
            currentKeyword = self.jackFile.keyword()
        # }:symbol
        self.writeXML(self.jackFile.symbol())
        self.jackFile.advance()
        # class
        self.tab -= 1
        self.outFile.write(f"{self.indention * self.tab}</class>\n")
        self.jackFile.close()

    def compileClassVarDec(self):
        """
        Compiles a class variable declaration.
        Writes the XML representation of the class variable declaration to the output file.
        """
        # classVarDec
        self.outFile.write(f"{self.indention * self.tab}<classVarDec>\n")
        self.tab += 1
        # ('static'|'field'):keyword
        self.writeXML(self.jackFile.keyword())
        self.jackFile.advance()
        # type :keyword
        self.writeXML(self.jackFile.keyword())
        self.jackFile.advance()
        # varName:identifier
        self.writeXML(self.jackFile.identifier())
        self.jackFile.advance()
        currentSymbol = self.jackFile.symbol()
        while currentSymbol == ',':
            # ("," varName)*:symbol
            self.writeXML(currentSymbol)
            self.jackFile.advance()
            # varName:identifier
            self.writeXML(self.jackFile.identifier())
            self.jackFile.advance()
            currentSymbol = self.jackFile.symbol()
        self.writeXML(currentSymbol)
        self.jackFile.advance()
        self.tab -= 1
        # classVarDec
        self.outFile.write(f"{self.indention * self.tab}</classVarDec>\n")

    def compileSubroutineDec(self):
        """
        Compiles a subroutine declaration.
        Writes the XML representation of the subroutine declaration to the output file.
        """
        # subroutineDec
        self.outFile.write(f"{self.indention * self.tab}<subroutineDec>\n")
        self.tab += 1
        # ('constructor'|'function'|'method'):keyword
        self.writeXML(self.jackFile.keyword())
        self.jackFile.advance()
        # ('void'|type):keyword
        self.writeXML(self.jackFile.keyword())
        self.jackFile.advance()
        # subroutineName:identifier
        self.writeXML(self.jackFile.identifier())
        self.jackFile.advance()
        # (:symbol
        self.writeXML(self.jackFile.symbol())
        self.jackFile.advance()
        self.compileParameterList()
        # ):symbol
        self.writeXML(self.jackFile.symbol())
        self.jackFile.advance()
        self.compileSubroutineBody()
        # subroutineDec
        self.tab -= 1
        self.outFile.write(f"{self.indention * self.tab}</subroutineDec>\n")

    def compileParameterList(self):
        """
        Compiles a parameter list.
        Writes the XML representation of the parameter list to the output file.
        """
        # parameterList
        self.outFile.write(f"{self.indention * self.tab}<parameterList>\n")
        currType = self.jackFile.tokenType()
        self.tab += 1
        while currType == self.jackFile.KEYWORD:
            # type :keyword
            self.writeXML(self.jackFile.keyword())
            self.jackFile.advance()
            # varName:identifier
            self.writeXML(self.jackFile.identifier())
            self.jackFile.advance()
            if self.jackFile.symbol() == ')':
                break
            # ,:symbol
            self.writeXML(self.jackFile.symbol())
            self.jackFile.advance()
        # parameterList
        self.tab -= 1
        self.outFile.write(f"{self.indention * self.tab}</parameterList>\n")

    def compileSubroutineBody(self):
        """
        Compiles a subroutine body.
        Writes the XML representation of the subroutine body to the output file.
        """
        # parameterList
        self.outFile.write(f"{self.indention * self.tab}<subroutineBody>\n")
        self.tab += 1
        # {:symbol
        self.writeXML(self.jackFile.symbol())
        self.jackFile.advance()
        while self.jackFile.keyword() == 'var':
            # varDec
            self.compileVarDec()

        if self.jackFile.keyword() in ['let', 'if', 'while', 'do', 'return']:
            # statement
            self.compileStatements()
        # }:symbol
        self.writeXML(self.jackFile.symbol())
        self.jackFile.advance()
        # parameterList
        self.tab -= 1
        self.outFile.write(f"{self.indention * self.tab}</subroutineBody>\n")

    def compileVarDec(self):
        """
        Compiles a variable declaration.
        Writes the XML representation of the variable declaration to the output file.
        """
        # varDec
        self.outFile.write(f"{self.indention * self.tab}<varDec>\n")
        self.tab += 1
        # var:keyword
        self.writeXML(self.jackFile.keyword())
        self.jackFile.advance()
        # type :keyword
        self.writeXML(self.jackFile.keyword())
        self.jackFile.advance()
        # varName:identifier
        self.writeXML(self.jackFile.identifier())
        self.jackFile.advance()
        currentSymbol = self.jackFile.symbol()
        while currentSymbol == ',':
            # ("," varName)*:symbol
            self.writeXML(currentSymbol)
            self.jackFile.advance()
            # varName:identifier
            self.writeXML(self.jackFile.identifier())
            self.jackFile.advance()
            currentSymbol = self.jackFile.symbol()
        # ";":symbol
        self.writeXML(currentSymbol)
        self.jackFile.advance()
        self.tab -= 1
        # varDec
        self.outFile.write(f"{self.indention * self.tab}</varDec>\n")

    def compileLet(self):
        """
        Compiles a 'let' statement.
        Writes the XML representation of the 'let' statement to the output file.
        """
        # let
        self.outFile.write(f"{self.indention * self.tab}<letStatement>\n")
        self.tab += 1
        # let:keyword
        self.writeXML(self.jackFile.keyword())
        self.jackFile.advance()
        # let:identifier
        self.writeXML(self.jackFile.identifier())
        self.jackFile.advance()
        # check if let x[expression]
        if self.jackFile.symbol() == '[':
            # [:symbol
            self.writeXML(self.jackFile.symbol())
            self.jackFile.advance()
            # expression
            self.compileExpression()
            # ]:symbol
            self.writeXML(self.jackFile.symbol())
            self.jackFile.advance()
        # "=":symbol
        self.writeXML(self.jackFile.symbol())
        self.jackFile.advance()
        # expression
        self.compileExpression()
        # ";":symbol
        self.writeXML(self.jackFile.symbol())
        self.jackFile.advance()
        # let
        self.tab -= 1
        self.outFile.write(f"{self.indention * self.tab}</letStatement>\n")

    def compileIf(self):
        """
        Compiles an 'if' statement.
        Writes the XML representation of the 'if' statement to the output file.
        """
        # if
        self.outFile.write(f"{self.indention * self.tab}<ifStatement>\n")
        self.tab += 1
        # if:keyword
        self.writeXML(self.jackFile.keyword())
        self.jackFile.advance()
        # (:symbol
        self.writeXML(self.jackFile.symbol())
        self.jackFile.advance()
        # expression
        self.compileExpression()
        # ):symbol
        self.writeXML(self.jackFile.symbol())
        self.jackFile.advance()
        # {:symbol
        self.writeXML(self.jackFile.symbol())
        self.jackFile.advance()
        # statements
        self.compileStatements()
        # }:symbol
        self.writeXML(self.jackFile.symbol())
        self.jackFile.advance()
        if self.jackFile.keyword() == 'else':
            # else:keyword
            self.writeXML(self.jackFile.keyword())
            self.jackFile.advance()
            # {:symbol
            self.writeXML(self.jackFile.symbol())
            self.jackFile.advance()
            # statements
            self.compileStatements()
            # }:symbol
            self.writeXML(self.jackFile.symbol())
            self.jackFile.advance()
        # if
        self.tab -= 1
        self.outFile.write(f"{self.indention * self.tab}</ifStatement>\n")

    def compileWhile(self):
        """
        Compiles a 'while' statement.
        Writes the XML representation of the 'while' statement to the output file.
        """
        # while
        self.outFile.write(f"{self.indention * self.tab}<whileStatement>\n")
        self.tab += 1
        # while:keyword
        self.writeXML(self.jackFile.keyword())
        self.jackFile.advance()
        # (:symbol
        self.writeXML(self.jackFile.symbol())
        self.jackFile.advance()
        # expression
        self.compileExpression()
        # ):symbol
        self.writeXML(self.jackFile.symbol())
        self.jackFile.advance()
        # {:symbol
        self.writeXML(self.jackFile.symbol())
        self.jackFile.advance()
        # statements
        self.compileStatements()
        # }:symbol
        self.writeXML(self.jackFile.symbol())
        self.jackFile.advance()
        # while
        self.tab -= 1
        self.outFile.write(f"{self.indention * self.tab}</whileStatement>\n")

    def compileDo(self):
        """
        Compiles a 'do' statement.
        Writes the XML representation of the 'do' statement to the output file.
        """
        # do
        self.outFile.write(f"{self.indention * self.tab}<doStatement>\n")
        self.tab += 1
        # do:keyword
        self.writeXML(self.jackFile.keyword())
        self.jackFile.advance()
        self.compileSubroutineCall()
        # ";":symbol
        self.writeXML(self.jackFile.symbol())
        self.jackFile.advance()
        # do
        self.tab -= 1
        self.outFile.write(f"{self.indention * self.tab}</doStatement>\n")

    def compileReturn(self):
        """
        Compiles a 'return' statement.
        Writes the XML representation of the 'return' statement to the output file.
        """
        # return
        self.outFile.write(f"{self.indention * self.tab}<returnStatement>\n")
        self.tab += 1
        # return:keyword
        self.writeXML(self.jackFile.keyword())
        self.jackFile.advance()
        # expression?
        if self.jackFile.tokenType() != self.jackFile.SYMBOL:
            self.compileExpression()
        # ";symbol
        self.writeXML(self.jackFile.symbol())
        self.jackFile.advance()
        # return
        self.tab -= 1
        self.outFile.write(f"{self.indention * self.tab}</returnStatement>\n")

    def compileStatements(self):
        """
        Compiles a series of statements.
        Writes the XML representation of the statements to the output file.
        """
        # statements
        self.outFile.write(f"{self.indention * self.tab}<statements>\n")
        self.tab += 1
        statmentType = self.jackFile.keyword()
        while statmentType in ['let', 'if', 'do', 'while', 'return']:
            if statmentType == 'if':
                self.compileIf()
            elif statmentType == 'let':
                self.compileLet()
            elif statmentType == 'do':
                self.compileDo()
            elif statmentType == 'while':
                self.compileWhile()
            elif statmentType == 'return':
                self.compileReturn()
            statmentType = self.jackFile.keyword()
        # statements
        self.tab -= 1
        self.outFile.write(f"{self.indention * self.tab}</statements>\n")

    def compileExpression(self):
        """
        Compiles an expression.
        Writes the XML representation of the expression to the output file.
        """
        # expression
        self.outFile.write(f"{self.indention * self.tab}<expression>\n")
        self.tab += 1
        # term
        self.compileTerm()
        # term (op term)*
        while (self.jackFile.tokenType() == self.jackFile.SYMBOL and self.jackFile.symbol() in
               ['+', '-', '*', '/', '&amp;', '|', '&lt;', '&gt;', '=']):
            self.writeXML(self.jackFile.symbol())
            self.jackFile.advance()
            # term
            self.compileTerm()
        # expression
        self.tab -= 1
        self.outFile.write(f"{self.indention * self.tab}</expression>\n")

    def compileTerm(self):
        """
        Compiles a term.
        Writes the XML representation of the term to the output file.
        """
        # term
        self.outFile.write(f"{self.indention * self.tab}<term>\n")
        self.tab += 1
        # search by token type
        tokenType = self.jackFile.tokenType()
        # intConstant
        if tokenType == self.jackFile.INT_CONST:
            self.writeXML(self.jackFile.intVal())
            self.jackFile.advance()
        # stringConstant
        elif tokenType == self.jackFile.STRING_CONST:
            self.writeXML(self.jackFile.stringVal())
            self.jackFile.advance()
        # keywordConstant
        elif tokenType == self.jackFile.KEYWORD and self.jackFile.keyword() in ['true', 'false', 'null', 'this']:
            self.writeXML(self.jackFile.keyword())
            self.jackFile.advance()
        # unaryOp term
        elif tokenType == self.jackFile.SYMBOL and self.jackFile.symbol() in '-~':
            # unaryOp
            self.writeXML(self.jackFile.symbol())
            self.jackFile.advance()
            # term
            self.compileTerm()
        # '(' expression ')'
        elif tokenType == self.jackFile.SYMBOL and self.jackFile.symbol() == '(':
            # '(': symbol
            self.writeXML(self.jackFile.symbol())
            self.jackFile.advance()
            # expression
            self.compileExpression()
            # ')':symbol
            self.writeXML(self.jackFile.symbol())
            self.jackFile.advance()
        elif tokenType == self.jackFile.IDENTIFIER:
            LL1Token = self.jackFile.identifier()
            self.writeXML(LL1Token)
            self.jackFile.advance()
            LL2Token = self.jackFile.symbol()
            # varName
            if LL2Token == ')':
                # term
                self.tab -= 1
                self.outFile.write(f"{self.indention * self.tab}</term>\n")
                return
            # varName[expression]
            elif LL2Token == '[':
                # [:symbol
                self.writeXML(LL2Token)
                self.jackFile.advance()
                # expression
                self.compileExpression()
                # ]:symbol
                self.writeXML(self.jackFile.symbol())
                self.jackFile.advance()
            # varName.something(exprssionList)
            elif LL2Token == '.':
                # .:symbol
                self.writeXML(LL2Token)
                self.jackFile.advance()
                # subroutineName:identifier
                self.writeXML(self.jackFile.identifier())
                self.jackFile.advance()
                # (:symbol
                self.writeXML(self.jackFile.symbol())
                self.jackFile.advance()
                # expressionList
                self.compileExpressionList()
                # ):symbol
                self.writeXML(self.jackFile.symbol())
                self.jackFile.advance()
            # varname(expressionList)
            elif LL2Token == '(':
                # (:symbol
                self.writeXML(LL2Token)
                self.jackFile.advance()
                # expressionList
                self.compileExpressionList()
                # ):symbol
                self.writeXML(self.jackFile.symbol())
                self.jackFile.advance()
        # term
        self.tab -= 1
        self.outFile.write(f"{self.indention * self.tab}</term>\n")

    def compileExpressionList(self):
        """
        Compiles an expression list.
        Writes the XML representation of the expression list to the output file.
        """
        # expressionList
        self.outFile.write(f"{self.indention * self.tab}<expressionList>\n")
        self.tab += 1
        while self.jackFile.tokenType() != self.jackFile.SYMBOL or self.jackFile.symbol() != ')':
            self.compileExpression()
            if self.jackFile.tokenType() == self.jackFile.SYMBOL and self.jackFile.symbol() == ',':
                # ,:symbol
                self.writeXML(self.jackFile.symbol())
                self.jackFile.advance()
        # expressionList
        self.tab -= 1
        self.outFile.write(f"{self.indention * self.tab}</expressionList>\n")

    def compileSubroutineCall(self):
        """
        Compiles a subroutine call.
        Writes the XML representation of the subroutine call to the output file.
        """
        # subroutineName|(className|varName)
        LL1Token = self.jackFile.identifier()
        self.writeXML(LL1Token)
        self.jackFile.advance()
        LL2Token = self.jackFile.symbol()
        if LL2Token == '.':
            # .:symbol
            self.writeXML(LL2Token)
            self.jackFile.advance()
            # subroutineName:identifier
            self.writeXML(self.jackFile.identifier())
            self.jackFile.advance()
        # (:symbol
        self.writeXML(self.jackFile.symbol())
        self.jackFile.advance()
        # expressionList
        self.compileExpressionList()
        # ):symbol
        self.writeXML(self.jackFile.symbol())
        self.jackFile.advance()

    def writeXML(self, str_cmd):
        """
        Writes an XML representation of a given command to the output file.
        Parameters:
            - str_cmd (str): The command to be written.
        """
        self.outFile.write(
            f"{self.indention * self.tab}<{self.jackFile.tokenType()}> {str_cmd} </{self.jackFile.tokenType()}>\n")
