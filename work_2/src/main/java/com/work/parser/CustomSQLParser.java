package com.work.parser;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLInListExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.visitor.SQLASTVisitorAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @author: jinqi
 * @create: 2023-02-26 21:50
 **/
public class CustomSQLParser {

    public static void parseStatementList(List<SQLStatement> statementList) {
        boolean lastStatementIsSelect = false;
        for (SQLStatement statement : statementList) {
            if (statement instanceof SQLSelectStatement) {
                if (lastStatementIsSelect) {
                    throw new RuntimeException("不允许进行 SQL 拼接");
                }
                lastStatementIsSelect = true;

                SQLSelectStatement selectStatement = (SQLSelectStatement) statement;
                SQLSelectQueryBlock queryBlock = selectStatement.getSelect().getQueryBlock();
                SQLExpr where = queryBlock.getWhere();

                if (where != null) {
                    List<SQLInListExpr> inListExprList = new ArrayList<>();
                    where.accept(new SQLASTVisitorAdapter() {
                        @Override
                        public boolean visit(SQLInListExpr inListExpr) {
                            inListExprList.add(inListExpr);
                            return true;
                        }
                    });

                    for (SQLInListExpr inListExpr : inListExprList) {
                        if (inListExpr.getTargetList().size() > 10) {
                            throw new RuntimeException("in 列表超过了 10 个");
                        }
                    }
                }
            } else {
                lastStatementIsSelect = false;
            }
        }
    }


    public static void main(String[] args) {
        // in参数
        String sql = "SELECT * FROM user WHERE id IN (1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11);";
        // SQL拼接
//        String sql = "SELECT * FROM user WHERE id = 1;SELECT * FROM user WHERE id = 2;";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        parseStatementList(statementList);
        System.out.println("SQL 校验通过");
    }
}

