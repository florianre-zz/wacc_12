parser grammar WACCParser;

@header {
  import bindings.Type;
}

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
      | IF expr THEN thenStat=statList (ELSE elseStat=statList)? FI # IfStat
      | WHILE expr DO statList DONE                              # WhileStat
      | BEGIN statList END                                       # BeginStat
      ;

assignLHS returns [Type returnType]: (ident | arrayElem | pairElem) {Type
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
expr returns [Type returnType]: binaryOper {Type returnType = null;};
sign: MINUS | PLUS;
binaryOper: logicalOper;
logicalOper: first=comparisonOper (ops+=(AND | OR) otherExprs+=comparisonOper)*
{
  WACCParser.ComparisonOperContext first;
  List<WACCParser.ComparisonOperContext> otherExprs = new ArrayList();
  List<TerminalNode> ops = new ArrayList();
};
comparisonOper: orderingOper | equalityOper;
orderingOper: first=addOper ((GT | GE | LT | LE) second=addOper)?
{
  WACCParser.AddOperContext first;
  WACCParser.AddOperContext second;
};
equalityOper: first=addOper ((EQ | NE) second=addOper)?
{
  WACCParser.AddOperContext first;
  WACCParser.AddOperContext second;
};
addOper: first=multOper (ops+=(PLUS | MINUS) otherExprs+=multOper)*
{
  WACCParser.MultOperContext first;
  List<WACCParser.MultOperContext> otherExprs = new ArrayList();
  List<TerminalNode> ops = new ArrayList();
};
multOper: first=atom (ops+=(MUL | DIV | MOD) otherExprs+=atom)*
{
  WACCParser.AtomContext first;
  List<WACCParser.AtomContext> otherExprs = new ArrayList();
  List<TerminalNode> ops = new ArrayList();
};
atom: integer | bool | character | string | pairLitr | unaryOper | array;
integer: (CHR)? (sign)? INTEGER;
bool: (NOT)? boolLitr;
character: (ORD)? CHARACTER;
array: (LEN)? arrayElem;
string: (LEN)? STRING;
unaryOper: (NOT | MINUS | LEN | ORD | CHR)? (ident | (OPEN_PARENTHESIS expr CLOSE_PARENTHESIS));
pairElem: (FST | SND) ident;
arrayElem returns [Type returnType]: varName=ident (OPEN_BRACKET expr
CLOSE_BRACKET)+ {Type returnType = null;};
boolLitr: TRUE | FALSE;
arrayLitr: OPEN_BRACKET (expr (COMMA expr)*)? CLOSE_BRACKET;
pairLitr: NULL;
ident: IDENT;
