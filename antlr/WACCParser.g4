parser grammar WACCParser;

options {
  tokenVocab=WACCLexer;
}

prog : BEGIN (expr)* END;
expr : term (binaryOper expr)?
     | OPEN_PARENTHESIS expr CLOSE_PARENTHESIS
     ;
term: (unaryOper)? atom;
atom: INTEGER
      | boolLitr
      | CHARACTER
      | STRING
      | pairLitr
      | IDENT
      | arrayLitr;

unaryOper: NOT
      | MINUS
      | LEN
      | ORD
      | CHR
      ;
binaryOper: arithmeticOper | comparisonOper | logicalOper;
arithmeticOper: MUL | DIV | MOD | PLUS | MINUS;
comparisonOper: GT | GTE | LT | LTE | EQ | NE;
logicalOper: AND | OR;
arrayElem: IDENT (OPEN_BRACKET expr CLOSE_BRACKET)+;
boolLitr: TRUE | FALSE;
arrayLitr: OPEN_BRACKET (expr (COMMA expr)*)? CLOSE_BRACKET;
pairLitr: NULL;
