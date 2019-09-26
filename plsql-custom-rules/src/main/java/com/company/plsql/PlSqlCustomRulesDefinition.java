package com.company.plsql;

import org.sonar.plsqlopen.CustomPlSqlRulesDefinition;

public class PlSqlCustomRulesDefinition extends CustomPlSqlRulesDefinition {

	@Override
	public String repositoryName() {
		return "dcits";
	}

	@Override
	public String repositoryKey() {
		return "dcits-rules";
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Class[] checkClasses() {
		return new Class[] {
				//         ForbiddenDmlCheck.class,
				NoDataFoundCheck.class,
				CursorMustBeClose.class,
				FileMustBeClose.class,
				NoDataFoundHandleCheck.class,
				UpdateAndDeleteWithWhereCheck.class,
				CreateTableWithTableSpaceCheck.class,
				MultipleQueriesCheck.class,
				CodeWithInstanceNameCheck.class,
				MultipleCallFunctionCheck.class,
				LoopWithoutInitCheck.class,
				CartesianProductCheck.class,
				FileMustBeCloseInExceptHanding.class
		};
	}

}
