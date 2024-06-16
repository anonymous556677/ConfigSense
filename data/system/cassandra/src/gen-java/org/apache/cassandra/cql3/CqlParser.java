// $ANTLR 3.5.2 /Users/wang/Documents/project/configuration_code_understanding/code3/data/system/cassandra/src/antlr/Cql.g 2024-04-10 10:28:18

    package org.apache.cassandra.cql3;

    import java.util.Collections;
    import java.util.EnumSet;
    import java.util.HashSet;
    import java.util.LinkedHashMap;
    import java.util.List;
    import java.util.Map;
    import java.util.Set;

    import org.apache.cassandra.auth.*;
    import org.apache.cassandra.cql3.conditions.*;
    import org.apache.cassandra.cql3.functions.*;
    import org.apache.cassandra.cql3.restrictions.CustomIndexExpression;
    import org.apache.cassandra.cql3.selection.*;
    import org.apache.cassandra.cql3.statements.*;
    import org.apache.cassandra.cql3.statements.schema.*;
    import org.apache.cassandra.exceptions.ConfigurationException;
    import org.apache.cassandra.exceptions.InvalidRequestException;
    import org.apache.cassandra.exceptions.SyntaxException;
    import org.apache.cassandra.schema.ColumnMetadata;
    import org.apache.cassandra.utils.Pair;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings("all")
