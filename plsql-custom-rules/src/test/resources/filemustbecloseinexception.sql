BEGIN
cbsd_batch_file_writer.init (p_sys_head_i => v_sys_head_i); -- Noncompliant {{The file must be closed in exception handling.}}
cbsd_batch_file_writer.writebodyfile (p_struct=> SYS.ANYDATA.convertobject,p_struct_name => v_struct_name ); 
cbsd_batch_file_writer.destroy();
cbsd_batch_file_writer.destroy();
END;
BEGIN
v_file_handle :=
         UTL_FILE.fopen (file_path,file_name,'R',32767);  -- Noncompliant {{The file must be closed in exception handling.}}
UTL_FILE.fclose (v_file_handle); 
UTL_FILE.fclose (v_file_handle); 
END;

BEGIN
v_file_handle :=
         UTL_FILE.fopen (file_path,file_name,'R',32767);   -- Noncompliant {{The file must be closed in exception handling.}}
UTL_FILE.fclose (v_file_handle); 
UTL_FILE.fclose (v_file_handle11); 
END;

BEGIN
v_file_handle :=
         UTL_FILE.fopen (file_path,file_name,'R',32767);  -- Noncompliant {{The file must be closed in exception handling.}}
UTL_FILE.fclose (v_file_handle); 
END;

BEGIN
v_file_handle :=
         UTL_FILE.fopen (file_path,file_name,'R',32767);-- Noncompliant {{The file must be closed in exception handling.}}
IF UTL_FILE.is_open (v_file_handle) 
      THEN
         UTL_FILE.fclose (v_file_handle);  
      END IF;
END;

BEGIN
cbsd_batch_file_writer.init (p_sys_head_i => v_sys_head_i); -- Noncompliant {{The file must be closed in exception handling.}}
cbsd_batch_file_writer.writebodyfile (p_struct=> SYS.ANYDATA.convertobject,p_struct_name => v_struct_name ); 
cbsd_batch_file_writer.destroy();   
END;

BEGIN
cbsd_batch_file_reader.init (p_sys_head_i => v_sys_head_i); -- Noncompliant {{The file must be closed in exception handling.}}
cbsd_batch_file_reader.destroy();   
END;

BEGIN
cbsd_batch_file.init (p_sys_head_i => v_sys_head_i); -- Noncompliant {{The file must be closed in exception handling.}}
cbsd_batch_file.writebodyfile (p_struct=> SYS.ANYDATA.convertobject,p_struct_name => v_struct_name ); 
cbsd_batch_file.destroy();   
END;

BEGIN
v_file_handle := UTL_FILE.fopen (v_file_path, v_file_name, 'W');
UTL_FILE.put_line (v_file_handle, v_curline, TRUE); 
UTL_FILE.fclose (v_file_handle);
EXCEPTION
WHEN OTHERS
THEN
UTL_FILE.fclose (v_file_handle);
RAISE;
END;

BEGIN
cbsd_batch_file_writer.init (p_sys_head_i => v_sys_head_i);
cbsd_batch_file_writer.writebodyfile (p_struct=> SYS.ANYDATA.convertobject,p_struct_name => v_struct_name );
cbsd_batch_file_writer.destroy(); 
EXCEPTION
WHEN OTHERS
THEN
cbsd_batch_file_writer.destroy(); 
RAISE; 
END;

BEGIN
   fileID := UTL_FILE.FOPEN ('/tmp', 'emp.dat', 'W'); -- Noncompliant {{The file must be closed in exception handling.}}s
 
   /* Quick and dirty construction here! */
   FOR emprec IN (SELECT * FROM emp)
   LOOP
      UTL_FILE.PUT_LINE 
         (TO_CHAR (emprec.empno) || ',' ||
          emprec.ename || ',' ||
          TO_CHAR (emprec.deptno));
   END LOOP;
 
   UTL_FILE.FCLOSE (fileID); 
END;

BEGIN
v_file_handle :=
         UTL_FILE.fopen (file_path,file_name,'R',32767);  -- Noncompliant {{The file must be closed.}}
END;

BEGIN
v_file_handle :=
         UTL_FILE.fopen (file_path,file_name,'R',32767); -- Noncompliant {{The file must be closed.}}
UTL_FILE.fclose (v_file_handleww);        								
END;

BEGIN
cbsd_batch_file_writer.init (p_sys_head_i => v_sys_head_i); -- Noncompliant {{The file must be closed.}}
cbsd_batch_file_writer.writebodyfile (p_struct=> SYS.ANYDATA.convertobject,p_struct_name => v_struct_name ); 
END;

BEGIN
cbsd_batch_file_reader.init (p_sys_head_i => v_sys_head_i); -- Noncompliant {{The file must be closed.}}
END;

BEGIN
cbsd_batch_file.init (p_sys_head_i => v_sys_head_i); -- Noncompliant {{The file must be closed.}}
END;

BEGIN
   fileID := UTL_FILE.FOPEN ('/tmp', 'emp.dat', 'W'); -- Noncompliant {{The file must be closed.}}
 
   /* Quick and dirty construction here! */
   FOR emprec IN (SELECT * FROM emp)
   LOOP
      UTL_FILE.PUT_LINE 
         (TO_CHAR (emprec.empno) || ',' ||
          emprec.ename || ',' ||
          TO_CHAR (emprec.deptno));
   END LOOP;
 
