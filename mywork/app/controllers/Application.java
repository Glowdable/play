package controllers;

import play.*;
import play.mvc.*;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.util.*;

import javax.naming.NamingException;
import javax.naming.Reference;

import org.h2.engine.SessionFactory;
import org.hibernate.Cache;
import org.hibernate.HibernateException;
import org.hibernate.Interceptor;
import org.hibernate.StatelessSession;
import org.hibernate.TypeHelper;
import org.hibernate.classic.Session;
import org.hibernate.engine.FilterDefinition;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.metadata.CollectionMetadata;
import org.hibernate.stat.Statistics;
import org.joda.time.DateTime;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
//It seems we could make a better parser for Map<String,Object> which converts the Object to a primitive wrapper, list, or map.  Right now, we just get an error back.
//"Type information is unavailable, and the target object is not a primitive"
//Fixed in GSON 2.0.so i used lastest gson version 2.2.4
import models.*;

public class Application extends Controller {

	public static void index() {
		render();
	}

	public static void create(String body) {
		OutboundVouch outVouch = new OutboundVouch("00000001", 01, "001");
		OutboundVouchSub outvouchsub = new OutboundVouchSub(outVouch,
				outVouch.cReceiptMainID, BigInteger.valueOf(1), 1, 123, "TEST",
				BigDecimal.valueOf(100));
		outVouch.addRowWithoutSave(outvouchsub);
		List<OutboundVouch> outVouchList = new ArrayList<OutboundVouch>();
		outVouchList.add(outVouch);
		List<OutboundVouchSub> outVouchSubList = new ArrayList<OutboundVouchSub>();
		outVouchSubList.add(outvouchsub);
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		map.put("OutboundVouchs", outVouchList);
		map.put("OutboundVouchSubs", outVouchSubList);
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation() // 不导出实体中没有用@Expose注解的属性
				.enableComplexMapKeySerialization() // 支持Map的key为复杂对象的形式
				.serializeNulls().setDateFormat("yyyy-MM-dd")// 时间转化为特定格式
				.setPrettyPrinting() // 对json结果格式化.
				.setVersion(1.0) // 有的字段不是一开始就有的,会随着版本的升级添加进来,那么在进行序列化和返序列化的时候就会根据版本号来选择是否要序列化.
				.create();
		String json = gson.toJson(map);
		// 对于collection来说（数组）
		/*
		 * JsonParser parser = new JsonParser(); JsonArray array =
		 * parser.parse(json).getAsJsonArray(); String message =
		 * gson.fromJson(array.get(0), String.class);
		 */

		JsonElement je = new JsonParser().parse(json);
		JsonObject jo = je.getAsJsonObject();
		String sjson = jo.get("OutboundVouchs").toString();

		List<OutboundVouch> lstTemOutVouch1 = gson.fromJson(sjson,
				new TypeToken<List<OutboundVouch>>() {
				}.getType());

		OutboundVouch temee1 = lstTemOutVouch1.get(0);
		System.out.println("tem1:" + temee1);

		List<OutboundVouch> lstTemOutVouch2 = gson.fromJson(
				jo.get("OutboundVouchs"), new TypeToken<List<OutboundVouch>>() {
				}.getType());

		OutboundVouch tem2 = lstTemOutVouch1.get(0);
		System.out.println("tem2:" + tem2);

		// List<OutboundVouch>
		// tem=(List<OutboundVouch>)jo.get("OutboundVouchs");
		System.out.println("Pass!!!");

		Map<String, Object> retMap = gson.fromJson(json,
				new TypeToken<Map<String, List<Object>>>() {
				}.getType());
		System.out.println("pass:" + json);
		for (String key : retMap.keySet()) {
			System.out.println("key:" + key + " values:" + retMap.get(key));
			if (key.equals("OutboundVouchs")) {
				/*
				 * List<OutboundVouch> lstTemOutVouch = (List<OutboundVouch>)
				 * retMap .get(key); String tem12 =
				 * gson.toJson(retMap.get(key)); System.out.println("tem: " +
				 * tem12); List<OutboundVouch> lstOutVouch =
				 * gson.fromJson(tem12, new TypeToken<List<OutboundVouch>>() {
				 * }.getType()); System.out.println(lstOutVouch);
				 */
				System.out.println("OutboundVouch count:"
						+ OutboundVouch.count());
				System.out.println("OutboundVouchSub count:"
						+ OutboundVouchSub.count());
				/*
				 * for (Object vouch : lstOutVouch) { ((OutboundVouch)
				 * vouch).save(); }
				 */
				System.out.println("OutboundVouch count:"
						+ OutboundVouch.count());
				System.out.println("OutboundVouchSub count:"
						+ OutboundVouchSub.count());
			}
		}
	}
	/*
	 * public static void create(String body){
	 * 
	 * //MyParam myParam = new
	 * GsonBuilder().create().fromJson(request.params.get("myParam"),
	 * MyParam.class) //Gson gson = new Gson(); OutboundVouch outVouch=new
	 * OutboundVouch("00000001", 01,"001"); OutboundVouchSub outvouchsub=new
	 * OutboundVouchSub(outVouch, outVouch.cReceiptMainID,
	 * BigInteger.valueOf(1), 1, 123, "TEST", BigDecimal.valueOf(100));;
	 * outVouch.addRowWithoutSave(outvouchsub);
	 * 
	 * List<OutboundVouch> outVouchList = new ArrayList<OutboundVouch>();
	 * outVouchList.add(outVouch); // List<OutboundVouchSub> outVouchSubList =
	 * new ArrayList<OutboundVouchSub>(); // outVouchSubList.add(outvouchsub);
	 * Map<String, Object> map = new LinkedHashMap<String, Object>();
	 * map.put("OutboundVouchs", outVouchList); map.put("OutboundVouchSubs2",
	 * outvouchsub); //map暂时有问题，有两个不同对象时，反序列化出错，而且 //
	 * 只有一个对象时反序列化出来后，内部对象是Gson的LinkTreeMap，还需要写转换方法
	 * 
	 * Gson gson = new GsonBuilder() .excludeFieldsWithoutExposeAnnotation()
	 * //不导出实体中没有用@Expose注解的属性 (必须加这个) //.enableComplexMapKeySerialization()
	 * //支持Map的key为复杂对象的形式
	 * .serializeNulls().setDateFormat("yyyy-MM-dd")//时间转化为特定格式 HH:mm:ss:SSS
	 * //.setFieldNamingPolicy
	 * (FieldNamingPolicy.UPPER_CAMEL_CASE)//会把字段首字母大写,注:对于实体上使用了
	 * 
	 * @SerializedName注解的不会生效. .setPrettyPrinting() //对json结果格式化.
	 * .setVersion(1.0)
	 * //有的字段不是一开始就有的,会随着版本的升级添加进来,那么在进行序列化和返序列化的时候就会根据版本号来选择是否要序列化.
	 * //@Since(版本号)能完美地实现这个功能.还的字段可能,随着版本的升级而删除,那么
	 * //@Until(版本号)也能实现这个功能,GsonBuilder.setVersion(double)方法需要调用. .create();
	 * 
	 * //Gson gson = new Gson(); // String s = gson.toJson(map); // String
	 * s1=gson.toJson(outVouch); String s2=gson.toJson(outVouchList);
	 * OutboundVouch v1=gson.fromJson(s1,OutboundVouch.class);
	 * List<OutboundVouch> lv1=gson.fromJson(s2,new
	 * TypeToken<List<OutboundVouch>>(){}.getType());
	 * System.out.println("OutboundVouch S1:"+v1.cDepartmentID);
	 * System.out.println
	 * ("outboundVouchSubs S1:"+v1.outboundVouchSubs.get(0).iRowNo);
	 * System.out.println("OutboundVouchs S2 Cont:"+lv1.size()); //
	 * System.out.println("outVouch:"+s2);
	 * 
	 * Map<String, List<OutboundVouch>> retMap = gson.fromJson(s, new
	 * TypeToken<Map<String, List<OutboundVouch>>>() { }.getType()); Map<String,
	 * Object> retMap = gson.fromJson(s, new
	 * TypeToken<Map<String,List<Object>>>() { }.getType()); List<OutboundVouch>
	 * lstOutVouch=gson.fromJson(s2, new TypeToken<List<OutboundVouch>>() {
	 * }.getType());
	 * 
	 * System.out.println("pass:");
	 * 
	 * for(OutboundVouch vouch : lstOutVouch){
	 * System.out.println("OutboundVouch count:"+OutboundVouch.count());
	 * System.out.println("OutboundVouchSub count:"+OutboundVouchSub.count());
	 * vouch.save();
	 * System.out.println("OutboundVouch count:"+OutboundVouch.count());
	 * System.out.println("OutboundVouchSub count:"+OutboundVouchSub.count()); }
	 * 
	 * 
	 * for (String key : retMap.keySet()) { System.out.println("key:" + key +
	 * " values:" + retMap.get(key)); if (key.equals("OutboundVouchs")) {
	 * List<OutboundVouch> outList = (List<OutboundVouch>) retMap.get(key);
	 * System.out.println(outList);
	 * System.out.println("OutboundVouch count:"+OutboundVouch.count());
	 * System.out.println("OutboundVouchSub count:"+OutboundVouchSub.count());
	 * for (Object vouch : outList) { ((OutboundVouch)vouch).save(); }
	 * System.out.println("OutboundVouch count:"+OutboundVouch.count());
	 * System.out.println("OutboundVouchSub count:"+OutboundVouchSub.count()); }
	 * else if (key.equals("others")) { List<OutboundVouchSub> outSubList =
	 * (List<OutboundVouchSub>) retMap.get(key); System.out.println(outSubList);
	 * }
	 * 
	 * }
	 * 
	 * String cReceiptMainID = params.get("cReceiptMainID"); String cStockCode =
	 * params.get("cStockCode"); String cDLReceiptMainID =
	 * params.get("cDLReceiptMainID"); String cdepartmentID =
	 * params.get("cdepartmentID"); String irowno = params.get("irowno"); ;
	 * String cDLReceiptDetailID = params.get("cDLReceiptDetailID"); String
	 * cInvCode = params.get("cInvCode"); OutboundVouch outVouch=new
	 * OutboundVouch(BigInteger.valueOf(Integer.parseInt(irowno)), cStockCode,
	 * Integer.parseInt(cDLReceiptMainID),cdepartmentID).save();
	 * OutboundVouchSub vouchsub=new
	 * OutboundVouchSub(outVouch,BigInteger.valueOf
	 * (Integer.parseInt(cReceiptMainID
	 * )),BigInteger.valueOf(1),Integer.parseInt(
	 * cDLReceiptDetailID),123,cInvCode); outVouch.addRow(vouchsub);
	 * System.out.println("OutboundVouch:"+OutboundVouch.count());
	 * System.out.println("OutboundVouchSub:"+OutboundVouchSub.count());
	 * //String[] names = params.getAll("names"); //render("CRUD/list.html"); //
	 * renderJSON(""); }
	 */
	/*
	 * private static List<MapData> getData(){ Gson gson = new Gson(); String
	 * jsonString =
	 * "[{\"id\":18,\"city\":\"test\",\"street\":\"test 1\",\"zipcode\":121209,\"state\":\"IL\",\"lat\":32.158138,\"lng\":34.807838},{\"id\":19,\"city\":\"test\",\"street\":\"1\",\"zipcode\":76812,\"state\":\"IL\",\"lat\":32.161041,\"lng\":34.810410}]"
	 * ; Type type = new TypeToken<List<MapData>>(){}.getType(); return
	 * gson.fromJson(jsonString, type); }
	 */

}

/*
 * public class BooksDeserializer implements JsonDeserializer<Books> {
 * 
 * @Override public Books deserialize(final JsonElement json, final Type
 * typeOfT, final JsonDeserializationContext context) throws JsonParseException
 * { Books books = new Books();
 * 
 * JsonObject jsonObject = json.getAsJsonObject();
 * books.addBookTitle(jsonObject.get("1").getAsString());
 * 
 * return books; } }
 */

class DateTimeSerializer implements JsonSerializer<DateTime> {
	public JsonElement serialize(DateTime src, Type typeOfSrc,
			JsonSerializationContext context) {
		return new JsonPrimitive(src.toString());
	}
}