public class CqlParser extends Parser {
	public static final String[] tokenNames = new String[] {
		"<invalid>", "<EOR>", "<DOWN>", "<UP>", "A", "B", "BOOLEAN", "C", "COMMENT", 
		"D", "DIGIT", "DURATION", "DURATION_UNIT", "E", "EMPTY_QUOTED_NAME", "EXPONENT", 
		"F", "FLOAT", "G", "H", "HEX", "HEXNUMBER", "I", "IDENT", "INTEGER", "J", 
		"K", "K_ACCESS", "K_ADD", "K_AGGREGATE", "K_AGGREGATES", "K_ALL", "K_ALLOW", 
		"K_ALTER", "K_AND", "K_APPLY", "K_AS", "K_ASC", "K_ASCII", "K_AUTHORIZE", 
		"K_BATCH", "K_BEGIN", "K_BIGINT", "K_BLOB", "K_BOOLEAN", "K_BY", "K_CALLED", 
		"K_CAST", "K_CLUSTER", "K_CLUSTERING", "K_COLUMNFAMILY", "K_COMPACT", 
		"K_CONTAINS", "K_COUNT", "K_COUNTER", "K_CREATE", "K_CUSTOM", "K_DATACENTERS", 
		"K_DATE", "K_DECIMAL", "K_DEFAULT", "K_DELETE", "K_DESC", "K_DESCRIBE", 
		"K_DISTINCT", "K_DOUBLE", "K_DROP", "K_DURATION", "K_ENTRIES", "K_EXECUTE", 
		"K_EXISTS", "K_FILTERING", "K_FINALFUNC", "K_FLOAT", "K_FROM", "K_FROZEN", 
		"K_FULL", "K_FUNCTION", "K_FUNCTIONS", "K_GRANT", "K_GROUP", "K_IF", "K_IN", 
		"K_INDEX", "K_INET", "K_INITCOND", "K_INPUT", "K_INSERT", "K_INT", "K_INTERNALS", 
		"K_INTO", "K_IS", "K_JSON", "K_KEY", "K_KEYS", "K_KEYSPACE", "K_KEYSPACES", 
		"K_LANGUAGE", "K_LIKE", "K_LIMIT", "K_LIST", "K_LOGIN", "K_MAP", "K_MATERIALIZED", 
		"K_MBEAN", "K_MBEANS", "K_MODIFY", "K_NEGATIVE_INFINITY", "K_NEGATIVE_NAN", 
		"K_NOLOGIN", "K_NORECURSIVE", "K_NOSUPERUSER", "K_NOT", "K_NULL", "K_OF", 
		"K_ON", "K_ONLY", "K_OPTIONS", "K_OR", "K_ORDER", "K_PARTITION", "K_PASSWORD", 
		"K_PER", "K_PERMISSION", "K_PERMISSIONS", "K_POSITIVE_INFINITY", "K_POSITIVE_NAN", 
		"K_PRIMARY", "K_RENAME", "K_REPLACE", "K_RETURNS", "K_REVOKE", "K_ROLE", 
		"K_ROLES", "K_SCHEMA", "K_SELECT", "K_SET", "K_SFUNC", "K_SMALLINT", "K_STATIC", 
		"K_STORAGE", "K_STYPE", "K_SUPERUSER", "K_TABLES", "K_TEXT", "K_TIME", 
		"K_TIMESTAMP", "K_TIMEUUID", "K_TINYINT", "K_TO", "K_TOKEN", "K_TRIGGER", 
		"K_TRUNCATE", "K_TTL", "K_TUPLE", "K_TYPE", "K_TYPES", "K_UNLOGGED", "K_UNSET", 
		"K_UPDATE", "K_USE", "K_USER", "K_USERS", "K_USING", "K_UUID", "K_VALUES", 
		"K_VARCHAR", "K_VARINT", "K_VIEW", "K_WHERE", "K_WITH", "K_WRITETIME", 
		"L", "LETTER", "M", "MULTILINE_COMMENT", "N", "O", "P", "Q", "QMARK", 
		"QUOTED_NAME", "R", "RANGE", "S", "STRING_LITERAL", "T", "U", "UUID", 
		"V", "W", "WS", "X", "Y", "Z", "'!='", "'%'", "'('", "')'", "'+'", "'+='", 
		"','", "'-'", "'-='", "'.'", "'/'", "':'", "';'", "'<'", "'<='", "'='", 
		"'>'", "'>='", "'['", "'\\*'", "']'", "'expr('", "'{'", "'}'"
	};
	public static final int EOF=-1;
	public static final int T__195=195;
	public static final int T__196=196;
	public static final int T__197=197;
	public static final int T__198=198;
	public static final int T__199=199;
	public static final int T__200=200;
	public static final int T__201=201;
	public static final int T__202=202;
	public static final int T__203=203;
	public static final int T__204=204;
	public static final int T__205=205;
	public static final int T__206=206;
	public static final int T__207=207;
	public static final int T__208=208;
	public static final int T__209=209;
	public static final int T__210=210;
	public static final int T__211=211;
	public static final int T__212=212;
	public static final int T__213=213;
	public static final int T__214=214;
	public static final int T__215=215;
	public static final int T__216=216;
	public static final int T__217=217;
	public static final int T__218=218;
	public static final int A=4;
	public static final int B=5;
	public static final int BOOLEAN=6;
	public static final int C=7;
	public static final int COMMENT=8;
	public static final int D=9;
	public static final int DIGIT=10;
	public static final int DURATION=11;
	public static final int DURATION_UNIT=12;
	public static final int E=13;
	public static final int EMPTY_QUOTED_NAME=14;
	public static final int EXPONENT=15;
	public static final int F=16;
	public static final int FLOAT=17;
	public static final int G=18;
	public static final int H=19;
	public static final int HEX=20;
	public static final int HEXNUMBER=21;
	public static final int I=22;
	public static final int IDENT=23;
	public static final int INTEGER=24;
	public static final int J=25;
	public static final int K=26;
	public static final int K_ACCESS=27;
	public static final int K_ADD=28;
	public static final int K_AGGREGATE=29;
	public static final int K_AGGREGATES=30;
	public static final int K_ALL=31;
	public static final int K_ALLOW=32;
	public static final int K_ALTER=33;
	public static final int K_AND=34;
	public static final int K_APPLY=35;
	public static final int K_AS=36;
	public static final int K_ASC=37;
	public static final int K_ASCII=38;
	public static final int K_AUTHORIZE=39;
	public static final int K_BATCH=40;
	public static final int K_BEGIN=41;
	public static final int K_BIGINT=42;
	public static final int K_BLOB=43;
	public static final int K_BOOLEAN=44;
	public static final int K_BY=45;
	public static final int K_CALLED=46;
	public static final int K_CAST=47;
	public static final int K_CLUSTER=48;
	public static final int K_CLUSTERING=49;
	public static final int K_COLUMNFAMILY=50;
	public static final int K_COMPACT=51;
	public static final int K_CONTAINS=52;
	public static final int K_COUNT=53;
	public static final int K_COUNTER=54;
	public static final int K_CREATE=55;
	public static final int K_CUSTOM=56;
	public static final int K_DATACENTERS=57;
	public static final int K_DATE=58;
	public static final int K_DECIMAL=59;
	public static final int K_DEFAULT=60;
	public static final int K_DELETE=61;
	public static final int K_DESC=62;
	public static final int K_DESCRIBE=63;
	public static final int K_DISTINCT=64;
	public static final int K_DOUBLE=65;
	public static final int K_DROP=66;
	public static final int K_DURATION=67;
	public static final int K_ENTRIES=68;
	public static final int K_EXECUTE=69;
	public static final int K_EXISTS=70;
	public static final int K_FILTERING=71;
	public static final int K_FINALFUNC=72;
	public static final int K_FLOAT=73;
	public static final int K_FROM=74;
	public static final int K_FROZEN=75;
	public static final int K_FULL=76;
	public static final int K_FUNCTION=77;
	public static final int K_FUNCTIONS=78;
	public static final int K_GRANT=79;
	public static final int K_GROUP=80;
	public static final int K_IF=81;
	public static final int K_IN=82;
	public static final int K_INDEX=83;
	public static final int K_INET=84;
	public static final int K_INITCOND=85;
	public static final int K_INPUT=86;
	public static final int K_INSERT=87;
	public static final int K_INT=88;
	public static final int K_INTERNALS=89;
	public static final int K_INTO=90;
	public static final int K_IS=91;
	public static final int K_JSON=92;
	public static final int K_KEY=93;
	public static final int K_KEYS=94;
	public static final int K_KEYSPACE=95;
	public static final int K_KEYSPACES=96;
	public static final int K_LANGUAGE=97;
	public static final int K_LIKE=98;
	public static final int K_LIMIT=99;
	public static final int K_LIST=100;
	public static final int K_LOGIN=101;
	public static final int K_MAP=102;
	public static final int K_MATERIALIZED=103;
	public static final int K_MBEAN=104;
	public static final int K_MBEANS=105;
	public static final int K_MODIFY=106;
	public static final int K_NEGATIVE_INFINITY=107;
	public static final int K_NEGATIVE_NAN=108;
	public static final int K_NOLOGIN=109;
	public static final int K_NORECURSIVE=110;
	public static final int K_NOSUPERUSER=111;
	public static final int K_NOT=112;
	public static final int K_NULL=113;
	public static final int K_OF=114;
	public static final int K_ON=115;
	public static final int K_ONLY=116;
	public static final int K_OPTIONS=117;
	public static final int K_OR=118;
	public static final int K_ORDER=119;
	public static final int K_PARTITION=120;
	public static final int K_PASSWORD=121;
	public static final int K_PER=122;
	public static final int K_PERMISSION=123;
	public static final int K_PERMISSIONS=124;
	public static final int K_POSITIVE_INFINITY=125;
	public static final int K_POSITIVE_NAN=126;
	public static final int K_PRIMARY=127;
	public static final int K_RENAME=128;
	public static final int K_REPLACE=129;
	public static final int K_RETURNS=130;
	public static final int K_REVOKE=131;
	public static final int K_ROLE=132;
	public static final int K_ROLES=133;
	public static final int K_SCHEMA=134;
	public static final int K_SELECT=135;
	public static final int K_SET=136;
	public static final int K_SFUNC=137;
	public static final int K_SMALLINT=138;
	public static final int K_STATIC=139;
	public static final int K_STORAGE=140;
	public static final int K_STYPE=141;
	public static final int K_SUPERUSER=142;
	public static final int K_TABLES=143;
	public static final int K_TEXT=144;
	public static final int K_TIME=145;
	public static final int K_TIMESTAMP=146;
	public static final int K_TIMEUUID=147;
	public static final int K_TINYINT=148;
	public static final int K_TO=149;
	public static final int K_TOKEN=150;
	public static final int K_TRIGGER=151;
	public static final int K_TRUNCATE=152;
	public static final int K_TTL=153;
	public static final int K_TUPLE=154;
	public static final int K_TYPE=155;
	public static final int K_TYPES=156;
	public static final int K_UNLOGGED=157;
	public static final int K_UNSET=158;
	public static final int K_UPDATE=159;
	public static final int K_USE=160;
	public static final int K_USER=161;
	public static final int K_USERS=162;
	public static final int K_USING=163;
	public static final int K_UUID=164;
	public static final int K_VALUES=165;
	public static final int K_VARCHAR=166;
	public static final int K_VARINT=167;
	public static final int K_VIEW=168;
	public static final int K_WHERE=169;
	public static final int K_WITH=170;
	public static final int K_WRITETIME=171;
	public static final int L=172;
	public static final int LETTER=173;
	public static final int M=174;
	public static final int MULTILINE_COMMENT=175;
	public static final int N=176;
	public static final int O=177;
	public static final int P=178;
	public static final int Q=179;
	public static final int QMARK=180;
	public static final int QUOTED_NAME=181;
	public static final int R=182;
	public static final int RANGE=183;
	public static final int S=184;
	public static final int STRING_LITERAL=185;
	public static final int T=186;
	public static final int U=187;
	public static final int UUID=188;
	public static final int V=189;
	public static final int W=190;
	public static final int WS=191;
	public static final int X=192;
	public static final int Y=193;
	public static final int Z=194;

