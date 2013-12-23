package com.wang.dao;

import org.junit.runner.RunWith;


/**
 * 对Spring的JDBCTemplatee进行测试
 * @author wang
 *
 */

@RunWith(SpringJUnit4ClassRunner.class)  
@ContextConfiguration(locations = { "classpath*:applicationContext.xml" })
@TransactionConfiguration(transactionManager = "txManager", defaultRollback = true)
@Transactional
public class SpringJDBCTemplate {

}
