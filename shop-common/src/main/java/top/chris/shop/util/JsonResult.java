package top.chris.shop.util;

/**
 * 对所有Controller和Service层中的方法，都设定返回值为JsonResult，通过JsonResult的返回结果来判断请求是否成功。
 * 这个类是提供给门户，ios，安卓，微信商城用的，门户接受此类数据后需要使用本类的方法转化成对应的数据类型格式(类，或者list)，
 * 其他自行处理。
 * status状态码:200表示成功，
 * 				400表示传入参数有误，
 * 				500表示服务器错误,错误信息在msg中，
 * 				501表示bean验证错误，
 * 				502表示拦截器拦截到用户token出错
 * 				503表示Session失效
 * 				555表示异常抛出信息
 * 				401表示请求为授权
 * 				202表示退出成功
 *
 */
public class JsonResult {
	//响应业务状态
	private Integer status;
	//响应数据
	private Object data;
	//响应信息
	private String msg;

	public JsonResult() {
		super();
	}
	public JsonResult(Integer status, Object data, String msg) {
		super();
		this.status = status;
		this.data = data;
		this.msg = msg;
	}

	public JsonResult(Object data) {
		this.data = data;
	}

	public JsonResult(Integer status) {
		this.status = status;
	}

	/**
	 * 响应成功：返回数据
	 * @param data
	 * @return
	 */
	public static JsonResult isOk(Object data) {
		return new JsonResult(200, data, "success");
	}
	/**
	 * 响应失败：返回状态码和错误信息
	 * @param status
	 * @param msg
	 * @return
	 */
	public static JsonResult isErr(Integer status,String msg) {
		return new JsonResult(status,null, msg);
	}
	public static JsonResult build(Integer status,String msg,Object data){
		return new JsonResult(status,data,msg);
	}
	public static JsonResult ok(Object data){
		return new JsonResult(data);
	}
	public static JsonResult ok(){
		return new JsonResult(200);
	}
	public static JsonResult ok(String msg){
		return new JsonResult(202,null,msg);
	}
	public static JsonResult errorMsg(String msg){
		return new JsonResult(500,null,msg);
	}
	public static JsonResult errorMap(Object data){
		return new JsonResult(501,data,"error");
	}
	public static JsonResult errorTokenMsg(String msg){
		return new JsonResult(502,null,msg);
	}
	public static JsonResult errorSession(String msg){return new JsonResult(503,null,msg);}
	public static JsonResult errorException(String msg){
		return new JsonResult(555,null,msg);
	}
	public static JsonResult errorAuthorized(String msg){
		return new JsonResult(401,msg,null);
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}


}