	// delegates
	public Cql_Parser gParser;
	public Parser[] getDelegates() {
		return new Parser[] {gParser};
	}

	// delegators


	public CqlParser(TokenStream input) {
		this(input, new RecognizerSharedState());
	}
	public CqlParser(TokenStream input, RecognizerSharedState state) {
		super(input, state);
		gParser = new Cql_Parser(input, state, this);
	}

	@Override public String[] getTokenNames() { return CqlParser.tokenNames; }
	@Override public String getGrammarFileName() { return "/Users/wang/Documents/project/configuration_code_understanding/code3/data/system/cassandra/src/antlr/Cql.g"; }


	    public void addErrorListener(ErrorListener listener)
	    {
	        gParser.addErrorListener(listener);
	    }

	    public void removeErrorListener(ErrorListener listener)
	    {
	        gParser.removeErrorListener(listener);
	    }

	    public void displayRecognitionError(String[] tokenNames, RecognitionException e)
	    {
	        gParser.displayRecognitionError(tokenNames, e);
	    }

	    protected void addRecognitionError(String msg)
	    {
	        gParser.addRecognitionError(msg);
	    }

	    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	    // Recovery methods are overridden to avoid wasting work on recovering from errors when the result will be
	    // ignored anyway.
	    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	    @Override
	    protected Object recoverFromMismatchedToken(IntStream input, int ttype, BitSet follow) throws RecognitionException
	    {
	        throw new MismatchedTokenException(ttype, input);
	    }

