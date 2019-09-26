package com.company.plsql;
import java.util.ArrayList;
import java.util.List;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.plsqlopen.annnotations.ActivatedByDefault;
import org.sonar.plsqlopen.annnotations.ConstantRemediation;
import org.sonar.plsqlopen.checks.AbstractBaseCheck;
import org.sonar.plsqlopen.checks.Tags;
import org.sonar.plugins.plsqlopen.api.PlSqlGrammar;

import com.sonar.sslr.api.AstNode;

@Rule(
		name = "File handle must be closed in exception handling",
		description = "<p>\r\n" + 
				"文件句柄必须在异常分支中进行关闭。\r\n"
				+ "使用UTL_FILE.fopen打开文件时，必须在正常分支和异常分支中分别进行UTL_FILE.fclose(v_file_handle)，\r\n" + 
				"使用框架cbsd_batch_file_xxx.init打开文件时，必须在正常分支和异常分支中分别进行cbsd_batch_file_xxx.destroy()，\r\n" + 
				"即关闭语句需出现两次 。" + 
				"</p>\r\n" + 
				"\r\n" + 
				"<h2>Noncompliant Code Example</h2>\r\n" + 
				"\r\n" + 
				"<pre>\r\n" + 
				"BEGIN\r\n" + 
				"v_file_handle := UTL_FILE.fopen (v_file_path, v_file_name, 'W'); --打开文件\r\n" + 
				"/业务处理代码/\r\n" + 
				"UTL_FILE.put_line (v_file_handle, v_curline, TRUE); --写文件\r\n" + 
				"UTL_FILE.fclose (v_file_handle); --关闭文件\r\n" + 
				"END;\r\n" + 
				"--示例二\r\n" + 
				"BEGIN\r\n" + 
				"cbsd_batch_file_writer.init (p_sys_head_i => v_sys_head_i); --打开文件\r\n" + 
				"/业务处理代码/\r\n" + 
				"cbsd_batch_file_writer.writebodyfile (p_struct=> SYS.ANYDATA.convertobject,p_struct_name => v_struct_name ); --写文件\r\n" + 
				"cbsd_batch_file_writer.destroy(); --关闭文件\r\n" + 
				"END;\r\n" + 
				"\r\n" + 
				"</pre>\r\n" + 
				"\r\n" + 
				"<h2>Compliant Solution</h2>\r\n" + 
				"\r\n" + 
				"<pre>\r\n" + 
				"--示例一\r\n" + 
				"BEGIN\r\n" + 
				"v_file_handle := UTL_FILE.fopen (v_file_path, v_file_name, 'W'); --打开文件\r\n" + 
				"/业务处理代码/\r\n" + 
				"UTL_FILE.put_line (v_file_handle, v_curline, TRUE); --写文件\r\n" + 
				"UTL_FILE.fclose (v_file_handle); --关闭文件\r\n" + 
				"EXCEPTION\r\n" + 
				"WHEN OTHERS\r\n" + 
				"THEN\r\n" + 
				"UTL_FILE.fclose (v_file_handle); --关闭文件\r\n" + 
				"RAISE; --抛出异常信息\r\n" + 
				"END;\r\n" + 
				"--示例二\r\n" + 
				"BEGIN\r\n" + 
				"cbsd_batch_file_writer.init (p_sys_head_i => v_sys_head_i); --打开文件\r\n" + 
				"/业务处理代码/\r\n" + 
				"cbsd_batch_file_writer.writebodyfile (p_struct=> SYS.ANYDATA.convertobject,p_struct_name => v_struct_name ); --写文件\r\n" + 			
				"cbsd_batch_file_writer.destroy(); --关闭文件\r\n" + 
				"EXCEPTION\r\n" + 
				"WHEN OTHERS\r\n" + 
				"THEN\r\n" + 
				"cbsd_batch_file_writer.destroy(); --关闭文件\r\n" + 
				"RAISE; --抛出异常信息\r\n" + 
				"END;\r\n" + 
				"</pre>",
				key = "FileMustBeClosedInException",
				priority = Priority.MAJOR,
				tags=Tags.BUG
		)
