parser grammar WACCParser;

options {
  tokenVocab=WACCLexer;
}

prog: BEGIN func* main END EOF;
main: statList;
func: type funcName=IDENT OPEN_PARENTHESIS (paramList)? CLOSE_PARENTHESIS IS statList END;
paramList: param (COMMA param)*;
param: type name=IDENT;
statList: stat (SEMICOLON stat)*;
stat: SKIP                                                       # SkipStat
      | type varName=IDENT EQUALS assignRHS                      # InitStat
      | assignLHS EQUALS assignRHS                               # AssignStat
      | READ assignLHS                                           # ReadStat
      | FREE expr                                                # FreeStat
      | EXIT expr                                                # ExitStat
      | RETURN expr                                              # ReturnStat
      | PRINT expr                                               # PrintStat
      | PRINTLN expr                                             # PrintStat
      | IF expr THEN thenStat=statList ELSE elseStat=statList FI # IfStat
      | WHILE expr DO statList DONE                              # WhileStat
      | BEGIN statList END                                       # BeginStat
      ;
assignLHS: IDENT | arrayElem | pairElem;
assignRHS: expr
      | arrayLitr
      | NEW_PAIR OPEN_PARENTHESIS first=expr COMMA second=expr CLOSE_PARENTHESIS
      | pairElem
      | CALL funcName=IDENT OPEN_PARENTHESIS (argList)? CLOSE_PARENTHESIS
      ;
argList: expr (COMMA expr)*;
type: nonArrayType | arrayType;
nonArrayType: baseType | pairType;
baseType: INT_T | BOOL_T | CHAR_T | STRING_T;
arrayType: nonArrayType (OPEN_BRACKET CLOSE_BRACKET)+;
pairType: PAIR OPEN_PARENTHESIS firstType=pairElemType COMMA secondType=pairElemType CLOSE_PARENTHESIS;
pairElemType: baseType | arrayType | PAIR;
expr: (CHR)? (sign)? INTEGER                                  # intExpr
      | (NOT)? boolLitr                                       # boolExpr
      | (ORD)? CHARACTER                                      # charExpr
      | STRING                                                # stringExpr
      | pairLitr                                              # pairExpr
      | (unaryOper)? IDENT                                    # identExpr
      | (LEN)? arrayElem                                      # arrayExpr
      | e1=expr binaryOper e2=expr                            # binaryExpr
      | (unaryOper)? OPEN_PARENTHESIS expr CLOSE_PARENTHESIS  # bracketedExpr
      ;
sign: MINUS | PLUS;
unaryOper: NOT | MINUS | LEN | ORD | CHR;
binaryOper: arithmeticOper | comparisonOper | logicalOper;
arithmeticOper: MUL | DIV | MOD | PLUS | MINUS;
comparisonOper: GT | GTE | LT | LTE | EQ | NE;
logicalOper: AND | OR;
pairElem: FST expr | SND expr;
arrayElem: varName=IDENT (OPEN_BRACKET expr CLOSE_BRACKET)+;
boolLitr: TRUE | FALSE;
arrayLitr: OPEN_BRACKET (expr (COMMA expr)*)? CLOSE_BRACKET;
pairLitr: NULL;