	    @Override
	    public void recover(IntStream input, RecognitionException re)
	    {
	        // Do nothing.
	    }



	// $ANTLR start "query"
	// /Users/wang/Documents/project/configuration_code_understanding/code3/data/system/cassandra/src/antlr/Cql.g:132:1: query returns [CQLStatement.Raw stmnt] : st= cqlStatement ( ';' )* EOF ;
	public final CQLStatement.Raw query() throws RecognitionException {
		CQLStatement.Raw stmnt = null;


		CQLStatement.Raw st =null;

		try {
			// /Users/wang/Documents/project/configuration_code_understanding/code3/data/system/cassandra/src/antlr/Cql.g:133:5: (st= cqlStatement ( ';' )* EOF )
			// /Users/wang/Documents/project/configuration_code_understanding/code3/data/system/cassandra/src/antlr/Cql.g:133:7: st= cqlStatement ( ';' )* EOF
			{
			pushFollow(FOLLOW_cqlStatement_in_query77);
			st=cqlStatement();
			state._fsp--;

			// /Users/wang/Documents/project/configuration_code_understanding/code3/data/system/cassandra/src/antlr/Cql.g:133:23: ( ';' )*
			loop1:
			while (true) {
				int alt1=2;
				int LA1_0 = input.LA(1);
				if ( (LA1_0==207) ) {
					alt1=1;
				}

				switch (alt1) {
				case 1 :
					// /Users/wang/Documents/project/configuration_code_understanding/code3/data/system/cassandra/src/antlr/Cql.g:133:24: ';'
					{
					match(input,207,FOLLOW_207_in_query80); 
					}
					break;

				default :
					break loop1;
				}
			}

			match(input,EOF,FOLLOW_EOF_in_query84); 
			 stmnt = st; 
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return stmnt;
	}
	// $ANTLR end "query"

	// Delegated rules
	public DataResource dataResource() throws RecognitionException { return gParser.dataResource(); }

	public CQL3Type.Raw comparatorType() throws RecognitionException { return gParser.comparatorType(); }

	public ModificationStatement.Parsed batchStatementObjective() throws RecognitionException { return gParser.batchStatementObjective(); }

	public void relation(WhereClause.Builder clauses) throws RecognitionException { gParser.relation(clauses); }

	public Selectable.Raw selectionSet(Selectable.Raw t1) throws RecognitionException { return gParser.selectionSet(t1); }

	public CreateIndexStatement.Raw createIndexStatement() throws RecognitionException { return gParser.createIndexStatement(); }

	public Cql_Parser.selectClause_return selectClause() throws RecognitionException { return gParser.selectClause(); }

	public void viewClusteringOrder(CreateViewStatement.Raw stmt) throws RecognitionException { gParser.viewClusteringOrder(stmt); }

	public Term.Raw mapLiteral(Term.Raw k) throws RecognitionException { return gParser.mapLiteral(k); }

	public DeleteStatement.Parsed deleteStatement() throws RecognitionException { return gParser.deleteStatement(); }

	public Operator containsOperator() throws RecognitionException { return gParser.containsOperator(); }

	public Selectable.RawIdentifier sident() throws RecognitionException { return gParser.sident(); }

	public void usingClause(Attributes.Raw attrs) throws RecognitionException { gParser.usingClause(attrs); }

	public DropKeyspaceStatement.Raw dropKeyspaceStatement() throws RecognitionException { return gParser.dropKeyspaceStatement(); }

	public TruncateStatement truncateStatement() throws RecognitionException { return gParser.truncateStatement(); }

	public Set<Permission> permissionOrAll() throws RecognitionException { return gParser.permissionOrAll(); }

	public void columnOperation(List<Pair<ColumnIdentifier, Operation.RawUpdate>> operations) throws RecognitionException { gParser.columnOperation(operations); }

	public void shorthandColumnOperation(List<Pair<ColumnIdentifier, Operation.RawUpdate>> operations, ColumnIdentifier key) throws RecognitionException { gParser.shorthandColumnOperation(operations, key); }

	public Selectable.Raw fieldSelectorModifier(Selectable.Raw receiver) throws RecognitionException { return gParser.fieldSelectorModifier(receiver); }

	public void tableProperty(CreateTableStatement.Raw stmt) throws RecognitionException { gParser.tableProperty(stmt); }

	public RevokeRoleStatement revokeRoleStatement() throws RecognitionException { return gParser.revokeRoleStatement(); }

	public DropViewStatement.Raw dropMaterializedViewStatement() throws RecognitionException { return gParser.dropMaterializedViewStatement(); }

	public CreateViewStatement.Raw createMaterializedViewStatement() throws RecognitionException { return gParser.createMaterializedViewStatement(); }

	public void viewPrimaryKey(CreateViewStatement.Raw stmt) throws RecognitionException { gParser.viewPrimaryKey(stmt); }

	public FieldIdentifier fident() throws RecognitionException { return gParser.fident(); }

	public ColumnIdentifier cident() throws RecognitionException { return gParser.cident(); }

	public Term.Raw collectionLiteral() throws RecognitionException { return gParser.collectionLiteral(); }

	public CreateKeyspaceStatement.Raw createKeyspaceStatement() throws RecognitionException { return gParser.createKeyspaceStatement(); }

	public DropRoleStatement dropRoleStatement() throws RecognitionException { return gParser.dropRoleStatement(); }

	public Tuples.INRaw inMarkerForTuple() throws RecognitionException { return gParser.inMarkerForTuple(); }

	public Selectable.Raw selectionAddition() throws RecognitionException { return gParser.selectionAddition(); }

	public CreateAggregateStatement.Raw createAggregateStatement() throws RecognitionException { return gParser.createAggregateStatement(); }

	public AlterViewStatement.Raw alterMaterializedViewStatement() throws RecognitionException { return gParser.alterMaterializedViewStatement(); }

	public boolean isStaticColumn() throws RecognitionException { return gParser.isStaticColumn(); }

	public AlterTypeStatement.Raw alterTypeStatement() throws RecognitionException { return gParser.alterTypeStatement(); }

	public DropAggregateStatement.Raw dropAggregateStatement() throws RecognitionException { return gParser.dropAggregateStatement(); }

	public Constants.Literal constant() throws RecognitionException { return gParser.constant(); }

	public GrantRoleStatement grantRoleStatement() throws RecognitionException { return gParser.grantRoleStatement(); }

	public Selectable.Raw selectionMap(Selectable.Raw k1) throws RecognitionException { return gParser.selectionMap(k1); }

	public List<Tuples.Literal> tupleOfTupleLiterals() throws RecognitionException { return gParser.tupleOfTupleLiterals(); }

	public CreateTypeStatement.Raw createTypeStatement() throws RecognitionException { return gParser.createTypeStatement(); }

	public Selectable.Raw selectionTupleOrNestedSelector() throws RecognitionException { return gParser.selectionTupleOrNestedSelector(); }

	public void tableDefinition(CreateTableStatement.Raw stmt) throws RecognitionException { gParser.tableDefinition(stmt); }

	public List<Selectable.Raw> selectionFunctionArgs() throws RecognitionException { return gParser.selectionFunctionArgs(); }

	public AbstractMarker.INRaw inMarker() throws RecognitionException { return gParser.inMarker(); }

	public void relationOrExpression(WhereClause.Builder clause) throws RecognitionException { gParser.relationOrExpression(clause); }

	public UpdateStatement.ParsedInsertJson jsonInsertStatement(QualifiedName qn) throws RecognitionException { return gParser.jsonInsertStatement(qn); }

	public void tableClusteringOrder(CreateTableStatement.Raw stmt) throws RecognitionException { gParser.tableClusteringOrder(stmt); }

	public Cql_Parser.username_return username() throws RecognitionException { return gParser.username(); }

	public UpdateStatement.ParsedInsert normalInsertStatement(QualifiedName qn) throws RecognitionException { return gParser.normalInsertStatement(qn); }

	public GrantPermissionsStatement grantPermissionsStatement() throws RecognitionException { return gParser.grantPermissionsStatement(); }

	public FunctionName functionName() throws RecognitionException { return gParser.functionName(); }

	public DropRoleStatement dropUserStatement() throws RecognitionException { return gParser.dropUserStatement(); }

	public void columnCondition(List<Pair<ColumnIdentifier, ColumnCondition.Raw>> conditions) throws RecognitionException { gParser.columnCondition(conditions); }

	public Tuples.Raw markerForTuple() throws RecognitionException { return gParser.markerForTuple(); }

	public QualifiedName columnFamilyName() throws RecognitionException { return gParser.columnFamilyName(); }

	public void userPassword(RoleOptions opts) throws RecognitionException { gParser.userPassword(opts); }

	public UpdateStatement.ParsedUpdate updateStatement() throws RecognitionException { return gParser.updateStatement(); }

	public UTName userTypeName() throws RecognitionException { return gParser.userTypeName(); }

	public Selectable.Raw selectorModifier(Selectable.Raw receiver) throws RecognitionException { return gParser.selectorModifier(receiver); }

	public ColumnIdentifier ident() throws RecognitionException { return gParser.ident(); }

	public Term.Raw listLiteral() throws RecognitionException { return gParser.listLiteral(); }

	public CreateTriggerStatement.Raw createTriggerStatement() throws RecognitionException { return gParser.createTriggerStatement(); }

	public AlterKeyspaceStatement.Raw alterKeyspaceStatement() throws RecognitionException { return gParser.alterKeyspaceStatement(); }

	public Term.Raw simpleTerm() throws RecognitionException { return gParser.simpleTerm(); }

	public Selectable.Raw collectionSubSelection(Selectable.Raw receiver) throws RecognitionException { return gParser.collectionSubSelection(receiver); }

	public List<RawSelector> selectors() throws RecognitionException { return gParser.selectors(); }

	public Term.Raw setLiteral(Term.Raw t) throws RecognitionException { return gParser.setLiteral(t); }

	public Selectable.Raw selectionGroupWithoutField() throws RecognitionException { return gParser.selectionGroupWithoutField(); }

	public void roleOptions(RoleOptions opts, DCPermissions.Builder dcperms) throws RecognitionException { gParser.roleOptions(opts, dcperms); }

	public String basic_unreserved_keyword() throws RecognitionException { return gParser.basic_unreserved_keyword(); }

	public Term.Raw selectionLiteral() throws RecognitionException { return gParser.selectionLiteral(); }

	public String unreserved_keyword() throws RecognitionException { return gParser.unreserved_keyword(); }

	public void groupByClause(List<ColumnIdentifier> groups) throws RecognitionException { gParser.groupByClause(groups); }

	public CQL3Type native_type() throws RecognitionException { return gParser.native_type(); }

	public ColumnIdentifier non_type_ident() throws RecognitionException { return gParser.non_type_ident(); }

	public CreateRoleStatement createRoleStatement() throws RecognitionException { return gParser.createRoleStatement(); }

	public JMXResource jmxResource() throws RecognitionException { return gParser.jmxResource(); }

	public String unreserved_function_keyword() throws RecognitionException { return gParser.unreserved_function_keyword(); }

	public RoleName userOrRoleName() throws RecognitionException { return gParser.userOrRoleName(); }

	public Operation.RawDeletion deleteOp() throws RecognitionException { return gParser.deleteOp(); }

	public Term.Raw value() throws RecognitionException { return gParser.value(); }

	public String propertyValue() throws RecognitionException { return gParser.propertyValue(); }

	public Selectable.Raw unaliasedSelector() throws RecognitionException { return gParser.unaliasedSelector(); }

	public Selectable.Raw selectionFunction() throws RecognitionException { return gParser.selectionFunction(); }

	public ColumnIdentifier noncol_ident() throws RecognitionException { return gParser.noncol_ident(); }

	public QualifiedName indexName() throws RecognitionException { return gParser.indexName(); }

	public Permission permission() throws RecognitionException { return gParser.permission(); }

	public DropTableStatement.Raw dropTableStatement() throws RecognitionException { return gParser.dropTableStatement(); }

	public Term.Raw setOrMapLiteral(Term.Raw t) throws RecognitionException { return gParser.setOrMapLiteral(t); }

	public void tableColumns(CreateTableStatement.Raw stmt) throws RecognitionException { gParser.tableColumns(stmt); }

	public Selectable.Raw selectionTypeHint() throws RecognitionException { return gParser.selectionTypeHint(); }

	public Term.Raw function() throws RecognitionException { return gParser.function(); }

	public List<Tuples.Raw> tupleOfMarkersForTuples() throws RecognitionException { return gParser.tupleOfMarkersForTuples(); }

	public Cql_Parser.mbean_return mbean() throws RecognitionException { return gParser.mbean(); }

	public Selectable.Raw selectionGroup() throws RecognitionException { return gParser.selectionGroup(); }

	public Term.Raw term() throws RecognitionException { return gParser.term(); }

	public void tablePartitionKey(CreateTableStatement.Raw stmt) throws RecognitionException { gParser.tablePartitionKey(stmt); }

	public BatchStatement.Parsed batchStatement() throws RecognitionException { return gParser.batchStatement(); }

	public CQL3Type.Raw tuple_type() throws RecognitionException { return gParser.tuple_type(); }

	public UserTypes.Literal usertypeLiteral() throws RecognitionException { return gParser.usertypeLiteral(); }

	public void viewProperty(CreateViewStatement.Raw stmt) throws RecognitionException { gParser.viewProperty(stmt); }

	public String allowedFunctionName() throws RecognitionException { return gParser.allowedFunctionName(); }

	public void property(PropertyDefinitions props) throws RecognitionException { gParser.property(props); }

	public CQL3Type.Raw collection_type() throws RecognitionException { return gParser.collection_type(); }

	public void dcPermission(DCPermissions.Builder builder) throws RecognitionException { gParser.dcPermission(builder); }

	public Selectable.Raw simpleUnaliasedSelector() throws RecognitionException { return gParser.simpleUnaliasedSelector(); }

	public void idxName(QualifiedName name) throws RecognitionException { gParser.idxName(name); }

	public ListRolesStatement listUsersStatement() throws RecognitionException { return gParser.listUsersStatement(); }

	public void properties(PropertyDefinitions props) throws RecognitionException { gParser.properties(props); }

	public FunctionResource functionResource() throws RecognitionException { return gParser.functionResource(); }

	public AlterRoleStatement alterRoleStatement() throws RecognitionException { return gParser.alterRoleStatement(); }

	public void udtColumnOperation(List<Pair<ColumnIdentifier, Operation.RawUpdate>> operations, ColumnIdentifier key, FieldIdentifier field) throws RecognitionException { gParser.udtColumnOperation(operations, key, field); }

	public Term.Raw intValue() throws RecognitionException { return gParser.intValue(); }

	public UseStatement useStatement() throws RecognitionException { return gParser.useStatement(); }

	public DropFunctionStatement.Raw dropFunctionStatement() throws RecognitionException { return gParser.dropFunctionStatement(); }

	public RevokePermissionsStatement revokePermissionsStatement() throws RecognitionException { return gParser.revokePermissionsStatement(); }

	public Selectable.Raw selectionList() throws RecognitionException { return gParser.selectionList(); }

	public AlterRoleStatement alterUserStatement() throws RecognitionException { return gParser.alterUserStatement(); }

	public String keyspaceName() throws RecognitionException { return gParser.keyspaceName(); }

	public void typeColumns(CreateTypeStatement.Raw stmt) throws RecognitionException { gParser.typeColumns(stmt); }

	public ModificationStatement.Parsed insertStatement() throws RecognitionException { return gParser.insertStatement(); }

	public DropTypeStatement.Raw dropTypeStatement() throws RecognitionException { return gParser.dropTypeStatement(); }

	public Operator relationType() throws RecognitionException { return gParser.relationType(); }

	public void columnOperationDifferentiator(List<Pair<ColumnIdentifier, Operation.RawUpdate>> operations, ColumnIdentifier key) throws RecognitionException { gParser.columnOperationDifferentiator(operations, key); }

	public CQLStatement.Raw cqlStatement() throws RecognitionException { return gParser.cqlStatement(); }

	public Term.Raw termGroup() throws RecognitionException { return gParser.termGroup(); }

	public WhereClause.Builder whereClause() throws RecognitionException { return gParser.whereClause(); }

	public CreateTableStatement.Raw createTableStatement() throws RecognitionException { return gParser.createTableStatement(); }

	public List<Term.Raw> singleColumnInValues() throws RecognitionException { return gParser.singleColumnInValues(); }

	public Selectable.Raw selectionMapOrSet() throws RecognitionException { return gParser.selectionMapOrSet(); }

	public void viewPartitionKey(CreateViewStatement.Raw stmt) throws RecognitionException { gParser.viewPartitionKey(stmt); }

	public void ksName(QualifiedName name) throws RecognitionException { gParser.ksName(name); }

	public void cfName(QualifiedName name) throws RecognitionException { gParser.cfName(name); }

	public DropTriggerStatement.Raw dropTriggerStatement() throws RecognitionException { return gParser.dropTriggerStatement(); }

	public List<ColumnIdentifier> tupleOfIdentifiers() throws RecognitionException { return gParser.tupleOfIdentifiers(); }

	public void orderByClause(Map<ColumnIdentifier, Boolean> orderings) throws RecognitionException { gParser.orderByClause(orderings); }

	public void roleOption(RoleOptions opts, DCPermissions.Builder dcperms) throws RecognitionException { gParser.roleOption(opts, dcperms); }

	public CreateFunctionStatement.Raw createFunctionStatement() throws RecognitionException { return gParser.createFunctionStatement(); }

	public ListPermissionsStatement listPermissionsStatement() throws RecognitionException { return gParser.listPermissionsStatement(); }

	public void roleName(RoleName name) throws RecognitionException { gParser.roleName(name); }

	public IResource resource() throws RecognitionException { return gParser.resource(); }

	public RoleResource roleResource() throws RecognitionException { return gParser.roleResource(); }

	public AlterTableStatement.Raw alterTableStatement() throws RecognitionException { return gParser.alterTableStatement(); }

	public RawSelector selector() throws RecognitionException { return gParser.selector(); }

	public Term.Raw termAddition() throws RecognitionException { return gParser.termAddition(); }

	public SelectStatement.RawStatement selectStatement() throws RecognitionException { return gParser.selectStatement(); }

	public Selectable.Raw selectionMultiplication() throws RecognitionException { return gParser.selectionMultiplication(); }

	public Selectable.Raw selectionGroupWithField() throws RecognitionException { return gParser.selectionGroupWithField(); }

	public CreateRoleStatement createUserStatement() throws RecognitionException { return gParser.createUserStatement(); }

	public Maps.Literal fullMapLiteral() throws RecognitionException { return gParser.fullMapLiteral(); }

	public void usingClauseObjective(Attributes.Raw attrs) throws RecognitionException { gParser.usingClauseObjective(attrs); }

	public List<Operation.RawDeletion> deleteSelection() throws RecognitionException { return gParser.deleteSelection(); }

	public void customIndexExpression(WhereClause.Builder clause) throws RecognitionException { gParser.customIndexExpression(clause); }

	public void normalColumnOperation(List<Pair<ColumnIdentifier, Operation.RawUpdate>> operations, ColumnIdentifier key) throws RecognitionException { gParser.normalColumnOperation(operations, key); }

	public void usingClauseDelete(Attributes.Raw attrs) throws RecognitionException { gParser.usingClauseDelete(attrs); }

	public DescribeStatement describeStatement() throws RecognitionException { return gParser.describeStatement(); }

	public ListRolesStatement listRolesStatement() throws RecognitionException { return gParser.listRolesStatement(); }

	public Tuples.Literal tupleLiteral() throws RecognitionException { return gParser.tupleLiteral(); }

	public Json.Raw jsonValue() throws RecognitionException { return gParser.jsonValue(); }

	public void collectionColumnOperation(List<Pair<ColumnIdentifier, Operation.RawUpdate>> operations, ColumnIdentifier key, Term.Raw k) throws RecognitionException { gParser.collectionColumnOperation(operations, key, k); }

	public void indexIdent(List<IndexTarget.Raw> targets) throws RecognitionException { gParser.indexIdent(targets); }

	public List<Term.Raw> functionArgs() throws RecognitionException { return gParser.functionArgs(); }

	public Term.Raw termMultiplication() throws RecognitionException { return gParser.termMultiplication(); }

	public List<Pair<ColumnIdentifier, ColumnCondition.Raw>> updateConditions() throws RecognitionException { return gParser.updateConditions(); }

	public DropIndexStatement.Raw dropIndexStatement() throws RecognitionException { return gParser.dropIndexStatement(); }



	public static final BitSet FOLLOW_cqlStatement_in_query77 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000008000L});
	public static final BitSet FOLLOW_207_in_query80 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000008000L});
	public static final BitSet FOLLOW_EOF_in_query84 = new BitSet(new long[]{0x0000000000000002L});
}