@ConstantRemediation("10min")
@ActivatedByDefault
public class FileMustBeCloseInExceptHanding extends AbstractBaseCheck {
	 public static final String UTILMETHODNAME = "utl_file";
	 public static final String MESSAGE = "The file must be closed.";
	 public static final String EXMESSAGE = "The file must be closed in exception handling.";
	 
	@Override
	public void init() {
		subscribeTo(PlSqlGrammar.STATEMENTS_SECTION);
	}

	@Override
	public void visitNode(AstNode node) {
		if (!node.hasParent(PlSqlGrammar.PROCEDURE_DECLARATION) && !node.hasParent(PlSqlGrammar.FUNCTION_DECLARATION) && !node.hasAncestor(PlSqlGrammar.ANONYMOUS_BLOCK))
		{
			return;
		}
			List<AstNode> methods = node.getDescendants(PlSqlGrammar.METHOD_CALL);			
			if(methods ==null || methods.isEmpty()) {
				return;
			}
			
			List<String> cbsdmethodnames = new ArrayList<>();		
			cbsdmethodnames.add("cbsd_batch_file_writer");
			cbsdmethodnames.add("cbsd_batch_file_reader");
			cbsdmethodnames.add("cbsd_batch_file");
			List<AstNode> cbsdfilemethods = new ArrayList<>();
			List<AstNode> ufilemethods = new ArrayList<>();
			for(int i = 0 ; i < methods.size() ; i++) {
				try {
					AstNode statement =  methods.get(i);
					String methodname = statement.getFirstChild(PlSqlGrammar.MEMBER_EXPRESSION).getTokenOriginalValue();
					if (cbsdmethodnames.contains(methodname)) {
						cbsdfilemethods.add(statement);
					}
					if ( methodname.equalsIgnoreCase(UTILMETHODNAME)) {
						ufilemethods.add(statement);
					}
				}
				catch(Exception e){
					continue;    					 
				}    			
			}
			
			if (!ufilemethods.isEmpty()){
				visitUtlFileMethod(ufilemethods);
			}
			if (!cbsdfilemethods.isEmpty()){
				visitCbsdMethod(cbsdfilemethods,cbsdmethodnames);
			}
						
	}

	private void visitUtlFileMethod(List<AstNode> filemethods) {
		  
			for(int i = 0 ; i < filemethods.size() ; i++) {
				AstNode statement =  filemethods.get(i);
				try {
					AstNode opennode =statement.getFirstChild(PlSqlGrammar.MEMBER_EXPRESSION);
					String openidentifier = opennode.getFirstChild(PlSqlGrammar.IDENTIFIER_NAME).getTokenOriginalValue();
					String openfilename = statement.getPreviousAstNode().getPreviousAstNode().getTokenOriginalValue();					
					if (opennode.getTokenOriginalValue().equalsIgnoreCase(UTILMETHODNAME) && openidentifier.equalsIgnoreCase("fopen")) { 
						List<AstNode> utlnextmethods = filemethods.subList(i+1, filemethods.size());
						if (utlnextmethods ==null || utlnextmethods.isEmpty()) {
							getContext().createViolation(this, MESSAGE, statement);
						}else {							
							getUtilfclose(utlnextmethods,  statement, openfilename);							
						}
					}
				}
				catch(Exception e){
					continue;    					 
				}    		
			}

	}
	
