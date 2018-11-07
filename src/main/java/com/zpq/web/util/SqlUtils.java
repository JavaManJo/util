package com.zpq.web.util;

import java.util.*;

public class SqlUtils {
	
	public static class Select {
		
		private Class<?> table;
		private String alias;
//		private String columns;
		private List<Class<?>> columnTable = new ArrayList<>();
		
		private int tableCount = 0;
		
		// 生成别名
		private Map<Class<?>, String> table2alias = new HashMap<>();
		
		private List<Join> join_list = new ArrayList<>();
		private List<Where> where_list = new ArrayList<>();
		private List<Group> group_list = new ArrayList<>();
		private List<Order> order_list = new ArrayList<>();
		
		private Map<String, Object> parameters = new HashMap<>();
		
		public static Select valueOf(Class<?> table) {
			Select result = new Select(table);
			return result;
		}
		
		private Select(Class<?> table) {
			this.table = table;
			this.alias = "t" + (tableCount++);
			this.table2alias.put(this.table, this.alias);
		}
		
		public Select addColumns(Class<?> table) {
			columnTable.add(table);
			return this;
		}
		
		public Select join(Join table) {
			join_list.add(table);
			table.alias = "t" + (tableCount++);
			this.table2alias.put(table.clazz, table.alias);
			return this;
		}
		
		public Select where(Where where) {
			where_list.add(where);
			return this;
		}
		
		public Select order(Order order) {
			order_list.add(order);
			return this;
		}
		
		public Select group(Group group) {
			group_list.add(group);
			return this;
		}
		
		public Map<String, Object> getParameters() {
			return parameters;
		}
		
		private int handleWhere(String relation, boolean p_hasWhere, boolean p_first, int p_propertyNum, StringBuilder sb, List<Where> where_list) {
			
			boolean hasWhere = p_hasWhere;
			boolean first = p_first;
			int propertyNum = p_propertyNum;
			for (Where where : where_list) {
				
				if(where.sub != null) {
					
					if (!hasWhere) {
						sb.append(" where ");
						hasWhere = true;
					}
					
					if (first) {
						first = false;
					} else {
						sb.append(" and ");
					}
					
					sb.append(" ( ");
					
					propertyNum += handleWhere(where.relation, hasWhere, true, propertyNum, sb, where.sub);
					
					sb.append(" ) ");
				}
				
				if(where.value == null) {
					continue;
				}
				
				if (!hasWhere) {
					sb.append(" where ");
					hasWhere = true;
				}

				String alias = null;
				if(where.clazz != null) {
					alias = table2alias.get(where.clazz);
				} else {
					alias = where.alias;
				}
				String bindName = alias + "_" + where.property + "_" + (propertyNum++);
				if (first) {
					first = false;
				} else {
					sb.append(" " + relation + " ");
				}
				if(where.op.equals("is null")) {
					sb.append(alias + "." + where.property + " " + where.op );
				} else {
					sb.append(alias + "." + where.property + " " + where.op + " :" + bindName);
					if(where.value instanceof Enum) {
						parameters.put(bindName, ((Enum<?>)(where.value)).name());
					} else {
						parameters.put(bindName, where.value);
					}
					
				}
			}
			
			return propertyNum;
		}

