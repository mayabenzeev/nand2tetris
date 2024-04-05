import re


class JackTokenizer:
    KEYWORD = 'keyword'
    SYMBOL = 'symbol'
    IDENTIFIER = 'identifier'
    INT_CONST = 'integerConstant'
    STRING_CONST = 'stringConstant'
    KEYWORDS = ["class", "constructor", "function", "method", "field", "static", "var", "int", "char",
                "boolean", "void", "true", "false", "null", "this", "do", "if", "else", "while", "return", "let"]
    SYMBOLS = ['{', '}', '(', ')', '[', ']', '.', ',', ';', '+', '-', '*', '/', '&', '|', '<', '>', '=', '~',]
    SPECIAL_SYMBOLS = {'<': '&lt;', '>': '&gt;', '"': '&qu;', '&': '&amp;'}

    def __init__(self, file):
        """
        Initializes a JackTokenizer instance.

        Parameters:
        - file (str): The path to the Jack source file.
        """
        self.currToken = None
        self.file = open(file, "r")
        self.line = None
        self.hasMoreLines = True
        self.currTokenType = None

    def advance(self):
        """
        Reads the next token from the input file and updates the current token.
        Handles keywords, symbols, integer constants, string constants, and identifiers.
        """
        # if curr line is empty/null then read another line from input
        while (not self.line) and self.hasMoreLines:
            self.line = self.file.readline()
            originLine = self.line # save the full line
            if not self.line:
                self.hasMoreLines = False
            else:
                self.line = self.stripLine(self.line, originLine)
        # if line isn't empty, read first token from it
        if self.hasMoreLines:
            isKeyword = [keyword for keyword in JackTokenizer.KEYWORDS if self.line.startswith(keyword)]
            isSymbol = [symbol for symbol in JackTokenizer.SYMBOLS if self.line.startswith(symbol)]

            # get the current token
            if isKeyword:  # if the current token is keyword
                self.currToken = isKeyword[0]
                self.currTokenType = JackTokenizer.KEYWORD

            elif isSymbol:  # if the current token is a symbol
                # self.currToken = isSymbol[0] if isSymbol[0] not in JackTokenizer.SPECIAL_SYMBOLS else (
                #     JackTokenizer.SPECIAL_SYMBOLS)[isSymbol[0]]
                self.currToken = isSymbol[0]
                self.currTokenType = JackTokenizer.SYMBOL

            elif re.match(r'(\d+)(.*)', self.line):  # if the current token is an Integer constant
                self.currToken = re.match(r'(\d+)(.*)', self.line).group(1)
                self.currTokenType = JackTokenizer.INT_CONST

            elif self.line.startswith('"'):  # if the current token is a String constant
                self.currToken = self.line.split('"')[1]
                self.currTokenType = JackTokenizer.STRING_CONST

            elif re.compile(r'^[a-zA-Z_]+').match(self.line):  # if the current token is an identifier
                self.currToken = re.compile(r'^[a-zA-Z_]+').search(self.line).group().strip()
                self.currTokenType = JackTokenizer.IDENTIFIER

            else:
                raise Exception("Input has an invalid token!")

            startIndex = len(self.currToken) + 2 if self.currTokenType == JackTokenizer.STRING_CONST else len(
                self.currToken)
            self.currToken = self.currToken if self.currToken not in JackTokenizer.SPECIAL_SYMBOLS else (
                JackTokenizer.SPECIAL_SYMBOLS)[self.currToken]
            self.line = self.line[startIndex:].strip()

    def tokenType(self):
        """
        Returns the type of the current token as a constant.

        Returns:
        - str: The token type (e.g., 'keyword', 'symbol', 'identifier', 'integerConstant', 'stringConstant').
        """
        return self.currTokenType


    def keyword(self):
        """
        Returns the keyword which is the current token.

        Returns:
        - str: The keyword.
        """
        return self.currToken

    def symbol(self):
        """
        Returns the character which is the current token.

        Returns:
        - str: The symbol.
        """
        return self.currToken

    def identifier(self):
        """
        Returns the string which is the current token.

        Returns:
        - str: The identifier.
        """
        return self.currToken

    def intVal(self):
        """
        Returns the integer value of the current token.

        Returns:
        - int: The integer value.
        """
        return int(self.currToken)

    def stringVal(self):
        """
        Returns the string value of the current token.

        Returns:
        - str: The string value.
        """
        return self.currToken

    def stripLine(this, line, originLine):
        """
        Strips comments and whitespace from a line.

        Parameters:
        - line (str): The input line.

        Returns:
        - str: The stripped line.
        """
        if originLine == line:
            for char in ['/**', '*/', '*']:
                if line.strip().startswith(char):
                    line = line.split(char)[0]
                    break
        line = line.split('//')[0]
        return line.strip()

    def close(self):
        """
        Closes the input file.
        """
        ...
        self.file.close()


if __name__ == '__main__':
    tokenizer = JackTokenizer('Prog.jack')
    tokenizer.advance()
    while tokenizer.hasMoreLines:
        tokenType = tokenizer.currTokenType
        print(f'<{tokenType}>', end="")
        print(tokenizer.currToken, end="")
        print(f'</{tokenType}>')
        tokenizer.advance()
