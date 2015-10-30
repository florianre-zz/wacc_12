lexer grammar WACCLexer;

// Symbols
OPEN_PARENTHESIS: '(';
CLOSE_PARENTHESIS: ')';
OPEN_BRACKET: '[';
CLOSE_BRACKET: ']';
COMMA: ',';
SEMICOLON: ';';
EQUALS: '=';
fragment UNDERSCORE: '_';
SINGLE_QUOTE: '\'';
DOUBLE_QUOTE: '"';
HASH: '#';
TRUE: 'true';
FALSE: 'false';
NULL: 'null';

// Types
INT_T: 'int';
BOOL_T: 'bool';
CHAR_T: 'char';
STRING_T: 'string';
ARRAY_T: (INT_T | BOOL_T | CHAR_T | STRING_T) (OPEN_BRACKET CLOSE_BRACKET)+;

// Control Flow
BEGIN: 'begin';
END: 'end';
SKIP: 'skip';
IS: 'is';
IF: 'if';
THEN: 'then';
ELSE: 'else';
FI: 'fi';
WHILE: 'while';
DO: 'do';
DONE: 'done';
CALL: 'call';

// Unary Operators
NOT: '!';
LEN: 'len';
ORD: 'ord';
CHR: 'chr';

// Binary Operators
MUL: '*';
DIV: '/';
MOD: '%';
PLUS: '+'; // Also used for POSITIVE
MINUS: '-'; // Also used for NEGATIVE & NEGATE
GT: '>';
GTE: '>=';
LT: '<';
LTE: '<=';
EQ: '==';
NE: '!=';
AND: '&&';
OR: '||';

// Statments
READ: 'read';
FREE: 'free';
RETURN: 'return';
EXIT: 'exit';
PRINT: 'print';
PRINTLN: 'println';
NEW_PAIR: 'newpair';
FST: 'fst';
SND: 'snd';
PAIR: 'pair';

// Words & Numbers
fragment LOWER: [a-z];
fragment UPPER: [A-Z];
fragment DIGIT: [0-9];
INTEGER: DIGIT+;
IDENT: (UNDERSCORE | LOWER | UPPER) (UNDERSCORE | LOWER | UPPER | INTEGER)*;
fragment ESCAPED_CHARACTER: '\\' [0btrnf"\'\\];
fragment LEGAL_CHARACTER: ~[\\\'"] | ESCAPED_CHARACTER;
CHARACTER: SINGLE_QUOTE LEGAL_CHARACTER SINGLE_QUOTE;
STRING: DOUBLE_QUOTE LEGAL_CHARACTER* DOUBLE_QUOTE;
COMMENT: HASH (~[\r\n])* '\r'? '\n' -> skip;
WS: [ \t\r\n]+ -> skip;
