package top.chris.shop.util;

import java.util.List;

import com.github.pagehelper.PageInfo;

/**
 * 存储分页查询后数据的模型
 * @Title: PagedGridResult.java
 * @Description: 用来返回分页Grid的数据格式 Copyright: Copyright
 */
public class PagedGridResult {

	private int page; // 当前页数
	private int total; // 总页数
	private long records; // 总记录数
	private List<?> rows; // 每行显示的内容（存储查询到数据对象）

	public PagedGridResult(int page, int total, long records, List<?> rows) {
		this.page = page;
		this.total = total;
		this.records = records;
		this.rows = rows;
	}

	public PagedGridResult() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * 将官方定义的pageinfo对象转换为自定义的专门渲染前端的PagedGridResult对象
	 * 
	 * @param list
	 * @param page
	 * @return
	 */
	public PagedGridResult(List<?> list, Integer page) {
		PageInfo<?> pageList = new PageInfo<>(list);
		this.page = page;
		this.rows = list;
		this.total = pageList.getPages();
		this.records = pageList.getTotal();
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public long getRecords() {
		return records;
	}

	public void setRecords(long records) {
		this.records = records;
	}

	public List<?> getRows() {
		return rows;
	}

	public void setRows(List<?> rows) {
		this.rows = rows;
	}
}