		public String buildSql() {
			
			String columns = "";
			if(columnTable.isEmpty()) {
				columns += "*";
			} else {
				boolean first = true;
				for (Class<?> table : columnTable) {
					if(!first) {
						columns += ",";
						first = false;
					}
					String alias = table2alias.get(table);
					columns += alias + ".*";
				}
			}

			// 基础SQL
			StringBuilder sb = new StringBuilder();
			sb.append("select "+columns+" from " + table.getSimpleName() + " " + this.alias + " ");
			
			for(Join join : join_list) {
				
				sb.append(" " +join.mode + " " + join.clazz.getSimpleName() + " " + join.alias + " ");
				List<On> on_List = join.on_list;
				boolean hasOn = false;
				for (On on : on_List) {
					if(!hasOn) {
						sb.append(" ON ");
						hasOn = true;
					}
					
					String newAlias = table2alias.get(on.newt);
					sb.append(" " + join.alias + "." + on.property + " = " + newAlias + "." + on.newt_property);
				}
			}
			
			int propertyNum = handleWhere("and", false, true, 0, sb, where_list);
//			boolean hasWhere = false;
//			boolean first = true;
//			int propertyNum = 0;
//			for (Where where : where_list) {
//				
//				if(where.value == null) {
//					continue;
//				}
//				
//				if (!hasWhere) {
//					sb.append(" where ");
//					hasWhere = true;
//				}
//
//				String alias = null;
//				if(where.clazz != null) {
//					alias = table2alias.get(where.clazz);
//				} else {
//					alias = where.alias;
//				}
//				String bindName = alias + "_" + where.property + "_" + (propertyNum++);
//				if (first) {
//					first = false;
//				} else {
//					sb.append(" and ");
//				}
//				if(where.op.equals("is null")) {
//					sb.append(alias + "." + where.property + " " + where.op );
//				} else {
//					sb.append(alias + "." + where.property + " " + where.op + " :" + bindName);
//					parameters.put(bindName, where.value);
//				}
//			}
			
			boolean hasGroup = false;
			boolean firstGroup = true;
			for (Group group : group_list) {
				if(!hasGroup) {
					sb.append(" GROUP BY ");
					hasGroup = true;
				}
				String alias = table2alias.get(group.clazz);
				if(firstGroup) {
					sb.append(alias + "." + group.property + " ");
					firstGroup = false;
				} else {
					sb.append("," + alias + "." + group.property + " ");
				}
			}
			
			boolean hasOrder = false;
			boolean firstOrder = true;
			for(Order order : order_list) {
				if(!hasOrder) {
					sb.append(" ORDER BY ");
					hasOrder = true;
				}
				
				String alias = null;
				if(order.where != null) {
					alias = table2alias.get(order.where.clazz);
				} else {
					alias = table2alias.get(order.clazz);
				}
				
				if(firstOrder) {
					firstOrder = false;
				} else {
					sb.append(",");
				}
				if(order.where != null) {
					
					String wh = "";
					if(order.where.op.equals("is null")) {
						wh = alias + "." + order.where.property + " " + order.where.op;
					} else {
						String bindName = alias + "_" + order.where.property + "_" + (propertyNum++);
						wh = alias + "." + order.where.property + " " + order.where.op + " :" + bindName;
						parameters.put(bindName, order.where.value);
					}
					
					sb.append(wh + " " + order.type + " ");
				} else {					
					sb.append(alias + "." + order.property + " " + order.type + " ");
				}
				
			}
			
			return sb.toString();
		}
		
	}
	
	public static class Join {
		Class<?> clazz;
		String alias;
		String mode;
		List<On> on_list = new ArrayList<>();
		
		public static Join create(Class<?> table, JoinType mode) {
			Join r = new Join();
			r.clazz = table;
			r.mode = mode.sql;
			return r;
		}
		
		public Join on(String property, Class<?> newT, String newT_property) {
			On on = On.create(property, newT, newT_property);
			on_list.add(on);
			return this;
		}
		
		public String getAlias() {
			return this.alias;
		}
	}

	public static class On {
		String property;
		Class<?> newt;
		String newt_property;
		
		public static On create(String property, Class<?> newt, String newt_property) {
			On r = new On();
			r.property = property;
			r.newt = newt;
			r.newt_property = newt_property;
			return r;
		}
	}
	
	public static class Where {
		Class<?> clazz;
		String alias;
		String property;
		Object value;
		String op;
		String relation;/** and or */
		
		List<Where> sub;
		
		@Override
		public String toString() {
			return "Where [clazz=" + clazz + ", alias=" + alias + ", property=" + property + ", op=" + op + "]";
		}

		public static Where or(Where ... sub) {
			Where r = new Where();
			r.sub = new ArrayList<>();
			r.relation = "or";
			Collections.addAll(r.sub, sub);
			return r;
		}
		
		public static Where and(Where ... sub) {
			Where r = new Where();
			r.sub = new ArrayList<>();
			r.relation = "and";
			Collections.addAll(r.sub, sub);
			return r;
		}
		
		public static Where eq(Class<?> clazz, String property, Object value) {
			Where r = new Where();
			r.clazz = clazz;
			r.property = property;
			r.value = value;
			r.op = "=";
			return r;
		}
		
		public static Where eq(String alias, String property, Object value) {
			Where r = new Where();
			r.alias = alias;
			r.property = property;
			r.value = value;
			r.op = "=";
			return r;
		}
		
