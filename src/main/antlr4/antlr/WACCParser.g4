parser grammar WACCParser;

options {
  tokenVocab=WACCLexer;
}

prog: BEGIN func* statList END EOF;
func: type IDENT OPEN_PARENTHESIS (paramList)? CLOSE_PARENTHESIS IS statList END;
paramList: param (COMMA param)*;
param: type IDENT;
statList: stat (SEMICOLON stat)*;
stat: SKIP # SkipStat
      | type IDENT EQUALS assignRHS            # InitStat
      | assignLHS EQUALS assignRHS             # AssignStat
      | READ assignLHS                         # ReadStat
      | FREE expr                              # FreeStat
      | EXIT expr                              # ExitStat
      | RETURN expr                            # ReturnStat
      | PRINT expr                             # PrintStat
      | PRINTLN expr                           # PrintStat
      | IF expr THEN statList ELSE statList FI # IfStat
      | WHILE expr DO statList DONE            # WhileStat
      | BEGIN statList END                     # BeginStat
      ;
assignLHS: IDENT | arrayElem | pairElem;
assignRHS: expr
      | arrayLitr
      | NEW_PAIR OPEN_PARENTHESIS expr COMMA expr CLOSE_PARENTHESIS
      | pairElem
      | CALL IDENT OPEN_PARENTHESIS (argList)? CLOSE_PARENTHESIS
      ;
argList: expr (COMMA expr)*;
type: nonArrayType | arrayType;
nonArrayType: baseType | pairType;
baseType: INT_T | BOOL_T | CHAR_T | STRING_T;
arrayType: nonArrayType (OPEN_BRACKET CLOSE_BRACKET)+;
pairType: PAIR OPEN_PARENTHESIS pairElemType COMMA pairElemType CLOSE_PARENTHESIS;
pairElemType: baseType | arrayType | PAIR;
expr: (CHR)? (sign)? INTEGER
      | (NOT)? boolLitr
      | (ORD)? CHARACTER
      | STRING //Length?
      | pairLitr
      | (unaryOper)? IDENT
      | (LEN)? arrayElem
      | expr binaryOper expr
      | (unaryOper)? OPEN_PARENTHESIS expr CLOSE_PARENTHESIS
      ;
sign: MINUS | PLUS;
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
pairElem: FST expr | SND expr;
arrayElem: IDENT (OPEN_BRACKET expr CLOSE_BRACKET)+;
boolLitr: TRUE | FALSE;
arrayLitr: OPEN_BRACKET (expr (COMMA expr)*)? CLOSE_BRACKET;
pairLitr: NULL;