	private void visitCbsdMethod(List<AstNode> filemethods,List<String> cbsdmethodnames) {
		for(int i = 0 ; i < filemethods.size() ; i++) {
			AstNode statement =  filemethods.get(i);
			try {
				String identifiername = statement.getFirstChild(PlSqlGrammar.MEMBER_EXPRESSION).getFirstChild(PlSqlGrammar.IDENTIFIER_NAME).getTokenOriginalValue();
				if (cbsdmethodnames.contains(statement.getFirstChild(PlSqlGrammar.MEMBER_EXPRESSION).getTokenOriginalValue()) && identifiername.equalsIgnoreCase("init")) {
					List<AstNode> cbsdnextmethods = filemethods.subList(i+1, filemethods.size());
					if (cbsdnextmethods ==null || cbsdnextmethods.isEmpty()) {
						getContext().createViolation(this, MESSAGE, statement);
					}else {							
						getCbsdDestroy(cbsdnextmethods,statement);							
					}
																	
				}										
				
			}
			catch(Exception e){
				continue;    					 
			}    		
		}
	}

	private void getUtilfclose(List<AstNode> utlnextmethods,AstNode statement, String openfilename) {	
		List<AstNode> closefilenodes = new ArrayList<>();
		for(int i = 0 ; i < utlnextmethods.size() ; i++) {
			try {
				AstNode closestatement =  utlnextmethods.get(i);
				String identifiername = closestatement.getFirstChild(PlSqlGrammar.MEMBER_EXPRESSION).getFirstChild(PlSqlGrammar.IDENTIFIER_NAME).getTokenOriginalValue();
				if (closestatement.getFirstChild(PlSqlGrammar.MEMBER_EXPRESSION).getTokenOriginalValue().equalsIgnoreCase(UTILMETHODNAME) && identifiername.equalsIgnoreCase("fclose")) {
					closefilenodes.add(closestatement);
				}
			}
			catch(Exception e){
				continue;    					 
			}    			
		}
		List<AstNode> matchclosenodes = new ArrayList<>();
		for(int i = 0 ; i < closefilenodes.size() ; i++) {			
			if (!closefilenodes.isEmpty() && closefilenodes.get(i).getFirstChild(PlSqlGrammar.ARGUMENTS).getFirstChild(PlSqlGrammar.ARGUMENT).getTokenOriginalValue().equalsIgnoreCase(openfilename)) {
				matchclosenodes.add(closefilenodes.get(i));
			}
		}		
		verifyMethod(statement, matchclosenodes);		
	}

	

	private void getCbsdDestroy(List<AstNode> cbsdnextmethodnames,AstNode statement) {
		List<AstNode> destroyfilenodes = new ArrayList<>();	
		for(int i = 0 ; i < cbsdnextmethodnames.size() ; i++) {
			String idename = cbsdnextmethodnames.get(i).getFirstChild(PlSqlGrammar.MEMBER_EXPRESSION).getFirstChild(PlSqlGrammar.IDENTIFIER_NAME).getTokenOriginalValue();
			if (idename.equalsIgnoreCase("destroy")) {
				destroyfilenodes.add(cbsdnextmethodnames.get(i));													
			}		
		}
		verifyMethod(statement, destroyfilenodes);
		
	}

	private void verifyMethod(AstNode statement, List<AstNode> filenodes) {
		if (filenodes.isEmpty()) {		
			getContext().createViolation(this, MESSAGE, statement);
		}
		if (!filenodes.isEmpty() && filenodes.size()<2) {		
			getContext().createViolation(this, EXMESSAGE, statement);
		}
		if (!filenodes.isEmpty() && filenodes.size()>1) {	
			verifyMethodInexcept(filenodes, statement);
		}
	}

	private void verifyMethodInexcept(List<AstNode> filenodes, AstNode statement) {
		boolean flag=true;
		for(int i = 0 ; i < filenodes.size() ; i++) {
			if(filenodes.get(i).hasAncestor(PlSqlGrammar.EXCEPTION_HANDLER)) {
				flag=false;
			}			
		}
		if (flag){
			getContext().createViolation(this, EXMESSAGE, statement);
		}
		}
	
}