		public static Where ne(Class<?> clazz, String property, Object value) {
			Where r = new Where();
			r.clazz = clazz;
			r.property = property;
			r.value = value;
			r.op = "<>";
			return r;
		}
		
		public static Where ne(String alias, String property, Object value) {
			Where r = new Where();
			r.alias = alias;
			r.property = property;
			r.value = value;
			r.op = "<>";
			return r;
		}
		
		public static Where like(Class<?> clazz, String property, Object value) {
			Where r = new Where();
			r.clazz = clazz;
			r.property = property;
			r.value = value;
			r.op = "like";
			return r;
		}
		
		public static Where like(String alias, String property, Object value) {
			Where r = new Where();
			r.alias = alias;
			r.property = property;
			r.value = value;
			r.op = "like";
			return r;
		}
		
		public static Where gt(Class<?> clazz, String property, Object value) {
			Where r = new Where();
			r.clazz = clazz;
			r.property = property;
			r.value = value;
			r.op = ">";
			return r;
		}
		
		public static Where ge(Class<?> clazz, String property, Object value) {
			Where r = new Where();
			r.clazz = clazz;
			r.property = property;
			r.value = value;
			r.op = ">=";
			return r;
		}
		
		public static Where lt(Class<?> clazz, String property, Object value) {
			Where r = new Where();
			r.clazz = clazz;
			r.property = property;
			r.value = value;
			r.op = "<";
			return r;
		}
		
		public static Where le(Class<?> clazz, String property, Object value) {
			Where r = new Where();
			r.clazz = clazz;
			r.property = property;
			r.value = value;
			r.op = "<=";
			return r;
		}
		
		public static Where isNull(Class<?> clazz, String property) {
			Where r = new Where();
			r.clazz = clazz;
			r.property = property;
			r.op = "is null";
			r.value = "";
			return r;
		}
		
		public static Where isNotNull(Class<?> clazz, String property) {
			Where r = new Where();
			r.clazz = clazz;
			r.property = property;
			r.op = "is not null";
			r.value = "";
			return r;
		}
	}
	
	public static class Order {
		String type;
		Class<?> clazz;
		String property;
		Where where;

		public static Order asc(Class<?> clazz, String property) {
			Order r = new Order();
			r.clazz = clazz;
			r.property = property;
			r.type = "asc";
			return r;
		}

		public static Order desc(Class<?> clazz, String property) {
			Order r = new Order();
			r.clazz = clazz;
			r.property = property;
			r.type = "desc";
			return r;
		}
		
		public static Order asc(Where where) {
			Order r = new Order();
			r.where = where;
			r.type = "asc";
			return r;
		}

		public static Order desc(Where where) {
			Order r = new Order();
			r.where = where;
			r.type = "desc";
			return r;
		}
	}
	
	public static class Group {
		Class<?> clazz;
		String property;
		
		public static Group group(Class<?> clazz, String property) {
			Group r = new Group();
			r.clazz = clazz;
			r.property = property;
			return r;
		}
	}
	
	public static enum JoinType {
		inner("inner join"),
		left("left join"),
		right("right join"),
		;
		
		private String sql;
		
		JoinType(String sql) {
			this.sql = sql;
		}

		public String getSql() {
			return sql;
		}
	}
	
	public static enum OrderType {
		asc,
		desc
	}
	
	
	
	public static void main(String[] args) {
//		Select select = new Select(User.class);
//		
//		System.err.println(select.buildSql());
//		
//		Join j1 = Join.create(BaseUser.class, JoinType.inner).on("id", User.class, "id");
//		Join j2 = Join.create(UserRole.class, JoinType.left).on("user_id", User.class, "id");
//		select.join(j1).join(j2);
//		
//		Where where = Where.or(Where.like(BaseUser.class, "anmeldenState", "%t%"), Where.eq(UserRole.class, "role_id", 1));
//		
//		select.where(Where.eq(BaseUser.class, "nation", "han"));
//		select.where(where);
//		select.where(Where.eq(UserRole.class, "id", 1));
//		
//		select.group(Group.group(User.class, "id"));
//		select.group(Group.group(UserRole.class, "id"));
//		
//		select.order(Order.asc(BaseUser.class, "workYear"));
//		select.order(Order.desc(User.class, "id"));
//		select.order(Order.desc(Where.eq(User.class, "sex", "man")));
//		
//		System.err.println(select.buildSql());
//		
//		System.err.println(JsonUtil.encodeJson(select.parameters));
	}
}
