lexer grammar WACCLexer;

// Symbols
OPEN_PARENTHESIS: '(';
CLOSE_PARENTHESIS: ')';
OPEN_BRACKET: '[';
CLOSE_BRACKET: ']';
COMMA: ',';
SEMICOLON: ';';
EQUALS: '=';
UNDERSCORE: '_';
SINGLE_QUOTE: '\'';
DOUBLE_QUOTE: '"';

// Types
INT: 'int';
BOOL: 'bool';
CHAR: 'char';
STRING: 'string';

// Control Flow
BEGIN: 'begin';
END: 'end';
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
NEGATE: '-'; // TODO: check these
LEN: 'len';
ORD: 'ord';
CHR: 'chr';

// Binary Operators
MUL: '*';
DIV: '/';
MOD: '%';
PLUS: '+'; // TODO: check these
MINUS: '-'; // TODO: check these
GT: '>';
GTE: '>=';
LT: '<';
LTE: '<=';
EQ: '==';
NE: '!=';
AND: '&&';
OR: '||';
