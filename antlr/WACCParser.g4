parser grammar WACCParser;

options {
  tokenVocab=WACCLexer;
}

prog: BEGIN (func)* statList END;
func: type IDENT OPEN_PARENTHESIS (paramList)? CLOSE_PARENTHESIS IS statList END;
paramList: param (COMMA param)*;
param: type IDENT;
statList: stat | stat SEMICOLON statList;
stat: SKIP
      | type IDENT EQUALS assignRHS
      | assignLHS EQUALS assignRHS
      | READ assignLHS
      | FREE expr
      | EXIT expr
      | PRINT expr
      | PRINTLN expr
      | IF expr THEN statList ELSE statList FI
      | WHILE expr DO statList DONE
      | BEGIN statList END
      ;
assignLHS: IDENT | arrayElem | pairElem;
assignRHS: expr
      | arrayLitr
      | NEW_PAIR OPEN_PARENTHESIS expr COMMA expr CLOSE_PARENTHESIS
      | pairElem
      | CALL IDENT OPEN_PARENTHESIS (argList)? CLOSE_PARENTHESIS
      ;
argList: expr (COMMA expr)*;
pairElem: FST expr | SND expr;
type: nonArrayType | arrayType;
nonArrayType: baseType | pairType;
baseType: INT_T | BOOL_T | CHAR_T | STRING_T;
arrayType: nonArrayType (OPEN_BRACKET CLOSE_BRACKET)+;
pairType: PAIR OPEN_PARENTHESIS pairElemType COMMA pairElemType CLOSE_PARENTHESIS;
pairElemType: baseType | arrayType | PAIR;
expr: intLitr
      | boolLitr
      | CHARACTER
      | STRING
      | pairLitr
      | IDENT
      | arrayElem
      | unaryOper expr
      | expr binaryOper expr
      | OPEN_PARENTHESIS expr CLOSE_PARENTHESIS
      ;
unaryOper: NOT
      | MINUS
      | LEN
      | ORD
      | CHR
      ;
binaryOper: MUL
      | DIV
      | MOD
      | PLUS
      | MINUS
      | GT
      | GTE
      | LT
      | LTE
      | EQ
      | NE
      | AND
      | OR
      ;
arrayElem: IDENT (OPEN_BRACKET expr CLOSE_BRACKET)+;
intLitr: (intSign)? INTEGER;
intSign: PLUS | MINUS;
boolLitr: TRUE | FALSE;
arrayLitr: OPEN_BRACKET (expr (COMMA expr)*)? CLOSE_BRACKET;
pairLitr: NULL;
