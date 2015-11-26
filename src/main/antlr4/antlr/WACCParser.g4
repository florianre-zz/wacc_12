parser grammar WACCParser;

options {
  tokenVocab=WACCLexer;
}

prog: BEGIN func* main END EOF;
main: statList;
func: type funcName=ident OPEN_PARENTHESIS (paramList)? CLOSE_PARENTHESIS IS statList END;
paramList: param (COMMA param)*;
param: type name=ident;
statList: stat (SEMICOLON stat)*;
stat: SKIP                                                       # SkipStat
      | type ident EQUALS assignRHS                              # InitStat
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

assignLHS returns [Object returnType]: (ident | arrayElem | pairElem) {Object
returnType = null;};
assignRHS: expr | arrayLitr | newPair | pairElem | call;
newPair: NEW_PAIR OPEN_PARENTHESIS first=expr COMMA second=expr CLOSE_PARENTHESIS;
call: CALL funcName=ident OPEN_PARENTHESIS (argList)? CLOSE_PARENTHESIS;
argList: expr (COMMA expr)*;
type: nonArrayType | arrayType;
nonArrayType: baseType | pairType;
baseType: INT_T | BOOL_T | CHAR_T | STRING_T;
arrayType: nonArrayType (OPEN_BRACKET CLOSE_BRACKET)+;
pairType: PAIR OPEN_PARENTHESIS firstType=pairElemType COMMA secondType=pairElemType CLOSE_PARENTHESIS;
pairElemType: baseType | arrayType | PAIR;
expr returns [Object returnType]: binaryOper {Object returnType = null;};
sign: MINUS | PLUS;
binaryOper: logicalOper;
logicalOper: first=comparisonOper ((AND | OR) otherExprs+=comparisonOper)*
{
  WACCParser.ComparisonOperContext first;
  List<WACCParser.ComparisonOperContext> otherExprs = new ArrayList();
};
comparisonOper: orderingOper | equalityOper;
orderingOper: first=arithmeticOper ((GT | GTE | LT | LTE) second=arithmeticOper)?
{
  WACCParser.ArithmeticOperContext first;
  WACCParser.ArithmeticOperContext second;
};
equalityOper: first=arithmeticOper ((EQ | NE) second=arithmeticOper)?
{
  WACCParser.ArithmeticOperContext first;
  WACCParser.ArithmeticOperContext second;
};
arithmeticOper: first=atom ((MUL | DIV | MOD | PLUS | MINUS) otherExprs+=atom)*
{
  WACCParser.AtomContext first;
  List<WACCParser.AtomContext> otherExprs = new ArrayList();
};
atom: integer | bool | character | string | pairLitr | unaryOper | array;
integer: (CHR)? (sign)? INTEGER;
bool: (NOT)? boolLitr;
character: (ORD)? CHARACTER;
array: (LEN)? arrayElem;
string: STRING;
unaryOper: (NOT | MINUS | LEN | ORD | CHR)? (ident | (OPEN_PARENTHESIS expr CLOSE_PARENTHESIS));
pairElem: (FST | SND) ident;
arrayElem: varName=ident (OPEN_BRACKET expr CLOSE_BRACKET)+;
boolLitr: TRUE | FALSE;
arrayLitr: OPEN_BRACKET (expr (COMMA expr)*)? CLOSE_BRACKET;
pairLitr: NULL;
ident: IDENT;