END;

CREATE OR REPLACE PACKAGE BODY RB_SHJR_TRANS
AS
PROCEDURE shjr_inner_collate_rec (p_input    IN     SYS.ANYDATA,
                                     p_out      IN OUT SYS.ANYDATA,
                                     p_status      OUT VARCHAR2)
   IS
      v_sys_head_o     msg_sys_header_o_t;
      v_sys_head_i     msg_sys_header_i_t;
      v_input          zx_collating_i_t;
      v_out            zx_collating_o_t;
      v_app_head_o     msg_app_header_t;
      v_step           VARCHAR2 (6);
      v_error_msg      VARCHAR2 (200);
      v_ret_code       VARCHAR2 (20);
      v_count          NUMBER := 0;
      v_num            NUMBER := 0;
      v_list_num       NUMBER := 0;
    
      
   BEGIN
      cbsd_log.setprocname ('[zx_nonfinancial.SHJR_inner_collate_rec]');
      cbsd_log.info ('[pt.SHJR_inner_collate_rec][BEGIN]');
      p_status := '000000';
 
      v_ret_code := p_input.getobject (v_input);
      v_ret_code := p_out.getobject (v_out);
      v_sys_head_o := v_out.getsyshead;
 
 
      BEGIN
         SELECT system_phase INTO v_system_phase FROM fm_parameter;
      EXCEPTION
         WHEN OTHERS
         THEN
            NULL;
      END;
 
      IF v_system_phase != 'INP'
      THEN
         BEGIN
            SELECT 'Y'
              INTO v_eod_flag
              FROM fm_system
             WHERE TO_CHAR (run_date, 'yyyymmdd') = v_input.collating_date;
         EXCEPTION
            WHEN NO_DATA_FOUND
            THEN
               NULL;
         END;
 
         IF v_eod_flag = 'Y'
         THEN
            p_status := '305389';
            raise_application_error (-20000, p_status);
         END IF;
      END IF;
 
 
      BEGIN
         SELECT file_snd_dir
           INTO v_file_dir
           FROM rb_file_dir
          WHERE FILE_TYPE = 'SHJR_FILE';
      END;
 
      v_collate_flag := 'N';
 
      BEGIN
         SELECT 'Y'
           INTO v_collate_flag
           FROM rb_channel_hist
          WHERE     TO_CHAR (TRAN_DATE, 'yyyymmdd') = v_input.collating_date
                AND source_type = 'BF'
                AND ROWNUM = 1;
      EXCEPTION
         WHEN NO_DATA_FOUND
         THEN
            v_collate_flag := 'N';
         WHEN OTHERS
         THEN
            v_collate_flag := 'N';
            p_status := '310524';
            cbsd_log.info (SQLERRM);
            raise_application_error (-20000, p_status);
      END;
 
      BEGIN
         v_file_handle :=
            UTL_FILE.fopen (v_file_dir,
                            'SHJR_HEIP_' || v_input.collating_date || '.txt',
                            'w');
      EXCEPTION
         WHEN OTHERS
         THEN
            p_status := '310821';
            cbsd_log.info (SQLERRM);
            raise_application_error (-20000, p_status);
      END;
 
      FOR c1 IN c_collating_heip (p_collating_date => v_input.collating_date)
      LOOP
         cbsd_log.info ('c1' || c1.reference);
 
 
         IF v_collate_flag = 'N'
         THEN
            UTL_FILE.fclose (v_file_handle);
         ELSE
            BEGIN
               UTL_FILE.put_line (
                  v_file_handle,
                     c1.reference                                     --REF_NO
                  || '|'
                  || c1.channel_seq_no                        --CHANNEL_SEQ_NO
                  || '|'
                  || TO_CHAR (c1.tran_date, 'yyyymmdd')           ---TRAN_DATE
                  || '|'
                  || c1.ccy                                              --CCY
                  || '|'
                  || c1.tran_amt                                    --TRAN_AMT
                  || '|'
                  || c1.cr_dr                                     ---DR_CR_IND
                  || '|'
                  || c1.PAYER_ACCT
                  || '|'
                  || c1.PAYER_NAME
                  || '|'
                  || c1.PAYEE_ACCT
                  || '|'
                  || c1.PAYEE_NAME
                  || '|'
                  || c1.tran_type                                 ---TRAN_TYPE
                  || '|'
                  || c1.STATUS
                  || '|');
            EXCEPTION
               WHEN OTHERS
               THEN
                  UTL_FILE.fclose (v_file_handle);
            END;
 
            v_count := v_count + 1;
         END IF;
      END LOOP;
 
      IF UTL_FILE.is_open (v_file_handle)
      THEN
         UTL_FILE.fclose (v_file_handle);
      END IF;
 
      v_out.total_num := v_count;
 
      v_out.file_path :=
         v_file_dir || 'SHJR_HEIP_' || v_input.collating_date || '.txt';
      v_out.msg_type := v_input.format;
      v_out.collating_date := v_input.collating_date;
 
      cbsd_util.addsyshead (p_status, 'Success', v_sys_head_o);
      v_out.setsyshead (v_sys_head_o);
      p_out := sys.anydata.convertobject (v_out);
   END shjr_inner_collate_rec;

   END RB_SHJR_TRANS;