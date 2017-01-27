package dpit.dao;

import java.util.HashMap;
import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository("mofDao")
public class MofDAO {
	
	private final String NS = "dpit.dao.";
	
	@Autowired 
	@Qualifier("sqlSessionTemplate")
	private SqlSessionTemplate sqlSessionTemplate;
	
	public List<HashMap> selectMofConnect() {
		return sqlSessionTemplate.selectList(NS+"selectMofConnect",null);
	}
}